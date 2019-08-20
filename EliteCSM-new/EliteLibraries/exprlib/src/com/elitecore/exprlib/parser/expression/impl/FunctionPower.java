package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionPower extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String POW = "pow";
	
	/**
	 * Returns value of first argument raised to the power of the second argument. 
	 * <br />
	 * Syntax:
	 * <pre>
	 *	pow(arg1, arg2)
	 *Example:
	 *	pow("2", "10") returns 1024
	 *	pow("2",  "0") returns 1
	 *	pow("0",  "7") returns 0
	 * </pre>
	 * @return mod value of given arguments.
	 * @see Math#pow(double, double)
	 * @throws IllegalArgumentException if two arguments are not provided
	 */
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		if (argumentList.size() != 2) {
			throw new  IllegalArgumentException(POW + " function requires two arguments, Syntax: pow(<arg1>,<arg2>)");
		}
		long operand = argumentList.get(0).getLongValue(valueProvider);
		if (operand == 0) {
			return 0;
		}
		long power = argumentList.get(1).getLongValue(valueProvider); 
		return (long) Math.pow(operand, power);
	}
	
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return POW;
	}

}
