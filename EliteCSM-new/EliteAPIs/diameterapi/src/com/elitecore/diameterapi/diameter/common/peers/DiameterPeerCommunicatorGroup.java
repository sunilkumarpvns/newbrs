package com.elitecore.diameterapi.diameter.common.peers;


import com.elitecore.core.systemx.esix.ESCommunicatorGroup;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public interface DiameterPeerCommunicatorGroup extends ESCommunicatorGroup<DiameterPeerCommunicator>{
	
	public boolean assignGroup(DiameterPacket diameterPacket);
	
	public String getNextPeer();
	
	public String getSecondaryPeer(DiameterPeerCommunicator... ignoreCommunicatorList);

}
