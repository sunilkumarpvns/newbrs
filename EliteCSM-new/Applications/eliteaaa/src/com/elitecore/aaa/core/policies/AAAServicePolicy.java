package com.elitecore.aaa.core.policies;

import com.elitecore.aaa.core.services.AAAServiceContext;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.core.servicex.ServiceRequest;

public abstract class AAAServicePolicy<R extends ServiceRequest> extends BaseServicePolicy<R> implements ReInitializable{

	public AAAServicePolicy(AAAServiceContext serviceContext) {
		super(serviceContext);
	}
}
