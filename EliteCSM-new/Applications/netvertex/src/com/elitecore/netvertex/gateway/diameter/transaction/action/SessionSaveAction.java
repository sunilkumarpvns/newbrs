package com.elitecore.netvertex.gateway.diameter.transaction.action;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;

public class SessionSaveAction extends ActionHandler {

	public static final String MODULE= "SESS-SAVE-ACTN";
	private static final String NAME = "SESSIONSAVEACTION";
	
	public SessionSaveAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}
	
	@Override
	public TransactionState handle() {
		
		String subscriberIdentity = getPCRFRequest().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Success RAA received. So updating session for subscriber ID: " + subscriberIdentity);
		}
		
		handleSession(getPCRFRequest(), getPCRFResponse());
		
		return TransactionState.COMPLETE;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
