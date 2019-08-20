package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionCeil extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=1)
			throw new IllegalArgumentException("Number of parameter mismatch ,ceil function has 1 arguments 1)value ");
		String valuestr=argumentList.get(0).getStringValue(valueProvider);
		try{
			return (long) Math.ceil(Double.parseDouble(valuestr));
		}catch(NumberFormatException e){
			throw new IllegalArgumentException("Configured expression contains a nonnumeric value : " + valuestr);
		}
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
 		return String.valueOf(getLongValue(valueProvider));
	}

	
	public String getName() {
		return "ceil";
	}

}
