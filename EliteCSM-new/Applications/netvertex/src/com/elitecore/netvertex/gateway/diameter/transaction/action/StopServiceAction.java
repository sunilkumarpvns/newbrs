package com.elitecore.netvertex.gateway.diameter.transaction.action;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

/**
 * StopService Method send RAR for rule remove to Gx and return WAIT_FOR_RAA if Gx session is found otherwise COMPLETE
 */

/*
 * Used in Transaction
 * 1)SessionTermination
 */

public class StopServiceAction extends ActionHandler {
	private static final String NAME = "STOP-SERVICE";
	private static final String MODULE = "STP-SRVC";
	
	public StopServiceAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		TransactionSession session = getTransactionContext().getTransactionSession();
		
		PCRFResponse response = session.get(SessionKeys.PCRF_RESPONSE);
		PCRFRequest pcrfRequest = session.get(SessionKeys.PCRF_REQUEST);

		sendSTR(pcrfRequest, response);

		if (response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.val) == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping sending RAR. Reason: Core Session id for Gx session not found");
			return TransactionState.COMPLETE;
		}
		
		List<AFSessionRule> sessionRules = response.getPreviousActiveAFSessions();

		if (Collectionz.isNullOrEmpty(sessionRules)) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping sending RAR. Reason: Could not found previous PCC rules");

			return TransactionState.COMPLETE;
		}


		return sendRAR(response, pcrfRequest, sessionRules);

	}

	private TransactionState sendRAR(PCRFResponse response, PCRFRequest pcrfRequest, List<AFSessionRule> previousSessionRules) {

		String coreSessionId = response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.val) + ':' + SessionTypeConstant.GX.val;

		SessionData gxSession = locateGxSession(coreSessionId);

		if (gxSession == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping sending RAR. Reason: Gx session not found.");
			return TransactionState.COMPLETE;
		}

		List<String> removablePCCRules = new ArrayList<String>();

		for (int i = 0; i < previousSessionRules.size(); i++) {
			removablePCCRules.add(previousSessionRules.get(i).getPccRule());
		}

		PCRFResponse pcrfResForRAR = buildPCRFResponse(response, gxSession, removablePCCRules);

		DiameterRequest request = getTransactionContext().getControllerContext().buildRAR(pcrfResForRAR);

		if (request != null) {

			boolean requestSent = getTransactionContext().sendRequest(request, pcrfResForRAR.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), false);

			if (requestSent) {
				/*
				 * Intentionally not wait for RAA. In case of STR, update Gx
				 * Session after RAR successfully sent
				 */

				PCRFRequest pcrfRequestForGx = new PCRFRequestImpl();
				PCRFPacketUtil.buildPCRFRequest(gxSession, pcrfRequestForGx);
				pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
				handleSession(pcrfRequestForGx, pcrfResForRAR);
			}

		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "RAR sending skipped. Reason: RAR not generated from PCRF response");
		}
		
		return TransactionState.COMPLETE;
	}

	private void sendSTR(PCRFRequest pcrfRequest, PCRFResponse response) {
		DiameterRequest diameterRequest =  getDiameterRequest();
		
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		
		getTransactionContext().getControllerContext().buildSTA(response, answer);
		
		getTransactionContext().sendAnswer(answer, diameterRequest);
		
		/*
		 * Intentionally not check response sent result. In STR, NetVertex should remove session. 
		 */
		handleSession(pcrfRequest, response);
	}

	private PCRFResponse buildPCRFResponse(PCRFResponse response, SessionData gxSession, List<String> removablePCCRules) {
		
		PCRFResponse pcrfResForRAR = new PCRFResponseImpl();
		PCRFPacketUtil.buildPCRFResponse(gxSession, pcrfResForRAR);
		pcrfResForRAR.setAttribute(PCRFKeyConstants.CS_AF_SESSION_ID.getVal(), response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
		pcrfResForRAR.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.DIAMETER.getVal());
		pcrfResForRAR.setRemovablePCCRules(removablePCCRules);
		pcrfResForRAR.setAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal(), response.getAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal()));
		
		
		return pcrfResForRAR;
	}
	
	/**
	 * locateGxSession fetch Gx session based on coresessionid
	 */
	private SessionData locateGxSession(String coreSessionId) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Locating Gx session Core session ID " + coreSessionId);
		SessionLocator sessionLocator =  getTransactionContext().getControllerContext().getSessionLocator();
		return sessionLocator.getCoreSessionByCoreSessionID(coreSessionId);
	}

}
