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
public class FunctionLowerCaseTest {
		private Compiler compiler;
@Mock 	private ValueProvider valueProvider;

@Rule 	
public ExpectedException exception = ExpectedException.none();
	
@Before
public void setUp(){
	MockitoAnnotations.initMocks(this);
	compiler = Compiler.getDefaultCompiler();
}

@Test
public void testGetLongValue_ShouldThrowInvalidTypeCastException_WheneverItIsCalled()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "toLowerCase(\"ELITECORE\")";
	Expression expression =  compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	exception.expectMessage("cannot cast a string to integer");
	expression.getLongValue(valueProvider);
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoOne")
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoOne(String function)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,toLowerCase function has 1 arguments 1)value ");
	expression.getStringValue(valueProvider);
}

public String[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualtoOne(){
	return new String[][]{
			{"toLowerCase(\"ELITECORE\",\"lowercase\",)"},
			{"toLowerCase()"}
	};
}

@Test 
public void testGetStringValue_ShouldThrowMissingIdentifierException_WhenParameterIsIdentifierAndTheSameIsNotConfigured()
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	String function = "toLowerCase(0:1)";
	Expression expression = compiler.parseExpression(function);
	Mockito.when(valueProvider.getStringValue("0:1")).thenReturn(null);

	exception.expect(MissingIdentifierException.class);
	exception.expectMessage("Configured identifier not found: " + "0:1" );
	expression.getStringValue(valueProvider);
}

@Test 
public void testGetStringValue_ShouldReturnTheStringConvertedAllItsCharactersToLowerCase_WhenParameterIsIdentifier()
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	String function = "toLowerCase(0:1)";
	Expression expression = compiler.parseExpression(function);
	Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("ELITECORE");
	String actual = expression.getStringValue(valueProvider);
	assertEquals("elitecore", actual);
}

@Test 
@Parameters(method = "dataFor_TestGetStringValue_ShouldReturnTheStringConvertedAllItsCharactersToLowerCase_WhenParameterIsSomeStringLiteral")
public void testGetStringValue_ShouldReturnTheStringConvertedAllItsCharactersToLowerCaseUsingTheRulesOfDefaultLocale_WhenParameterIsSomeStringLiteral(String function, String expected)
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals(expected, actual);
}

public Object[][] dataFor_TestGetStringValue_ShouldReturnTheStringConvertedAllItsCharactersToLowerCase_WhenParameterIsSomeStringLiteral(){
	return new Object[][]{
			{"toLowerCase(\"ELITECORE\")", "elitecore"}, // all uppercase charaacters
			{"toLowerCase(\"elitecore\")", "elitecore"}, // all lowercase characters
			{"toLowerCase(\"Elite cORE\")", "elite core"}, //  uppercase + lowercase characters
			{"toLowerCase(\"Elite\tcORE\")", "elite	core"}, // with escape sequence
			{"toLowerCase(\"0123456789\")", "0123456789"}, //all digits
			{"toLowerCase(\"₹€\")","₹€"},//symbols
			{"toLowerCase(\"Elite\ncORE123\")", "elite\ncore123"},
			{"toLowerCase(\"\")", ""}
	};
}

@Test
public void testGetStringValue_ShouldReturnTheStringConvertedAllItsCharactersToLowerCase_WhenParameterIsFunctionWhichReturnsSomeValidParameter()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "toLowerCase(toLowerCase(\"ELITECORE\"))";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("elitecore", actual);
}
}