package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.util.Set;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;

public interface ApplicationContainer {

	public Set<ApplicationEnum> getApplications();
	public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications);
	
}
