package com.elitecore.exprlib.parser.expression.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.StringFunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class AbstractStringFunctionExpression extends AbstractFunctionExpression implements StringFunctionExpression {

	private static final long serialVersionUID = 1L;

	public List<Long> getLongValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		List<Long> longValues = new ArrayList<Long>();
		List<String> strValues = getStringValues(valueProvider);
		for(String strValue : strValues){
			try{
				longValues.add(Long.parseLong(strValue));
			}catch(NumberFormatException ex){
				throw new InvalidTypeCastException(ex);
			}
		}
		return longValues;
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		Long value ;
		String strValue = getStringValue(valueProvider);
		if(strValue == null){
			throw new MissingIdentifierException("Can not Find Value for Expression " + this.getName());
		}
		try{
			value = Long.parseLong(strValue);
		}catch(NumberFormatException ex){
			throw new InvalidTypeCastException(ex);
		}
		return value;
	}

}
