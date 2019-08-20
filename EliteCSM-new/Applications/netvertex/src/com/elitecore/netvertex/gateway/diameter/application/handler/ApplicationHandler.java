package com.elitecore.netvertex.gateway.diameter.application.handler;


import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;

public interface ApplicationHandler {
	
	public void handleReceivedRequest(Session session, DiameterRequest diameterRequest);

	void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer);

	public void handleTimeoutRequest(Session session, DiameterRequest diameterRequest);
	
	default public void handleReceivedRequest(Session session, DiameterRequest diameterRequest, DiameterAnswerListener diameterAnswerListener) {
		handleReceivedRequest(session, diameterRequest);
	}

}
