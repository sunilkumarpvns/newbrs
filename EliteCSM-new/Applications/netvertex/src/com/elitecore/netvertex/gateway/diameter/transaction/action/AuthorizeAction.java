package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.core.util.Predicates;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class AuthorizeAction extends ActionHandler {
	private static final String NAME = "AUTHORIZE";
	private static final String MODULE = "AUTHORIZE-ACTN";
	
	public AuthorizeAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {

		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterGatewayControllerContext controllerContext = getTransactionContext().getControllerContext();
		//FIXME NEED TO REFACTOR THIS CODE, AS WE HAVE ALREADY FETCH THE GATWEAYCONFIGURATION WHEN IN xxxHandler() or xxxApplicatin class
		DiameterGatewayConfiguration configuration = controllerContext.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(configuration == null){
			configuration = controllerContext.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}

		PCRFRequest pcrfRequest;

		try {
			pcrfRequest = createPCRFRequest(configuration.getGxCCRMappings(), configuration);

		}catch(MappingFailedException e){

			sendRejectResponse(e.getErrorCode(), configuration);
			LogManager.getLogger().trace(MODULE, e);
			return TransactionState.COMPLETE;
		}


		if(getTransactionContext().getControllerContext().isSubscrberRoming(getDiameterRequest())){
			pcrfRequest.setAttribute(PCRFKeyConstants.SUBSCRIBER_ROAMING.val, "true");
		}

		TransactionSession tranSession = getTransactionContext().getTransactionSession();
		String sessionType = tranSession.get(SessionKeys.SESSION_TYPE);
		if(sessionType != null && sessionType.length() > 0){
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), sessionType);
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + sessionType);
		}
		String val = tranSession.get(SessionKeys.GATEWAY_TYPE);
		if(val != null && val.length() > 0){
			pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), val);
		}
		pcrfRequest.setAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal(), tranSession.get(SessionKeys.TRANSACTION_ID));

		Session diameterSession =  locateDiameterSessionBasedOnSessionId(pcrfRequest, diameterRequest.getApplicationID());
		if(pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START)){
			if(diameterSession !=null){
				String currentRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal());
				if(currentRequestNo != null){
					diameterSession.setParameter(PCRFKeyConstants.REQUEST_NUMBER.getVal(), currentRequestNo);

				}  else {
					diameterSession.removeParameter(PCRFKeyConstants.REQUEST_NUMBER.getVal());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Request number not found in pcrf Request");
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Unable to store request number. Reason: Diameter session not found for session-Id "+pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()));
				}
			}
		}

		if(pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) == false){

			SessionData session = null;
			if(pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE)){
				if(diameterSession !=null){
					String currentRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal());
					if(currentRequestNo != null){
						String previousReqString = (String) diameterSession.getParameter(PCRFKeyConstants.REQUEST_NUMBER.getVal());
						diameterSession.setParameter(PCRFKeyConstants.REQUEST_NUMBER.getVal(), currentRequestNo);
						pcrfRequest.setAttribute(PCRFKeyConstants.PREVIOUS_REQUEST_NUMBER.getVal(), previousReqString);

					}  else {
						diameterSession.removeParameter(PCRFKeyConstants.REQUEST_NUMBER.getVal());
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Request number not found in pcrf Request");
						}
					}
				}
			}
			session = locateCoreSession(pcrfRequest);

			if (session != null) {
				PCRFPacketUtil.buildPCRFRequest(session, pcrfRequest, false);
			}
		}

		String coreSessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);

		String addOnString;
		String dataPackageName;



		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "No AddOn found from netvertex session for Core-Session-ID:"+coreSessionID + " fetch addOns from DiameterSessiion");



		if(diameterSession == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to fetch addOns in diameter session. Reason: Diameter session not found for Core-Session-ID:"+coreSessionID + " fetch addOns from DiameterSessiion");
		}else {
			addOnString = (String) diameterSession.getParameter(PCRFKeyConstants.CS_ADD_ONS.val);

			if(addOnString == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No AddOn found from diameter session for Core-Session-ID:"+coreSessionID);
			}else {
				addOnString = addOnString.trim();

				if(addOnString.isEmpty() == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "AddOn "+ addOnString +" found from diameter session for Core-Session-ID:"+coreSessionID);
					pcrfRequest.setAttribute(PCRFKeyConstants.CS_ADD_ONS.val, addOnString);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No AddOn found from diameter session for Core-Session-ID:"+coreSessionID);
				}

			}



			dataPackageName = (String) diameterSession.getParameter(PCRFKeyConstants.SUB_DATA_PACKAGE.val);

			if(dataPackageName == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No Data Package found from diameter session for Core-Session-ID:"+coreSessionID);
			}else {
				dataPackageName = dataPackageName.trim();

				if(dataPackageName.isEmpty() == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Data Package "+ dataPackageName +" found from diameter session for Core-Session-ID:"+coreSessionID);
					pcrfRequest.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val, dataPackageName);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No Data Package found from diameter session for Core-Session-ID:"+coreSessionID);
				}

			}
		}




		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "PCRF Request : " + pcrfRequest);
		}
		//It saves transaction before submitting req to PCRF
		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if(requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL){
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}


		return TransactionState.WAIT_FOR_AUTH_RES;// Wait
	}
	
	/**
	 * Locating core session based on Core-Session-Id
	 * @param request
	 * @return
	 */
	private SessionData locateCoreSession(PCRFRequest request){
		SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
		String coreSessionId = request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
		
		if(coreSessionId == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Locating Core Session-ID skipped. Reason: Core session ID not found");
			return null;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Locating Core Session for CoreSessionID = " + coreSessionId);
		
		SessionData session = sessionLocator.getCoreSessionByCoreSessionID(coreSessionId, request, Predicates.requestNumberPredicateForRequest());
		if(session != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Core Session Located for Core Session Id = " + coreSessionId);
			return session;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Core Session for core session-Id = " + coreSessionId + " not located");
		}
		
		return null;
	}
	
	/**
	 * Locating diameter session based on Session-Id
	 * @param request
	 * @param appId 
	 * @return
	 */
	private Session locateDiameterSessionBasedOnSessionId(PCRFRequest request, long appId){
		String sessionId = request.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal());
		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Locating diameter session for session-Id = " + sessionId);
		

		if(getTransactionContext().getControllerContext().getStackContext().hasSession(sessionId, appId)){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Diameter session located for session-Id = " + sessionId);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Diameter session for session-Id = " + sessionId + " not located");			
		}
		return getTransactionContext().getControllerContext().getStackContext().getOrCreateSession(sessionId, appId);
	
	}
	
}
