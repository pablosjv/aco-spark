package chocoaco;

import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

import java.io.Serializable;

/**
 * Created by Pablo on 19/6/17.
 */
public class AntVariableSelector implements VariableSelector<IntVar>, Serializable {
    @Override
    public IntVar getVariable(IntVar[] variables) {
        return null;
    }
}


