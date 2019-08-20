package com.elitecore.aaa.diameter.service.application.listener;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

public class SessionManagementHandler<T extends ApplicationRequest, V extends ApplicationResponse> {
	private static final String MODULE = "SESSION-MGMT-HDLR";
	
	@Nullable private final IDiameterSessionManager diameterSessionManager;
	private final SessionReleaseIndiactor sessionReleaseIndicator;

	public SessionManagementHandler(@Nullable IDiameterSessionManager sessionManager, SessionReleaseIndiactor sessionReleaseIndicator) {
		this.diameterSessionManager = sessionManager;
		this.sessionReleaseIndicator = sessionReleaseIndicator;
	}
	
	public void handleSessionManagement(T applicationRequest,
			V applicationResponse, String sessionStatus) {
		if (diameterSessionManager == null) {
			return;
		}
		if (addSessionStatusAVP(applicationRequest.getDiameterRequest(), sessionStatus) == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Skipping session management, Reason: session status could not be decided for command code: " 
						+ applicationRequest.getDiameterRequest().getCommandCode());
			}
			return;
		}
		doSesionManagement(applicationRequest.getDiameterRequest(), applicationResponse.getDiameterAnswer());
		setApplicationFlagsFromParameter(applicationResponse);
	}
	
	private void setApplicationFlagsFromParameter(V applicationResponse) {
		Boolean parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.MARK_FOR_DROP_REQUEST);
		if(parameter != null && parameter) {
			DiameterProcessHelper.dropResponse(applicationResponse);
		}
		parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.FURTHER_PROCESSING_REQUIRED);
		if (parameter != null) {
			applicationResponse.setFurtherProcessingRequired(parameter);
		}
		parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.PROCESSING_COMPLETED);
		if (parameter != null) {
			applicationResponse.setProcessingCompleted(parameter);
		}
	}

	private boolean addSessionStatusAVP(DiameterRequest diameterRequest, String fetchedSessionStatus) {
		IDiameterAVP ecSessionStatus = diameterRequest.getAVP(DiameterAVPConstants.EC_SESSION_STATUS);
				if(diameterRequest.containsAVP(ecSessionStatus)) {
					return true;
				}
		IDiameterAVP sessionStatus = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SESSION_STATUS);
		if (sessionStatus == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "EC-SESSION-STATUS (" +  DiameterAVPConstants.EC_SESSION_STATUS + ") avp not found in dictionary");
			}
			return false;
		}
		if (Strings.isNullOrBlank(fetchedSessionStatus)) {
			return false;
		}
		sessionStatus.setStringValue(fetchedSessionStatus);
		diameterRequest.addInfoAvp(sessionStatus);
		return true;
	}

	
	private String getModuleName() {
		return MODULE;
	}

	private void doSesionManagement(DiameterRequest diameterRequest,
			DiameterAnswer diameterAnswer) {
		
		LogManager.getLogger().info(getModuleName(), "Applying diameter session management.");
		
		if (sessionReleaseIndicator.isEligible(diameterAnswer)) {
			LogManager.getLogger().info(getModuleName(), "Session is eligible for release so deleting it from session manager.");
			diameterSessionManager.delete(diameterRequest, diameterAnswer);
		} else {
			diameterSessionManager.updateOrSave(diameterRequest, diameterAnswer, diameterRequest.getLocatedSessionData());
		}
		
	}
}
