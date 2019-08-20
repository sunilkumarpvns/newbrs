package com.elitecore.exprlib.parser.expression.impl;

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

import static org.junit.Assert.*;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionMaxTest {

	private Compiler compiler;
	
	@Mock
	private ValueProvider valueProvider;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
	}
	
	public Object[][] dataFor_TestGetLongValueAndTestGetStringValue_ShouldReturnMaximumValue_WhenASetOfValidArgumentsArePassed(){
		return new Object[][]{
				{"max(\"2\")",2},
				{"max(\"7\",\"-23\")",7},
				{"max(\"9\",\"-1\",\"0\")",9},
				{"max(\"-23\",\"-43\",\"-5\")",-5}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldReturnMaximumValue_WhenASetOfValidArgumentsArePassed")
	public void testGetLongValue_ShouldReturnMaximumValue_WhenASetOfValidArgumentsArePassed(
			String function, long expected) throws InvalidExpressionException, 
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		long value = expression.getLongValue(valueProvider);
		assertEquals(value,expected);
	}
	
	
	@Test
	@Parameters(method = "dataFor_TestGetLongValueAndTestGetStringValue_ShouldReturnMaximumValue_WhenASetOfValidArgumentsArePassed")
	public void testGetStringValue_ShouldReturnMaxiumStringValue_WhenASetOfValidArgumentsArePassed(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(value,expected);
	}
	
	public Object[][] dataFor_TestGetLong_ShouldReturnMaximumValue_WhenIdentifiersArePassedInArgument(){
		return new Object[][]{
				{"max(0:31,0:30,\"90\")","245"},
				{"max(0:31,var,0:30)","1000"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLong_ShouldReturnMaximumValue_WhenIdentifiersArePassedInArgument")
	public void testGetLong_ShouldReturnMaximumValue_WhenIdentifiersArePassedInArgument(
			String function, long expected) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getLongValue("0:30")).thenReturn(245l);
		Mockito.when(valueProvider.getLongValue("0:31")).thenReturn(123l);
		Mockito.when(valueProvider.getLongValue("var")).thenReturn(1000l);
		long value = expression.getLongValue(valueProvider);
		assertEquals(value,expected);
	}
	
	public Object[][] dataFor_TestGetLong_ShouldReturnTheMaximumValue_WhenFunctionCallIsPassedAsArgument(){
		
		return new Object[][]{
				{"max(max(\"123\",\"34\"),\"23\",\"0\")",123},
				{"max(max(\"-25\",0:31),\"2\")",2}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestGetLong_ShouldReturnTheMaximumValue_WhenFunctionCallIsPassedAsArgument")
	public void testGetLong_ShouldReturnTheMaximumValue_WhenFunctionCallIsPassedAsArgument(
			String function, long expected) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getLongValue("0:31")).thenReturn(-127l);
		long value = expression.getLongValue(valueProvider);
		assertEquals(expected, value);
	}
	
	
	@Test
	public void testGetLong_ShouldThrowIllegalArgumentException_WhenNoArgumntsArePassed() throws 
			InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("max()");
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Number of parameter mismatch ,MAX function must has atlest 1 argument ");
		expression.getLongValue(valueProvider);
	}
	
	
	public Object[][] dataFor_testGetLong_ShouldThrowInvalidTypeCastException_WhenArgumentContainsNonNumericCharacter(){
		return new Object[][]{
				{"max(\"1.2\")","1.2"},
				{"max(\"1\",\"12a\",\"4\")","12a"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testGetLong_ShouldThrowInvalidTypeCastException_WhenArgumentContainsNonNumericCharacter")
	public void testGetLong_ShouldThrowInvalidTypeCastException_WhenArgumentContainsNonNumericCharacter(
			String function,String arg) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("Configured literal contains a nonnumeric value : " + arg);
		expression.getLongValue(valueProvider);
	}
	
	
}
