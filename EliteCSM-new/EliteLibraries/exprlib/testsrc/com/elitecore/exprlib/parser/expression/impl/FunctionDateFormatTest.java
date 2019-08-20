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
public class FunctionDateFormatTest {
		private Compiler compiler;
@Mock	private ValueProvider valueProvider;

@Rule
public ExpectedException exception = ExpectedException.none();

@Before
public void setUp(){
	compiler = Compiler.getDefaultCompiler();
	MockitoAnnotations.initMocks(this);
}

@Test
public void testGetLongValue_WillThrowInvalidTypeCastException_WheneverItIsCalled()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "dateFormat(\"2016 31 January\",\"y d MMMM\")";
	Expression expression = compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	exception.expectMessage("cannot cast a string to integer");
	expression.getLongValue(valueProvider);
	
}
@Test
@Parameters(method ="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualsToTwo")
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualsToTwo(String function)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,Date Format function must have 2 argument ");
	expression.getStringValue(valueProvider);
}

public Object [][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotEqualsToTwo(){
	return new Object[][]{
			{"dateFormat()"},
			{"dateFormat(\"1916-JANUARY-7\",\"y-MMM-d\",\"IST\")"},
			{"dateFormat(\"1916-JANUARY-7\")"}
	};
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenForamtParameterDoesNotMatchWithDateParameter")
public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenForamtParameterDoesNotMatchWithDateParameter(String function)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	expression.getStringValue(valueProvider);
}

public Object[][] dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenForamtParameterDoesNotMatchWithDateParameter(){
	return new Object[][]{
			{"dateFormat(\"1920-JAN-7\",\"y-M-d\")"},
			{"dateFormat(\"1920-JAN-7\",\"d-M-yy\")"},
			{"dateFormat(\"1920-JAN-7\",\"yyyy/MMM/dd\")"},
			{"dateFormat(\"31-January-2016\",\"y-MMM-d\")"}
	};
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldReturnTheFormattedDate_WhenParametersAreProper")
public void testGetStringValue_ShouldReturnTheFormattedDate_WhenParametersAreProper(String function,String expected)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	String actual =  expression.getStringValue(valueProvider);
	assertEquals(expected, actual);
	
}
public Object [][]dataFor_TestGetStringValue_ShouldReturnTheFormattedDate_WhenParametersAreProper(){
	return new Object[][]{
			{"dateFormat(\"1916-JANUARY-7\",\"y-MMM-d\")","7/1/1916"},
			{"dateFormat(\"31-January-2016\",\"d-MMMM-y\")","31/1/2016"}, 
			{"dateFormat(\"2016 31 January\",\"y d MMMM\")","31/1/2016"} 
};
}

@Test
public void testGetStringValue_ShouldReturnTheFormattedDate_WhenParametersAreIdentifiers()
		throws InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
	String function = "dateFormat(0:25,date_format)";
	Expression expression = compiler.parseExpression(function);

	Mockito.when(valueProvider.getStringValue("0:25")).thenReturn("1916-JANUARY-7");
	Mockito.when(valueProvider.getStringValue("date_format")).thenReturn("y-MMM-d");
	
	String actual =  expression.getStringValue(valueProvider);
	assertEquals("7/1/1916", actual);
	
}
}
