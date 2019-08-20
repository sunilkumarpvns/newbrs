package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SessionHandler {

	private static final String MODULE = "ABSTRACT-SESS-HDLR";
	private SessionOperation sessionOperation;
	private PolicyRepository policyRepository;
	
	public SessionHandler(SessionOperation sessionOperation, PolicyRepository policyRepository) {
		this.sessionOperation = sessionOperation;
		this.policyRepository = policyRepository;
	}
	
	public void handle(PCRFRequest request, PCRFResponse response) {

		String coreSessionId = response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
		if (request.getPCRFEvents().contains(PCRFEvent.GATEWAY_REBOOT) == false) {
			if (coreSessionId == null) {
				if (getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Skipping Session handling. Reason: Session-ID not found");
				return;
			}
		}

		if (request.getPCRFEvents().contains(PCRFEvent.SESSION_START)) {
			createSession(request, response, coreSessionId);
		} else if (request.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE) || request.getPCRFEvents().contains(PCRFEvent.SESSION_RESET)) {
			updateSession(request, response, coreSessionId);
		} else if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
			deleteSession(request, response, coreSessionId);
		} else if (request.getPCRFEvents().contains(PCRFEvent.GATEWAY_REBOOT)) {
			deletingSessionForGateway(request);
		}
	}

	protected void deletingSessionForGateway(PCRFRequest request) {
		String gatewayAddress = request.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal());

		if (gatewayAddress == null) {
			getLogger().error(MODULE, "Skipping session deletion. Reason: CS_GATEWAY_ADDRESS not found");
			return;
		}
		try {
			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Deleting sessions for Gateway: " + gatewayAddress);

			Criteria sessionRuleCriteria = sessionOperation.getSessionRuleCriteria();
			sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
			sessionOperation.deleteSessionRule(sessionRuleCriteria);

			if (getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Deleting session rule for gateway: " + gatewayAddress);

			Criteria coreSessionCriteria = sessionOperation.getCoreSessionCriteria();
			coreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
			sessionOperation.deleteCoreSession(coreSessionCriteria);
		} catch (SessionException e) {
				getLogger().error(MODULE, "Error in deleting core session for CS_GATEWAY_ADDRESS = " + gatewayAddress + ". Reason : "
						+ e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	protected void deleteSession(PCRFRequest request, PCRFResponse response, String coreSessionId) {
		if (getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Deleting core session for CoreSession-ID: " + coreSessionId);

		try {
			sessionOperation.deleteCoreSessionByCoreSessionId(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

			if (SessionTypeConstant.RX.val.equals(response.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))) {
				Criteria sessionRuleCriteria = sessionOperation.getSessionRuleCriteria();
				sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, request
						.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val)));
				sessionOperation.deleteSessionRule(sessionRuleCriteria);
			}
		} catch (SessionException ex) {
			getLogger().error(MODULE, "Error in updating session. Reason : " + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}
	}

	protected void updateSession(PCRFRequest request, PCRFResponse response, String coreSessionId) {
		try {

			deleteSessionRules(response, request);
			if (request.isSessionFound() == true) {
				if (getLogger().isInfoLogLevel())
					getLogger().info(MODULE, "Updating core session for CoreSession-ID: " + coreSessionId);

				sessionOperation.updateCoreSession(response);
			} else {
				if (getLogger().isInfoLogLevel())
					getLogger().info(MODULE, "Creating new core session for CoreSession-ID: " + coreSessionId);
				sessionOperation.createCoreSession(response);
			}

			if (SessionTypeConstant.RX.val.equals(response.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))) {
				Map<SessionManagerOperations, List<AFSessionRule>> installablePCCRules = createInstallableSessionRule(request);
				if (installablePCCRules != null && installablePCCRules.isEmpty() == false) {

					List<AFSessionRule> saveList = installablePCCRules.get(SessionManagerOperations.SAVE);
					if (saveList != null) {
						if (getLogger().isInfoLogLevel())
							getLogger().info(MODULE, "Creating " + saveList.size() + " session rule for CoreSession-ID: " + coreSessionId);
						sessionOperation.createSessionRule(response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.getVal()),
								response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),
								saveList, response);
					}

					List<AFSessionRule> updateList = installablePCCRules.get(SessionManagerOperations.UPDATE);
					if (updateList != null) {
						if (getLogger().isInfoLogLevel())
							getLogger().info(MODULE, "Updating " + updateList.size() + " session rule for CoreSession-ID: " + coreSessionId);
						sessionOperation.updateSessionRule(response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.getVal()),
								response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),
								updateList, response);
					}

					List<AFSessionRule> deleteList = installablePCCRules.get(SessionManagerOperations.DELETE);
					if (deleteList != null) {
						if (getLogger().isInfoLogLevel())
							getLogger().info(MODULE, "Deleting " + deleteList.size() + " session rule for CoreSession-ID: " + coreSessionId);
						sessionOperation.deleteSessionRule(response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.getVal()),
								response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),
								deleteList, response);
					}
				}
			}

		} catch (SessionException ex) {
			getLogger().error(MODULE, "Error in updating session. Reason : " + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}
	}

	protected void createSession(PCRFRequest request, PCRFResponse response, String coreSessionId) {
		if (getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Creating new core session for CoreSession-ID: " + coreSessionId);

		sessionOperation.createCoreSession(response);

		if (SessionTypeConstant.RX.val.equals(response.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))) {
			Map<SessionManagerOperations, List<AFSessionRule>> installablePCCRules = createInstallableSessionRule(request);
			if (installablePCCRules != null && installablePCCRules.isEmpty() == false) {
				if (getLogger().isInfoLogLevel())
					getLogger().info(MODULE, "Creating session rule for CoreSession-ID: " + coreSessionId);

				List<AFSessionRule> saveList = installablePCCRules.get(SessionManagerOperations.SAVE);
				if (saveList != null) {
					if (getLogger().isInfoLogLevel())
						getLogger().info(MODULE, "Creating " + saveList.size() + " session rule for CoreSession-ID: " + coreSessionId);
					sessionOperation.createSessionRule(response.getAttribute(PCRFKeyConstants.GX_SESSION_ID.val),
							response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),
							saveList, response);
				}
			}
		}
	}

	/**
	 * deleteSessionRule delete session rule based on removable pcc rules
	 * @param response
	 * @throws SessionException
	 */
	private void deleteSessionRules(PCRFResponse response, PCRFRequest pcrfRequest) throws SessionException{
		
		Map<String, String> activePCCRuleIdToSubscriptions = response.getActivePccRules();
		if(Maps.isNullOrEmpty(activePCCRuleIdToSubscriptions)){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "flush active PCCRules. Reason: No active PCCRule Found");
			return;
		}
		
		if(pcrfRequest.getRemovedPCCRules() == null || pcrfRequest.getRemovedPCCRules().isEmpty() == true){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Skipping session rule deletion. Reason: No removable pcc rules found");
			return;
		}
		
		List<String> inActivePCCRules = pcrfRequest.getRemovedPCCRules();
		
		Iterator<Entry<String, String>> activePCCiterator = activePCCRuleIdToSubscriptions.entrySet().iterator();
		
		/* 
		 * remove pcc reported to remove in request from active pcc str
		 */
		while (activePCCiterator.hasNext()) {
			
			Entry<String, String> pccIdToSubscriptionEntry = activePCCiterator.next();
			
			PCCRule pccRule = policyRepository.getPCCRuleById(pccIdToSubscriptionEntry.getKey());
			if (pccRule == null) {
				if(getLogger().isWarnLogLevel())
					getLogger().warn(MODULE, "Removing pcc rule("+ pccIdToSubscriptionEntry.getKey() +") from active pcc rules. Reason: pcc rule not found");
				
				activePCCiterator.remove();
				continue;
			}
			
			if (inActivePCCRules.contains(pccRule.getName())) {
				activePCCiterator.remove();
			}
		}
		
		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Deleting session rule for CoreSession-ID: " + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
					
		Criteria sessionRuleCriteria = sessionOperation.getSessionRuleCriteria();
		sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal())));
		sessionRuleCriteria.add(getCriterion(inActivePCCRules, 0));
		sessionOperation.deleteSessionRule(sessionRuleCriteria);
	}
	
	/**
	* getCriteria return or condition which is in loop ex.(cond1 OR (cond2 OR (cond3 OR (....
	* @param pccRules
	* @param index
	* @return Criterion
	*/
	private Criterion getCriterion(List<String> pccRules, int index){
		if(index == pccRules.size()-1)
			return Restrictions.eq(PCRFKeyConstants.PCC_RULE_LIST.getVal(),pccRules.get(index));
		else{
			return Restrictions.or(Restrictions.eq(PCRFKeyConstants.PCC_RULE_LIST.getVal(),pccRules.get(index)), getCriterion(pccRules, index+1));
	 	}
	 }
	
	
	private Map<SessionManagerOperations, List<AFSessionRule>> createInstallableSessionRule(PCRFRequest pcrfRequest) {
		
		Map<SessionManagerOperations, List<AFSessionRule>> operationToAfSessionRules = new EnumMap<>(SessionManagerOperations.class);
		
		List<MediaComponent> mediaComponents = pcrfRequest.getMediaComponents();
		for(int i=0; i < mediaComponents.size(); i++) {
			mediaComponents.get(i).add(operationToAfSessionRules);
		}
		
		return operationToAfSessionRules;
		
	}
}
