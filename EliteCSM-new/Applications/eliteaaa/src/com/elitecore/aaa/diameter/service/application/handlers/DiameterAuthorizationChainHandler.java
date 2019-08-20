package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.Iterator;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;

/**
 * Represents a sequence of Diameter Authorization handlers. e.g.  DiameterAuthWimaxHandler, DiameterAuthorizationHandler
 * @see DiameterChainHandler
 * @author khushbu.chauhan
 *
 */
public class DiameterAuthorizationChainHandler extends DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest,ApplicationResponse>>  implements SubscriberProfileRepositoryAware {

	/**
	 * Sets the subscriber profile repository to the handlers which are aware to it
	 * 
	 * @param spr Subscriber Profile Repository
	 */
	
	@Override	
	public void setSubscriberProfileRepository(DiameterSubscriberProfileRepository spr) {
		Iterator<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> iterator = iterator() ;
		while (iterator.hasNext()) {
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> next = iterator.next();
			if (next instanceof SubscriberProfileRepositoryAware) {
				((SubscriberProfileRepositoryAware) next).setSubscriberProfileRepository(spr);
			}
		}
	}
}
