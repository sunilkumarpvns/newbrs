package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadInternalHandler extends BaseAuthMethodHandler implements
		IRadAuthMethodHandler {
	private static final String MODULE = "RAD-INTRNL-HND";

	public RadInternalHandler(ServiceContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response,
			IAccountInfoProvider accountInfoProvider)
			throws AuthenticationFailedException {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling request using Internal handler.");
		}
		
		AccountData accountData = accountInfoProvider.getAccountData(request,response); 
		if(accountData==null)
			throw new UserNotFoundException();
		
		String passwordCheck = accountData.getPasswordCheck();
		if("YES".equalsIgnoreCase(passwordCheck)){			
			//None Supported Authentication method handler found.
			response.setFurtherProcessingRequired(false);
			response.setResponseMessage(AuthReplyMessageConstant.UNSUPPORTED_AUTHENTICATION_METHOD);			
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			LogManager.getLogger().debug(MODULE, "Password check is enabled for this account. but request does not contains password attribute.");
		}else{			
			((RadServiceResponse)response).setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);		
			((RadServiceResponse)response).setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
			LogManager.getLogger().debug(MODULE, "Password check is disabled for this account.So sending Access Accept.");
		}
		
	}

	@Override
	public boolean isEligible(RadAuthRequest request) {		
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
	}

	@Override
	public void reInit() throws InitializationFailedException {
		//As it does not cache any configuration so no need for reinitializing
	}
	
	@Override
	public int getMethodType() {
		return 0;
	}

}
