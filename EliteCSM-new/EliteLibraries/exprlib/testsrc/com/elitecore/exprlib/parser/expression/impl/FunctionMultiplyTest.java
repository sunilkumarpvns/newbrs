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
public class FunctionMultiplyTest {
	private Compiler compiler;
	private FunctionMultiply functionMultiply;
	private ValueProvider valueProvider= new ValueProviderImpl();
	@Before
	public void setUp(){
		compiler = Compiler.getDefaultCompiler();
		functionMultiply = (FunctionMultiply)new FunctionMultiply().clone();
	}
	
	public Object[][] dataprovider_for_getting_longVales_of_expression() throws Exception{
			compiler= Compiler.getDefaultCompiler();
			
			return new Object [][] {
					{
						new Expression[] { compiler.parseExpression("\"30\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"10\"")}, 6000
					},
					
					{
						new Expression[] { compiler.parseExpression("\"30\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"10\"")}, 6000
					},
					
					{
						new Expression[] { compiler.parseExpression("\"0\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"10\"")}, 0,
					},
					
					{	
						new Expression[] { compiler.parseExpression("\"-30\"") , compiler.parseExpression("\"20\"") , compiler.parseExpression("\"10\"")}, -6000
					},
					
					{	
						new Expression[] { compiler.parseExpression("\"-30\"") , compiler.parseExpression("\"-20\"") , compiler.parseExpression("\"10\"")}, 6000
					},
					
					{	
						new Expression[] { compiler.parseExpression("-30") , compiler.parseExpression("-20") , compiler.parseExpression("10")}, 1
					},
					
					{	
						new Expression[] { compiler.parseExpression("\"-30\"") , compiler.parseExpression("-20") , compiler.parseExpression("10")}, -30
					}
					
			};
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void test_function_should_call_getLongValues_when_argument_list_is_passed_with_two_or_more_elements(Expression[] parameters,long results) throws Exception{
			for(Expression ex : parameters){
				functionMultiply.addArgument(ex);
			}
			Assert.assertEquals(results, functionMultiply.getLongValue(valueProvider));
			
			
			
		
	}
	
	@Test
	@Parameters(method="dataprovider_for_getting_longVales_of_expression")
	public void  test_function_should_call_getStringValues_when_argument_list_is_passed_with_two_or_more_elements(Expression[] parameters,String results) throws Exception{
			for(Expression ex : parameters){
				functionMultiply.addArgument(ex);
			}
			Assert.assertEquals(results, functionMultiply.getStringValue(valueProvider));
	}

	public Object[][] dataprovider_for_function_should_throw_Illegal_Argument_exception_when_no_of_arg_is_less_than_two() throws InvalidExpressionException {
		compiler= Compiler.getDefaultCompiler();
		return new Object [][] {
		
			{new Expression[] {compiler.parseExpression("\"10\"")},0},
			{new Expression[] {},0},
			
	
		};
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="dataprovider_for_function_should_throw_Illegal_Argument_exception_when_no_of_arg_is_less_than_two")
	public void test_function_should_throw_Illegal_Argument_exception_when_no_of_arg_is_less_than_two(Expression[] parameters,String results)throws Exception{
			for(Expression ex : parameters){
				functionMultiply.addArgument(ex);
			}
			functionMultiply.getLongValue(valueProvider);
	}
	
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test 
	public void test_function_should_re_throw_exception_thrown_by_valueprovider()throws Exception{
		
	
		functionMultiply.addArgument(compiler.parseExpression("30"));
		functionMultiply.addArgument(compiler.parseExpression("\"20\""));
		functionMultiply.addArgument(compiler.parseExpression("\"10\""));
		
		valueProvider = Mockito.mock(ValueProviderImpl.class);
		
		Mockito.when(valueProvider.getLongValue(Mockito.anyString())).thenThrow(new InvalidTypeCastException("value provider exception"));
		
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("value provider exception");
		
		functionMultiply.getLongValue(valueProvider);
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
