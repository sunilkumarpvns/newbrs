
package com.elitecore.netvertex.service.pcrf.servicepolicy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestAction;
import com.elitecore.corenetvertex.constants.ServicePolicyActions;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.servicepolicy.ServicePolicy;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.radius.ValueProviderImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerFactory;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerType;

/**
 *
 * @author Manjil Purohit
 */
public class PCRFServicePolicy extends ServicePolicy<ServiceHandler> {
	private static final String MODULE = "PCRF-SRV-PLC";

	private PccServicePolicyConfiguration servicePolicyConfiguration;
	private ServiceHandlerFactory serviceHandlerFactory;

	public PCRFServicePolicy(PccServicePolicyConfiguration pcrfServicePolicyConfiguration,
                             ServiceHandlerFactory serviceHandlerFactory,
                             ServiceContext serviceContext) {
		super(pcrfServicePolicyConfiguration.getRuleset(), serviceContext);
		this.servicePolicyConfiguration = pcrfServicePolicyConfiguration;
		this.serviceHandlerFactory = serviceHandlerFactory;
	}

	/**
	 * All service handlers will be initialized and added to <code>ArrayList</code>. 
	 * Service handlers should be executed in following order.<br><br>
	 * 1) <code>SubscriberProfileHandler</code><br>
	 * 2) <code>AuthenticationHandler</code><br>
	 * 3) <code>DynamicSubscriberPolicyHandler</code><br>
	 * 4) <code>CumulativeUsageHandler</code><br>
	 * 5) <code>UsageMeteringHandler</code><br>
	 * 6) <code>QuotaHandler</code><br>
	 * 7) <code>PCRFSyHandler</code><br>
	 * 8) <code>SubscriberPolicyHandler</code><br>
	 * 9) <code>SessionHandler</code><br>
	 * 10) <code>CDRHandler</code><br>
	 */
	@Override
	public void init() throws InitializationFailedException{
	
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Initializing PCRF Service Policy : " + servicePolicyConfiguration.getName());
		}
		super.init();

		if(servicePolicyConfiguration.getAction() == RequestAction.DROP_REQUEST){
			return;
		}

		if(servicePolicyConfiguration.getIdentityAttribute() == null || servicePolicyConfiguration.getIdentityAttribute().isEmpty() == true)  {
			throw new InitializationFailedException("Missing Identity Attribute");
		}

		DeploymentMode deploymentMode = ((NetVertexServerContext) getServiceContext().getServerContext()).getServerConfiguration().getSystemParameterConfiguration().getDeploymentMode();

