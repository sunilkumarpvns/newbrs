package com.elitecore.aaa.radius.policies.servicepolicy;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface RadiusServicePolicy<T extends RadServiceRequest, V extends RadServiceResponse>
extends ServicePolicy<T> {
	
	public void handlePrePluginRequest(T request, V response, ISession session);
	public void handlePostPluginRequest(T request, V response, ISession session);	

	public boolean checkForUserIdentityAttr(RadServiceRequest request,RadServiceResponse response) ;
	public boolean isValidatePacket();
	public Optional<ConcurrencySessionManager> getSessionManager();
	
	/**
	 * ELITEAAA-2168, Configurable Response behavior, this method will return whether the 
	 * response action configured in the service policy configuration was applied or not. If 
	 * applied successfully the request processing should be stopped.
	 * @return true, if all the drivers or external systems are dead (depends on the configuration of 
	 * the service policy), false otherwise. If any of the external systems or any of primary/secondary
	 * drivers alive then false is returned and response action is not applied.
	 */
	public boolean applyResponseBehavior(T request, V response);
	
	public RadiusRequestExecutor<T, V> newExecutor(T request, V response);
	public RadiusRequestExecutor<T, V> newAdditionalExecutor(T request, V response);
}
