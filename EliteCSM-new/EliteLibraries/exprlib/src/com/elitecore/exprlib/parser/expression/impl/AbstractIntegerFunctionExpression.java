package com.elitecore.exprlib.parser.expression.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.IntegerFunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class AbstractIntegerFunctionExpression extends AbstractFunctionExpression implements
		IntegerFunctionExpression {

	private static final long serialVersionUID = 1L;

	@Override
	public int getFunctionType() {
		return IntegerFunction;
	}


	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return String.valueOf(getLongValue(valueProvider));
	}
	
	public List<String> getStringValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException{
		List<String> lstStringValues = new ArrayList<String>();
		List<Long> longValues = getLongValues(valueProvider);
		for(Long longValue :  longValues){
			lstStringValues.add(String.valueOf(longValue));
		}
		return lstStringValues;
	}
	

}
