package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionConcat extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	public FunctionConcat(){
	}
	
	public int getFunctionType() {	
		return 0;
	}
	
	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException{
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()==0)
			throw new IllegalArgumentException("Number of parameters mismatch ,Concat function must have atlest 1 argument ");
		
		StringBuilder strConcat=new StringBuilder();		
		for(Expression next:argumentList){
			strConcat=strConcat.append(next.getStringValue(valueProvider));
		}		
		return strConcat.toString();
	}

	public String getName() {
		return "concat";
	}
	
}
