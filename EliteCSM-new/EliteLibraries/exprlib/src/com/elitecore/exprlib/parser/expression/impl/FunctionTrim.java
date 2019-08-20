package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionTrim extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public FunctionTrim(){
	}
	
	public int getFunctionType() {	
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		if(argumentList.size()!=1)
			throw new InvalidTypeCastException("Improper number of arguement in funtion ,TRIM funtion has 1 arguments 1)value ");
		
		String result=null;
		result=argumentList.get(0).getStringValue(valueProvider).trim();
		return result;
	}

	public String getName() {
		return "trim";
	}
	
}
