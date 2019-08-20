package com.elitecore.netvertex.gateway.diameter.application;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.Arrays;

import org.junit.Test;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;

public class PostReceivedScriptsPreProcessorTest {

	@Test
	public void test_process_postReceivedShouldCall_When_ScriptsConfiguredForProvidedGateway() {
		DummyDiameterGatewayControllerContext context = spy(new DummyDiameterGatewayControllerContext());
		DiameterRequest request = new DiameterRequest();
		DiameterGatewayConfigurationImpl gatewayConf = mock(DiameterGatewayConfigurationImpl.class);
		PostReceivedScriptsPreProcessor processor = new PostReceivedScriptsPreProcessor(context);
		
		DummyDiameterGroovyScript dummyDiameterGroovyScript = new DummyDiameterGroovyScript(context, gatewayConf);
		doReturn(Arrays.asList(dummyDiameterGroovyScript)).when(context).getDiameterGroovyScripts(anyString());
		processor.process(request, gatewayConf);
		
		dummyDiameterGroovyScript.checkForPostReceivedCall();
	}
	
	//TODO only postReceivedShould Call , Not other groovymethod
	
	private static class DummyDiameterGroovyScript extends DiameterGroovyScript {

		boolean postReceivedCalled = false;
		
		public DummyDiameterGroovyScript(DiameterGatewayControllerContext diameterGatewayControllerContext,
				DiameterGatewayConfiguration configuration) {
			super(diameterGatewayControllerContext, configuration);
		}
		

		@Override
		public String getName() {
			return "name";
		}
		
		@Override
		public void postReceived(DiameterPacket diameterPacket) {
			this.postReceivedCalled = true;
		}
		
		public void checkForPostReceivedCall() {
			assertTrue(postReceivedCalled);
		}
	} 
}
