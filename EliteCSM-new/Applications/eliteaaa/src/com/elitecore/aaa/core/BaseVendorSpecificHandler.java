package com.elitecore.aaa.core;

import com.elitecore.core.servicex.ServiceContext;

public abstract class BaseVendorSpecificHandler implements VendorSpecificHandler{
	
private ServiceContext serviceContext;
	
	public BaseVendorSpecificHandler(ServiceContext context){
		serviceContext = context;
	}

	protected ServiceContext getServiceContext(){
		return serviceContext;
	}
}
