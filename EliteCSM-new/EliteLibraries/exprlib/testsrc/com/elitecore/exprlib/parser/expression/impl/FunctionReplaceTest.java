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
public class FunctionReplaceTest {


	private Compiler compiler;

	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private ValueProvider valueProvider;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();

	}

	@Test
	public void testGetLongValue_ShouldThroughInvalidTypeCastException_WhenFunctionHasAnyArgument() throws InvalidExpressionException,
			InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression("replace(\"Elitecore\",\"core\",\"AAA\")");
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("cannot cast a string to integer");
		long value = expression.getLongValue(valueProvider);

	}

	@Test
	public void testGetStringValue_ShouldReplaceOldCharWithNewChar_IfFunctionHasValidArgument() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression("replace(\"EliteCore\",\"Core\",\"AAA\")");
		String value = expression.getStringValue(valueProvider);
		assertEquals("EliteAAA", value);

	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualsToThree(){

		return new Object[][]{
				{"replace()"},
				{"replace(\"EliteCore\")"},
				{"replace(\"EliteCore\",\"Core\",\"AAA\",\"CSM\")"}
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualsToThree")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentIsNotEqualsToThree(String function) throws InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,REPLACE function has 3 arguments 1)actual string    2)old character   3)new character ");
		String value = expression.getStringValue(valueProvider);

	}


	@Test
	public void testGetStringValue_ShouldReplaceOldCharWithNewCharInString_WhichIsProvidedByValueProvider_WhenIdentifierPassedAsAnArgument()
			throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{

		Expression expression = compiler.parseExpression("replace(0:1,\"Core\",\"Csm\")");
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("EliteCore");
		String value = expression.getStringValue(valueProvider);
		assertEquals("EliteCsm", value);

	}


	public Object[][] dataFor_TestGetStringValue_ShouldEvaluateFunctionPassedAsAnArgument_ThenReplaceOldCharWithNewCharInString(){

		return new Object[][]{
				{"replace(replace(\"EliteAAA\",\"AAA\",\"Csm\"),\"Csm\",\"Core\")","EliteCore"},
				{"replace(replace(0:1,\"AAA\",\"Csm\"),\"Csm\",\"Core\")","EliteCore"}
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldEvaluateFunctionPassedAsAnArgument_ThenReplaceOldCharWithNewCharInString")
	public void testGetStringValue_ShouldEvaluateFunctionPassedAsAnArgument_ThenReplaceOldCharWithNewCharInString(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("EliteAAA");
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected,value);

	}

	public Object[][] dataFor_TestGetStringValue_ShouldReplaceOldCharWithNewChar_WhenArgumentHasBlankSpacesOrEscapeSequences(){

		return new Object[][]{
				{"replace(\"Elite	Core\",\"\t\",\"AAA\")","EliteAAACore"},
				{"replace(\"Elite\nCore\",\"\n\",\"AAA\")","EliteAAACore"},
				{"replace(\"Elite Core\",\" \",\"-\")","Elite-Core"}
		};
	}
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReplaceOldCharWithNewChar_WhenArgumentHasBlankSpacesOrEscapeSequences")
	public void testGetStringValue_ShouldReplaceOldCharWithNewChar_WhenArgumentHasBlankSpacesOrEscapeSequences(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
	}
}
