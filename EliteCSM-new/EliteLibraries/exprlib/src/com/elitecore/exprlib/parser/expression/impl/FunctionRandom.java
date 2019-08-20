package com.elitecore.exprlib.parser.expression.impl;

import java.util.Random;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionRandom extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static Random random=new Random();

	public int getFunctionType() {
		return 0;
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=1)
			throw new IllegalArgumentException("Number of parameter mismatch ,random function has 1 arguements 1)value ");
		
		int n = (int)argumentList.get(0).getLongValue(valueProvider);
		return random.nextInt(n);
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
 		return String.valueOf(getLongValue(valueProvider));
	}

	
	public String getName() {
		return "random";
	}
}
