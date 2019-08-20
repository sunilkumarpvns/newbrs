package com.elitecore.core.systemx.esix.loadBalancer;

import com.elitecore.core.systemx.esix.ESCommunicator;
/**
 * Load Balancer defines the way Communicators behave in group 
 * 
 * @param <T> is any type of {@link ESCommunicator}
 */
public interface LoadBalancer<T extends ESCommunicator> {

	int DEFAULT_WEIGHT = 1;
	int SECONDARY_WEIGHT = 0;
	/**
	 * 
	 * @return Communicator of Type T
	 */
	public T getCommunicator();
	
	/**
	 * Add communicator to the Group
	 *  
	 * @param esCommunicator of Type T 
	 * @param weightage is the Weight Factor
	 */
	public void addCommunicator(T esCommunicator, int weightage);
	
	/**
	 * 
	 * @param ignoreCommunicators
	 * @return Secondary Communicator from the Group
	 */
	public T getSecondaryCommunicator(T... ignoreCommunicators);
	
	public boolean isAlive();
}
