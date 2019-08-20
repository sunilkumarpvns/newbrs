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
public class FunctionCeilTest {

	private Compiler compiler;
	@Mock 
	private ValueProvider valueProvider;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
		
	}

	@Test
	public void testGetLongValue_ShouldReturnCeilValue_WhenValidArgumentIsPassed() throws 
			InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("ceil(\"2.3\")");
		long value = expression.getLongValue(valueProvider);
		assertEquals(3, value);
	}
	
	@Test
	public void testGetStringValue_ShouldReturnStringCeilValue_WhenValidArgumentIsPassed() throws 
			InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("ceil(\"2.3\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("3", value);
	}
	
	public Object[][] dataFor_TestGetLongValue_ShouldReturnNumericValue_WhenParameterIsRealNumber(){
		return new Object[][]{
				{"ceil(\"-1.2\")",-1},
				{"ceil(\"0.6\")",1},
				{"ceil(\"0\")",0}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLongValue_ShouldReturnNumericValue_WhenParameterIsRealNumber")
	public void testGetLongValue_ShouldReturnNumericValue_WhenParameterIsRealNumber(
			String function,long expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}
	
	public Object[][] dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterContainsNonNumericValue(){
		return new Object[][]{
				{"ceil(\"abc\")","abc"},
				{"ceil(\"12a\")","12a"}
				
			};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterContainsNonNumericValue")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterContainsNonNumericValue(
			String function,String valueStr) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Configured expression contains a nonnumeric value : "  +valueStr);
		expression.getLongValue(valueProvider);
		
		
	}
		
	public Object[][] dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne(){
		return new Object[][]{
				{"ceil()"},
				{"ceil(\"1.2\",\"2.3\")"}
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne")
	public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne(
			String function) throws InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,ceil function has 1 arguments 1)value ");
		expression.getLongValue(valueProvider);
	}
 
	@Test
	public void testGetLongValue_ShouldReturnTheCeilValue_WhenAnIdentifierIsPassedAsAnArgument() throws InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("ceil(a)");
		Mockito.when(valueProvider.getStringValue("a")).thenReturn("1.2");
		long value = expression.getLongValue(valueProvider);
		assertEquals(2, value);
	}
	
	@Test
	public void testGetStringValue_ShouldReturnTheCeilValue_WhenAnIdentifierIsPassedAsAnArgument() throws InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("ceil(a)");
		Mockito.when(valueProvider.getStringValue("a")).thenReturn("1.2");
		String value = expression.getStringValue(valueProvider);
		assertEquals("2", value);
	}
	
	
	public Object[][] dataFor_TestGetLongValue_ShouldReturnNumericValue_WhenAFunctionCallIsPasssedAsAnArgument(){
		return new Object[][]{
				{"ceil(ceil(\"1.2\"))",2},
				{"ceil(ceil(var))",2}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLongValue_ShouldReturnNumericValue_WhenAFunctionCallIsPasssedAsAnArgument")
	public void testGetLongValue_ShouldReturnNumericValue_WhenAFunctionCallIsPasssedAsAnArgument(
			String function,long expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("var")).thenReturn("1.2");
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}
	@Test
	public void testGetLongValue_ShouldRoundOffTheCeilValueToMaximumLongPossiblevalue_WhenArgumentExceedsBoundaryOfLongType() 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("ceil(max_value)");
		Mockito.when(valueProvider.getStringValue("max_value")).thenReturn("9223372036854775807.9"); //Long.MAX_VALUE=9223372036854775807
		long value = expression.getLongValue(valueProvider);
		assertEquals(Long.MAX_VALUE, value);
	}
	
	
	
}