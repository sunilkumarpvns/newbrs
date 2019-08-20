package com.elitecore.core.systemx.esix.loadBalancer;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.commons.util.ConcurrentCounter;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;

public class RoundRobinLoadBalancer<T extends ESCommunicator> implements LoadBalancer<T>{
	
	protected List<T> communicatorList;
	private Object communicatorListLock;
	private ConcurrentCounter counter;
	private ESIEventListener<T> statusListner;
	private boolean checkAlive;
	
	public RoundRobinLoadBalancer(boolean checkAlive) {
		communicatorList = new ArrayList<T>();
		communicatorListLock = new Object();
		this.checkAlive = checkAlive;
		counter = new ConcurrentCounter(0,-1);
		statusListner = new ESIEventListener<T>(){
			@Override
			public void alive(T esCommunicator) {
				addCommunicator(esCommunicator,DEFAULT_WEIGHT, false);
			}

			@Override
			public void dead(T esCommunicator) {
				removeCommunicator(esCommunicator);
			}
		};
	}
	
	@Override
	public T getCommunicator() {
		try{
			return communicatorList.get((int)counter.incrementCounter());
		}catch(IndexOutOfBoundsException e){
		}
		return null;
	}
	
	@Override
	public T getSecondaryCommunicator(T... ignoreCommunicators) {
		if(ignoreCommunicators == null && communicatorList.size()>0){
			return communicatorList.get(0);
		}
		
		for(int i=0; i < communicatorList.size(); i++){
			try{
				boolean communicatorFound = false;
				for(int j=0; j < ignoreCommunicators.length; j++){
					if(ignoreCommunicators[j] == communicatorList.get(i)){
						communicatorFound = true;
						break;
					}
				}					
				
				if(communicatorFound == false){
					if(checkAlive == false || communicatorList.get(i).isAlive()){
						return communicatorList.get(i);
					}
				}
						
					
			}catch(ArrayIndexOutOfBoundsException e){
				return null;
			}catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		return null;
	}
	
	@Override
	public void addCommunicator(T esCommunicator, int weight){
		addCommunicator(esCommunicator, weight, true);
	}
	
	private void addCommunicator(T esCommunicator, int weight, boolean addListener){
		synchronized (communicatorListLock) {
			for (int i = 0; i < weight; i++) {
				if ((checkAlive == false) || (esCommunicator.isAlive())) {
					communicatorList.add(esCommunicator);
					counter.incrementMaxVal();
				}
				
				if (checkAlive && addListener) {
					esCommunicator.addESIEventListener((ESIEventListener<ESCommunicator>) statusListner);
				}
			}
		}
	}
	
	private void removeCommunicator(T esCommunicator){
		synchronized (communicatorListLock) {
			if (communicatorList.remove(esCommunicator))
				counter.decrementMaxVal();
		}
	}
	
	@Override
	public boolean isAlive() {
		return communicatorList.size() > 0;
	}
}
