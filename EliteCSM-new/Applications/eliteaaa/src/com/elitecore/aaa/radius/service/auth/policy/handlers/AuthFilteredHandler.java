package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.FilteredHandler;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthFilteredHandler extends FilteredHandler<RadAuthRequest, RadAuthResponse>
implements RadAuthServiceHandler {

	public AuthFilteredHandler(String rulesetString, RadAuthServiceHandler handler) {
		super(rulesetString, handler);
	}
}
