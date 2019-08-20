package com.elitecore.core.systemx.esix;

public interface ESCommunicatorGroup<T extends ESCommunicator> {

	int SECONDARY_COMM_WEIGHTAGE = 0;
	
	/**
	 * Adds Communicator into the group with given weightage. 
	 * if weightage is 0 then that system will be considered as
	 * secondary system
	 * @param esCommunicator
	 * @param weightage
	 */
	void addCommunicator(T esCommunicator, int weightage);
	
	/**
	 * Adds Communicator into the group with default weightage 1
	 * @param esCommunicator
	 */
	void addCommunicator(T esCommunicator);
	
	/**
	 * Returns whether any communicator in the group is alive or not
	 * @return true - If any one of the communicator in the group is alive, false otherwise
	 */
	boolean isAlive();
}
