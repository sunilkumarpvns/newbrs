package com.elitecore.aaa.radius.service.auth.policy.handlers;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.impl.RadiusPolicyDetail;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.drivers.BaseAuthDriver;
import com.elitecore.aaa.core.policies.accesspolicy.AccessDeniedException;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthorizationHandlerData;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthorizationHandler implements RadAuthServiceHandler, SubscriberProfileRepositoryAware {
	private static final String MODULE = "AUTHORIZE-HNDLR";
	private static final int TIME_IN_DAYS = 1000 * 60 * 60 * 24;

	private final AuthorizationHandlerData data;
	private final RadAuthServiceContext context;
	private final RadiusPolicyManager radiusPolicyManager;
	private final AccessPolicyManager accessPolicyManager;
	private final TimeSource timeSource;
	private RadiusSubscriberProfileRepository accountInfoProvider;

	public AuthorizationHandler(RadAuthServiceContext context, AuthorizationHandlerData authorizeOnlyHandlerData) {
		this(context, authorizeOnlyHandlerData,
				RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY),
				AccessPolicyManager.getInstance(), TimeSource.systemTimeSource());
	}
	
	@VisibleForTesting
	AuthorizationHandler(RadAuthServiceContext context, AuthorizationHandlerData authorizeOnlyHandlerData,
			RadiusPolicyManager radiusPolicyManager, AccessPolicyManager accessPolicyManager,
			TimeSource timeSource) {
		this.context = context;
		this.data = authorizeOnlyHandlerData;
		this.radiusPolicyManager = radiusPolicyManager;
		this.accessPolicyManager = accessPolicyManager;
		this.timeSource = timeSource;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Authorization handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		// No-op

	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		handlePreRequest(request, response);
		
		try{
			if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Authorization process started for policy: " + data.getPolicyName());

			try{
				AccountData accountData = request.getAccountData();
				if(accountData == null){
					accountData = accountInfoProvider.getAccountData(request,response);
					if(accountData == null) {
						accountData = accountInfoProvider.getAnonymousAccountData(request);
						if(accountData == null) {
							throw new AuthorizationFailedException(AuthReplyMessageConstant.USER_NOT_FOUND);
						}
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, " User Not found further processing by Anonymous User Profile : " + request.getAccountData());
						}
					}
				} 

				performAccountLevelAuthorizationChecks(request,response);  

				if(!response.isFurtherProcessingRequired()) {
					return;
				}

				if (response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE) {
					addElitecoreVSA(request, accountData,response);
				}

				//Account Level Checks
				IRadiusAttribute hotlineReason = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.HOTLINE_REASON);
				if(hotlineReason != null){
					throw new AuthorizationFailedException(hotlineReason.getStringValue());
				}
				
				applyAccountLevelPolicyChecks(request,response);
				//TODO- Dynamic Check Items implementation remaing.

				applyRadiusPolicy(request, response, accountData);

				applyAccessPolicy(request, response, accountData);

				applyClientPolicy(request, response, accountData);

				//TODO - ensure that only one session-timeout is returned back in response. provide proper implementation of setAttribute method of RadiusAttribute class						
				IRadiusAttribute responseSessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR);
				if(responseSessionTimeout == null && data.getDefaultSessionTimeout() > 0){
					responseSessionTimeout = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
					responseSessionTimeout.setLongValue(data.getDefaultSessionTimeout());
					response.addAttribute(responseSessionTimeout);

					if(accountData.getMaxSessionTime() > 0 && accountData.getMaxSessionTime() < responseSessionTimeout.getLongValue()){
						responseSessionTimeout.setLongValue(accountData.getMaxSessionTime());
					}

					if(accountData.getExpiryDate() != null && !accountData.isGracePeriodApplicable()){
						long accountExpiryTime = (accountData.getExpiryDate().getTime() - timeSource.currentTimeInMillis())/1000;
						if(accountExpiryTime < responseSessionTimeout.getLongValue()){
							responseSessionTimeout.setLongValue(accountExpiryTime);
						}
					}						
				}

				if((request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE) != null &&
						request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE).getIntValue() == RadiusAttributeValuesConstants.AUTHORIZE_ONLY) || 
						(request.getRadiusAttributes(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO)!=null && 
								((IRadiusAttribute) request.getRadiusAttributes(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO).iterator().next()).getStringValue().trim().equalsIgnoreCase("N-prepaid-3g"))){

					IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
					if(serviceType != null) {
						serviceType.setIntValue(RadiusAttributeValuesConstants.AUTHORIZE_ONLY);
						response.addAttribute(serviceType);
					}

					IRadiusAttribute stateAttribute = request.getRadiusAttribute(RadiusAttributeConstants.STATE);
					if(stateAttribute!=null){
						IRadiusAttribute radStatAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.STATE);
						radStatAttr.setStringValue(stateAttribute.getStringValue());
						response.addAttribute(radStatAttr);
					}  
				}	
			} catch (PolicyFailedException e) {
				throw new AuthorizationFailedException(e.getMessage(), e);
			}	
		} catch(AuthorizationFailedException e) {
			response.setFurtherProcessingRequired(false);					
			response.setResponseMessage(e.getMessage());				
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			LogManager.ignoreTrace(e);
			return;
		}

		// To set response-message in case of Authorize only request
		response.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);

	}

	private void handlePreRequest(RadAuthRequest request,
			RadAuthResponse response) {
		/**
		 * From EliteAAA system at least one Session Timeout attribute should go if default
		 * value is configured in Authorization handler. So authorization handler blindly 
		 * adds that session timeout attribute in response, so that if the request gets 
		 * converted to ACCESS_ACCEPT (by policy level plugin or universal hotlining)
		 * then this value can be harnessed there.
		 */
		addSessionTimeOut(request, response);
	}

	private void applyClientPolicy(RadAuthRequest request,
			RadAuthResponse response, AccountData accountData) throws AuthorizationFailedException{
		RadiusPolicyDetail radiusPolicyDetail = data.getRadiusPolicy();

		try{
			String clientPolicy = response.getClientData().getClientPolicy(); 
			if(clientPolicy!=null && clientPolicy.trim().length()>0){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Applying Client Policy "+clientPolicy+ " for User-Identity:" + accountData.getUserIdentity());
				List<String> satisfiedClientPolicies = radiusPolicyManager.applyPolicies(request, response, clientPolicy, response.getClientData().getVendorType(), null, null, null, radiusPolicyDetail.isRejectOnCheckItemNotFound(), radiusPolicyDetail.isRejectOnRejectItemNotFound(), radiusPolicyDetail.isAcceptOnPolicyOnFound(),true);
				if(satisfiedClientPolicies != null && satisfiedClientPolicies.size() > 0)
					request.setParameter(AAAServerConstants.SATISFIED_CLIENT_POLICIES, satisfiedClientPolicies);
			}	

		}catch(ParserException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"Client-Policy failed for User-Identity : "+accountData.getUserIdentity()+" Reason : "+e.getMessage());
			throw new AuthorizationFailedException(e.getMessage(), e);
		}catch(PolicyFailedException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"Client-Policy failed for User-Identity : "+accountData.getUserIdentity()+" Reason : "+e.getMessage());
			throw new AuthorizationFailedException(e.getMessage(), e);
		}


	}

	private void performAccountLevelAuthorizationChecks (RadAuthRequest request, RadAuthResponse response) throws AuthorizationFailedException {

		AccountData accountData = request.getAccountData();

		if(accountData.isCreditLimitCheckRequired() && accountData.getCreditLimit() < 1){
			throw new AuthorizationFailedException(AuthReplyMessageConstant.CREDIT_LIMIT_EXCEEDED);
		}else if (!"A".equalsIgnoreCase(accountData.getAccountStatus()) & !CommonConstants.STATUS_ACTIVE.equalsIgnoreCase(accountData.getAccountStatus())){
			throw new AuthorizationFailedException(AuthReplyMessageConstant.ACCOUNT_NOT_ACTIVE);
		}else if(isAccountExpired(request)){
			throw new AuthorizationFailedException(AuthReplyMessageConstant.ACCOUNT_EXPIRED);
		}
	}

	private boolean isAccountExpired(RadAuthRequest request) {
		boolean bAccountExpired = true;
		AccountData accountData = request.getAccountData();
		if (accountData == null)
			return bAccountExpired;
		Date currentDate = new Date(timeSource.currentTimeInMillis());
		int graceType = 0;

		if(accountData.getExpiryDate() !=null){
			if (currentDate.before(accountData.getExpiryDate())) {
				bAccountExpired=false;
				IRadiusAttribute graceTypeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_GRACE_TYPE);
				if (graceTypeAttr != null) {
					graceTypeAttr.setIntValue(graceType);
					request.addInfoAttribute(graceTypeAttr);
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE,"Account is not expired ,so grace policy not applied.");
				}
			}else if(accountData.getGracePolicy()!=null && accountData.getGracePolicy().length()>0){
				int[] gracePeriod = context.getServerContext()
						.getServerConfiguration().getGracePolicyConfiguration(accountData.getGracePolicy());
				if(gracePeriod!=null && gracePeriod.length>0){
					bAccountExpired = applyGracePeriod(request, gracePeriod, accountData, currentDate,accountData.getGracePolicy());
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Grace policy : "+accountData.getGracePolicy()+" configured in Customer profile not found ,so no grace period allowed for this customer");
				}
			}else if (data.getGracePolicy()!=null && data.getGracePolicy().length()>0) {
				int[] gracePeriod = context.getServerContext()
						.getServerConfiguration().getGracePolicyConfiguration(data.getGracePolicy());
				if(gracePeriod!=null && gracePeriod.length>0){
					bAccountExpired = applyGracePeriod(request, gracePeriod, accountData, currentDate, data.getGracePolicy());
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Grace policy : " + data.getGracePolicy() + " configured in auth policy configuration  not found ,so no grace period allowed for this customer");
				}
			}
		}else{
			if(!accountData.isExpiryDateCheckRequired()) {
				bAccountExpired = false;
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Expiry date is not configured for Customer profile of User-identity : "+accountData.getUserIdentity()+ ", so considering this Customer account expired");
				bAccountExpired = true;
			}
		}
		return bAccountExpired;
	}
	private boolean applyGracePeriod(RadAuthRequest request,int[] gracePeriod,AccountData accountData,Date currentDate,String gracePolicy) {
		boolean bAccountExpired = true;

		int dayDiff = (int) Math.ceil((currentDate.getTime() - accountData.getExpiryDate().getTime())/ TIME_IN_DAYS);
		for (int i = 0; i < gracePeriod.length; i++) {
			if (dayDiff <= gracePeriod[i]) {
				IRadiusAttribute graceTypeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_GRACE_TYPE);
				if (graceTypeAttr != null) {
					i=i+1;
					graceTypeAttr.setIntValue(i); // Here value of i represents the Grace-Type. 
					request.addInfoAttribute(graceTypeAttr);
					accountData.setGracePeriodApplicable(true);
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE," Grace-Type : "+ i+  " applied, for Grace Policy : "+ gracePolicy);
					bAccountExpired=false;
					break;
				}
			}
		}
		if(bAccountExpired){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Can't give Grace-Period for this request, Reason Grace policy :" +gracePolicy+" not satisfied");
		}
		return bAccountExpired;
	}
	private void applyAccountLevelPolicyChecks(RadAuthRequest request,RadAuthResponse response) throws PolicyFailedException {
		RadiusPolicyDetail radiusPolicyDetail = data.getRadiusPolicy();

		AccountData accountData= request.getAccountData();
		if(response.getClientData().getVendorType() == ClientTypeConstant.PORTAL.typeId)
			return;
		String strServiceType = accountData.getServiceType();
		if(strServiceType != null && strServiceType.length() > 0 ){
			IRadiusAttribute serviceType = request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE);
			if(serviceType != null){
				boolean validServiceType = false;
				StringTokenizer serviceTypeTokens = new StringTokenizer(strServiceType,",;");
				final int totalTokens = serviceTypeTokens.countTokens();
				for(int cntr = 0; cntr<totalTokens; cntr++){
					String currentToken=serviceTypeTokens.nextToken();
					try{
						int iServiceType = Integer.parseInt(currentToken);
						if(iServiceType == serviceType.getIntValue()){
							validServiceType = true;
							break;
						}
					}catch(NumberFormatException nfe){
						if(serviceType.getStringValue().equalsIgnoreCase(currentToken)){
							validServiceType = true;
							break;
						}
					}
				}
				if(!validServiceType)
					throw new PolicyFailedException(AuthReplyMessageConstant.INVALID_NAS_PORT_TYPE);
			}else if(radiusPolicyDetail.isRejectOnCheckItemNotFound())
				throw new PolicyFailedException(AuthReplyMessageConstant.NAS_PORT_TYPE_NOT_FOUND);
		}
		String strCallingStationId = accountData.getCallingStationId();
		if(strCallingStationId != null && strCallingStationId.length() > 0){
			IRadiusAttribute callingStationId = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			if(callingStationId != null){
				boolean validCallingStationId = false;
				StringTokenizer callingStationIdTokens = new StringTokenizer(strCallingStationId,",;");
				final int totalTokens = callingStationIdTokens.countTokens();
				for(int cntr = 0; cntr<totalTokens; cntr++){
					if(callingStationId.patternCompare(callingStationIdTokens.nextToken())){					
						validCallingStationId = true;
						break;
					}
				}
				if(!validCallingStationId)
					throw new PolicyFailedException(AuthReplyMessageConstant.INVALID_CALLING_STATION_ID);
			}else if(radiusPolicyDetail.isRejectOnCheckItemNotFound())
				throw new PolicyFailedException(AuthReplyMessageConstant.CALLING_STATION_ID_NOT_FOUND );
		}

		String strCalledStationId = accountData.getCalledStationId();
		if(strCalledStationId != null && strCalledStationId.length() > 0){
			IRadiusAttribute calledStationId = request.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
			if(calledStationId != null){
				boolean validCalledStationId = false;
				StringTokenizer calledStationIdTokens = new StringTokenizer(strCalledStationId,",;");
				final int totalTokens = calledStationIdTokens.countTokens();
				for(int cntr = 0; cntr<totalTokens; cntr++){
					if(calledStationId.patternCompare(calledStationIdTokens.nextToken())){					
						validCalledStationId = true;
						break;
					}
				}
				if(!validCalledStationId)
					throw new PolicyFailedException(AuthReplyMessageConstant.INVALID_CALLED_STATION_ID);
			}else if(radiusPolicyDetail.isRejectOnCheckItemNotFound())
				throw new PolicyFailedException(AuthReplyMessageConstant.CALLED_STATION_ID_NOT_FOUND);
		}

	}	

	private void applyRadiusPolicy(RadAuthRequest request, RadAuthResponse response, AccountData accountData) throws AuthorizationFailedException{
		RadiusPolicyDetail radiusPolicyDetail = data.getRadiusPolicy();

		List<String> satisfiedPolicies = null;
		List<String> additionalSatisfiedPolicies = null;
		try {
			String strCheckItems = accountData.getCheckItem();
			String strDynamicCheck = accountData.getDynamicCheckItems();
			String strReplyItem = accountData.getReplyItem();
			if(strReplyItem == null) {
				strReplyItem = "";
			}
			
			request.setParameter(AAAServerConstants.CUSTOMER_REPLY_ITEM, strReplyItem);
			request.setParameter(AAAServerConstants.REJECT_ON_CHECK_ITEM_NOT_FOUND, radiusPolicyDetail.isRejectOnCheckItemNotFound());
			request.setParameter(AAAServerConstants.REJECT_ON_REJECT_ITEM_NOT_FOUND, radiusPolicyDetail.isRejectOnRejectItemNotFound());
			request.setParameter(AAAServerConstants.CONTINUE_ON_POLICY_NOT_FOUND, radiusPolicyDetail.isAcceptOnPolicyOnFound());
			
			if(strDynamicCheck != null && strDynamicCheck.length() > 0){
				strCheckItems = strCheckItems + "," + strDynamicCheck.trim();
			}else{
				strDynamicCheck = "";
			}
			
			String radiusPolicyGroupExpression = getPolicyGroupExpression(accountData.getAuthorizationPolicyGroup());
			
			String radiusPolicy = accountData.getAuthorizationPolicy();
			String policyToApply = "";
			
			if (Strings.isNullOrBlank(radiusPolicy) == false && Strings.isNullOrBlank(radiusPolicyGroupExpression) == false) {
				policyToApply = radiusPolicyGroupExpression + " && " + radiusPolicy;
			} else if (Strings.isNullOrBlank(radiusPolicy) == false) {
				policyToApply = radiusPolicy;
			} else if (Strings.isNullOrBlank(radiusPolicyGroupExpression) == false) {
				policyToApply = radiusPolicyGroupExpression;
			}

			if (Strings.isNullOrBlank(policyToApply) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Radius authorization policies to apply: " + policyToApply
							+ " for user identity: " + accountData.getUserIdentity());
				}
			}
			
			satisfiedPolicies = radiusPolicyManager.applyPolicies(
					request,response,
					policyToApply,
					response.getClientData().getVendorType(),
					strCheckItems,
					accountData.getRejectItem(),
					accountData.getReplyItem(),
					radiusPolicyDetail.isRejectOnCheckItemNotFound(),
					radiusPolicyDetail.isRejectOnRejectItemNotFound(),
					radiusPolicyDetail.isAcceptOnPolicyOnFound());

			if(strDynamicCheck.contains("*")){
				updateAccountLevelDynamicChecks(request, strDynamicCheck, radiusPolicyDetail.isRejectOnCheckItemNotFound(),accountData);
			}			
		} catch (ParserException e) {
			throw new AuthorizationFailedException(e.getMessage(), e);
		} catch (PolicyFailedException e) {
			throw new AuthorizationFailedException(e.getMessage(), e);
		}
		String strAdditionalPolicy = accountData.getAdditionalPolicy();
		if(strAdditionalPolicy!=null && strAdditionalPolicy.length()>0){
			additionalSatisfiedPolicies = applyAdditionalRadiusPolicy(request, response,strAdditionalPolicy,accountData.getUserIdentity());
		}	
		addSatisfiedPolicyAttr(request,response, satisfiedPolicies,additionalSatisfiedPolicies);

	}

	private void addSatisfiedPolicyAttr(RadAuthRequest request,RadAuthResponse response,List<String> satisfiedRadiusAuthPolicy, List<String> satisfiedAdditionalPolicy){

		if(satisfiedRadiusAuthPolicy!=null){
			if(satisfiedAdditionalPolicy!=null){
				satisfiedRadiusAuthPolicy.addAll(satisfiedAdditionalPolicy);
			}
		}else {
			satisfiedRadiusAuthPolicy = satisfiedAdditionalPolicy;
		}

		if(satisfiedRadiusAuthPolicy != null && satisfiedRadiusAuthPolicy.size() > 0) {
			IRadiusAttribute satisfiedPolicyAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_SATISFIED_POLICIES);
			if(satisfiedPolicyAttr != null) {

				String satisfiedPolicyStr = "";
				for(int i=0;i<satisfiedRadiusAuthPolicy.size();i++) {
					if(i == satisfiedRadiusAuthPolicy.size()-1)
						satisfiedPolicyStr = satisfiedPolicyStr + satisfiedRadiusAuthPolicy.get(i);
					else
						satisfiedPolicyStr = satisfiedPolicyStr + satisfiedRadiusAuthPolicy.get(i) + ",";
				}
				satisfiedPolicyAttr.setStringValue(satisfiedPolicyStr);
				response.addInfoAttribute(satisfiedPolicyAttr);
				LogManager.getLogger().info(MODULE, "Satisfied-Policies attribute added to response packet");

			} else {
				LogManager.getLogger().info(MODULE, "Satisfied-Policies Attribute is not found in elitecore dictonary  , so this attribute is not included in response packet.");
			}
		}
		
		request.setParameter(AAAServerConstants.SATISFIED_POLICIES, satisfiedRadiusAuthPolicy);
	}

	private List<String> applyAdditionalRadiusPolicy(RadAuthRequest request, RadAuthResponse response, String strAdditionalPolicy,String userIdentity){
		RadiusPolicyDetail radiusPolicyDetail = data.getRadiusPolicy();

		List<String> satisfiedPolicies= null;
		try {

			satisfiedPolicies = radiusPolicyManager.applyPolicies(
					request,response,
					strAdditionalPolicy,
					response.getClientData().getVendorType(),
					null,
					null,
					null,
					radiusPolicyDetail.isRejectOnCheckItemNotFound(),
					radiusPolicyDetail.isRejectOnRejectItemNotFound(),
					radiusPolicyDetail.isAcceptOnPolicyOnFound());

		} catch (ParserException e) {
			if(LogManager.getLogger().isWarnLogLevel()){
				LogManager.getLogger().warn(MODULE, "Failed to apply additional policy :"+strAdditionalPolicy+" for UserIdentity :"+strAdditionalPolicy+" Reason :"+e.getMessage());
			}
			LogManager.ignoreTrace(e);
		} catch (PolicyFailedException e) {
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Failed to apply additional policy :"+strAdditionalPolicy+" for UserIdentity :"+strAdditionalPolicy+" Reason :"+e.getMessage());
			}
			LogManager.ignoreTrace(e);
		}
		return satisfiedPolicies;
	}

	private void applyAccessPolicy(RadAuthRequest request, RadAuthResponse response, AccountData accountData) throws AuthorizationFailedException{
		RadiusPolicyDetail radiusPolicyDetail = data.getRadiusPolicy();

		try{
			long maxSessionTime = accessPolicyManager.checkAccessPolicy(accountData.getAccessPolicy(), radiusPolicyDetail.isAcceptOnPolicyOnFound());
			if(maxSessionTime > 0){
				IRadiusAttribute acctSessionTimeAtt = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR);
				if (acctSessionTimeAtt != null) {
					long sessionTimeoutValueFromDriver = acctSessionTimeAtt.getLongValue();
					if( maxSessionTime < sessionTimeoutValueFromDriver){
						acctSessionTimeAtt.setLongValue(maxSessionTime);
					}
				}else {
					acctSessionTimeAtt = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
					acctSessionTimeAtt.setLongValue(maxSessionTime);
					response.addAttribute(acctSessionTimeAtt);
				}
			}
		}catch (AccessDeniedException e1) {
			throw new AuthorizationFailedException("Access denied by Access Policy Manager. Reason: " + e1.getMessage(), e1);
		}
	}


	private void updateAccountLevelDynamicChecks(RadAuthRequest request, String strDynamicChecks, boolean rejectOnCheckItemNotFound, AccountData accountData) {
		StringBuffer dynamicCheckStrBuffer = new StringBuffer();
		boolean bDynCheckUpdated = false;
		String[] dynamicChecks = ParserUtility.splitString(strDynamicChecks, ',');
		if(dynamicChecks != null){
			for(int i=0; i<dynamicChecks.length; i++){
				String dynamicCheckAttr = dynamicChecks[i];
				String[] strCheckTokens = ParserUtility.splitKeyAndValue(dynamicCheckAttr);
				if(strCheckTokens.length == 3){
					String strAttributeId = strCheckTokens[0];
					String strValue = strCheckTokens[2];
					boolean bValueUpdated = true;
					StringBuffer valueBuilder = new StringBuffer();
					if(strValue.contains("*")){
						IRadiusAttribute attribute = request.getRadiusAttribute(strAttributeId);
						if(attribute != null){
							String attrValue = attribute.getStringValue();
							String[] valueTokens = ParserUtility.splitString(strValue.replaceAll("\\[", "").replaceAll("\\]", ""),',');
							for(int j=0; j<valueTokens.length; j++){
								String strTok = valueTokens[j];
								if(attrValue.equals(strTok)){
									bValueUpdated = false;
									break;
								}
							}
							if(bValueUpdated){
								valueBuilder.append("=\"");
								valueBuilder.append(strValue.replaceFirst("\\*", attrValue));
								valueBuilder.append("\"");
								bDynCheckUpdated = true;
							}
						} else {
							valueBuilder.append("=\"");
							valueBuilder.append(strValue);
							valueBuilder.append("\"");
						}
					}
					if(valueBuilder.length() > 0){
						if(dynamicCheckStrBuffer.length() > 0){
							dynamicCheckStrBuffer.append(",");
						}
						dynamicCheckStrBuffer.append(strAttributeId);
						dynamicCheckStrBuffer.append(valueBuilder.toString());
					}else{
						if(dynamicCheckStrBuffer.length() > 0){
							dynamicCheckStrBuffer.append(",");
						}
						dynamicCheckStrBuffer.append(strCheckTokens[0]);
						dynamicCheckStrBuffer.append(strCheckTokens[1]);
						dynamicCheckStrBuffer.append("\""+strCheckTokens[2]+"\"");
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Ignoring dynamic check attribute " + dynamicCheckAttr + " due to improper format. Format should be vendorid:attrid=* or vendorid:attrid=\"[*,*,...,*]\"");
				}
			}
		}
		if(bDynCheckUpdated){
			try {
				accountData.setDynamicCheckItems(dynamicCheckStrBuffer.toString());
				setDynamicCheck(request, accountData.getDynamicCheckItems(), accountData.getUserIdentity());
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "DynamicCheck: "+ accountData.getDynamicCheckItems() 
							+ "  updated for UserIdentity: " + accountData.getUserIdentity());
			} catch (UpdateAccountDataFailedException e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Error updating dynamic check items, reason: " + e.getMessage());
			}
		}
	}

	public void setDynamicCheck(RadAuthRequest request, String strDynamicCheck,String strUserIdentity)
	throws UpdateAccountDataFailedException{
		BaseAuthDriver  driver = (BaseAuthDriver)this.context.getDriver((String)request.getParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID));
		if(driver != null){
			driver.setDynamicCheck(strDynamicCheck, strUserIdentity);
		}
	}

	protected void addSessionTimeOut(RadAuthRequest request, RadAuthResponse response) {
		long defaultSessionTimeOut = data.getDefaultSessionTimeout();
		if(defaultSessionTimeOut > 0) {
			IRadiusAttribute sessionTimeoutAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			sessionTimeoutAttribute.setLongValue(defaultSessionTimeOut);
			response.addAttribute(sessionTimeoutAttribute);
		} else if(defaultSessionTimeOut < 0) {
			request.setParameter(AAAServerConstants.SESSION_TIMEOUT, AAAServerConstants.SESSION_TIMEOUT_DISABLED);
		}
		
	}

	protected void addElitecoreVSA(RadAuthRequest request,
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
			LogManager.getLogger().trace(MODULE,"Error during adding customer VSA into radius packet,reason : "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
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
	
	private String getPolicyGroupExpression(String radiusPolicyGroup) {
		if (Strings.isNullOrBlank(radiusPolicyGroup)) {
			return null;
		}

		String expressionFromPolicyGroup = radiusPolicyManager.getExpressionFrom(radiusPolicyGroup);
		
		if (Strings.isNullOrBlank(expressionFromPolicyGroup)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Configuration of policy group: " + radiusPolicyGroup + " was not found.");
			}
			return null;
		}
		
		return "( " + expressionFromPolicyGroup + " ) ";
	}
}
