/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service.Handlers;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceRequest;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeServiceHandler extends ServiceHandler<GTPPrimeServiceRequest, GTPPrimeServiceResponse> {
	public boolean isEligible (GTPPrimeServiceRequest request, GTPPrimeServiceResponse response);
	public void handleRequest(GTPPrimeServiceRequest request, GTPPrimeServiceResponse response,
			ISession session);
	public void stop(); 
}
