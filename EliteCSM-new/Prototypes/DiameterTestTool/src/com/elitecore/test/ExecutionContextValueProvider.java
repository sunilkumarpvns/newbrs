package com.elitecore.test;

import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.base.Optional;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class ExecutionContextValueProvider implements ValueProvider {
	
	private ExecutionContext executionContext;
	

	public ExecutionContextValueProvider(ExecutionContext executionContext) {
		super();
		this.executionContext = executionContext;
	}

	@Override
	public String getStringValue(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		Optional<Object> value = executionContext.get(identifier);
		
		if(value.isPresent()){
			return value.toString();
		}
		
		throw new MissingIdentifierException(identifier + " not found from execution context");
	}

	@Override
	public long getLongValue(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		String strVal = getStringValue(identifier);
		
		try{
			return Long.parseLong(strVal);
		}catch(Exception ex){
			throw new InvalidTypeCastException(strVal + " is not numeric value");
		}
	}

	@Override
	public List<String> getStringValues(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		return Arrays.asList(getStringValue(identifier));
	}

	@Override
	public List<Long> getLongValues(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		return Arrays.asList(getLongValue(identifier));
	}

}
