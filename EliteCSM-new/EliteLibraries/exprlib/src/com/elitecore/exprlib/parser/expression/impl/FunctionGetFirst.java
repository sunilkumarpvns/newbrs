package com.elitecore.exprlib.parser.expression.impl;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * A function that accepts a single argument, evaluates the argument gets the first value from  
 * the result.
 * 
 * <p>
 * Example:
 * <br/>
 * <code>getFirst(0:25)</code> will fetch the first class attribute out of multiple class attributes in packet. 
 * 
 * @author khushbu.chauhan
 *
 */
public class FunctionGetFirst extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String GET_FIRST = "getfirst";
	
	@Override
	public String getName() {
		return GET_FIRST;
	}

	/**
	 * @return the first value from the result of evaluated argument.
	 * @exception IllegalArgumentException if number of arguments is not one.
	 * @exception MissingIdentifierException if no result is found.
	 */
	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws IllegalArgumentException, InvalidTypeCastException, MissingIdentifierException {

		if (argumentList.size() != 1) {
			throw new IllegalArgumentException("Number of agruments must be one");
		}
		
			List<String> result = argumentList.get(0).getStringValues(valueProvider);
			if (Collectionz.isNullOrEmpty(result) == false) {
				return result.get(0);
			}

		throw new MissingIdentifierException("Can not find value for expression: " + argumentList.get(0).getName());
	}
}
