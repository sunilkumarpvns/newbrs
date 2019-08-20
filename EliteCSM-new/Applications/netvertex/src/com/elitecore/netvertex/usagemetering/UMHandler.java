package com.elitecore.netvertex.usagemetering;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

/**
 * 
 * Calculate the current usage and add or update the usage to appropriate bucket with the help of previous and reported usage.
 * UMhandler find the appropriate bucket for reported usage with the help of PCRF key "UsageReservation" which has the value  
 * key-value pair.
 * 
 * Usage reservation should have value in following form
 * 	if  gateway is pcc rule monitoring supported then
 * 		monitoring-key of pcc rule as key and basepackageId or subscriptionId as value
 * 
 * if  gateway is pcc rule monitoring not supported then
 * 		basepackageId or subscriptionId as key and quota profile id as value
 * 
 * when gateway not support pcc rule level metering then UMHandler will use only session level reported usage and calculate the usage only for "All-service" of quota profile found from usage reservation key.
 * when gateway support pcc rule level metering then UMHandler will pcc level reported usage.
 * 
 * @author harsh
 *
 */
public class UMHandler extends ServiceHandler {


	static final String MODULE = "UM-HDL";
	
	private SessionLevelUsageProcessor sessionLevelUsageProcessor;
	private PCCLevelUsageProcessor serviceLevelUsageProcessor;
	

	public UMHandler(PCRFServiceContext serviceContext) {
		super(serviceContext);
		this.sessionLevelUsageProcessor = new SessionLevelUsageProcessor(serviceContext.getServerContext());
		this.serviceLevelUsageProcessor = new PCCLevelUsageProcessor(serviceContext.getServerContext());	
	}
	

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		
		PCRFRequest request = (PCRFRequest)serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;

		String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal());
		
		if (subscriberIdentity == null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping Usage handling for subscriber ID: " + subscriberIdentity + ". Reason: Subscriber Identity not found");
			}
			return;
		}

		List<UsageMonitoringInfo> reportedUsageList = request.getReportedUsageInfoList();
		
		if (isNullOrEmpty(reportedUsageList)) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping Usage handling for subscriber ID: " + subscriberIdentity + ". Reason: Usage not reported");
			}
			return;
		}
		
		/*
		 * for Policy Group <GoldPCCRule, GoldPolicyGroup> 
		 * for AddOn <SilverPCCRule, SilverAddOn-123456>
		 */
		@Nullable Map<String, Subscription> addOnSubscriptions = null;
		try {
			addOnSubscriptions = executionContext.getSubscriptions();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching addon subscription for subscriber ID: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
		
		/*
		 * package/addon-subscription --> QuotaProfileID_Service Name --> Usage
		 */
		@Nullable ServiceUsage previousServiceUsage;
		try {
			previousServiceUsage = executionContext.getCurrentUsage();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching usage for subscriber ID: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return;
		}
		
		if (previousServiceUsage != null) {
			response.setServiceUsageTillLastUpdate(previousServiceUsage);
		}
		
		response.setSessionUsageTillLastSessionUpdate(request.getSessionUsage());
		
		
		Map<String, String> usageReservations = response.getUsageReservations();
		/*
		 * Currently, We are depends on usageReservation string. If it is not found then we can not perform usage metering.
		 * 
		 */
		if(Maps.isNullOrEmpty(usageReservations)) {
			LogManager.getLogger().warn(MODULE, "Unable to meter usage for subscriber ID: " + subscriberIdentity + ". Reason: Usage reservation key not found");
			return;
		}
		
		SessionUsage currentSessionUsage = null;
		if (request.getSessionUsage() == null) {
			currentSessionUsage = new SessionUsage();
		} else {
			try {
				currentSessionUsage = (SessionUsage) request.getSessionUsage().clone();
			} catch (CloneNotSupportedException ex) {
				getLogger().error(MODULE, "Error while cloning session usage. Reason: " + ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
		

		ServiceUsage currentServiceUsage = null;
		if (previousServiceUsage == null) {
			currentServiceUsage = new ServiceUsage();
		} else {
			try {
				currentServiceUsage = (ServiceUsage) previousServiceUsage.clone();
			} catch (CloneNotSupportedException ex) {
				getLogger().error(MODULE, "Error while cloning service usage. Reason: " + ex.getMessage());
				getLogger().trace(MODULE, ex);
			}
		}
		
		if (currentServiceUsage == null && currentSessionUsage == null) {
			getLogger().warn(MODULE, "Unable to meter session and service usage for subscriber ID: " + subscriberIdentity + ". Reason: Error while creating service and session usage");
			return;
		}
		
		if(PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(request.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))){
			sessionLevelUsageProcessor.process(response, executionContext, reportedUsageList, addOnSubscriptions, currentServiceUsage, currentSessionUsage);
		} else {
			serviceLevelUsageProcessor.process(response, executionContext, addOnSubscriptions, currentServiceUsage, currentSessionUsage, reportedUsageList);
		}
		
	}


	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

	}


	@Override
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		
	}
	
	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.USAGE_REPORT);
	}

	@Override
	public String getName() {
		return "UM-Handler";
	}

}
