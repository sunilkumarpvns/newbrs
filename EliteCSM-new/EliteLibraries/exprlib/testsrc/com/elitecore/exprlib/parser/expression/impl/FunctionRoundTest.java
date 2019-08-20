 package com.elitecore.exprlib.parser.expression.impl;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
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
public class FunctionRoundTest {
	private Compiler compiler;
	@Mock private ValueProvider valueProvider;

	@Rule 
	public ExpectedException exception= ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		this.compiler=Compiler.getDefaultCompiler();
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function) 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,round function has 1 arguements 1)value ");
		expression.getLongValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,round function has 1 arguements 1)value ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualToOne(){
		return new Object[][]{
				// Input             
				{"round(\"1\",\"1\")"},
				{"round()"}

		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNonNumeric")
	public void testgetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNoNumeric(String function,String parameter)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		
		Mockito.when(valueProvider.getStringValue(parameter)).thenReturn((parameter));
		String valuestr= valueProvider.getStringValue(parameter);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Configured expression contains a nonnumeric value : " + valuestr);
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenParameterIsNonNumeric(){
		return new Object[][]{
				// Input             
				{"round(\"\")",""},						
				{"round(\"        \")","        "},				
				{"round(\"some_string\")","some_string"},			
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldReturnRoundedValue_WhenParameterIsaNumericValue")
	public void testGetLongValue_ShouldReturnRoundedValue_WhenParameterIsaNumericValue(String function, long expected)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression=compiler.parseExpression(function);
		long actual= expression.getLongValue(valueProvider);
		Assert.assertEquals(expected, actual);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldReturnRoundedValue_WhenParameterIsaNumericValue(){
		return new Object[][]{
				// Input     //output        
				{"round(\"9.6\")",10},
				{"round(\"0.2\")",0},
				{"round(\"-100.9\")",-101},
				{"round(\"5\")",5}

		};
	}

	@Test
	public void testGetLongValue_ShouldReturnRoundedValue_WhenParameterIsaFunction()
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		String function="round(round(\"20.5\"))";
		long expected=21;
		Expression expression=compiler.parseExpression(function);
		long actual= expression.getLongValue(valueProvider);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLongValueAndGetStringValue_ShouldReturnSameValueForSameParameter()
			throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		String function="round(\"-50.5\")";
		Expression expression = compiler.parseExpression(function);
		assertEquals(expression.getLongValue(valueProvider),Long.parseLong(expression.getStringValue(valueProvider)));

	}

	@Test
	public void testGetLongValue_ShouldReturnRoundedValue_WhenParameterIsanIdentifier()
			throws InvalidTypeCastException,MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		String function = "round(some_identifier)";
		long expected = 10;
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("some_identifier")).thenReturn("9.8");
		long actual = expression.getLongValue(valueProvider);
		assertEquals(expected,actual);

	}
	
	@Test
	@Parameters(method ="dataFor_TestGetLongValue_ShouldReturnMaximumLongValue_WhenParameterIsPositiveInfinityOrGreaterthanMaximumLongValue")
	public void testGetLongValue_ShouldReturnMaximumLongValue_WhenParameterIsPositiveInfinityOrGreaterthanMaximumLongValue(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		long expected = Long.MAX_VALUE; 
		Expression expression = compiler.parseExpression(function);
		long actual = expression.getLongValue(valueProvider);
		assertEquals(expected, actual);
	}
	
	public String[] dataFor_TestGetLongValue_ShouldReturnMaximumLongValue_WhenParameterIsPositiveInfinityOrGreaterthanMaximumLongValue(){
		return new String[]{
				// Input             
				"round(\"" + Long.MAX_VALUE + ".9\")",
				"round(\"" + Double.POSITIVE_INFINITY + "\")"
	
				};
	}
	
	@Test
	@Parameters(method ="dataFor_TestGetLongValue_ShouldReturnMinimumLongValue_WhenParameterIsNegativeInfinityOrLessthanMinimumLongValue")
	public void testGetLongValue_ShouldReturnMinimumLongValue_WhenParameterIsNegativeInfinityOrLessthanMinimumLongValue(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		long expected = Long.MIN_VALUE; // -9223372036854775808
		Expression expression = compiler.parseExpression(function);
		long actual = expression.getLongValue(valueProvider);
		assertEquals(expected, actual);
	}
	
	public String[] dataFor_TestGetLongValue_ShouldReturnMinimumLongValue_WhenParameterIsNegativeInfinityOrLessthanMinimumLongValue(){
		return new String[]{
				// Input     
				"round(\"" + Long.MIN_VALUE + "\")",
				"round(\"" + Double.NEGATIVE_INFINITY + "\")"
	
				};
	}
	
	@Test
	public void testGetLongValue_ShouldReturnZero_WhenParameterIsNaN()
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String function = "round(\"" + Double.NaN + "\")" ;
		long expected = 0;
		Expression expression = compiler.parseExpression(function);
		long actual = expression.getLongValue(valueProvider);
		assertEquals(expected, actual);
		
	}
}


