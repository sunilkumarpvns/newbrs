package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.drivers.IEliteCrestelRatingDriver;
import com.elitecore.aaa.core.drivers.TranslatorCommunicatorGroup;
import com.elitecore.aaa.core.drivers.TranslatorCommunicatorGroupImpl;
import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CcServicePolicyConfigurationData;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.ResponseAttributeAdditionHandler;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class CCAppPolicy extends AAAServicePolicy<ApplicationRequest> 
implements ApplicationPolicy {
	private static final String MODULE = "CC-APP-PLCY";
	private LogicalExpression ruleSet;
	private String policyId;
	private CcServicePolicyConfigurationData policyConfiguration;
	private TranslatorCommunicatorGroup translatorGroup;
	private ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> responseAttributeAdditionHandler;
	private DiameterPluginManager pluginManager;
	private final @Nullable DiameterSessionManager diameterSessionManager;
	private DefaultResponseBehavior defaultResponseBehavior;

	public CCAppPolicy(DiameterServiceContext serviceContext,String policyId, 
			@Nullable DiameterSessionManager diameterSessionManager) {
		super(serviceContext);
		this.policyId = policyId;
		policyConfiguration = (CcServicePolicyConfigurationData)serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		translatorGroup = new TranslatorCommunicatorGroupImpl(serviceContext.getServerContext());
		this.responseAttributeAdditionHandler = new ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>(serviceContext, policyId);
		
		this.pluginManager = new DiameterPluginManager(serviceContext.getServerContext().getDiameterPluginManager().getNameToPluginMap());
		this.diameterSessionManager = diameterSessionManager;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "CC Policy: " + policyConfiguration.getName() + ", initialization process started");
		
		initRuleset();
		
		/* initializing Driver list */	
		Map<String, Integer> driverInstanceMap = policyConfiguration.getDriverInstanceIdMap();
		IEliteCrestelRatingDriver mainDriver = null;
		for(Entry<String, Integer> driverDetail :driverInstanceMap.entrySet()){
			mainDriver = (IEliteCrestelRatingDriver)((DiameterServiceContext)getServiceContext()).getDriver(driverDetail.getKey());
			if (mainDriver != null) {					
				translatorGroup.addCommunicator(mainDriver,driverDetail.getValue());
			} else {
				LogManager.getLogger().warn(MODULE,"Problem in initializing Driver For Instance Id :"+driverDetail.getKey()+" Reason :Driver Not Found");
			}				
		}
		
		createDefaultResponseBehavior();
		
		/* initializing Post Request Handler */
		this.responseAttributeAdditionHandler.init();
		
		pluginManager.registerInPlugins(policyConfiguration.getPrePlugins());
		LogManager.getLogger().info(MODULE, "Registered Policy level In plugins: " + policyConfiguration.getPrePlugins());

		pluginManager.registerOutPlugins(policyConfiguration.getPostPlugins());
		LogManager.getLogger().info(MODULE, "Registered Policy level Out plugins: " + policyConfiguration.getPostPlugins());
	}

	private void createDefaultResponseBehavior() {

		this.defaultResponseBehavior = DefaultResponseBehavior.create(policyConfiguration.getDefaultResponseBehaviorType(), 
					policyConfiguration.getDefaultResponseBehaviorParameter());
	}

	private void initRuleset() throws InitializationFailedException{
		try {
			LogManager.getLogger().info(MODULE, "CC Policy: " + policyId + ", parsing ruleset");
			ruleSet = Compiler.getDefaultCompiler().parseLogicalExpression(policyConfiguration.getRuleSet());
			LogManager.getLogger().info(MODULE, "CC Policy: " + policyId + ", parse ruleset completed");
		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException("Failed to parse ruleset: " 
					+ policyConfiguration.getRuleSet() , e);
		}
	}

	@Override
	public boolean assignRequest(ApplicationRequest serviceRequest) {
		return ruleSet.evaluate(new DiameterAVPValueProvider(serviceRequest.getDiameterRequest()));
	}
	
	@Override
	public void handleRequest(ApplicationRequest request,
			ApplicationResponse response, DiameterSession session) {
		
		if (applyDefaultResponseBehavior(request, response)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Default Response behavior applied");
			}
			return;
		}
		
		locateSession(request, response);
		
		pluginManager.applyInPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
		
		try {
			preDriverProcessing(request, response);
			translatorGroup.translate(request, response);
		} catch (TranslationFailedException e) {
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);						
			resultCodeAvp.setInteger(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
			response.addAVP(resultCodeAvp);
			response.setFurtherProcessingRequired(false);
		} finally {
			if (response.isFurtherProcessingRequired()) {
				responseAttributeAdditionHandler.handleRequest(request, response, session);
			}
			postDriverProcessing(request, response);
		}
		
		pluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
	}
	
	public void locateSession(ApplicationRequest request,
			ApplicationResponse response) {
		if (diameterSessionManager != null && policyConfiguration.isSessionManagementEnabled()) {
			List<SessionData> sessions = locateSession(request.getDiameterRequest(), response.getDiameterAnswer());
			setApplicationFlagsFromParameter(response);
			if (sessions != null) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, sessions.size() + " session(s) located");
				}
				request.getDiameterRequest().setLocatedSessionData(sessions);
			}
		}
	}
	
	private void setApplicationFlagsFromParameter(ApplicationResponse applicationResponse) {
		Boolean parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.MARK_FOR_DROP_REQUEST);
		if(parameter != null && parameter) {
			DiameterProcessHelper.dropResponse(applicationResponse);
		}
		parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.FURTHER_PROCESSING_REQUIRED);
		if (parameter != null) {
			applicationResponse.setFurtherProcessingRequired(parameter);
		}
		parameter = (Boolean)applicationResponse.getParameter(IDiameterSessionManager.PROCESSING_COMPLETED);
		if (parameter != null) {
			applicationResponse.setProcessingCompleted(parameter);
		}
	}
	
	private List<SessionData> locateSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Locating session for Diameter Packet with HbH-ID = " 
						+ diameterRequest.getHop_by_hopIdentifier() + " and EtE-ID = " + diameterRequest.getEnd_to_endIdentifier());
		}

		return diameterSessionManager.locate(diameterRequest, diameterAnswer);
	}

	private void postDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(policyConfiguration.getDriverScript() != null){
			try {
				getServiceContext().getServerContext().getExternalScriptsManager().execute(policyConfiguration.getDriverScript(), DriverScript.class, "postDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing  \"post\" method of driver script" + policyConfiguration.getDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	private void preDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(policyConfiguration.getDriverScript() != null){
			try {
				getServiceContext().getServerContext().getExternalScriptsManager().execute(policyConfiguration.getDriverScript(), DriverScript.class, "preDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing  \"pre\" method of driver script" + policyConfiguration.getDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	@Override
	public String getPolicyName() {		
		return policyConfiguration.getName();
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		policyConfiguration = (CcServicePolicyConfigurationData)((DiameterServiceContext)getServiceContext()).getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		initRuleset();
		/* Re-initializing Post Request Handler */
		this.responseAttributeAdditionHandler.reInit();
	}
	
	public boolean isSessionManagementEnabled() {
		return policyConfiguration.isSessionManagementEnabled();
	}

	private boolean applyDefaultResponseBehavior(
			ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		boolean isResponseBehaviorApplicable = translatorGroup.isAlive() == false;
		if(isResponseBehaviorApplicable){
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Request is eligible for Default Response behavior.");
			}
			defaultResponseBehavior.apply(applicationRequest, applicationResponse);
			return true;
		}
		return false;
	}
}
