package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.serverx.manager.scripts.ScriptsExecutor;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.diameterapi.diameter.translator.DiameterCopyPacketTranslator;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslationConfigDataImpl;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslatorPolicyDataImpl;

public class RelayAgentServerInitiatedTest extends DiameterTestSupport {
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	private TranslationAgent translationAgent;
	private RoutingEntry routingEntry;
	private RelayAgent relayAgent;
	private DiameterRequest clientInitiatedRequest;
	private DiameterPeerSpy remotePeer;
	private DiameterRequest rarFromRemotePeer;
	private DiameterPeerSpy originatorPeer;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		translationAgent = TranslationAgent.getInstance();
		registerCopyPacketTranslatorInTranslationAgent();
		
		createRoutingEntry();
		
		relayAgent = new ProxyAgent(getRouterContext(), translationAgent, mock(IDiameterSessionManager.class));
		
		clientInitiatedRequest = createCCInitialRequest();
		originatorPeer = getPeerOperation(ORIGINATOR_PEER_1[1]);
		remotePeer = getPeerOperation(ROUTING_PEER[1]);
		relayClientInitiatedRequest();
		rarFromRemotePeer = createRAR(getRouterContext().getPeerData(ROUTING_PEER[1]));
	}

	private void registerCopyPacketTranslatorInTranslationAgent() {
		CopyPacketTranslatorPolicyDataImpl copyPacketMappingData = new CopyPacketTranslatorPolicyDataImpl();
		
		copyPacketMappingData.setCopyPacketMapConfId("1");
		copyPacketMappingData.setName("dia_to_dia");
		copyPacketMappingData.setFromTranslatorId("TTI0001");
		copyPacketMappingData.setToTranslatorId("TTI0001");
		
		List<CopyPacketTranslationConfigDataImpl> mappings = new ArrayList<CopyPacketTranslationConfigDataImpl>();
		CopyPacketTranslationConfigDataImpl mapping1 = new CopyPacketTranslationConfigDataImpl();
		mapping1.setMappingName("mapping1");
		mapping1.setInExpression("0:263 = \"*\"");
		mappings.add(mapping1);
		
		copyPacketMappingData.setTranslationConfigDataList(mappings);
		
		translationAgent.registerCopyPacketTranslator(new DiameterCopyPacketTranslator(copyPacketMappingData, 
				mock(ScriptsExecutor.class), 
				null));

	}

	private void relayClientInitiatedRequest() throws RoutingFailedException {
		relayAgent.routeRequest(clientInitiatedRequest, getSession(), routingEntry);
		remotePeer.sendsAnswer(createSuccessAnswerFrom(remotePeer), getSession());
	}
	
	@Test
	public void onReceivingServerInitiatedMessage_WeShouldForwardItToTheSamePeer_FromWhichTheSessionWasInitiated() throws RoutingFailedException {
		
		relayAgent.routeServerInitiatedRequest(rarFromRemotePeer, getSession());
		
		originatorPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(originatorPeer), getSession());
		
		remotePeer.verifyAnswerReceipt();
		DiameterAssertion.assertThat(remotePeer.getLastSentDiameterPacket())
			.hasResultCode(ResultCode.DIAMETER_SUCCESS)
			.hasHeaderOf(rarFromRemotePeer);
	}
	
	@Test
	public void onReceivingServerInitiatedMessage_WeShouldForwardItToTheSamePeer_FromWhichTheSessionWasInitiated_WithSameHopByHopIdentifier() throws RoutingFailedException, InitializationFailedException {
		
		relayAgent.routeServerInitiatedRequest(rarFromRemotePeer, getSession());
		
		originatorPeer.verifyRequestReceipt()
			.sendsAnswer(createSuccessAnswerFrom(originatorPeer), getSession());
		
		remotePeer.verifyAnswerReceipt();
		DiameterAssertion.assertThat(remotePeer.getLastSentDiameterPacket())
			.hasResultCode(ResultCode.DIAMETER_SUCCESS)
			.hasHeaderOf(rarFromRemotePeer);
	}
	
	
	@Test
	public void serverInitiatedRequestShouldBeRelayedToOriginatorPeerAndWeMustSendUnableToDeliver_IfRequestToOriginatorPeerTimesOut() throws RoutingFailedException {
		relayAgent.routeServerInitiatedRequest(rarFromRemotePeer, getSession());
		
		originatorPeer.verifyRequestReceipt()
			.doesNotAnswerWithinRequestTimeout(getSession());
	
		DiameterAssertion.assertThat(remotePeer.getLastSentDiameterPacket())
			.hasHeaderOf(rarFromRemotePeer)
			.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
	}

	@Test
	public void tryingToSendServerInitiatedRequestShouldResultInExceptionWithDiameterUnableToDeliverReason_WhenOriginatorPeerIsDead() throws RoutingFailedException {
		originatorPeer.markDead();
		
		try {
			relayAgent.routeServerInitiatedRequest(createLocalRAR(), getSession());
			fail("Relay agent must throw exception if peer is dead");
		} catch (RoutingFailedException ex) {
			assertEquals(ResultCode.DIAMETER_UNABLE_TO_DELIVER, ex.getResultCode());
		}
	}
	
	private DiameterRequest createLocalRAR() {
		DiameterRequest rar = DiameterPacketBuilder.localRequestBuilder()
				.commandCode(CommandCode.RE_AUTHORIZATION)
				.application(ApplicationIdentifier.CC)
				.addAVP(DiameterAVPConstants.DESTINATION_REALM, ORIGINATOR_PEER_1[2])
				.build();
		
		return rar;
	}

	@Test
	public void serverInitiatedRequestShouldBeRelayedToOriginatorPeerAndRouteRecordAVPMustBeAddedInRemoteRequest() throws RoutingFailedException {
		relayAgent.routeServerInitiatedRequest(rarFromRemotePeer, getSession());
		
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
			.containsAVP(DiameterAVPConstants.ROUTE_RECORD, ROUTING_PEER[1]);
	}
	
	@Test
	public void serverInitiatedRequestShouldBeRelayedToOriginatorPeerAndRouteRecordMustNotBeAddedInRemoteRequest_InCaseItIsALocalRequest() throws RoutingFailedException {
		relayAgent.routeServerInitiatedRequest(createLocalRAR(), getSession());
		
		DiameterAssertion.assertThat(originatorPeer.getLastSentDiameterPacket())
			.doesNotContainAVP(DiameterAVPConstants.ROUTE_RECORD);
	}
	
	private RoutingEntry createRoutingEntry() throws InitializationFailedException {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
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
		data.setTransMapName("dia_to_dia");
		
		routingEntry = new RoutingEntry(data, getRouterContext(), 
				translationAgent);
		routingEntry.init();
		
		getRouterContext().addRoutingEntry(routingEntry);
		return routingEntry;
	}

	protected RoutingActions getRoutingAction() {
		return RoutingActions.RELAY;
	}
}
