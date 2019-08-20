package com.elitecore.core.systemx.esix.loadBalancer;

import java.util.Collections;
import java.util.LinkedList;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;

public class FailoverLoadBalancer<T extends ESCommunicator> implements LoadBalancer<T> {
	private Object communicatorListLock;
	private LinkedList<OrderedESCommunicator> aliveCommunicatorList;
	private int nextOrderId = 1;
	
	public FailoverLoadBalancer() {
		aliveCommunicatorList = new LinkedList<OrderedESCommunicator>();
		communicatorListLock = new Object();
	}
	
	@Override
	public T getCommunicator() {
		
		T esCommunicator=null;
		int size = aliveCommunicatorList.size();
		for(int i=0;i<size;i++){
			if(aliveCommunicatorList.get(i)!=null && aliveCommunicatorList.get(i).isAlive()){
				esCommunicator = aliveCommunicatorList.get(i).get();
				return esCommunicator;
			}
		}
		return esCommunicator;
	}
	
	@Override
	public T getSecondaryCommunicator(T... ignoreCommunicators) {
		if(ignoreCommunicators == null && aliveCommunicatorList.size()>0){
			return aliveCommunicatorList.get(0).get();
		}
		for (int i=0; i < aliveCommunicatorList.size(); i++) {
			try {
				boolean communicatorFound = false;
				for (int j=0; j < ignoreCommunicators.length; j++) {
					if (ignoreCommunicators[j] == aliveCommunicatorList.get(i).get()) {
						communicatorFound = true;
						break;
					}
				}
				
				if (communicatorFound == false && aliveCommunicatorList.get(i).isAlive()) {
					return aliveCommunicatorList.get(i).get();

				}
				
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		return null;
	}

	private void addCommunicator(T esCommunicator) {
		/**
		 * Wrapping the original communicator as ordered one, which will add the ESCommunicator with the order number.
		 * So, if ESCommunicator is dead than it will be removed from the list, and once if it is alive then
		 * it will be added into list at the assigned order number.
		 */
		final OrderedESCommunicator orderedCommunicator = new OrderedESCommunicator(esCommunicator, nextOrderId++);
		orderedCommunicator.addESIEventListener(new ESIEventListener<ESCommunicator>() {

			@Override
			public void alive(ESCommunicator esCommunicator) {
				addInternal(orderedCommunicator);
			}

			@Override
			public void dead(ESCommunicator esCommunicator) {
				removeInternal(orderedCommunicator);
			}

			/**
			 * adds the communicator back into list and re-orders the list of communicators.
			 * @param communicator non-null communicator ordered communicator that needs to be added back to the list
			 */
			private void addInternal(OrderedESCommunicator communicator) {
				synchronized (communicatorListLock) {
					aliveCommunicatorList.add(communicator);
					Collections.sort(aliveCommunicatorList);
				}
			}

			/**
			 * removes the ordered communicator from the list
			 * @param communicator of type OrderESCommunicator
			 */
			private void removeInternal(OrderedESCommunicator communicator) {
				synchronized (communicatorListLock) {
					aliveCommunicatorList.remove(communicator);
				}
			}
		});
		
		/**
		 * No need to add dead communicator into aliveCommunicatorList
		 * As Listener is already registered so once communicator becomes
		 * alive then it will be added into aliveCommunicatorList.
		 * 
		 * The reason to do this is; if we will add communicator in the 
		 * list even when it is dead, then the status of group will become
		 * alive as it checks for size of list. Thus we do not need to add
		 * communicator to list if it is dead.
		 */
		if (esCommunicator.isAlive() == false) {
			return;
		}
		
		synchronized (communicatorListLock) {
			aliveCommunicatorList.add(orderedCommunicator);
		}
	}
	
	@Override
	public void addCommunicator(T esCommunicator, int weightage) {
		this.addCommunicator(esCommunicator);
	}
	
	@Override
	public boolean isAlive() {
		return aliveCommunicatorList.isEmpty() == false;
	}
	
	
	/**
	 * Wraps the ES Communicator to provide ordering so that when an external system becomes alive, then it must be added in the
	 * same order from which it was removed. 
	 */
	private class OrderedESCommunicator implements ESCommunicator, Comparable<OrderedESCommunicator> {
		private final T externalSystem;
		private final Integer orderNo;
		
		public OrderedESCommunicator(T externalSystem, int orderNo) {
			this.externalSystem = externalSystem;
			this.orderNo = orderNo;
		}
		
		@Override
		public void reInit() throws InitializationFailedException {
			externalSystem.reInit();
		}

		@Override
		public void init() throws InitializationFailedException {
			externalSystem.init();
		}

		@Override
		public boolean isAlive() {
			return externalSystem.isAlive();
		}

		@Override
		public void scan() {
			externalSystem.scan();
		}

		@Override
		public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
			externalSystem.addESIEventListener(eventListener);
		}

		@Override
		public void removeESIEventListener(	ESIEventListener<ESCommunicator> eventListener) {
			externalSystem.removeESIEventListener(eventListener);
		}
		
		@Override
		public void stop() {
			externalSystem.stop();
		}

		@Override
		public String getName() {
			return externalSystem.getName();
		}

		@Override
		public String getTypeName() {
			return externalSystem.getTypeName();
		}

		@Override
		public ESIStatistics getStatistics() {
			return externalSystem.getStatistics();
		}

		@Override
		public void registerAlertListener(AlertListener alertListener) {
			externalSystem.registerAlertListener(alertListener);
		}
		
		/**
		 * Returns the wrapped external system
		 * @return non-null wrapped external system
		 */
		public T get() {
			return externalSystem;
		}

		@Override
		public int compareTo(OrderedESCommunicator that) {
			return this.orderNo.compareTo(that.orderNo);
		}
	}
}
