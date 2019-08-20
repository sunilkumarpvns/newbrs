package com.elitecore.core.serverx.servicepolicy;

import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;

public abstract class BaseServicePolicy<R extends ServiceRequest> implements ServicePolicy<R> {
	
	private ServiceContext serviceContext;
	
	public BaseServicePolicy(ServiceContext serviceContext) {		
		this.serviceContext = serviceContext;
	}
		
	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public ServiceContext getServiceContext() {
		return serviceContext;
	}

}
