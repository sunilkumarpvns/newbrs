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
public class FunctionLengthTest {

	@Mock private ValueProvider valueProvider;
	private Compiler compiler;

	@Rule 
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);

		this.compiler= Compiler.getDefaultCompiler();
	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenFunctionInvokedWithoutArgument(){

		return new Object[][]{
				{"strlen()"},
				{"strlen(\"Elite\",\"Core\")"}

		};
	}

	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenFunctionInvokedWithoutArgument")

	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenFunctionInvokedWithoutArgument(
			String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException{

		Expression expression=compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameters mismatch ,Length function must have only 1 argument");
		expression.getStringValue(valueProvider);

	}


	@Test
	public void testGetStringValue_ShouldReturnTheLengthOfGivenString_WhenFunctionHasOneArgument() throws 
	InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression=compiler.parseExpression("strlen(\"Elite\")");
		String strLength= expression.getStringValue(valueProvider);
		assertEquals("5",strLength);
	}


	@Test
	public void testGetLongValue_ShouldReturnTheLengthOfGivenString_WhenFunctionHasOneArgument() throws 
	InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression("strlen(\"Elite\")");
		long strLength = expression.getLongValue(valueProvider);
		assertEquals(5,strLength);

	}


	public Object[][] dataFor_TestGetStringValue_ShouldConsiderBlankStringAsACharacterAndReturnTheLengthOfString() {
		return new Object[][]{
				{"strlen(\"\")","0"},
				{"strlen(\" \")","1"},
				{"strlen(\"  elite\")","7"},
				{"strlen(\"AAA  \")","5"},
		};
	}


	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldConsiderBlankStringAsACharacterAndReturnTheLengthOfString")
	public void testGetStringValue_ShouldConsiderBlankStringAsACharacterAndReturnTheLengthOfString(
			String function, String expected) throws InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException, InvalidExpressionException {

		Expression expression = compiler.parseExpression(function);
		String strlength = expression.getStringValue(valueProvider);
		assertEquals(expected, strlength);


	}


	public Object[][] dataFor_TestGetStringValue_ShouldConsiderEscapeSequencesAsACharacterAndReturnTheLengthOfString() {
		return new Object[][]{
				{"strlen(\"\tCore\")","5"},
				{"strlen(\"\nCore\")","5"}	
		};
	}


	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldConsiderEscapeSequencesAsACharacterAndReturnTheLengthOfString")
	public void testGetStringValue_ShouldConsiderEscapeSequencesAsACharacterAndReturnTheLengthOfString(
			String function, String expected) throws InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException, InvalidExpressionException {

		Expression expression = compiler.parseExpression(function);
		String strlength = expression.getStringValue(valueProvider);
		assertEquals(expected, strlength);


	}


	@Test
	public void testGetStringValue_ShouldReturnTheLengthOfStringProvidedByValueProvider_WhenIdentifierPassedAsAnArgument()
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException {

		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("EliteCore");
		Expression expression = compiler.parseExpression("strlen(0:1)");
		String strLength = expression.getStringValue(valueProvider);
		assertEquals("9", strLength);

	} 


	public Object[][] dataFor_testGetStringValue_ShouldEvaluteTheFunctionPassedAsAnArgument_ThenReturnLengthOfItsResult(){

		return new Object[][]{
				{"strlen(strlen(0:1))",1},
				{"strlen(strlen(\"EliteCore\"))","1"}
		};
	}


	@Test
	@Parameters(method="dataFor_testGetStringValue_ShouldEvaluteTheFunctionPassedAsAnArgument_ThenReturnLengthOfItsResult")
	public void testGetStringValue_ShouldEvaluteTheFunctionPassedAsAnArgument_ThnReturnLengthOfItsResult(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{

		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("EliteCore");
		Expression expression = compiler.parseExpression(function);
		String strLength = expression.getStringValue(valueProvider);
		assertEquals(expected, strLength);

	}

}