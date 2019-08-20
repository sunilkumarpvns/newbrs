package com.elitecore.diameterapi.diameter.common.peers;

import static com.elitecore.diameterapi.diameter.DiameterMatchers.PacketMatchers.hasResultCode;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionEventListener;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.PeerData.DuplicateConnectionPolicyType;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder.DiameterRequestBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.capabilityexchange.ApplicationProviderFactory;
import com.elitecore.diameterapi.diameter.common.peers.capabilityexchange.PeerApplicationProvider;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class DiameterPeerTest {
	
	private DiameterPeerStub diameterPeer;
	private PeerDataImpl peerData;
	private DummyStackContext dummyStackContext = spy(new DummyStackContext(null));
	private DiameterRequestBuilder requestBuilder;
	private NetworkConnectionHandler connectionHandler;
	private NetworkConnectionEventListener connectionEventListener;
	private FixedTimeSource timesource;
	private DiameterAnswer duplicateConnectionAnswer;

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws InitializationFailedException {
		timesource = new FixedTimeSource(System.currentTimeMillis());
		dummyStackContext.addApplicationsIdentifiersList(ApplicationIdentifier.NASREQ);

		peerData = new PeerDataProvider().withPeerName("testPeer1")
				.withHostIdentity("peer1.test.com")
				.withRealmName("test.com")
				.build();

		connectionHandler = Mockito.mock(NetworkConnectionHandler.class);
		when(connectionHandler.isConnected()).thenReturn(true);
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DiameterPeerTest.this.connectionEventListener = invocation.getArgumentAt(0, NetworkConnectionEventListener.class);
				return null;
			}
		}).when(connectionHandler).addNetworkConnectionEventListener(Mockito.any(NetworkConnectionEventListener.class));
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				when(connectionHandler.isConnected()).thenReturn(false);
				return null;
			}
		}).when(connectionHandler).terminateConnection();

		requestBuilder = DiameterPacketBuilder.requestBuilder(peerData);
		addMandatoryAVPs(requestBuilder);

	}

	public class SessionCleanup {

		public class SessionCleanupOnCERIsDisabled {

			@Before
			public void setUp() throws InitializationFailedException {
				peerData.setSessionCleanUpOnCER(false);
				diameterPeer = new DiameterPeerStub(peerData);
				diameterPeer.init();
				diameterPeer.start();
			}

			public class OriginStateIdSessionReleaseCompliance {

				@Test
				public void doesNotReleaseAnySessionsIfOriginStateIdAvpIsNotPresent() {
					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					verifyNoSessionReleased(cer);
				}

				@Test
				public void noSessionAssociatedWithPeerIsReleasedOnFirstCapabilityExchangeIfOriginStateIdIsPositive() {
					addOriginStateId(requestBuilder, 1);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					verifyNoSessionReleased(cer);
				}

				@Test
				public void noSessionAssociatedWithPeerIsReleasedOnFirstCapabilityExchangeIfOriginStateIdIsZero() {
					addOriginStateId(requestBuilder, 0);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					verifyNoSessionReleased(cer);
				}

				@Test
				public void onSubsequentCapabilityExchangeSessionsAreNotReleasedIfOriginStateIdIsPositiveAndUnchangedInRequest() {
					int originStateId = 1;

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					reset(dummyStackContext);

					disconnectInitialConnection();

					NetworkConnectionHandler subsequentConnection = aNewConnectedConnection();

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest subsequentCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(subsequentCer, subsequentConnection);

					verifyNoSessionReleased(subsequentCer);
				}

				@Test
				public void onSubsequentCapabilityExchangeSessionsAreNotReleasedIfOriginStateIdIsZeroAndUnchangedInRequest() {
					int originStateId = 0;

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					reset(dummyStackContext);

					disconnectInitialConnection();

					NetworkConnectionHandler subsequentConnection = aNewConnectedConnection();

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest subsequentCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(subsequentCer, subsequentConnection);

					verifyNoSessionReleased(subsequentCer);
				}

				@Test
				public void onSubsequentCapabilityExchangeSessionsAreReleasedIfOriginStateIdIsIncrementedInRequest() {
					int originStateId = 0;

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					reset(dummyStackContext);

					disconnectInitialConnection();

					requestBuilder = DiameterPacketBuilder.requestBuilder(peerData);
					addMandatoryAVPs(requestBuilder);

					NetworkConnectionHandler subsequentConnection = aNewConnectedConnection();

					addOriginStateId(requestBuilder, ++originStateId);

					DiameterRequest subsequentCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(subsequentCer, subsequentConnection);

					verify(dummyStackContext).releasePeerSessions(subsequentCer);
				}

				@Test
				/*
				 * RFC suggests that sessions should be released if origin state id received is greater than previous.
				 * But we remove sessions on inequality.
				 */
				public void onSubsequentCapabilityExchangeSessionsAreReleasedIfOriginStateIdIsDecrementedInRequest() {
					int originStateId = 1;

					addOriginStateId(requestBuilder, originStateId);

					DiameterRequest cer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(cer, connectionHandler);

					reset(dummyStackContext);

					disconnectInitialConnection();

					requestBuilder = DiameterPacketBuilder.requestBuilder(peerData);
					addMandatoryAVPs(requestBuilder);

					NetworkConnectionHandler subsequentConnection = aNewConnectedConnection();

					addOriginStateId(requestBuilder, --originStateId);

					DiameterRequest subsequentCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(subsequentCer, subsequentConnection);

					verify(dummyStackContext).releasePeerSessions(subsequentCer);
				}

				private void verifyNoSessionReleased(DiameterRequest cer) {
					verify(dummyStackContext, never()).releasePeerSessions(cer);
				}
			}
		}
	}


	public class DuplicateConnection {

		public class DefaultPolicy {
			
			public class DeviceWatchdogIsDisabled {

				@Before
				public void setUp() throws InitializationFailedException {
					peerData.setWatchdogInterval(0);
					diameterPeer = new DiameterPeerStub(peerData);
					diameterPeer.init();
					diameterPeer.start();
				}

				@Test
				public void rejectsDuplicateConnectionIfItIsAttemptedBeforeCooldownPeriod() {
					DiameterRequest initialCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

					DiameterRequest duplicateCer = requestBuilder.build();
					NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

					diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

					verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
				}

				@Test
				public void rejectsFirstAttemptOfDuplicateConnectionEvenIfCooldownPeriodIsOverAndNoTrafficIsObservedOnOldConnection() {
					DiameterRequest initialCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

					timesource.advance(3001);

					DiameterRequest duplicateCer = requestBuilder.build();
					NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

					diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

					verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
				}
				
				@Test
				public void acceptsSecondAttemptOfDuplicateConnectionAndTerminatesOldConnectionIfCooldownPeriodIsOverAndNoTrafficIsObservedOnOldConnection() {
					DiameterRequest initialCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

					timesource.advance(3001);

					DiameterRequest duplicateCer = requestBuilder.build();
					NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

					diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

					timesource.advance(3001);
					
					duplicateCer = requestBuilder.build();
					
					duplicationConnectionHandler = aNewConnectedConnection();

					diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);
					
					verify(connectionHandler).terminateConnection();
					assertThat(duplicateConnectionAnswer, hasResultCode(ResultCode.DIAMETER_SUCCESS));
				}
				
				@Test
				public void rejectsDuplicateConnectionIfCooldownPeriodIsOverAndButTrafficWasObservedOnOldConnection() {
					DiameterRequest initialCer = requestBuilder.build();

					diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

					timesource.advance(3001);

					diameterPeer.processReceivedDiameterPacket(createDWR(), connectionHandler);

					DiameterRequest duplicateCer = requestBuilder.build();
					NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

					diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

					verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
				}
			}
		}
		
		public class DiscardOldPolicy {
			
			@Before
			public void setUp() throws InitializationFailedException {
				peerData.setWatchdogInterval(1000);
				peerData.setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType.DISCARD_OLD);
				diameterPeer = new DiameterPeerStub(peerData);
				diameterPeer.init();
				diameterPeer.start();
			}
		
			@Test
			public void rejectsFirstAttemptOfDuplicateConnectionEvenIfNoTrafficIsObservedOnOldConnection() {
				DiameterRequest initialCer = requestBuilder.build();

				diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

				DiameterRequest duplicateCer = requestBuilder.build();
				NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

				verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
			}
			
			@Test
			public void rejectsFirstAttemptOfDuplicateConnectionIfTrafficIsObservedOnOldConnection() {
				DiameterRequest initialCer = requestBuilder.build();

				diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

				timesource.advance(1000);
				
				diameterPeer.processReceivedDiameterPacket(createDWR(), connectionHandler);
				
				DiameterRequest duplicateCer = requestBuilder.build();
				NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

				verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
			}
			
			@Test
			public void acceptsImmediateSecondAttemptOfDuplicateConnectionIfNoTrafficIsObservedOnOldConnection() {
				DiameterRequest initialCer = requestBuilder.build();

				diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

				timesource.advance(1000);
				
				diameterPeer.processReceivedDiameterPacket(createDWR(), connectionHandler);
				
				DiameterRequest duplicateCer = requestBuilder.build();
				NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);
				
				duplicateCer = requestBuilder.build();
				addMandatoryAVPs(requestBuilder);
				
				duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

				verify(connectionHandler).terminateConnection();
				assertThat(duplicateConnectionAnswer, hasResultCode(ResultCode.DIAMETER_SUCCESS));
			}
			
			@Test
			public void rejectsSubsequentAttemptsOfDuplicateConnectionIfTrafficIsObservedOnOldConnection() {
				DiameterRequest initialCer = requestBuilder.build();

				diameterPeer.processReceivedDiameterPacket(initialCer, connectionHandler);

				timesource.advance(1000);
				
				diameterPeer.processReceivedDiameterPacket(createDWR(), connectionHandler);
				
				DiameterRequest duplicateCer = requestBuilder.build();
				NetworkConnectionHandler duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);
				
				diameterPeer.processReceivedDiameterPacket(createDWR(), connectionHandler);
				
				duplicateCer = requestBuilder.build();
				duplicationConnectionHandler = aNewConnectedConnection();

				diameterPeer.processReceivedDiameterPacket(duplicateCer, duplicationConnectionHandler);

				verify(duplicationConnectionHandler).closeConnection(ConnectionEvents.REJECT_CONNECTION);
			}
		}
	}

	private NetworkConnectionHandler aNewConnectedConnection() {
		NetworkConnectionHandler subsequentConnectionHandler = Mockito.mock(NetworkConnectionHandler.class);
		when(subsequentConnectionHandler.isConnected()).thenReturn(true);
		try {
			doAnswer(new Answer<Void>() {

				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					duplicateConnectionAnswer = invocation.getArgumentAt(0, DiameterAnswer.class);
					return null;
				}
			}).when(subsequentConnectionHandler).send(Mockito.any(Packet.class));
		} catch (IOException e) {
			throw new AssertionError(e.getMessage());
		}
		return subsequentConnectionHandler;
	}

	private void disconnectInitialConnection() {
		when(connectionHandler.isConnected()).thenReturn(false);
		connectionEventListener.connectionBreak(connectionHandler, ConnectionEvents.CONNECTION_BREAK);
	}

	private void addOriginStateId(DiameterRequestBuilder requestBuilder, int id) {
		requestBuilder.addAVP(DiameterAVPConstants.ORIGIN_STATE_ID, String.valueOf(id));
	}

	private void addMandatoryAVPs(DiameterRequestBuilder requestBuilder) {
		requestBuilder.commandCode(CommandCode.CAPABILITIES_EXCHANGE)
		.addAVP(DiameterAVPConstants.PRODUCT_NAME, "Test Product")
		.addAVP(DiameterAVPConstants.VENDOR_ID, "1")
		.addAVP(DiameterAVPConstants.AUTH_APPLICATION_ID, "1");
	}

	private DiameterRequest createDWR() {
		return DiameterPacketBuilder.requestBuilder(peerData)
				.commandCode(CommandCode.DEVICE_WATCHDOG)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, "eliteaaa.com")
				.build();
	}

	private class DiameterPeerStub extends DiameterPeer {

		public DiameterPeerStub(PeerData peerData) {
			super(peerData, dummyStackContext, new DiameterRouter(dummyStackContext, Collections.<RoutingEntryData>emptyList()), new SessionFactoryManagerImpl(dummyStackContext), new DiameterAppMessageHandler(dummyStackContext), Mockito.mock(ExplicitRoutingHandler.class), new DuplicateDetectionHandler(dummyStackContext), timesource, new PeerApplicationProvider(peerData, new ApplicationProviderFactory(dummyStackContext)));
		}

		@Override
		public void sendDiameterRequest(DiameterRequest diameterRequest, ResponseListener listener)
				throws UnhandledTransitionException {
			LogManager.getLogger().info("DUMMY_DIAMETER_PEER", "Sent diameter request to " + getHostIdentity()
			+ " -> " + diameterRequest);
		}

		@Override
		public void sendDiameterAnswer(DiameterAnswer diameterAnswer) throws UnhandledTransitionException {
			LogManager.getLogger().info("DUMMY_DIAMETER_PEER", "Sent diameter answer to " + getHostIdentity()
			+ " -> " + diameterAnswer);
		}
	}
}