package com.elitecore.exprlib.parser.expression.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.FunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;


public abstract class AbstractFunctionExpression implements FunctionExpression {
	
	private static final long serialVersionUID = 1L;
	protected ArrayList<Expression> argumentList;

	public AbstractFunctionExpression() {
		this.argumentList = new ArrayList<Expression>();
	}

	@Override
	public void addArgument(Expression expression) {
		argumentList.add(expression);
	}
	
	@Override
	public int getExpressionType() {
		return Expression.FunctionExpression;
	}

	public Object clone() {
		try {
			AbstractFunctionExpression abstractFunctionExpression=(AbstractFunctionExpression)super.clone();
			abstractFunctionExpression.argumentList=new ArrayList<Expression>();
			return abstractFunctionExpression;
		} catch (CloneNotSupportedException e) {
			return null;
		}
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

	@Override
	public int getFunctionType() {
		return FunctionExpression;
	}

	@Override
	public List<String> getStringValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException{
		List<String> lstStringValues = new ArrayList<String>(2);
		String value = getStringValue(valueProvider);
		if(value == null){
			throw new MissingIdentifierException("Can not Find the Value for Expression" +this.getName());
		}
		lstStringValues.add(value);
		return lstStringValues;
	}

	@Override
	public List<Long> getLongValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException{
		List<Long> lstLongValues = new ArrayList<Long>(2);
		long value = getLongValue(valueProvider);
		lstLongValues.add(value);
		return lstLongValues;
	}

}
