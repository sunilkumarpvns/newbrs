package com.elitecore.diameterapi.core.common.session;

import static com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy.allOf;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.commons.tests.Troubleshooter;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.api.AnswerMemorizingResponseListener;
import com.elitecore.diameterapi.core.common.peer.group.DefaultDiameterPeerGroup;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerCommGroup;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
// TODO add cases that no result code is considered as successful answer i.e. the answer will be eligible for session statefulness
@RunWith(HierarchicalContextRunner.class)
public class DefaultDiameterPeerGroupStatefullTest extends DiameterTestSupport {

	private static final String OTHER_SESSION_ID_VALUE = "SESSION2";
	private static final int TRANSACTION_TIMEOUT = 10;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	@Rule public Troubleshooter troubleshooter = Troubleshooter.disabled();
	
	private DiameterPeerGroupFactory diameterPeerGroupFactory;
	
	private DiameterSession session;
	private DiameterSession otherSession;
	private FixedTimeSource timesource;
	private DiameterRequest initialRequest;
	private PeerData originatorPeerData;
	private AnswerMemorizingResponseListener userListener;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		session = addSession(SESSION_ID_VALUE);
		otherSession = addSession(OTHER_SESSION_ID_VALUE);
		
		originatorPeerData = getPeerOperation(ORIGINATOR_PEER_1[1]).getDiameterPeer().getPeerData();
		initialRequest = createCCInitialRequestFrom(originatorPeerData);
		timesource = new FixedTimeSource(initialRequest.creationTimeMillis());

		diameterPeerGroupFactory = new DiameterPeerGroupFactory(getStackContext()) {
			@Override
			protected DefaultDiameterPeerGroup create(DiameterPeerGroupParameter diameterPeerGroupParameter) {
				return new DefaultDiameterPeerGroup(getStackContext(), diameterPeerGroupParameter.getName(), 
						diameterPeerGroupParameter.getMaxRetryCount(), 
						diameterPeerGroupParameter.getTransactionTimeout(), timesource, true);
			}
		};

