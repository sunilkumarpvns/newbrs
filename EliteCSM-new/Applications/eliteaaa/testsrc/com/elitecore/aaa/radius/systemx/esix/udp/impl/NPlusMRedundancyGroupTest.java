package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.RedundancyMode;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author soniya
 * 
 */
@RunWith(HierarchicalContextRunner.class)
public class NPlusMRedundancyGroupTest extends RadiusESIGroupTestSupport {

	private RadiusEsiGroupData radiusGroupData;
	private RadiusEsiGroup radiusESIGroupImpl;
	private RadiusPacket authPacket;

	private ISession session1;
	private ISession session2;

	private DefaultExternalSystemData primaryEsi1;
	private DefaultExternalSystemData primaryEsi2;
	private DefaultExternalSystemData failOverEsi1;
	private DefaultExternalSystemData failOverEsi2;

	private UDPCommunicatorSpy primaryEsi1Communicator;
	private UDPCommunicatorSpy primaryEsi2Communicator;

	private UDPCommunicatorSpy failOverEsi1Communicator;
	private UDPCommunicatorSpy failOverEsi2Communicator;
	
	private String activeEsiParmeter;
	private String standByEsiParameter;

	@Mock SessionsFactory radiusSessionFactory;
	
	@Before
	public void setUp() throws InvalidURLException, InitializationFailedException {
		super.setUp();

		session1 = new HazelcastRadiusSession(SESSION_ID_1, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());
		session2 = new HazelcastRadiusSession(SESSION_ID_2, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());

		userListener = spy(new AnsweringListener());

		authPacket = new RadiusPacket();
		authPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);

