package com.elitecore.aaa.core.authprotocol;

import com.elitecore.core.servicex.ServiceContext;

public abstract class BaseAuthMethodHandler implements IAuthMethodHandler {
	
	private ServiceContext serviceContext;
	
	public BaseAuthMethodHandler(ServiceContext context){
		serviceContext = context;
	}

	protected ServiceContext getServiceContext(){
		return serviceContext;
	}
	
}
