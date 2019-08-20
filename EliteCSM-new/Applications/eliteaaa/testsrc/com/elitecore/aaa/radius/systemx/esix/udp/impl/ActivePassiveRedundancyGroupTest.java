package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.RedundancyMode;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ActivePassiveRedundancyGroupTest extends RadiusESIGroupTestSupport {

	private RadiusEsiGroupData radiusGroupData;
	private RadiusEsiGroup radiusESIGroupImpl;
	private RadiusPacket authPacket;

	private ISession session1;
	private ISession session2;

	private DefaultExternalSystemData activeEsi1;
	private DefaultExternalSystemData activeEsi2;
	private DefaultExternalSystemData passiveEsi1;
	private DefaultExternalSystemData passiveEsi2;

	private UDPCommunicatorSpy activeEsi1Communicator;
	private UDPCommunicatorSpy activeEsi2Communicator;

	private UDPCommunicatorSpy passiveEsi1Communicator;
	private UDPCommunicatorSpy passiveEsi2Communicator;

	private String activeEsiParmeter;
	
	@Mock SessionsFactory radiusSessionFactory;
	@Rule public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws InvalidURLException, InitializationFailedException {
		super.setUp();

		session1 = new HazelcastRadiusSession(SESSION_ID_1, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());
		session2 = new HazelcastRadiusSession(SESSION_ID_2, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());

		userListener = spy(new AnsweringListener());

		authPacket = new RadiusPacket();
		authPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);

		activeEsi1 = createESI().setEsiName("primaryEsi1").
				setUUID("primaryEsi1").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		activeEsi2 = createESI().setEsiName("primaryEsi2").
				setUUID("primaryEsi2").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		passiveEsi1 = createESI().setEsiName("failOverEsi1").
				setUUID("failOverEsi1").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		passiveEsi2 = createESI().setEsiName("failOverEsi2").
				setUUID("failOverEsi2").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		activeEsi1Communicator = getCommunicator(activeEsi1);
		activeEsi2Communicator = getCommunicator(activeEsi2);
		passiveEsi1Communicator = getCommunicator(passiveEsi1);
		passiveEsi2Communicator = getCommunicator(passiveEsi2);
	}

	@Ignore
	public void throwsIntializationFailedExceptionIfNumberOfActiveAndPassiveEsiAreNotEqual() throws InitializationFailedException, InvalidURLException {
		radiusGroupData = createESIGroupData().groupName("esi-group1").
				redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
				isStatefulEnable(true).
				esiType(EsiType.AUTH.typeName).
				isSwitchBackEnable(false).
				addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
				addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
				addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
				radiusGroupData;

		exception.expect(InitializationFailedException.class);
		exception.expectMessage("Invalid configuration in Radius ESI Group: " + radiusGroupData.getName());

		radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
	}

	public class StickySessionEnable {

		@Before
		public void setUp() throws InitializationFailedException, InvalidURLException {

			radiusGroupData = createESIGroupData().groupName("esi-group1").
					isStatefulEnable(true).
					redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
					isSwitchBackEnable(false).
					esiType(EsiType.AUTH.typeName).
					addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
					addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
					radiusGroupData;
			radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
		}

		public class ForInitialRequest {

			@Test
			public void requestIsForwardedToAliveEsiBasedOnLoadFactor() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void requestWillBeSentToPassiveEsiWhenGetsTimeOutFromActiveEsi() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsTimedOutOnTimedOutFromActiveEsiAndPassiveEsiIsDead() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				passiveEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestNotReceived();

				verifyUserListenerRequestTimeOutEvent();

				// it is known behavior
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void sendTimeOutIfGetsTimeOutFromBothActiveAndPassiveEsi() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				// it is known behavior
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void entryOfEsiRemainsInLoadBalancerTillAnyOfActiveOrPassiveEsiIsAliveAndSendRequestToThatAliveEsi() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);
				assertTrue(passiveEsi1Communicator.isAlive());

				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void entryOfEsiWillBeRemovedFromLoadBalancerIfAndOnlyIfBothActiveAndPassiveCommunicatorsAreDead() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				activeEsi1Communicator.markDead();
				passiveEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi2Communicator.getCommunicator().getName()));
			}

			@Test
			public void anyOfActiveOrPassiveEsiGetsAliveThatEntryWillBeAddedBackIntoLoadBalancer() {
				passiveEsi1Communicator.markDead();
				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi2Communicator.getCommunicator().getName()));

				passiveEsi1Communicator.markAlive();
				activeEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				activeEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session2.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsDroppedIfNoAliveCommunicatorIsFound() {
				activeEsi1Communicator.markDead();
				activeEsi2Communicator.markDead();

				passiveEsi1Communicator.markDead();
				passiveEsi2Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verifyUserListenerRequestDroppedEvent();
			}

			@Test
			public void groupWillBeDeadMarkIfNoAliveCommunicatorFound() {
				activeEsi1Communicator.markDead();
				activeEsi2Communicator.markDead();

				passiveEsi1Communicator.markDead();
				passiveEsi2Communicator.markDead();

				assertThat(radiusESIGroupImpl.isAlive(), equalTo(false));
			}
		}


		public class ForwardingSubsequentRequestWhenSwitchBackDisable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
						isSwitchBackEnable(false).
						addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
						addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsSendToStatefulActiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void onGetsTimeoutFromStatefulActiveEsiItWillSendToItsPassiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void sendToPassiveEsiWhenStatefulActiveEsiIsDead() {
				initialRequestServedByActiveEsi();

				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void ifSubsequenceRequestIsServedByPassiveEsiAsStatefulActiveEsiIsDeadThenStatefulnessIsMaintainedWithPassiveEsiEvenIfActiveComesAlive() {
				initialRequestServedByActiveEsi();

				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(1).sendsAccessAccept();

				activeEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void ifSubsequenceRequestIsServedByPassiveEsiAsTimedOutFromActiveEsiThenStatefulnessIsMaintainedWithPassiveEsiEvenIfActiveComesAlive() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void sendTimeOutIfRequestGetsTimeOutFromStatefulActiveEsiAndPassiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void initialRequestIsServedByPassiveEsiAsTimedOutFromActiveEsiThenSubsquentRequestIsServedBySameEsi() {
				initalRequestServedByEsiFromFailOverEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void initialRequestIsServedByPassiveEsiAsActiveEsiIsDeadMarkedThenSubsquentRequestIsServedBySameEsiEvenActiveEsiGetsAlive() {
				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(1).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));

				activeEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void sendTimeOutIfGetsTimeOutFromStatefulPassiveEsi() {
				initalRequestServedByEsiFromFailOverEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsDroppedIfStatefulPassiveEsiIsDead() {
				initalRequestServedByEsiFromFailOverEsiGroup();

				passiveEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt(1);

				verifyUserListenerRequestDroppedEvent();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));

			}

			private void initialRequestServedByActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			private void initalRequestServedByEsiFromFailOverEsiGroup() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(passiveEsi1Communicator.getCommunicator().getName()));
			}
		}

		public class ForwardingSubsequentRequestWhenSwitchBackEnable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
						isSwitchBackEnable(true).
						addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
						addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsSendToStatefulActiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void onGetsTimeoutFromStatefulActiveEsiItWillSendToItsPassiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void sendTimeOutIfGetsTimeOutFromStatefulActiveEsiAndItsPassiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestSendToPassiveEsiIfStatefulActiveEsiIsDead() {
				initialRequestServedByActiveEsi();

				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}


			// 
			@Test
			public void ifInitialRequestIsServedByPassiveEsiAsTimedOutFromActiveEsiThenSubsquentRequestIsServedByActiveEsiIfItIsAlive() {
				initalRequestServedByPassiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();
			}

			@Test
			public void ifInitialRequestIsServedByPassiveEsiAsActiveEsiIsDeadThenSubsquentRequestIsServedByActiveEsiIfItGetsAlive() {
				activeEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

				activeEsi1Communicator.verifyRequestNotReceived();

				activeEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt(1).sendsAccessAccept();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(activeEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsDroppedIfStatefulPassiveAndActiveEsiAreDead() {
				initalRequestServedByPassiveEsi();

				activeEsi1Communicator.markDead();
				passiveEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verifyUserListenerRequestDroppedEvent();
			}
		}
	}

	public class StickySessionDisable {

		@Before
		public void setUp() throws InitializationFailedException, InvalidURLException {

			radiusGroupData = createESIGroupData().groupName("esi-group1").
					isStatefulEnable(false).
					redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
					isSwitchBackEnable(false).
					addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
					addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
					radiusGroupData;
			radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
		}

		public class ForInitialRequest {

			@Test
			public void requestIsForwardedToAliveEsiBasedOnLoadFactor() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
			}

			@Test
			public void requestSendToPassiveEsiWhenGetsTimedOutFromActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1Communicator.verifyRequestReceipt();
			}
		}

		public class ForwardingSubsequentRequest {

			@Test
			public void subSequentRequestSendToEsiBasedOnLoadFactor() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2Communicator.verifyRequestReceipt(1).sendsAccessAccept();
			}

			@Test
			public void toPassiveEsiWhenGetsTimedOutFromActiveEsi() {
				initialRequestServedByActiveEsi();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi2Communicator.verifyRequestReceipt();
			}

		}

	}

	private void initialRequestServedByActiveEsi() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

		activeEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
	}

	private void initalRequestServedByPassiveEsi() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

		activeEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

		passiveEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
	}

	private RadiusEsiGroup createRadiusEsiGroup(RadiusEsiGroupData radiusGroupData) throws InitializationFailedException, InvalidURLException {

		when(radESConfiguration.getESDataByName("primaryEsi1")).thenReturn(Optional.of(activeEsi1));
		when(radESConfiguration.getESDataByName("primaryEsi2")).thenReturn(Optional.of(activeEsi2));
		when(radESConfiguration.getESDataByName("failOverEsi1")).thenReturn(Optional.of(passiveEsi1));
		when(radESConfiguration.getESDataByName("failOverEsi2")).thenReturn(Optional.of(passiveEsi2));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi1.getUUID(),
				dummyServerContext, activeEsi1)).thenReturn(activeEsi1Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi2.getUUID(),
				dummyServerContext, activeEsi2)).thenReturn(activeEsi2Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi1.getUUID(),
				dummyServerContext, passiveEsi1)).thenReturn(passiveEsi1Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi2.getUUID(),
				dummyServerContext, passiveEsi2)).thenReturn(passiveEsi2Communicator.getCommunicator());

		this.activeEsiParmeter = radiusGroupData.getName() + "-active esi";

		radiusESIGroupImpl = new RadiusESIGroupFactory().getOrCreateGroupInstance(dummyServerContext, radiusGroupData);

		return radiusESIGroupImpl;
	}
}
