
package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
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
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NPlusMRedundancyGroupForCorrelatedRADIUSTest extends RadiusESIGroupTestSupport {

	private RadiusPacket authPacket, acctPacket;

	private DefaultExternalSystemData primaryEsi1Auth;
	private DefaultExternalSystemData primaryEsi1Acct;
	private DefaultExternalSystemData failOverEsi1Auth;
	private DefaultExternalSystemData failOverEsi1Acct;

	private DefaultExternalSystemData primaryEsi2Auth;
	private DefaultExternalSystemData primaryEsi2Acct;
	private DefaultExternalSystemData failOverEsi2Auth;
	private DefaultExternalSystemData failOverEsi2Acct;

	private UDPCommunicatorSpy primaryEsi1AuthCommunicator;
	private UDPCommunicatorSpy primaryEsi1AcctCommunicator;
	private UDPCommunicatorSpy failOverEsi1AuthCommunicator;
	private UDPCommunicatorSpy failOverEsi1AcctCommunicator;

	private UDPCommunicatorSpy primaryEsi2AuthCommunicator;
	private UDPCommunicatorSpy primaryEsi2AcctCommunicator;
	private UDPCommunicatorSpy failOverEsi2AuthCommunicator;
	private UDPCommunicatorSpy failOverEsi2AcctCommunicator;

	private RadiusEsiGroupData radiusGroupData;
	private RadiusEsiGroup radiusESIGroupImpl;

	private ISession session1, session2;
	private String activeEsiParmeter, standByEsiParameter;

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

		primaryEsi1Auth = createESI().setEsiName("primaryEsi1Auth").
				setUUID("primaryEsi1Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		primaryEsi1Acct = createESI().setEsiName("primaryEsi1Acct").
				setUUID("primaryEsi1Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		failOverEsi1Auth = createESI().setEsiName("failOverEsi1Auth").
				setUUID("failOverEsi1Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		failOverEsi1Acct = createESI().setEsiName("failOverEsi1Acct").
				setUUID("failOverEsi1Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		// Second communicator

		primaryEsi2Auth = createESI().setEsiName("primaryEsi2Auth").
				setUUID("primaryEsi2Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		primaryEsi2Acct = createESI().setEsiName("primaryEsi2Acct").
				setUUID("primaryEsi2Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:1813").
				getEsiData();

		failOverEsi2Auth = createESI().setEsiName("failOverEsi2Auth").
				setUUID("failOverEsi2Auth").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		failOverEsi2Acct = createESI().setEsiName("failOverEsi2Acct").
				setUUID("failOverEsi2Acct").
				setESIType(RadESTypeConstants.RAD_ACCT_PROXY.type).
				setStringIpAddress("127.0.0.1:2813").
				getEsiData();

		primaryEsi1AuthCommunicator = getCommunicator(primaryEsi1Auth);
		primaryEsi1AcctCommunicator = getCommunicator(primaryEsi1Acct);
		failOverEsi1AuthCommunicator = getCommunicator(failOverEsi1Auth);
		failOverEsi1AcctCommunicator = getCommunicator(failOverEsi1Acct);

		primaryEsi2AuthCommunicator = getCommunicator(primaryEsi2Auth);
		primaryEsi2AcctCommunicator = getCommunicator(primaryEsi2Acct);
		failOverEsi2AuthCommunicator = getCommunicator(failOverEsi2Auth);
		failOverEsi2AcctCommunicator = getCommunicator(failOverEsi2Acct);

		radiusGroupData = createESIGroupData().groupName("esi-group1").
				isStatefulEnable(true).
				esiType(EsiType.CORRELATED_RADIUS.typeName).
				redundancyMode(RedundancyMode.NM.redundancyModeName).
				isSwitchBackEnable(false).
				addPrimaryEsiIdWithLoadFactor("primary1",LOAD_FACTOR_1).
				addPrimaryEsiIdWithLoadFactor("primary2",LOAD_FACTOR_1).
				addFailOverEsiIdWithLoadFactor("failOver1",LOAD_FACTOR_1).
				addFailOverEsiIdWithLoadFactor("failOver2",LOAD_FACTOR_1).
				radiusGroupData;

		radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);

	}

	public class StickySessionEnable {

		/* written with respect to one authentication and one accounting request*/

		public class ForInitialRequest {

			@Test
			public void requestIsSendToEsiAsPerLoadBalancer() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session2);

				primaryEsi2AcctCommunicator.verifyRequestReceipt();
			}

			@Test
			public void authenticationRequestWillBeForwardedViaAuthCommunicator() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(primaryEsi1AuthCommunicator.getCommunicator().getName() + "-" + primaryEsi1AcctCommunicator.getCommunicator().getName());
			}

			@Test
			public void accountingRequestWillBeForwardedViaAcctCommunicator() {
				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AcctCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(primaryEsi1AuthCommunicator.getCommunicator().getName() + "-" + primaryEsi1AcctCommunicator.getCommunicator().getName());
			}

			@Test
			public void anyOfAuthAcctEsiIsDeadIsRemovedFromLoadBalancerAndInitialRequestIsSendToAnotherEsi() {
				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi2AuthCommunicator.verifyRequestReceipt();

				primaryEsi1AuthCommunicator.verifyRequestNotReceived();
				
				verifyActiveEsiParameterSessionIs(getName(primaryEsi2AuthCommunicator) + "-" + getName(primaryEsi2AcctCommunicator));
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
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						isSwitchBackEnable(false).
						addPrimaryEsiIdWithLoadFactor("primary1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primary2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOver1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOver2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void authAndAcctRequestIsServedBySameEsiForSessionOfSameUser() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AcctCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
			}

			@Test
			public void sendingAuthRequestToStatefulAuthEsiEvenIfStatefulAcctEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));

				primaryEsi1AcctCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt(2);

				failOverEsi1AuthCommunicator.verifyRequestNotReceived();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
			}

			@Test
			public void sendingAcctRequestToStatefulAcctEsiEvenIfStatefulAuthEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AcctCommunicator.verifyRequestReceipt();

				failOverEsi1AcctCommunicator.verifyRequestNotReceived();
			}

			@Test
			public void onRequestTimeoutFromStatefulPrimaryEsiRequestIsLoadBalancedThroughFailOverEsiGroup() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();
				
				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}

			@Test
			public void requestIsLoadBalancedThroughFailOverGroupIfStatefulPrimaryEsiIsDead() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();

				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}
			
			@Test
			public void requestIsDroppedIfStatefulPrimaryEsiIsDeadAndNoAliveFailOverCommunicatorIsFound() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();

				primaryEsi1AuthCommunicator.markDead();
				failOverEsi1AuthCommunicator.markDead();
				failOverEsi2AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				verifyUserListenerRequestDroppedEvent();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
			}

			@Test
			public void sendTimeOutIfGetsTimedOutFromStatefulPrimaryEsiAndEsiFromFailOverGroup() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}

			@Test
			public void requestIsServedByFailOverEsiIfInitialRequestIsServedByItAsTimedOutFromPrimaryEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				assertTrue(primaryEsi1AuthCommunicator.isAlive());

				failOverEsi1AuthCommunicator.verifyRequestReceipt(2);

				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}

			@Test
			public void requestIsTimedOutIfStatefulFailOverEsiIsNotResponding() {
				authRequestIsServedByFailOverEsiGroup();
				
				verifyActiveEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1AuthCommunicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();
			}

			@Test
			public void requestIsDroppedIfStatefulFailOverEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				failOverEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

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
						redundancyMode(RedundancyMode.NM.redundancyModeName).
						addPrimaryEsiIdWithLoadFactor("primary1",LOAD_FACTOR_1).
						addPrimaryEsiIdWithLoadFactor("primary2",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOver1",LOAD_FACTOR_1).
						addFailOverEsiIdWithLoadFactor("failOver2",LOAD_FACTOR_1).
						radiusGroupData;

				radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
			}

			@Test
			public void authAndAcctRequestIsServedBySameEsiForSessionOfSameUser() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AcctCommunicator.verifyRequestReceipt(1).sendsAccessAccept();

				verifyUserListenerResponseReceivedEvent(2);
			}

			@Test
			public void sendingAuthRequestToStatefulAuthEsiEvenIfStatefulAcctEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));

				primaryEsi1AcctCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt(2);

				failOverEsi1AuthCommunicator.verifyRequestNotReceived();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
			}

			@Test
			public void sendingAcctRequestToStatefulAcctEsiEvenIfStatefulAuthEsiIsDead() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AcctCommunicator.verifyRequestReceipt();

				failOverEsi1AcctCommunicator.verifyRequestNotReceived();
			}

			@Test
			public void onRequestTimeoutFromStatefulPrimaryEsiRequestIsLoadBalancedThroughFailOverEsiGroup() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();
				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				verifyStandByEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}

			@Test
			public void requestIsLoadBalancedThroughFailOverGroupIfStatefulPrimaryEsiIsDead() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();

				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1AuthCommunicator.verifyRequestReceipt();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				verifyStandByEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}
			
			@Test
			public void requestIsDroppedIfStatefulPrimaryEsiIsDeadAndNoAliveFailOverCommunicatorIsFound() {
				authRequestIsServedByEsiFromPrimaryEsiGroup();

				primaryEsi1AuthCommunicator.markDead();
				failOverEsi1AuthCommunicator.markDead();
				failOverEsi2AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);
				
				verifyUserListenerRequestDroppedEvent();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
			}

			@Test
			public void sendTimeOutIfGetsTimedOutFromStatefulPrimaryEsiAndEsiFromFailOverGroup() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt(2).doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				verifyUserListenerRequestTimeOutEvent();

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				verifyStandByEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
			}
			
			@Test
			public void requestIsServedByPrimaryEsiIfAliveEvenThoughInitialIsServedByFailOverEsiAsTimedOutFromPrimaryEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				verifyStandByEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				assertTrue(primaryEsi1AuthCommunicator.isAlive());

				primaryEsi1AuthCommunicator.verifyRequestReceipt(2);

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				assertTrue(Strings.isNullOrBlank((String) session1.getParameter(standByEsiParameter)));
			}
			
			@Test
			public void requestIsServedByStatefulFailOverEsiIfPrimaryEsiIsDeadInCaseWhenInitialRequestIsServedByFailOverEsiAsTimedOutFromPrimaryEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				verifyStandByEsiParameterSessionIs(getName(failOverEsi1AuthCommunicator) + "-" + getName(failOverEsi1AcctCommunicator));
				
				primaryEsi1AuthCommunicator.markDead();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				failOverEsi1AuthCommunicator.verifyRequestReceipt(2);

				verifyActiveEsiParameterSessionIs(getName(primaryEsi1AuthCommunicator) + "-" + getName(primaryEsi1AcctCommunicator));
				assertFalse(Strings.isNullOrBlank((String) session1.getParameter(standByEsiParameter)));
			}
		}
	}

	public class StickySessionDisable {
		
		@Before
		public void setUp() throws InitializationFailedException, InvalidURLException {
			radiusGroupData = createESIGroupData().groupName("esi-group1").
					isStatefulEnable(false).
					esiType(EsiType.CORRELATED_RADIUS.typeName).
					redundancyMode(RedundancyMode.NM.redundancyModeName).
					isSwitchBackEnable(false).
					addPrimaryEsiIdWithLoadFactor("primary1",LOAD_FACTOR_1).
					addPrimaryEsiIdWithLoadFactor("primary2",LOAD_FACTOR_1).
					addFailOverEsiIdWithLoadFactor("failOver1",LOAD_FACTOR_1).
					addFailOverEsiIdWithLoadFactor("failOver2",LOAD_FACTOR_1).
					radiusGroupData;

			radiusESIGroupImpl = createRadiusEsiGroup(radiusGroupData);
		}
		
		public class ForInitialRequest {

			@Test
			public void requestIsForwardedToAliveEsiBasedOnLoadFactor() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
				
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session2);

				primaryEsi2AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
			}

			@Test
			public void requestSendToFailOverEsiWhenGetsTimedOutFromPrimaryEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt();
			}
		}

		public class ForwardingSubsequentRequest {

			@Test
			public void subSequentRequestSendToEsiBasedOnLoadFactor() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(acctPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2AcctCommunicator.verifyRequestReceipt(1).sendsAccessAccept();
			}

			@Test
			public void toFailOverEsiWhenGetsTimedOutFromPrimaryEsi() {
				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

				radiusESIGroupImpl.handleRequest(authPacket.getBytes(true), SHARED_SECRET, userListener, session1);

				primaryEsi2AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

				failOverEsi1AuthCommunicator.verifyRequestReceipt();
			}

		}
		
	}

	private RadiusEsiGroup createRadiusEsiGroup(RadiusEsiGroupData radiusGroupData) throws InitializationFailedException, InvalidURLException {

		CorrelatedRadiusData primaryData1 = new CorrelatedRadiusData();
		primaryData1.setName("primary1");
		primaryData1.setAuthEsiName("primaryEsi1Auth");
		primaryData1.setAcctEsiName("primaryEsi1Acct");

		CorrelatedRadiusData failOverData1 = new CorrelatedRadiusData();
		failOverData1.setName("failOver1");
		failOverData1.setAuthEsiName("failOverEsi1Auth");
		failOverData1.setAcctEsiName("failOverEsi1Acct");

		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("primary1")).thenReturn(primaryData1);
		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("failOver1")).thenReturn(failOverData1);

		when(radESConfiguration.getESDataByName("primaryEsi1Auth")).thenReturn(Optional.of(primaryEsi1Auth));
		when(radESConfiguration.getESDataByName("primaryEsi1Acct")).thenReturn(Optional.of(primaryEsi1Acct));
		when(radESConfiguration.getESDataByName("failOverEsi1Auth")).thenReturn(Optional.of(failOverEsi1Auth));
		when(radESConfiguration.getESDataByName("failOverEsi1Acct")).thenReturn(Optional.of(failOverEsi1Acct));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi1Auth.getUUID(),
				dummyServerContext, primaryEsi1Auth)).thenReturn(primaryEsi1AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi1Acct.getUUID(),
				dummyServerContext, primaryEsi1Acct)).thenReturn(primaryEsi1AcctCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi1Auth.getUUID(),
				dummyServerContext, failOverEsi1Auth)).thenReturn(failOverEsi1AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi1Acct.getUUID(),
				dummyServerContext, failOverEsi1Acct)).thenReturn(failOverEsi1AcctCommunicator.getCommunicator());

		// 2nd communicator

		CorrelatedRadiusData primaryData2 = new CorrelatedRadiusData();
		primaryData2.setName("primary2");
		primaryData2.setAuthEsiName("primaryEsi2Auth");
		primaryData2.setAcctEsiName("primaryEsi2Acct");

		CorrelatedRadiusData failOverData2 = new CorrelatedRadiusData();
		failOverData2.setName("failOver2");
		failOverData2.setAuthEsiName("failOverEsi2Auth");
		failOverData2.setAcctEsiName("failOverEsi2Acct");

		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("primary2")).thenReturn(primaryData2);
		when(correatedRadiusConfiguration.getCorrelatedRadiusUsingName("failOver2")).thenReturn(failOverData2);

		when(radESConfiguration.getESDataByName("primaryEsi2Auth")).thenReturn(Optional.of(primaryEsi2Auth));
		when(radESConfiguration.getESDataByName("primaryEsi2Acct")).thenReturn(Optional.of(primaryEsi2Acct));
		when(radESConfiguration.getESDataByName("failOverEsi2Auth")).thenReturn(Optional.of(failOverEsi2Auth));
		when(radESConfiguration.getESDataByName("failOverEsi2Acct")).thenReturn(Optional.of(failOverEsi2Acct));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi2Auth.getUUID(),
				dummyServerContext, primaryEsi2Auth)).thenReturn(primaryEsi2AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi2Acct.getUUID(),
				dummyServerContext, primaryEsi2Acct)).thenReturn(primaryEsi2AcctCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi2Auth.getUUID(),
				dummyServerContext, failOverEsi2Auth)).thenReturn(failOverEsi2AuthCommunicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(failOverEsi2Acct.getUUID(),
				dummyServerContext, failOverEsi2Acct)).thenReturn(failOverEsi2AcctCommunicator.getCommunicator());

		this.activeEsiParmeter = radiusGroupData.getName() + "-active esi";
		this.standByEsiParameter = radiusGroupData.getName() + "-stand by esi";

		radiusESIGroupImpl = new RadiusESIGroupFactory().getOrCreateGroupInstance(dummyServerContext, radiusGroupData);

		return radiusESIGroupImpl;
	}

	public void verifyStandByEsiParameterSessionIs(String esiName) {
		assertThat(session1.getParameter(standByEsiParameter), equalTo(esiName));
	}

	private void authRequestIsServedByFailOverEsiGroup() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

		primaryEsi1AuthCommunicator.verifyRequestReceipt().doesNotRespondWithinRequestTimeout();

		failOverEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();
	}

	private void authRequestIsServedByEsiFromPrimaryEsiGroup() {
		radiusESIGroupImpl.handleRequest(authPacket.getBytes(), SHARED_SECRET, userListener, session1);

		primaryEsi1AuthCommunicator.verifyRequestReceipt().sendsAccessAccept();

	}

	private void verifyActiveEsiParameterSessionIs(String esiName) {
		assertThat(session1.getParameter(activeEsiParmeter), equalTo(esiName));
	}

}
