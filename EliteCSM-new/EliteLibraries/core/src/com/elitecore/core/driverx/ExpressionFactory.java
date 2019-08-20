package com.elitecore.core.driverx;


public interface ExpressionFactory {
	public ValueExpression newInstance(String expressionStr) throws Exception;
}

