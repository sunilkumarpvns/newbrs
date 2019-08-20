package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

/**
 * This interface configures ServiceHandler Factory
 * 
 */
public interface ServiceHandlerFactory {
	
	/**
	 * This method returns ServiceHandler of given {@link com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerType ServiceHandlerType}
	 * 
	 */
	public ServiceHandler serviceHandlerOf(ServiceHandlerType serviceHandlerType, PccServicePolicyConfiguration servicePolicyConfiguration) throws InitializationFailedException, IllegalArgumentException;

}
