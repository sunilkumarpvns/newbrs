package com.elitecore.aaa.core.wimax;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;

public interface WimaxRequestHandler {
	public void handleRequest(WimaxRequest request,WimaxResponse response,WimaxSessionData wimaxSessionData) throws AuthorizationFailedException;
	public boolean isEligible(WimaxRequest request,WimaxResponse response); 

}
