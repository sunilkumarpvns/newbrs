
package com.elitecore.core.serverx.servicepolicy;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;

/**
 *
 * The <code>ServicePolicy</code> interface represents policies and configuration
 * to handle service request.
 * 
 * @author Subhash Punani
 *
 * @param <R> represents the type of request the policy handles
 */
public interface ServicePolicy<R extends ServiceRequest> {
	public boolean assignRequest(R request);
	public String getPolicyName();
	public void init() throws InitializationFailedException;
}