		try{
			//	Subscriber Profile handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.SUBSCRIBER_PROFILE_HANDLER, servicePolicyConfiguration));

			// Emergency Policy Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.EMEREGENCY_POLICY_HANDLER, servicePolicyConfiguration));
			
			//	Authentication Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.USAGE_METERING_HANDLER, servicePolicyConfiguration));

			// Data RnC Report Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.DATA_RNC_REPORT_HANDLER, servicePolicyConfiguration));
			
			// RnC Report Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.RNC_REPORT_HANDLER, servicePolicyConfiguration));
			
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.AUTHENTICATION_HANDLER, servicePolicyConfiguration));

			// PCRF Sy Handler
			if(servicePolicyConfiguration.getSyGateway() != null){
				addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.PCRF_SY_HANDLER, servicePolicyConfiguration));
			}else{
				LogManager.getLogger().warn(MODULE, "Skipping PCRF Sy Handler for service policy: " + servicePolicyConfiguration.getName() +". Reason: Sy Gateways not configured" );
			}

			//Auto Subscription Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.AUTO_SUBSCRIPTION_HANDLER, servicePolicyConfiguration));

			if (DeploymentMode.PCC == deploymentMode || DeploymentMode.PCRF == deploymentMode) {
				//	Subscriber Policy Handler
				addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.SUBSCRIBER_POLICY_HANDLER, servicePolicyConfiguration));
			}

			// IMS Policy Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.IMS_POLICY_HANDLER, servicePolicyConfiguration));

			if (DeploymentMode.PCC == deploymentMode || DeploymentMode.OCS == deploymentMode) {
				//Data RnC Handler
				addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.DATA_RNC_HANDLER, servicePolicyConfiguration));
			}

			//RnC Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.RNC_HANDLER, servicePolicyConfiguration));

			//Event RnC Handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.EVENT_RNC_HANDLER, servicePolicyConfiguration));

			//	Session handler
			addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.SESSION_HANDLER, servicePolicyConfiguration));

			//	Policy CDR Handler
			if(servicePolicyConfiguration.getPolicyCdrDriver() != null) {
				addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.POLICY_CDR_HANDLER,servicePolicyConfiguration));
			}else{
				if(getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Skipping Policy CDR for service policy: " + servicePolicyConfiguration.getName() + ". Reason: Policy CDR is not configured");
				}
			}

			//	Charging CDR Handler
			if(servicePolicyConfiguration.getChargingCdrDriver() != null) {
				addHandler(serviceHandlerFactory.serviceHandlerOf(ServiceHandlerType.CHARGING_CDR_HANDLER,servicePolicyConfiguration));
			}else{
				if(getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Skipping Charging CDR for service policy: " + servicePolicyConfiguration.getName() +". Reason: CDR Driver not configured" );
				}
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "PCRF Service Policy: " + servicePolicyConfiguration.getName() + " initialization completed");
			}
		}catch(IllegalArgumentException ex){
			throw new InitializationFailedException("Failed to Initialized service policy: " + servicePolicyConfiguration.getName() + ", Reason: "+ ex.getMessage(), ex);
		}
	}
	@Override
	protected void preHandleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		if(servicePolicyConfiguration.getAction() == RequestAction.DROP_REQUEST){
			serviceResponse.markForDropRequest();
			((PCRFResponse)serviceResponse).setFurtherProcessingRequired(false);
			((PCRFResponse)serviceResponse).setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_RESPONSE_DROPPED.val);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "PCRF Response marked as dropped. Reason: Service Policy Action: " + ServicePolicyActions.DROPREQUEST.getName());
			}
		}
		
		/*
		 * Usage-Metering required SubscriberIdentity we required to check whether 
		 * subscriber identity is in PCRFRequest and if there is any Usage-Metering related event
		 * in PCRFRequet then required to add Authenticate event in PCRFREqeust to fetch profile to get Subscriber Identity
		 *  
		 */
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.USAGE_REPORT)) {
			String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
			
			if (subscriberIdentity == null) {
				if(getLogger().isLogLevel(LogLevel.DEBUG)) {
					getLogger().debug(MODULE, "Adding "+ PCRFEvent.AUTHENTICATE +" event to fetch profile for Session-ID = " 
							+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) + ". Reason: Subscriber Identity not found in PCRFRequest");
				}
				
				pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
			}
		}	
	}

	@Override
	protected void postHandleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		// IGNORED
	}


	@Override
	public boolean assignRequest(ServiceRequest request,ServiceResponse response) {
		boolean servicePolicyApplied;
		
		if(expression != null){
			LogicalExpression expression = (LogicalExpression)this.expression;
			servicePolicyApplied = expression.evaluate(new ValueProviderImpl((PCRFRequest) request));
		}else{
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Considering Expression evaluation TRUE for Service Policy: " + servicePolicyConfiguration.getName() + ". Reason: Expression is NULL");
			servicePolicyApplied = true;
		}


		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Applying Service Policy : " + servicePolicyConfiguration.getName() + ". Result: " + servicePolicyApplied);
		
		return servicePolicyApplied;
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	class ValueProviderImpl implements ValueProvider{ 
		private PCRFRequest request;
		public ValueProviderImpl(PCRFRequest request) {
			this.request = request;
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException,MissingIdentifierException {
			return Long.parseLong(request.getAttribute(identifier));
		}

		@Override
		public String getStringValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException {
			return request.getAttribute(identifier);
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			String value = request.getAttribute(identifier);
			if(value!=null){
				List<String> stringValues=new ArrayList<String>(1);	
				stringValues.add(value);
				return stringValues;
			}else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			String value = request.getAttribute(identifier);
			if(value != null){
				try{
					List<Long> longValues=new ArrayList<Long>(1);
					longValues.add(Long.parseLong(value));
					return longValues;
				}catch(Exception e){
					throw new InvalidTypeCastException(e.getMessage(), e);
				}
			} else{
				throw new MissingIdentifierException("Object not found: "+identifier);
			}
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		if(servicePolicyConfiguration.getAction() == RequestAction.DROP_REQUEST){
			out.println("Action = DROP REQUEST");
			return out.toString();
		}
		
		out.println(" -- PCRF Service Policy Active Handlers -- ");
        for(ServiceHandler handler : serviceHandlerList){
			out.println("      " + handler.getName());
		}
		return out.toString();
	}

	@Override
	public String getServicePolicyName() {
		return servicePolicyConfiguration.getName();
	}
	
	@Override
	public String getIdentityAttribute() {
		return servicePolicyConfiguration.getIdentityAttribute();
	}
}
