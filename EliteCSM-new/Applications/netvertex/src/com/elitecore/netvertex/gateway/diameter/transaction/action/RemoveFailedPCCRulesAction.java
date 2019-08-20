package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;

public class RemoveFailedPCCRulesAction extends ActionHandler{
	private static final String NAME = "REMOVE-FAILED-RULES";
	public RemoveFailedPCCRulesAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		// FIXME: Need to remove PCC Rules which is failed to install
		
		
		return null;
	}
}
