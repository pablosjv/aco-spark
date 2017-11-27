package aco;

import data.DataModel;
import data.railway.Resource;
import data.railway.Task;

import java.io.Serializable;
import java.util.*;


/**
 * Created by Pablo on 26/5/17.
 */
public abstract class Ant implements Serializable {
    /**
     * A parameter from the ACO algorithm to control the influence of the amount of pheromone when making a choice
     */
    private final double ALPHA;
    /**
     * A parameters from ACO that controls the influence of the distance to the next node in _pick_path()
     */
    private final double BETA;

    private final double[][] pheromoneMatrix;

    private final double[][] distanceMatrix;

    protected final DataModel dataModel;
    protected DataModel solutionDataModel;
    private double explorationProbability = 0.75;
    //private Object route;   //a list that is updated with the labels of the nodes that the ant has traversed
    private Random generator = new Random();

    public Ant(double[][] pheromoneMatrix, double[][] distanceMatrix, double ALPHA, double BETA, DataModel model) {
        this.pheromoneMatrix = pheromoneMatrix;
        this.distanceMatrix = distanceMatrix;
        this.ALPHA = ALPHA;
        this.BETA = BETA;
        this.dataModel = model;
        //route = null;
        solutionDataModel = null;
    }


    /**
     * @return a solution for the entire model in a Tuple 2 form. The first element of the tuple is the score for the solution and the second one is the whole solution dataModel object
     */
    public DataModel findSolution() {

        // Clone the dataModel because the parameters are going to change with the assignations
        solutionDataModel = this.dataModel.clone();

        //DEBUG
        //System.out.println("ENTROOOOOOOOO");
        //System.out.println("******** Solution initial Score: " + solutionDataModel.getAssignationScore());
        //System.out.println("******** Solution initial tasks Covered: " + solutionDataModel.getTasksCovered());
        //System.out.println("******** Solution initial resources Used: " + solutionDataModel.getResources().length);
        //int resourcesUsed = 0;
        int taskCovered = 0;
        int nTasks = solutionDataModel.getAssignationTaskToResource().length;

        List<Integer> resourceOrderedIndexes = selectVariables();
        Iterator<Integer> resourceIterator = resourceOrderedIndexes.iterator();
        int resourceIndex;
        Resource resourceAssignation;
        ArrayList<Resource> resourcesSolution = new ArrayList<>();

        while (resourceIterator.hasNext() && taskCovered < nTasks) {

            //resourceIndex = selectVariables();
            resourceIndex = resourceIterator.next();
            //DEBUG
            //System.out.println("Selected resource with index = " + resourceIndex);
            resourceAssignation = selectValues(solutionDataModel.getResourceWithIndex(resourceIndex), resourceIndex);
            if (resourceAssignation != null && resourceAssignation.getnTaskCovered() > 0) {
                taskCovered += resourceAssignation.getnTaskCovered();
                //resourcesUsed += 1;
                resourcesSolution.add(resourceAssignation);
                //DEBUG
                //System.out.println("~~~~~~~~~~~~~~~~~~~~ Resource assigned~~~~~~~~~~~~~~~~~~~~");
            }
        }
        //DEBUG
        //System.out.println("************************** Assignation found ************************** ");
        solutionDataModel.setResources(resourcesSolution.toArray(new Resource[resourcesSolution.size()]));
        //return new Tuple2(solutionDataModel.getAssignationScore(), solutionDataModel);
        return solutionDataModel;
    }

    /**
     * Returns a list of indexes corresponding the resources in the order in which they have to be assigned.
     */
    // Different algorithms will select variables in different ways
    protected abstract List<Integer> selectVariables();

    /**
     * Select the values for the specific resource
     */
    private Resource selectValues(Resource resource, int resourceIndex) {

        int taskIndex = 0;
        int minimum = 0;
        boolean firstAssignation = true;
        Resource resourceAssignationSafe = resource;            //TODO: Check if this is necessary
        try {
            //DEBUG
            //System.out.println("~~~~~~~~~~~~~~~~~ Assigning tasks to RESOURCE: " + resource.getResourceIndex() + " ~~~~~~~~~~~~~~~~~");
            while (resource.getWorkedMinutes() <= solutionDataModel.getWorkday()) {
                //DEBUG
                //System.out.println("^^^ WORKED MINUTES: " + resource.getWorkedMinutes() + " WORKDAY: " + solutionDataModel.getWorkday());

                // Calcular un valor aleatorio
                Double random = generator.nextDouble();
                if (random > this.explorationProbability) {
                    // Seleccionar tarea de forma aleatora
                    //DEBUG
                    //System.out.println("*** SELECTING TASK RANDOMLY *** ");

                    resource = this.selectRandomTask(resource, random);
                } else {
                    // Seleccionar tarea mas probable
                    //DEBUG
                    //System.out.println("*** SELECTING TASK BASED ON PROBABILITY *** ");

                    resource = this.selectMostProbableTask(resource);
                }

                //DEBUG:
                //if (firstAssignation) System.out.println("->->->->->->-> First assignation for the resource: " + resource.getResourceIndex() + " DONE");
                //System.out.println("->->->->->->-> ASSIGNATION ACHIEVE FOR THE RESOURCE: " + resource.getResourceIndex());

                firstAssignation = false;
                resourceAssignationSafe = resource;

            }

            //DEBUG
            //System.out.println("** The Resource: " + resource.getResourceIndex() + " has reached the time limit");
        } catch (ArrayIndexOutOfBoundsException e) {
            //No Se puede hacer mas asignaciones en ese recurso
            //DEBUG
            //System.out.println("** No more assignations possible for resource: " + resource.getResourceIndex());
            if (firstAssignation) {
                resourceAssignationSafe = null;
            }
        }

        return resourceAssignationSafe;
    }

