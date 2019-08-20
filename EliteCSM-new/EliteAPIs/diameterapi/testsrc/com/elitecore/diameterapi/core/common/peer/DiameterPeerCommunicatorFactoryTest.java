package com.elitecore.diameterapi.core.common.peer;

import static com.elitecore.diameterapi.diameter.DiameterMatchers.PacketMatchers.hasResultCode;
import static com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy.allOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.peer.api.AnswerMemorizingResponseListener;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DummyPeerProvider;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class DiameterPeerCommunicatorFactoryTest {

	private static final String ANY_SESSION_ID = "SESSION1";
	private DiameterPeerCommunicatorFactory factory;
	private DummyPeerProvider peerProvider;
	private DummyStackContext stackContext;
	private DiameterSession session;

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void loadDiameterDictionary() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() {
		peerProvider = new DummyPeerProvider();
		stackContext = new DummyStackContext(peerProvider);
		factory = new DiameterPeerCommunicatorFactory(stackContext, peerProvider);
		session = new DiameterSession(ANY_SESSION_ID, null);
	}
	
	public class ServerInitiatedRequest {
		
		private DiameterPeerSpy primaryPeerSpy;
		private DiameterPeerSpy secondaryPeerSpy;
		private AnswerMemorizingResponseListener responseListener = spy(new AnswerMemorizingResponseListener());
		
		public class WithFailover {
			
			private DiameterPeerCommunicator peerCommunicator;

			@Before
			public void setUp() {
				PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
						.withHostIdentity("peer1.test.com")
						.withSecondaryPeer("testSecondaryPeer1")
						.withRealmName("test.com").build();
				PeerData peer1SecondaryData = new PeerDataProvider().withPeerName("testSecondaryPeer1")
						.withHostIdentity("peer1secondary.test.com")
						.withRealmName("test.com").build();

				primaryPeerSpy = registerPeer(peerData1);
				secondaryPeerSpy = registerPeer(peer1SecondaryData);
				
				peerCommunicator = factory.createInstance(peerData1.getHostIdentity());
			}
			
			@Test
			public void requestIsSentToPrimaryPeerIfAlive() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
				
				primaryPeerSpy.verifyRequestReceipt();
			}
			
			@Test
			public void requestIsSentToFailoverPeerIfPrimaryIsDead() throws CommunicationException {
				primaryPeerSpy.markDead();
				
				DiameterRequest rar = createRAR();
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
				
				secondaryPeerSpy.verifyRequestReceipt();
			}
			
			@Test
			public void failsoverAndSendsRequestToFailoverPeerIfRequestToPrimaryPeerTimesOut() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.doesNotAnswerWithinRequestTimeout(session);
				
				secondaryPeerSpy.verifyRequestReceipt();
			}
			
			@Test
			public void failsoverAndSendsRequestToFailoverPeerOnTooBusyAnswerFromPrimaryPeer() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.sendsTooBusyAnswer(session);
				
				secondaryPeerSpy.verifyRequestReceipt();
			}
			
			@Test
			public void notifiesUserOfTimeoutEventIfRequestToBothPeersTimeOut() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
				
				allOf(primaryPeerSpy, secondaryPeerSpy).doNotAnswerWithinRequestTimeout(session);
				
				verify(responseListener).requestTimedout(secondaryPeerSpy.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void notifiesUserOfTooBusyAnswerOnTooBusyAnswerFromBothPeers() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
				
				allOf(primaryPeerSpy, secondaryPeerSpy).sendTooBusyAnswer(session);
				
				assertThat(responseListener.diameterAnswer, hasResultCode(ResultCode.DIAMETER_TOO_BUSY));
			}
			
			@Test
			public void notifiesUserOfAnswerReceivedOnSuccessAnswerFromPeer() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.sendsAnswer(createSuccessAnswerFrom(primaryPeerSpy), session);
				
				assertThat(responseListener.diameterAnswer, hasResultCode(ResultCode.DIAMETER_SUCCESS));
			}
			
			@Test
			public void notifiesUserOfAnswerReceivedOnSuccessAnswerFromFailoverPeer() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.sendsTooBusyAnswer(session);
				
				secondaryPeerSpy.sendsAnswer(createSuccessAnswerFrom(secondaryPeerSpy), session);
				
				assertThat(responseListener.diameterAnswer, hasResultCode(ResultCode.DIAMETER_SUCCESS));
			}
			
			@Test
			public void notifiesUserOfRequestTimeoutIfRequestToPrimaryPeerTimesOutAndSecondaryPeerIsDead() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				secondaryPeerSpy.markDead();
				
				primaryPeerSpy.doesNotAnswerWithinRequestTimeout(session);
				
				verify(responseListener).requestTimedout(primaryPeerSpy.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void notifiesUserOfTooBusyAnswerOnTooBusyAnswerFromPrimaryPeerAndSecondaryPeerIsDead() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				secondaryPeerSpy.markDead();
				
				primaryPeerSpy.sendsTooBusyAnswer(session);
				
				assertThat(responseListener.diameterAnswer, hasResultCode(ResultCode.DIAMETER_TOO_BUSY));
			}
		}
		
		public class WithoutFailover {
			
			private DiameterPeerCommunicator peerCommunicator;
			private AnswerMemorizingResponseListener responseListener = spy(new AnswerMemorizingResponseListener());
			@Before
			public void setUp() {
				PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
						.withHostIdentity("peer1.test.com")
						.withRealmName("test.com").build();

				primaryPeerSpy = registerPeer(peerData1);
				
				peerCommunicator = factory.createInstance(peerData1.getHostIdentity());
			}
			
			@Test
			public void requestIsSentToPrimaryPeerIfAlive() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
				
				primaryPeerSpy.verifyRequestReceipt();
			}

			@Test
			public void failsIfPrimaryPeerIsDead() throws CommunicationException {
				primaryPeerSpy.markDead();
				
				DiameterRequest rar = createRAR();

				exception.expect(CommunicationException.class);
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);
			}
			
			@Test
			public void notifiesUserOfTimeoutEventIfRequestTimesOut() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.doesNotAnswerWithinRequestTimeout(session);
				
				verify(responseListener).requestTimedout(primaryPeerSpy.getDiameterPeer().getHostIdentity(), session);
			}
			
			@Test
			public void notifiesUserOfAnswerReceivedOnSuccessAnswerFromPeer() throws CommunicationException {
				DiameterRequest rar = createRAR();
				
				peerCommunicator.sendServerInitiatedRequest(session, rar, responseListener);

				primaryPeerSpy.sendsAnswer(createSuccessAnswerFrom(primaryPeerSpy), session);
				
				assertThat(responseListener.diameterAnswer, hasResultCode(ResultCode.DIAMETER_SUCCESS));
			}
		}
	}
	
	private DiameterPeerSpy registerPeer(PeerData peerData) {
		stackContext.addPeerData(peerData);
		DiameterPeerSpy peerSpy = 
				new DiameterPeerSpy(stackContext, peerData);
		peerProvider.addPeer(peerSpy.getDiameterPeer());
		return peerSpy;
	}
	
	private DiameterRequest createRAR() {
		return DiameterPacketBuilder.localRequestBuilder()
				.commandCode(CommandCode.RE_AUTHORIZATION)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, "test.com")
				.addAVP(DiameterAVPConstants.SESSION_ID, ANY_SESSION_ID)
				.build();
	}
	
	private DiameterAnswer createSuccessAnswerFrom(DiameterPeerSpy routingPeer) {
		return DiameterPacketBuilder.answerBuilder(routingPeer.getLastSentDiameterPacket().getAsDiameterRequest())
				.resultCode(ResultCode.DIAMETER_SUCCESS)
				.addAVP(DiameterAVPConstants.CLASS, "hello")
				.build();
	}
}