		primaryEsi1 = createESI().setEsiName("primaryEsi1").
				setUUID("primaryEsi1").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		primaryEsi2 = createESI().setEsiName("primaryEsi2").
				setUUID("primaryEsi2").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		failOverEsi1 = createESI().setEsiName("failOverEsi1").
				setUUID("failOverEsi1").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		failOverEsi2 = createESI().setEsiName("failOverEsi2").
				setUUID("failOverEsi2").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		primaryEsi1Communicator = getCommunicator(primaryEsi1);
		primaryEsi2Communicator = getCommunicator(primaryEsi2);
		failOverEsi1Communicator = getCommunicator(failOverEsi1);
		failOverEsi2Communicator = getCommunicator(failOverEsi2);

	}

	public class StickySessionEnable {

		public class ForInitialRequest {
			
			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						isSwitchBackEnable(false).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsForwardedToAliveESIBasedOnLoadBalancingConfiguration() {

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void onRequestTimeoutFromEsiOfPrimaryEsiGroupItWillSendToEsiOfFailOverEsiGroupAsPerLoadFactor() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();
				
				failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}
			
			@Test
			public void requestIsTimedOutOnTimeOutFromPrimaryEsiAndAllFailOverEsiAreDeadMarked() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);
				
				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();
				
				verifyRequestTimeOut();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestWillTimeOutOnGetsTimeOutFromEsiOfBothPrimaryEsiGroupAndSecondaryEsiGroup() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
			}

			@Test
			public void entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromPrimaryEsiGroup() {
				primaryEsi1Communicator.markDead();
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2Communicator.verifyRequestReceipt();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi2Communicator.getCommunicator().getName()));

			}

			@Test
			public void entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromFailOverEsiGroup() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				failOverEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi2Communicator.verifyRequestReceipt();

				failOverEsi1Communicator.verifyRequestNotReceived();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi2Communicator.getCommunicator().getName()));
				
			}

			@Test
			public void ifAllEsiOfPrimaryEsiGroupAreDeadThenRequestIsSendToEsiOfFailOverGroupAsPerLoadFactor() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt();
			}

			@Test
			public void communicatorWillAddedBackIntoLoadBalancerWhenItGetsAlive_ForEsiFromPrimaryEsiGroup() {
				entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromPrimaryEsiGroup();

				primaryEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				primaryEsi1Communicator.verifyRequestReceipt();

			}

			@Test
			public void communicatorWillAddedBackIntoLoadBalancerWhenItGetsAlive_ForEsiFromFailOverEsiGroup() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);
				
				failOverEsi1Communicator.markDead();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();
				
				failOverEsi2Communicator.verifyRequestReceipt();
				
				failOverEsi1Communicator.verifyRequestNotReceived();
				
				// when failOverEsi1Communicator gets alive

				failOverEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt();
			}

			@Test
			public void requestIsDroppedIfNoAliveCommunicatorIsFound() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();


				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verifyUserListenerRequestDroppedEvent();

			}

			@Test
			public void groupWillBeDeadMarkIfNoAliveCommunicatorFoundEitherFromPrimaryOrFromFailOverEsiGroup() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();

				assertThat(radiusESIGroupImpl.isAlive(), equalTo(false));;
			}
			
			@Test
			public void groupIsAliveEvenWhenAllPrimaryEsiAreDeadAndOnlyOneFailOverEsiIsAlive() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markAlive();

				assertThat(radiusESIGroupImpl.isAlive(), equalTo(true));;
			}
			
			@Test
			public void groupIsAliveEvenWhenAllFailOverEsiAreDeadAndOnlyOnePrimaryEsiIsAlive() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markAlive();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();

				assertThat(radiusESIGroupImpl.isAlive(), equalTo(true));;
			}

		}

		public class ForwardingSubsequentRequestWhenSwitchBackDisable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						isSwitchBackEnable(false).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsSendToStatefulEsiWhichBelongsToPrimaryEsiGroup() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void requestIsLoadBalancedToFailOverGroupIfStatefulEsiBelongsToPrimaryGroupIsDead() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				primaryEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void onRequestTimeoutFromStatefulEsiWhichBelongsToPrimaryEsiGroupItWillSendToEsiOfFailOverEsiGroupAsPerLoadFactor() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void sendTimeOutIfRequestIsTimeOutFromStatefulEsiWhichBelongsToPrimaryGroupAndFailOverGroup() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
				
				// it is known behavior that on request time-out, session parameter for stateful communication is not updated
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void ifInitialRequestIsServedByEsiFromFailOverGroupSubsquentRequestIsServedBySameEsiEvenIfPrimaryEsiIsAlive() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackDisable();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt(2);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsDroppedOnTimedOutFromStatefulEsiWhichBelongsToFailOverGroup() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackDisable();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();
				
				verifyUserListenerRequestTimeOutEvent();

				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void requestIsDroppedIfStatefulEsiWhichBelongsToFailOverGroupIsDead() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackDisable();

				failOverEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt(1);

				verifyUserListenerRequestDroppedEvent();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));

			}

		}

		public class ForwardingSubsequentRequestWhenSwitchBackEnable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						isSwitchBackEnable(true).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsSendToStatefulEsiWhichBelongsToPrimaryEsiGroup() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				verifyUserListenerResponseReceivedEvent(2);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));

			}

			@Test
			public void onRequestTimeoutFromStatefulEsiWhichBelongsToPrimaryEsiGroupItWillSendToEsiOfFailOverEsiGroupAsPerLoadFactor() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
				
				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsLoadBalancedToFailOverGroupIfStatefulEsiBelongsToPrimaryGroupIsDead() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				primaryEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt(1);
				
				failOverEsi1Communicator.verifyRequestReceipt();
				
				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));

			}
			
			@Test
			public void requestIsLoadBalancedToFailOverGroupIfStatefulEsiBelongsToPrimaryGroupIsDeadAndSendToPrimaryEsiWhenGetsAlive() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				primaryEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt();

				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
				
				primaryEsi1Communicator.markAlive();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				primaryEsi1Communicator.verifyRequestReceipt(2);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
				assertNull(session1.getParameter(standByEsiParameter));

			}

			@Test
			public void sendTimeOutIfRequestIsTimeOutFromStatefulEsiWhichBelongsToPrimaryGroupAndFailOverGroup() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void ifInitialRequestIsServedByEsiFromFailOverGroupAsTimeOutFromPrimaryGroupEsiThenSubsquentRequestIsServedByPrimaryEsiIfItGetsAlive() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackEnable();
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt(2).sendsAccessAccept();

				failOverEsi1Communicator.verifyRequestReceipt(1);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
				assertNull(session1.getParameter(standByEsiParameter));
			}

			@Test
			public void ifStatefulPrimaryEsiIsDeadThenRequestIsServedByStatefulFailOverEsi() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackEnable();
				
				primaryEsi1Communicator.markDead();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt(1).sendsAccessAccept();

				failOverEsi1Communicator.verifyRequestReceipt(2);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
			}

			@Test
			public void requestIsDroppedIfStatefulEsiWhichBelongsToFailOverGroupAndPrimaryEsiAreDead() {
				initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackEnable();

				primaryEsi1Communicator.markDead();
				failOverEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt(1);
				
				assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
				assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
				
				verifyUserListenerRequestDroppedEvent();
			}
		}
	}

	public class StickySessionDisable {

		public class ForInitialRequest {
			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(false).
						isSwitchBackEnable(false).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsForwardedToAliveESIBasedOnLoadBalancingConfiguration() {

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
			}

			@Test
			public void onRequestTimeoutFromEsiOfPrimaryEsiGroupItWillSendToEsiOfFailOverEsiGroupAsPerLoadFactor() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
			}

			@Test
			public void requestWillTimeOutGetsTimeOutFromEsiOfBothPrimaryEsiGroupAndSecondaryEsiGroup() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt();

				primaryEsi1Communicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt();

				failOverEsi1Communicator.doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
			}

			@Test
			public void entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromPrimaryEsiGroup() {
				primaryEsi1Communicator.markDead();
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2Communicator.verifyRequestReceipt(1);

			}

			@Test
			public void entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromFailOverEsiGroup() {
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				failOverEsi1Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi2Communicator.verifyRequestReceipt(1);

				failOverEsi1Communicator.verifyRequestNotReceived();
			}

			@Test
			public void ifAllEsiOfPrimaryEsiGroupAreDeadThenRequestIsSendToEsiOfFailOverGroupAsPerLoadFactor() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1Communicator.verifyRequestReceipt();
			}

			@Test
			public void communicatorWillAddedBackIntoLoadBalancerWhenItGetsAlive_ForEsiFromPrimaryEsiGroup() {
				entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromPrimaryEsiGroup();

				primaryEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				primaryEsi1Communicator.verifyRequestReceipt();

			}

			@Test
			public void communicatorWillAddedBackIntoLoadBalancerWhenItGetsAlive_ForEsiFromFailOverEsiGroup() {
				entryOfEsiWillBeRemovedFromLoadBalancerIfItIsDeadMarked_ForEsiFromFailOverEsiGroup();

				failOverEsi1Communicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1Communicator.verifyRequestReceipt();

			}

			@Test
			public void requestIsDroppedIfNoAliveCommunicatorIsFound() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();


				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verify(userListener).requestDropped(Mockito.any(RadUDPRequest.class));

			}

			@Test
			public void groupWillBeDeadMarkIfNoAliveCommunicatorFoundEitherFromPrimaryOrFromFailOverEsiGroup() {
				primaryEsi1Communicator.markDead();
				primaryEsi2Communicator.markDead();

				failOverEsi1Communicator.markDead();
				failOverEsi2Communicator.markDead();

				assertThat(radiusESIGroupImpl.isAlive(), equalTo(false));
			}

		}

		public class ForwardingSubsequentRequestWhenSwitchBackDisable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(false).
						isSwitchBackEnable(false).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsForwardedToAliveESIBasedOnLoadBalancingConfiguration() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2Communicator.verifyRequestReceipt(1).sendsAccessAccept();

				verifyUserListenerResponseReceivedEvent(2);

			}

		}

		public class ForwardingSubsequentRequestWhenSwitchBackEnable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(false).
						isSwitchBackEnable(true).
						esiType(EsiType.AUTH.typeName).
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOverEsi2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void requestIsForwardedToAliveESIBasedOnLoadBalancingConfiguration() {
				initialRequestServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2Communicator.verifyRequestReceipt(1);

				Assert.assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi2Communicator.getCommunicator().getName()));

			}

		}
	}

	private RadiusEsiGroup createRadiusEsiGroup(RadiusEsiGroupData radiusGroupData) throws InitializationFailedException, InvalidURLException {

		when(radESConfiguration.getESDataByName("primaryEsi1")).thenReturn(Optional.of(primaryEsi1));
		when(radESConfiguration.getESDataByName("primaryEsi2")).thenReturn(Optional.of(primaryEsi2));
		when(radESConfiguration.getESDataByName("failOverEsi1")).thenReturn(Optional.of(failOverEsi1));
		when(radESConfiguration.getESDataByName("failOverEsi2")).thenReturn(Optional.of(failOverEsi2));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi1.getUUID(),
				dummyServerContext, primaryEsi1)).thenReturn(primaryEsi1Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi2.getUUID(),
				dummyServerContext, primaryEsi2)).thenReturn(primaryEsi2Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi1.getUUID(),
				dummyServerContext, failOverEsi1)).thenReturn(failOverEsi1Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi2.getUUID(),
				dummyServerContext, failOverEsi2)).thenReturn(failOverEsi2Communicator.getCommunicator());
		
		this.activeEsiParmeter = radiusGroupData.getName() + "-active esi";
		this.standByEsiParameter = radiusGroupData.getName() + "-stand by esi";

		radiusESIGroupImpl = new RadiusESIGroupFactory().getOrCreateGroupInstance(dummyServerContext, radiusGroupData);

		return radiusESIGroupImpl;
	}

	private void initialRequestServedByEsiFromPrimaryEsiGroup() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

		primaryEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

		assertThat(session1.getParameter(activeEsiParmeter), equalTo(primaryEsi1Communicator.getCommunicator().getName()));
	}

	private void initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackDisable() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

		primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

		failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
		
		assertThat(session1.getParameter(activeEsiParmeter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
	}
	
	private void initalRequestServedByEsiFromFailOverEsiGroupWhenSwithBackEnable() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

		primaryEsi1Communicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

		failOverEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
		
		assertThat(session1.getParameter(standByEsiParameter), equalTo(failOverEsi1Communicator.getCommunicator().getName()));
		
	}

}
