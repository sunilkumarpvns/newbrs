package com.elitecore.exprlib.parser.expression.impl;

import java.util.regex.PatternSyntaxException;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionReplaceAll extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public FunctionReplaceAll(){
	}

	public int getFunctionType() {	
		return 0;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=3)
			throw new IllegalArgumentException("Improper number of arguement in function ,REPLACEFIRST function has 3 arguments 1)actual string    2)regx   3)replacement ");
		
		String value = argumentList.get(0).getStringValue(valueProvider);
		String regex = argumentList.get(1).getStringValue(valueProvider);
		String replacement = argumentList.get(2).getStringValue(valueProvider);
		
		try{
			value=value.replaceAll(regex, replacement);
		}catch(PatternSyntaxException e){
			throw new IllegalArgumentException("Error in Function ReplaceAll ,Regular expression's : "+regex+" syntax is invalid Reason : "+e.getMessage());
		}
		return value;
	}

	public String getName() {
		return "replaceAll";
	}
	
}
