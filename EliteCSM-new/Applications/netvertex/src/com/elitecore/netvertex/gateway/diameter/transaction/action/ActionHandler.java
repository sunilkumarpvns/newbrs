package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.core.util.Predicates;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;


import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class ActionHandler {

	private static final String MODULE = "ACTION-HNDLR";
	private DiameterTransactionContext transactionContext;

	public ActionHandler(DiameterTransactionContext transactionContext) {
		this.transactionContext = transactionContext;
	}

	protected final DiameterTransactionContext getTransactionContext() {
		return transactionContext;
	}

	protected DiameterRequest getDiameterRequest() {
		return transactionContext.getTransactionSession().get(SessionKeys.DIAMETER_REQUEST);
	}

	protected DiameterAnswer getDiameterResponse() {
		return transactionContext.getTransactionSession().get(SessionKeys.DIAMETER_RESPONSE);
	}

	public abstract TransactionState handle();

	public abstract String getName();


	private void sendRejectAnswer(int code, String gatewayName){

		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		IDiameterAVP resultCodeAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAVP.setInteger(code);
		answer.addAvp(resultCodeAVP);

		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME);

		if (diameterAVP != null) {

			if (gatewayName != null) {
				diameterAVP.setStringValue(gatewayName);

				answer.addInfoAvp(diameterAVP);
			} else {
				getLogger().warn(MODULE, "Unable to create "
						+ DiameterDictionary.getInstance().getAttributeName(diameterAVP.getVendorId(), diameterAVP.getAVPCode())
						+ " attribute. Reason: Gateway name not found from PCRF Request");
			}

		} else {
			getLogger().warn(MODULE, "Unable to create EC_ORIGINATOR_PEER_NAME (" + DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME +
					") attribute. Reason: attribute not found from dictionary");
		}

		getTransactionContext().getControllerContext().sendAnswer(answer, diameterRequest);

	}


	protected void sendRejectResponse(ResultCode resultCode, DiameterGatewayConfiguration configuration) {

		String gatewayName = configuration.getName();

		int code = resultCode.getCode();

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Sending " + code + " for Diameter request. Reason: PCRF Request not submitted to PCRFRequest");
		}

		sendRejectAnswer(code, gatewayName);

	}


	protected void sendRejectResponse(PCRFRequest pcrfRequest) {

		OverloadAction overloadAction = getTransactionContext().getControllerContext().getActionOnOverload();
		if (overloadAction == OverloadAction.DROP) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Dropping request, Session-ID:" + pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val)
						+ ". Reason: PCRF Request not submitted to PCRFRequest");
			}
			return;
		}

		int resultCode = getTransactionContext().getControllerContext().getOverloadResultCode();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Sending " + resultCode + " for Diameter request. Reason: PCRF Request not submitted to PCRFRequest");
		}

		String gatewayName = pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);

		sendRejectAnswer(resultCode,gatewayName);

	}

	protected PCRFRequest getPCRFRequest() {
		return transactionContext.getTransactionSession().get(SessionKeys.PCRF_REQUEST);
	}

	protected PCRFResponse getPCRFResponse() {
		return transactionContext.getTransactionSession().get(SessionKeys.PCRF_RESPONSE);
	}

	protected void handleSession(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
		getTransactionContext().getControllerContext().handleSession(pcrfRequest, pcrfResponse);
	}

	protected DiameterAnswerListener getDiameterAnswerListener() {
		return getTransactionContext().getTransactionSession().get(SessionKeys.DIAMETER_ANSWER_LISTENER);
	}

	protected void addCoreSessionAttibutes(PCRFRequest pcrfRequest) throws MappingFailedException{

		SessionLocator sessionLocator = this.transactionContext.getControllerContext().getSessionLocator();
		String coreSessionId = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Locating Core Session by core-session-ID = " + coreSessionId);
        }

		SessionData session = sessionLocator.getCoreSessionByCoreSessionID(coreSessionId, pcrfRequest, Predicates.requestNumberPredicateForRequest());
		if (session != null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Core Session located for core-session-ID = " + coreSessionId);
            }
			PCRFPacketUtil.buildPCRFRequest(session, pcrfRequest, false);

			validateSessionType(pcrfRequest, session);

		} else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No record found for core-session-ID = " + coreSessionId);
            }
        }

	}

	private void validateSessionType(PCRFRequest pcrfRequest, SessionData session) throws MappingFailedException{

		String sessionType = session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.val);
		String pcrfRequestSessionType = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val);

		if(pcrfRequestSessionType == null){

			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, sessionType);
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, session.getValue(PCRFKeyConstants.CS_SERVICE.val));

		}else if(sessionType != null && sessionType.equals(pcrfRequestSessionType) == false){

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Session type is changed from  " + sessionType + " to " + pcrfRequestSessionType);
			}

			throw new MappingFailedException("Reason: Session type is changed.", ResultCode.DIAMETER_END_USER_SERVICE_DENIED);

		}

	}

	
	protected PCRFRequest createPCRFRequest(DiameterToPCCPacketMapping dToPMapping, DiameterGatewayConfiguration configuration) throws MappingFailedException {

		PCRFRequest pcrfRequest = new PCRFRequestImpl();
		dToPMapping.apply(new PCRFRequestMappingValueProvider(getDiameterRequest(), pcrfRequest, configuration));
		
		TransactionSession tranSession = getTransactionContext().getTransactionSession();
		
		String transactionId = tranSession.get(SessionKeys.TRANSACTION_ID);
		pcrfRequest.setAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal(), transactionId);

		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) == false && isEventRequest(pcrfRequest) == false) {
			addCoreSessionAttibutes(pcrfRequest);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "PCRF Request Created. " + pcrfRequest.toString());
		}

		return pcrfRequest;
	}

	private boolean isEventRequest(PCRFRequest pcrfRequest) {
		return PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val));
	}
}