    /**
     * Select a random task. It only considers the task which it distance is > 0.0
     *
     * @return
     */
    private Resource selectRandomTask(Resource resource, double random) {
        double probability = 0.0;
        double evaluation = 0.0;
        int selectedTaskIndex = -1;
        int possibleTask = -1;
        int currentTask = resource.getLastStep().getTaskIndex();
        Task[] tasks = this.solutionDataModel.getTasks();

        double selectionCriteria = random - (random - this.explorationProbability); //TODO: Check how to do it properly
//        double selectionCriteria = 0.5
        int i = 0;

        while (i < this.solutionDataModel.getnTasks()) {
            evaluation = computeProbability(currentTask, i, resource.possibleStep(tasks[i]));

            if (evaluation > 0.0) {
                possibleTask = i;
                if (new Random().nextDouble() < selectionCriteria) {
                    selectedTaskIndex = i;
                    break;
                }
            }
            i += 1;
        }
        if (selectedTaskIndex < 0) {
            selectedTaskIndex = possibleTask;
        }
        //Debug
        //System.out.println("======== Selecting the task " + selectedTaskIndex + " randomly ========");
        resource = this.addTaskToResource(resource, selectedTaskIndex);
        return resource;
    }

    private Resource selectMostProbableTask(Resource resource) {

        double probability = 0.0;
        double evaluation = 0.0;
        int selectedTaskIndex = -1;
        int currentTask = resource.getLastStep().getTaskIndex();
        Task[] tasks = this.solutionDataModel.getTasks();
        //DEBUG
        //System.out.println("***************** NUMBER OF TASKS: " + this.solutionDataModel.getnTasks() + " ***************** ");
        for (int i = 0; i < this.solutionDataModel.getnTasks(); i++) {
            evaluation = computeProbability(currentTask, i, resource.possibleStep(tasks[i]));
            //DEBUG:
            //System.out.println("It is a possible step in time?: " + resource.possibleStep(tasks[i]));
            //System.out.println("Evaluation form the task " + currentTask + " to the task " + i + " = " + evaluation);
            //System.out.println("Current best probability = " + probability);
            if (evaluation > probability) {             //TODO: Check if it is correct
                probability = evaluation;
                selectedTaskIndex = i;
            }
        }

        //DEBUG:
        //System.out.println("======== Selecting the task " + selectedTaskIndex + " with a probability of " + probability + " ========");
        // NOTE: If all the probabilities are 0 => it should raise ArrayIndexOutBound exception
        resource = this.addTaskToResource(resource, selectedTaskIndex);
        return resource;
    }

    private Resource addTaskToResource(Resource resource, int taskIndex) {
        boolean success = false;
        boolean taskAlreadyCovered = (solutionDataModel.getAssignationTaskToResource()[taskIndex] >= 0);
        success = resource.addStep(solutionDataModel.getTasks()[taskIndex], taskAlreadyCovered);

        if (success) this.solutionDataModel.setAssignationTaskToResource(taskIndex, resource.getResourceIndex());
        return resource;
    }

    private double computeProbability(int startTaskIndex, int endTaskIndex, int possibleStepFactor) {

        double distance = Math.pow(this.computeDistance(startTaskIndex, endTaskIndex) * possibleStepFactor, this.BETA);
        double pheromone = Math.pow(pheromoneMatrix[startTaskIndex][endTaskIndex], this.ALPHA);
        //DEBUG
        //System.out.println("Computed distance: " + distance);
        return distance * pheromone;

    }


    //Computa la distancia entre dos tareas segun la diferencia de tiempo, la estacion de origen y de destino y si ha sido cubierta o no
    private double computeDistance(int startTaskIndex, int endTaskIndex) {
        double distance = 0.0;

        Task[] tasks = this.solutionDataModel.getTasks();
        int[] assignationTaskToResource = this.solutionDataModel.getAssignationTaskToResource();
//        int timeDifference;
//        int epsilon = 1;
//
//        if (tasks[startTaskIndex].getFinalStation() == tasks[endTaskIndex].getFinalStation()) {
//            timeDifference = tasks[endTaskIndex].getInitialTime() - tasks[startTaskIndex].getFinalTime();
//
//            if (timeDifference >= 0){
//                distance = 1/(timeDifference + 1);
//
//            }
//        }else {
//            distance = -1;
//        }
        distance = distanceMatrix[startTaskIndex][endTaskIndex];

        //DEBUG
        //System.out.println("Raw distance from the distance matrix: " + distance);
//        if (distance > 0){
//            System.out.println("POSIBLE TASK FOUND");
//        }

        if (distance < 0) {
            distance = 0;
        } else if (assignationTaskToResource[endTaskIndex] >= 0) {              //NOTE: PENALIZA A LOS QUE YA ESTEN ASIGNADOS
            distance = distance / 2;                      //TODO: Check this
        }

        return distance;
    }

    public double[][] getPheromoneMatrix() {
        return pheromoneMatrix;
    }

    public void printPheromoneMatrix(){

        for (int i = 0; i < pheromoneMatrix.length; i++) {
            for (int j = 0; j < pheromoneMatrix[i].length; j++) {
                System.out.print(pheromoneMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printDistanceMatrix(){
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                System.out.print(distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

}
