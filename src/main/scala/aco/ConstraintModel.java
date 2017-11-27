package aco;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import data.railway.Train;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pablo on 13/6/17.
 * CP model using cho which is going to be pased to each ant
 */
public class ConstraintModel implements Serializable {

    private String[] stations;                  //name of the station
    private int nStations;
    private int[][] checkPoints;                //index to the station, and time to arrive to that station
    private int nResources;
    private Train[] trains;
    private int workday;
    private int[] resourceBase;                 //foreach resource, an index to its base station
    private int[][] tasks;                      //each task has an index for the initial checkpoint and the final checkpoint
    private int[] workedMinutes;                //worked minitues for each resource TODO: CHOCO VARIABLE??
    private int[] asignationTaskToResource;     //it has the size of number of tasks TODO: CHOCO VARIABLE??
    private int[][] resourceSteps;              //pasos de un recurso   =>   cada indice hace referencia a un recurso, el array de cada recurso puede ser mutable.

    Model model = new Model("RailwayAssingment");


    public ConstraintModel(Train[] trains, String[] stations, int[] resourcesBase) {
        this(trains, stations, resourcesBase, 8 * 3600);
    }

    public ConstraintModel(Train[] trains, String[] stations, int[] resourcesBase, int workday) {

        // ------------------ LOAD PARAMETERS ------------------
        this.trains = trains;
        this.stations = stations;
        this.resourceBase = resourcesBase;
        this.workday = workday;

        this.nStations = stations.length;
        this.nResources = resourcesBase.length;
        this.workedMinutes = new int[nResources];

        //this.generateCheckpointsAndTasks(trains);

        this.asignationTaskToResource = new int[tasks.length];

        this.resourceSteps = new int[nResources][];

        //------------------ VARIABLES ------------------
        // Assingment task to resource  TODO: Check boundedDomain
        IntVar[] assignation = model.intVarArray(
                "asignation", this.tasks.length, 0, this.nResources, false);

        // Worked minutes by resource   TODO: Check boundedDomain
        IntVar[] workedMinutes = model.intVarArray("workedMinutes", this.nResources, 0, workday, true);

        //------------------ CONSTRAINTS ------------------
        //Ningun recurso podrá trabajar mas minutos que la jornada en un dia
        for (int i = 0; i < this.nResources; i++) {

        }
        //Las unicas tareas que pueden ser asignadas inicialmente a un recurso son la vacia y la que tenga como origen su estacion base


        //En dos pasos consecutivos, la tarea asignada inicial y la siguiente compartirán la estación destino de la primera y la estación de comienzo de la segunda.



        //En dos pasos consecutivos, la tarea asignada inicial y la siguiente estarán ordenadas temporalmente, el tiempo de llegada al punto de control destino de la primera tarea será inferior al tiempo de llegada del punto de control origen de la tarea destino.


        //Una tarea sólo podrá ser cubierta una vez.


    }

//    private void generateCheckpointsAndTasks(Train[] trains) {
//        int contCheckpoints = 0;
//        int contTasks = 0;
//        int[] schedule;
//        String[] trainStations;
//        List<String> stationsList = Arrays.asList(this.stations);
//
//
//        int nCheckpoints = 0;
//        int nTasks = 0;
//
//        for (int i = 0; i < trains.length; i++) {
//            nCheckpoints += trains[i].getnStops();
//            nTasks += trains[i].getnStops() - 1;
//        }
//
//        this.tasks = new int[nTasks][2];
//        this.checkPoints = new int[nCheckpoints][2];
//
//        for (int i = 0; i < trains.length; i++) {
//            schedule = trains[i].getSchedule();
//            trainStations = trains[i].getStations();
//
//            //TODO: check loop
//            for (int j = 0; j < trains[i].getnStops(); j++) {
//                checkPoints[contCheckpoints][0] = stationsList.indexOf(trainStations[j]);
//                checkPoints[contCheckpoints][1] = schedule[j];
//                if (j != 0) {
//                    this.tasks[contTasks][0] = contCheckpoints - 1;
//                    this.tasks[contTasks][1] = contCheckpoints;
//                    contTasks++;
//                }
//                contCheckpoints++;
//            }
//        }
//    }


    /**
     * TODO: Method to asign a task to a resource wich also check if that asignment is posible.
     *
     * @param resource or resource id??
     * @param task     or task id??
     * @return true if the asignment is posible and false otherwise
     */
    public boolean asignTaskToResource(int resource, int task) {
        return false;
    }


    // ------------------------------------  GETTERS & SETTERS ------------------------------------

    public int[][] getTasks() {
        return tasks;
    }

    public void setTasks(int[][] tasks) {
        this.tasks = tasks;
    }
}
