package com.elitecore.aaa.core.subscriber.conf;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;

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
	public void setSubscriberProfileRepositoryDetails(
			@Nonnull RadiusSubscriberProfileRepositoryDetails details);
}
