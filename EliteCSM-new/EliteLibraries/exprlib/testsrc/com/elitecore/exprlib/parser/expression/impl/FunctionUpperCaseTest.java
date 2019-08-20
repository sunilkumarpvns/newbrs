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
public class FunctionUpperCaseTest {

	private Compiler compiler;
	
	@Mock private ValueProvider valueProvider;
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
	}
	
	
	@Test
	public void testGetStringValue_MustChangeGivenArgumentToUppercase_WhenParameterIsValid() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("toUpperCase(\"abc\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("ABC", value);
	}
	
	@Test
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenAnyArgumentIsPassed() throws IllegalArgumentException, 
			MissingIdentifierException, InvalidExpressionException, InvalidTypeCastException {
		Expression expression = compiler.parseExpression("toUpperCase(\"abc\")");
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("cannot cast a string to integer");
		expression.getLongValue(valueProvider);
	}
 
	
	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne(){
		return new Object[][]{
				{"toUpperCase(\"one\",\"two\")"},
				{"toUpperCase()"}
		};
	}
	
	
	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne")
    public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotOne(String function) throws 
    InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException
    {
    	Expression expression = compiler.parseExpression(function);
    	exception.expect(IllegalArgumentException.class);
    	exception.expectMessage("Number of parameter mismatch ,toUpperCase funtion has 1 arguments 1)value ");
    	expression.getStringValue(valueProvider);
   
    }
	
	
	public static Object[][] dataFor_TestGetStringValue_ShuouldChangeTheAlphabetsToUpperCase_WhenParameterIsAlphaNumericOrHasSpecialCharacters()
	{
		return new Object[][]{
				{"toUpperCase(\"abc123\")","ABC123"},
				{"toUpperCase(\"123\")","123"},
				{"toUpperCase(\"-123\")","-123"},
				{"toUpperCase(\"!@#$%\")","!@#$%"},
				{"toUpperCase(\"abcXYZ\")","ABCXYZ"},
				{"toUpperCase(\"abc@123\")","ABC@123"}
				
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShuouldChangeTheAlphabetsToUpperCase_WhenParameterIsAlphaNumericOrHasSpecialCharacters")
	public void testGetStringValue_ShuouldChangeTheAlphabetsToUpperCase_WhenParameterIsAlphaNumeric(String function, String expected) 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected,value);
	}

    public static Object[][] dataFor_TestGetStringValue_ShouldChangeTheAlphabetsToUpperCase_WhenParameterContainsBlankSpaces() {
    	
    	return new Object[][]{
    			{"toUpperCase(\"ab  c\")","AB  C"},
    			{"toUpperCase(\"	after a tab\")","	AFTER A TAB"}
    	};
    }
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldChangeTheAlphabetsToUpperCase_WhenParameterContainsBlankSpaces")
    public void testGetStringValue_ShouldChangeTheAlphabetsToUpperCase_WhenParameterContainsBlankSpaces(
    		String function,String expected) throws InvalidExpressionException, InvalidTypeCastException, 
    		IllegalArgumentException, MissingIdentifierException{
    	Expression expression = compiler.parseExpression(function);
    	String value = expression.getStringValue(valueProvider);
    	assertEquals(expected,value);
    
    }
	
	
	public static Object[][] dataFor_TestGetStringValue_ShouldChangeTheStringToUpperCase_WhenParameterContainsEscapeSequences() {
		return new Object[][]{
				{"toUpperCase(\"a\tb\t\")","A	B	"},
				{"toUpperCase(\"a\nb\")","A\nB"}
		};
	}

	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldChangeTheStringToUpperCase_WhenParameterContainsEscapeSequences")
	public void testGetStringValue_ShouldChangeheStringToUpperCase_WhenParameterContainsEscapeSequences(
			String function,String expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
	}


	
	@Test
	public void testGetStringValue_ShouldReturnTheParametersAsItIs_WhenParameterIsAnEmptyString(
			 ) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Expression expression= compiler.parseExpression("toUpperCase(\"\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("", value);
	}
	
	
	@Test
	public void testGetStringValue_ShouldChangeTheValueOfTheGivenParametersToUpperString_WhenParameterIsIdentifier() 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("toUpperCase(0:1)");
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("username");
		String value = expression.getStringValue(valueProvider);
		assertEquals("USERNAME", value);
	}
	
	public static Object[][] dataFor_TestGetStringValue_ShouldEvaluateTheGivenFunctionAsArgument_AndShouldChangeItToUpperCase_WhenAnotherFunctionCallIsPassedAsAnArgument(){

		return new Object[][]{
				{"toUpperCase(toUpperCase(\"abc\"))","ABC"},
				{"toUpperCase(toUpperCase(0:1))","USERNAME"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldEvaluateTheGivenFunctionAsArgument_AndShouldChangeItToUpperCase_WhenAnotherFunctionCallIsPassedAsAnArgument")
	public void testGetStringValue_ShouldEvaluateTheGivenFunctionAsArgument_AndShouldChangeItToUpperCase_WhenAnotherFunctionCallIsPassedAsAnArgument(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		Expression expression= compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("username");
		String value=expression.getStringValue(valueProvider);
		assertEquals(expected, value);
	}
	
	
}
