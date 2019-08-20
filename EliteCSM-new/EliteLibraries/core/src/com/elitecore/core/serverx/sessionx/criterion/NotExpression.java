package com.elitecore.core.serverx.sessionx.criterion;


public class NotExpression implements Criterion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8387488729117541031L;

	protected NotExpression(Criterion criterion) {
	}

	@Override
	public int getExpressionType() {
		return Criterion.NOT_EXPRESSION;
	}

}
