package com.elitecore.aaa.diameter.applications.commons;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

public class DiameterSessionLocationHandler<T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V> {


	private static final String MODULE = "DIA-SESS-LOC-HNDLR";
	private @Nonnull DiameterSessionManager diameterSessionManager;

	public DiameterSessionLocationHandler(
			@Nonnull DiameterSessionManager diameterSessionManager) {
		this.diameterSessionManager = diameterSessionManager;
	}

	@Override
	public void init() throws InitializationFailedException {

	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		List<SessionData> sessions = locateSession(request.getDiameterRequest(), response.getDiameterAnswer());
		setApplicationFlagsFromParameter(response);
		
		if (sessions == null) {
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, sessions.size() + " session(s) located");
		}
		
		Collections.sort(sessions);
		request.getDiameterRequest().setLocatedSessionData(sessions);
	}
	private void setApplicationFlagsFromParameter(V applicationResponse) {
		Boolean parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.MARK_FOR_DROP_REQUEST);
		if(parameter != null && parameter) {
			applicationResponse.markForDropRequest();
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


	private List<SessionData> locateSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Locating session for Diameter Packet with HbH-ID = " 
					+ diameterRequest.getHop_by_hopIdentifier() + " and EtE-ID = " + diameterRequest.getEnd_to_endIdentifier());
		}
		return diameterSessionManager.locate(diameterRequest, diameterAnswer);
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
