package com.elitecore.aaa.core.authprotocol;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;

public interface IAuthenticationMethodHandler {
	public boolean isEligible (RadAuthRequest request);
	public void handleRequest(RadAuthRequest request,RadAuthResponse response, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException;
}
