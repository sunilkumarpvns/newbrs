package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicatorFactory;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.DummyPeerProvider;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.DummyRouterContext;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

/**
 * 
 * @author narendra.pathai
 *
 */
public abstract class DiameterTestSupport {
	private static final long APPLICATION_ID = ApplicationIdentifier.NASREQ.applicationId;
	
	protected static final String[] ORIGINATOR_PEER_1 = {"originator_peer", "originator.example.com", "example.com"};
	protected static final String[] ORIGINATOR_PEER_2 = {"originator_peer_2", "originator2.example.com", "example.com"};
	
	protected static final String[] ROUTING_PEER = {"routing_peer", "routing.example.net","example.net"};
	protected static final String[] REQUEST_KNOWN_PEER = {"request_peer", "known.example.net","example.net"};
	protected static final String[] ROUND_ROBIN_PEER = {"round_robin_peer", "roundrobin.example.net","example.net"};
	protected static final String[] SECONDARY_PEER = {"secondary_peer", "secondary.example.net","example.net"};
	protected static final String[] UNKNOWN_PEER = {"unknown_peer", "unknown.example.net","example.net"};
	protected static final String SESSION_ID_VALUE = "SESSION1";
	
	private DiameterSession session;
	private DummyPeerProvider peerProvider = new DummyPeerProvider();
	private DummyStackContext stackContext = spy(new DummyStackContext(peerProvider));
	private DiameterPeerCommunicatorFactory peerCommunicatorFactory = createPeerCommunicatorFactory();
	private DummyRouterContext routerContext = new DummyRouterContext(peerCommunicatorFactory);
	private Map<String, DiameterPeerSpy> identityToPeerOperation = new HashMap<String, DiameterPeerSpy>();
	
	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
		registerPeers();
		session = addSession(SESSION_ID_VALUE);
	}

	private void registerPeers() {
		PeerData originPeerData = new PeerDataProvider()
				.withPeerName(ORIGINATOR_PEER_1[0])
				.withHostIdentity(ORIGINATOR_PEER_1[1])
				.withRealmName(ORIGINATOR_PEER_1[2])
				.build();
		
		PeerData originPeer2Data = new PeerDataProvider()
				.withPeerName(ORIGINATOR_PEER_2[0])
				.withHostIdentity(ORIGINATOR_PEER_2[1])
				.withRealmName(ORIGINATOR_PEER_2[2])
				.build();

		PeerData roundRobinPeerData = new PeerDataProvider()
				.withPeerName(ROUND_ROBIN_PEER[0])
				.withHostIdentity(ROUND_ROBIN_PEER[1])
				.withRealmName(ROUND_ROBIN_PEER[2])
				.build();	

		PeerData routingPeerData = new PeerDataProvider()
				.withPeerName(ROUTING_PEER[0])
				.withHostIdentity(ROUTING_PEER[1])
				.withRealmName(ROUTING_PEER[2])
				.build();

		PeerData knownDestHostPeer = new PeerDataProvider()
				.withPeerName(REQUEST_KNOWN_PEER[0])
				.withHostIdentity(REQUEST_KNOWN_PEER[1])
				.withRealmName(REQUEST_KNOWN_PEER[2])
				.build();
		
		PeerData secondaryPeer = new PeerDataProvider()
				.withPeerName(SECONDARY_PEER[0])
				.withHostIdentity(SECONDARY_PEER[1])
				.withRealmName(SECONDARY_PEER[2])
				.build();
		
		addPeer(originPeerData)
		.addPeer(originPeer2Data)
		.addPeer(roundRobinPeerData)
		.addPeer(routingPeerData)
		.addPeer(knownDestHostPeer)
		.addPeer(secondaryPeer);
	}
	
	public DummyRouterContext getRouterContext() {
		return routerContext;
	}
	
	protected DiameterSession addSession(String sessionId) {
		stackContext.addSession(sessionId);
		return (DiameterSession) stackContext.getOrCreateSession(sessionId, APPLICATION_ID);
	}
	
	protected DiameterSession getSession(String sessionId, long appId) {
		return (DiameterSession) stackContext.getOrCreateSession(sessionId, appId);
	}
	
	protected DiameterPeerCommunicatorFactory createPeerCommunicatorFactory() {
		return new DiameterPeerCommunicatorFactory(stackContext, peerProvider);
	}
	
	public DiameterTestSupport addPeer(PeerData peerData) {
		routerContext.addPeerData(peerData);
		stackContext.addPeerData(peerData);
		DiameterPeerSpy peerOperation = 
				new DiameterPeerSpy(stackContext, peerData);
		peerProvider.addPeer(peerOperation.getDiameterPeer());
		identityToPeerOperation.put(peerData.getHostIdentity(), peerOperation);
		identityToPeerOperation.put(peerData.getPeerName(), peerOperation);
		return this;
	}
	
	public DiameterTestSupport addPeer(DiameterPeer peer) {
		peerProvider.addPeer(peer);
		return this;
	}
	
	protected DiameterPeerSpy getPeerOperation(String hostIdentity) {
		return identityToPeerOperation.get(hostIdentity);
	}
	
	protected DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
		return stackContext.getPeerCommunicator(hostIdentity);
	}

	public DummyStackContext getStackContext() {
		return stackContext;
	}
	
	protected DiameterAnswer createSuccessAnswerFrom(DiameterPeerSpy routingPeer) {
		return DiameterPacketBuilder.answerBuilder(routingPeer.getLastSentDiameterPacket().getAsDiameterRequest())
				.resultCode(ResultCode.DIAMETER_SUCCESS)
				.addAVP(DiameterAVPConstants.CLASS, "hello")
				.build();
	}
	

	protected DiameterAnswer createAnswerFrom(DiameterPeerSpy peerOperation, ResultCode resultCode) {
		return DiameterPacketBuilder.answerBuilder(peerOperation.getLastSentDiameterPacket().getAsDiameterRequest())
				.resultCode(resultCode)
				.build();
	}
	
	protected DiameterRequest createCCInitialRequest(String sessionId) {
		DiameterRequest request = DiameterPacketBuilder.requestBuilder(getRouterContext().getPeerData(ORIGINATOR_PEER_1[1]))
				.commandCode(CommandCode.CREDIT_CONTROL)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST_STR)
				.addAVP(DiameterAVPConstants.SESSION_ID, sessionId)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ROUTING_PEER[2])
				.addAVP(DiameterAVPConstants.AUTH_APPLICATION_ID, "4")
				.build();
		return request;
	}
	
	protected DiameterRequest createCCInitialRequest() {
		return createCCInitialRequest(SESSION_ID_VALUE);
	}
	
	protected DiameterRequest createCCInitialRequestFrom(PeerData peerData) {
		DiameterRequest request = DiameterPacketBuilder.requestBuilder(peerData)
				.commandCode(CommandCode.CREDIT_CONTROL)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST_STR)
				.addAVP(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ROUTING_PEER[2])
				.addAVP(DiameterAVPConstants.AUTH_APPLICATION_ID, "4")
				.build();
		return request;
	}
	
	protected DiameterRequest createCCUpdateRequest() {
		DiameterRequest request = DiameterPacketBuilder.requestBuilder(getRouterContext().getPeerData(ORIGINATOR_PEER_1[1]))
				.commandCode(CommandCode.CREDIT_CONTROL)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST_STR)
				.addAVP(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ROUTING_PEER[2])
				.addAVP(DiameterAVPConstants.AUTH_APPLICATION_ID, "4")
				.build();
		return request;
	}
	
	protected DiameterRequest createCCUpdateRequestFrom(PeerData peerData) {
		DiameterRequest request = DiameterPacketBuilder.requestBuilder(peerData)
				.commandCode(CommandCode.CREDIT_CONTROL)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST_STR)
				.addAVP(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ROUTING_PEER[2])
				.addAVP(DiameterAVPConstants.AUTH_APPLICATION_ID, "4")
				.build();
		return request;
	}
	
	public DiameterSession getSession() {
		return session;
	}

	public void setSession(DiameterSession session) {
		this.session = session;
	}
	
	protected DiameterRequest createRAR(PeerData peerData) {
		DiameterRequest rar = DiameterPacketBuilder.requestBuilder(peerData)
				.commandCode(CommandCode.RE_AUTHORIZATION)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ORIGINATOR_PEER_1[2])
				.addAVP(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE)
				.build();
		return rar;
	}
	
	protected DiameterAnswer createFailureRemoteAnswerFrom(DiameterPeerSpy peer, ResultCode resultCode) {
		return DiameterPacketBuilder.answerBuilder(peer.getLastSentDiameterPacket().getAsDiameterRequest())
				.resultCode(resultCode)
				.build();
	}
	
	protected static void assertNoRequestSentTo(DiameterPeerSpy...diameterPeerOperations) {
		for (DiameterPeerSpy op : diameterPeerOperations) {
			op.verifyRequestNotReceived();
		}
	}
}
