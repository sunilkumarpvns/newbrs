package com.elitecore.exprlib.parser.expression;

import java.util.regex.Pattern;

public interface LiteralExpression extends Expression {
	public Pattern getPattern();
}
