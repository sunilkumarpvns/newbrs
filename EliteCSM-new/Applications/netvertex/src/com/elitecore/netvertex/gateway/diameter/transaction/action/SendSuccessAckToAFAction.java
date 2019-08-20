package com.elitecore.netvertex.gateway.diameter.transaction.action;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class SendSuccessAckToAFAction extends ActionHandler {
	private static final String NAME = "SEND-SUCCESS-ACK-TO-AF";
	private static final String MODULE = "SEND-SUCCESS-ACK-TO-AF";
	
	public SendSuccessAckToAFAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {	
		return NAME;
	}

	@Override
	public TransactionState handle() {
		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		PCRFResponse pcrfResponse = getPCRFResponse();
		PCRFRequest pcrfRequest = getPCRFRequest();
				
		/**
		 * On Success RAA, Gx Session should be Updated/Created.
		 * 
		 *  TODO: Create seperate action to update session
		 */
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) || pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE)) {
			handleSession(getGxPCRFRequest(), getGxPCRFResponse());
		}
		
		//FIXME If AAA Fail then send RAR to revert installed rule
		getTransactionContext().getControllerContext().buildAAA(pcrfResponse, answer);		
		boolean responseSent = getTransactionContext().sendAnswer(answer,diameterRequest);
				
		if (responseSent) {
			getTransactionContext().getControllerContext().handleSession(pcrfRequest, pcrfResponse);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping session handling. AAA response is not sent");
			}
		}
		
		return TransactionState.COMPLETE;
	}

	private PCRFRequest getGxPCRFRequest() {
		return getTransactionContext().getTransactionSession().get(SessionKeys.GX_PCRF_REQUEST);
	}

	private PCRFResponse getGxPCRFResponse() {
		return getTransactionContext().getTransactionSession().get(SessionKeys.GX_PCRF_RESPONSE);
	}
}
