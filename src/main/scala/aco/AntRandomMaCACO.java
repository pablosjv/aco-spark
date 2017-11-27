package aco;

import data.DataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pablo on 16/6/17.
 */
public class AntRandomMaCACO extends Ant {

    public AntRandomMaCACO(double[][] pheromoneMatrix, double[][] distanceMatrix, double alpha, double beta, DataModel model) {
        super(pheromoneMatrix, distanceMatrix, alpha, beta, model);
    }

    /**
     * Each resource is selected at random
     * @return
     */
    @Override
    protected List<Integer> selectVariables() {
        List<Integer> list = new ArrayList<>();
        for (int i=0; i<this.dataModel.getnResources(); i++) {
            list.add(i);
        }
        Collections.shuffle(list);

        return list;
    }
}
