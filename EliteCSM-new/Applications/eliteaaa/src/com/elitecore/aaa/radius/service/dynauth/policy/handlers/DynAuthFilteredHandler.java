package com.elitecore.aaa.radius.service.dynauth.policy.handlers;

import com.elitecore.aaa.radius.service.base.policy.handler.FilteredHandler;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.handlers.RadDynAuthServiceHandler;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DynAuthFilteredHandler extends FilteredHandler<RadDynAuthRequest, RadDynAuthResponse>
implements RadDynAuthServiceHandler {

	public DynAuthFilteredHandler(String rulesetString,	RadServiceHandler<RadDynAuthRequest, RadDynAuthResponse> handler) {
		super(rulesetString, handler);
	}
}
