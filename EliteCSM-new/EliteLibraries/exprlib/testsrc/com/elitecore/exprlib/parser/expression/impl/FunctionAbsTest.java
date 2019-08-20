package com.elitecore.exprlib.parser.expression.impl;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionAbsTest {
	private Compiler compiler;
	@Mock private ValueProvider valueProvider;

	@Rule public ExpectedException exception= ExpectedException.none();

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		this.compiler=Compiler.getDefaultCompiler();

	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,abs function has 1 arguments 1)value ");
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,abs function has 1 arguments 1)value ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(){
		return new Object[][]{
				// Input             
				{"abs(\"1\",\"1\")"},
				{"abs()"}
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsNotOfTypeLong")
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenParameterIsNotOfTypeLong(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsNotOfTypeLong")
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsNotOfTypeLong(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsNotOfTypeLong(){
		return new Object[][]{
				// Input             
				{"abs(\"\")"},						//passing empty String
				{"abs(\"        \")"},				//passing blank String
				{"abs(\"some_string\")"},			//passing string
				{"abs(\"10.0\")"}					//passing decimal
		};
	}

	@Test
	@Parameters(method="dataFor_TestgetLongValue_ShouldReturnAbsoluteValue_WhenParameterIsOfTypeLong")
	public void testGetLongValue_ShouldReturnAbsoluteValue_WhenParameterIsOfTypeLong(String function, long expected)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression=compiler.parseExpression(function);
		long actual= expression.getLongValue(valueProvider);
		assertEquals(expected, actual);
	}

	public Object[][] dataFor_TestgetLongValue_ShouldReturnAbsoluteValue_WhenParameterIsOfTypeLong() throws InvalidExpressionException{
		return new Object[][]{
				// Input             
				{"abs(\"9\")",9},
				{"abs(\"0\")",0},
				{"abs(\"-100\")",100}
		};
	}

	@Test
	public void testGetLongValue_ShouldReturnAbsoluteValue_WhenParameterIsaFunction() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		String function="abs(abs(abs(\"-20\")))";
		long expected=20;
		Expression expression=compiler.parseExpression(function);
		long longValue= expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	@Test
	public void testGetLongValue_ShouldReturnAbsoluteValue_WhenParameterIsanIdentifier()
			throws InvalidTypeCastException,MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		String function = "abs(some_identifier)";
		long expected = 10;
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getLongValue("some_identifier")).thenReturn(-10l);
		long actual = expression.getLongValue(valueProvider);
		assertEquals(expected,actual);

	}
	
	@Test
	public void testGetLongValue_ShouldReturnAbsoluteValue_WhenParameterWithMaximumLongValueIsConfigured() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		String function = "abs(\"" + Long.MAX_VALUE + "\")";
		long expected = Long.MAX_VALUE ;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	@Test
	public void testGetLongValue_ShouldReturnSameNegativeLongValue_WhenParameterWithMinimumLongValueIsConfigured()
			 throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{    	
		String function = "abs(\"" + Long.MIN_VALUE + "\")";
		long expected = Long.MIN_VALUE ;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	@Test
	public void testGetLongValueAndGetStringValue_ShouldReturnSameValueForSameParameter() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		String function="abs(\"-50\")";
		Expression expression = compiler.parseExpression(function);
		assertEquals(expression.getLongValue(valueProvider),Long.parseLong(expression.getStringValue(valueProvider)));

	}
	
}


