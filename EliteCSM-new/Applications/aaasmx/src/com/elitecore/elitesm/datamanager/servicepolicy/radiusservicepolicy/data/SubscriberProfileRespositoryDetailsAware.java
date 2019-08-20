package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;

/**
 * All the {@link RadServiceHandler}s that need to know about the details of the
 * subscriber profile repository should implement this interface. The details are
 * provided via setter injection.
 * 
 * @author narendra.pathai
 *
 */
public interface SubscriberProfileRespositoryDetailsAware {
	
	/**
	 * The details of the subscriber profile repository are injected after the instance
	 * of handler has been created, but before being initialized
	 * 
	 * @param details a non-null 
	 */
	public void setSubscriberProfileRepositoryDetails(RadiusSubscriberProfileRepositoryDetails details);
}
