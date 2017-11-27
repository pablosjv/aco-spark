package data.railway;

import java.io.Serializable;

/**
 * Created by Pablo on 19/6/17.
 */
public class CheckPoint implements Serializable {

    private String station;
    private int arrivalTime;


    public CheckPoint(String station, int arrivalTime) {
        this.station = station;
        this.arrivalTime = arrivalTime;
    }


    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
