/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.search.strategy.decision;

import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.explanations.RuleStore;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperator;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IEventType;
import org.chocosolver.util.PoolManager;

/**
 * A decision based on a {@link IntVar}
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 2 juil. 2010
 */
public class IntDecision extends Decision<IntVar> {

    private static final long serialVersionUID = 4319290465131546449L;
 
    /**
     * The decision value
     */
    private int value;
    /**
     * The assignment operator
     */
    private DecisionOperator<IntVar> assignment;
    /**
     * Decision pool manager, to recycle decisions
     */
    transient private final PoolManager<IntDecision> poolManager;

    /**
     * Create an decision based on an {@link IntVar}
     * @param poolManager decision pool manager, to recycle decisions
     */
    public IntDecision(PoolManager<IntDecision> poolManager) {
        super(2);
        this.poolManager = poolManager;
    }

    @Override
    public Integer getDecisionValue() {
        return value;
    }

    @Override
    public void apply() throws ContradictionException {
        if (branch == 1) {
            assignment.apply(var, value, this);
        } else if (branch == 2) {
            assignment.unapply(var, value, this);
        }
    }

    /**
     * Instantiate this decision with the parameters
     * @param v a variable
     * @param value a value
     * @param assignment a decision operator
     */
    public void set(IntVar v, int value, DecisionOperator<IntVar> assignment) {
        super.set(v);
        this.value = value;
        this.assignment = assignment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reverse() {
        this.assignment = assignment.opposite();
    }

    @Override
    public void free() {
        poolManager.returnE(this);
    }

    @Override
    public IntDecision duplicate() {
        IntDecision d = poolManager.getE();
        if (d == null) {
            d = new IntDecision(poolManager);
        }
        d.set(var, value, assignment);
        return d;
    }

    @Override
    public boolean isEquivalentTo(Decision dec) {
        if (dec instanceof IntDecision) {
            IntDecision id = (IntDecision) dec;
            return (id.var == this.var
                    && id.assignment == this.assignment
                    && id.value == this.value
                    && id.max_branching == this.max_branching
                    && id.branch == this.branch);
        } else {
            return false;
        }
    }

    /**
     * @return the current decision operator
     */
    public DecisionOperator<IntVar> getDecOp() {
        return assignment;
    }

    /**
     * @return a copy of this decision wherein the he decision operator is reversed
     */
    @SuppressWarnings("unchecked")
    public IntDecision flip(){
        IntDecision d = poolManager.getE();
        if (d == null) {
            d = new IntDecision(poolManager);
        }
        int val = value;
        if(assignment == DecisionOperatorFactory.makeIntSplit()){
            val++;
        }
        else if(assignment == DecisionOperatorFactory.makeIntReverseSplit()){
            val--;
        }
        d.set(var, val, assignment.opposite());
        return d;
    }

    @Override
    public String toString() {
        if (assignment.equals(DecisionOperatorFactory.makeIntEq())) {
            return String.format("%s %s {%d}",
                    var.getName(),
                    branch < 1 ? "=" : '\\',
                    value);
        } else if (assignment.equals(DecisionOperatorFactory.makeIntNeq())) {
            return String.format("%s %s {%d}",
                    var.getName(),
                    branch < 1 ? '\\' : "=",
                    value);
        } else if (assignment.equals(DecisionOperatorFactory.makeIntSplit())) {
            return String.format("%s in %s%d,%d]",
                    var.getName(),
                    branch < 1 ? '[' : ']',
                    branch < 1 ? var.getLB() : value,
                    branch < 1 ? value : var.getUB());
        } else if (assignment.equals(DecisionOperatorFactory.makeIntReverseSplit())) {
            return String.format("%s in [%d,%d%s",
                    var.getName(),
                    branch < 1 ? value : var.getLB(),
                    branch < 1 ? var.getUB() : value,
                    branch < 1 ? ']' : '[');
        } else {
            return String.format("%s %s {%s}",
                    var.getName(),
                    branch < 1 ? assignment.toString() : assignment.opposite().toString(),
                    value);
        }
    }

    @Override
    public boolean why(RuleStore ruleStore, IntVar var, IEventType evt, int value) {
        return false;
    }
}
