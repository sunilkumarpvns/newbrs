package com.elitecore.exprlib.parser.expression;

import java.io.Serializable;

public interface LogicalExpression extends Expression, Serializable {

	boolean evaluate(ValueProvider valueProvider);
}
