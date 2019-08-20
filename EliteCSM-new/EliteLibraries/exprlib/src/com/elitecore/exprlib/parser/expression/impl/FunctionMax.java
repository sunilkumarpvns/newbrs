package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionMax extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()==0)
			throw new IllegalArgumentException("Number of parameter mismatch ,MAX function must has atlest 1 argument ");
		
		long maxValue = argumentList.get(0).getLongValue(valueProvider);
		for(int index=1;index<argumentList.size();index++){
			long nextValue=argumentList.get(index).getLongValue(valueProvider);
			if(nextValue>maxValue)
				maxValue=nextValue;
		}
		return maxValue;
	}


	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
 		return String.valueOf(getLongValue(valueProvider));
	}

	
	public String getName() {
		return "max";
	}

}