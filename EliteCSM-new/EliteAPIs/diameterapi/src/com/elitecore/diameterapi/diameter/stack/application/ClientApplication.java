package com.elitecore.diameterapi.diameter.stack.application;

import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.ApplicationListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public abstract class ClientApplication extends ApplicationListener{

	public ClientApplication(IStackContext stackContext, ApplicationEnum applicationEnum) {
		super(stackContext, applicationEnum);
	}
	
	@Override
	protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		if (diameterRequest.isServerInitiated()) {
			session.setParameter(DiameterSession.SESSION_RELEASE_KEY, diameterRequest.getRequestingHost());
			session.setParameter(DiameterAVPConstants.DESTINATION_HOST, diameterRequest.getRequestingHost());
		}
	}
}
