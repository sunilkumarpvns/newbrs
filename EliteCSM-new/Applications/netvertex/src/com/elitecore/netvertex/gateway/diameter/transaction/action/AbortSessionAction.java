package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
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
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/*
 * Transaction that use this Action
 * ================================
 *  - Rule-Remove Transaction
 */
/**
 * AbortSessionAction fetch sessionRule based on CoreSession-ID and PCCRuleName which is found to be inactive. 
 * If Rx-Sessions is found we send ASR to each AF.
 * AbortSessionAction submit PCRFRequest to PCRFService
 * @author harsh
 *
 */
public class AbortSessionAction extends ActionHandler{
	
	private static final String NAME = "ABORT-SESSION";
	private static final String MODULE = "ABRT-SESN";
	
	public AbortSessionAction(DiameterTransactionContext transactionContext) {
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
		
		if(getTransactionContext().getControllerContext().isSubscrberRoming(diameterRequest)){
			pcrfRequest.setAttribute(PCRFKeyConstants.SUBSCRIBER_ROAMING.val, "true");
		}
		
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "PCRFRequest created " + pcrfRequest);
		}
		
		if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Skipping further processing. Reason: CoreSession-ID not found in PCRFRequest for Session-ID = "
							+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
			
			return TransactionState.COMPLETE; 
		}
		
		List<String> removedPCCRuleNames = fetchRemovablePCCRulesFromPacket(diameterRequest);
		
		
		String ruleName = pcrfRequest.getAttribute(PCRFKeyConstants.IN_ACTIVE_PCC_RULE_NAME.getVal());
		
		if(ruleName != null && !removedPCCRuleNames.contains(ruleName)){
			removedPCCRuleNames.add(ruleName);
		}
		
		if(!removedPCCRuleNames.isEmpty()){

			pcrfRequest.setRemovedPCCRules(removedPCCRuleNames);
			if(getTransactionContext().getControllerContext().isRxInterfaceEnable()){
				List<String> rxSessionIds = locateSessionRule(pcrfRequest);
				if(rxSessionIds != null && !rxSessionIds.isEmpty()){
					sendASR(rxSessionIds);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: No Rx session rule found linked with Gx Session-Id = " 
									+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: Rx interface is disabled");
			}
		
		}


		List<String> removedChargingRuleBaseNames = fetchRemovableChargingRuleBaseNamesFromPacket(diameterRequest);


		String chargingRuleBaseName = pcrfRequest.getAttribute(PCRFKeyConstants.IN_ACTIVE_PCC_RULE_NAME.getVal());

		if(chargingRuleBaseName != null && !removedChargingRuleBaseNames.contains(chargingRuleBaseName)){
			removedChargingRuleBaseNames.add(chargingRuleBaseName);
		}

		if(!removedChargingRuleBaseNames.isEmpty()){

			pcrfRequest.setRemovedChargingRuleBaseNames(removedChargingRuleBaseNames);
			if(getTransactionContext().getControllerContext().isRxInterfaceEnable()){
				List<String> rxSessionIds = locateSessionRule(pcrfRequest);
				if(rxSessionIds != null && !rxSessionIds.isEmpty()){
					sendASR(rxSessionIds);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: No Rx session rule found linked with Gx Session-Id = "
								+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: Rx interface is disabled");
			}

		}
		
		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if(RequestStatus.SUBMISSION_SUCCESSFUL != requestStatus){
			
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}
		
		return TransactionState.WAIT_FOR_AUTH_RES;
	}
	
	
	private List<String> fetchRemovablePCCRulesFromPacket(DiameterRequest diameterRequest) {

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Check for Charging-Rule-Report(" + DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP to find removable pccRule from DiameterPacket");

		List<String> removedPCCRuleNames = new ArrayList<String>();
		if (diameterRequest.getApplicationID() == ApplicationIdentifier.TGPP_S9.getApplicationId()) {
			IDiameterAVP sessionEnforcementAVP = diameterRequest.getAVP(DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO);
			if (sessionEnforcementAVP == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "SessionEnforcementInfo(" + DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO + ") AVP not found from packet");
				}

				return removedPCCRuleNames;
			}

			List<IDiameterAVP> chargingRuleReportAVPs = ((AvpGrouped) sessionEnforcementAVP).getSubAttributeList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
			if (Collectionz.isNullOrEmpty(chargingRuleReportAVPs)) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Charging-Rule-Report(" + DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP not found from SessionEnforcementInfo("
							+ DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO + ") AVP ");
				}

				return removedPCCRuleNames;

			}

			addRemovedPCCs(removedPCCRuleNames, chargingRuleReportAVPs);
		} else {
			List<IDiameterAVP> chargingRuleReportAVPs = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
			if (Collectionz.isNullOrEmpty(chargingRuleReportAVPs)) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Charging-Rule-Report(" + DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP not found from packet");
				}

				return removedPCCRuleNames;

			}

			addRemovedPCCs(removedPCCRuleNames, chargingRuleReportAVPs);
		}
		return removedPCCRuleNames;
	}

	private void addRemovedPCCs(List<String> removedPCCRuleNames, List<IDiameterAVP> chargingRuleReportAVPs) {
		for(IDiameterAVP diameterAVP : chargingRuleReportAVPs){
            AvpGrouped chargingRuleReportAVP = (AvpGrouped) diameterAVP;
            IDiameterAVP chargingRuleStatusAVP = chargingRuleReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_PCC_RULE_STATUS);
            if(chargingRuleStatusAVP != null && chargingRuleStatusAVP.getInteger() == DiameterAttributeValueConstants.TGPP_PCC_RULE_STATUS_INACTIVE){
                IDiameterAVP pccRuleName = chargingRuleReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_NAME);
                if(pccRuleName != null){
                    removedPCCRuleNames.add(pccRuleName.getStringValue());
                }

            }

        }
	}


	private List<String> fetchRemovableChargingRuleBaseNamesFromPacket(DiameterRequest diameterRequest){

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Check for Charging-Rule-Report("+DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP to find removable ChargingRuleBaseNames from DiameterPacket");

		List<String> removedChargingRuleBaseNames = new ArrayList<String>();
		if(diameterRequest.getApplicationID() == ApplicationIdentifier.TGPP_S9.getApplicationId()){
			IDiameterAVP sessionEnforcementAVP = diameterRequest.getAVP(DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO);
			if(sessionEnforcementAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "SessionEnforcementInfo(" + DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO + ") AVP not found from packet");
				}

				return removedChargingRuleBaseNames;
			}

			List<IDiameterAVP> chargingRuleReportAVPs = ((AvpGrouped)sessionEnforcementAVP).getSubAttributeList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
			if(Collectionz.isNullOrEmpty(chargingRuleReportAVPs)){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Charging-Rule-Report(" + DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP not found from SessionEnforcementInfo("
							+ DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO + ") AVP ");
				}
				return removedChargingRuleBaseNames;
			}

			addRemovedCRBNs(removedChargingRuleBaseNames, chargingRuleReportAVPs);
		} else {
			List<IDiameterAVP> chargingRuleReportAVPs = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
			if(Collectionz.isNullOrEmpty(chargingRuleReportAVPs)){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Charging-Rule-Report(" + DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT + ") AVP not found from packet");
				}
				return removedChargingRuleBaseNames;
			}
			addRemovedCRBNs(removedChargingRuleBaseNames, chargingRuleReportAVPs);
		}
		return removedChargingRuleBaseNames;
	}

	private void addRemovedCRBNs(List<String> removedChargingRuleBaseNames, List<IDiameterAVP> chargingRuleReportAVPs) {
		for(IDiameterAVP diameterAVP : chargingRuleReportAVPs){
            AvpGrouped chargingRuleReportAVP = (AvpGrouped) diameterAVP;
            IDiameterAVP chargingRuleStatusAVP = chargingRuleReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_PCC_RULE_STATUS);
            if(chargingRuleStatusAVP != null && chargingRuleStatusAVP.getInteger() == DiameterAttributeValueConstants.TGPP_CHARGING_RULE_BASE_NAME_STATUS_INACTIVE){
                IDiameterAVP chargingRuleBaseName = chargingRuleReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_CHARGING_RULE_BASE_NAME);
                if(chargingRuleBaseName != null){
                    removedChargingRuleBaseNames.add(chargingRuleBaseName.getStringValue());
                }

            }

        }
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
	
	/**
	 * getCriteria return or condition which is in loop ex.(cond1 OR (cond2 OR (cond3 OR (....
	 * @param coreSessIds
	 * @param index
	 * @return Criterion
	 */
	private Criterion getCriterionOnCoreSessionId(List<String> coreSessIds, int index){
		if(index == coreSessIds.size()-1)
			return Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.getVal(),coreSessIds.get(index));
		else{
			return Restrictions.or(Restrictions.eq(PCRFKeyConstants.PCC_RULE_LIST.getVal(),coreSessIds.get(index)), getCriterion(coreSessIds, index+1));
		}
	}
	
	//Locate session rule to find affected AF Sessions
	private List<String> locateSessionRule(PCRFRequest pcrfRequest){ 
		SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
		Criteria sessRuleCriteria;
		try{
			
			sessRuleCriteria = sessionLocator.getSessionRuleCriteria();
			
		} catch (SessionException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Unable to locate session rule. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return null;
		}
		
		
		String coreSessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
		sessRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), coreSessionID));
		sessRuleCriteria.add(getCriterion(pcrfRequest.getRemovedPCCRules(), 0));
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, new StringBuffer().append("Locating Session Rule for CoreSessionID = ").
											append(coreSessionID).append(" And PCCRule = ").append(pcrfRequest.getRemovedPCCRules()).toString());
		String hint = getTransactionContext().getControllerContext().getServerContext().getServerConfiguration().
				getMiscellaneousParameterConfiguration().
				getParameterValue(MiscellaneousConfiguration.SESSIONRULE_CORESESSIONID_AND_PCCRULE);
		if(Strings.isNullOrBlank(hint) == false){
			sessRuleCriteria.setHint(hint);
		}
	
		
		List<SessionData> sessRuleSessions = sessionLocator.getSessionRules(sessRuleCriteria);
		
		if(sessRuleSessions == null || sessRuleSessions.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "No session rule exist for removable PCC Rule with CoreSession-ID = " + coreSessionID);
			return null;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, " Session rule Located");
		
		List<String> rxSessions = new ArrayList<String>(sessRuleSessions.size());
		for(SessionData sessionRule : sessRuleSessions){
			String afSessionID = sessionRule.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.getVal());
			if(afSessionID != null && !rxSessions.contains(afSessionID)){
				rxSessions.add(afSessionID);
			}
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Total " + rxSessions.size() + " session rule found for Rx");
		
		return rxSessions;
	}
	
	/**
	 * sendASR to AF found in rxGatewas
	 * @param rxSessions
	 */
	private void sendASR(List<String> rxSessions){
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Locating Rx Sessions");
		
		SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
		Criteria coreCriteria;
		
		try { 
			coreCriteria = sessionLocator.getCoreSessionCriteria();
		} catch (SessionException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Skipping ASR Sending.Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return;
		}
		
		
		coreCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.RX.getVal()));
		coreCriteria.add(getCriterionOnCoreSessionId(rxSessions, 0));
		List<SessionData>  coreSessions = sessionLocator.getCoreSessionList(coreCriteria);
		
		if(coreSessions == null || coreSessions.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping ASR sending. Reason: No Rx session rule found");
			return;
		}			

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Rx Sessions Located.");
		
		//Send ASR for each Rx gateways..
		for(SessionData session : coreSessions){
			PCRFResponse response = new PCRFResponseImpl();
			PCRFPacketUtil.buildPCRFResponse(session, response);
			
			response.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal(), PCRFKeyValueConstants.AC_INSUFFICIENT_BEARER_RESOURCES.val);
			
			DiameterRequest asrRequest = getTransactionContext().getControllerContext().buildASR(response);
			if(asrRequest == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "ASR sending skipped. Reason : Can't build ASR from PCRF Response");
				return;
			}
			getTransactionContext().sendRequest(asrRequest, session.getValue(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), false);
			
		}
	
	}

}
