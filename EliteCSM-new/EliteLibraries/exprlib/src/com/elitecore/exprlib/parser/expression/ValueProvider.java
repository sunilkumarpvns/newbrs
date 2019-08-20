package com.elitecore.exprlib.parser.expression;

import java.util.List;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public interface ValueProvider {
	String getStringValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	long getLongValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	
	List<String> getStringValues(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	List<Long> getLongValues(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	/**
	 * @return the value mapped for the key. Returns null if key is not associated with any value or key is unknown.
	 */
	Object getValue(String key);
}
