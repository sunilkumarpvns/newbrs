package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionSubString extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	public FunctionSubString(){
	}	
	
	public int getFunctionType() {	
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()<2 || argumentList.size()>3)
			throw new IllegalArgumentException("Number of parameter mismatch ,SubString function has Either 2 or 3 arguements 1)actual string    2)beginIndex   3)endIndex ");
		
		String value = argumentList.get(0).getStringValue(valueProvider);
		int beginIndex = (int)argumentList.get(1).getLongValue(valueProvider);
		if(argumentList.size()==2){
			value = value.substring(beginIndex);
		}else{
			int endIndex = (int)argumentList.get(2).getLongValue(valueProvider);
			value = value.substring(beginIndex, endIndex);
		}
		return value;
	}

	public String getName() {
		return "subString";
	}
	
}
