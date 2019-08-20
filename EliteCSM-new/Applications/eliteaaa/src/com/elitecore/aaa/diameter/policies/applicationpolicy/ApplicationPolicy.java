package com.elitecore.aaa.diameter.policies.applicationpolicy;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;


/**
 * The {@code ApplicationPolicy} interface defines a set of behaviors which must be present in all the application policies.
 * It must be implemented by every application policy .

 * @author vicky singh  
 * @since 6.8
 */
public interface ApplicationPolicy extends ServicePolicy<ApplicationRequest> {
	
	void handleRequest(ApplicationRequest appRequest,
			ApplicationResponse appResponse, DiameterSession session);
}
