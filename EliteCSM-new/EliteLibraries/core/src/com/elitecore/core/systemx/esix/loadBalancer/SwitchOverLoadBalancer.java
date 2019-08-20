package com.elitecore.core.systemx.esix.loadBalancer;

import com.elitecore.core.systemx.esix.ESCommunicator;

/**
 * SwitchOverLoadBalance has a list of Alivecommunicators, 
 * Whenever getCommunicator() is called first communicator in list is provided.
 * whenever any communicator is dead, it is removed from the list
 * when any communicator gets live it is appended in the list of Alivecommunicators. 
 * In switchOverLoadBalacer there is no secondary communicator, all are primary communicator
 * 
 * @author harsh
 *
 */
public class SwitchOverLoadBalancer<T extends ESCommunicator> extends RoundRobinLoadBalancer<T> {
	public SwitchOverLoadBalancer() {
		super(true);
	}
	
	public SwitchOverLoadBalancer(boolean checkAlive) {
		super(checkAlive);
	}
	
	@Override
	public T getCommunicator() {
		
		T esCommunicator=null;
		if(communicatorList.size() > 0){
			try{
				esCommunicator = communicatorList.get(0);
			}catch(IndexOutOfBoundsException ex){}
		}
		return esCommunicator;
	}
	
	@Override
	public T getSecondaryCommunicator(T... ignoreCommunicators) {
		return null;
	}
}
