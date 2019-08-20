package com.elitecore.aaa.core.plugins.transactionlogger;

import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class StringFormatter implements Formatter {
	
	private final String value;

	public StringFormatter(String value) {
		this.value = value;
	}

	@Override
	public String process(ValueProvider valueProvider) {
		return this.value;
	}

	@Override
	public void init() throws InvalidExpressionException {
		
	}
}