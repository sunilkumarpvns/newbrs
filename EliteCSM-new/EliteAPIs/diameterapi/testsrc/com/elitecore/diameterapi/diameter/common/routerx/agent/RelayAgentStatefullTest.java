package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

public class RelayAgentStatefullTest extends DiameterTestSupport {

	private static final int INITIAL = 1;
	private static final int UPDATE = 1;
	
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	private RelayAgent relayAgent;
	private DiameterRequest initialRequest;
	private DiameterPeerSpy remotePeer;
	private RoutingEntry routingEntry;
	private DiameterRequest subsequentRequest;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	private DiameterPeerSpy secondaryPeer;
	private DiameterPeerSpy originatorPeer;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRoutingEntry();
		relayAgent = new RelayAgent(getRouterContext(), mock(IDiameterSessionManager.class));
		initialRequest = createCCInitialRequest();
		originatorPeer = getPeerOperation(ORIGINATOR_PEER_1[1]);
		remotePeer = getPeerOperation(ROUTING_PEER[1]);
		secondaryPeer = getPeerOperation(SECONDARY_PEER[1]);
		
		subsequentRequest = createCCUpdateRequest();
	}
	
	private void sendSuccessfulInitialRequest() throws RoutingFailedException {
		relayAgent.routeRequest(initialRequest, getSession(), routingEntry);
		remotePeer.verifyRequestReceipt()
		.sendsAnswer(createSuccessAnswerFrom(remotePeer), getSession());
	}

	@Test
	public void subsequentRequestsForSameSession_MustBeRelayedToStatefulPeerToWhichTheInitialRequestWasRelayed() throws RoutingFailedException {
		sendSuccessfulInitialRequest();
		
		relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		
		remotePeer.verifyRequestReceipts(INITIAL + UPDATE);
	}
	
	@Test
	public void givenStatefulPeerIsDead_SendingSubsequentRequestsForSameSession_ShouldSendUnableToDeliverToOriginatorPeer() throws RoutingFailedException {
		sendSuccessfulInitialRequest();
		
		remotePeer.markDead();
		
		relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		
		originatorPeer.verifyAnswerReceipts(INITIAL + UPDATE);
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
		.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER)
		.hasHeaderOf(subsequentRequest);
	}

	@Test
	public void shouldRelaySubsequentRequestsToThePeer_ThatSendsSuccessfulAnswer_AfterFailoverOnTimeOut() throws RoutingFailedException {

		relayAgent.routeRequest(initialRequest, getSession(), routingEntry);
		
		remotePeer.doesNotAnswerWithinRequestTimeout(getSession());
		
		secondaryPeer.sendsAnswer(createSuccessAnswerFrom(secondaryPeer), getSession());
		
		relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		
		secondaryPeer.verifyRequestReceipts(INITIAL + UPDATE);
	}
	
	@Test
	public void shouldRelaySubsequentRequestsToThePeer_ThatSendsSuccessfulAnswer_AfterFailoverOnErrorCode() throws RoutingFailedException {
		
		relayAgent.routeRequest(initialRequest, getSession(), routingEntry);
		
		remotePeer.sendsTooBusyAnswer(getSession());
		
		secondaryPeer.sendsAnswer(createSuccessAnswerFrom(secondaryPeer), getSession());
		
		relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		
		secondaryPeer.verifyRequestReceipts(INITIAL + UPDATE);
	}
	
	@Test
	public void shouldRelayBasedOnStatefulDestinationHostFromSession_EvenIfRequestContainsDestinationHostAVP() throws RoutingFailedException {
		sendSuccessfulInitialRequest();
		
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, subsequentRequest, SECONDARY_PEER[1]);
		
		relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		
		remotePeer.verifyRequestReceipts(2);
		secondaryPeer.verifyRequestNotReceived();
	}
	
	@Test
	public void shouldThrowRoutingFailedException_IfStatefulDestinationHostFromSessionIsDead_AndNotConsiderDestinationHostAVPInRequest() throws RoutingFailedException {
		sendSuccessfulInitialRequest();
		
		remotePeer.markDead();
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, subsequentRequest, SECONDARY_PEER[1]);
		
		try {
			relayAgent.routeRequest(subsequentRequest, getSession(), routingEntry);
		} catch (RoutingFailedException ex) {
			assertEquals(ResultCode.DIAMETER_PEER_NOT_FOUND, ex.getResultCode());
		}
	}
	
	private void createRoutingEntry() throws InitializationFailedException {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setRoutingAction(getRoutingAction().routingAction);

		data.setStatefulRouting(true);
		
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
		failoverConfig.setAction(DiameterFailureConstants.FAILOVER.failureAction);

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
