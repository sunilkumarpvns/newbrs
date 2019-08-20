package com.elitecore.exprlib.parser.expression.impl;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class FunctionGetFirstTest {
	
	private Compiler compiler;

	@Mock private ValueProvider valueProvider;

	@Rule public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
	}

	@Test
	public void throwsMissingIdentifierExceptionIfArgumentIsEvaluatedToNull() 
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		
		Mockito.when(valueProvider.getStringValues("missing_identifier")).thenReturn(null);
		
		Expression expression = compiler.parseExpression("getfirst(missing_identifier)");
		
		expectedException.expect(MissingIdentifierException.class);
		expression.getStringValue(valueProvider);
	}
	
	@Test
	public void throwsMissingIdentifierExceptionIfArgumentIsEvaluatedToEmptyList() 
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		
		Mockito.when(valueProvider.getStringValues("missing_identifier")).thenReturn(Collections.<String>emptyList());
		
		Expression expression = compiler.parseExpression("getfirst(missing_identifier)");
		
		expectedException.expect(MissingIdentifierException.class);
		expression.getStringValue(valueProvider);
	}

	@Test
	@Parameters(method = "dataFor_getsFirstValueFromTheResultOfExpressionEvaluatedInArgument")
	public void getsFirstValueFromTheResultOfExpressionEvaluatedInArgument(List<String> identifierValues) throws Exception {
		Mockito.when(valueProvider.getStringValues("identifier")).thenReturn(identifierValues);
		Expression expression = compiler.parseExpression("getfirst(identifier)");
		
		String actual = expression.getStringValue(valueProvider);
		assertEquals(identifierValues.get(0), actual);
	}
	
	public Object[] dataFor_getsFirstValueFromTheResultOfExpressionEvaluatedInArgument() {
		return $(
				Arrays.asList("1"),
				Arrays.asList("3", "2", "1")
		);
	}
	 
	@Test
	@Parameters (method = "dataFor_throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotOne")
	public void throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotOne(String function) throws Exception {
		
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(IllegalArgumentException.class);
		
		expression.getStringValue(valueProvider);
	}
	
	public Object[][] dataFor_throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotOne() {
		return new Object[][] {
				{ "getfirst(\"literal\", any_expression)"},
				{ "getfirst()" }
		};
	}
	
	@Test
	public void getFirstThrowsMissingIdentifierExpressionIfComposedExpressionReturnsNull() throws
	InvalidExpressionException, InvalidTypeCastException,
	IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("getfirst(null)");
		expectedException.expect(MissingIdentifierException.class);
		expression.getStringValue(valueProvider);
	}
}

