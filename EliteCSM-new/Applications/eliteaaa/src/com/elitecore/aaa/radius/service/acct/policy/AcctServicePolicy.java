package com.elitecore.aaa.radius.service.acct.policy;


import java.util.List;

import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.conf.AccountingPolicyData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Represents the Authentication service workflow through which the request will pass
 * if the ruleset provided is satisfied.
 * 
 * @author narendra.pathai
 *
 */
public class AcctServicePolicy extends AAAServicePolicy<RadAcctRequest> 
implements RadiusServicePolicy<RadAcctRequest, RadAcctResponse> {

	private static final String MODULE = "ACCT-SP";
	private final AccountingPolicyData data;
	private LogicalExpression ruleset;
	private AccountingChainHandler handlerChain = new AccountingChainHandler();
	private RadPluginRequestHandler pluginRequestHandler;
	private Optional<ConcurrencySessionManager> sessionManager = Optional.absent();
	private RadAcctServiceHandler postResponseHandler;

	public AcctServicePolicy(RadAcctServiceContext serviceContext,
			AccountingPolicyData data) {
		super(serviceContext);
		this.data = data;
	}

	void setRuleset(LogicalExpression ruleset) {
		this.ruleset = ruleset;
	}

	@Override
	public boolean assignRequest(RadAcctRequest serviceRequest) {
		return ruleset.evaluate(
				new RequestOnlyValueProvider(serviceRequest));
	}

	@Override
	public void init() throws InitializationFailedException {
		//all the parts are initialized and passed by factory
	}

	@Override
	public void reInit() throws InitializationFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPolicyName() {
		return data.getPolicyName();
	}


	@Override
	public void handlePrePluginRequest(RadAcctRequest request, RadAcctResponse response, ISession session) {
		pluginRequestHandler.handlePreRequest(request, response, session);
	}

	@Override
	public void handlePostPluginRequest(RadAcctRequest request, RadAcctResponse response, ISession session) {
		pluginRequestHandler.handlePostRequest(request, response, session);
	}

	@Override
	public boolean checkForUserIdentityAttr(RadServiceRequest request, RadServiceResponse response) {
		boolean isValidRequest = false;
		if (request.getRadiusAttribute(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()) != null || response.getClientData().getVendorType().equals("WiMAX-DHCP")){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Request is from DHCP Server so skipping the user-identity check.");
			}					
			isValidRequest =true;
		} else {
			IRadiusAttribute radiusAttribute = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);;
			if (radiusAttribute != null) {
				int iStatusType = radiusAttribute.getIntValue();
				if(iStatusType!= RadiusAttributeValuesConstants.ACCOUNTING_ON && iStatusType != RadiusAttributeValuesConstants.ACCOUNTING_OFF){
					List<String> userIdentityList = getUserIdentities(response);
					if (userIdentityList.isEmpty()) {
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE, "No User Identity is configured in Accounting Policy: " 
									+ data.getPolicyName() + " and Client Profile: " + response.getClientData().getProfileName());
						}
						return isValidRequest;
					}
					final int configuredAttributeSize = userIdentityList.size();
					for(int i =0;i<configuredAttributeSize;i++){
						if(request.getRadiusAttribute(userIdentityList.get(i), true)!=null){
							isValidRequest =true;
							break;
						}
					}
				}else {
					isValidRequest=true;
				}	
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Service policy : "+getPolicyName()+", Acct-Status-Type attribute not found in request");
				isValidRequest = true;
			}	

		}
		
		if(isValidRequest == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "None of User Identity attributes: " + getUserIdentities(response) 
						+ " was found in request.");
			}
		}
		
		return isValidRequest;
	}

	private List<String> getUserIdentities(RadServiceResponse response){
		List<String> userIdentities = response.getClientData().getUserIdentities();
		if(Collectionz.isNullOrEmpty(userIdentities)) {
			userIdentities = data.getUserIdentities();
		}
		return userIdentities;
	}

	@Override
	public boolean isValidatePacket() {
		return data.isValidatePacket();
	}

	@Override
	public Optional<ConcurrencySessionManager> getSessionManager() {
		return sessionManager;
	}

	@Override
	public boolean applyResponseBehavior(RadAcctRequest request, RadAcctResponse response) {
		boolean responseBehaviorApplicable = handlerChain.isResponseBehaviorApplicable();
		if(responseBehaviorApplicable) {
			data.getDefaultResponseBehaviour().apply(request, response);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Skipping application of default response behavior for policy: " + data.getPolicyName());
			}
		}
		return responseBehaviorApplicable;
	}

	@Override
	public RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> newExecutor(RadAcctRequest request, RadAcctResponse response) {
		return new RadiusRequestExecutor<RadAcctRequest, RadAcctResponse>(handlerChain, request, response);
	}

	@Override
	public RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> newAdditionalExecutor(RadAcctRequest request, RadAcctResponse response) {
		return new RadiusRequestExecutor<RadAcctRequest, RadAcctResponse>(postResponseHandler, request, response);
	}

	void addHandler(RadAcctServiceHandler handler) {
		handlerChain.addHandler(handler);
	}

	void setSessionManager(Optional<ConcurrencySessionManager> sessionManager) {
		this.sessionManager = sessionManager;
	}

	void setPluginRequestHandler(RadPluginRequestHandler pluginRequestHandler) {
		this.pluginRequestHandler = pluginRequestHandler;
	}

	public AccountingPolicyData getConfiguration() {
		return data;
	}
	
	void setPostResponseHandler(RadAcctServiceHandler postResponseHandler) {
		this.postResponseHandler = postResponseHandler;
	}
	
	@Override
	public String toString() {
		return String.format("\tName = %s, Rule set = %s", data.getPolicyName(),
				data.getRuleset());
	}
}
