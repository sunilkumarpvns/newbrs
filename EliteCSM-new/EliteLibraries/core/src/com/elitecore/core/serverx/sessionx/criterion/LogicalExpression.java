package com.elitecore.core.serverx.sessionx.criterion;


public class LogicalExpression implements Criterion {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7508207323490828239L;
	
	private final Criterion lhs;
	private final Criterion rhs;
	private final String op;

	protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	@Override
	public int getExpressionType() {
		return Criterion.LOGICAL_EXPRESSION;
	}

	public Criterion getLhs() {
		return lhs;
	}

	public Criterion getRhs() {
		return rhs;
	}

	public String getOp() {
		return op;
	}

}
