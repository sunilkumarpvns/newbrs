package com.elitecore.aaa.exprlib.function;

import junitparams.JUnitParamsRunner;
import static junitparams.JUnitParamsRunner.$;
import junitparams.Parameters;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.exprlib.function.FunctionBaseValue;
import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith (JUnitParamsRunner.class)
public class FunctionBaseValueTest {

	@Mock AttributeValueProvider attributeValueProvider;
	private Compiler compiler;
	private Expression functionExpression;
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception{
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
		compiler.addFunction(new FunctionBaseValue());
		functionExpression = compiler.parseExpression("baseValue(0:40)");
	}
	
	@Test
	@Parameters(method="dataFor_testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToOne")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToOne(String function)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
				MissingIdentifierException, IllegalArgumentException {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Number of parameters mismatch, baseValue function must have one argument");
		
		compiler.parseExpression(function).getStringValue(attributeValueProvider);
	}

	public Object[][] dataFor_testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToOne() {
		return new Object[][] {
				{"baseValue()"},
				{"baseValue(0:40,0:41)"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_baseValueFunctionDoesNotSupportAnyArgumentOtherThanAnIdentifier")
	public void baseValueFunctionDoesNotSupportAnyArgumentOtherThanAnIdentifier(String expression)
			throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
				MissingIdentifierException {
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("Argument must be an identifier");
		
		compiler.parseExpression(expression).getStringValue(attributeValueProvider);
	}
	
	public Object[] dataFor_baseValueFunctionDoesNotSupportAnyArgumentOtherThanAnIdentifier() {
		return $(
			"baseValue(baseValue(0:40))",
			"baseValue(\"literal\")"
		); 
	}
	
	@Test
	public void testGetStringValue_ShouldThrowClassCastException_WhenProvidedValueProviderIsNotOfTypeAttributeValueProvider() 
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		expectedException.expect(ClassCastException.class);
		
		functionExpression.getStringValue(mock(ValueProvider.class));
	}
	
	@Test
	public void baseValueFunctionReturnsTheBaseOrRawValueAndDoesNotProvideItsMappedNameInDictionary() 
			throws InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException {
		String expected = "1";
		
		Mockito.when(attributeValueProvider.getDictionaryKey("0:40")).thenReturn(expected);
		assertEquals(expected, functionExpression.getStringValue(attributeValueProvider));
	}
}
