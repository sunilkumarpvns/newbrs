package com.elitecore.nvsmx.system.groovy;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.nvsmx.ws.resetusage.request.ResetBillingCycleWSRequest;
import com.elitecore.nvsmx.ws.resetusage.response.ResetBillingCycleResponse;

public abstract class ResetUsageWsScript {
	
	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(getModule(), "ResetUsageWS script(" + getName() + ") initialized successfully");
		}
	}
	
	abstract public String getName();
	
	public void preResetBillingCycle(ResetBillingCycleWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getModule(), "preResetBillingCycle of ResetUsageWsScript(" + getName() + ") is called");
		}
	}
	
	public void postpreResetBillingCycle(ResetBillingCycleWSRequest request, ResetBillingCycleResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getModule(), "postResetBillingCycle of ResetUsageWsScript(" + getName() + ") is called");
		}
	}
	
	public abstract String getModule();

}
