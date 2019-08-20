package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;

public class ExclusiveApplicationContainer implements ApplicationContainer {

	private static final String MODULE = "EXCLUSIVE-APP-CONTAINER";
	Set<ApplicationEnum> applications;
	private String exclusiveApplicationString;
	
	public ExclusiveApplicationContainer(Set<ApplicationEnum> applications) {
		this.applications = applications;
		
		StringBuilder exclusiveAppLst = new StringBuilder("Exclusive Applications:[");
		for(ApplicationEnum applicationEnum : this.applications){
			exclusiveAppLst.append(applicationEnum.getApplicationType());
			exclusiveAppLst.append('=');
			exclusiveAppLst.append(applicationEnum.getVendorId());
			exclusiveAppLst.append(':');
			exclusiveAppLst.append(applicationEnum.getApplicationId());
			exclusiveAppLst.append(',');
		}
		if (this.applications.isEmpty() == false) {
			exclusiveAppLst.deleteCharAt(exclusiveAppLst.length() -1);
		}
		exclusiveAppLst.append(']');
		exclusiveApplicationString = exclusiveAppLst.toString();
	}

	@Override
	public Set<ApplicationEnum> getApplications() {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, exclusiveApplicationString); 
		}
		return applications;
	}

	@Override
	public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, exclusiveApplicationString); 
		}
		return applications;
	}

}
