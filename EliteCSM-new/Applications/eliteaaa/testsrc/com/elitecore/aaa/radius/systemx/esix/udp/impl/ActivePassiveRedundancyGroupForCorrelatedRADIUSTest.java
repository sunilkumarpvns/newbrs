package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusData;
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
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ActivePassiveRedundancyGroupForCorrelatedRADIUSTest extends RadiusESIGroupTestSupport {

	private RadiusPacket authPacket;
	private RadiusPacket acctPacket;

	private DefaultExternalSystemData activeEsi1Auth;
	private DefaultExternalSystemData activeEsi1Acct;
	private DefaultExternalSystemData passiveEsi1Auth;
	private DefaultExternalSystemData passiveEsi1Acct;

	private DefaultExternalSystemData activeEsi2Auth;
	private DefaultExternalSystemData activeEsi2Acct;
	private DefaultExternalSystemData passiveEsi2Auth;
	private DefaultExternalSystemData passiveEsi2Acct;

	private UDPCommunicatorSpy activeEsi1AuthCommunicator;
	private UDPCommunicatorSpy activeEsi1AcctCommunicator;
	private UDPCommunicatorSpy passiveEsi1AuthCommunicator;
	private UDPCommunicatorSpy passiveEsi1AcctCommunicator;

	private UDPCommunicatorSpy activeEsi2AuthCommunicator;
	private UDPCommunicatorSpy activeEsi2AcctCommunicator;
	private UDPCommunicatorSpy passiveEsi2AuthCommunicator;
	private UDPCommunicatorSpy passiveEsi2AcctCommunicator;

	private RadiusEsiGroupData radiusGroupData;
	private RadiusEsiGroup radiusESIGroupImpl;

	private ISession session1;
	private ISession session2;

	private String activeEsiParmeter;


	@Mock SessionsFactory radiusSessionFactory;

	@Before
	public void setUp() throws InvalidURLException, InitializationFailedException {
		super.setUp();

		session1 = new HazelcastRadiusSession(SESSION_ID_1, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());
		session2 = new HazelcastRadiusSession(SESSION_ID_2, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());

		userListener = spy(new AnsweringListener());

		authPacket = new RadiusPacket();
		authPacket.setPacketType(RadiusPacketTypeConstant.ACCESS_REQUEST.packetTypeId);

		acctPacket = new RadiusPacket();
		acctPacket.setPacketType(RadiusPacketTypeConstant.ACCOUNTING_REQUEST.packetTypeId);

		activeEsi1Auth = createESI().setEsiName("primaryEsi1Auth").
				setUUID("primaryEsi1Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		activeEsi1Acct = createESI().setEsiName("primaryEsi1Acct").
				setUUID("primaryEsi1Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		passiveEsi1Auth = createESI().setEsiName("failOverEsi1Auth").
				setUUID("failOverEsi1Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		passiveEsi1Acct = createESI().setEsiName("failOverEsi1Acct").
				setUUID("failOverEsi1Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		// Second communicator

		activeEsi2Auth = createESI().setEsiName("primaryEsi2Auth").
				setUUID("primaryEsi2Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		activeEsi2Acct = createESI().setEsiName("primaryEsi2Acct").
				setUUID("primaryEsi2Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		passiveEsi2Auth = createESI().setEsiName("failOverEsi2Auth").
				setUUID("failOverEsi2Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		passiveEsi2Acct = createESI().setEsiName("failOverEsi2Acct").
				setUUID("failOverEsi2Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		activeEsi1AuthCommunicator = getCommunicator(activeEsi1Auth);
		activeEsi1AcctCommunicator = getCommunicator(activeEsi1Acct);
		passiveEsi1AuthCommunicator = getCommunicator(passiveEsi1Auth);
		passiveEsi1AcctCommunicator = getCommunicator(passiveEsi1Acct);

		activeEsi2AuthCommunicator = getCommunicator(activeEsi2Auth);
		activeEsi2AcctCommunicator = getCommunicator(activeEsi2Acct);
		passiveEsi2AuthCommunicator = getCommunicator(passiveEsi2Auth);
		passiveEsi2AcctCommunicator = getCommunicator(passiveEsi2Acct);

		radiusGroupData = createESIGroupData().groupName("esi-group1").
				isStatefulEnable(true).
				esiType(EsiType.CORRELATED_RADIUS.typeName).
				redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
				isSwitchBackEnable(false).
				addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
				addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
				radiusGroupData;

		radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);

	}

	public class StickySessionEnable {

		/* written with respect to one authentication and one accounting request*/

		public class ForInitialRequest {

			@Test
			public void requestIsSendToEsiAsPerLoadBalancer() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session2);

				activeEsi2AcctCommunicator.verifyRequestReceipt();
			}

			@Test
			public void authenticationRequestWillBeForwardedViaAuthCommunicator() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(activeEsi1AuthCommunicator.getCommunicator().getName() + "-" + activeEsi1AcctCommunicator.getCommunicator().getName());
			}

			@Test
			public void accountingRequestWillBeForwardedViaAcctCommunicator() {
				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AcctCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(activeEsi1AuthCommunicator.getCommunicator().getName() + "-" + activeEsi1AcctCommunicator.getCommunicator().getName());
			}

			@Test
			public void anyOfAuthAcctEsiIsDeadThenThatCorrectedRadiusGetsDeadMarkedAndInitialRequestIsSendToItsPassiveEsi() {
				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt();

				activeEsi1AuthCommunicator.verifyRequestNotReceived();

			}
		}

		public class ForwardingSubsequentRequestWhenSwitchBackDisable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						esiType(EsiType.CORRELATED_RADIUS.typeName).
						redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
						isSwitchBackEnable(false).
						addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
						addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void authAndAcctRequestIsServedBySameEsiForSessionOfSameUser() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AcctCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void sendingAuthRequestToStatefulAuthEsiEvenIfStatefulAcctEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));

				activeEsi1AcctCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt(2);

				passiveEsi1AuthCommunicator.verifyRequestNotReceived();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void sendingAcctRequestToStatefulAcctEsiEvenIfStatefulAuthEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AcctCommunicator.verifyRequestReceipt();

				passiveEsi1AcctCommunicator.verifyRequestNotReceived();
			}

			@Test
			public void onRequestTimeoutFromStatefulActiveEsiRequestIsSentToItsPassiveEsi() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));
			}

			@Test
			public void requestIsServedByPassiveEsiIfStatefulActiveEsiIsDead() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));
			}

			@Test
			public void sendTimeOutIfGetsTimedOutFromStatefulActiveEsiAndItsPassiveEsi() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));
			}

			@Test
			public void requestIsDroppedIfStatefulActiveEsiAndItsPassiveEsiIsDead() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				activeEsi1AuthCommunicator.markDead();

				passiveEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verifyUserListenerRequestDroppedEvent();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void requestIsServedByPassiveEsiIfInitialRequestIsServedByItAsTimedOutFromActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				assertTrue(activeEsi1AuthCommunicator.isAlive());

				passiveEsi1AuthCommunicator.verifyRequestReceipt(2);

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));
			}
			
			@Test
			public void requestIsServedByPassiveEsiIfInitialRequestIsServedByItAsActiveEsiIsDeadMarked() {
				activeEsi1AuthCommunicator.markDead();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				activeEsi1AuthCommunicator.markAlive();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt(2).sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(passiveEsi1AuthCommunicator) + "-" + getName(passiveEsi1AcctCommunicator));
			}

			@Test
			public void requestIsTimedOutIfStatefulPassiveEsiIsNotResponding() {
				authRequestIsServedByPassiveEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
			}

			@Test
			public void requestIsDroppedIfStatefulPassiveEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				passiveEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt(1);

				verifyUserListenerRequestDroppedEvent();
			}

		}

		public class ForwardingSubsequentRequestWhenSwitchBackEnable {

			@Before
			public void setUp() throws InitializationFailedException, InvalidURLException {
				doReturn(true).when(dummyServerContext).hasRadiusSession(Mockito.anyString());
				doReturn(session1).when(dummyServerContext).getOrCreateRadiusSession(SESSION_ID_1);

				radiusGroupData = createESIGroupData().groupName("esi-group1").
						isStatefulEnable(true).
						esiType(EsiType.CORRELATED_RADIUS.typeName).
						isSwitchBackEnable(true).
						redundancyMode(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).
						addActivePassiveEsiAndLoadFactor("primaryEsi1", "failOverEsi1", LOAD_FACTOR_1).
						addActivePassiveEsiAndLoadFactor("primaryEsi2", "failOverEsi2", LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void authAndAcctRequestIsServedBySameEsiForSessionOfSameUser() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AcctCommunicator.verifyRequestReceipt(1).sendsAccessAccept();

				verifyUserListenerResponseReceivedEvent(2);
			}

			@Test
			public void sendingAuthRequestToStatefulAuthEsiEvenIfStatefulAcctEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));

				activeEsi1AcctCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt(2);

				passiveEsi1AuthCommunicator.verifyRequestNotReceived();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void sendingAcctRequestToStatefulAcctEsiEvenIfStatefulAuthEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AcctCommunicator.verifyRequestReceipt();

				passiveEsi1AcctCommunicator.verifyRequestNotReceived();
			}

			@Test
			public void onRequestTimeoutFromStatefulActiveEsiRequestIsSentToItsPassiveEsi() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void requestIsServedByPassiveEsiIfStatefulActiveEsiIsDead() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void sendTimeOutIfGetsTimedOutFromStatefulActiveEsiAndItsPassiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void requestIsSentToActiveEsiIfAliveEvenThoughInitialIsServedByPassiveEsiAsTimedOutFromActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				assertTrue(activeEsi1AuthCommunicator.isAlive());

				activeEsi1AuthCommunicator.verifyRequestReceipt(2);

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}

			@Test
			public void requestIsSentToActiveEsiIfAliveEvenThoughInitialIsServedByPassiveEsiAsActiveEsiIsDead() {
				activeEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				activeEsi1AuthCommunicator.verifyRequestNotReceived();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));

				activeEsi1AuthCommunicator.markAlive();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}
			
			@Test
			public void requestIsDroppedIfStatefulActiveEsiAndItsPassiveEsiIsDead() {
				authRequestIsServedByEsiFromActiveEsiGroup();

				activeEsi1AuthCommunicator.markDead();

				passiveEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				verifyUserListenerRequestDroppedEvent();

				verifyActiveEsiParameterSessionIs(getName(activeEsi1AuthCommunicator) + "-" + getName(activeEsi1AcctCommunicator));
			}
		}
	}
	
	public class StickySessionDisable {
		
		@Before
		public void setUp() throws InitializationFailedException, InvalidURLException {
			radiusGroupData = createESIGroupData().groupName("esi-group1").
					isStatefulEnable(false).
					esiType(EsiType.CORRELATED_RADIUS.typeName).
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

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				activeEsi2AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
			}

			@Test
			public void requestSendToPassiveEsiWhenGetsTimedOutFromActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi1AuthCommunicator.verifyRequestReceipt();
			}
		}

		public class ForwardingSubsequentRequest {

			@Test
			public void subSequentRequestSendToEsiBasedOnLoadFactor() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2AcctCommunicator.verifyRequestReceipt(1).sendsAccessAccept();
			}

			@Test
			public void toPassiveEsiWhenGetsTimedOutFromActiveEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				activeEsi2AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				passiveEsi2AuthCommunicator.verifyRequestReceipt();
			}

		}
		
	}

	private RadiusEsiGroup createRadiusEsiGroup(RadiusEsiGroupData radiusGroupData) throws InitializationFailedException, InvalidURLException {

		CorrelatedRadiusData primaryData1 = new CorrelatedRadiusData();
		primaryData1.setName("primaryEsi1");
		primaryData1.setAuthEsiName("primaryEsi1Auth");
		primaryData1.setAcctEsiName("primaryEsi1Acct");

		CorrelatedRadiusData failOverData1 = new CorrelatedRadiusData();
		failOverData1.setName("failOverEsi1");
		failOverData1.setAuthEsiName("failOverEsi1Auth");
		failOverData1.setAcctEsiName("failOverEsi1Acct");

		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("primaryEsi1")).thenReturn(primaryData1);
		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("failOverEsi1")).thenReturn(failOverData1);

		when(radESConfiguration.getESDataByName("primaryEsi1Auth")).thenReturn(Optional.of(activeEsi1Auth));
		when(radESConfiguration.getESDataByName("primaryEsi1Acct")).thenReturn(Optional.of(activeEsi1Acct));
		when(radESConfiguration.getESDataByName("failOverEsi1Auth")).thenReturn(Optional.of(passiveEsi1Auth));
		when(radESConfiguration.getESDataByName("failOverEsi1Acct")).thenReturn(Optional.of(passiveEsi1Acct));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi1Auth.getUUID(),
				dummyServerContext, activeEsi1Auth)).thenReturn(activeEsi1AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi1Acct.getUUID(),
				dummyServerContext, activeEsi1Acct)).thenReturn(activeEsi1AcctCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi1Auth.getUUID(),
				dummyServerContext, passiveEsi1Auth)).thenReturn(passiveEsi1AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi1Acct.getUUID(),
				dummyServerContext, passiveEsi1Acct)).thenReturn(passiveEsi1AcctCommunicator.getCommunicator());

		// 2nd communicator

		CorrelatedRadiusData primaryData2 = new CorrelatedRadiusData();
		primaryData2.setName("primaryEsi2");
		primaryData2.setAuthEsiName("primaryEsi2Auth");
		primaryData2.setAcctEsiName("primaryEsi2Acct");

		CorrelatedRadiusData failOverData2 = new CorrelatedRadiusData();
		failOverData2.setName("failOverEsi2");
		failOverData2.setAuthEsiName("failOverEsi2Auth");
		failOverData2.setAcctEsiName("failOverEsi2Acct");

		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("primaryEsi2")).thenReturn(primaryData2);
		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("failOverEsi2")).thenReturn(failOverData2);

		when(radESConfiguration.getESDataByName("primaryEsi2Auth")).thenReturn(Optional.of(activeEsi2Auth));
		when(radESConfiguration.getESDataByName("primaryEsi2Acct")).thenReturn(Optional.of(activeEsi2Acct));
		when(radESConfiguration.getESDataByName("failOverEsi2Auth")).thenReturn(Optional.of(passiveEsi2Auth));
		when(radESConfiguration.getESDataByName("failOverEsi2Acct")).thenReturn(Optional.of(passiveEsi2Acct));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi2Auth.getUUID(),
				dummyServerContext, activeEsi2Auth)).thenReturn(activeEsi2AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(activeEsi2Acct.getUUID(),
				dummyServerContext, activeEsi2Acct)).thenReturn(activeEsi2AcctCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi2Auth.getUUID(),
				dummyServerContext, passiveEsi2Auth)).thenReturn(passiveEsi2AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(passiveEsi2Acct.getUUID(),
				dummyServerContext, passiveEsi2Acct)).thenReturn(passiveEsi2AcctCommunicator.getCommunicator());

		this.activeEsiParmeter = radiusGroupData.getName() + "-active esi";

		radiusESIGroupImpl = new RadiusESIGroupFactory().getOrCreateGroupInstance(dummyServerContext, radiusGroupData);

		return radiusESIGroupImpl;
	}

	private void authRequestIsServedByPassiveEsiGroup() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

		activeEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

		passiveEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
	}

	private void authRequestIsServedByEsiFromActiveEsiGroup() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

		activeEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

	}

	private void verifyActiveEsiParameterSessionIs(String esiName) {
		assertThat(session1.getParameter(activeEsiParmeter), equalTo(esiName));
	}
}