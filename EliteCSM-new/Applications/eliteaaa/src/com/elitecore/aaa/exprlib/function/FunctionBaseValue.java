package com.elitecore.aaa.exprlib.function;

import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractStringFunctionExpression;

/**
 * This function returns the base/raw value of the enumerated attribute passed as an argument. In case
 * when the attribute is not of enumerated type then it will return the raw value.
 * <br/>
 * <br/>
 * Note: baseValue function does not support literal or nested functions as an argument.
 * <br/><br/>
 * 
 * <b>Usage:</b> 
 * <br/>
 * If the configuration is "baseValue(0:6)" and the value of attribute 0:6 is 1, mapped in dictionary
 * with name {@literal "}Login-User{@literal "}; this function will return 1 which is the base value.
 * <br/>
 * 
 * If baseValue(0:1) is configured and the value of the attribute 0:1 is eliteaaa, here argument 0:1 
 * is not of enumerated type. This function will return the raw value itself i.e. "eliteaaa". 
 * 
 * <br/>
 * <br/>
 * <b/>Warning: This function needs an AttributeValueProvider for working. Failing to do so will cause a 
 * {@link ClassCastException} at runtime.
 * 
 * @author khushbu.chauhan
 */
public class FunctionBaseValue extends AbstractStringFunctionExpression {
	
	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		
		if (argumentList.size() != 1) {
			throw new IllegalArgumentException("Number of parameters mismatch, baseValue function must have one argument");
		}
		
		if (argumentList.get(0).getExpressionType() != Expression.IdentifierExpression) {
			throw new InvalidTypeCastException("Argument must be an identifier");
		}
		
		IdentifierExpression identifier = (IdentifierExpression) argumentList.get(0);
		String symbol = identifier.getIdentifierSymbol().getName();
		
		AttributeValueProvider attributeValueProvider = (AttributeValueProvider) valueProvider;
		return attributeValueProvider.getDictionaryKey(symbol);
	}

	@Override
	public String getName() {
		return "baseValue";
	}

}
