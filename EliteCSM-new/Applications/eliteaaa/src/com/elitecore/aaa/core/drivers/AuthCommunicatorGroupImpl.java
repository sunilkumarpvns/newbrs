package com.elitecore.aaa.core.drivers;

import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.core.systemx.esix.LoadBalancerType;

public class AuthCommunicatorGroupImpl extends ESCommunicatorGroupImpl<IEliteAuthDriver> implements AuthCommunicatorGroup {

	public AuthCommunicatorGroupImpl(LoadBalancerType loadBalancerType) {
		super(loadBalancerType);
	}

	public IEliteAuthDriver getCommunicator(){
		return super.getCommunicator();
	}

	public IEliteAuthDriver getSecondaryCommunicator(IEliteAuthDriver... communicator){
		return super.getSecondaryCommunicator(communicator);
	}


}
