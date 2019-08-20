package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.aaa.util.constants.AAAServerConstants.NONE;
import static com.elitecore.aaa.util.constants.AAAServerConstants.REQUEST;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

public class DiameterAuthenticationHandler 
implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>,
SubscriberProfileRepositoryAware {

	private static final String MODULE = "NAS-AUTH-HNDLR";
	private List<DiameterAuthMethodHandler> authMethodHandlers;
	private DiameterSubscriberProfileRepository accountInfoProvider;
	private DiameterAuthenticationHandlerData data;
	private DiameterServiceContext context;
	
	public DiameterAuthenticationHandler(DiameterServiceContext context,
			DiameterAuthenticationHandlerData data){
		this.context = context;
		this.data = data;
	}
	
	@Override
	public void init() throws InitializationFailedException{
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Authentication handler for policy: " + data.getPolicyName());
		}
		
		if (Collectionz.isNullOrEmpty(data.getAutheMethodHandlerTypes())) {
		if	(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "No authentication method configured for policy: " + data.getPolicyName());
			}
			return;
		}
		
		authMethodHandlers = new ArrayList<DiameterAuthMethodHandler>();
		List<Integer> autheMethodHandlerTypes = data.getAutheMethodHandlerTypes();
		final int length = autheMethodHandlerTypes.size();
		int methodType;		
		for(int index =0; index < length; index++){
			methodType  = autheMethodHandlerTypes.get(index);
			DiameterAuthMethodHandler handler = createHandler(methodType);
			if (handler != null) {
				handler.init();
				authMethodHandlers.add(handler);
			}
		}
		LogManager.getLogger().info(MODULE, "Initialized authentication handler for policy " + data.getPolicyName());
	}

	@VisibleForTesting
	DiameterAuthMethodHandler createHandler(int methodType) {
		DiameterAuthMethodHandler handler;
		switch (methodType) {
		case AuthMethods.PAP:
			handler = new DiameterPAPMethodHandler(context, data); 
			break;
		case AuthMethods.CHAP:
			handler = new DiameterCHAPMethodHandler(context, data); 
			break;
		case AuthMethods.EAP:
			handler = new DiameterEapHandler(context, data.getEapConfigId(), accountInfoProvider);
			break;
		default:
			LogManager.getLogger().error(MODULE,"No Authentication Handler found for Method Type = "+ methodType);
			return null;
		}
		return handler;
	}
	
	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		
		boolean isSupportedAuthenticationMethod = false;
		
		for (DiameterAuthMethodHandler methodHandler : authMethodHandlers){
			if (methodHandler.isEligible(request)) {
				isSupportedAuthenticationMethod = true;
				try {						
					methodHandler.handleRequest(request, response,accountInfoProvider);
				}catch(InvalidPasswordException e){						
					DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_AUTHENTICATION_REJECTED, e.getMessage());
					LogManager.getLogger().info(MODULE, "Invalid password found,result code: " 
							+ ResultCode.DIAMETER_AUTHENTICATION_REJECTED + ", Reason: " + e);
				}catch (AuthenticationFailedException e) {
					LogManager.getLogger().info(MODULE, "Authentication failed,result code: " 
							+ ResultCode.DIAMETER_USER_UNKNOWN +  ", Reason: " + e);
					DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_USER_UNKNOWN, e.getMessage());
				}
				break;
			}
		}
		if(!isSupportedAuthenticationMethod){
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			if(request.getDiameterRequest().getCommandCode()==CommandCode.SESSION_TERMINATION.getCode()){ 
				resultCodeAvp.setInteger(ResultCode.DIAMETER_SUCCESS.code);
				response.addAVP(resultCodeAvp);
				response.setFurtherProcessingRequired(false);
			}else {
				LogManager.getLogger().debug(MODULE, "Authentication Method not supported. Result code " + ResultCode.DIAMETER_UNABLE_TO_COMPLY);				
				DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.UNSUPPORTED_AUTHENTICATION_METHOD);
			}
		}
		
		IDiameterAVP resultCode = response.getAVP(DiameterAVPConstants.RESULT_CODE_STR);
		if (resultCode != null) {
			if (ResultCodeCategory.getResultCodeCategory(resultCode.getInteger()).isFailureCategory == false){
				addUserName(request, response);
			}
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		// not required to implement
	}

	@Override
	public void setSubscriberProfileRepository(DiameterSubscriberProfileRepository spr) {
		this.accountInfoProvider = spr;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return this.accountInfoProvider.isAlive() == false;
	}
	
	private void addUserName(ApplicationRequest authRequest, ApplicationResponse authResponse){
		if (authRequest.getParameter(AAAServerConstants.USERNAME_ADDED) != null){
			return;
		}
		/*
		 * Adding the User-name value based on the service policy configuration
		 */
		if(Strings.isNullOrBlank(data.getSubscriberProfileRepositoryDetails().getUserName()) || NONE.equalsIgnoreCase(data.getSubscriberProfileRepositoryDetails().getUserName())){
			return;
		}
		//considering the value of Authenticated-Identity as the default value
		String userNameRespValue = (String) authRequest.getParameter(AAAServerConstants.CUI_KEY);
		if (REQUEST.equalsIgnoreCase(data.getSubscriberProfileRepositoryDetails().getUserName())) {
			userNameRespValue = (String) authRequest.getParameter(AAAServerConstants.UNSTRIPPED_CUI);
		}

		List<String> userNameResponseAttributeList = data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList();
		if (userNameResponseAttributeList.isEmpty() == false) {
			for(String userNameAttr : userNameResponseAttributeList){
				IDiameterAVP userNameRespAttribute = DiameterDictionary.getInstance().getKnownAttribute(userNameAttr);
				if(userNameRespAttribute != null){
					userNameRespAttribute.setStringValue(userNameRespValue);
					authResponse.addAVP(userNameRespAttribute);
				}else{
					if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE,"Username attribute " + userNameAttr + 
								" will not be added in response, Reason: Attribute not found in dictionary.");
				}
			}
		} else {
			IDiameterAVP userNameAttribute = authResponse.getAVP(DiameterAVPConstants.USER_NAME);
			if (userNameAttribute != null){
				userNameAttribute.setStringValue(userNameRespValue);
			} else {
				userNameAttribute = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);

				if (userNameAttribute != null) {
					userNameAttribute.setStringValue(userNameRespValue);
					authResponse.addAVP(userNameAttribute);
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Username attribute " + DiameterAVPConstants.USER_NAME + 
								" will not be added in response, Reason: Attribute not found in dictionary.");
					}
				}
			}
		}

		//adding the value in the request parameter that the Username is added once
		authRequest.setParameter(AAAServerConstants.USERNAME_ADDED, userNameRespValue);
	}
	
}
