package com.elitecore.diameterapi.diameter.translator.function;

import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractFunctionExpression;

public class FunctionPeerSequence extends AbstractFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String PEER_SEQUENCE = "seqpeer";
	transient private IStackContext stackContext;
	
	public FunctionPeerSequence(IStackContext stackContext) {
		this.stackContext = stackContext;
	}

	@Override
	public String getName() {
		return PEER_SEQUENCE;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, MissingIdentifierException{
		return String.valueOf(getLongValue(valueProvider));
	}

	@Override
	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, MissingIdentifierException {
		return stackContext.getNextPeerSequence(valueProvider.getStringValue(DiameterAVPConstants.ORIGIN_HOST));
	}

}
