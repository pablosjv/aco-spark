package data;

import data.railway.*;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Pablo on 19/6/17.
 */
public class DataModel implements Serializable {

    private String[] stations;                  //name of the station
    private int nStations;
    private CheckPoint[] checkPoints;           //index to the station, and time to arrive to that station
    private int nResources;
    private Resource[] resources;
    private Train[] trains;

    private int workday;

    private int[] resourceBase;                 //foreach resource, an index to its base station
    private Task[] tasks;                       //each task has an index for the initial checkpoint and the final checkpoint
    private int nTasks;
    private int tasksCovered;
    //private int[] workedMinutes;              //worked minitues for each resource
    // FIXME: Considerar este array como el numero de recursos que cubren una tarea???
    private int[] assignationTaskToResource;     //it has the size of number of tasks


    public DataModel(Train[] trains, String[] stations, int[] resourcesBase) {
        this(trains, stations, resourcesBase, 8 * 3600);
    }

    public DataModel(Train[] trains, String[] stations, int[] resourcesBase, int workday) {
        this.trains = trains;

        this.workday = workday;

        this.stations = stations;
        this.nStations = stations.length;
        this.resourceBase = resourcesBase;
        this.nResources = resourcesBase.length;

        this.generateCheckpointsAndTasks(trains);
        //this.nTasks = this.tasks.length;          Is assignated inside the method
        this.tasksCovered = 0;
        this.resources = new Resource[nResources];
        this.initializeResources();
        //DEBUG
        //System.out.println(" ******* Number of resources: " + this.nResources + " or " + this.resources.length);
        this.assignationTaskToResource = new int[this.nTasks];

        Arrays.fill(assignationTaskToResource, -1);

    }

    public DataModel(String[] stations, int nStations, CheckPoint[] checkPoints,
                     int nResources, Train[] trains, int workday, int[] resourceBase, Task[] tasks, int nTasks,
                     int[] assignationTaskToResource, Resource[] resources) {

        this.stations = stations;
        this.nStations = nStations;
        this.checkPoints = checkPoints;
        this.nResources = nResources;
        //DEBUG
        //System.out.println("------------------ N RESOURCES = " + resources.length);
        this.resources = new Resource[resources.length];
        for (int i = 0; i < resources.length; i++) {
            this.resources[i] = resources[i].clone();
        }
        //this.resources = resources.clone();
        this.trains = trains;
        this.workday = workday;
        this.resourceBase = resourceBase;
        this.tasks = tasks.clone();
        this.nTasks = nTasks;
        this.tasksCovered = 0;
        this.assignationTaskToResource = new int[this.nTasks];
        Arrays.fill(this.assignationTaskToResource, -1);
    }

    @Override
    public DataModel clone() {

        return new DataModel(this.stations, this.nStations, this.checkPoints, this.nResources, this.trains,
                this.workday, this.resourceBase, this.tasks, this.nTasks, this.assignationTaskToResource, this.resources);
    }

    private void initializeResources() {
        for (int i = 0; i < this.nResources; i++) {
            this.resources[i] = new Resource(this.stations[this.resourceBase[i]], this.resourceBase[i], workday, i, this.nTasks);
        }
    }

    private void generateCheckpointsAndTasks(Train[] trains) {
        int contCheckpoints = 0;
        int contTasks = 0;
        int[] schedule;
        String[] trainStations;
        //List<String> stationsList = Arrays.asList(this.stations);


        int nCheckpoints = 0;
        int nTasks = 0;

        for (Train train1 : trains) {
            nCheckpoints += train1.getnStops();
            nTasks += train1.getnStops() - 1;
        }

        this.tasks = new Task[nTasks + this.stations.length];
        this.checkPoints = new CheckPoint[nCheckpoints];

        for (Train train : trains) {
            schedule = train.getSchedule();
            trainStations = train.getStations();

            CheckPoint lastCheckPoint = null;
            for (int j = 0; j < train.getnStops(); j++) {

                checkPoints[contCheckpoints] = new CheckPoint(trainStations[j], schedule[j]);
                if (j != 0) {
                    this.tasks[contTasks] = new Task(lastCheckPoint, checkPoints[contCheckpoints], contTasks);
                    contTasks++;
                }
                lastCheckPoint = checkPoints[contCheckpoints];
                contCheckpoints++;
            }
        }

        for (int i = 0; i < this.stations.length; i++) {
            tasks[contTasks + i] = new StartTask(this.stations[i], contTasks + i);
        }

        this.nTasks = contTasks;
    }

    public String printResourceAssignation() {
//        System.out.println("--------------------------------------------");
//        for (Resource resource : this.resources) {
//            System.out.println("RESOURCE: " + resource.getResourceIndex());
//            System.out.println("Task Covered: " + resource.getnTaskCovered());
//            System.out.println("Number of steps: " + resource.getnSteps());
//            System.out.println("Worked Minutes: " + resource.getWorkedMinutes() + " of a workday with: " + resource.getWorkday());
//            for (Step step : resource.getSteps()) {
//                Task task = step.getTask();
//                System.out.println("Task: " + task.getTaskIndex()
//                        + " from: " + task.getInitialCheckPoint().getStation() + " at " + task.getInitialTime()
//                        + "; to: " + task.getFinalCheckPoint().getStation() + " at " + task.getFinalTime());
        //System.out.println("--------------------------------------------");
//
//            }
        //System.out.println("--------------------------------------------");
//
//        }
//
        //System.out.println("--------------------------------------------");
        //System.out.println("END OF THE ASSIGNATIONS");

        String result = "TASKS COVERED IN TOTAL: " + this.tasksCovered + "\n" +
                "TASKS NOT COVERED IN TOTAL: " + (this.nTasks - this.tasksCovered) + "\n" +
                "RESOURCES USED: " + this.resources.length;
        //System.out.println("TASKS COVERED IN TOTAL: " + this.tasksCovered);
        //System.out.println("TASKS NOT COVERED IN TOTAL: " + (this.nTasks - this.tasksCovered));
        //System.out.println("RESOURCES USED: " + this.resources.length);
        //System.out.println("--------------------------------------------");
        return result;
    }

    public int getAssignationScore() {
        int score = 0;
        for (int i : this.assignationTaskToResource) {
            //DEBUG
            //System.out.println("VALOR DE I ====================== " + i);
            if (i >= 0) {
                score += 1;
            }
        }
        if (score == this.assignationTaskToResource.length) {
            //DEBUG
            //System.out.println("OJOOOOOOOOOOOOOOOOOOO");
            score += nResources - this.resources.length;
        }
        return score;
    }


    public Task[] getTasks() {
        return this.tasks;
    }

    public Task getTaskWithIndex(int i) {
        return this.tasks[i];
    }

    public Resource[] getResources() {
        return this.resources;
    }

    public Resource getResourceWithIndex(int i) {
        return this.resources[i];
    }

    public int getnStations() {
        return nStations;
    }

    public int[] getAssignationTaskToResource() {
        return assignationTaskToResource;
    }

    public void setAssignationTaskToResource(int taskIndex, int resourceIndex) {
        assignationTaskToResource[taskIndex] = resourceIndex;
    }

    public int getnResources() {
        return nResources;
    }

    public int getnTasks() {
        return nTasks;
    }

    public void setResources(Resource[] resources) {

        this.tasksCovered = 0;
        for (Resource resource : resources) {
            this.tasksCovered += resource.getnTaskCovered();
        }
        this.resources = resources;
    }

    public int getTasksCovered() {
        return tasksCovered;
    }

    public int getWorkday() {
        return workday;
    }
}
