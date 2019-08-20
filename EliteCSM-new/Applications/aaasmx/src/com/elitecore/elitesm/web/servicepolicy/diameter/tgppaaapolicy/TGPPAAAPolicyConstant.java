package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;



/**
 * @author nayana.rathod
 *
 */
public class TGPPAAAPolicyConstant {
	
	public static String PROFILE_LOOKUP_HANDLER = "DiameterSubscriberProfileRepositoryDetails";	
	public static String AUTHENTICATION_HANDLER = "DiameterAuthenticationHandlerData";
	public static String AUTHORIZATION_HANDLER = "DiameterAuthorizationHandlerData";
	public static String PLUGIN_HANDLER = "DiameterPluginHandlerData";
	public static String PROXY_COMMUNICATION_HANDLER="DiameterSynchronousCommunicationHandlerData";
	public static String BROADCAST_COMMUNICATION_HANDLER="DiameterBroadcastCommunicationHandlerData";
	public static String CDR_HANDLER = "DiameterCDRGenerationHandlerData"; 
	public static String CONCURRENCY_HANDLER="DiameterConcurrencyHandlerData";

	/* This constant is used for Radius Proxy Handler and Radius Broadcast Handler of 3GPP AAA Policy*/
	public static final long RADAUTH_ESI = 1;
	public static final long RADACCT_ESI = 2;
	public static final long IP_POOL_SERVER_ESI = 3;
	public static final long CHARGING_GATEWAY_ESI = 6;
	public static final long NAS_ESI = 7;
}
