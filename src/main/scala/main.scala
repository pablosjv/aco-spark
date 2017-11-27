import aco.AntRandomMaCACO
import data.DataModel
import org.apache.spark.{SparkConf, SparkContext}
import data.railway.{Resource, Step, Task, Train}
import java.io._

/**
  * Created by Pablo on 26/5/17.
  */
object main extends App {
  /**
    * The main class of the application. Controls the execution of the colony and the initialization of all elements
    */

  // ---------- SET THE GLOBAL PARAMETERS ----------
  val workDir = "src/main/scala/"
  val trainsFile = workDir + "horarios.txt"
  val resourcesFile = workDir + "recursosBase.txt"
  val workday = 8 * 60 // "Jornada" - 8 hours

  val algorithm = "RANDOM-MACACO" // FIXME: Buscar forma de inicializar array en funcion de este parametro

  val EXPERIMENTS = 50
  val resultsFile = workDir + "resultsFiles/result_seq_2.txt"

  val ITERATIONS = 100
  val N_ANTS = 25 // number of ants
  val ALPHA = 1 // a parameter from the ACO algorithm to control the influence of the amount of pheromone when an ant makes a choice
  val BETA = 2 // a parameters from ACO that controls the influence of the distance to the next node in ant choice making
  val pheromoneEvaporationCoefficient = 0.05 // a parameter used in removing pheromone values from the pheromone_map (rho in ACO algorithm)
  val pheromoneConstant = 0.75// / N_ANTS // a parameter used in depositing pheromones on the map (Q in ACO algorithm)
  val initialPheromone = 0.5


  // --------------- READ THE DATA IN FILES ---------------
  val reader = new FileReader()

  var trains: Array[Train] = reader.readTrains(trainsFile)
  val (stations, resourcesStation) = reader.readResources(resourcesFile)


  val dataModel = new DataModel(trains, stations, resourcesStation, workday)

  //DEBUG
  println("***** NUMBER OF RESOURCES: " + dataModel.getnResources() + " *****")
  println("***** NUMBER OF TASKS = " + dataModel.getnTasks() + " *****")

  var pheromoneMatrix: Array[Array[Double]] = constructPheromoneMatrix(
    tasks = dataModel.getTasks, nStations = dataModel.getnStations(), initialPheromone = initialPheromone)
  var distanceMatrix: Array[Array[Double]] = constructDistanceMatrix(
    tasks = dataModel.getTasks, nTask = dataModel.getnTasks(), nStations = dataModel.getnStations())

  val ants = List.fill[AntRandomMaCACO](N_ANTS)(new AntRandomMaCACO(pheromoneMatrix, distanceMatrix, ALPHA, BETA, dataModel))

