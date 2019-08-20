package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import java.util.EnumSet;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SendCCAAction extends ActionHandler {
	private static final String NAME = "SEND-CCA";
	private static final String MODULE = "SND-CCA-ACTN";
	public SendCCAAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		PCRFResponse pcrfResponse = getTransactionContext().getTransactionSession().get(SessionKeys.PCRF_RESPONSE);
		PCRFRequest pcrfRequest = getTransactionContext().getTransactionSession().get(SessionKeys.PCRF_REQUEST);

		/*
		 * on response received, if sy communication is happen then we should
		 * send the STR request to sy gateway. In this case we need to check if
		 * successful syCommunication is happen or not.
		 * 
		 * In case of Timeout,unknown session id, syCommunicationFail and no
		 * alive gateway found no sy session is created so no need to send STR
		 */
		String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val);
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) 
				&& resultCode != null && resultCode.equalsIgnoreCase(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val) == false) {
			initiateSTRProcedure(pcrfResponse);
		}

		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		getTransactionContext().getControllerContext().buildCCA(pcrfResponse, answer);

		/*
		 * IF CCR-T request, delete session irrespective of response result
		 * code
		 */
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) || PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(resultCode)) {
			handleSession(pcrfRequest, pcrfResponse);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Session handling skipped. Reason: CCA Result code is not success");
			}
		}

		getTransactionContext().sendAnswer(answer, diameterRequest);

		return TransactionState.COMPLETE;
	}

	private void initiateSTRProcedure(PCRFResponse pcrfResponse) {
		String syCommunication = pcrfResponse.getAttribute(PCRFKeyConstants.SY_COMMUNICATION.val);
		if (syCommunication != null &&
				(syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.SY_COMMUNICATION_TIMEOUT.val)
						|| syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_SESSION_ID.val)
						|| syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val)
				) == false)
		{

			if (LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Initiating procedure to terminate sy session");

			PCRFRequest syPCRFRequest = new PCRFRequestImpl();
			PCRFPacketUtil.buildPCRFRequest(pcrfResponse, syPCRFRequest);
			syPCRFRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
			if (LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "PCRF Request created from PCRFResponse" + syPCRFRequest);
			getTransactionContext().submitPCRFRequest(syPCRFRequest);
		}
	}

}
