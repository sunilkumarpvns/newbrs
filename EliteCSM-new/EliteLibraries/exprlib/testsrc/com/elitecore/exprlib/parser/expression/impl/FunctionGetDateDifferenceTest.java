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
public class FunctionGetDateDifferenceTest {
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
@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsLessThanOneOrGreaterThanTwo")
public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsLessThanOneOrGreaterThanTwo(String function) 
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,Datedifference function must has 1 or 2 argument ");
	expression.getLongValue(valueProvider);
}

@Test
@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsLessThanOneOrGreaterThanTwo")
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsLessThanOneOrGreaterThanTwo(String function) 
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Number of parameter mismatch ,Datedifference function must has 1 or 2 argument ");
	expression.getStringValue(valueProvider);
}

public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsLessThanOneOrGreaterThanTwo(){
	return new Object[][]{
			// Input    yyyy/MM/dd        
			{"getDateDifference(\"2015/1/1\",\"2016/1/1\",\"7\")"},
			{"getDateDifference()"}

	};
}

@Test
@Parameters(method = "datafor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterIsNotInProperFormat")
public void testGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterIsNotInProperFormat(String function)
		throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	expression.getLongValue(valueProvider);
}

public Object[][] datafor_TestGetLongValue_ShouldThrowIllegalArgumentException_WhenParameterIsNotInProperFormat(){
	return new Object[][]{
			// Input    yyyy/MM/dd        
			{"getDateDifference(\"2016/4/31\")"},
			{"getDateDifference(\"2016/15/39\",\"1947/2/25\")"},
			{"getDateDifference(\"2015/2/29\")"}

	};
}

@Test
@Parameters(method = "dataFor_TestGetLongValue_ShouldReturnNumberOfDaysBetweenTwoDates_WhenParametersAreProper")
public void testGetLongValue_ShouldReturnNumberOfDaysBetweenTwoDates_WhenParametersAreProper(String function , long expected)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
Expression expression = compiler.parseExpression(function);
long days = expression.getLongValue(valueProvider);
assertEquals(expected, days);

}

public Object[][] dataFor_TestGetLongValue_ShouldReturnNumberOfDaysBetweenTwoDates_WhenParametersAreProper(){
	return new Object[][]{
			// Input    yyyy/MM/dd        
			{"getDateDifference(\"2016/4/30\",\"2016/4/28\")",2},
			{"getDateDifference(\"1570/1/30\",\"2016/4/28\")",162977},
			{"getDateDifference(\"2016/4/30\",\"2016/4/30\")",0}
			
	};
}
@Test
public void testGetLongValueAndGetStringValue_ShouldReturnSameValue_ForSameParameters()
		throws InvalidExpressionException, NumberFormatException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function="getDateDifference(\"2016/4/30\")";
	Expression expression = compiler.parseExpression(function);
	assertEquals(expression.getLongValue(valueProvider),Long.parseLong(expression.getStringValue(valueProvider)));
}

@Test
public void testGetStringValue_ShouldReturnNumberOfDaysBetweenTwoDates_WhenParameterIsIdentifier()
		throws InvalidExpressionException, NumberFormatException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function="getDateDifference(0:25,0:25)";
	Mockito.when(valueProvider.getStringValue("0:25")).thenReturn("2016/4/30");
	Expression expression = compiler.parseExpression(function);
	long days = expression.getLongValue(valueProvider);
	assertEquals(0, days);
	
}
}
