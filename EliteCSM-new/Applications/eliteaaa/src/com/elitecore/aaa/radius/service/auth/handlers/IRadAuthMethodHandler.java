package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.core.authprotocol.IAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;

public interface IRadAuthMethodHandler extends IAuthMethodHandler {
	public boolean isEligible (RadAuthRequest request);
	public void handleRequest(RadAuthRequest request,RadAuthResponse response, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException;
	public int getMethodType();
	
}