  //print(distanceMatrix.map(_.mkString("\t")).mkString("\n"))
  //--------------- SET UP SPARK ---------------
  val conf = new SparkConf()
    .setAppName("ACO SPARK").setMaster("local[*]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  // FileWriter
  val file = new File(resultsFile)
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write("-+-+-+-+-+-+-+-+-+-+-+ ACO -+-+-+-+-+-+-+-+-+-+-+\n")

  try {

    for(i <- 0 until EXPERIMENTS){
      println("******** Experiment number " + i + " ********\n")
      bw.write("******** Experiment number " + i + " ********\n")
      val t0 = System.currentTimeMillis()

      //antColonyParallelExecution()
      antColonySequentialExecution()
      val t1 = System.currentTimeMillis()
      val elapsedTime = t1 - t0
      bw.write("Time for the experiment " + i + " = " + elapsedTime + "\n")
      bw.write("********************************\n")
      resetPheromoneMatrix()
    }

  } finally {
    bw.close()
    sc.stop()
  }


  println("------------ APPLICATION ENDED  ------------ ")


  def antColonyParallelExecution(): Unit = {
    // --------------- ACO LOOP ---------------
    val distAnts = sc.parallelize(ants)
    var i = 0
    var bestSolution: DataModel = dataModel
    var bestScore: Int = 0
    var unImprovementCount: Int = 0
    for (i <- 0 until ITERATIONS) {
      bw.write("------------ Iteration number " + i + " ------------ \n")
      //Medir el tiempo de la iteracion
      val t0 = System.currentTimeMillis()

      val solutions = distAnts
        .map(_.findSolution())
        .sortBy(x => x.getAssignationScore, ascending = false)
      //DEBUG
      //println("MAP FUNC FINISHH")

      val iterationBestSolution = solutions.first()
      bw.write("Iteration best Score: " + iterationBestSolution.getAssignationScore() + "\n")
      bw.write(iterationBestSolution.printResourceAssignation()+ "\n")

      if (iterationBestSolution.getAssignationScore > bestScore) {

        bestScore = iterationBestSolution.getAssignationScore
        bestSolution = iterationBestSolution
        unImprovementCount = 0
        bw.write(">>> New best assignation in the iteration " + i + " !!!!!! " + "\n")

      } else unImprovementCount += 1
      bw.write("Current best Score: " + bestScore+ "\n")
      bw.write(bestSolution.printResourceAssignation()+ "\n")
      //val result = bestSolution.printResourceAssignation()

      if (unImprovementCount > 25) {
        //DEBUG
        bw.write("+++++++ Solution Stuck!!: Resetting Pheromone Matrix +++++++"+ "\n")
        resetPheromoneMatrix()
        unImprovementCount = 0
      } else {
        val collectedSolutions: Array[DataModel] = solutions.collect()
        updatePheromoneMatrix(collectedSolutions)
        //DAEMON: MAX
        addPheromonesToSolution(iterationBestSolution)
        //TODO: DAEMON MIN??
      }
      addPheromonesToSolution(bestSolution)
      //Guardar el tiempo de la iteracion
      val t1 = System.currentTimeMillis()
      val elapsedTime = t1 - t0
      bw.write("-- Time for the iteration: " + i + " = " + elapsedTime + "\n")
    }
    // --------------- END OF THE ACO ---------------
    bw.write("------->>> Best global solution: ")
    bw.write(bestSolution.printResourceAssignation() + "\n")
  }

  def antColonySequentialExecution(): Unit ={

    val antColonySequential = new SequentialAlgorithm(ITERATIONS, N_ANTS, pheromoneMatrix, distanceMatrix, ALPHA, BETA,
      initialPheromone, pheromoneEvaporationCoefficient, pheromoneConstant, dataModel, bw)

    antColonySequential.antColonySequentialExecution()

//    var i = 0
//    var bestSolution: DataModel = null
//    var bestScore: Int = 0
//    var unImprovementCount: Int = 0
//    for (i <- 0 until ITERATIONS) {
//      println("------------ Iteration number " + i + " ------------ " + "\n")
//      //Medir el tiempo de la iteracion
//      val t0 = System.currentTimeMillis()
//
//      var solutions = Array[DataModel]()
//      //var ant: AntRandomMaCACO = null
//      for (ant <- ants){
//        //DEBUG
//        val solution = ant.findSolution()
//        solutions = solutions :+ ant.findSolution()
//
//      }
//     // println("BUCLE ANTSSSSSSSS")
//      val sortedSolutions = solutions.sortBy(solution => solution.getAssignationScore).reverse
//
//      for ( a <- sortedSolutions){
//        println("ASIGNATION SCORE: " + a.getAssignationScore)
//      }
//
//      val iterationBestSolution = sortedSolutions.head
//      println("Iteration best Score: " + iterationBestSolution.getAssignationScore + "\n")
//      iterationBestSolution.printResourceAssignation()
//
//      if (iterationBestSolution.getAssignationScore >= bestScore) {
//
//        bestScore = iterationBestSolution.getAssignationScore
//        bestSolution = iterationBestSolution
//        unImprovementCount = 0
//        println(">>> New best assignation in the iteration " + i + " !!!!!! " + "\n")
//
//      } else unImprovementCount += 1
//      println("Current best Score: " + bestScore + "\n")
//      println(bestSolution.printResourceAssignation() + "\n")
//      //val result = bestSolution.printResourceAssignation()
//
//      if (unImprovementCount > 25) {
//        //DEBUG
//        println("+++++++ Solution Stucked!!: Resetting Pheromone Matrix +++++++" + "\n")
//        resetPheromoneMatrix()
//        unImprovementCount = 0
//      } else {
//        updatePheromoneMatrix(solutions)
//        //DAEMON: MAX
//        addPheromonesToSolution(iterationBestSolution)
//        //TODO: DAEMON MIN
//      }
//      //Guardar el tiempo de la iteracion
//      val t1 = System.currentTimeMillis()
//      val elapsedTime = t1 - t0
//      println("-- Time for the iteration: " + i + " = " + elapsedTime + "\n")
//    }
//    // --------------- END OF THE ACO ---------------
//    println("------->>> Best global solution: " + "\n")
//    println(bestSolution.printResourceAssignation() + "\n")
  }

  def constructPheromoneMatrix(tasks: Array[Task], nStations: Int, initialPheromone: Double): Array[Array[Double]] = {
    val newPheromoneMatrix = Array.ofDim[Double](tasks.length + nStations, tasks.length)

    for (a <- newPheromoneMatrix.indices; b <- newPheromoneMatrix(a).indices) {
      newPheromoneMatrix(a)(b) = initialPheromone
    }
    newPheromoneMatrix
  }

  def constructDistanceMatrix(tasks: Array[Task], nTask: Int, nStations: Int): Array[Array[Double]] = {
    val newDistanceMatrix = Array.ofDim[Double](nTask + stations.length, tasks.length)
    // Compute the distance between tasks
    var timeDifference = 0;
    val epsilon = 1
    for (i <- 0 until nTask) {
      for (j <- 0 until nTask) {

        if (i == j) newDistanceMatrix(i)(j) = 0
        else if (tasks(i).getFinalStation == tasks(j).getInitialStation) {

          timeDifference = tasks(j).getInitialTime - tasks(i).getFinalTime

          if (timeDifference >= 0) {
            //            println("Final Station of the initial Task: " + tasks(i).getFinalStation()
            //              + "\tInitial Station of the target task: " + tasks(j).getInitialStation()
            //              + "\t Are there equal?: " + (tasks(i).getFinalStation() == tasks(j).getInitialStation())
            //              + "\tFinal time of the initial Task: " + tasks(i).getFinalTime()
            //              + "\tInitial time of the target task: " + tasks(j).getInitialTime()
            //              + "\tDifference: " + (tasks(j).getInitialTime() - tasks(i).getFinalTime())
            //              + "\tValue in the matrix: " + (1436.0/(tasks(j).getInitialTime() - tasks(i).getFinalTime() + epsilon)))
            //FIXME: check values range for the distance matrix
            newDistanceMatrix(i)(j) = 1 / (timeDifference + epsilon)
          }
        } else newDistanceMatrix(i)(j) = -1
      }
    }
    // Compute the distance between the "base station task" to the rest of the task:
    for (i <- nTask to (nTask + stations.length - 1)) {
      for (j <- 0 to nTask - 1) {
        //if (i eq j) newDistanceMatrix(i)(j) = 0
        if (tasks(i).getFinalStation() == tasks(j).getInitialStation()) {
          newDistanceMatrix(i)(j) = 1

        } else newDistanceMatrix(i)(j) = -1
      }
    }

    return newDistanceMatrix
  }

  def updatePheromoneMatrix(solutions: Array[DataModel]): Unit = {
    //DEBUG
    //println("`Updating pheromone matrix")
    var cont = 0

    for (a <- pheromoneMatrix.indices; b <- pheromoneMatrix(a).indices) {
      pheromoneMatrix(a)(b) = (1 - pheromoneEvaporationCoefficient) * pheromoneMatrix(a)(b)
    }

    for (solution <- solutions) {
      //DEBUG
      //println("Solution: " + cont)
      cont += 1
      addPheromonesToSolution(solution)
    }
  }

  def addPheromonesToSolution(solution: DataModel): Unit = {

    val dataModel: DataModel = solution
    val pheromoneAmount = computePheromoneAmount(dataModel)
    //DEBUG
    //println("Pheromone amount: " + pheromoneAmount)

    for (resource <- dataModel.getResources) {
      val steps: Array[Step] = resource.getSteps()
      var previousStep: Step = steps(0)
      for (i <- 1 to steps.length - 1) {
        //DEBUG
        //println("Updating from tasks: " + previousStep.getTaskIndex() + " to " + steps(i).getTaskIndex)

        pheromoneMatrix(previousStep.getTaskIndex())(steps(i).getTaskIndex) = pheromoneMatrix(previousStep.getTaskIndex())(steps(i).getTaskIndex) + pheromoneAmount
        previousStep = steps(i)
      }
    }
  }

  /*
  El depósito de feromonas se basa en el número de tareas que no han podido ser cubiertas. En particular, cuantas más
  tareas sin cubrir menor es el refuerzo. Para modelar el refuerzo se hace uso de la siguiente fórmula,
  donde Q es la recompensa y H el número de tareas no cubiertas
   */
  def computePheromoneAmount(solution: DataModel): Double = {
    //DEBUG
    //println("NUMBER OF TASKS: " + solution.getnTasks)
    //println("NUMBER OF TASKS COVERED: " + solution.getTasksCovered)
    val H: Int = solution.getnTasks() - solution.getTasksCovered

    if (H > 0) pheromoneConstant / H
    else if (H == 0) pheromoneConstant
    else throw new IllegalStateException(" ------ Something went wrong with the assignations: There was more tasks covered than the actual number of tasks")

  }

  def resetPheromoneMatrix(): Unit = {
    for (a <- pheromoneMatrix.indices; b <- pheromoneMatrix(a).indices) {
      pheromoneMatrix(a)(b) = initialPheromone
    }
  }


  def printData(): Unit = {
    for (train <- trains) {
      println(train)
    }
    println(trains.length)
    for (station <- stations) {
      println(station)
    }
    println("\n")


    println("------ PHEROMONE MATRIX --------")
    println(pheromoneMatrix.map(_.mkString("\t  ")).mkString("\n"))
    println("------ DISTANCE MATRIX --------")
    println(distanceMatrix.map(_.mkString("\t  ")).mkString("\n"))
    //  distanceMatrix foreach { row => row foreach print; println }

    //    val pw = new PrintWriter(new File("hello.txt"))
    //    pw.write(distanceMatrix.map(_.mkString("\t  ")).mkString("\n"))
    //    pw.close


  }

  def selectAlgorithm(): Unit = {
    //    algorithm match {
    ////        TODO: Todos los demas casos de algoritmo
    //      case "RANDOM-MACACO" => return Array.fill[AntRandomMaCACO](nAnts)(new AntRandomMaCACO(pheromoneMatrix, distanceMatrix, alpha, beta, constraintModel))
    //      case  _ => return Array.fill[AntRandomMaCACO](nAnts)(new AntRandomMaCACO(pheromoneMatrix, distanceMatrix, alpha, beta, constraintModel))
    //    }
  }

}
