package com.elitecore.aaa.rm.policies;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.aaa.rm.drivers.RMChargingCommunicatorGroup;
import com.elitecore.aaa.rm.drivers.RMChargingCommunicatorGroupImpl;
import com.elitecore.aaa.rm.drivers.RMChargingDriver;
import com.elitecore.aaa.rm.policies.conf.RMChargingPolicyConfiguration;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingRequest;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingResponse;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingServiceContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class RMChargingPolicy extends AAAServicePolicy<RMChargingRequest> {
	private static final String MODULE = "RM-CHRGNG-PLCY";
	public static final String RM_ASYNC_REQUEST_EXECUTOR = "RM_ASYNC_REQUEST_EXECUTOR";
	private static final AsyncRequestExecutor<RMChargingRequest, RMChargingResponse> NONE = new AsyncRequestExecutor<RMChargingRequest, RMChargingResponse>(){
		@Override
		public void handleServiceRequest(
				RMChargingRequest serviceRequest,
				RMChargingResponse serviceResponse) {
			// Do nothing
		}};
		
	private String policyId;
	private LogicalExpression radiusRuleSet;
	private RMChargingPolicyConfiguration policyConfiguration;
	private RMChargingCommunicatorGroup communicatorGroup;
	private RadPluginRequestHandler pluginRequestHandler;
	private AsyncRequestExecutor<ServiceRequest, ServiceResponse> asyncRequestExecutor;
	
	public RMChargingPolicy(RMChargingServiceContext serviceContext,String polocyId) {
		super(serviceContext);
		this.policyId = polocyId;
		this.policyConfiguration = serviceContext.getChargingConfiguration().getPolicyConfigration(polocyId);
		this.pluginRequestHandler = (RadPluginRequestHandler)serviceContext.createPluginRequestHandler(serviceContext.getChargingConfiguration().getPrePluginList(), serviceContext.getChargingConfiguration().getPostPluginList());
		this.communicatorGroup = new RMChargingCommunicatorGroupImpl();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "RMCharging Policy: " + policyId + ", initialization process started");
			LogManager.getLogger().info(MODULE, "RMCharging Policy" + policyId + ", parsing ruleset");
		}
		initRuleset();
		/* initializing Driver list */		
		RMChargingDriver mainDriver = null;
		Map<String, Integer> driverInstanceMap = (HashMap<String, Integer>)((HashMap<String, Integer>)policyConfiguration.getDriverInstanceIdsMap()).clone();
			
		for(Entry<String, Integer> driverDetail :driverInstanceMap.entrySet()){
			mainDriver = (RMChargingDriver) ((RMChargingServiceContext)getServiceContext()).getDriver(driverDetail.getKey());
			if (mainDriver != null) {
				communicatorGroup.addCommunicator(mainDriver,driverDetail.getValue());
			} else {
				LogManager.getLogger().warn(MODULE,"Problem in initializing Driver For Instance Id :"+driverDetail.getKey()+" Reason :Driver Not Found");
			}				
		}
		
		 asyncRequestExecutor =	new AsyncRequestExecutor<ServiceRequest, ServiceResponse>() {

					@Override
					public void handleServiceRequest(ServiceRequest serviceRequest,
							ServiceResponse serviceResponse) {
						((RMChargingServiceContext) getServiceContext()).submitAsyncRequest(
								(RMChargingRequest)serviceRequest, (RMChargingResponse)serviceResponse,NONE);
					}
				};
	}
	
	private void initRuleset() throws InitializationFailedException {
		try {
			this.radiusRuleSet = Compiler.getDefaultCompiler().parseLogicalExpression(policyConfiguration.getRuleSet());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "RMCharging Policy" + getPolicyName() + ", parse ruleset completed");
			}
		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException("Failed to parse ruleset: " 
					+ policyConfiguration.getRuleSet() , e);
		}
	}

	@Override
	public boolean assignRequest(RMChargingRequest serviceRequest) {
		return radiusRuleSet.evaluate(new RequestOnlyValueProvider(serviceRequest));
	}
	
	public void handlePrePluginRequest(RadServiceRequest request, RadServiceResponse response){
		this.pluginRequestHandler.handlePreRequest(request, response, ISession.NO_SESSION);
	}
	
	public void handleRequest(ServiceRequest request, ServiceResponse response){
		try {
			preDriverProcessing(request, response);
			
			request.setParameter(RM_ASYNC_REQUEST_EXECUTOR, asyncRequestExecutor);
			
			this.communicatorGroup.handleRequest(request, response);
		} catch (DriverProcessFailedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Driver processing failed Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(e);
			if( ((RadServiceRequest)request).getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
				response.setFurtherProcessingRequired(false);
				response.markForDropRequest();
			}else{
				response.setFurtherProcessingRequired(false);					
				((RadServiceResponse)response).setResponseMessage(e.getMessage());				
				((RadServiceResponse)response).setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			}
			
		}finally{
			//after driver processing even if success or failure call to script MUST be there
			postDriverProcessing(request, response);
		}
	}
	
	private void postDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		if(policyConfiguration.getDriverScript() != null && policyConfiguration.getDriverScript().trim().length() > 0){
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
		if(policyConfiguration.getDriverScript() != null && policyConfiguration.getDriverScript().trim().length() > 0){
			try {
				getServiceContext().getServerContext().getExternalScriptsManager().execute(policyConfiguration.getDriverScript(), DriverScript.class, "preDriverProcessing", new Class<?>[]{ServiceRequest.class, ServiceResponse.class}, new Object[]{serviceRequest, serviceResponse});
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Error in executing  \"pre\" method of driver script" + policyConfiguration.getDriverScript() + ". Reason: " + e.getMessage());
				
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	public void handlePostPluginRequest(RadServiceRequest request, RadServiceResponse response){	
		this.pluginRequestHandler.handlePostRequest(request, response, ISession.NO_SESSION);
	}
	
	@Override
	public String getPolicyName() {
		return this.policyConfiguration.getPolicyName();
	}
	
	@Override
	public String toString(){
		return policyConfiguration.toString();
	}

	@Override
	public void reInit() throws InitializationFailedException {
		this.policyConfiguration = ((RMChargingServiceContext)getServiceContext()).getChargingConfiguration().getPolicyConfigration(this.policyId); 
		initRuleset();
	}
}
