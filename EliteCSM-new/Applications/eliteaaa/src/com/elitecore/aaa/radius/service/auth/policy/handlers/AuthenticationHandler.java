package com.elitecore.aaa.radius.service.auth.policy.handlers;

import static com.elitecore.aaa.util.constants.AAAServerConstants.ADVANCED;
import static com.elitecore.aaa.util.constants.AAAServerConstants.GROUP;
import static com.elitecore.aaa.util.constants.AAAServerConstants.NONE;
import static com.elitecore.aaa.util.constants.AAAServerConstants.PROFILE_CUI;
import static com.elitecore.aaa.util.constants.AAAServerConstants.REQUEST;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.IRadAuthMethodHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadCHAPHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadDigestHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadEAPHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadInternalHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadPAPHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.radius.util.exprlib.DefaultValueProvider;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthenticationHandler implements RadAuthServiceHandler, SubscriberProfileRepositoryAware {
	private static final String MODULE = "AUTH-HANDLER";

	private final AuthenticationHandlerData data;
	private final RadAuthServiceContext context;
	private RadiusSubscriberProfileRepository accountInfoProvider;
	private ArrayList<IRadAuthMethodHandler> authMethodHandlers;
	private RadInternalHandler radInternalHandler;
	
	/// FIXME this should not be done in this manner. It should be refactored
	/* This will only exist in case when CUI configuration is advanced expression */
	@Nullable private Expression cuiExpression;
	/* This will only exist in case when Username configuration is advanced expression */
	@Nullable private Expression usernameExpression;
	
	public AuthenticationHandler(RadAuthServiceContext context, AuthenticationHandlerData data) {
		this.context = context;
		this.data = data;
		this.authMethodHandlers = Collectionz.newArrayList();
		this.radInternalHandler = new RadInternalHandler(context);
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Authentication handler for policy: " + data.getPolicyName());
		}
		
		List<Integer> supportedMethods = data.getSupportedMethods();
		if(supportedMethods== null){
			LogManager.getLogger().debug(MODULE, "No authentication method configured for policy: " + data.getPolicyName());
			return;
		}

		final int length = supportedMethods.size();
		int methodType = 0;		
		for(int index =0; index < length; index++){
			methodType  = supportedMethods.get(index);
			switch (methodType) {
			case AuthMethods.PAP:
				authMethodHandlers.add(new RadPAPHandler(context, data));
				break;
			case AuthMethods.CHAP:
				authMethodHandlers.add(new RadCHAPHandler(context));
				break;
			case AuthMethods.DIGEST:									
				authMethodHandlers.add(new RadDigestHandler(context, data));				
				break;
			case AuthMethods.EAP:
				RadEAPHandler radEAPHandler = new RadEAPHandler(context, data);
				radEAPHandler.init();
				authMethodHandlers.add(radEAPHandler);
				break;
			default:
				LogManager.getLogger().error(MODULE,"Unknown Authentication method Type = "+ methodType + " in policy: " + data.getPolicyName());
			}
		}

		radInternalHandler.init();
		
		initCUIExpression();
		initUsernameExpression();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Authentication handler for policy: " + data.getPolicyName());
		}
	}


	private void initCUIExpression() throws InitializationFailedException {
		if (ADVANCED.equalsIgnoreCase(data.getCuiConfiguration().getCui())) {
			try {
				this.cuiExpression = Compiler.getDefaultCompiler().parseExpression(
						data.getCuiConfiguration().getExpression());
			} catch (InvalidExpressionException ex) {
				LogManager.getLogger().error(MODULE, "Error in compiling advanced CUI expression: " 
						+ data.getCuiConfiguration().getExpression() + " for policy: " 
						+ data.getPolicyName() + ". Reason: " + ex.getMessage()
						+ ". Will use Authenticated-UserName as default value.");
			}
		}
	}
	
	private void initUsernameExpression() throws InitializationFailedException {
		if (ADVANCED.equalsIgnoreCase(data.getUserName())) {
			try {
				this.usernameExpression = Compiler.getDefaultCompiler().parseExpression(
						data.getUserNameExpression());
			} catch (InvalidExpressionException ex) {
				LogManager.getLogger().error(MODULE, "Error in compiling advanced Username expression: " 
						+ data.getUserNameExpression() + " for policy: " 
						+ data.getPolicyName() + ". Reason: " + ex.getMessage()
						+ ". Will use Authenticated-UserName as default value.");
			}
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}
	
	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		boolean isSupportedMethod = false;  

		ArrayList<IRadAuthMethodHandler> autheMethodHandlers = this.authMethodHandlers;
		for (IRadAuthMethodHandler authMethodHandler : autheMethodHandlers) {
			if (authMethodHandler.isEligible(request)) {
				try {
					request.setParameter(AAAServerConstants.SELECTED_AUTHENTICATION_METHOD, authMethodHandler.getMethodType());
					authMethodHandler.handleRequest(request, response,accountInfoProvider);
					isSupportedMethod = true;
					break;
				}catch (UserNotFoundException e) {
					if(accountInfoProvider.getAnonymousAccountData(request) !=null){

						//in case when the anonymous profile is fetched calling post driver script
						postDriverProcessingScript(request, response);

						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, " User Not found further processing by Anonymous User Profile : " + request.getAccountData());
						}
						isSupportedMethod = true;

						response.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
						response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
						break;
					}else{
						response.setFurtherProcessingRequired(false);					
						response.setResponseMessage(e.getMessage());				
						response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
						return;
					}
				} catch (AuthenticationFailedException e) {					
					response.setFurtherProcessingRequired(false);					
					response.setResponseMessage(e.getMessage());				
					response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					return;
				}
			}
		}
		
		if(!isSupportedMethod){
			try {
				// This method handler will take care of request which does not contains
				// User Password Attribute. 
				this.radInternalHandler.handleRequest(request, response, accountInfoProvider);
			} catch (AuthenticationFailedException e) {
				response.setFurtherProcessingRequired(false);					
				response.setResponseMessage(e.getMessage());				
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			}

			return;			
		}

		if(!response.isFurtherProcessingRequired())
			return;
		
		if (response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE) {
			AccountData accountData = request.getAccountData();
			if (accountData != null){
				addElitecoreVSA(request, accountData,response);
			}
			
			addCUI(request, response);
			addUserName(request, response);
		}
	}
	
	protected void addCUI(RadAuthRequest request,RadAuthResponse response) {
		if (request.getParameter(RadiusAttributeConstants.CUI_ADDED) != null) {
			return;
		}
		
		IRadiusAttribute cuiRequestAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
		String strUserIdentity = (String) request.getParameter(AAAServerConstants.CUI_KEY);
		AccountData accountData = request.getAccountData();
		ChargeableUserIdentityConfiguration cuiConfiguration = data.getCuiConfiguration();
		String strCUIConfiguration = cuiConfiguration.getCui();
		
		if (NONE.equalsIgnoreCase(strCUIConfiguration)
				&& cuiRequestAttribute != null && cuiRequestAttribute.getLength() > 2) {
			if (accountData == null) {
				LogManager.getLogger().warn(MODULE, "Customer profile not available for " + strUserIdentity);
			} else if (accountData.getCUI() != null) {
				strUserIdentity = accountData.getCUI();
			} else if (accountData.getGroupName() != null) {
				strUserIdentity = accountData.getGroupName();
			}
			
			IRadiusAttribute cuiRespAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CUI);
			cuiRespAttribute.setStringValue(strUserIdentity);
			response.addAttribute(cuiRespAttribute);
			//FIXME NARENDRA - this else block should be inlined with inner conditions. Remove this after unit testing
		} else if (NONE.equalsIgnoreCase(strCUIConfiguration) == false) {
			if (accountData == null) {
				LogManager.getLogger().warn(MODULE, "Customer profile not available for "+ strUserIdentity+ ", considering Authenticated-UserName as default");
			} else if (PROFILE_CUI.equalsIgnoreCase(strCUIConfiguration)) {
				if (accountData.getCUI() == null) {
					LogManager.getLogger().warn(MODULE,"Profile-CUI not available for "+ strUserIdentity+ ",considering Authenticated-UserName as default");
				} else {
					strUserIdentity = accountData.getCUI();
				}
			} else if (GROUP.equalsIgnoreCase(strCUIConfiguration)) {
				if (accountData.getGroupName() == null) {
					LogManager.getLogger().warn(MODULE,"Group-Name not available for "+ strUserIdentity+ ",considering Authenticated-UserName as default");
				} else {
					strUserIdentity = accountData.getGroupName();
				}
			} else if (ADVANCED.equalsIgnoreCase(strCUIConfiguration)) {
				strUserIdentity = evaluateCUIFromExpression(request, response, strUserIdentity);
			}
			
			addCUIAttributesInResponse(response, strUserIdentity);
		}

		/*
		 * Check for CUI into request and updates it's value for the RM
		 * communication.
		 */

		if (cuiRequestAttribute == null) {
			IRadiusAttribute cuiAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CUI);
			cuiAttribute.setStringValue(strUserIdentity);
			request.addInfoAttribute(cuiAttribute);
		} else if ((cuiRequestAttribute.getLength() > 2 && cuiRequestAttribute.getStringValue().charAt(0) == 0x00)|| !strCUIConfiguration.equalsIgnoreCase(NONE)) {
			cuiRequestAttribute.setStringValue(strUserIdentity);
		}


		/*
		 * Adding Callback-Id into response packet if the request received from the Vendor Type-A12-ANAAA.
		 */
		int vendorType = response.getClientData().getVendorType();
		if(vendorType == ClientTypeConstant.A12_ANAAA.typeId){
			if(accountData!=null){
				String callbackId = accountData.getCallbackId();
				if(callbackId != null && callbackId.length() > 0){
					IRadiusAttribute callbackIdAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
					callbackIdAttr.setStringValue(callbackId);
					response.addAttribute(callbackIdAttr);
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Callback-Id is not added to response packet for : "+strUserIdentity + " reason Customer profile not available");
			}
		}
		request.setParameter(RadiusAttributeConstants.CUI_ADDED, strUserIdentity);
	}

	private void addCUIAttributesInResponse(RadAuthResponse response, String cuiValue) {
		List<String> cuiRespAttrList =  data.getCuiConfiguration().getAuthenticationCuiAttributes();
		
		if (cuiRespAttrList.size() > 0) {
			for (String cuiRespAttr : cuiRespAttrList) {
				IRadiusAttribute cuiRespAttribute = Dictionary.getInstance().getKnownAttribute(cuiRespAttr);
				if (cuiRespAttribute != null) {
					cuiRespAttribute.setStringValue(cuiValue);
					response.addAttribute(cuiRespAttribute);
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().trace(MODULE,"Configured CUI Attribute: " + cuiRespAttr + " is not found in dictionary. So will not be added in response.");
					}
				}
			}
		} else {
			IRadiusAttribute cuiRespAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CUI);
			cuiRespAttribute.setStringValue(cuiValue);
			response.addAttribute(cuiRespAttribute);
		}
	}

	private String evaluateCUIFromExpression(RadAuthRequest request, RadAuthResponse response,
			String userIdentity) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Evaluating advanced CUI expression for Identity: " 
					+ userIdentity);
		}
		
		if (cuiExpression == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Advanced CUI expression did not initialize " 
						+ "so considering Authenticated-UserName as default for Identity: " 
						+ userIdentity);
			}
			return userIdentity;
		}
		
		String cui = userIdentity;
		try {
			cui = cuiExpression.getStringValue(new DefaultValueProvider(request, response));
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Advanced CUI expression: " 
						+ data.getCuiConfiguration().getExpression() + " evaluated to: "
						+ cui + " for Identity: " + userIdentity);
			}
		} catch (InvalidTypeCastException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getCuiConfiguration().getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (IllegalArgumentException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getCuiConfiguration().getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (MissingIdentifierException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getCuiConfiguration().getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		}
		
		return cui;
	}

	protected void addUserName(RadAuthRequest radAuthRequest, RadAuthResponse radAuthResponse){
		if (radAuthRequest.getParameter(RadiusAttributeConstants.USERNAME_ADDED) != null){
			return;
		}
		/*
		 * Adding the User-name value based on the service policy configuration
		 */
		String userNameRespValue = "";
		if (!NONE.equalsIgnoreCase(data.getUserName())) {

			//considering the value of Authenticated-Identity as the default value
			userNameRespValue = (String) radAuthRequest.getParameter(AAAServerConstants.CUI_KEY);

			if (REQUEST.equalsIgnoreCase(data.getUserName())) {
				userNameRespValue = (String) radAuthRequest.getParameter(AAAServerConstants.UNSTRIPPED_CUI);
			} else if (ADVANCED.equalsIgnoreCase(data.getUserName())) {
				userNameRespValue = evaluateUsernameFromExpression(radAuthRequest, radAuthResponse,
						userNameRespValue, radAuthRequest.getAccountData().getUserIdentity());
			}

			List<String>userNameRespAttrList = data.getUserNameRespAttrList();
			if (userNameRespAttrList.size() > 0) {
				for (String userNameAttr:userNameRespAttrList) {
					IRadiusAttribute userNameRespAttribute = Dictionary.getInstance().getKnownAttribute(userNameAttr);
					if(userNameRespAttribute != null){
						userNameRespAttribute.setStringValue(userNameRespValue);
						radAuthResponse.addAttribute(userNameRespAttribute);
					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE,"Configured Username Attribute is not found in dictionary. Attribute: " + userNameAttr);
					}
				}
			} else {
				IRadiusAttribute userNameAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
				if (userNameAttribute == null)
					userNameAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.USER_NAME);
				userNameAttribute.setStringValue(userNameRespValue);
				radAuthResponse.addAttribute(userNameAttribute);
			}
		}

		//adding the value in the request parameter that the Username is added once
		radAuthRequest.setParameter(RadiusAttributeConstants.USERNAME_ADDED, userNameRespValue);
	}
	
	private String evaluateUsernameFromExpression(RadAuthRequest request,
			RadAuthResponse response, String authenticatedUsername, String userIdentity) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Evaluating advanced Username expression for Identity: " 
					+ userIdentity);
		}
		
		
		if (usernameExpression == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Advanced Username expression did not initialize " 
						+ "so considering Authenticated-UserName as default for Identity: " 
						+ userIdentity);
			}
			return authenticatedUsername;
		}
		
		String userName = authenticatedUsername;
		try {
			userName = usernameExpression.getStringValue(new DefaultValueProvider(request, response));
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Advanced Username expression: " 
						+ data.getUserNameExpression() + " evaluated to: "
						+ userName + " for Identity: " + userIdentity);
			}
		} catch (InvalidTypeCastException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating Username expression " 
						+ data.getUserNameExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (IllegalArgumentException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating Username expression " 
						+ data.getUserNameExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (MissingIdentifierException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating Username expression " 
						+ data.getUserNameExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		}
		
		return userName;
	}

	private void addElitecoreVSA(RadAuthRequest request,
			AccountData accountData,RadAuthResponse response) {
		if (request.getParameter(RadiusAttributeConstants.ELITECORE_VSA_ADDED)!=null){
			return;
		}
		try {
			IRadiusAttribute radiusAttribute;
			String ipPoolName= accountData.getIPPoolName();
			if(ipPoolName==null || !(ipPoolName.length()>0))
				ipPoolName = response.getClientData().getFramedPoolName();
			if(ipPoolName!=null && ipPoolName.length()>0){
				radiusAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_FRAMED_POOL_NAME);
				if(radiusAttribute != null) {
					radiusAttribute.setStringValue(ipPoolName);
					request.addInfoAttribute(radiusAttribute);
				}
			}
			
			request.setParameter(RadiusAttributeConstants.ELITECORE_VSA_ADDED, "true");
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE,"Error during adding customer VSA into radius packet,reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

	}
	

	//this is the code for calling the post driver processing
	private void postDriverProcessingScript(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		String script = data.getUserProfileRepoDetails().getDriverDetails().getDriverScript();
		if(script != null && script.trim().length() > 0){
			try {
				context.getServerContext().getExternalScriptsManager().execute(script, DriverScript.class, "postDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest,serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing \"post\" method of driver script" + script + ". Reason: " + e.getMessage());

				LogManager.getLogger().trace(e);
			}
		}
	}
	
	@Override
	public void setSubscriberProfileRepository(RadiusSubscriberProfileRepository spr) {
		this.accountInfoProvider = spr;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return this.accountInfoProvider.isAlive() == false;
	}
}
