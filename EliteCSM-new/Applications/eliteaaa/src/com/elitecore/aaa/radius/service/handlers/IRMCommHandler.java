/**
 * 
 */
package com.elitecore.aaa.radius.service.handlers;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * @author pulin
 *
 */
public interface IRMCommHandler {

	public boolean isEligible(ServiceRequest request);
	
	public void init() throws InitializationFailedException;
	
	public void handleRequest(ServiceRequest request, ServiceResponse response);
}
