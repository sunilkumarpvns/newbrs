package com.elitecore.diameterapi.core.common.peer.group;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.api.AnswerMemorizingResponseListener;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class GeoRedunduncyPeerGroupTest extends DiameterTestSupport {

	private static final String SECONDARY_GROUP_NAME = "SecondaryGroup";

	private static final String PRIMARY_GROUP_NAME = "PrimaryGroup";

	private static final String OTHER_SESSION_ID_VALUE = "SESSION2";
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	private DiameterSession session;
	private DiameterSession otherSession;
	private DiameterRequest initialRequest;
	private PeerData originatorPeerData;
	private AnswerMemorizingResponseListener userListener;
	private GeoRedunduncyPeerGroup geoRedunduncyGroup;
	private DiameterPeerCommGroupMock primaryGroup;
	private DiameterPeerCommGroupMock secondaryGroup;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		session = addSession(SESSION_ID_VALUE);
		otherSession = addSession(OTHER_SESSION_ID_VALUE);
		
		originatorPeerData = getPeerOperation(ORIGINATOR_PEER_1[1]).getDiameterPeer().getPeerData();
		initialRequest = createCCInitialRequestFrom(originatorPeerData);

		userListener = spy(new AnswerMemorizingResponseListener());
		primaryGroup = new DiameterPeerCommGroupMock(PRIMARY_GROUP_NAME);
		secondaryGroup = new DiameterPeerCommGroupMock(SECONDARY_GROUP_NAME);
		
		geoRedunduncyGroup = new GeoRedunduncyPeerGroup(primaryGroup.mockedInstance, secondaryGroup.mockedInstance);
	}
	
	@Test
	public void sendsRequestsToPrimaryGroupIfAlive() throws CommunicationException {
		primaryGroup.markAlive();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);
		
		verify(primaryGroup.mockedInstance).sendClientInitiatedRequest(session, initialRequest, userListener);
	}
	
	@Test
	public void sendsRequestToSecondaryGroupIfPrimaryGroupIsDead() throws CommunicationException {
		primaryGroup.markDead();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);
		
		verify(secondaryGroup.mockedInstance).sendClientInitiatedRequest(session, initialRequest, userListener);
	}
	
	@Test
	public void sendsRequestToSecondaryGroupIfPrimayGroupIsAliveButFailsWhileCommunication() throws CommunicationException {
		primaryGroup.markAlive();
		doThrow(new CommunicationException("All peers in group are dead"))
			.when(primaryGroup.mockedInstance).sendClientInitiatedRequest(session, initialRequest, userListener);
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);
		
		verify(secondaryGroup.mockedInstance).sendClientInitiatedRequest(session, initialRequest, userListener);
	}
	
	@Test
	public void failsbackToPrimaryGroupForExistingSessionsIfPrimaryGroupComesBackAlive() throws CommunicationException {
		primaryGroup.markDead();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);

		reset(primaryGroup.mockedInstance);
		primaryGroup.markAlive();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);
		
		verify(primaryGroup.mockedInstance).sendClientInitiatedRequest(session, initialRequest, userListener);
	}
	
	@Test
	public void failsbackToPrimaryGroupForNewSessionsIfPrimaryGroupComesBackAlive() throws CommunicationException {
		primaryGroup.markDead();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);

		reset(primaryGroup.mockedInstance);
		primaryGroup.markAlive();
		
		geoRedunduncyGroup.sendClientInitiatedRequest(otherSession, initialRequest, userListener);
		
		verify(primaryGroup.mockedInstance).sendClientInitiatedRequest(otherSession, initialRequest, userListener);
	}
	
	@Test
	public void throwsCommunicationFailedExceptionIfBothGroupsAreDead() throws CommunicationException {
		primaryGroup.markDead();
		secondaryGroup.markDead();
		
		expectedException.expect(CommunicationException.class);
		expectedException.expectMessage(String.format("GR-Group [P - %s, S - %s] is dead", PRIMARY_GROUP_NAME, SECONDARY_GROUP_NAME));
		
		geoRedunduncyGroup.sendClientInitiatedRequest(session, initialRequest, userListener);
	}
	
	public class Aliveness {
		
		@Test
		public void isAliveIfOnlyPrimaryGroupIsAlive() throws CommunicationException {
			primaryGroup.markAlive();
			secondaryGroup.markDead();
			
			assertTrue(geoRedunduncyGroup.isAlive());
		}
		
		@Test
		public void isAliveIfOnlySecondaryGroupIsAlive() throws CommunicationException {
			primaryGroup.markDead();
			secondaryGroup.markAlive();
			
			assertTrue(geoRedunduncyGroup.isAlive());
		}
		
		@Test
		public void isDeadIfBothGroupsAreDead() throws CommunicationException {
			primaryGroup.markDead();
			secondaryGroup.markDead();
			
			assertFalse(geoRedunduncyGroup.isAlive());
		}
	}
	
	private class DiameterPeerCommGroupMock {
		
		private String name;
		DiameterPeerCommGroup mockedInstance;
		
		public DiameterPeerCommGroupMock(String name) {
			this.name = name;
			this.mockedInstance = mock(DiameterPeerCommGroup.class);
			when(mockedInstance.getName()).thenReturn(name);
		}
		
		public void markDead() throws CommunicationException {
			when(mockedInstance.isAlive()).thenReturn(false);
			doThrow(new CommunicationException("No alive peer in group " + name))
				.when(mockedInstance).sendClientInitiatedRequest(Mockito.any(DiameterSession.class), Mockito.any(DiameterRequest.class), Mockito.any(ResponseListener.class));
		}
		
		public void markAlive() throws CommunicationException {
			when(mockedInstance.isAlive()).thenReturn(true);
			doAnswer(new Answer<Void>() {

				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			}).when(mockedInstance).sendClientInitiatedRequest(Mockito.any(DiameterSession.class), Mockito.any(DiameterRequest.class), Mockito.any(ResponseListener.class));
		}
	}
}
