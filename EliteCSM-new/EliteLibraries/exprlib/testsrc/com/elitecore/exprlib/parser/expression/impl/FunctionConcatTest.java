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
public class FunctionConcatTest {

	@Mock ValueProvider valueProvider;
	private Compiler compiler;

	@Rule 
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
	}

	@Test
	public void testGetStringValue_shouldThrowIllegalArgumentException_WhenFunctionInvokedWithoutArguments() throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("concat()");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameters mismatch ,Concat function must have atlest 1 argument ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetStringValue_FunctionMustConcatGivenArguments_WhenFunctionHaveOneOrMoreStringArguments(){ 
		return new Object[][]{
				{"concat(\"elite\")", "elite"},
				{"concat(\"elite\",\"aaa\")", "eliteaaa"},
				{"concat(\"CSM\",\"ELITE\",\"AAA\")", "CSMELITEAAA"},
				{"concat(\"Elite\",\"aaa\",\"one\")", "Eliteaaaone"},
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetStringValue_FunctionMustConcatGivenArguments_WhenFunctionHaveOneOrMoreStringArguments")
	public void testGetStringValue_FunctionMustConcatGivenArguments_WhenFunctionHaveOneOrMoreStringArguments(String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expresion = compiler.parseExpression(function);
		String stringValue = expresion.getStringValue(valueProvider);
		assertEquals(expected, stringValue);
	}

	public Object[][] dataFor_TestGetStringValue_FunctionMustNotTrimArgumentValue_AndConcatAsItIs_WhenArgumentHasBlankSpacesOrEscapeSequences(){
		return new Object[][]{
				{"concat(\"\",\"\")",""},
				{"concat(\"\",\" \")"," "},
				{"concat(\" \",\" \")","  "},
				{"concat(\" \",\"\")"," "},
				{"concat(\"  elite\",\"aaa  \")", "  eliteaaa  "},
				{"concat(\"\tCore\",\"\tSession\")","	Core	Session"},
				{"concat(\"\nCore\",\"\nSession\")","\nCore\nSession"},	
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetStringValue_FunctionMustNotTrimArgumentValue_AndConcatAsItIs_WhenArgumentHasBlankSpacesOrEscapeSequences")
	public void testGetStringValue_FunctionMustNotTrimArgumentValue_AndConcatAsItIs_WhenArgumentHasBlankSpacesOrEscapeSequences(String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals(expected, stringValue);
	}
	public Object[][] dataFor_testGetStringValue_MustEvaluateGivenFunctionAsArgument_AndMustConcatItsResult_WithOtherGivenArguments(){
		return new Object[][]{
				{"concat(concat(\"Elite\",\"core\"),\"AAA\")","ElitecoreAAA"},
				{"concat(concat(\"Elite\",0:25),\"AAA\")","ElitecoreAAA"},
				{"concat(concat(0:1,0:25),\"AAA\")","ElitecoreAAA"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_testGetStringValue_MustEvaluateGivenFunctionAsArgument_AndMustConcatItsResult_WithOtherGivenArguments")
	public void testGetStringValue_MustEvaluateGivenFunctionAsArgument_AndMustConcatItsResult_WithOtherGivenArguments(String function,String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("Elite");
		Mockito.when(valueProvider.getStringValue("0:25")).thenReturn("core");
		Expression expression = compiler.parseExpression(function);
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals(expected, stringValue);
	}
	
	public Object[][] dataFor_testGetStringValue_FunctionShouldConcatValueOfIdentifierProvidedByValueProvider_WithGivenString_WhenIdentifierAndStringPassedAsArgument(){
		return new Object[][]{
				{"concat(\"Elite\",0:25)","EliteAAA"},
				{"concat(0:1,0:25)","EliteAAA"}
		};
	}
	@Test
	@Parameters(method="dataFor_testGetStringValue_FunctionShouldConcatValueOfIdentifierProvidedByValueProvider_WithGivenString_WhenIdentifierAndStringPassedAsArgument")
	public void testGetStringValue_FunctionShouldConcatValueOfIdentifierProvidedByValueProvider_WithGivenString_WhenIdentifierAndStringPassedAsArgument(String function,String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("Elite");
		Mockito.when(valueProvider.getStringValue("0:25")).thenReturn("AAA");
		Expression expression=compiler.parseExpression(function);
		String strValue=expression.getStringValue(valueProvider);
		assertEquals(expected, strValue);
	}
	
	public Object[][] dataFor_TestGetLongValue_shouldThrowInvalidTypeCastException_WhenFunctionCalledWithOrWithoutArguments(){
		return new Object[][]{
				{"concat()"},
				{"concat(\"elite\",\"aaa\")"}						
		};
	}
	@Test
	@Parameters(method="dataFor_TestGetLongValue_shouldThrowInvalidTypeCastException_WhenFunctionCalledWithOrWithoutArguments")
	public void testGetLongValue_shouldThrowInvalidTypeCastException_WhenFunctionCalledWithOrWithoutArguments( String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("cannot cast a string to integer");
		expression.getLongValue(valueProvider);
	}
}