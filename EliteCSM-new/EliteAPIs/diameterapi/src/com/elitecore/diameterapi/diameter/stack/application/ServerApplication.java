package com.elitecore.diameterapi.diameter.stack.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.ApplicationListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public abstract class ServerApplication extends ApplicationListener {

	private static final String MODULE = "SRV-APP";
	public ServerApplication(IStackContext stackContext, ApplicationEnum... applicationEnum) {
		super(stackContext, applicationEnum);
	}
	
	public ServerApplication(IStackContext stackContext) {
		super(stackContext);
	}
	
	@Override
	protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		if (diameterRequest.isServerInitiated() == false) {
			((DiameterSession)session).setParameter(DiameterSession.SESSION_RELEASE_KEY, diameterRequest.getRequestingHost());
			((DiameterSession)session).setParameter(DiameterAVPConstants.ORIGIN_HOST, diameterRequest.getRequestingHost());
			if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Client initiated request received for session: " + session.getSessionId()
						+ ", setting session release key and origin host to be: " + diameterRequest.getRequestingHost());
			}
		}
	}
	
	@Override
	public void postProcess(DiameterSession session, DiameterAnswer diameterAnswer) {
		super.postProcess(session, diameterAnswer);
		
		if (sessionEligibleToRelease(diameterAnswer)) {
			session.release();
		}
	}
}
