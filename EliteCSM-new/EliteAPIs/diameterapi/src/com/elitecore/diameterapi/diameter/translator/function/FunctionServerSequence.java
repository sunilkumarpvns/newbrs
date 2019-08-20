package com.elitecore.diameterapi.diameter.translator.function;

import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractFunctionExpression;

public class FunctionServerSequence extends AbstractFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String SERVER_SEQUENCE = "seqserv";
	transient private IStackContext stackContext;
	
	public FunctionServerSequence(IStackContext stackContext) {
		this.stackContext = stackContext;
	}
	@Override
	public String getName() {
		return SERVER_SEQUENCE;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return String.valueOf(getLongValue(valueProvider));
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return stackContext.getNextServerSequence();
	}

}
