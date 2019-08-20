package com.elitecore.core.systemx.esix;
import com.elitecore.core.systemx.esix.loadBalancer.FailoverLoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.RoundRobinLoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.SwitchOverLoadBalancer;


public abstract class ESCommunicatorGroupImpl<T extends ESCommunicator> implements ESCommunicatorGroup<T>{
	
	private static final boolean ALIVE = true;
	
	private LoadBalancer<T> primaryGroup;
	private T secondaryCommunicator;
	//Aliveness Property --> true if Communicators in Group are required Alive. 
	private boolean aliveness;
	
	/*
	 * This property maintains the static group size and does not reflect actual runtime
	 * group size based on aliveness of communicators in primary group.
	 */
	private int groupSize = 0;
	
	public ESCommunicatorGroupImpl() {
		this(LoadBalancerType.ROUND_ROBIN);
	}
	
	public ESCommunicatorGroupImpl(LoadBalancerType loadBalancerType) {
		this(loadBalancerType, ALIVE);
	}
	
	public ESCommunicatorGroupImpl(LoadBalancerType loadBalancerType ,
			boolean aliveness) {
		this.aliveness = aliveness;
		if(loadBalancerType == LoadBalancerType.FAIL_OVER){
			primaryGroup = new FailoverLoadBalancer<T>();
		}else if(loadBalancerType == LoadBalancerType.SWITCH_OVER){
			primaryGroup = new SwitchOverLoadBalancer<T>();
		}else{
			primaryGroup = new RoundRobinLoadBalancer<T>(aliveness);
		}
	}
	
	protected T getCommunicator(){
		T communicator = primaryGroup.getCommunicator();
		if(communicator == null && isSecondaryCommAlive())
			communicator = secondaryCommunicator;
		return communicator;
	}
	
	protected T getSecondaryCommunicator(T... ignoreCommunicators){
		
		if(secondaryCommunicator == null || isSecondaryCommAlive() == false){
			return primaryGroup.getSecondaryCommunicator(ignoreCommunicators);
		}

		if (ignoreCommunicators == null)
			return secondaryCommunicator;
		
		boolean communictorFound = false;
		for(int j=0; j < ignoreCommunicators.length; j++){
			if(ignoreCommunicators[j] == secondaryCommunicator){
				communictorFound = true;
				break;
			}
		}

		if(communictorFound == false)
			return secondaryCommunicator;
		
		return null;

	}
	
	@Override
	public void addCommunicator(T esCommunicator, int weightage){
		if(esCommunicator != null){
			if(weightage == SECONDARY_COMM_WEIGHTAGE)
				secondaryCommunicator = esCommunicator;
			else if (weightage > 0){
				primaryGroup.addCommunicator(esCommunicator, weightage);
			}
			groupSize++;
		}		
	}
	
	@Override
	public void addCommunicator(T esCommunicator) {
		addCommunicator(esCommunicator, LoadBalancer.DEFAULT_WEIGHT);
	}
	
	/**
	 * 
	 * @return false if Secondary Communicator is needed Alive
	 * 			and it is Dead
	 */
	private boolean isSecondaryCommAlive(){
		return (aliveness==false || (secondaryCommunicator != null && secondaryCommunicator.isAlive()));
	}
	
	@Override
	public boolean isAlive() {
		return (primaryGroup.isAlive() || isSecondaryCommAlive());
	}

	/**
	 * @return <b>static</b> size of group based on configuration of group and <b>not runtime</b> size.
	 * NOTE this method does not address the aliveness of communicators in group.
	 */
	protected int getGroupSize() {
		return groupSize;
	}
}
