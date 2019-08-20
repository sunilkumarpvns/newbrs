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
public class FunctionLog10Test {

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
	
	@Test
	public void test_getLongValue_must_not_throw_any_exception() 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {
		
		assertEquals((long)Math.log10(10), 
				compiler.parseExpression("log10(\"10\")")
					.getLongValue(valueProvider));
	}
	
	
	
	@Test
	@Parameters(method="dataprovider_for_invalid_loge_expressions")
	public void test_getLongValue_for_invalid_loge_expressions_must_throw_IllegalArgumentException(
					String expression, String message) 
					throws InvalidTypeCastException, 
					IllegalArgumentException, 
					MissingIdentifierException, 
					InvalidExpressionException {

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(message);
		
		compiler.parseExpression(expression)
			.getLongValue(valueProvider);
	}
	
	public Object[] dataprovider_for_invalid_loge_expressions() {
		
		return $(
				$("log10(\"0\")", "log10 function requires non-zero positive argument"),
				$("log10(\"-1\")", "log10 function requires non-zero positive argument"),
				$("log10(\"10\", \"2\")", "log10 function requires only one argument, Syntax: loge(<arg>)"),
				$("log10()", "log10 function requires only one argument, Syntax: loge(<arg>)")
				);
	}
	
}
