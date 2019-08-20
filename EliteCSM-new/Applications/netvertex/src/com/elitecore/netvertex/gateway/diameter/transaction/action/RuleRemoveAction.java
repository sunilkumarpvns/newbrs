package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class RuleRemoveAction extends ActionHandler {
	private static final String NAME = "RULE-REMOVE";
	private static final String MODULE = "RULE-REMOVE-ACTN";
	public RuleRemoveAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		// Submit to PCRF
		PCRFRequest pcrfRequest = getTransactionContext().getTransactionSession().get(SessionKeys.PCRF_REQUEST);
		if(pcrfRequest.getAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal()) == null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal(), getTransactionContext().getTransactionSession().get(SessionKeys.TRANSACTION_ID));
		}

		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if(requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Sending Diameter answer with Result Code Diameter TOO Busy. Reason:" +
						"PCRF Request status " + requestStatus.getVal());
			}
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}
		return TransactionState.WAIT_RULE_REMOVE_ACK;
	}

}
