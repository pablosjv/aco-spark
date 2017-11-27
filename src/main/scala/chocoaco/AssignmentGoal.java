package chocoaco;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

public class AssignmentGoal extends IntStrategy {

    public AssignmentGoal(IntVar[] scope, VariableSelector<IntVar> varSelector, IntValueSelector valSelector) {
        super(scope, varSelector, valSelector);
    }


}

