package com.elitecore.exprlib.parser.expression.impl;

import java.util.Date;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionGetHour  extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}


	public String getName() {
		return "getHour";
	}

	@SuppressWarnings("deprecation")
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		return new Date().getHours();
	}

	
	@SuppressWarnings("deprecation")
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		
		return  String.valueOf(new Date().getHours());
	}

}
