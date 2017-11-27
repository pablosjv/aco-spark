package chocoaco;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Pablo on 19/6/17.
 */
public class AntValueSelector implements IntValueSelector, Serializable {


    @Override
    public int selectValue(IntVar var) {
        return 0;
    }
}

