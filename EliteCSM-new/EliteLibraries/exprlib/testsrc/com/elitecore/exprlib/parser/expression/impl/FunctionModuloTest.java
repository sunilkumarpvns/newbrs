package com.elitecore.exprlib.parser.expression.impl;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionModuloTest {

	private Compiler compiler = Compiler.getDefaultCompiler();
	@Mock private ValueProvider valueProvider;
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void beforeEachTest() throws InvalidTypeCastException, MissingIdentifierException {
		MockitoAnnotations.initMocks(this);
		when(valueProvider.getLongValue(Mockito.anyString())).thenAnswer(new Answer<Long>() {

			@Override
			public Long answer(InvocationOnMock invocation) throws Throwable {
				return Long.valueOf(invocation.getArgumentAt(0, String.class));
			}
		});
	}
	
	public Object[] dataprovider_for_getting_longVales_of_expression() {
		
		return $(
				$("mod(\"1\" , \"2\")", 1),
				$("mod(\"10\", \"2\")", 0)
				);
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression") 
	public void test_getLongValue_must_not_throw_any_exception(
			String expression, long expectedResult) 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {
		
		assertEquals(expectedResult, 
				compiler.parseExpression(expression)
					.getLongValue(valueProvider));
	}
	
	@Test
	public void test_getLongValue_must_throw_IllegalArgumentException_on_arguments_less_than_two() 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("mod function requires two arguments, Syntax: mod(<arg1>,<arg2>)");
		
		compiler.parseExpression("mod(\"1\")")
			.getLongValue(valueProvider);
	}

	@Test
	public void test_getLongValue_must_throw_IllegalArgumentException_on_arguments_value_zero() 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("can not mod by zero");
		
		compiler.parseExpression("mod(\"23\", \"0\")")
			.getLongValue(valueProvider);
		
	}
	
	@Test
	public void test_getLongValue_must_throw_IllegalArgumentException_on_arguments_more_than_two() 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("mod function requires two arguments, Syntax: mod(<arg1>,<arg2>)");
		
		compiler.parseExpression("mod(\"1\" , \"3\", \"7\")")
			.getLongValue(valueProvider);
	}
}
