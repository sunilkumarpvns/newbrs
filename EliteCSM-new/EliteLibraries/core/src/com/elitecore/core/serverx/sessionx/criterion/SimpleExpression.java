package com.elitecore.core.serverx.sessionx.criterion;


public class SimpleExpression implements Criterion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5119683741634023831L;
	
	private String propertyName;
	private String value;
	private String operator;
	public SimpleExpression(String propertyName,String value,String operator) {
		this.propertyName = propertyName;
		this.value = value;
		this.operator = operator;
	}
	
	@Override
	public int getExpressionType() {
		return Criterion.SIMPLE_EXPRESSION;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getValue() {
		return value;
	}

	public String getOperator() {
		return operator;
	}
}
