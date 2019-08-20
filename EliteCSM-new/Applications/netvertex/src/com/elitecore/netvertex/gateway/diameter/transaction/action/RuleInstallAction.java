package com.elitecore.netvertex.gateway.diameter.transaction.action;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

/*
 * Transaction User This Action
 * ============================
 * 	NewServiceTransaction
 */
public class RuleInstallAction extends ActionHandler {
	private static final String NAME = "RULE-INSTALL";
	private static final String MODULE = "RLE-INSTL";
	
	public RuleInstallAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		PCRFResponse response = getTransactionContext().getTransactionSession().get(SessionKeys.PCRF_RESPONSE);
		
		if(response == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "PCRFResponse is not found. skipping further processing");
			return TransactionState.COMPLETE;
		}
		
		String resultCode = PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val;
		
		
		/*
		 * According to 3gpp TS 29.214
		 * 
		 * The AF may additionally provide preliminary service information not 
		 * fully negotiated yet (e.g. based on the SDP offer) at an earlier stage. 
		 * To do so, the AF shall include the Service-Info-Status AVP with the value 
		 * set to PRELIMINARY SERVICE INFORMATION. Upon receipt of such preliminary 
		 * service information, the PCRF shall perform an early authorization check 
		 * of the service information. For GPRS, the PCRF shall not provision PCC 
		 * rules towards the PCEF unsolicited.
		 */
		if(PCRFKeyValueConstants.SERVICE_INFO_STATUS_PRELIMINARY_SERVICE_INFORMATION.val.equalsIgnoreCase(response.getAttribute(PCRFKeyConstants.SERVICE_INFO_STATUS.val)) == false) {
			setInstallableAndRemovaleRule(response);
			
			//when installable or removable pcc rule is not found skipping sending RAR
			if((response.getInstallablePCCRules() == null || response.getInstallablePCCRules().isEmpty()) && 
					(response.getRemovablePCCRules() == null   || response.getRemovablePCCRules().isEmpty())){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "RAR generation skipped. Reason: " +
							"No pcc rule found to be Installed or Removed at Gx gateway");
				
			} else {
				if(sendRAR(response)){
	        		return TransactionState.WAIT_RULE_INSTALL_ACK;
	        	} else {
	        		resultCode = PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val;
	        	}
			}
        	
		}
		
		response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, resultCode);
		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		getTransactionContext().getControllerContext().buildAAA(response, answer);		
		getTransactionContext().sendAnswer(answer, diameterRequest);
		
		return TransactionState.COMPLETE;
	}
	
	/**
	 * sendRAR send RAR if response contains any removable or installable pcc-rules
	 * @param response
	 * @return
	 */
	private boolean sendRAR(PCRFResponse response) {
		
		SessionData gxSession = getTransactionContext().getTransactionSession().get(SessionKeys.GX_SESSION);
		if(gxSession == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "RAR generation skipped. Reason: " +
						"Gx Session not found from Transaction");
			}
			return false;
		}
		
		// created pcrfresponse to build RAR Request
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		PCRFRequest pcrfRequest = new PCRFRequestImpl();
		
		PCRFPacketUtil.buildPCRFResponse(gxSession, pcrfResponse);
		PCRFPacketUtil.buildPCRFRequest(gxSession, pcrfRequest);
		
		pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
		pcrfResponse.setInstallablePCCRules(response.getInstallablePCCRules());
		pcrfResponse.setRemovablePCCRules(response.getRemovablePCCRules());
		pcrfResponse.setSessionTimeOut(response.getSessionTimeOut());
		pcrfResponse.setRevalidationTime(response.getRevalidationTime());
		
		DiameterRequest rarRequest = getTransactionContext().getControllerContext().buildRAR(pcrfResponse);
		
		if(rarRequest != null){
			if(getTransactionContext().sendRequest(rarRequest, gxSession.getValue(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), true)){
				
				getTransactionContext().getTransactionSession().put(SessionKeys.GX_PCRF_REQUEST, pcrfRequest);
				getTransactionContext().getTransactionSession().put(SessionKeys.GX_PCRF_RESPONSE, pcrfResponse);
				
				return true;
			}
		} else {
			getLogger().error(MODULE, "diameter request is not found for sending RAR. skipping further processing");
		}
		
		return false;
	}

	private void setInstallableAndRemovaleRule(PCRFResponse response) {
		List<MediaComponent> mediaComponents = response.getMediaComponents();
		
		Map<String, List<MediaComponent>> serviceToMediaComponents = new HashMap<String, List<MediaComponent>>();
		for(int i=0; i<mediaComponents.size(); i++) { 
			MediaComponent mediaComponent = mediaComponents.get(i);
			List<MediaComponent> serviceMediaComponents = serviceToMediaComponents.get(mediaComponent.getMediaType());
			if(serviceMediaComponents == null) {
				serviceMediaComponents = new ArrayList<MediaComponent>();
				serviceToMediaComponents.put(mediaComponent.getMediaType(), serviceMediaComponents);
			}
			serviceMediaComponents.add(mediaComponent);
		}
	
		List<PCCRule> installablePCCRules = new ArrayList<PCCRule>();
		List<String> removablePCCRules = new ArrayList<String>();
		
		for(List<MediaComponent> serviceMediaComponents : serviceToMediaComponents.values()) {
			
			if(serviceMediaComponents.size() == 1) {
				MediaComponent mediaComponent = serviceMediaComponents.get(0);
				mediaComponent.addRemovablePCCRule(removablePCCRules);
				
				if(mediaComponent.getFlowStatus() != FlowStatus.REMOVED) {
					mediaComponent.addInstallablePCCRules(installablePCCRules);
				}
			} else {
				for(int i=0; i < serviceMediaComponents.size(); i++) {
					
					MediaComponent mediaComponent = serviceMediaComponents.get(i);
					mediaComponent.addRemovablePCCRule(removablePCCRules);
					if(mediaComponent.getFlowStatus() != FlowStatus.REMOVED) {
						mediaComponent.addInstallablePCCRules(installablePCCRules);
					}
					
				}
			}
			
		}
		
		response.setInstallablePCCRules(installablePCCRules);
		response.setRemovablePCCRules(removablePCCRules);
	}

}
