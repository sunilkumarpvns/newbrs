package com.elitecore.exprlib.parser.expression.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionRandomTest {

	private Compiler compiler;
	@Mock	private ValueProvider valueProvider;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function) 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,random function has 1 arguements 1)value ");
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,random function has 1 arguements 1)value ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(){
		return new Object[][]{
				// Input             
				{"random(\"1\",\"1\")"},
				{"random()"}

		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNegativeIntegerOrZeroOrOutOfTheRangeOfPositiveInteger")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterIsNegativeIntegerOrZeroOrOutOfTheRangeOfPositiveInteger(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		exception.expect(java.lang.IllegalArgumentException.class);
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNegativeIntegerOrZeroOrOutOfTheRangeOfPositiveInteger")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNegativeIntegerOrZeroOrOutOfTheRangeOfPositiveInteger(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		exception.expect(java.lang.IllegalArgumentException.class);
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNegativeIntegerOrZeroOrOutOfTheRangeOfPositiveInteger(){
		return new Object[][]{
				{"random(\"-9\")"},
				{"random(\"2147483648\")"},//long value
				{"random(\"0\")"}
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsAStringOrDecimal")
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenParameterIsAStringOrDecimal(String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsAStringOrDecimal")
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsAStringOrDecimal(String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsAStringOrDecimal(){
		return new Object[][]{
				{"random(\"2.5\")"},
				{"random(\"abc\")"},
		};
	}
}
