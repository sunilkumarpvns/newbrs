package com.elitecore.test;

import java.util.List;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public interface ValueProvider {
	String getStringValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	long getLongValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	
	List<String> getStringValues(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
	List<Long> getLongValues(String identifier)throws InvalidTypeCastException,MissingIdentifierException;
}
