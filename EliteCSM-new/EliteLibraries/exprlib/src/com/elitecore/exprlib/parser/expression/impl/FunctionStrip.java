package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionStrip extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	private static String LEADING ="L";
	private static String TRAILING ="T";
	
	public FunctionStrip(){
	}
	
	public int getFunctionType() {	
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=3)
			throw new IllegalArgumentException("Number of parameters mismatch ,STRIP function has 3 arguments 1)actual string    2)option L(Leading) or T(Trailing)    3)character ");
		
		String value = argumentList.get(0).getStringValue(valueProvider);
		String option = argumentList.get(1).getStringValue(valueProvider);
		String character = argumentList.get(2).getStringValue(valueProvider);
		
		if(option.equalsIgnoreCase(LEADING)){
			int index = value.indexOf(character);
			value = index == -1 ? value : value.substring(index+1);
		}else if(option.equalsIgnoreCase(TRAILING)){
			int index = value.lastIndexOf(character);
			value = index == -1 ? value : value.substring(0,index);
		}else{
			throw new IllegalArgumentException("Incorrect parameter Option: "+option+" , Option must be either L(Leading) or T(Trailing)");
		}
		return value;
	}

	public String getName() {
		return "strip";
	}
	
}
