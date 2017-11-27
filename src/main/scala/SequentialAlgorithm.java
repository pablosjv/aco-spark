import aco.Ant;
import aco.AntRandomMaCACO;
import data.DataModel;
import data.railway.Resource;
import data.railway.Step;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Pablo on 7/7/17.
 */
public class SequentialAlgorithm {

    private Ant[] ants;
    private int ITERATIONS;
    private int nAnts;
    private double[][] pheromoneMatrix;
    private double[][] distanceMatrix;
    private double ALPHA;
    private double BETA;
    private double initialPheromone;
    private double pheromoneEvaporationCoefficient;
    private double pheromoneConstant;
    private BufferedWriter bw;

    DataModel dataModel;

    public SequentialAlgorithm(int ITERATIONS, int nAnts, double[][] pheromoneMatrix, double[][] distanceMatrix,
                               double ALPHA, double BETA, double initialPheromone, double pheromoneEvaporationCoefficient,
                               double pheromoneConstant, DataModel dataModel, BufferedWriter bw) {
        this.ITERATIONS = ITERATIONS;
        this.nAnts = nAnts;
        this.pheromoneMatrix = pheromoneMatrix;
        this.distanceMatrix = distanceMatrix;
        this.ALPHA = ALPHA;
        this.BETA = BETA;
        this.initialPheromone = initialPheromone;
        this.pheromoneEvaporationCoefficient = pheromoneEvaporationCoefficient;
        this.dataModel = dataModel;
        ants = new Ant[nAnts];
        for (int i = 0; i < nAnts; i++) {
            ants[i] = new AntRandomMaCACO(pheromoneMatrix, distanceMatrix, ALPHA, BETA, dataModel);
        }
        this.pheromoneConstant = pheromoneConstant;

        this.bw = bw;
    }


    public void antColonySequentialExecution() throws IOException {

        DataModel bestSolution = null;
        int bestScore = 0;
        int unImprovementCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            bw.write("------------ Iteration number " + i + " ------------ " + "\n");
            //Medir el tiempo de la iteracion
            double t0 = System.currentTimeMillis();

            DataModel[] solutions = new DataModel[ants.length];
            int j = 0;
            for (Ant ant : ants) {
                solutions[j] = ant.findSolution();
                j += 1;

            }
            Arrays.sort(solutions, (o1, o2) -> {
                if (o1.getAssignationScore() < o2.getAssignationScore()) {
                    return 1;
                } else if (o1.getAssignationScore() > o2.getAssignationScore()) {
                    return -1;
                }
                return 0;
            });


//            for (DataModel a : solutions) {
//                System.out.println("ASIGNATION SCORE: " + a.getAssignationScore());
//            }

            DataModel iterationBestSolution = solutions[0];
            bw.write("Iteration best Score: " + iterationBestSolution.getAssignationScore() + "\n");
            iterationBestSolution.printResourceAssignation();

            if (iterationBestSolution.getAssignationScore() >= bestScore) {

                bestScore = iterationBestSolution.getAssignationScore();
                bestSolution = iterationBestSolution;
                unImprovementCount = 0;
                bw.write(">>> New best assignation in the iteration " + i + " !!!!!! " + "\n");

            } else unImprovementCount += 1;
            bw.write("Current best Score: " + bestScore + "\n");
            bw.write(bestSolution.printResourceAssignation() + "\n");
            //val result = bestSolution.printResourceAssignation()

            if (unImprovementCount > 25) {
                //DEBUG
                bw.write("+++++++ Solution Stucked!!: Resetting Pheromone Matrix +++++++" + "\n");
                resetPheromoneMatrix();
                unImprovementCount = 0;
            } else {
                updatePheromoneMatrix(solutions);
                //DAEMON: MAX
                addPheromonesToSolution(iterationBestSolution);
                //TODO: DAEMON MIN
            }
            addPheromonesToSolution(bestSolution);
            //Guardar el tiempo de la iteracion
            double t1 = System.currentTimeMillis();
            double elapsedTime = t1 - t0;
            bw.write("-- Time for the iteration: " + i + " = " + elapsedTime + "\n");
        }
        // --------------- END OF THE ACO ---------------
        bw.write("------->>> Best global solution: " + "\n");
        bw.write(bestSolution.printResourceAssignation() + "\n");
    }

    private void updatePheromoneMatrix(DataModel[] solutions) {
        //DEBUG
        //println("`Updating pheromone matrix")
        int cont = 0;

        for (int a = 0; a < pheromoneMatrix.length; a++) {
            for (int b = 0; b < pheromoneMatrix[a].length; b++) {
                pheromoneMatrix[a][b] = (1 - pheromoneEvaporationCoefficient) * pheromoneMatrix[a][b];
            }
        }

        for (DataModel solution : solutions) {
            //DEBUG
            //println("Solution: " + cont)
            cont += 1;
            addPheromonesToSolution(solution);
        }
    }

    private void addPheromonesToSolution(DataModel solution) {

        double pheromoneAmount = computePheromoneAmount(solution);
        //DEBUG
        //println("Pheromone amount: " + pheromoneAmount)

        for (Resource resource : dataModel.getResources()) {
            Step[] steps = resource.getSteps();
            Step previousStep = steps[0];
            for (int i = 1; i < steps.length; i++) {
                //DEBUG
                //println("Updating from tasks: " + previousStep.getTaskIndex() + " to " + steps(i).getTaskIndex)

                pheromoneMatrix[previousStep.getTaskIndex()][steps[i].getTaskIndex()] = pheromoneMatrix[previousStep.getTaskIndex()][steps[i].getTaskIndex()] + pheromoneAmount;
                previousStep = steps[i];
            }
        }
    }

    /*
    El depósito de feromonas se basa en el número de tareas que no han podido ser cubiertas. En particular, cuantas más
    tareas sin cubrir menor es el refuerzo. Para modelar el refuerzo se hace uso de la siguiente fórmula,
    donde Q es la recompensa y H el número de tareas no cubiertas
     */
    private double computePheromoneAmount(DataModel solution) {
        //DEBUG
        //println("NUMBER OF TASKS: " + solution.getnTasks)
        //println("NUMBER OF TASKS COVERED: " + solution.getTasksCovered)
        int H = solution.getnTasks() - solution.getTasksCovered();

        if (H > 0) return this.pheromoneConstant / H;
        else if (H == 0) return pheromoneConstant;
        else
            throw new IllegalStateException(" ------ Something went wrong with the assignations: There was more tasks covered than the actual number of tasks");

    }

    private void resetPheromoneMatrix() {
        for (int a = 0; a < pheromoneMatrix.length; a++) {
            for (int b = 0; b < pheromoneMatrix[a].length; b++) {
                pheromoneMatrix[a][b] = this.initialPheromone;
            }
        }
    }

}
