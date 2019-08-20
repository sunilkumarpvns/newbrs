package com.elitecore.exprlib.parser.expression.impl;

import java.util.Date;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionGetWeekDay extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}

	public String getName() {
		return "getWeekDay";
	}

	@SuppressWarnings("deprecation")
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		return new Date().getDay();
	}

	
	@SuppressWarnings("deprecation")
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		
		return  String.valueOf(new Date().getDay());
	}

}
