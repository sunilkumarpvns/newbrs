package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionLog10 extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String LOG10 = "log10";
	
	/**
	 * Returns value of logarithm to the base 10 of given argument
	 * <br />
	 * Syntax:
	 * <pre>
	 *	log10(arg1)
	 *Example:
	 *	log10("10") returns 1
	 * </pre>
	 * @return Log to base 10 of given argument
	 * @see Math#log10(double)
	 * @throws IllegalArgumentException if single argument is not provided or
	 * 		given argument is negative or zero
	 */
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		if (argumentList.size() != 1) {
			throw new  IllegalArgumentException(LOG10 + " function requires only one argument, Syntax: loge(<arg>)");
		}
		long arg = argumentList.get(0).getLongValue(valueProvider);
		if (arg <= 0) {
			throw new IllegalArgumentException(LOG10 + " function requires non-zero positive argument");
		}
		return (long) Math.log10(arg);
	}
	
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return LOG10;
	}

}
