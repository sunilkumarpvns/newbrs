package com.elitecore.aaa.core.plugins.transactionlogger;

import com.elitecore.commons.base.Strings;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class ValueProviderFormatter implements Formatter {
	
	private String literal;

	public ValueProviderFormatter(String literal) {
		this.literal = literal;
	}

	@Override
	public void init() throws InvalidExpressionException {
		
	}

	@Override
	public String process(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return Strings.join(";", valueProvider.getStringValues(this.literal));
	}

}
