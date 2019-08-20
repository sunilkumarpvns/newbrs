package com.elitecore.aaa.rm.service.ippool;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;

public interface RMIPPoolServiceContext extends RadServiceContext<RMIPPoolRequest, RMIPPoolResponse> {
	
	public RMIPPoolConfiguration getIPPoolConfiguration();
	public String getServiceIdentifier();

}
