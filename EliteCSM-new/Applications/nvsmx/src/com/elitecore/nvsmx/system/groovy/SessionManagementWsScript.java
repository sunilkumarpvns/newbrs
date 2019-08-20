package com.elitecore.nvsmx.system.groovy;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthByCoreSessionIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthBySubscriberIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionReAuthResponse;

public abstract class SessionManagementWsScript {

	private static final String MODULE = "SESSION-WS-SCRIPT";

	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "SessionManagementWs script(" + getName() + ") initialized successfully");
		}
	}
	
	abstract public String getName();

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

	public void preReAuthBySubscriberId(SessionReAuthBySubscriberIdRequest sessionReAuthRequest) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preReAuthBySubscriberId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void postReAuthBySubscriberId(SessionReAuthBySubscriberIdRequest sessionReAuthRequest, SessionReAuthResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postReAuthBySubscriberId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void preReAuthByCoreSessionId(SessionReAuthByCoreSessionIdRequest sessionReAuthByCoreSessionRequest) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preReAuthByCoreSessionId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}

	public void postReAuthByCoreSessionId(SessionReAuthByCoreSessionIdRequest sessionReAuthByCoreSessionRequest, SessionReAuthResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postReAuthByCoreSessionId of SessionManagementWsScript(" + getName() + ") is called");
		}
	}
}
