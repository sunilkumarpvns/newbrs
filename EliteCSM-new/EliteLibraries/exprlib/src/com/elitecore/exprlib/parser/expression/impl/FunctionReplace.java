package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionReplace extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public FunctionReplace(){
	}	
	
	public int getFunctionType() {	
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=3)
			throw new IllegalArgumentException("Number of parameter mismatch ,REPLACE function has 3 arguments 1)actual string    2)old character   3)new character ");
		
		String value = argumentList.get(0).getStringValue(valueProvider);
		String oldChar = argumentList.get(1).getStringValue(valueProvider);
		String newChar = argumentList.get(2).getStringValue(valueProvider);
		
		value=value.replace(oldChar, newChar);
		
		return value;
	}

	public String getName() {
		return "replace";
	}
	
}
