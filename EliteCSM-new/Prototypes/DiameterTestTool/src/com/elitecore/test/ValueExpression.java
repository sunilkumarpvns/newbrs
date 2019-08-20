package com.elitecore.test;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractStringFunctionExpression;

public class ValueExpression extends AbstractStringFunctionExpression {


	private final @Nonnull ArrayList<Expression> argumentList;

	public ValueExpression(){
		this.argumentList = new ArrayList<Expression>();
	}

	@Override
	public int getFunctionType() {
		return com.elitecore.exprlib.parser.expression.FunctionExpression.StringFunction;
	}

	@Override
	public void addArgument(Expression expression) {
		argumentList.add(expression);
	}

	@Override
	public String getName() {
		return "value";
	}

	@Override
	public int getExpressionType() {
		return Expression.FunctionExpression;
	}

	@Override
	public Object clone(){
		return new ValueExpression();
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return argumentList.get(0).getStringValue(valueProvider);
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return argumentList.get(0).getLongValue(valueProvider);
	}

	@Override
	public boolean returnsMutipleValue() {
		return false;
	}

	@Override
	public boolean isRegularExpression() {
		return false;
	}

	@Override
	public boolean hasWildCardCharacter() {
		return false;
	}

}
