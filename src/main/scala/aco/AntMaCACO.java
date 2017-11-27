package aco;

import data.DataModel;

import java.util.List;

/**
 * Created by Pablo on 20/6/17.
 */
public class AntMaCACO extends Ant {
    public AntMaCACO(double[][] pheromoneMatrix, double[][] distanceMatrix, double ALPHA, double BETA, DataModel model) {
        super(pheromoneMatrix, distanceMatrix, ALPHA, BETA, model);
    }

    //TODO: Devuelva una lista de los indices de los recursos en orden
    @Override
    protected List<Integer> selectVariables() {
        return null;
    }
}
