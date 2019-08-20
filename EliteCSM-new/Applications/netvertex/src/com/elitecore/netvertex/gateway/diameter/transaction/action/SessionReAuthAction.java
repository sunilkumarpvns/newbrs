package com.elitecore.netvertex.gateway.diameter.transaction.action;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

public class SessionReAuthAction extends ActionHandler{

	public static final String MODULE= "SESS-RE-AUTH-ACTN";
	private static final String NAME = "SESSIONREAUTH";
	
	public SessionReAuthAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public TransactionState handle() {
		PCRFResponse response = getPCRFResponse();
		
		if(response.isMarkedForDropRequest()){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "RAR generation skipped. Reason: PCRF Response dropped");
			}
			return TransactionState.COMPLETE;
		}
		
		if (isEligibleToSendRAR(response) == false) {
			return TransactionState.COMPLETE;
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "PCRFResponse for sending RAR " + response);
		
		DiameterRequest request = getTransactionContext().getControllerContext().buildRAR(response);
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE," RAR request "+request);

		boolean result = getTransactionContext().sendRequest(request, response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), true);
		
		if(getTransactionContext().getControllerContext().isCiscoGxEnabled() &&
				(response.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal()) != null)){
			sendSessDisconnReqToCiscoGx(response);
		}

		/// if RAR successfully send then only wait for RAA
		if (result) {
			return TransactionState.WAIT_FOR_RAA;
		}
		
		return TransactionState.COMPLETE;
	}

	private boolean isEligibleToSendRAR(PCRFResponse response) {
		
		if(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equalsIgnoreCase(response.getAttribute(PCRFKeyConstants.RESULT_CODE.val))){
			
			/* This flag will be TRUE, when Session Auth Rule changed for Subscriber Package or BoD Package.
			 * isPolicyChanged() states , whether session Auth rule is changed or not, from PolicyGroup and BoD (not from AddOns).  
			 */
			if(PCRFKeyValueConstants.FORCEFUL_SESSION_RE_AUTH.val.equalsIgnoreCase(response.getAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val))){	
				if(getLogger().isDebugLogLevel()){
					getLogger().debug(MODULE, "Generating RAR. Reason: Forceful session re-authorization for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}

			}else if(response.isPolicyChanged()){

				if(getLogger().isDebugLogLevel()){
					getLogger().debug(MODULE, "Generating RAR. Reason: Change in policy for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}

			}else{

				if(getLogger().isDebugLogLevel()){
					getLogger().debug(MODULE, "Policy not changed for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
				}
				
				/* AddOn changes will be determined by checking Installable or Removable PCC Rules.
				 * Here, it is assumed that at least one PCC Rule(s) must be present in AddOn Session Auth Rule.
				 */
				
				if((response.getInstallablePCCRules() == null || response.getInstallablePCCRules().isEmpty()) 
						&& (response.getRemovablePCCRules() == null || response.getRemovablePCCRules().isEmpty())){
					if(getLogger().isLogLevel(LogLevel.INFO)){
						getLogger().info(MODULE, "RAR packet generation skipped. Reason: Installable and Removable PCC Rules not found for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
					}
					return false;
				}else{
					if(getLogger().isDebugLogLevel()){
						getLogger().debug(MODULE, "Generating RAR. Reason: Installable or Removable PCC Rules found for Subscriber Identity: " + response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
					}
				}
			}
		} else {
			if(response.getAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal()) == null) {
					response.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal(), PCRFKeyValueConstants.SRC_UE_SUBSCRIPTION_REASON.val);
			}
		}
		
		return true;
	}

	/**
	 * Locating cisco gx session for sending Abort-Session Request for session reauthorization
	 * @param response
	 */
	private void sendSessDisconnReqToCiscoGx(PCRFResponse response) {
		
		String sessionIPv4 = response.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal());
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Locating cisco gx session on Session-IPv4 = " + sessionIPv4);
		
		DiameterGatewayControllerContext context = getTransactionContext().getControllerContext();
		SessionLocator locator = context.getSessionLocator();
		Criteria ciscoGxcriteria = null;
		try {
			ciscoGxcriteria = locator.getCoreSessionCriteria();			
		} catch (SessionException sessEx) {
			getLogger().error(MODULE, "Error in locating cisco gx session. Reason: " + sessEx.getMessage());
			getLogger().trace(MODULE, sessEx);
			return;
		}
		
		ciscoGxcriteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.getVal(), response.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal())));
		List<SessionData> sessions = locator.getCoreSessionList(ciscoGxcriteria);
		
		
		if(sessions == null || sessions.isEmpty()){
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "No cisco gx session located on SessionIPv4 = " + sessionIPv4);
			return;
		}
		
		boolean isCiscoGxSessionFound = false;
		
		for(SessionData session : sessions){
			if(SessionTypeConstant.CISCO_GX.getVal().equals(session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))){
				isCiscoGxSessionFound = true;
				PCRFRequest pcrfRequest = new PCRFRequestImpl();
				PCRFPacketUtil.buildPCRFRequest(session, pcrfRequest);
				context.sendASRtoCiscoGx(pcrfRequest);
			}else{
				continue;
			}
		}
		
		if(isCiscoGxSessionFound == false){
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "No cisco gx session located on SessionIP = " + sessionIPv4);
			return;
		}
	}

}