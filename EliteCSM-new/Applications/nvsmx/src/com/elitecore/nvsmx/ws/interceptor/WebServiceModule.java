package com.elitecore.nvsmx.ws.interceptor;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.ws.resetusage.ResetUsageWS;
import com.elitecore.nvsmx.ws.sessionmanagement.SessionManagementWS;
import com.elitecore.nvsmx.ws.subscriberprovisioning.SubscriberProvisioningWS;
import com.elitecore.nvsmx.ws.subscription.SubscriptionWS;

public enum WebServiceModule {
	
	SESSION_MANAGEMENT_WS (SessionManagementWS.class.getSimpleName()){
		
		@Override
		public boolean hasReachedTPSLimit() {
		    int SESSION_MANAGEMENT_WS_THRESHOLD = 80;
			return isThresholdReached(SESSION_MANAGEMENT_WS_THRESHOLD);
		}
	},
	
	SUBSCRIPTION_WS	(SubscriptionWS.class.getSimpleName()){
		@Override
		public boolean hasReachedTPSLimit() {
			int SUBSCRIPTION_WS_THRESHOLD 	= 90;			
			return isThresholdReached(SUBSCRIPTION_WS_THRESHOLD);
		}
	},
	
	SUBSCRIBER_PROVISIONING_WS (SubscriberProvisioningWS.class.getSimpleName()){
		@Override
		public boolean hasReachedTPSLimit() {
			return false;
		}
	},
	
	RESET_USAGE_WS (ResetUsageWS.class.getSimpleName()){
		@Override
		public boolean hasReachedTPSLimit() {
			 int RESET_USAGE_WS_THRESHOLD 	= 80;		
			return isThresholdReached(RESET_USAGE_WS_THRESHOLD);
		}
	};
	
	private static final String WS_MODULE = "WS-MODULE";
	private String name;
	private static Map<String,WebServiceModule> wsMap = new HashMap<String, WebServiceModule>();
	
	static {
		for(WebServiceModule module : values()){
			wsMap.put(module.name, module);
		}
	}
	
	private WebServiceModule(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static WebServiceModule fromName(String name){
		return wsMap.get(name);
	}
	
	public abstract boolean hasReachedTPSLimit();
	
	public boolean isThresholdReached(int thresholdLimit){
		
		int webServiceTps = ConfigurationProvider.getInstance().getWebServiceTps();
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(WS_MODULE, "Configured TPS for Web-Services = '"+webServiceTps+"'");
		}
		
		long totalAllowedTpm = webServiceTps * 60;
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(WS_MODULE, "Max allowed TPM for Web-Services = '"+totalAllowedTpm+"'");
		}
		
		WebServiceStatistics globalStatistics = WebServiceStatisticsManager.getInstance().getWSGlobalStatistics();
		long currentTpm =  globalStatistics.getLastMinutesTotalRequest();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(WS_MODULE, "Current TPM for Web-Services = '"+currentTpm+"'");
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(WS_MODULE, "Configured Threshold limit for Web-Service '"+name()+"' is '"+thresholdLimit+"%' of Max allowed");
		}
		
		int totalAllowedThresholdTpm = Math.round((thresholdLimit*totalAllowedTpm)/100);
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(WS_MODULE, "Based on Threshold limit of '"+thresholdLimit+"%', Web-Service '"+name()+"' will be allowed upto  '"+totalAllowedThresholdTpm+"' TPM");
		}
		
		if( currentTpm > totalAllowedThresholdTpm ){
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(WS_MODULE, "No call allowed for Web-Service '"+name()+"', Current TPM is already above '"+totalAllowedThresholdTpm+"'.");
			}	
			return true;
		}
		
		return false;
	}
}