		userListener = spy(new AnswerMemorizingResponseListener());
	}
	
	public class LoadBalancingWithoutHighAvailability {
		
		private DiameterPeerSpy peerOperation1;
		private DiameterPeerSpy peerOperation2;
		private DiameterPeerSpy peerOperation3;
		private DiameterPeerCommGroup group;
		
		@Before
		public void setUp() {
			PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
					.withHostIdentity("peer1.test.com")
					.withRealmName("test.com").build();
			PeerData peerData2 = new PeerDataProvider().withPeerName("testPeer2")
					.withHostIdentity("peer2.test.com")
					.withRealmName("test.com").build();
			PeerData peerData3 = new PeerDataProvider().withPeerName("testPeer3")
					.withHostIdentity("peer3.test.com")
					.withRealmName("test.com").build();
			
			addPeer(peerData1)
			.addPeer(peerData2)
			.addPeer(peerData3);
	
			peerOperation1 = getPeerOperation(peerData1.getHostIdentity());
			peerOperation2 = getPeerOperation(peerData2.getHostIdentity());
			peerOperation3 = getPeerOperation(peerData3.getHostIdentity());
			
			DiameterPeerGroupParameter groupData = createGroup("peer group1", 3, TRANSACTION_TIMEOUT);
			group = diameterPeerGroupFactory.getInstance(groupData);
		}
		
		private DiameterPeerGroupParameter createGroup(String name, int maxRetry, int transactionTimeout) {
			Map<String, Integer> peers = new LinkedHashMap<String, Integer>();
			peers.put(peerOperation1.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation2.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation3.getDiameterPeer().getHostIdentity(), 1);
			return new DiameterPeerGroupParameter(name, peers, LoadBalancerType.ROUND_ROBIN, true, maxRetry, transactionTimeout);
		}

		public class ForwardingInitialRequestForASession {
			
			@Test
			public void requestIsForwardedToPeerAliveBasedOnLoadBalancingConfiguration() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.verifyRequestReceipt();
				
				verifyAnswerReceived(peerOperation1);
			}
			
			@Test
			public void anyPeerThatIsDeadIsRemovedFromLoadBalancingAndRequestIsForwardedToAlivePeers() throws CommunicationException {
				peerOperation1.markDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				assertNoRequestSentTo(peerOperation1);
				peerOperation2.verifyRequestReceipt();
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void whenAPeerComesBackAliveAgainItIsAddedBackIntoLoadBalancer() throws CommunicationException {
				peerOperation1.markDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.markAlive();
				
				group.sendClientInitiatedRequest(addSession("ANY"), createCCInitialRequest("ANY"), ResponseListener.NO_RESPONSE_LISTENER);
				group.sendClientInitiatedRequest(addSession("ANY"), createCCInitialRequest("ANY"), ResponseListener.NO_RESPONSE_LISTENER);
				
				peerOperation1.verifyRequestReceipt();
			}
			
			@Test
			public void onTooBusyAnswerFromPeerTheRequestIsForwardedToOtherPeerBasedOnLoadBalancingConfiguration() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				peerOperation2.verifyRequestReceipt();
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void onRequestTimeoutAnswerFromPeerTheRequestIsForwardedToOtherPeerBasedOnLoadBalancingConfiguration() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation2.verifyRequestReceipt();
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void onTooBusyAnswerFromPeersWillKeepTryingToSendRequestToOtherPeersIfTransactionTimeoutIsNotReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation2, peerOperation3).sendTooBusyAnswer(session);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
			}
			
			@Test
			public void onRequestTimeoutAnswerFromPeersWillKeepTryingToSendRequestToOtherPeersIfTransactionTimeoutIsNotReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation2, peerOperation3).doNotAnswerWithinRequestTimeout(session);
				
				verify(userListener, times(1)).requestTimedout(peerOperation3.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void onTooBusyAnswerFromPeersWillKeepTryingToSendRequestToPeersAndStopAfterTransactionTimeoutIsReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				transactionTimeoutReached();

				peerOperation2.sendsTooBusyAnswer(session);
				
				assertNoRequestSentTo(peerOperation3);
			}
			
			@Test
			public void onRequestTimeoutAnswerFromPeersWillKeepTryingToSendRequestToPeersAndStopAfterTransactionTimeoutIsReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.doesNotAnswerWithinRequestTimeout(session);
				
				transactionTimeoutReached();

				peerOperation2.doesNotAnswerWithinRequestTimeout(session);
				
				assertNoRequestSentTo(peerOperation3);
			}
			
			@Test
			public void failsWhenAllPeersInTheGroupAreDead() throws CommunicationException {
				peerOperation1.markDead();
				peerOperation2.markDead();
				peerOperation3.markDead();
				
				expectedException.expect(CommunicationException.class);
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
			}
		}
		
		public class ForwardingSubsequentRequestsForASession {

			private DiameterRequest subsequentRequest;

			@Before
			public void setUp() throws CommunicationException {
				initialRequestServed();
				subsequentRequest = createCCUpdateRequestFrom(originatorPeerData);
			}

			private void initialRequestServed() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, ResponseListener.NO_RESPONSE_LISTENER);
				
				peerOperation1.verifyRequestReceipt().sendsAnswer(createSuccessAnswerFrom(peerOperation1), session);
			}
			
			public class SessionFailoverWithinGroupIsDisabled {

				@Test
				public void givenStatefullPeerIsAliveRequestsAreForwardedToStatefullPeer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequest);
					
					verifyAnswerReceived(peerOperation1);
					assertNoRequestSentTo(peerOperation2, peerOperation3);
				}
				
				@Test
				public void failsIfStatefullPeerIsDead() throws CommunicationException {
					peerOperation1.markDead();

					expectedException.expect(CommunicationException.class);
					
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
				}
				
				@Test
				public void doesNotRecoverOnRequestTimeoutAnswerFromStatefulPeer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1.doesNotAnswerWithinRequestTimeout(session);
					
					verify(userListener, times(1)).requestTimedout(peerOperation1.getDiameterPeer().getHostIdentity(), session);
				}
				
				@Test
				@Ignore
				public void notifiesUserWithUnableToDeliverOnTooBusyAnswerFromStatefulPeer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1.sendsTooBusyAnswer(session);
					
					DiameterAssertion.assertThat(userListener.diameterAnswer).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
				}
			}
		}
	}
	
	public class LoadBalancingWithHighAvailability {
		private DiameterPeerSpy peerOperation1;
		private DiameterPeerSpy peerOperation2;
		private DiameterPeerSpy peerOperation3;
		private DiameterPeerSpy peerOperation1Secondary;
		private DiameterPeerSpy peerOperation2Secondary;
		private DiameterPeerSpy peerOperation4;
		private DiameterPeerSpy peerOperation3And4Secondary;
		private DiameterPeerSpy peerOperation5;
		
		private DiameterPeerCommGroup group;
		
		@Before
		public void setUp() {
			PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
					.withHostIdentity("peer1.test.com")
					.withSecondaryPeer("testSecondaryPeer1")
					.withRealmName("test.com").build();
			PeerData peer1SecondaryData = new PeerDataProvider().withPeerName("testSecondaryPeer1")
					.withHostIdentity("peer1secondary.test.com")
					.withRealmName("test.com").build();
			PeerData peerData2 = new PeerDataProvider().withPeerName("testPeer2")
					.withHostIdentity("peer2.test.com")
					.withSecondaryPeer("testSecondaryPeer2")
					.withRealmName("test.com").build();
			PeerData peerData2Secondary = new PeerDataProvider().withPeerName("testSecondaryPeer2")
					.withHostIdentity("peer2secondary.test.com")
					.withRealmName("test.com").build();
			PeerData peerData3 = new PeerDataProvider().withPeerName("testPeer3")
					.withHostIdentity("peer3.test.com")
					.withSecondaryPeer("testSecondaryPeer3And4")
					.withRealmName("test.com").build();
			PeerData peerData4 = new PeerDataProvider().withPeerName("testPeer4")
					.withHostIdentity("peer4.test.com")
					.withSecondaryPeer("testSecondaryPeer3And4")
					.withRealmName("test.com").build();
			PeerData peerData3And4Secondary = new PeerDataProvider().withPeerName("testSecondaryPeer3And4")
					.withHostIdentity("peer34secondary.test.com")
					.withRealmName("test.com").build();
			PeerData peerData5 = new PeerDataProvider().withPeerName("testPeer5")
					.withHostIdentity("peer5.test.com")
					.withSecondaryPeer("testPeer1")
					.withRealmName("test.com").build();

			addPeer(peerData1)
			.addPeer(peer1SecondaryData)
			.addPeer(peerData2)
			.addPeer(peerData2Secondary)
			.addPeer(peerData3)
			.addPeer(peerData4)
			.addPeer(peerData3And4Secondary)
			.addPeer(peerData5);
			
			
			peerOperation1 = getPeerOperation(peerData1.getHostIdentity());
			peerOperation1Secondary = getPeerOperation(peer1SecondaryData.getHostIdentity());
			peerOperation2 = getPeerOperation(peerData2.getHostIdentity());
			peerOperation2Secondary = getPeerOperation(peerData2Secondary.getHostIdentity());
			peerOperation3 = getPeerOperation(peerData3.getHostIdentity());
			peerOperation4 = getPeerOperation(peerData4.getHostIdentity());
			peerOperation3And4Secondary = getPeerOperation(peerData3And4Secondary.getHostIdentity());
			peerOperation5 = getPeerOperation(peerData5.getHostIdentity());
			
			DiameterPeerGroupParameter groupData = createGroup("peer group1", 3, TRANSACTION_TIMEOUT);
			group = diameterPeerGroupFactory.getInstance(groupData);
		}
		
		public class ForwardingInitialRequestForASession {
			
			@Test
			public void requestIsForwardedToPrimaryPeerAliveBasedOnLoadBalancingConfiguration() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.verifyRequestReceipt();
				
				verifyAnswerReceived(peerOperation1);
			}
			
			@Test
			public void givenPrimaryPeerIsDeadWhileLoadBalancingWillAlsoConsiderAlivenessOfSecondaryPeer() throws CommunicationException {
				peerOperation1.markDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1Secondary.verifyRequestReceipt();
				assertNoRequestSentTo(peerOperation2, peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation1Secondary);
			}
			
			@Test
			public void highAvailabilityGroupThatIsDeadShouldNotBeSelectedWhileLoadBalancing() throws CommunicationException {
				allOf(peerOperation1, peerOperation1Secondary).areDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation2.verifyRequestReceipt();
				assertNoRequestSentTo(peerOperation1, peerOperation1Secondary, peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void whenAnyPeerFromTheHighAvailabilityGroupBecomesAliveTheHighAvailabilityGroupIsAddedBackToLoadBalancer() throws CommunicationException {
				allOf(peerOperation1, peerOperation1Secondary, peerOperation2, 
						peerOperation2Secondary, peerOperation3, peerOperation3And4Secondary, 
						peerOperation4, peerOperation5).areDead();
				
				peerOperation1.markAlive();
				group.sendClientInitiatedRequest(session, initialRequest, ResponseListener.NO_RESPONSE_LISTENER);
				peerOperation1.verifyRequestReceipt();
				
				group.sendClientInitiatedRequest(otherSession, createCCInitialRequest(OTHER_SESSION_ID_VALUE), ResponseListener.NO_RESPONSE_LISTENER);
				peerOperation1.verifyRequestReceipts(2);

				peerOperation2Secondary.markAlive();
				group.sendClientInitiatedRequest(otherSession, createCCInitialRequest(OTHER_SESSION_ID_VALUE), ResponseListener.NO_RESPONSE_LISTENER);
				peerOperation2Secondary.verifyRequestReceipt();
			}
			
			@Test
			public void onTooBusyAnswerFromSelectedPrimaryPeerRequestIsForwardedToSecondaryPeerOfThePrimaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				peerOperation1Secondary.verifyRequestReceipt();
				DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(initialRequest);
				
				assertNoRequestSentTo(peerOperation2, peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation1Secondary);
			}
			
			@Test
			public void givenPrimaryPeerInHighAvailabilityGroupWasDeadAndRequestIsSentToSecondaryThePrimaryWillNotBeTriedOnTimeoutFromSecondaryEvenIfItComesAlive() throws CommunicationException {
				peerOperation1.markDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.markAlive();
				
				peerOperation1Secondary.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation1.verifyRequestNotReceived();
				peerOperation2.verifyRequestReceipt();
			}
			
			@Test
			public void givenPrimaryPeerInHighAvailabilityGroupWasDeadAndRequestIsSentToSecondaryThePrimaryWillNotBeTriedOnTooBusyAnswerFromSecondaryEvenIfItComesAlive() throws CommunicationException {
				peerOperation1.markDead();
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.markAlive();
				
				peerOperation1Secondary.sendsTooBusyAnswer(session);
				
				peerOperation1.verifyRequestNotReceived();
				peerOperation2.verifyRequestReceipt();
			}
			
			@Test
			public void onRequestTimeoutAnswerFromSelectedPrimaryPeerRequestIsForwardedToSecondaryPeerOfThePrimaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation1Secondary.verifyRequestReceipt();
				DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(initialRequest);
				
				assertNoRequestSentTo(peerOperation2, peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation1Secondary);
			}
			
			@Test
			public void onTooBusyAnswerFromPrimaryPeerIfSecondaryPeerInHighAvailabilityGroupIsDeadThenRequestIsLoadBalanced() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1Secondary.markDead();
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				peerOperation1Secondary.verifyAnswerNotReceived();
				
				peerOperation2.verifyRequestReceipt();
			}
			
			@Test
			public void onRequestTimeoutFromPrimaryPeerIfSecondaryPeerInHighAvailabilityGroupIsDeadThenRequestIsLoadBalanced() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1Secondary.markDead();
				
				peerOperation1.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation1Secondary.verifyAnswerNotReceived();
				
				peerOperation2.verifyRequestReceipt();
			}
			
			@Test
			public void onTooBusyAnswerFromSelectedPrimaryPeerAndItsSecondaryPeerRequestIsForwardedToOtherPrimaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary).sendTooBusyAnswer(session);
				
				peerOperation2.verifyRequestReceipt();
				DiameterAssertion.assertThat(peerOperation2.getLastSentDiameterPacket()).hasHeaderOf(initialRequest);
				
				assertNoRequestSentTo(peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void onRequestTimeoutAnswerFromSelectedPrimaryPeerAndItsSecondaryPeerRequestIsForwardedToOtherPrimaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary).doNotAnswerWithinRequestTimeout(session);
				
				peerOperation2.verifyRequestReceipt();
				DiameterAssertion.assertThat(peerOperation2.getLastSentDiameterPacket()).hasHeaderOf(initialRequest);
				
				assertNoRequestSentTo(peerOperation2Secondary, peerOperation3);
				
				verifyAnswerReceived(peerOperation2);
			}
			
			@Test
			public void onTooBusyAnswerFromPeersWillKeepTryingToSendRequestToAllPrimarySecondaryPairsIfTransactionTimeoutIsNotReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4,
						peerOperation5)
				.sendTooBusyAnswer(session);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
				
			}
			
			// TODO the event when one HA group fails was TOO busy but when trying with other HA group was timeout then timeout will be sent to listener.
			@Test
			public void onRequestTimeoutAnswerFromPeersWillKeepTryingToSendRequestToAllPrimarySecondaryPairsIfTransactionTimeoutIsNotReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4,
						peerOperation5)
				.doNotAnswerWithinRequestTimeout(session);
				
				verify(userListener, times(1)).requestTimedout(peerOperation5.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void onTooBusyAnswerFromPeersWillKeepTryingToSendRequestToPrimarySecondaryPairsAndStopAfterTransactionTimeoutIsReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, ResponseListener.NO_RESPONSE_LISTENER);
				
				allOf(peerOperation1, peerOperation1Secondary).sendTooBusyAnswer(session);
				
				transactionTimeoutReached();
				peerOperation2.sendsTooBusyAnswer(session);
				
				assertNoRequestSentTo(peerOperation2Secondary, peerOperation3);
			}
			
			@Test
			public void onRequestTimeoutAnswerFromPeersWillKeepTryingToSendRequestToPrimarySecondaryPairsAndStopAfterTransactionTimeoutIsReached() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, ResponseListener.NO_RESPONSE_LISTENER);
				
				allOf(peerOperation1, peerOperation1Secondary).doNotAnswerWithinRequestTimeout(session);
				
				transactionTimeoutReached();
				peerOperation2.doesNotAnswerWithinRequestTimeout(session);
				
				assertNoRequestSentTo(peerOperation2Secondary, peerOperation3);
			}
			
			@Test
			public void givesUpOnTheRequestIfTransactionTimesoutBeforeRequestTimeoutFromSecondaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				transactionTimeoutReached();
				
				peerOperation1Secondary.doesNotAnswerWithinRequestTimeout(session);
				
				verify(userListener, times(1)).requestTimedout(peerOperation1Secondary.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void givesUpOnTheRequestIfTransactionTimesoutBeforeRetryableAnswerFromSecondaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				transactionTimeoutReached();
				
				peerOperation1Secondary.sendsTooBusyAnswer(session);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
			}
			
			@Test
			public void givesUpOnRequestOnTooBusyAnswerFromSecondaryPeerIfNoOtherHighAvailabilityGroupIsAlive() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				peerOperation1.sendsTooBusyAnswer(session);
				
				allOf(peerOperation2, peerOperation2Secondary, peerOperation3, 
						peerOperation3And4Secondary, peerOperation4, peerOperation5).areDead();
				
				peerOperation1Secondary.sendsTooBusyAnswer(session);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
			}
			
			@Test
			public void givesUpOnRequestOnRequestTimeoutFromSecondaryPeerIfNoOtherHighAvailabilityGroupIsAlive() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				peerOperation1.sendsTooBusyAnswer(session);
				
				allOf(peerOperation2, peerOperation2Secondary, peerOperation3, 
						peerOperation3And4Secondary, peerOperation4, peerOperation5).areDead();
				
				peerOperation1Secondary.doesNotAnswerWithinRequestTimeout(session);
				
				verify(userListener, times(1)).requestTimedout(peerOperation1Secondary.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void onTooBusyAnswerIfSamePeerIsSecondaryForMoreThanOnePrimaryPeerThenItWillNotBeTriedMoreThanOnce() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4,
						peerOperation5)
				.sendTooBusyAnswer(session);
				
				peerOperation3And4Secondary.verifyRequestReceipts(1);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
			}
			
			@Test
			public void onTooBusyAnswerIfPrimaryPeerIsSecondaryForOtherPeerThenItWillNotBeTriedMoreThanOnce() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4, peerOperation5)
				.sendTooBusyAnswer(session);
				
				peerOperation1.verifyRequestReceipts(1);
				
				DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(initialRequest)
				.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
			}
			
			@Test
			public void onRequestTimeoutAnswerIfSamePeerIsSecondaryForMoreThanOnePrimaryPeerThenItWillNotBeTriedMoreThanOnce() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4, peerOperation5)
				.doNotAnswerWithinRequestTimeout(session);
				
				peerOperation3And4Secondary.verifyRequestReceipts(1);
				
				verify(userListener, times(1)).requestTimedout(peerOperation5.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void onRequestTimeoutAnswerIfPrimaryPeerIsSecondaryForOtherPeerThenItWillNotBeTriedMoreThanOnce() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary,
						peerOperation4, peerOperation5)
				.doNotAnswerWithinRequestTimeout(session);
				
				peerOperation1.verifyRequestReceipts(1);
				
				verify(userListener, times(1)).requestTimedout(peerOperation5.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void givenSecondaryForAllHighAvailabilityPairsIsSameOnTooBusyAnswerFromAHighAvailabilityPairIfPrimaryPeerOfOtherHighAvailabilityPairIsDeadTheSecondaryWillNotBeTriedAgain() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3)
				.sendTooBusyAnswer(session);
				
				peerOperation4.markDead();
				
				peerOperation3And4Secondary.sendsTooBusyAnswer(session);
				
				peerOperation3And4Secondary.verifyRequestReceipts(1);
			}
			
			@Test
			public void givenSecondaryForAllHighAvailabilityPairsIsSameOnRequestTimeoutFromAHighAvailabilityPairIfPrimaryPeerOfOtherHighAvailabilityPairIsDeadTheSecondaryWillNotBeTriedAgain() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				allOf(peerOperation1, peerOperation1Secondary,
						peerOperation2, peerOperation2Secondary,
						peerOperation3)
				.doNotAnswerWithinRequestTimeout(session);
				
				peerOperation4.markDead();
				
				peerOperation3And4Secondary.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation3And4Secondary.verifyRequestReceipts(1);
			}
			
			@Test
			public void failsIfAllTheHighAvailabilityGroupsAreDead() throws CommunicationException {
				allOf(peerOperation1, peerOperation1Secondary, 
						peerOperation2, peerOperation2Secondary,
						peerOperation3, peerOperation3And4Secondary, 
						peerOperation4, peerOperation5)
				.areDead();
				
				expectedException.expect(CommunicationException.class);
				
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
			}
			
		}
		
		public class ForwardingSubsequentRequestsForASession {
			
			private DiameterRequest subsequentRequest;
			
			@Before
			public void setUp() throws CommunicationException {
				initialRequestServed();
				subsequentRequest = createCCUpdateRequestFrom(originatorPeerData);
			}
			
			public class SessionFailoverWithinGroupIsDisabled {
				
				@Test
				public void givenStatefullPeerIsAliveRequestsAreForwardedToStatefullPeer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequest);
					
					verifyAnswerReceived(peerOperation1);
				}
				
				@Test
				public void givenStatefullPeerIsDeadRequestSwitchesOverToSecondaryPeer() throws CommunicationException {
					peerOperation1.markDead();
					
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1Secondary.verifyRequestReceipt();
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequest);
					
					verifyAnswerReceived(peerOperation1Secondary);
				}
				
				@Test
				public void givenStatefullPeerIsBusyRequestSwitchesOverToSecondaryPeer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
					
					peerOperation1.sendsTooBusyAnswer(session);
					
					peerOperation1Secondary.verifyRequestReceipt();
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequest);
				}
				
				@Test
				public void whenSwitchOverOccursBecausePrimaryPeerIsDeadSecondaryPeerBecomesTheStatefullPeer() throws CommunicationException {
					peerOperation1.markDead();
					
					requestSentAndAnswerFromSecondaryPeer();
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequestAfterFailover);
					
					verifyAnswerReceived(peerOperation1Secondary);
				}
				
				@Test
				public void whenSwitchOverOccursBecausePrimaryPeerIsBusyThenSecondaryPeerBecomesTheStatefullPeerOnSuccessfulAnswer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
					
					peerOperation1.sendsTooBusyAnswer(session);
					peerOperation1Secondary.sendsAnswer(createSuccessAnswerFrom(peerOperation1Secondary), session);
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequestAfterFailover);
					
					verifyAnswerReceived(peerOperation1Secondary);
				}
				
				@Test
				public void whenSwitchOverOccursBecauseRequestToPrimaryPeerTimesoutThenSecondaryPeerBecomesTheStatefullPeerOnSuccessfulAnswer() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1.doesNotAnswerWithinRequestTimeout(session);
					peerOperation1Secondary.sendsAnswer(createSuccessAnswerFrom(peerOperation1Secondary),session);
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequestAfterFailover);
					
					verifyAnswerReceived(peerOperation1Secondary);
				}
				
				@Test
				public void whenSwitchOverOccursThenPrimaryPeerRemainsTheStatefullPeerOnRetryableAnswerFromSecondary() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
					
					allOf(peerOperation1, peerOperation1Secondary).sendTooBusyAnswer(session);
					
					DiameterRequest otherSubsequentRequest = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, otherSubsequentRequest, userListener);
					
					peerOperation1.verifyRequestReceipts(3);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(otherSubsequentRequest);
					
					verifyAnswerReceived(peerOperation1);
				}
				
				@Test
				public void whenSwitchOverOccursThenPrimaryPeerRemainsTheStatefullPeerOnRequestTimeoutFromSecondary() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
					
					peerOperation1.sendsTooBusyAnswer(session);
					peerOperation1Secondary.doesNotAnswerWithinRequestTimeout(session);
					
					DiameterRequest otherSubsequentRequest = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, otherSubsequentRequest, userListener);
					
					peerOperation1.verifyRequestReceipts(3);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(otherSubsequentRequest);
					
					verifyAnswerReceived(peerOperation1);
				}
				
				@Test
				public void secondaryPeerRemainsStatefullPeerAndDoesNotSwitchbackToPrimaryWhenPrimaryPeerBecomesAliveAgain() throws CommunicationException {
					peerOperation1.markDead();
					
					requestSentAndAnswerFromSecondaryPeer();
					
					peerOperation1.markAlive();
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1Secondary.getLastSentDiameterPacket()).hasHeaderOf(subsequentRequestAfterFailover);
					
					verifyAnswerReceived(peerOperation1Secondary);
				}
				
				@Test
				public void switchesBackToPrimaryPeerOnlyWhenSecondaryStatefullPeerGoesDead() throws CommunicationException {
					peerOperation1.markDead();
					
					requestSentAndAnswerFromSecondaryPeer();
					
					peerOperation1.markAlive();
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					
					peerOperation1Secondary.markDead();
					DiameterRequest switchBackRequestToPrimary = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, switchBackRequestToPrimary, userListener);
					
					peerOperation1.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(switchBackRequestToPrimary);
					
					verifyAnswerReceived(peerOperation1);
				}
				
				@Test
				public void switchesBackToPrimaryPeerOnlyWhenSecondaryStatefullPeerIsBusy() throws CommunicationException {
					peerOperation1.markDead();
					
					requestSentAndAnswerFromSecondaryPeer();
					
					peerOperation1.markAlive();
					
					DiameterRequest subsequentRequestAfterFailover = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, subsequentRequestAfterFailover, userListener);
					
					peerOperation1Secondary.verifyRequestReceipts(2);
					
					DiameterRequest switchBackRequestToPrimary = createCCUpdateRequestFrom(originatorPeerData);
					group.sendClientInitiatedRequest(session, switchBackRequestToPrimary, userListener);
					peerOperation1Secondary.sendsTooBusyAnswer(session);
					
					peerOperation1.verifyRequestReceipts(2);
					DiameterAssertion.assertThat(peerOperation1.getLastSentDiameterPacket()).hasHeaderOf(switchBackRequestToPrimary);
					
					verifyAnswerReceived(peerOperation1);
				}
				
				@Test
				public void failsWhenBothPrimaryAndSecondaryStatefullPeersAreDead() throws CommunicationException {
					allOf(peerOperation1, peerOperation1Secondary).areDead();
					
					expectedException.expect(CommunicationException.class);
					group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
				}
				
				@Test
				public void givesUpWhenRequestTimesoutFromActivePeerAndStandbyCommunicatorIsDead() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1Secondary.markDead();
					peerOperation1.doesNotAnswerWithinRequestTimeout(session);
					
					verify(userListener, times(1)).requestTimedout(peerOperation1.getDiameterPeer().getHostIdentity(), session);
				}
				
				@Test
				public void givesUpWhenTooBusyAnswerReceivedFromActivePeerAndStandbyCommunicatorIsDead() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					peerOperation1Secondary.markDead();
					peerOperation1.sendsTooBusyAnswer(session);
				
					DiameterAssertion.assertThat(userListener.diameterAnswer).hasHeaderOf(subsequentRequest)
					.hasResultCode(ResultCode.DIAMETER_TOO_BUSY);
				}
				
				@Test
				@Ignore
				//TODO discuss this with harsh and take appropriate steps
				public void notifiesUserWithUnableToDeliverAnswerWhenBothPrimaryAndSecondaryStatefullPeersAreBusy() throws CommunicationException {
					group.sendClientInitiatedRequest(session, subsequentRequest, userListener);
					
					allOf(peerOperation1, peerOperation1Secondary).sendTooBusyAnswer(session);
					
					verify(userListener, times(1)).responseReceived(userListener.diameterAnswer, peerOperation1Secondary.getDiameterPeer().getHostIdentity(), session);
					DiameterAssertion.assertThat(userListener.diameterAnswer).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
				}
			}
			
			private void requestSentAndAnswerFromSecondaryPeer() throws CommunicationException {
				group.sendClientInitiatedRequest(session, subsequentRequest, ResponseListener.NO_RESPONSE_LISTENER);
				
				peerOperation1Secondary.sendsAnswer(createSuccessAnswerFrom(peerOperation1Secondary), session);
			}
			
			private void initialRequestServed() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, ResponseListener.NO_RESPONSE_LISTENER);
				
				peerOperation1.verifyRequestReceipt().sendsAnswer(createSuccessAnswerFrom(peerOperation1), session);
			}
		}
		
		private DiameterPeerGroupParameter createGroup(String name, int maxRetry, long transactionTimeout) {
			Map<String, Integer> peers = new LinkedHashMap<String, Integer>();
			peers.put(peerOperation1.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation2.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation3.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation4.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation5.getDiameterPeer().getHostIdentity(), 1);
			return new DiameterPeerGroupParameter(name, peers, LoadBalancerType.ROUND_ROBIN, true, maxRetry, transactionTimeout);
		}
	}
	
	public class LoadBalancingWithMixMode {
		
		private DiameterPeerSpy peerOperation1;
		private DiameterPeerSpy peerOperation2;
		private DiameterPeerSpy peerOperation2Secondary;
		private DiameterPeerCommGroup group;

		@Before
		public void setUp() {
			PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
					.withHostIdentity("peer1.test.com")
					.withRealmName("test.com").build();
			PeerData peerData2 = new PeerDataProvider().withPeerName("testPeer2")
					.withHostIdentity("peer2.test.com")
					.withSecondaryPeer("testSecondaryPeer2")
					.withRealmName("test.com").build();
			PeerData peerData2Secondary = new PeerDataProvider().withPeerName("testSecondaryPeer2")
					.withHostIdentity("peer2secondary.test.com")
					.withRealmName("test.com").build();
			
			addPeer(peerData1)
			.addPeer(peerData2)
			.addPeer(peerData2Secondary);
	
			peerOperation1 = getPeerOperation(peerData1.getHostIdentity());
			peerOperation2 = getPeerOperation(peerData2.getHostIdentity());
			peerOperation2Secondary = getPeerOperation(peerData2Secondary.getHostIdentity());
			
			DiameterPeerGroupParameter groupData = createGroup("peer group1", 3, TRANSACTION_TIMEOUT);
			group = diameterPeerGroupFactory.getInstance(groupData);
		}
		
		private DiameterPeerGroupParameter createGroup(String name, int maxRetry, int transactionTimeout) {
			Map<String, Integer> peers = new LinkedHashMap<String, Integer>();
			peers.put(peerOperation1.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation2.getDiameterPeer().getHostIdentity(), 1);
			peers.put(peerOperation2Secondary.getDiameterPeer().getHostIdentity(), 1);
			return new DiameterPeerGroupParameter(name, peers, LoadBalancerType.ROUND_ROBIN, true, maxRetry, transactionTimeout);
		}
		
		public class ForwardingInitialRequestForASession {
			
			@Test
			public void whenNonHAPeerAnswersTooBusyLoadBalancerTriesToForwardRequestToHighAvailabilityGroup() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.sendsTooBusyAnswer(session);
				
				peerOperation2.verifyRequestReceipt();
				peerOperation2.sendsTooBusyAnswer(session);
				peerOperation2Secondary.verifyRequestReceipt();
			}
			
			@Test
			public void whenNonHAPeerDoesNotAnswerWithinRequestTimeoutLoadBalancerTriesToForwardRequestToHighAvailabilityGroup() throws CommunicationException {
				group.sendClientInitiatedRequest(session, initialRequest, userListener);
				
				peerOperation1.doesNotAnswerWithinRequestTimeout(session);
				
				peerOperation2.verifyRequestReceipt();
				peerOperation2.doesNotAnswerWithinRequestTimeout(session);
				peerOperation2Secondary.verifyRequestReceipt();
			}
		}
	}
	
	private void transactionTimeoutReached() {
		timesource.advance(TRANSACTION_TIMEOUT);
	}
	
	private void verifyAnswerReceived(DiameterPeerSpy peerOperation) {
		DiameterAnswer answer = createSuccessAnswerFrom(peerOperation);
		peerOperation.sendsAnswer(answer, session);
		
		verify(userListener, times(1)).responseReceived(answer, peerOperation.getDiameterPeer().getHostIdentity(), session);
	}
}
