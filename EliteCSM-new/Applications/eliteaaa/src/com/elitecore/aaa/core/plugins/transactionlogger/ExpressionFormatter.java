package com.elitecore.aaa.core.plugins.transactionlogger;

import com.elitecore.commons.base.Strings;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class ExpressionFormatter implements Formatter {

	private String value;
	private Expression expression;
	
	public ExpressionFormatter(String value) {
		this.value = value;
	}

	@Override
	public void init() throws InvalidExpressionException {
		expression = Compiler.getDefaultCompiler().parseExpression(value);
	}
	
	@Override
	public String process(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		return Strings.join(";", expression.getStringValues(valueProvider));
	}
	
}