package com.elitecore.coreeap.commons.policy;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.policy.EapPolicyValueConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

public class EapPolicy {
	private static final String MODULE = "EAP POLICY";	
	private int method;
	private Integer decision;
	
	public EapPolicy() {
		
	}

	public int getNextMethod(int configuredMethod){				
		if(method>0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "EAP Policy selects Method: " + EapPolicyValueConstants.getName(method));
			return method;
		}
		else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "EAP Policy selects Method: " + EapPolicyValueConstants.getName(configuredMethod));
			return configuredMethod;
		}
	}
	public int getDecision(){				
		if(decision != null)
			return (decision);
		else
			return EapPolicyValueConstants.CONTINUE.value;
	}
	public boolean doPickUp(int respMethod){		
		return EapTypeConstants.isValid(respMethod);
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public void setDecision(Integer decision) {
		this.decision = decision;
	}	
}
