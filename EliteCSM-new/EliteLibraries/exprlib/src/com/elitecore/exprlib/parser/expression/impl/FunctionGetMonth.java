package com.elitecore.exprlib.parser.expression.impl;

import java.util.Date;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionGetMonth extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}

	public String getName() {
		return "getMonth";
	}

	@SuppressWarnings("deprecation")
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		return new Date().getMonth()+1;
	}

	
	@SuppressWarnings("deprecation")
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		
		return  String.valueOf(new Date().getMonth()+1);
	}

}
