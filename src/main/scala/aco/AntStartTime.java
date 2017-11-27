package aco;

import data.DataModel;

import java.util.List;

/**
 * Created by Pablo on 20/6/17.
 */
public class AntStartTime extends Ant{
    public AntStartTime(double[][] pheromoneMatrix, double[][] distanceMatrix, double ALPHA, double BETA, DataModel model) {
        super(pheromoneMatrix, distanceMatrix, ALPHA, BETA, model);
    }

    //TODO: devuelva una lista de los indices de los recursos ordenados por la estacion que tengan tareas que empiecen antes
    @Override
    protected List<Integer> selectVariables() {
        return null;
    }
}
