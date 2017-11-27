package data.railway;

import java.io.Serializable;

/**
 * Created by Pablo on 19/6/17.
 */
public class Step implements Serializable {
    private int accumulatedWorkedMinutes;
    private Task task;

    public Step(int previousWorkedMinutes, Task task) {
        this.accumulatedWorkedMinutes = previousWorkedMinutes + task.getTaskTime();
        this.task = task;
    }


    public int getAccumulatedWorkedMinutes() {
        return accumulatedWorkedMinutes;
    }

    public void setAccumulatedWorkedMinutes(int accumulatedWorkedMinutes) {
        this.accumulatedWorkedMinutes = accumulatedWorkedMinutes;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getTaskIndex(){
        return this.task.getTaskIndex();
    }
}
