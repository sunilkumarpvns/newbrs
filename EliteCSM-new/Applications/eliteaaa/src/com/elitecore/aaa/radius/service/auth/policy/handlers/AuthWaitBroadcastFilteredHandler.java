package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

 /**
 * In broadcasting context, if we have some entries with wait for response flag true, then 
 * this filtered handler is used. So that if any of the filters is selected then it will
 * halt the request execution.
 * 
 * <p>Wait for response is used when the response from the external system is to be aggregated,
 * to achieve that we need to halt the further request processing.
 * 
 * @author narendra.pathai
 */
public class AuthWaitBroadcastFilteredHandler extends AuthFilteredHandler {

	public AuthWaitBroadcastFilteredHandler(String rulesetString,
			RadAuthServiceHandler handler) {
		super(rulesetString, handler);
	}
	
	@Override
	public void handleAsyncRequest(
			RadAuthRequest request,
			RadAuthResponse response,
			ISession session,
			BroadcastResponseListener<RadAuthRequest, RadAuthResponse> listener) {
		/*
		 * In case of broadcast with wait communication if any filter is satisfied then further
		 * processing needs to be halted, so we are halting the processing. If no wait filter is
		 * selected then request will not be halted and further handler execution will proceed.
		 */
		RadiusProcessHelper.onExternalCommunication(request, response);
		super.handleAsyncRequest(request, response, session, listener);
	}
}
