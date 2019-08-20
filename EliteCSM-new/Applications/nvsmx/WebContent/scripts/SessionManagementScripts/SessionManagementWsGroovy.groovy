package com.elitecore.nvsmx.system.groovy;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;

public class SessionManagementWsGroovy extends SessionManagementWsScript{

	private static final String MODULE = "SESSION-MGMT-WS-GRY";
	
	@Override
	public String getName() {
		return MODULE;
	}

	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "SessionManagementWs script(" + getName() + ") initialized successfully");
		}
	}
	

	public void preListSessionsBySubscriberId(SessionQueryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListSessionsBySubscriberId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void postListSessionsByIP(SessionQueryRequest request, SessionQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListSessionsBySubscriberId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void preListSessionsByIP(SessionQueryByIPRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListSessionsByIP of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void postListSessionsByIP(SessionQueryByIPRequest request, SessionQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListSessionsByIP of SessionManagementWsScript(" + getName() + ") is called");
		}
	}
}
