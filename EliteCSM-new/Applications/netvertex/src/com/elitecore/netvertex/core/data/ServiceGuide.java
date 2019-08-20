package com.elitecore.netvertex.core.data;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class ServiceGuide {

    private LogicalExpression condition;
    private String serviceId;
    private String conditionStr;

    public ServiceGuide(LogicalExpression condition, String conditionStr, String alias) {
        this.condition = condition;
        this.conditionStr = conditionStr;
        this.serviceId = alias;
    }

    public LogicalExpression getCondition() {
        return condition;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getConditionStr() {
        return conditionStr;
    }
}
