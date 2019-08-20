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
public class FunctionPercentageTest {
	
	private Compiler compiler;
	private FunctionPercentage functionPercentage;
	private ValueProvider valueProvider= new ValueProviderImpl();
	@Before
	public void setUp(){
		compiler = Compiler.getDefaultCompiler();
		
		functionPercentage = (FunctionPercentage)new FunctionPercentage().clone();
	}
	
	public Object[][] dataprovider_for_getting_longVales_of_expression() throws Exception{
			compiler= Compiler.getDefaultCompiler();
			return new Object [][] {
					{
						new Expression[] { compiler.parseExpression("\"700\"") , compiler.parseExpression("\"1000\"")}, 70
					},
					{
						new Expression[] { compiler.parseExpression("\"300\"") , compiler.parseExpression("\"1000\"")}, 30
					},
					{
						new Expression[] { compiler.parseExpression("\"666\"") , compiler.parseExpression("\"1000\"")}, 66
					},
					{
						new Expression[] { compiler.parseExpression("\"0\"") , compiler.parseExpression("\"1000\"")}, 0
					},
					{
						new Expression[] { compiler.parseExpression("\"666\"") , compiler.parseExpression("\"666\"")}, 100
					}
			};
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void test_function_should_call_getLongValues_when_argument_list_is_passed_with_two_or_more_elements(Expression[] parameters,long results) throws Exception{
			for(Expression ex : parameters){
				functionPercentage.addArgument(ex);
			}
			Assert.assertEquals(results, functionPercentage.getLongValue(valueProvider));
	}
	
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void test_function_should_call_getStringValues_when_argument_list_is_passed_with_two_or_more_elements(Expression[] parameters,String results) throws Exception{
			for(Expression ex : parameters){
				functionPercentage.addArgument(ex);
			}
			Assert.assertEquals(results, functionPercentage.getStringValue(valueProvider));
			
		
	}
	
	public Object[][] dataProvider_for_function_that_throws_IllegalArgumentException_when_arguments_are_not_equal_to_two() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
		
			{new Expression[] { compiler.parseExpression("\"10\"")}},
			{new Expression[] { compiler.parseExpression("\"40\"")}},
			{new Expression[] { compiler.parseExpression("\"100\"")}},
			{
				new Expression[] { compiler.parseExpression("\"666\"") , compiler.parseExpression("\"1000\""),compiler.parseExpression("\"666\"")}
			}
			
	
		};
	}
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_function_that_throws_IllegalArgumentException_when_arguments_are_not_equal_to_two")
	public void test_getLongValue_Exception(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionPercentage.addArgument(ex);
			}
			functionPercentage.getLongValue(valueProvider);
	}
	
	
	
	public Object[][] dataProvider_for_function_that_throws_IllegalArgumentException_when_any_of_the_argument_is_negative() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
			{
				new Expression[] { compiler.parseExpression("\"-666\"") , compiler.parseExpression("\"1000\"")}
			},
			{
				new Expression[] { compiler.parseExpression("\"666\"") , compiler.parseExpression("\"-1000\"")}
			}

			
	
		};
	}
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_function_that_throws_IllegalArgumentException_when_any_of_the_argument_is_negative")
	public void test_functtion_should_throw_IllegalArgumentException_when_any_of_two_arguments_is_negative(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionPercentage.addArgument(ex);
			}
			functionPercentage.getLongValue(valueProvider);
	}
	
	
	
	
	public Object[][] dataProvider_for_Providing_ZERO_as_denominator_Value_to_function_getLongValue() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
			{
				new Expression[] { compiler.parseExpression("\"10\"") , compiler.parseExpression("\"0\"")}
			}
	
		};
	}
	
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataProvider_for_Providing_ZERO_as_denominator_Value_to_function_getLongValue")
	public void test_function_should_throw_Illegal_argument_exception_when_zero_is_passed_as_argument_value(Expression[] parameters)throws Exception{
			for(Expression ex : parameters){
				functionPercentage.addArgument(ex);
			}
			functionPercentage.getLongValue(valueProvider);
	}
	
	@Rule public ExpectedException exception = ExpectedException.none();
	@Test
	public void test_function_should_Re_throw_exception_thrown_by_valueprovider() throws Exception{
		functionPercentage.addArgument(compiler.parseExpression("30"));
		functionPercentage.addArgument(compiler.parseExpression("\"20\""));
		
		valueProvider = Mockito.mock(ValueProviderImpl.class);
		Mockito.when(valueProvider.getLongValue(Mockito.anyString())).thenThrow(new InvalidTypeCastException("value provider exception"));
		
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("value provider exception");
		functionPercentage.getLongValue(valueProvider);
		
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
