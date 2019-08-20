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
public class FunctionStripTest {
	
	  private Compiler compiler;
@Mock private ValueProvider valueProvider;
	
@Rule
public ExpectedException exception = ExpectedException.none();

@Before
public void setUp(){
	MockitoAnnotations.initMocks(this);
	this.compiler = Compiler.getDefaultCompiler();
}

@Test
public void testGetLongValue_ShouldThrowInvalidTypeCastException_WheneverItIsCalled()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "strip(\"Elitecore\",\"L\",\"c\")";
	Expression expression =  compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	exception.expectMessage("cannot cast a string to integer");
	expression.getLongValue(valueProvider);
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoThree")
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoThree(String function)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameters mismatch ,STRIP function has 3 arguments 1)actual string    2)option L(Leading) or T(Trailing)    3)character ");
	expression.getStringValue(valueProvider);
}

public String[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoThree(){
	return new String[][]{
			{"strip(\"Elitecore\",\"L\",)"},
			{"strip(\"Elitecore\",\"L\",\"c\",\"\r\")"},
			{"strip(\"Elitecore\")"},
			{"strip()"}
	};
}

@Test
public void testGetStringValue_ShouldReturnSubstringAfterTheFirstOccuranceOfTheParameterCharacter_WhenParameterOptionIsL()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "strip(\"Eliteuser\",\"L\",\"e\")";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("user", actual);
}

@Test
public void testGetStringValue_ShouldReturnSubstringBeforeTheLastOccuranceOfTheParameterCharacter_WhenParameterOptionIsT()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "strip(\"Eliteuser\",\"T\",\"e\")";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("Eliteus", actual);
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldReturnSubstringAsPerTheBehaviourOfTheOptionParameterLOrTByMatchingFirstCharacterOfTheParameterCharacter_WhenStringIsPassedAsCharacterParameter")
public void testGetStringValue_ShouldReturnSubstringAsPerTheBehaviourOfTheOptionParameterLOrTByMatchingFirstCharacterOfTheParameterCharacter_WhenStringIsPassedAsCharacterParameter(String function, String expected)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals(expected, actual);
}

public String[][] dataFor_TestGetStringValue_ShouldReturnSubstringAsPerTheBehaviourOfTheOptionParameterLOrTByMatchingFirstCharacterOfTheParameterCharacter_WhenStringIsPassedAsCharacterParameter(){
	return new String[][]{
			{"strip(\"Eliteuser\",\"L\",\"te\")","euser"},
			{"strip(\"Eliteuser\",\"T\",\"se\")","Eliteu"}
	};
}

@Test
@Parameters(method = "dataFor_TestGestStringValue_ShouldThrowIllegalArgumentException_WhenParameterOptionIsOtherThanLOrT")
public void testGestStringValue_ShouldThrowIllegalArgumentException_WhenParameterOptionIsOtherThanLOrT(String function,String paramoption)
		throws InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
	Expression expression = compiler.parseExpression(function);
	
	Mockito.when(valueProvider.getStringValue(paramoption)).thenReturn(paramoption);
	String option = valueProvider.getStringValue(paramoption);
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Incorrect parameter Option: "+option+" , Option must be either L(Leading) or T(Trailing)");
	expression.getStringValue(valueProvider);
}

public String[][] dataFor_TestGestStringValue_ShouldThrowIllegalArgumentException_WhenParameterOptionIsOtherThanLOrT(){
	return new String[][]{
			{"strip(\"Eliteuser\",\"Trailing\",\"e\")","Trailing"},
			{"strip(\"Eliteuser\",\"Leading\",\"e\")","Leading"}
	};
}

@Test 
public void testGestStringValue_ShouldReturnSubstringAsPerTheBehaviourOfTheOptionParameterLOrT_WhenParameterIsIdentifier()
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	String function = "strip(0:1,\"T\",\"@\")";
	Expression expression = compiler.parseExpression(function);
	Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("user@elitecore.com");
	String actual = expression.getStringValue(valueProvider);
	assertEquals("user", actual);
}

@Test
public void testGetStringValue_ShouldReturnTheSameString_WhenTheValueOfParameterCharacterIsNotFoundInParameterValue() throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "strip(\"Eliteuser\",\"T\",\"w\")";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("Eliteuser", actual);
	}

@Test
public void testGetStringValue_ShouldReturnSubstringAsPerTheBehaviourOfTheOptionParameterLOrT_WhenParameterIsFunction()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "strip(strip(\"Eliteuser\",\"T\",\"w\"),\"L\",\"e\")";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("user", actual);
	
}

}