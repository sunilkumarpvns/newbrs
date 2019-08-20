package com.elitecore.diameterapi.diameter.stack;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicatorFactory;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public class DummyRouterContext implements RouterContext {
	
	private Map<String, PeerData> peerIdentityToPeerData = new HashMap<String, PeerData>();
	private DiameterPeerCommunicatorFactory peerCommunicatorFactory;
	private Map<String, RoutingEntry> routingEntries;
	
	
	public DummyRouterContext(DiameterPeerCommunicatorFactory peerCommunicatorFactory) {
		this.peerCommunicatorFactory = peerCommunicatorFactory;
		this.routingEntries = new HashMap<String, RoutingEntry>();
	}
	
	public void addPeerData(PeerData peerData) {
		peerIdentityToPeerData.put(peerData.getHostIdentity(), peerData);
		peerIdentityToPeerData.put(peerData.getPeerName(), peerData);
	}
	
	@Override
	public PeerData getPeerData(String hostIdentity) {
		return peerIdentityToPeerData.get(hostIdentity);
	}

	@Override
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
		return peerCommunicatorFactory.createInstance(hostIdentity);
	}

	@Override
	public String getVirtualRoutingPeerName() {
		return null;
	}

	@Override
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName,
			RoutingActions routeAction) {
		
	}

	@Override
	public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName,
			RoutingActions routeAction) {
		
	}

	@Override
	public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
		
	}

	@Override
	public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
		
	}

	@Override
	public void updateRealmTimeoutRequestStatistics(DiameterRequest destinationRequest, String realmName,
			RoutingActions routingAction) {
		
	}

	@Override
	public void postRequestRouting(DiameterRequest originRequest, DiameterRequest destinationRequest,
			String originPeerId, String destPeerId, String routingEntryName) {
		
	}

	@Override
	public void preAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer, String originPeerId, String routingEntryName) {
		
	}

	@Override
	public void postAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String originPeerId, String destPeerId,
			String routingEntryName) {
		
	}

	@Override
	public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name)
			throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
		return null;
	}

	@Override
	public RoutingEntry getRoutingEntry(String routingEntryName) {
		return this.routingEntries.get(routingEntryName);
	}
	
	public void addRoutingEntry(RoutingEntry routingEntry) {
		this.routingEntries.put(routingEntry.getRoutingEntryName(), routingEntry);
	}

}
