package com.elitecore.exprlib.parser.expression.impl;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
public class FunctionGetLastTest {
	private Compiler compiler;
	private FunctionGetLast getFirst;

	@Mock private ValueProvider valueProvider;

	@Rule public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
		getFirst = new FunctionGetLast();
		compiler.addFunction(getFirst);
	}

	@Test
	@Parameters (method= "dataFor_throwsMissingIdentifierExceptionIfArgumentIsEvaluatedToNullOrEmptyString")
	public void throwsMissingIdentifierExceptionIfArgumentIsEvaluatedToNullOrEmptyString(List<String> result) 
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Mockito.when(valueProvider.getStringValues("identifier_null")).thenReturn(result);
		Expression expression = compiler.parseExpression("getlast(identifier_null)");
		expectedException.expect(MissingIdentifierException.class);
		expression.getStringValue(valueProvider);
	}

	public List<Object[]> dataFor_throwsMissingIdentifierExceptionIfArgumentIsEvaluatedToNullOrEmptyString() {
		ArrayList<Object> nullList= null;
		return  Arrays.asList(
				$ (nullList) ,
				$ (Collections.emptyList())
				);
	}

	@Test
	public void getsFirstValue() 
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		List<String> result = Arrays.asList("1","2","3");
		Mockito.when(valueProvider.getStringValues("identifier")).thenReturn(result);
		Expression expression = compiler.parseExpression("getlast(identifier)");
		String actual = expression.getStringValue(valueProvider);
		assertEquals("3", actual);
	}

	@Test
	@Parameters (method = "dataFor_throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotEqualtoOne")
	public void throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotEqualtoOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(IllegalArgumentException.class);
		expression.getStringValue(valueProvider);
	}

	public List<String> dataFor_throwsIllegalArgumentExceptionIfNumberOfArgumentsIsNotEqualtoOne() {
		return Arrays.asList(
				"getlast(\"identifier1\",dbsession(\"pdpType\"))",
				"getlast()"
		);
	}
}


