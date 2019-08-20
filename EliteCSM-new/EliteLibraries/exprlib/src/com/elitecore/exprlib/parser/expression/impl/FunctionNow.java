package com.elitecore.exprlib.parser.expression.impl;

import java.util.Calendar;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionNow extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public FunctionNow(){
	}	
	
	public int getFunctionType() {	
		return 0;
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		
		Calendar calendar=Calendar.getInstance();
		String returnDate="";			
		returnDate=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));

		return returnDate;
	}

	public String getName() {
		return "now";
	}

	
}
