/**
  * Created by Pablo on 23/6/17.
  */

import aco.{Ant, AntRandomMaCACO}
import org.apache.spark.{SparkConf, SparkContext}

object scalaTest extends App {

  val pheromoneMatrix = Array.ofDim[Double](5, 5)
  for (a <- pheromoneMatrix.indices; b <- pheromoneMatrix(a).indices) {
    pheromoneMatrix(a)(b) = 33
  }

  val ants: Array[AntRandomMaCACO] = Array.fill[AntRandomMaCACO](2)(new AntRandomMaCACO(pheromoneMatrix, null, 0, 0, null))

  //--------------- SET UP SPARK ---------------
  val conf = new SparkConf()
    .setAppName("wordCount").setMaster("local[*]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("WARN")
  var distAnts = sc.parallelize(ants)
  var ants2: Array[Ant] = null
  var tempMatrix = pheromoneMatrix

  for (i <- 0 to 3) {

    println(" ----------------- ")
    println("Original pheromone matrix")
    println(pheromoneMatrix.map(_.mkString("\t  ")).mkString("\n"))

    ants2 = distAnts.collect().asInstanceOf[Array[Ant]]
    for(ant <- ants2){
      println("Pheromone Matrix in the ant")
      println(ant.getPheromoneMatrix().map(_.mkString("\t  ")).mkString("\n"))
    }
    pheromoneMatrix(i)(i) = i

  }

}
