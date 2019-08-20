package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.BaseAuthDriver;
import com.elitecore.aaa.core.policies.accesspolicy.AccessDeniedException;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class DiameterAuthorizationHandler<T extends ApplicationRequest, V extends ApplicationResponse> 
implements DiameterApplicationHandler<T, V>, SubscriberProfileRepositoryAware {

	private static final String MODULE = "DIA-AUTHORIZATION-HANDLER";
	private static final int TIME_IN_DAYS = 1000 * 60 * 60 * 24;
	
	private DiameterServiceContext serviceContext;
	private DiameterAuthorizationHandlerData data;
	private DiameterSubscriberProfileRepository accountInfoProvider;
	private DiameterPolicyManager policyManager;
	private AccessPolicyManager accessPolicyManager;
	
	public DiameterAuthorizationHandler(DiameterServiceContext context,
			DiameterAuthorizationHandlerData data) {
		this(context, data,DiameterPolicyManager.getInstance(
				DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY), AccessPolicyManager.getInstance());
	}

	public DiameterAuthorizationHandler(DiameterServiceContext context,DiameterAuthorizationHandlerData data,
			DiameterPolicyManager policyManager, AccessPolicyManager accessPolicyManager) {
		this.serviceContext = context;
		this.data = data;
		this.policyManager = policyManager;
		this.accessPolicyManager = accessPolicyManager;
	}

	public void init() throws InitializationFailedException {
		// no-op
	}

	public void handleRequest(T request, V response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Authorization process started");
		}
		
		AccountData accountData = request.getAccountData();
		try{
			if (accountData == null){
				accountData = accountInfoProvider.getAccountData(request,response);
				if (accountData == null) {
					accountData = ((DiameterSubscriberProfileRepository)accountInfoProvider).getAnonymousAccountData(request);
					if (accountData == null){
						throw new AuthorizationFailedException(DiameterErrorMessageConstants.USER_NOT_FOUND);
					} 
				}
			}
			
			LogManager.getLogger().info(MODULE, "Authorizing user with identity: [" + accountData.getUserIdentity() + "]");
			
			performAccountLevelAuthorizationChecks(request, response, accountData);
			
			applyAccountLevelPolicyChecks(request, response);
			
			applyDiameterPolicy(request, response, accountData);
			
			applyAccessPolicy(request, response, accountData);
			
			addDiameterSuccessAvp(response);
			
			addSessionTimeoutAVP(request, response);
			
		} catch (AuthorizationFailedException e){
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Authorization Failed. Reason: " + e.getMessage());
			}
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_AUTHORIZATION_REJECTED, e.getMessage());
		}
	}

	private void addSessionTimeoutAVP(T request, V response) {
		long defaultSessionTimeOut = data.getDefaultSessionTimeoutInSeconds();
		if (defaultSessionTimeOut > 0) {
			IDiameterAVP sessionTimeoutAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT_INT);
			sessionTimeoutAvp.setInteger(defaultSessionTimeOut);
			response.addAVP(sessionTimeoutAvp);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session timeout = " + data.getDefaultSessionTimeoutInSeconds() 
						+ " added in answer from policy level.");
			}
		} else if (defaultSessionTimeOut < 0) {
			request.setParameter(AAAServerConstants.SESSION_TIMEOUT, AAAServerConstants.SESSION_TIMEOUT_DISABLED);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session timeout = " + data.getDefaultSessionTimeoutInSeconds() 
						+ " is less than 0 at policy level. So no session timeout value will be" 
						+ " added in answer.");
			}
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No Session timeout value added from policy level in answer.");
			}
		}
	}

	private void addDiameterSuccessAvp(V response) {
		IDiameterAVP resultCodeAvp = response.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCodeAvp == null){
			resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			response.addAVP(resultCodeAvp);
		}
		resultCodeAvp.setInteger(ResultCode.DIAMETER_SUCCESS.code);
	}

	private void applyDiameterPolicy(T request, V response, AccountData accountData) throws AuthorizationFailedException{
		
		List<String> satisfiedPolicies = null;
		List<String> additionalSatisfiedPolicies = null;
		try {
			String strCheckItems = accountData.getCheckItem();
			String strDynamicCheck = accountData.getDynamicCheckItems();
			String strReplyItem = accountData.getReplyItem();
			if(strReplyItem==null)
				strReplyItem = "";
			request.setParameter(AAAServerConstants.CUSTOMER_REPLY_ITEM, strReplyItem);
			if (Strings.isNullOrEmpty(strDynamicCheck) == false) {
				strCheckItems = strCheckItems + "," + strDynamicCheck.trim();
			} else {
				strDynamicCheck = "";
			}
			
			String diameterPolicyGroupExpression = getPolicyGroupExpression(accountData.getAuthorizationPolicyGroup());
			
			String diameterPolicy = accountData.getAuthorizationPolicy();
			String policyToApply = "";
			
			if (Strings.isNullOrBlank(diameterPolicy) == false && Strings.isNullOrBlank(diameterPolicyGroupExpression) == false) {
				policyToApply = diameterPolicyGroupExpression + " && " + diameterPolicy;
			} else if (Strings.isNullOrBlank(diameterPolicy) == false) {
				policyToApply = diameterPolicy;
			} else if (Strings.isNullOrBlank(diameterPolicyGroupExpression) == false) {
				policyToApply = diameterPolicyGroupExpression;
			}

			if (Strings.isNullOrBlank(policyToApply) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Diameter authorization policies to apply: " + policyToApply 
							+ " for user identity: " + accountData.getUserIdentity());
				}
			}
			satisfiedPolicies = policyManager.applyPolicies(
					request.getDiameterRequest(),response.getDiameterAnswer(),
					policyToApply,
					strCheckItems,
					accountData.getRejectItem(),
					accountData.getReplyItem(),
					data.isRejectOnCheckItemNotFound(),
					data.isRejectOnRejectItemNotFound(),
					data.isContinueOnPolicyNotFound());

			if(strDynamicCheck.contains("*")){
				updateAccountLevelDynamicChecks(request, strDynamicCheck, 
						data.isRejectOnCheckItemNotFound(),accountData);
			}			
		} catch (ParserException e) {
			throw new AuthorizationFailedException(e.getMessage(), e);
		} catch (PolicyFailedException e) {
			throw new AuthorizationFailedException(e.getMessage(), e);
		}
		
		String strAdditionalPolicy = accountData.getAdditionalPolicy();
		if (Strings.isNullOrEmpty(strAdditionalPolicy) == false) {
			additionalSatisfiedPolicies = applyAdditionalDiameterPolicy(request.getDiameterRequest(), response.getDiameterAnswer(),strAdditionalPolicy,accountData.getUserIdentity());
		}	
		addSatisfiedPolicyAttr(request,response, satisfiedPolicies,additionalSatisfiedPolicies);

	}
	
	private void performAccountLevelAuthorizationChecks (
			T request, V response, AccountData accountData) throws AuthorizationFailedException {

		if(isCreditLimitExceeded(accountData))
			throw new AuthorizationFailedException(DiameterErrorMessageConstants.CREDIT_LIMIT_EXCEEDED);
		else if(isAccountInActive(accountData))
			throw new AuthorizationFailedException(DiameterErrorMessageConstants.ACCOUNT_NOT_ACTIVE);
		else if(isAccountExpired(request))
			throw new AuthorizationFailedException(DiameterErrorMessageConstants.ACCOUNT_EXPIRED);

		
	}
	private boolean isAccountExpired(T request) {
		AccountData accountData = request.getAccountData();
		if (accountData == null) {
			return true;
		}
		
		boolean bAccountExpired = true;
		Date currentDate = new Date();
		int graceType = 0;
		
		if (accountData.getExpiryDate() != null) {
			if (currentDate.before(accountData.getExpiryDate())) {
				bAccountExpired = false;
				onAccountNotExpired(request, graceType);
			} else if (isGracePolicyBoundInProfile(accountData)) {
				bAccountExpired = handleGracePolicy(request, accountData, 
						currentDate, accountData.getGracePolicy(), 
						"Customer profile");
				
			} else if (isGracePolicyBoundInConfiguration()) {
				bAccountExpired = handleGracePolicy(request, accountData, 
						currentDate, data.getGracePolicy(), 
						"NAS App policy configuration");
			}
		} else {
			if (accountData.isExpiryDateCheckRequired() == false) {
				bAccountExpired = false;
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Expiry date is not configured for Customer profile of " +
							"User-identity : "+accountData.getUserIdentity()+ ", " +
									"so considering this Customer account expired");
				}
				bAccountExpired = true;
			}
		}
		return bAccountExpired;
	}
	
	private boolean handleGracePolicy(
			T request, AccountData accountData, Date currentDate, String gracePolicyName,
			String configuredIn) {
		
		int[] gracePeriod = serviceContext.getServerContext()
				.getServerConfiguration().getGracePolicyConfiguration(gracePolicyName);
		
		if (gracePeriod != null && gracePeriod.length > 0) {
			return applyGracePeriod(request, gracePeriod, accountData, 
					currentDate, gracePolicyName);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Grace policy : " + gracePolicyName 
						+ " configured in " + configuredIn + " not found,"
						+ " so no grace period allowed for this customer");
			}
		}
		return true;
	}

	private boolean isGracePolicyBoundInConfiguration() {
		return data.getGracePolicy() != null && data.getGracePolicy().length() > 0;
	}

	private boolean isGracePolicyBoundInProfile(AccountData accountData) {
		return Strings.isNullOrEmpty(accountData.getGracePolicy()) == false;
	}

	private void onAccountNotExpired(T request, int graceType) {
		IDiameterAVP graceTypeAttr = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EC_GRACE_TYPE);
		if (graceTypeAttr != null) {
			graceTypeAttr.setInteger(graceType);
			request.addInfoAvp(graceTypeAttr);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE,"Account is not expired, so grace policy not applied.");
			}
		}
	}
	
	private boolean applyGracePeriod(T request,int[] gracePeriod,AccountData accountData,Date currentDate,String gracePolicy) {
		boolean bAccountExpired = true;

		int dayDiff = (int) Math.ceil((currentDate.getTime() - accountData.getExpiryDate().getTime())/ TIME_IN_DAYS);
		for (int i = 0; i < gracePeriod.length; i++) {
			if (dayDiff <= gracePeriod[i]) {
				IDiameterAVP graceTypeAttr = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_GRACE_TYPE);
				if (graceTypeAttr != null) {
					i=i+1;
					graceTypeAttr.setInteger(i); // Here value of i represents the Grace-Type. 
					request.addInfoAvp(graceTypeAttr);
					accountData.setGracePeriodApplicable(true);
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE," Grace-Type : "+ i+  " applied, for Grace Policy : "+ gracePolicy);
					}
					bAccountExpired=false;
					break;
				}
			}
		}
		
		if(bAccountExpired){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Can't give Grace-Period for this request, " +
						"Reason Grace policy :" + gracePolicy +" not satisfied");
			}
		}
		return bAccountExpired;
	}
	
	private void applyAccountLevelPolicyChecks(T request, V response) throws AuthorizationFailedException {

		AccountData accountData= request.getAccountData();
		validateCallingStationId(request, accountData);
		validateCalledStationId(request, accountData);
	}	

	private void validateCallingStationId(T request, AccountData accountData) throws AuthorizationFailedException {
		String strCallingStationId = accountData.getCallingStationId();
		if (Strings.isNullOrEmpty(strCallingStationId)) {
			return;
		}
			
		IDiameterAVP callingStationId = request.getAVP(DiameterAVPConstants.CALLING_STATION_ID);
		if (callingStationId != null) {
			if (matches(strCallingStationId, callingStationId) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, AuthReplyMessageConstant.INVALID_CALLING_STATION_ID + " for user identity: " + accountData.getUserIdentity());
				}
				throw new AuthorizationFailedException(DiameterErrorMessageConstants.INVALID_CALLING_STATION_ID);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Calling-Station-Id(0:31) matches with profile for user identity: " + accountData.getUserIdentity());
				}
			}
		} else if (data.isRejectOnCheckItemNotFound()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Calling-Station-Id(0:31) not found in request, " +
						"so rejecting as reject on check item is enabled");
			}
			throw new AuthorizationFailedException(DiameterErrorMessageConstants.CALLING_STATION_ID_NOT_FOUND);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Calling-Station-Id(0:31) not found in request, " +
						"but skipping as reject on check item is disabled");
			}
		}
	}

	private void validateCalledStationId(T request, AccountData accountData) throws AuthorizationFailedException {
		String strCalledStationId = accountData.getCalledStationId();
		if (Strings.isNullOrEmpty(strCalledStationId)) {
			return;
		}

		IDiameterAVP calledStationId = request.getAVP(DiameterAVPConstants.CALLED_STATION_ID);
		if (calledStationId != null) {

			if (matches(strCalledStationId, calledStationId) == false) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, AuthReplyMessageConstant.INVALID_CALLED_STATION_ID + " for user identity: " + accountData.getUserIdentity());
				}
				throw new AuthorizationFailedException(DiameterErrorMessageConstants.INVALID_CALLED_STATION_ID);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Called-Station-Id(0:30) matches with profile for user identity: " + accountData.getUserIdentity());
				}
			}
		}else if (data.isRejectOnCheckItemNotFound()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Called-Station-Id(0:30) not found in request, " +
						"so rejecting as reject on check item is enabled");
			}
			throw new AuthorizationFailedException(DiameterErrorMessageConstants.CALLED_STATION_ID_NOT_FOUND);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Called-Station-Id(0:30) not found in request, " +
						"but skipping as reject on check item is disabled");
			}
		}
	}


	private boolean matches(String value, IDiameterAVP avp) {
		StringTokenizer tokens = new StringTokenizer(value, ",;");
		
		for (int cntr = 0 ; cntr<tokens.countTokens() ; cntr++) {
			if (DiameterUtility.matches(avp.getStringValue(true), tokens.nextToken())) {
				return true;
			}
		}
		return false;
	}	
	
	private void updateAccountLevelDynamicChecks(T request, String strDynamicChecks, boolean rejectOnCheckItemNotFound, AccountData accountData) {
		StringBuffer dynamicCheckStrBuffer = new StringBuffer();
		boolean bDynCheckUpdated = false;
		String[] dynamicChecks = ParserUtility.splitString(strDynamicChecks, ',');
		if(dynamicChecks != null){
			for(int i=0; i<dynamicChecks.length; i++){
				String dynamicCheckAVP = dynamicChecks[i];
				String[] strCheckTokens = ParserUtility.splitKeyAndValue(dynamicCheckAVP);
				if(strCheckTokens.length == 3){
					String strAvpId = strCheckTokens[0];
					String strValue = strCheckTokens[2];
					boolean bValueUpdated = true;
					StringBuffer valueBuilder = new StringBuffer();
					if(strValue.contains("*")){
						IDiameterAVP diameterAVP = request.getAVP(strAvpId);
						if(diameterAVP != null){
							String attrValue = diameterAVP.getStringValue();
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
						dynamicCheckStrBuffer.append(strAvpId);
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
						LogManager.getLogger().debug(MODULE, "Ignoring dynamic check AVP " + dynamicCheckAVP + " due to improper format. Format should be vendorid:attrid=* or vendorid:attrid=\"[*,*,...,*]\"");
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

	public void setDynamicCheck(T request, String strDynamicCheck,String strUserIdentity)
	throws UpdateAccountDataFailedException{
		BaseAuthDriver  driver = (BaseAuthDriver)serviceContext.getDriver((String)request.getParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID));
		if(driver != null){
			driver.setDynamicCheck(strDynamicCheck, strUserIdentity);
		}
	}
	
	private void applyAccessPolicy(T request, ApplicationResponse response, AccountData accountData) throws AuthorizationFailedException{
		try{
			long maxSessionTime = accessPolicyManager.checkAccessPolicy(accountData.getAccessPolicy(),
					data.isContinueOnPolicyNotFound());
			if(maxSessionTime > 0){
				IDiameterAVP acctSessionTimeAvp = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT);
				if (acctSessionTimeAvp != null) {
				    long sessionTimeoutValueFromDriver = acctSessionTimeAvp.getInteger();
				    if( maxSessionTime < sessionTimeoutValueFromDriver){
				    	acctSessionTimeAvp.setInteger(maxSessionTime);
				    }
				}else {
			    	acctSessionTimeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
			    	acctSessionTimeAvp.setInteger(maxSessionTime);
			    	response.addAVP(acctSessionTimeAvp);
			    }
			}
		}catch (AccessDeniedException e1) {
			throw new AuthorizationFailedException("Access denied by Access Policy Manager. Reason: " + e1.getMessage(), e1);
		}
	}
	
	private List<String> applyAdditionalDiameterPolicy(DiameterRequest request,DiameterAnswer response, String strAdditionalPolicy,String userIdentity){
		
		List<String> satisfiedPolicies= null;
		try {
			
			satisfiedPolicies = policyManager.applyPolicies(
				request,response,
				strAdditionalPolicy,
				null,
				null,
				null,
				data.isRejectOnCheckItemNotFound(),
				data.isRejectOnRejectItemNotFound(),
				data.isContinueOnPolicyNotFound());
			
		} catch (ParserException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Failed to apply additional policy :"+strAdditionalPolicy+" for UserIdentity :"+strAdditionalPolicy+" Reason :"+e.getMessage());
		} catch (PolicyFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Failed to apply additional policy :"+strAdditionalPolicy+" for UserIdentity :"+strAdditionalPolicy+" Reason :"+e.getMessage());
		}
		return satisfiedPolicies;
	}
	
	private void addSatisfiedPolicyAttr(T request, V response,List<String> satisfiedRadiusAuthPolicy, List<String> satisfiedAdditionalPolicy){
		
		if(satisfiedRadiusAuthPolicy!=null){
			if(satisfiedAdditionalPolicy!=null){
				satisfiedRadiusAuthPolicy.addAll(satisfiedAdditionalPolicy);
			}
		}else {
			satisfiedRadiusAuthPolicy = satisfiedAdditionalPolicy;
		}
		
		if(satisfiedRadiusAuthPolicy != null && satisfiedRadiusAuthPolicy.size() > 0) {
			IDiameterAVP satisfiedPolicyAttr = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EC_SATISFIED_POLICIES);
			if(satisfiedPolicyAttr != null) {

				String satisfiedPolicyStr = "";
				for(int i=0;i<satisfiedRadiusAuthPolicy.size();i++) {
					if(i == satisfiedRadiusAuthPolicy.size()-1)
						satisfiedPolicyStr = satisfiedPolicyStr + satisfiedRadiusAuthPolicy.get(i);
					else
						satisfiedPolicyStr = satisfiedPolicyStr + satisfiedRadiusAuthPolicy.get(i) + ",";
				}
				satisfiedPolicyAttr.setStringValue(satisfiedPolicyStr);
				response.addInfoAvp(satisfiedPolicyAttr);
				LogManager.getLogger().info(MODULE, "Satisfied-Policies attribute added to response packet");

			} else {
				LogManager.getLogger().info(MODULE, "Satisfied-Policies Attribute is not found in elitecore dictonary  , so this attribute is not included in response packet.");
			}
		}
		
		request.setParameter(AAAServerConstants.SATISFIED_POLICIES, satisfiedRadiusAuthPolicy);
	}


	private boolean isAccountInActive(AccountData accountData) {
		return !"A".equalsIgnoreCase(accountData.getAccountStatus()) & !CommonConstants.STATUS_ACTIVE.equalsIgnoreCase(accountData.getAccountStatus());
	}

	private boolean isCreditLimitExceeded(AccountData accountData) {
		return accountData.isCreditLimitCheckRequired() && accountData.getCreditLimit() < 1;
	}
	


	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	
	private String getPolicyGroupExpression(String diameterPolicyGroup) {
		if (Strings.isNullOrBlank(diameterPolicyGroup)) {
			return null;
		}

		String expressionFromPolicyGroup = policyManager.getExpressionFrom(diameterPolicyGroup);
		
		if (Strings.isNullOrEmpty(expressionFromPolicyGroup)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Configuration of policy group: " + diameterPolicyGroup + " was not found.");
			}
			return null;
		}
		return "( " + expressionFromPolicyGroup + " ) ";
	}

	@Override
	public void setSubscriberProfileRepository(DiameterSubscriberProfileRepository spr) {
		this.accountInfoProvider = spr;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return accountInfoProvider.isAlive() == false;
	}
}