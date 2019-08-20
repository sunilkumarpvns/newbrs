package com.elitecore.exprlib.parser.expression;

import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.symbol.impl.IdentifierSymbol;

public interface IdentifierExpression extends Expression {

	/**
	 * @param valueProvider
	 * @ The Identifier returns multiple String values
	 */
	List<String> getStringValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
	
	
	/**
	 * @param valueProvider
	 * @ The Identifier returns multiple String values
	 */
	List<Long> getLongValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
	
	
	/**
	 * @return Identifier Symbol
	 */
	IdentifierSymbol getIdentifierSymbol();
	
}
