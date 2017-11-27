package data.railway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 19/6/17.
 */
public class Resource implements Serializable {

    private String baseStation;
    private int baseStationIndex;
    private int resourceIndex;
    private int workedMinutes;
    private final int workday;
    private List<Step> steps;
    private int nSteps;
    private int nTaskCovered;

    private int nTasks;

    public Resource(String baseStation, int baseStationIndex, int workday, int resourceIndex, int nTasks) {
        this.baseStation = baseStation;
        this.baseStationIndex = baseStationIndex;
        this.workday = workday;
        this.resourceIndex = resourceIndex;
        this.workedMinutes = 0;
        this.nSteps = 0;
        this.nTaskCovered = 0;
        this.steps = new ArrayList<>();
        this.nTasks = nTasks;
        this.steps.add(new Step(0, new StartTask(baseStation, nTasks + baseStationIndex)));
    }

    public Step[] getSteps() {
        return steps.toArray(new Step[steps.size()]);
    }

    public Step getLastStep() {
        return this.steps.get(this.nSteps);
    }


    /**
     * Add a new step with the task passed in the parameter
     *
     * @param task               the Task to add to the resource
     * @param taskAlreadyCovered if the task which are going to be assigned is already covered = True
     * @return boolean to indicate if the step was added or not
     */
    public boolean addStep(Task task, boolean taskAlreadyCovered) {
        boolean success = false;
        Step lastStep = this.getLastStep();
        int lastStepFinalTime = lastStep.getTask().getFinalTime();
        int workedTimeIncrement;

        if (lastStepFinalTime < 0) {
            workedTimeIncrement = task.getTaskTime();

        } else workedTimeIncrement = task.getTaskTime() + (task.getInitialTime() - lastStepFinalTime);

        //DEBUG
        //System.out.println("lastStepFinalTime: " + lastStepFinalTime);
        //System.out.println("New Task initial time: " + task.getInitialTime());
        //System.out.println("New Task final time: " + task.getFinalTime());
        //System.out.println("Task time: " + task.getTaskTime());
        //System.out.println("Working increment: " + workedTimeIncrement);
        //System.out.println("Worked minutest: " + workedMinutes);
        //System.out.println("~ RESOURCE: " + this.resourceIndex
        //        + " PREVIOUS WORKED MINUTES: " + workedMinutes
        //        + "(lastStepFinalTime: " + lastStepFinalTime + ") "
        //        + " TASK ADDED: " + task.getTaskIndex()
        //        + " (New Task initial time: " + task.getInitialTime()
        //        + " New Task final time: " + task.getFinalTime()
        //        + " Task time: " + task.getTaskTime() + ") "
        //        + " WITH TIME INCREMENT: " + workedTimeIncrement
        //        + " CURRENT WORKED MINUTES " + (workedMinutes + workedTimeIncrement));


        //if ((workedMinutes + workedTimeIncrement) > workday) {
        ////Assignation not possible
        //} else {

        this.steps.add(new Step(workedMinutes, task));
        nSteps += 1;
        workedMinutes += workedTimeIncrement;

        if (!taskAlreadyCovered) {
            nTaskCovered += 1;
            success = true;
        }

        //}

        return success;
    }

    /**
     * @param task Task to check if it is going to be assigned
     * @return 0 if it is not possible to add the step, or 1 if it is possible
     */
    public int possibleStep(Task task) {
        int result;
        Step lastStep = this.getLastStep();
        int lastStepFinalTime = lastStep.getTask().getFinalTime();

        int workedTimeIncrement;
        if (lastStepFinalTime < 0) {
            workedTimeIncrement = task.getTaskTime();

        } else workedTimeIncrement = task.getTaskTime() + (task.getInitialTime() - lastStepFinalTime);

        //DEBUG
        //System.out.println("RESOURCE WORKDAY: " + workday);
        //System.out.println("WORKED MINUTES: " + workedMinutes);
        //System.out.println("WORK INCREMENT: " + workedTimeIncrement);
        if ((workedMinutes + workedTimeIncrement) > workday) {
            //Assignation not possible
            result = 0;
        } else {
            result = 1;
        }

        return result;
    }

    public Resource clone(){
        //System.out.println("CLONING RESOURCE " + resourceIndex);
        return new Resource(baseStation, baseStationIndex, workday, resourceIndex, nTasks);
    }

    public int getWorkedMinutes() {
        return workedMinutes;
    }

    //public void setWorkedMinutes(int workedMinutes) {
    //    this.workedMinutes = workedMinutes;
    //}

    public int getnTaskCovered() {
        return nTaskCovered;
    }


    //public void setnTaskCovered(int nTaskCovered) {
    //    this.nTaskCovered = nTaskCovered;
    //}

    public int getnSteps() {
        return nSteps;
    }

    public int getWorkday() {
        return this.workday;
    }

    public int getResourceIndex() {
        return resourceIndex;
    }

}
