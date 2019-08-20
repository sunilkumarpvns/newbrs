package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionLoge extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String LOGE = "loge";
	
	/**
	 * Returns value of the natural logarithm (base e) of given argument
	 * <br />
	 * Syntax:
	 * <pre>
	 *	loge(arg1)
	 *Example:
	 *	loge("10") returns 2
	 * </pre>
	 * @return Natural Log value of given argument
	 * @see Math#log(double)
	 * @throws IllegalArgumentException if single argument is not provided or
	 * 		given argument is negative or zero
	 */
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		if (argumentList.size() != 1) {
			throw new  IllegalArgumentException(LOGE + " function requires only one argument, Syntax: loge(<arg>)");
		}
		long arg = argumentList.get(0).getLongValue(valueProvider);
		if (arg <= 0) {
			throw new IllegalArgumentException(LOGE + " function requires non-zero positive argument");
		}
		return (long) Math.log(arg);
	}
	
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return LOGE;
	}

}
