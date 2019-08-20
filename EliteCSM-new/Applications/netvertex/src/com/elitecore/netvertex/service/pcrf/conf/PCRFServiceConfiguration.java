
package com.elitecore.netvertex.service.pcrf.conf;

import com.elitecore.corenetvertex.util.ToStringable;

/**
 * This interface configure the PCRF services
 * @author Harsh Patel
 *
 */
public interface PCRFServiceConfiguration extends ToStringable {

	/**
	 * This method returns the queue size
	 * @return int
	 */
	public int getQueueSize();

	/**
	 * This method returns minimum thread
	 * @return int
	 */
	public int getMinimumThread();

	/**
	 * This method returns maximum thread
	 * @return int
	 */
	public int getMaximumThread();

	/**
	 * This method returns worker thread priority
	 * @return int
	 */
	public int getWorkerThreadPriority();

}
