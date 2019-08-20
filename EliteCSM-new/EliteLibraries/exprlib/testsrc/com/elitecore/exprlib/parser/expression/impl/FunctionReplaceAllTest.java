package com.elitecore.exprlib.parser.expression.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.assertEquals;
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
public class FunctionReplaceAllTest {
    	private Compiler compiler;
@Mock	private ValueProvider valueProvider;

@Rule
public ExpectedException exception = ExpectedException.none();

@Before
public  void setUp(){

	MockitoAnnotations.initMocks(this);
	this.compiler = Compiler.getDefaultCompiler();
}

@Test
@Parameters(method = "dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotThree")
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotThree(String function) 
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
	Expression expression = compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Improper number of arguement in function ,REPLACEFIRST function has 3 arguments 1)actual string    2)regx   3)replacement ");
	expression.getStringValue(valueProvider);
}

public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfParametersIsNotThree(){
	return new Object[][]{
			// Input             
			{"replaceAll(\"\\d\",\"digit_here\")"},
			{"replaceAll()"},
			{"replaceAll(\"Hello\")"},
			{"replaceAll(\"Hello\",\"h\",\"f\",\"a\")"}

	};
}
@Test
public void testGetLongValue_WillThrowInvalidTypeCastException_WheneverItIsCalled()
		throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
	String function = "replaceAll(\"[0-9]\",\"0\")";
	Expression expression = compiler.parseExpression(function);
	exception.expect(InvalidTypeCastException.class);
	exception.expectMessage("cannot cast a string to integer");
	expression.getLongValue(valueProvider);
	
}


@Test
@Parameters(method="dataFor_TestGetStringValue_ShouldReturnTheValueReplacedByTheReplacementString_WhenSubStringOfTheValueMatchesWithTheGivenRegex")
public void testGetStringValue_ShouldReturnTheValueReplacedByTheReplacementString_WhenSubStringOfTheValueMatchesWithTheGivenRegex(String function,String expected)
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals(expected, actual);
	
}
	
public Object[][] dataFor_TestGetStringValue_ShouldReturnTheValueReplacedByTheReplacementString_WhenSubStringOfTheValueMatchesWithTheGivenRegex(){
	return new Object[][]{
			// Input (value,regex,replacement)//output            
			{"replaceAll(\"Hello\",\"ll\",\"r\")","Hero"},
			{"replaceAll(\"user1@elitecore.com\",\".+\",\"raduser@elitecore.com\")","raduser@elitecore.com"},
			{"replaceAll(\"   Elite   core\",\"(\\\\w)(\\\\s+)\",\"$1\")","   Elitecore"} // removes the spaces comes after a word

	};
}	

@Test
public void testGetStringValue_ShouldReturnTheValueReplacedByTheReplacementString_WhenSubStringOfTheValueMatchesWithTheGivenRegex_PassingIdentifiersAsParameter()
		throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
	String function="replaceAll(0:1,semicolon,hyphen)";
	Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("user1@elitecore.com;100;10A");
	Mockito.when(valueProvider.getStringValue("semicolon")).thenReturn(";");
	Mockito.when(valueProvider.getStringValue("hyphen")).thenReturn("-");
	
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("user1@elitecore.com-100-10A", actual);
}

@Test
public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenSyntaxErrorIsFoundInRegex()
		throws InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
	String function = "replaceAll(\"He\",\"?i)h\",\"R\")";
	Expression expression =  compiler.parseExpression(function);
	exception.expect(IllegalArgumentException.class);
	expression.getStringValue(valueProvider);
}

@Test
public void testGetStringValue_ShouldReturnTheValueReplacedByTheReplacementString_WhenParameterIsAFunction()
		throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
	String function = "replaceAll(\"user@elitecore.com\",\".com\",replaceAll(\"org\",\".+\",\".org\"))";
	Expression expression = compiler.parseExpression(function);
	String actual = expression.getStringValue(valueProvider);
	assertEquals("user@elitecore.org", actual);
}
	
	
}


