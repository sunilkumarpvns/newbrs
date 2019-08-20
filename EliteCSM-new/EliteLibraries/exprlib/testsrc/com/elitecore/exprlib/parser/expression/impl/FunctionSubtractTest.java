package com.elitecore.exprlib.parser.expression.impl;


import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;




@RunWith(JUnitParamsRunner.class)
public class FunctionSubtractTest{

	private Compiler compiler;
	private FunctionSubtract functionSubtract;
	private ValueProvider valueProvider= new ValueProviderImpl();
	@Before
	public void setUp(){
		compiler = Compiler.getDefaultCompiler();
		functionSubtract = (FunctionSubtract)new FunctionSubtract().clone();
	}
	
	public Object[][] dataprovider_for_getting_longVales_of_expression() throws Exception{
			compiler= Compiler.getDefaultCompiler();
			return new Object [][] {
					{
						new Expression[] { compiler.parseExpression("\"30\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"10\"")}, 0
					},
					{
						new Expression[] { compiler.parseExpression("\"10\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"30\"")}, -40
					},
					{
						new Expression[] { compiler.parseExpression("\"25\"") , compiler.parseExpression("\"10\"") , compiler.parseExpression("\"5\"")}, 10
					},
					{
						new Expression[] { compiler.parseExpression("\"-25\"") , compiler.parseExpression("\"10\"") , compiler.parseExpression("\"5\"")}, -40
					},
					{
						new Expression[] { compiler.parseExpression("\"-25\"") , compiler.parseExpression("\"-10\"") , compiler.parseExpression("\"5\"")}, -20
					}
					
					
			};
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void test_function_should_call_getLongValues_when_two_or_more_arguments_are_passed_as_parameters(Expression[] parameters,long results) throws Exception{
			for(Expression ex : parameters){
				functionSubtract.addArgument(ex);
			}
			Assert.assertEquals(results, functionSubtract.getLongValue(valueProvider));
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void  test_function_should_call_getStringValues_when_two_or_more_arguments_are_passed_as_parameters(Expression[] parameters,String results) throws Exception{
			for(Expression ex : parameters){
				functionSubtract.addArgument(ex);
			}
			Assert.assertEquals(results, functionSubtract.getStringValue(valueProvider));
			
		
	}
	
	
	public Object[][] dataProvider_for_function_that_throws_IllegalArgumentException_when_less_than_two_arguments_are_passed() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
		
			{new Expression[] { compiler.parseExpression("\"10\"")}}
	
		};
	}
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_function_that_throws_IllegalArgumentException_when_less_than_two_arguments_are_passed")
	public void test_function_should_throw_Illegal_argument_exception_when_less_than_two_arguments_are_passed_in_parameter_list(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionSubtract.addArgument(ex);
			}
			functionSubtract.getLongValue(valueProvider);
			
	}

	@Rule public ExpectedException exception = ExpectedException.none();
	@Test
	public void test_function_should_Re_throw_exception_thrown_by_valueprovider() throws Exception{
		functionSubtract.addArgument(compiler.parseExpression("30"));
		functionSubtract.addArgument(compiler.parseExpression("\"20\""));
		functionSubtract.addArgument(compiler.parseExpression(" \"\" "));
		
		valueProvider = Mockito.mock(ValueProviderImpl.class);
		Mockito.when(valueProvider.getLongValue(Mockito.anyString())).thenThrow(new InvalidTypeCastException("value provider exception"));
		
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("value provider exception");
		functionSubtract.getLongValue(valueProvider);
		
	}
	
	
	@Rule public ExpectedException exception1 = ExpectedException.none();
	@Test
	public void test_function_should_Re_throw_exception_thrown_by_valueprovider_when_empty_string_is_passed() throws Exception{
		functionSubtract.addArgument(compiler.parseExpression("\"30\""));
		functionSubtract.addArgument(compiler.parseExpression("\"20\""));
		functionSubtract.addArgument(compiler.parseExpression(" \"\" "));
		
		valueProvider = Mockito.mock(ValueProviderImpl.class);
		Mockito.when(valueProvider.getLongValue(Mockito.anyString())).thenThrow(new InvalidTypeCastException("value provider exception"));
		
		exception1.expect(InvalidTypeCastException.class);
		functionSubtract.getLongValue(valueProvider);
		
	}

	
	private static class ValueProviderImpl implements ValueProvider{

		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return String.valueOf(getLongValue(identifier));
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return 0;
		}

		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public Object getValue(String key) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
}
