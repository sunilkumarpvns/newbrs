package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RelayAgentWithDropActionTest extends DiameterTestSupport {

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	private RoutingEntry routingEntry;
	private RelayAgent relayAgent;
	private DiameterRequest originalRequest;
	private DiameterPeerSpy firstPeer;
	private DiameterPeerSpy originatorPeer;
	private DiameterPeerSpy failoverPeer;
	private DiameterPeerSpy secondaryPeer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRoutingEntry();
		relayAgent = new RelayAgent(getRouterContext(), mock(IDiameterSessionManager.class));
		originalRequest = createCCInitialRequest();
		originatorPeer = getPeerOperation(ORIGINATOR_PEER_1[1]);
		firstPeer = getPeerOperation(ROUTING_PEER[1]);
		failoverPeer = getPeerOperation(ROUND_ROBIN_PEER[1]);
		secondaryPeer = getPeerOperation(SECONDARY_PEER[1]);
	}

	@Test
	public void originatorPeerShouldNotReceiveAnswer_WhenFailureResultCodeIsReceivedFromRemotePeer_AndRequestWillNotBeSentToAnyOtherPeerInGroup() throws RoutingFailedException {
		relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
		
		firstPeer.sendsTooBusyAnswer(getSession());
		
		failoverPeer.verifyRequestNotReceived();
		secondaryPeer.verifyRequestNotReceived();
		
		originatorPeer.verifyAnswerNotReceived();
	}
	
	@Test
	public void originatorPeerShouldNotReceiveAnswer_WhenRequestGetsTimeoutFromRemotePeer_AndRequestWillNotBeSentToAnyOtherPeerInGroup() throws RoutingFailedException {
		relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
		
		firstPeer.doesNotAnswerWithinRequestTimeout(getSession());
		
		failoverPeer.verifyRequestNotReceived();
		secondaryPeer.verifyRequestNotReceived();
		
		originatorPeer.verifyAnswerNotReceived();
	}

	private void createRoutingEntry() throws InitializationFailedException {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setRoutingAction(getRoutingAction().routingAction);
		data.setDestRealm(ROUTING_PEER[2]);
		data.setRoutingName("Relay");
		PeerGroupImpl group = new PeerGroupImpl();
		PeerInfoImpl info = new PeerInfoImpl();
		info.setPeerName(ROUTING_PEER[0]);
		info.setLoadFactor(1);
		group.getPeerList().add(info);
		info = new PeerInfoImpl();
		info.setPeerName(ROUND_ROBIN_PEER[0]);
		info.setLoadFactor(1);
		group.getPeerList().add(info);
		info = new PeerInfoImpl();
		info.setPeerName(SECONDARY_PEER[0]);
		info.setLoadFactor(0);
		group.getPeerList().add(info);
		data.setPeerGroupList(Arrays.asList(group));

		DiameterFailoverConfigurationImpl failoverConfig = new DiameterFailoverConfigurationImpl();
		failoverConfig.setAction(DiameterFailureConstants.DROP.failureAction);

		String errorCodes = Strings.join(",", new Object[] {
				ResultCode.DIAMETER_REQUEST_TIMEOUT.code,
				ResultCode.DIAMETER_TOO_BUSY.code});

		failoverConfig.setErrorCodes(errorCodes);
		data.setFailoverDataList(Arrays.asList(failoverConfig));

		routingEntry = new RoutingEntry(data, getRouterContext(), 
				mock(ITranslationAgent.class));

		routingEntry.init();
	}

	protected RoutingActions getRoutingAction() {
		return RoutingActions.RELAY;
	}
}
