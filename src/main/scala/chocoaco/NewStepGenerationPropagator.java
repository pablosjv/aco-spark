package chocoaco;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

public class NewStepGenerationPropagator extends Propagator<IntVar> {

    @Override
    public void propagate(int evtmask) throws ContradictionException {

    }

    @Override
    public ESat isEntailed() {
        return null;
    }
}
