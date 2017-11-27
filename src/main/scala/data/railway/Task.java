package data.railway;

import java.io.Serializable;

/**
 * Created by Pablo on 19/6/17.
 */
public class Task implements Serializable {

    private CheckPoint initialCheckPoint;
    private CheckPoint finalCheckPoint;
    private int taskIndex;

    public Task(CheckPoint initialCheckPoint, CheckPoint finalCheckPoint, int taskIndex) {
        this.initialCheckPoint = initialCheckPoint;
        this.finalCheckPoint = finalCheckPoint;
        this.taskIndex = taskIndex;
    }

    /**
     * @return taskTime: Number of minutes needed to carry the task
     */
    public int getTaskTime() {
        return finalCheckPoint.getArrivalTime() - initialCheckPoint.getArrivalTime();
    }

    public int getInitialTime(){
        return initialCheckPoint.getArrivalTime();
    }

    public int getFinalTime(){
        return finalCheckPoint.getArrivalTime();
    }

    public String getInitialStation(){
        return initialCheckPoint.getStation();
    }

    public String getFinalStation(){
        return finalCheckPoint.getStation();
    }

    public CheckPoint getInitialCheckPoint() {
        return initialCheckPoint;
    }

    public void setInitialCheckPoint(CheckPoint initialCheckPoint) {
        this.initialCheckPoint = initialCheckPoint;
    }

    public CheckPoint getFinalCheckPoint() {
        return finalCheckPoint;
    }

    public void setFinalCheckPoint(CheckPoint finalCheckPoint) {
        this.finalCheckPoint = finalCheckPoint;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

}
