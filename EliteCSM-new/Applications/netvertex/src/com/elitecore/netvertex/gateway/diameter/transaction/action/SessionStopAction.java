package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
 * Transaction Use This Action
 * ===========================
 * SessionStopTransaction
 */
/**
 * Session Stop Action create  PCRFRequest from DiamterRequest and fetch Rx Session based on Session Binding Attribute
 * if Rx session found then it generate ASR for each and every Rx session found and then submit PCRFrequest to PCRFSerive. 
 * @author harsh
 *
 */
public class SessionStopAction extends ActionHandler{
	private static final String NAME = "SESSION-STOP";
	private static final String MODULE = "SESS-STOP";

	public SessionStopAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);			 
	}


	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		
		DiameterRequest diameterRequest = getDiameterRequest();
		if(diameterRequest == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Skipping further processing. Reason: Diameter Reqeust not found");
			return TransactionState.COMPLETE;
		}

		DiameterGatewayControllerContext controllerContext = getTransactionContext().getControllerContext();
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
		
		if(getTransactionContext().getControllerContext().isRxInterfaceEnable()){
			processRxSessionsRelease(pcrfRequest);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: Rx interface is disabled");
		}
		
		
		String coreSessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "No AddOn Found from netvertex session for Core-Session-ID:"+coreSessionID + " fetch addOns from DiameterSessiion");
		
		ISession session =  getTransactionContext().getControllerContext().getStackContext().readOnlySession(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val), diameterRequest.getApplicationID());
		
		if(session == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to fetch addOns in diameter session. Reason: Diameter session not found for Core-Session-ID:"+coreSessionID + " fetch addOns from DiameterSessiion");
		}else {
			String addOnString = (String) session.getParameter(PCRFKeyConstants.CS_ADD_ONS.val);
			
			if(addOnString == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No AddOn Found from diameter session for Core-Session-ID:"+coreSessionID);
			}else {
				addOnString = addOnString.trim();
				
				if(addOnString.isEmpty() == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "AddOn "+ addOnString +" Found from diameter session for Core-Session-ID:"+coreSessionID);
					pcrfRequest.setAttribute(PCRFKeyConstants.CS_ADD_ONS.val, addOnString);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No AddOn Found from diameter session for Core-Session-ID:"+coreSessionID);
				}
				
			}
		}
		
		
		
		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if(requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL){
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}
		
		return TransactionState.WAIT_FOR_AUTH_RES;
	}
	
	private void processRxSessionsRelease(PCRFRequest pcrfRequest) {
		String coreSessionId = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val);
		if(coreSessionId == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: " + PCRFKeyConstants.CS_SESSION_ID.val +" = " + coreSessionId +" not found in PCRFRequest");
			return;
		}
		
		List<SessionData> sessionRules = locateSessionRules(coreSessionId);
		if(sessionRules == null || sessionRules.isEmpty() == true){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: No Rx session rule found with " + PCRFKeyConstants.CS_SESSION_ID.val + " = " + coreSessionId);
			return;
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Rx Session Rules Located");

		List<SessionData> coreSessions = getCoreSessions(sessionRules);
		if(coreSessions == null || coreSessions.isEmpty() == true){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: No Rx core sessions found with " + PCRFKeyConstants.CS_SESSION_ID.val + " = " + coreSessionId);
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Rx Sessions Located");
		
		for(SessionData session : coreSessions){
			sendASR(session);
		}
	}


	/**
	 * Send ASR to core sessions sessions
	 */
	private void sendASR(SessionData session){
			PCRFResponse response = new PCRFResponseImpl();
			PCRFPacketUtil.buildPCRFResponse(session, response);
			response.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.val, PCRFKeyValueConstants.AC_BEARER_RELEASED.val);
			DiameterRequest asrRequest = getTransactionContext().getControllerContext().buildASR(response);
			if(asrRequest == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "ASR sending skipped. Reason: Unable to generate ASR from PCRF Response");
				return;
			}
			getTransactionContext().sendRequest(asrRequest, session.getValue(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), false);
	}
	
	/**
	 * fetch all session rules by SessionLookupVal
	 * @param coreSessionId
	 */
	private List<SessionData> locateSessionRules(String coreSessionId) {
		SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
		Criteria sessionRuleCriteria;
		try {
			sessionRuleCriteria = sessionLocator.getSessionRuleCriteria();
		} catch (SessionException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Skipping ASR Sending. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			return Collections.emptyList();
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Locating Rx Session for " + PCRFKeyConstants.CS_CORESESSION_ID.val + " = " + coreSessionId);
		}

		sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
		return sessionLocator.getSessionRules(sessionRuleCriteria);
	}
	
	/**
	 * fetch core sessions by sessionRules
	 * 
	 * @param sessionRules
	 */
	private List<SessionData> getCoreSessions(List<SessionData> sessionRules){

		SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
		Criteria coreSessionCriteria = null;

		try {
			coreSessionCriteria = sessionLocator.getCoreSessionCriteria();
		} catch (SessionException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Skipping ASR Sending. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return Collections.emptyList();
		}

		List<Criterion> criterions = new ArrayList<Criterion>();
		
		//once ASR is sent to AF, adding in this set 
		Set<String> afSessionIds = new HashSet<String>(sessionRules.size());
		for(SessionData session : sessionRules){
			// if contains AFSessionID then skip it
			if(afSessionIds.add(session.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.getVal())) == false){
				continue;
			}
			criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, session.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.val)));
		}

		if(criterions.isEmpty()) {
			return Collections.emptyList();
		}

		coreSessionCriteria.add(createORCriterian(criterions));
		return sessionLocator.getCoreSessionList(coreSessionCriteria);
	}
	
	/**
	 * createORCriterian returns OR condition which is in Loop ex.(cond1 OR (cond2 OR (cond3 OR (....
	 *  
	 * @param criterions
	 * @return Criterion
	 */
	private Criterion createORCriterian(List<Criterion> criterions){
		Criterion criterion = criterions.get(0);
		// to create (cond1 OR (cond2 OR (cond3 OR (.... chain
		for(int i=1; i<criterions.size(); i++){
			criterion = Restrictions.or(criterion, criterions.get(i));
		}
		return criterion;
	}
}
