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
public class FunctionFloorTest {

	@Mock private ValueProvider valueProvider;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Compiler compiler;

	@Before
	public void setup(){

		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();

	}

	public Object[][] dataFor_TestGetLongValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument(){
		return new Object[][]{

				{"floor(\"23.3\")",23},
				{"floor(\"-23.3\")",-24},
				{"floor(\"23\")",23},
				{"floor(\"-23\")",-23}

		};
	}


	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument")
	public void testGetLongValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument(
			String function, long expected) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {

		Expression expression = compiler.parseExpression(function);
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualToOne(){

		return new Object[][] {
				{"floor()"},
				{"floor(\"10\",\"20\")"}

		};
	}


	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualToOne")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualToOne(String function) throws 
	InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {

		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,floor function has 1 arguments 1)value ");
		expression.getLongValue(valueProvider);
	}


	public Object[][] dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenArgumentIsNonNumericValue(){

		return new Object[][]{
				{"floor(\"1 .23\")","1 .23"},
				{"floor(\"Elite Core\")","Elite Core"},
				{"floor(\"AAA123\")","AAA123"},
				{"floor(\"\tElite\tCore\")","\tElite\tCore"}
		};
	}


	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenArgumentIsNonNumericValue")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenArgumentIsNonNumericValue(String function, String parameter) 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {

		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue(parameter)).thenReturn(parameter);
		String argument = valueProvider.getStringValue(parameter);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Configured expression contains a nonnumeric value : " + argument);
		expression.getLongValue(valueProvider);

	}


	@Test
	public void testGetLongValue_ShouldReturnTheFloorOfValueProvidedByValueProvider_WhenArgumentPassedAsAnIdentifier()
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{

		Expression expression = compiler.parseExpression("floor(0:27)");
		Mockito.when(valueProvider.getStringValue("0:27")).thenReturn("23.5");
		long value = expression.getLongValue(valueProvider);
		assertEquals(23, value);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenReturnFloorValueOfItsResult(){

		return new Object[][]{

				{"floor(floor(\"23.5\"))",23},
				{"floor(0:27)",23}
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenReturnFloorValueOfItsResult")
	public void test_GetLongValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenReturnFloorValueOfItsResult(String function,
			long expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("0:27")).thenReturn("23.5");
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}


	public Object[][] dataFor_TestGetLongValue_ShouldReturnMaximumOrMinimumPossibleLongValue_WhenArgumentExccedsBoundaryOfTypeLong(){
		
		return new Object[][]{
				
				{"floor(\"9223372036854775809\")",Long.MAX_VALUE},		//Long.MAX_VALUE=9223372036854775807
				{"floor(\"-9223372036854775809\")",Long.MIN_VALUE},		//Long.MIN_VALUE=-9223372036854775808
		};
	}
	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldReturnMaximumOrMinimumPossibleLongValue_WhenArgumentExccedsBoundaryOfTypeLong")
	public void testGetLongValue_ShouldReturnMaximumOrMinimumPossibleLongValue_WhenArgumentExccedsBoundaryOfTypeLong(
			String function, long expected)	throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}
	

	public Object[][] dataFor_TestGetStringValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument(){

		return new Object[][]{
				{"floor(\"23.3\")","23"},
				{"floor(\"-23.3\")","-24"},
				{"floor(\"23\")","23"},
				{"floor(\"-23\")","-23"}

		};
	}

	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument")
	public void testGetStringValue_ShouldReturnFloorValue_WhenValidStringPassedAsArgument(String function, 
			String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);

	}



}
