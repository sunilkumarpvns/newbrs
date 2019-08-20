package com.elitecore.exprlib.parser.expression;

public interface FloatingPointFunctionExpression extends FunctionExpression {
	double evaluate(ValueProvider valueProvider);
}
