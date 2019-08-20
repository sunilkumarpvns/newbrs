package com.elitecore.exprlib.parser;

import java.util.List;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.FunctionExpression;
import com.elitecore.exprlib.scanner.Symbol;

public interface Parser {
	public Expression parse(List<Symbol> symbol)throws InvalidExpressionException;
	void addFunction(String functionName,FunctionExpression functionObject);
}
