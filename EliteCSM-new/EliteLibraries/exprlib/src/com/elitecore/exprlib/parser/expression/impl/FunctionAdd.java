package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionAdd extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		if(argumentList.size()==0)
			throw new IllegalArgumentException("Number of parameter mismatch ,add function must has atlest 1 argument ");

		long sum=0;		
		for(Expression expression : argumentList){
			sum+=expression.getLongValue(valueProvider);
		}
		return sum;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException ,MissingIdentifierException{
		return String.valueOf(getLongValue(valueProvider));
	}

	
	public String getName() {
		return "add";
	}

}
