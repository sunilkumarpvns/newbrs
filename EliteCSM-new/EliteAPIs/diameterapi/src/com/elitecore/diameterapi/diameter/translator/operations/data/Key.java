package com.elitecore.diameterapi.diameter.translator.operations.data;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public interface Key<T> {
	public KeyTypes getKeyType();
	public T getElement();
	public String getParent();
	public LogicalExpression getCondition();
}
