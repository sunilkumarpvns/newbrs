package com.elitecore.diameterapi.diameter.common.routerx;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public interface RouterContext {
	
	/**
	 * 
	 * @param hostIdentity can be Peer Name, Host Identity or IP of Peer
	 * @return Peer Data for Peer
	 */
	public PeerData getPeerData(String hostIdentity);
	
	/**
	 * This method creates if not and returns {@link PeerCommunicator} of the Peer. 
	 * Also maintains a Map of Peer Communicator for Diameter Router.
	 *  
	  * @param hostIdentity hostIdentity , IP or Name of the Peer for which you need Peer Communicator
	 * @return Peer Communicator for the peer
	 */
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity); 
	
	/**
	 * 
	 * @return Virtual Routing Peer's Name
	 */
	public String getVirtualRoutingPeerName();
	
	/**
	 * Update Unknown Answer HbH Drop Statistics.
	 * @param answer
	 * @param hostIdentity
	 * @param realmName
	 * @param routeAction
	 */
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, 
			String hostIdentity, String realmName, RoutingActions routeAction);

	/**
	 * Update packet Dropped Statistics.
	 * @param packet
	 * @param hostIdentity
	 * @param realmName
	 * @param routeAction
	 */
	public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, 
			String hostIdentity, String realmName, RoutingActions routeAction);
	
	public void updateRealmInputStatistics(DiameterPacket packet, String realmName,RoutingActions routeAction);
	public void updateRealmOutputStatistics(DiameterPacket packet, String realmName,RoutingActions routeAction);
	public void updateRealmTimeoutRequestStatistics(DiameterRequest destinationRequest, String realmName,RoutingActions routingAction);
	//For Groovy
	public void postRequestRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			String originPeerId, String destPeerId, String routingEntryName);
	
	public void preAnswerRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			DiameterAnswer originAnswer, String originPeerId, String routingEntryName);
	
	public void postAnswerRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, 
			String originPeerId, String destPeerId, String routingEntryName);
	
	public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;

	public RoutingEntry getRoutingEntry(String routingEntryName);

	
	
}