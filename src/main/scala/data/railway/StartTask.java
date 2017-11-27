package data.railway;

/**
 * Created by Pablo on 19/6/17.
 */
public class StartTask extends Task {


    public StartTask(String station, int taskIndex) {
        super(new CheckPoint(station, -1), new CheckPoint(station, -1), taskIndex);

    }

    @Override
    public int getTaskTime() {
        return 0;
    }
}
