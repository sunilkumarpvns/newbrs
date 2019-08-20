package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion.assertThat;
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

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class RelayAgentWithFailoverActionTest extends DiameterTestSupport {

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	private RelayAgent relayAgent;
	private DiameterRequest originalRequest;
	private DiameterPeerSpy firstPeer;
	private DiameterPeerSpy originatorPeer;
	private DiameterPeerSpy failoverPeer;
	private DiameterPeerSpy secondaryPeer;
	private DiameterPeerSpy specificPeer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		relayAgent = new RelayAgent(getRouterContext(), mock(IDiameterSessionManager.class));
		originalRequest = createCCInitialRequest();
		originatorPeer = getPeerOperation(ORIGINATOR_PEER_1[1]);
		firstPeer = getPeerOperation(ROUTING_PEER[1]);
		failoverPeer = getPeerOperation(ROUND_ROBIN_PEER[1]);
		secondaryPeer = getPeerOperation(SECONDARY_PEER[1]);
		// TODO This specific peer can be any peer, even outside the group. So update the test case to show that
		specificPeer = secondaryPeer;
	}

	public class FailoverToNextPeerInGroupScenario {
		private RoutingEntry routingEntry;

		@Before
		public void setUp() throws InitializationFailedException {
			createRoutingEntry();
		}

		@Test
		public void shouldRelayRequestToNextPeerInGroupOnTimeoutFromFirstPeer_AndSendAnswerToOriginatorPeer_OnAnswerFromFailoverPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.verifyRequestReceipt()
			.doesNotAnswerWithinRequestTimeout(getSession());

			failoverPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(failoverPeer), getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);
		}

		@Test
		public void givenOnlyFirstPeerIsAlive_WeMustSendUnableToDeliverAnswerToOriginatorPeer_WhenRequestTimesoutFromFirstPeer() throws RoutingFailedException {
			failoverPeer.markDead();
			secondaryPeer.markDead();

			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.doesNotAnswerWithinRequestTimeout(getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}

		@Test
		public void shouldRelayRequestToNextPeerInGroup_WhenRemotePeerAnswersWithErrorCodeConfiguredInFailoverFailureAction_AndSendAnswerToOriginatorPeer_OnAnswerFromFailoverPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.sendsTooBusyAnswer(getSession());

			failoverPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(failoverPeer), getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);

		}

		@Test
		public void givenSecondaryCommunicatorIsDead_WeMustSendUnableToDeliverAnswerToOriginatorPeer_WhenFailoverResultCodeReceivedFromFirstPeer_AndRequestTimesOutFromFailoverPeer() throws RoutingFailedException {
			secondaryPeer.markDead();

			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.sendsTooBusyAnswer(getSession());

			failoverPeer.doesNotAnswerWithinRequestTimeout(getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}

		@Test
		public void shouldRelayRequestToSecondaryPeerInGroup_WhenAllPrimaryRemotePeersAnswerWithErrorCodeConfiguredInFailoverFailureAction_AndSendAnswerToOriginatorPeer_OnSuccessAnswerFromSecondaryPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.verifyRequestReceipt()
			.sendsTooBusyAnswer(getSession());

			failoverPeer.verifyRequestReceipt()
			.sendsTooBusyAnswer(getSession());

			secondaryPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(secondaryPeer), getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);
		}

		private void createRoutingEntry() throws InitializationFailedException {
			RoutingEntryDataImpl data = createBasicRoutingEntryData();

			DiameterFailoverConfigurationImpl failoverConfig = new DiameterFailoverConfigurationImpl();
			failoverConfig.setAction(DiameterFailureConstants.FAILOVER.failureAction);
			failoverConfig.setFailoverArguments("255.255.255.255");

			String errorCodes = Strings.join(",", new Object[] {
					ResultCode.DIAMETER_REQUEST_TIMEOUT.code,
					ResultCode.DIAMETER_TOO_BUSY.code});

			failoverConfig.setErrorCodes(errorCodes);
			data.setFailoverDataList(Arrays.asList(failoverConfig));

			routingEntry = new RoutingEntry(data, getRouterContext(), 
					mock(ITranslationAgent.class));

			routingEntry.init();
		}
	}

	public class FailoverToSecondaryPeerInGroupScenario {
		private RoutingEntry routingEntry;

		@Before
		public void setUp() throws InitializationFailedException {
			createRoutingEntry();
		}

		@Test
		public void shouldRelayRequestToSecondaryPeerInGroupOnTimeoutFromFirstPeer_AndSendAnswerToOriginatorPeer_OnSuccessAnswerFromSecondaryPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.doesNotAnswerWithinRequestTimeout(getSession());

			secondaryPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(secondaryPeer), getSession());

			originatorPeer.verifyAnswerReceipt();

			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);
		}
		
		@Test
		public void shouldRelayRequestToSecondaryPeerInGroup_WhenFirstPeerAnswersWithErrorCodeConfiguredInFailoverFailureAction_AndSendAnswerToOriginatorPeer_OnSuccessAnswerFromSecondaryPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
			
			firstPeer.sendsTooBusyAnswer(getSession());
			
			secondaryPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(secondaryPeer), getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);
		}
		
		@Test
		public void weMustSendUnableToDeliverToOriginatorPeer_WhenFirstPeerAnswersWithErrorCodeConfiguredInFailoverFailureAction_AndRequestToSecondaryPeerTimesOut() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
			
			firstPeer.sendsTooBusyAnswer(getSession());
			
			secondaryPeer.doesNotAnswerWithinRequestTimeout(getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}
		
		@Test
		public void givenSecondaryCommunicatorIsDead_WeShouldRelayRequestToOtherPeerInGroup_WhenFailoverResultCodeReceivedFromFirstPeer() throws RoutingFailedException {
			secondaryPeer.markDead();

			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.sendsTooBusyAnswer(getSession());

			failoverPeer.verifyRequestReceipt();
		}

		private void createRoutingEntry() throws InitializationFailedException {
			RoutingEntryDataImpl data = createBasicRoutingEntryData();

			DiameterFailoverConfigurationImpl failoverConfig = new DiameterFailoverConfigurationImpl();
			failoverConfig.setAction(DiameterFailureConstants.FAILOVER.failureAction);
			failoverConfig.setFailoverArguments("0.0.0.0");

			String errorCodes = Strings.join(",", new Object[] {
					ResultCode.DIAMETER_REQUEST_TIMEOUT.code,
					ResultCode.DIAMETER_TOO_BUSY.code});

			failoverConfig.setErrorCodes(errorCodes);
			data.setFailoverDataList(Arrays.asList(failoverConfig));

			routingEntry = new RoutingEntry(data, getRouterContext(), 
					mock(ITranslationAgent.class));

			routingEntry.init();
		}
	}

	public class FailoverToSpecificPeerScenario {

		private RoutingEntry routingEntry;

		@Before
		public void setUp() throws InitializationFailedException {
			createRoutingEntry();
		}
		

		@Test
		public void shouldRelayRequestToSpecificPeerProvided_WhenRequestToFirstPeerTimesOut_AndSendAnswerToOriginatorPeer_OnAnswerFromSpecificPeer() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
			
			firstPeer.doesNotAnswerWithinRequestTimeout(getSession());
			
			specificPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(specificPeer), getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_SUCCESS);
		}
		
		@Test
		public void mustSendUnableToDeliver_WhenRequestToBothFirstAndSpecificPeerTimesOut() throws RoutingFailedException {
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);
			
			firstPeer.doesNotAnswerWithinRequestTimeout(getSession());
			specificPeer.doesNotAnswerWithinRequestTimeout(getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}
		
		@Test
		public void givenSpecificPeerIsDead_WeMustSendUnableToDeliverToOriginatorPeer_WhenRequestFromFirstPeerTimesOut() throws RoutingFailedException {
			specificPeer.markDead();
			
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.doesNotAnswerWithinRequestTimeout(getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}
		
		@Test
		public void givenSpecificPeerIsDead_WeMustSendUnableToDeliverToOriginatorPeer_WhenFirstPeerAnswersWithFailoverResultCode() throws RoutingFailedException {
			specificPeer.markDead();
			
			relayAgent.routeRequest(originalRequest, getSession(), routingEntry);

			firstPeer.sendsTooBusyAnswer(getSession());
			
			originatorPeer.verifyAnswerReceipt();
			
			assertThat(originatorPeer.getLastSentDiameterPacket())
			.hasHeaderOf(originalRequest)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		}

		private void createRoutingEntry() throws InitializationFailedException {
			RoutingEntryDataImpl data = createBasicRoutingEntryData();

			DiameterFailoverConfigurationImpl failoverConfig = new DiameterFailoverConfigurationImpl();
			failoverConfig.setAction(DiameterFailureConstants.FAILOVER.failureAction);
			failoverConfig.setFailoverArguments(SECONDARY_PEER[0]);

			String errorCodes = Strings.join(",", new Object[] {
					ResultCode.DIAMETER_REQUEST_TIMEOUT.code,
					ResultCode.DIAMETER_TOO_BUSY.code});

			failoverConfig.setErrorCodes(errorCodes);
			data.setFailoverDataList(Arrays.asList(failoverConfig));

			routingEntry = new RoutingEntry(data, getRouterContext(), 
					mock(ITranslationAgent.class));

			routingEntry.init();
		}
	}

	private RoutingEntryDataImpl createBasicRoutingEntryData() {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setTransActionTimeOut(10000000);
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
		return data;
	}

	protected RoutingActions getRoutingAction() {
		return RoutingActions.RELAY;
	}
}
