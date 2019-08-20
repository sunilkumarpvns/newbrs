package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

public class RelayAgentBasicTest extends DiameterTestSupport {

	private static final String UNKNOWN_PEER = "unknown.example.net";
	private RoutingEntry routingEntry;
	private RelayAgent relayAgent;
	private DiameterRequest request;
	private DiameterPeerSpy remotePeer;
	private DiameterPeerSpy secondaryPeer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRoutingEntry();
		relayAgent = new RelayAgent(getRouterContext(), mock(IDiameterSessionManager.class));
		request = createCCInitialRequest();
		remotePeer = getPeerOperation(ROUTING_PEER[1]);
		secondaryPeer = getPeerOperation(SECONDARY_PEER[1]);
	}

	@Test
	public void givenRequestContainsDestinationHostAVP_WeMustRelayRequestToThatDestination_IfItIsKnownAndAlive() throws RoutingFailedException {
		request.addAvp(DiameterAVPConstants.DESTINATION_HOST, SECONDARY_PEER[1]);

		relayAgent.routeRequest(request, getSession(), routingEntry);

		secondaryPeer.verifyRequestReceipt();
		remotePeer.verifyRequestNotReceived();
	}

	@Test
	public void givenRequestContainsDestinationHostAVP_WeShouldChoosePeerFromGroup_IfDestinationPeerIsUnknown() throws RoutingFailedException {
		request.addAvp(DiameterAVPConstants.DESTINATION_HOST, UNKNOWN_PEER);

		relayAgent.routeRequest(request, getSession(), routingEntry);

		remotePeer.verifyRequestReceipt();
	}

	@Test
	public void givenRequestContainsDestinationHostAVP_WeMustThrowRoutingFailedException_IfDestinationPeerIsDead() throws RoutingFailedException {
		request.addAvp(DiameterAVPConstants.DESTINATION_HOST, SECONDARY_PEER[1]);

		secondaryPeer.markDead();
		try {
			relayAgent.routeRequest(request, getSession(), routingEntry);
		} catch (RoutingFailedException ex) {
			assertEquals(ResultCode.DIAMETER_PEER_NOT_FOUND, ex.getResultCode());
		}
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

		routingEntry = new RoutingEntry(data, getRouterContext(), 
				mock(ITranslationAgent.class));
		routingEntry.init();

	}

	protected RoutingActions getRoutingAction() {
		return RoutingActions.RELAY;
	}
}
