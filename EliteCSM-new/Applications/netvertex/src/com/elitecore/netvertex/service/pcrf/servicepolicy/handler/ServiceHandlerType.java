package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;
/**
 * 
 * The ServiceHandlerType enum represents types of {@link com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler ServiceHandler}
 * 
 */
public enum ServiceHandlerType {
	SUBSCRIBER_PROFILE_HANDLER("Subscriber Profile Handler"),
	EMEREGENCY_POLICY_HANDLER("Emergency Policy Handler"),
	USAGE_METERING_HANDLER("Usage Metering Handler"),
	DATA_RNC_REPORT_HANDLER("Data RnC Report Handler"),
	SUBSCRIBER_POLICY_HANDLER("Subscriber Policy Handler"),
	IMS_POLICY_HANDLER("IMS Policy Handler"),
	DATA_RNC_HANDLER("Data RNC Handler"),
	SESSION_HANDLER("Session Handler"),
	PCRF_SY_HANDLER("PCRF Sy Handler"),
	POLICY_CDR_HANDLER("Policy CDR Handler"),
	CHARGING_CDR_HANDLER("Charging CDR Handler"),
	AUTHENTICATION_HANDLER("Authentication Handler"),
	RNC_HANDLER("RnC Handler"),
	AUTO_SUBSCRIPTION_HANDLER(("Auto Subscription Handler")),
	RNC_REPORT_HANDLER("RnC Report Handler"),
	EVENT_RNC_HANDLER("Event RnC Handler");


	private final String serviceHandlerTypes;
	private ServiceHandlerType(String serviceHandlerTypes){
		this.serviceHandlerTypes = serviceHandlerTypes;
	}
	
	public String getName(){
		return serviceHandlerTypes;
	}
}
