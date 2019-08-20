package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.authprotocol.IAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;

public interface DiameterAuthMethodHandler extends IAuthMethodHandler{
	public boolean isEligible (ApplicationRequest request);
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException;
}
