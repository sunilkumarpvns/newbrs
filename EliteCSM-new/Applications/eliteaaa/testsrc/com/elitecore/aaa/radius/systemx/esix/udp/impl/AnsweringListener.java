package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class AnsweringListener implements RadResponseListener {

	private static final String MODULE = "DUMMY-RESPONSE-LISTENER";

	@Override
	public void responseReceived(RadUDPRequest radUDPRequest, RadUDPResponse radUDPResponse, ISession session) {
		LogManager.getLogger().warn(MODULE, "Response is received.");
	}

	@Override
	public void requestDropped(RadUDPRequest radUDPRequest) {
		LogManager.getLogger().warn(MODULE, "Request is time out.");
		
	}

	@Override
	public void requestTimeout(RadUDPRequest radUDPRequest) {
		LogManager.getLogger().warn(MODULE, "Request is dropped.");
	}

}
