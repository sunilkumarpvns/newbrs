package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionModulo extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String MOD = "mod";
	
	/**
	 * Returns remainder of the divison of two numbers, 
	 * also known as Mod value.
	 * <br />
	 * Syntax:
	 * <pre>
	 *	mod(arg1, arg2)
	 *Example:
	 *	mod("10", "7") returns 3
	 *	mod( "0", "7") returns 0
	 * </pre>
	 * @return mod value of given arguments.
	 * @throws IllegalArgumentException if two arguments are not provided or
	 * 		second argument is zero  
	 */
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		if (argumentList.size() != 2) {
			throw new  IllegalArgumentException(MOD + " function requires two arguments, Syntax: mod(<arg1>,<arg2>)");
		}
		long mod = argumentList.get(0).getLongValue(valueProvider);
		if (mod == 0) {
			return 0;
		}
		long quotient = argumentList.get(1).getLongValue(valueProvider); 
		if (quotient == 0) {
			throw new IllegalArgumentException("can not " + MOD + " by zero");
		} 
		return mod % quotient;
	}
	
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return MOD;
	}

}
