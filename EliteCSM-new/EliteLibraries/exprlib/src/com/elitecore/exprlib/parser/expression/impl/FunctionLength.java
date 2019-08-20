package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionLength extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;

	@Override
	public int getFunctionType() {
		return 0;
	}

	@Override
	public String getName() {
		return "strlen";
	}

	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=1)
			throw new IllegalArgumentException("Number of parameters mismatch ,Length function must have only 1 argument");
		
		String str = argumentList.get(0).getStringValue(valueProvider);
		return Long.toString(str.length());
	}

	@Override
	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {		
		return Long.valueOf(getStringValue(valueProvider));
	}

}
