package com.elitecore.aaa.core.wimax;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.SerciceType;

public class AuthorizeOnlyRequestHandler implements WimaxRequestHandler{

	private static final String MODULE = "AUTHORIZE-ONLY-HANDLER";
	
	public AuthorizeOnlyRequestHandler() {
		
	}
	
	@Override
	public void handleRequest(WimaxRequest request, WimaxResponse response,WimaxSessionData wimaxSessionData) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Handling Authorize-only type of request.");
		
	}

	@Override
	public boolean isEligible(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		int serviceType = 0;
		if(wimaxRequest.getServiceType() != null){
			serviceType = wimaxRequest.getServiceType();
		}
		return (SerciceType.isAuthorizeOnly(serviceType)); //TODO ask for this condition. in the older one other checks are also done.
	}
}
