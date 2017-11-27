package data.railway;


import java.io.Serializable;

/**
 * Created by Pablo on 15/6/17.
 */
public class Train implements Serializable {

    //    private List<Integer> schedule;
//    private List<String> stations;
    private int[] schedule;
    private String[] stations;
    private int nStops;

    public Train(int[] schedule, String[] stations) {

        this.schedule = schedule;
        this.stations = stations;
        this.nStops = schedule.length;
    }


    public String[] getStations() {
        return stations;
    }

    public void setStations(String[] stations) {
        this.stations = stations;
    }

    public int[] getSchedule() {
        return schedule;
    }

    public void setSchedule(int[] schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        String value = super.toString();
        for (int i = 0; i < this.schedule.length; i++) {
            value = value + "\n" + this.schedule[i] + " " + this.stations[i];
        }
//        value = stations + schedule.toString();
        return value;
    }


    public int getnStops() {
        return nStops;
    }

    public void setnStops(int nStops) {
        this.nStops = nStops;
    }
}
