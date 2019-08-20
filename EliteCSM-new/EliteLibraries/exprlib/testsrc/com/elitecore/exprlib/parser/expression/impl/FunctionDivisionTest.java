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
public class FunctionDivisionTest {

	private Compiler compiler;
	private FunctionDivision functionDivision;
	private ValueProvider valueProvider= new ValueProviderImpl();
	@Before
	public void setUp(){
		compiler = Compiler.getDefaultCompiler();
		functionDivision = (FunctionDivision)new FunctionDivision().clone();
	}
	
	public Object[][] dataprovider_for_getting_longVales_of_expression() throws Exception{
			compiler= Compiler.getDefaultCompiler();
			return new Object [][] {
					{
						new Expression[] { compiler.parseExpression("\"9\"") , compiler.parseExpression("\"2\"") , compiler.parseExpression("\"2\"")}, 2
					},
					{
						new Expression[] { compiler.parseExpression("\"7\"") , compiler.parseExpression("\"2\"") , compiler.parseExpression("\"2\"")}, 1
					},
					{
						new Expression[] { compiler.parseExpression("\"-4\"") , compiler.parseExpression("\"2\"")}, -2
					},
					{
						new Expression[] { compiler.parseExpression("\"-4\"") , compiler.parseExpression("\"-2\"")}, 2
					}
			};
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression") 
	public void test_getLongValues_function_is_called_when_argument_list_contains_two_or_more_elements(Expression[] parameters,long results) throws Exception{
			for(Expression ex : parameters){
				functionDivision.addArgument(ex);
			}
			Assert.assertEquals(results, functionDivision.getLongValue(valueProvider));
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void test_getStringValue_function_is_called_when_argument_list_contains_two_or_more_elements(Expression[] parameters,String results) throws Exception{
			for(Expression ex : parameters){
				functionDivision.addArgument(ex);
			}
			Assert.assertEquals(results, functionDivision.getStringValue(valueProvider));
			
		
	}
	
	public Object[][] dataProvider_for_function_should_throw_Illegal_Argument_exception_when_no_of_arg_is_less_than_two() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
		
			{new Expression[] { compiler.parseExpression("\"10\"")}}
	
		};
	}
	
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_function_should_throw_Illegal_Argument_exception_when_no_of_arg_is_less_than_two")
	public void test_function_should_throw_IllegalArgumentException_when_less_than_two_arguments_passed(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionDivision.addArgument(ex);
			}
			functionDivision.getLongValue(valueProvider);
	}	
	
	
	
	public Object[][] dataProvider_for_Providing_ZERO_as_Expression_Value_to_function_getLongValue() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
		
			{new Expression[] { compiler.parseExpression("\"10\"") , compiler.parseExpression("\"0\"") ,  compiler.parseExpression("\"20\"")}},
	
		};
	}
	
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_Providing_ZERO_as_Expression_Value_to_function_getLongValue")
	public void test_function_should_throw_Illegal_argument_exception_when_zero_is_passed_as_argument_value(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionDivision.addArgument(ex);
			}
			functionDivision.getLongValue(valueProvider);
	}
	
	
	@Rule public ExpectedException exception = ExpectedException.none();
	@Test 
	public void test_function_should_re_throw_exception_thrown_by_valueprovider()throws Exception{

		functionDivision.addArgument(compiler.parseExpression("30"));
		functionDivision.addArgument(compiler.parseExpression("\"20\""));
		functionDivision.addArgument(compiler.parseExpression("null"));
		
		valueProvider = Mockito.mock(ValueProviderImpl.class);
		Mockito.when(valueProvider.getLongValue(Mockito.anyString())).thenThrow(new InvalidTypeCastException("value provider exception"));
		
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("value provider exception");
		
		functionDivision.getLongValue(valueProvider);
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
			return 1;
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
