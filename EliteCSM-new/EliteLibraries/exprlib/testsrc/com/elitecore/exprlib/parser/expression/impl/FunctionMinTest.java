package com.elitecore.exprlib.parser.expression.impl;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
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
public class FunctionMinTest {
	 	private Compiler compiler;
@Mock	private ValueProvider valueProvider;

@Rule
public ExpectedException exception = ExpectedException.none();

@Before
public void setUp(){
	MockitoAnnotations.initMocks(this);
	this.compiler = Compiler.getDefaultCompiler();
}

@Test
public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNoParameterIsPassed()
		throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
	Expression expression = compiler.parseExpression("min()");
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,MIN function must has atlest 1 argument ");
	expression.getLongValue(valueProvider);
}

@Test
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNoParameterIsPassed()
		throws InvalidExpressionException, InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
	Expression expression = compiler.parseExpression("min()");
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,MIN function must has atlest 1 argument ");
	expression.getStringValue(valueProvider);
}

@Test  
@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid")
public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid(String function) 
		throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
	Expression expression = compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	expression.getLongValue(valueProvider);
}

@Test  
@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid")
public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid(String function) 
		throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
	Expression expression = compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	expression.getStringValue(valueProvider);
}

public Object[] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowInvalidTypeCastException_WhenParameterIsInvalid(){
	return new Object[][] {
			{ "min(\"          1\",\"10          \")" },
			{ "min(\"\",\"\")" }, 
			{ "min(\"abc\",\"xyz\")" },
			{ "min(\"1.0\",\"1.5\")" },
	};
}

@Test
public void testGetLongValueAndGetStringValue_ShouldReturnSameValueForSameParameters()
		throws InvalidExpressionException,InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
	String function="min(\"10\",\"3\",\"5\",\"-1\",\"0\")";
	Expression expression = compiler.parseExpression(function);
	assertEquals(expression.getLongValue(valueProvider),Long.parseLong(expression.getStringValue(valueProvider)));

}

@Test
@Parameters(method = "dataFor_TestGetLongValue_ShouldReturnMinimumValueAmongTheSetOfInputParameters_WhenParameterIsOfTypeLong")
public void testGetLongValue_ShouldReturnMinimumValueAmongTheSetOfInputParameters_WhenParameterIsOfTypeLong(String function, long expected)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	long actual = expression.getLongValue(valueProvider);
	Assert.assertEquals(expected, actual);
}

public Object[] dataFor_TestGetLongValue_ShouldReturnMinimumValueAmongTheSetOfInputParameters_WhenParameterIsOfTypeLong(){
	return new Object[][] {
			{ "min(\"10\",\"3\",\"5\",\"-1\",\"0\")" , -1},
			{ "min(\"10\")", 10 }, 
			{ "min(\"-10\",\"-3\",\"-5\",\"-1\",\"-90\")" , -90},
			{ "min(\"10\",\"20\",\"50\",\"100\",\"60\")", 10 },
			{ "min(\"" + Long.MIN_VALUE + "\",\"-1\",\"5\")" , Long.MIN_VALUE}
	};
}

@Test
public void testGetLongValue_ShouldReturnMinimumValueAmongTheSetOfInputParameters_WhenParameterIsIdentifier()
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	Mockito.when(valueProvider.getLongValue("some_identifier")).thenReturn((9l));
	Mockito.when(valueProvider.getLongValue("0:27")).thenReturn(600l);
	String function="min(some_identifier,0:27)";
	Expression expression = compiler.parseExpression(function);
	long actual = expression.getLongValue(valueProvider);
	assertEquals(9,actual);
}

@Test
public void testGetLongValue_ShouldReturnMinimumValueAmongTheSetOfInputParameters_WhenParameterIsFunction()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "min(min(\"-10\",\"-20\",\"-90\"),\"0\",\"1000\",min(\"10\",\"20\",\"50\"))" ;
	Expression expression = compiler.parseExpression(function);
	long actual = expression.getLongValue(valueProvider);
	assertEquals(-90, actual);
}

}