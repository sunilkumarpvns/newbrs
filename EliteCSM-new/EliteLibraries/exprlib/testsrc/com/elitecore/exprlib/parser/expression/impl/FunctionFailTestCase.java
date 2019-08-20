package com.elitecore.exprlib.parser.expression.impl;

import org.junit.Assert;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * <p>
 * An expression library adapter for {@link Assert#fail()} method, that allows you to fail test case from expression library.
 * <p>
 * Usage:
 * <pre>
 * Checking short-circuit behavior of expression library
 * <code>
 * 	 0:1 = "not-satisfied" AND fail("Expression library AND operator is of short-circuit type");
 * <code>
 * </pre>
 * 
 * In the above expression if first expression is not satisfied then other part of expression should not be evaluated. In case
 * if it is evaluated then the {@link FunctionFailTestCase} will throw {@link AssertionError} with given message.
 *  
 * @author narendra.pathai
 *
 */
public class FunctionFailTestCase extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "fail";
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		if (argumentList.size() > 0) {
			Assert.fail(String.valueOf(argumentList.get(0)));
		} else {
			Assert.fail();
		}
		return null;
	}
}
