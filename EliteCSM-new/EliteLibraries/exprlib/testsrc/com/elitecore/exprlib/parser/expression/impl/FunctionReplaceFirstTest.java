package com.elitecore.exprlib.parser.expression.impl;

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

import static org.junit.Assert.*;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionReplaceFirstTest {

	private Compiler compiler;
	
	@Mock
	private ValueProvider valueProvider;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void prep(){
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
	}
	
	@Test
	public void testGetLongValue_ShouldThrowInvalidTypeCastException() throws 
			InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("replaceFirst(\"Sample\",\"[am]\",\"trap\")");
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("cannot cast a string to integer");
		expression.getLongValue(valueProvider);
	}
	
	
	public Object[][] datafor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotThree(){
		return new Object[][]{
				{"replaceFirst(\"Sample\",\"[am]+\")"},
				{"replaceFirst(\"Sample\",\"[am]+\",\"trap\",\"extra\")"}
		};
	}

	@Test
	@Parameters(method = "datafor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotThree")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotThree(
			String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Number of parameter mismatch ,REPLACEFIRST function has 3 arguments 1)actual string    2)regx   3)replacement ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_testGetStringValue_ShouldReplaceFirstOccurenceOfStringMatchingGivenRegexWithTheReplacementString_WhenValidArgumentsArePassed(){
		return new Object[][]{
				{"replaceFirst(\"sample stamp\",\"[amp]+\",\"REPLACED\")","sREPLACEDle stamp"},
				{"replaceFirst(\"10 on 10\",\"\\\\d+\",\"@@\")","@@ on 10"}
				
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_testGetStringValue_ShouldReplaceFirstOccurenceOfStringMatchingGivenRegexWithTheReplacementString_WhenValidArgumentsArePassed")
	public void testGetStringValue_ShouldReplaceFirstOccurenceOfStringMatchingGivenRegexWithTheReplacementString_WhenValidArgumentsArePassed(
			String function, String expected) throws InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
	}
	
	
	@Test
	public void testGetStringValue_ShouldNotMakeAnyReplacemntIfAMatchingSubstringIsNotFound(
			) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException{
		Expression expression = compiler.parseExpression("replaceFirst(\"Sample String\",\"\\\\d{4}\",\"replacement\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("Sample String", value);
	}
	
	
	@Test
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenRegexIsInvalid(
			) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("replaceFirst(\"Sample String\",\"[a}\",\"REP\")");
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Error in Function ReplaceAll ,Regular expression's : [a} syntax is invalid Reason : Unclosed character class near index 2");
										
		String value = expression.getStringValue(valueProvider);
		};
		
	@Test
	public void testGetStringValue_ShouldReplaceFirstOccurenceOfStringMatchingGivenRegexWithReplacementString_WhenAnIdentifierIsPassedAsArgument(
			) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("replaceFirst(original,\"\\\\w+\\\\s\",\"REP\")");
		Mockito.when(valueProvider.getStringValue("original")).thenReturn("passing identifier");
		String value = expression.getStringValue(valueProvider);
		assertEquals("REPidentifier", value);
	}
	

	public Object[][] dataFor_TestGetStringValue_ShouldEvaluateFunctionCallAsArgumentAndReplaceFirstMatchOfRegexWithReplacementString_WhenFunctionCallIsPassedAsArgument(){
			return new Object[][]{
					{"replaceFirst(replaceFirst(\"user@elitecore\",\"\\\\w+@\",\"new User at \"),\"\\\\w+\\\\s\",\"Replaced \")",
						"Replaced User at elitecore"},
					{"replaceFirst(replaceFirst(original,\"[a-z\\\\-]{6}\",replacementOne),\"\\\\-\\\\w+\",replacementTwo)",
							"outer-result"}
			};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldEvaluateFunctionCallAsArgumentAndReplaceFirstMatchOfRegexWithReplacementString_WhenFunctionCallIsPassedAsArgument")
	public void testGetStringValue_ShouldEvaluateFunctionCallAsArgumentAndReplaceFirstMatchOfRegexWithReplacementString_WhenFunctionCallIsPassedAsArgument(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		
		Mockito.when(valueProvider.getStringValue("replacementOne")).thenReturn("outer-");
		Mockito.when(valueProvider.getStringValue("replacementTwo")).thenReturn("-result");
		Mockito.when(valueProvider.getStringValue("original")).thenReturn("inner-string");
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
		
	}
}
