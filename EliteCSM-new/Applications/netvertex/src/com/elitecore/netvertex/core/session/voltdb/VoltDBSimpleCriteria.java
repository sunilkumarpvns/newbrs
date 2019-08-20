package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.SimpleExpression;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;

import java.util.Objects;

/**
 * @author Prakash Pala
 * @since 12-Mar-2019
 * Simple Criteria Support for VoltDB.
 * Tables other than TBLT_SESSION, this criteria will be used and here only single and simple criterion (property "eq" value) is supported.
 */
public class VoltDBSimpleCriteria extends CriteriaImpl {

    private String simpleKey;
    private String simpleValue;

    public VoltDBSimpleCriteria(String schemaName) {
        super(schemaName);
    }

    @Override
    public void add(Criterion criterion) {
        if (Objects.nonNull(simpleKey)) {
            throw new UnsupportedOperationException("Only one criterion is supported in VoltDB");
        }

        if (criterion.getExpressionType() != Criterion.SIMPLE_EXPRESSION) {
            throw new UnsupportedOperationException("Only simple expression is supported in VoltDB");
        }

        SimpleExpression simpleExpression = (SimpleExpression)criterion;
        simpleKey = simpleExpression.getPropertyName();
        simpleValue = simpleExpression.getValue();
        super.add(criterion);
    }

    public String getSimpleKey() {
        return simpleKey;
    }

    public String getSimpleValue() {
        return simpleValue;
    }

}