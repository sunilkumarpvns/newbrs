package com.elitecore.exprlib.parser.expression.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.junit.Before;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionSubStringTest {

	private Compiler compiler;
	
	@Mock
	private ValueProvider valueProvider;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
	}
	
	
	public Object[][] dataFor_TestGetStringValue_ShouldReturnTheSubString_WhenValidArgumentIsPassed(){
		return new Object[][]{
				{"subString(\"Sample\",\"1\")","ample"},
				{"subString(\"Sample String\",\"1\",\"8\")","ample S"},
				{"subString(\"SampleString\",\"1\",\"4\")","amp"},
				{"subString(\"123456\",\"0\",\"3\")","123"},
				{"subString(\"Sample\",\"2\",\"2\")",""}
				
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReturnTheSubString_WhenValidArgumentIsPassed")
	public void testGetStringValue_ShouldReturnTheSubString_WhenValidArgumentIsPassed(
			String function,String expected) throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
	}
	
	public Object[][] dataFor_TestGetLongValue_ShouldThrowInvalidTypeCastException_WhenAnyParameterIsPassed(){
		return new Object[][]{
				{"subString(\"Sample\",\"1\")"},
				{"subString(\"SampleString\",\"1\",\"4\")"}
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetLongValue_ShouldThrowInvalidTypeCastException_WhenAnyParameterIsPassed")
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenAnyParameterIsPassed(
			String function) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("cannot cast a string to integer");
		expression.getLongValue(valueProvider);
	}
	
	
	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotTwoOrThree(){
		return new Object[][]{
				{"subString(\"Sample\")"},
				{"subString(\"SampleString\",\"1\",\"4\",\"extra\")"},
				{"subString()"}
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotTwoOrThree")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotTwoOrThree(
			String function) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Number of parameter mismatch ,SubString function has " +
				"Either 2 or 3 arguements 1)actual string    2)beginIndex   3)endIndex ");
		expression.getStringValue(valueProvider);
		
		
	}
	

	@Test
	public void testGetStringValue_ShouldReturnSubStringOfTheFirstParameter_WhenIdentifierIsPassedAsFirstParameter() 
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression  expression = compiler.parseExpression("subString(string,\"1\",\"5\")");	
		Mockito.when(valueProvider.getStringValue("string")).thenReturn("SampleString");
		String value = expression.getStringValue(valueProvider);
		assertEquals("ampl", value);
	}
	
	@Test
	public void testGetStringValue_ShouldEvaluateTheFirstArgumentThenReturnSubstringOfTheResult_WhenAFunctionCallIsPassedAsFirstArgument() 
			throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("subString(subString(\"Sample\",\"1\"),\"1\",\"4\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("mpl", value);
		
	}
	
	@Test
	public void testGetStringValue_ShouldReturnSubStringOfFirstArgumentCountingAEscapeSequenceAsASingleCharacter() 
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		
		Expression  expression = compiler.parseExpression("subString(string,\"1\",\"8\")");	
		Mockito.when(valueProvider.getStringValue("string")).thenReturn("Sample\tString");
		String value = expression.getStringValue(valueProvider);
		assertEquals("ample\tS", value);
	}
	
	@Test
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenSecondOrThirdParameterIsNonNumeric() throws 
			InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("subString(\"Sample\",\"$\",\"8\")");
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("Configured literal contains a nonnumeric value : $" );
		expression.getStringValue(valueProvider);
	}
	
	
	public Object[][] dataFor_TestGetStringValue_ShouldThrowStringIndexOutOfBoundsException_WhenBeginOrEndIndexValuesAreOutOfBound(){
		return new Object[][]{
				{"subString(\"Sample\",\"1\",\"8\")",8},
				{"subString(\"Sample\",\"-2\")",-2},
				{"subString(\"Sample\",\"4\",\"1\")",-3}
     			
		};
	}
	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowStringIndexOutOfBoundsException_WhenBeginOrEndIndexValuesAreOutOfBound")
	public void testGetStringValue_ShouldThrowStringIndexOutOfBoundsException_WhenBeginOrEndIndexValuesAreOutOfBound(
			String function,long OutOfBound) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(StringIndexOutOfBoundsException.class);
		expectedException.expectMessage("String index out of range: "+OutOfBound);
		expression.getStringValue(valueProvider);
	}
}








