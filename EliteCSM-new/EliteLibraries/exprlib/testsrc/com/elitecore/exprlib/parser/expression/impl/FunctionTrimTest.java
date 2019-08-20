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
public class FunctionTrimTest {

	private Compiler compiler;
	@Mock private ValueProvider valueProvider;

	@Rule
	public ExpectedException exception= ExpectedException.none();

	@Before
	public void setup(){
		
		MockitoAnnotations.initMocks(this);
		this.compiler=Compiler.getDefaultCompiler();
	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenNumberOfArgumentIsNotEqualToOne(){
		return new Object[][]{
				
				{"trim()"},
				{"trim(\"Elite \",\" patel\")"}
		};
	}
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenNumberOfArgumentIsNotEqualToOne")
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenNumberOfArgumentIsNotEqualToOne(
			String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("Improper number of arguement in funtion ,TRIM funtion has 1 arguments 1)value ");
		expression.getStringValue(valueProvider);
	}

	public Object[][] dataFor_TestGetLongValue_ShouldThrowInvalidTypeCastException_WhenFunctionHasAnyArgument(){
		return new Object[][]{
				
				{"trim(\" Elitecore \")"},
				{"trim(\"elite\",\"aaa\")"}						
		};
	}
	@Test
	@Parameters(method = "dataFor_TestGetLongValue_ShouldThrowInvalidTypeCastException_WhenFunctionHasAnyArgument")
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenFunctionHasAnyArgument( 
			String function) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("cannot cast a string to integer");
		expression.getLongValue(valueProvider);
	}

	public Object[][] dataFor_TestGetStringValue_MustTrimTheGivenArgument_WhenArgumentHasBlankSpaceOrEscapeSequence(){
		return new Object[][]{
				
				{"trim(\"\")",""},
				{"trim(\" \")",""},
				{"trim(\"  elite\")","elite"},
				{"trim(\"AAA  \")","AAA"},
				{"trim(\"\tCore\")","Core"},
				{"trim(\"\nCore\")","Core"}
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetStringValue_MustTrimTheGivenArgument_WhenArgumentHasBlankSpaceOrEscapeSequence")
	public void testGetStringValue_MustTrimTheGivenArgument_WhenArgumentHasBlankSpaceOrEscapeSequence(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals(expected, stringValue);
	}
	
	@Test
	public void testGetStringValue_ShouldNotTrimAnySpacesExceptLeadingOrTrailingSpaces () throws InvalidExpressionException, 
	InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression("trim(\"  Elite AAA  \")");
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals("Elite AAA", stringValue);
	}
	@Test
	public void testGetStringValue_ShouldTrimTheStringWhichIsProvidedByValueProvider_WhenIdentifierPassedAsArgument()
			throws 	InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException {
		
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn(" CSM ");
		Expression expression = compiler.parseExpression("trim(0:1)");
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals("CSM",stringValue);
	}

	public Object[][] dataFor_TestGetStringValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenTrimItsResult(){
		return new Object[][]{
				
				{"trim(trim(\" CSM  \"))","CSM"},
				{"trim(trim(0:1))","CSM"}
		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenTrimItsResult")
	public void testGetStringValue_ShouldEvaluateTheFunctionPassedAsArgument_ThenTrimItsResult(
			String function, String expected) throws InvalidTypeCastException, IllegalArgumentException, 
			InvalidExpressionException, MissingIdentifierException {
		
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("  CSM  ");
		String stringValue = expression.getStringValue(valueProvider);
		assertEquals(expected, stringValue);
	}




}
