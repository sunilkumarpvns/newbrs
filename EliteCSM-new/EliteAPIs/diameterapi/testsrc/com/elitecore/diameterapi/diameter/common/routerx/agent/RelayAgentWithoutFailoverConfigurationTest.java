package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class RelayAgentWithoutFailoverConfigurationTest extends DiameterTestSupport {
	private RelayAgent relayAgent;
	private DiameterRequest originalRequest;
	private DiameterPeerSpy remotePeer;
	private DiameterPeerSpy originatorPeer;
	private DiameterPeerSpy failoverPeer;
	private DiameterPeerSpy secondaryPeer;
	private RoutingEntry routingEntry;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRoutingEntry();
		relayAgent = new RelayAgent(getRouterContext(), mock(IDiameterSessionManager.class));
		originalRequest = createCCInitialRequest();
		originatorPeer = getPeerOperation(ORIGINATOR_PEER_1[1]);
		remotePeer = getPeerOperation(ROUTING_PEER[1]);
		failoverPeer = getPeerOperation(ROUND_ROBIN_PEER[1]);
		secondaryPeer = getPeerOperation(SECONDARY_PEER[1]);
	}

	@Test
	public void requestShouldBeRelayedToDestinationPeerAndRouteRecordAVPMustBeAddedInRemoteRequest() throws RoutingFailedException {
		relayAgent.routeRequest(originalRequest, getSession(), getRoutingEntry());
		
		remotePeer.verifyRequestReceipt();

		DiameterAssertion.assertThat(remotePeer.getLastSentDiameterPacket())
			.containsAVP(DiameterAVPConstants.ROUTE_RECORD, ORIGINATOR_PEER_1[1]);
	}
	
	@Test
	public void requestShouldBeRelayedToDestinationPeerAndAVPsFromRemoteAnswerShouldBeAddedInAnswerOfOriginatorPeer() throws RoutingFailedException {
		relayAgent.routeRequest(originalRequest, getSession(), getRoutingEntry());
		
		remotePeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(remotePeer), getSession());
			
		originatorPeer.verifyAnswerReceipt();
		
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS)
			.containsAVP(DiameterAVPConstants.CLASS, "hello");
	}
	
	@Test
	public void requestShouldBeRelayedToDestinationPeerAndWeMustSendUnableToDeliverIfRequestToRemotePeerTimesOut() throws RoutingFailedException {
		relayAgent.routeRequest(originalRequest, getSession(), getRoutingEntry());
		
		remotePeer.verifyRequestReceipt()
			.doesNotAnswerWithinRequestTimeout(getSession());
		
		originatorPeer.verifyAnswerReceipt();
	
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
	}
	
	@Test
	public void givenNoPeerInGroupIsAlive_TryingToSendRequestShouldSendAnswerToOriginatorWithUnableToDeliverResultCode() throws RoutingFailedException {
		remotePeer.markDead();
		failoverPeer.markDead();
		secondaryPeer.markDead();
		
		relayAgent.routeRequest(originalRequest, getSession(), getRoutingEntry());
		
		originatorPeer.verifyAnswerReceipt();
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
		.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER)
		.hasHeaderOf(originalRequest);
	}
	
	private RoutingEntry createRoutingEntry() throws InitializationFailedException {
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
		
		routingEntry = new RoutingEntry(data, getRouterContext(), 
				mock(ITranslationAgent.class));
		routingEntry.init();
		return routingEntry;
	}

	protected RoutingActions getRoutingAction() {
		return RoutingActions.RELAY;
	}

	public RoutingEntry getRoutingEntry() {
		return routingEntry;
	}
}
