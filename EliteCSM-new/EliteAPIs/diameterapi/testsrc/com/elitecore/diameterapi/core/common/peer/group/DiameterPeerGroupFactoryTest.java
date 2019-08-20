package com.elitecore.diameterapi.core.common.peer.group;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.peers.DummyPeerProvider;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class DiameterPeerGroupFactoryTest {

	private DummyStackContext stackContext;
	private DummyPeerProvider peerProvider;
	private DiameterPeerGroupFactory factory;

	@Before
	public void setUp() {
		peerProvider = new DummyPeerProvider();
		stackContext = new DummyStackContext(peerProvider);
		PeerData peerData = new PeerDataProvider().withPeerName("test")
				.withHostIdentity("test.example.com")
				.withRealmName("example.com")
				.build();
		stackContext.registerPeerSpy(peerData);
		
		factory = new DiameterPeerGroupFactory(stackContext);
	}
	
	public class GroupInstanceCaching {
		
		@Test
		public void createsGroupOnceForEachGroupBasedOnGroupName() {
			Map<String, Integer> peers = new HashMap<String, Integer>();
			peers.put("test", 1);
			
			DiameterPeerGroupParameter groupParameter = new DiameterPeerGroupParameter("testGroup",
					peers, LoadBalancerType.ROUND_ROBIN, false, 1, 3000);
			DiameterPeerGroupParameter groupParameterOther = new DiameterPeerGroupParameter("testGroup",
					peers, LoadBalancerType.ROUND_ROBIN, false, 1, 3000);
			
			assertThat(factory.getInstance(groupParameter), 
					is(sameInstance(factory.getInstance(groupParameterOther))));
		}
	}
}
