package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.systemx.esix.ESCommunicatorGroup;

public interface CDRCommunicatorGroup extends ESCommunicatorGroup<CDRDriver<ValueProviderExtImpl>> {

	public void handleRequest(ValueProviderExtImpl valueProvider);
	
}
