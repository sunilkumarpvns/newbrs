/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr.service.Handlers;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * @author nitul.kukadia
 *
 */
public interface RDRServiceHandler extends ServiceHandler<ServiceRequest, ServiceResponse> {
	
	public boolean isEligible (ServiceRequest request, ServiceResponse response);
	public void handleRequest(ServiceRequest request,ServiceResponse response,
			ISession session);
	public void stop(); 
}
