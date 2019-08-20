package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.OrderedAsyncRequestExecutor;

public class AuthNoWaitForResponseBroadcastResponseListener implements BroadcastResponseListener<RadAuthRequest, RadAuthResponse> {
	private static final String MODULE = "AUTH-BROADCAST-NO-WAIT-RES-LSTNR";

	@Override
	public void responseReceived(RadUDPRequest radUDPRequest, RadUDPResponse radUDPResponse, ISession session) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Response received: " + radUDPResponse);
		}
	}

	@Override
	public void requestDropped(RadUDPRequest radUDPRequest) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Response dropped for request: " + radUDPRequest);
		}
	}

	@Override
	public void requestTimeout(RadUDPRequest radUDPRequest) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Request timedout: " + radUDPRequest);
		}
	}

	@Override
	public void addAsyncRequestExecutor(OrderedAsyncRequestExecutor<RadAuthRequest, RadAuthResponse> executor) {
		// no-op
	}

	@Override
	public int getNextOrder() {
		return 0; //no need for maintaining ordering here
	}
}
