package com.elitecore.aaa.core.drivers;

import com.elitecore.core.systemx.esix.ESCommunicatorGroup;

public interface AuthCommunicatorGroup extends ESCommunicatorGroup<IEliteAuthDriver> {

	IEliteAuthDriver getCommunicator();
	IEliteAuthDriver getSecondaryCommunicator(IEliteAuthDriver... communicator);
	
}
