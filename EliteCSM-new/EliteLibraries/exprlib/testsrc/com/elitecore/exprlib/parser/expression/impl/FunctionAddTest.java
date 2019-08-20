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
public class FunctionAddTest {

	private Compiler compiler;
	@Mock private ValueProvider valueProvider;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();

	}

	@Test
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNoParameterIsPassed()
			throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("add()");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,add function must has atlest 1 argument ");
		expression.getLongValue(valueProvider);

	}

	@Test
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNoParameterIsPassed()
			throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("add()");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,add function must has atlest 1 argument ");
		expression.getStringValue(valueProvider);

	}


	@Test  
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid")
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid(String function)
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getLongValue(valueProvider);
	}


	@Test  
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid")
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid(String function) 
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		expression.getStringValue(valueProvider);
	}

	public Object[] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid()
			throws InvalidExpressionException {
		return new Object[][] {
				{ "add(\"          1\",\"1          \")" },
				{ "add(\"\",\"\")" }, 
				{ "add(\"abc\",\"xyz\")" },
				{ "add(\"1.0\",\"1.0\")" },
		};
	}

	@Test 
	@Parameters(method = "dataFor_TestGetLongValue_ShouldPerformAddition_WhenParameterIsNonNegative")
	public void testGetLongValue_ShouldPerformAddition_WhenParameterIsNonNegative(String function,long expected)
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldPerformAddition_WhenParameterIsNonNegative(){
		return new Object[][] {
				// Input // Output
				{ "add(\"1\"\"1\"\"1\"\\n \"1\",\"1\")", 5 },
				{ "add(\"0\",\"90\")", 90 },
				{ "add(\"09\",\"2147483645\")", 2147483654l },
				{ "add(\"9\")", 9 },
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValue_ShouldPerformAddition_WhenParameterIsNegative")
	public void testGetLongValue_ShouldPerformAddition_WhenParameterIsNegative(String function,long expected)
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldPerformAddition_WhenParameterIsNegative(){
		return new Object[][] {
				// Input // Output
				{ "add(\"-8\",\"5\")", -3 }, 
				{ "add(\"-5\",\"-5\")", -10 },
				{ "add(\"8\",\"-5\")", 3 },
		};
	}

	@Test
	public void testGetLongValue_ShouldOverFlow_WhenParameterWithMaximumLongValueIsConfigured()
			throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		String function = "add(\"" + Long.MAX_VALUE + "\",\"1\")";
		long expected=Long.MIN_VALUE;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	@Test
	public void testGetLongValue_ShouldUnderFlow_WhenParameterWithMinimumLongValueIsConfigured()
			throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		String function = "add(\"" + Long.MIN_VALUE + "\",\"-1\")";
		long expected=Long.MAX_VALUE;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}


	@Test
	public void testGetLongValue_ShouldPerformAddition_WhenParameterIsFunction()
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		String function= "add(\"1\",add(\"1\",\"1\"))";
		long expected= 3;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected, longValue);
	}

	@Test
	public void testGetLongValueAndGetStringValue_ShouldReturnSameValueForSameParameters()
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		String function="add(\"1\",\"1\",\"1\",\"1\",\"1\")";
		Expression expression = compiler.parseExpression(function);
		assertEquals(expression.getLongValue(valueProvider),Long.parseLong(expression.getStringValue(valueProvider)));

	}

	@Test
	public void testGetLongValue_ShouldPerformAddition_WhenParameterIsIdentifier()
			throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		//Mockito.when(valueProvider.getLongValue("some_identifier")).thenReturn((9l));
		Mockito.when(valueProvider.getLongValue("0:27")).thenReturn(600l);
		String function="add(\"9\",,0:27)";
		long expected=609;
		Expression expression = compiler.parseExpression(function);
		long longValue = expression.getLongValue(valueProvider);
		assertEquals(expected,longValue);
	

	}



}
