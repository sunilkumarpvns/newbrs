package com.elitecore.netvertex.gateway.diameter.application;

import org.junit.Assert;
import org.mockito.Mockito;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public class DummyRequestProprocessor implements RequestPreprocessor {

	private boolean processCalled = false;
	
	@Override
	public void process(DiameterRequest request, DiameterGatewayConfiguration gatewayConf) {
		processCalled = true;
	}
	
	public void checkForProcessCall() {
		Assert.assertTrue(processCalled);
	}

}
