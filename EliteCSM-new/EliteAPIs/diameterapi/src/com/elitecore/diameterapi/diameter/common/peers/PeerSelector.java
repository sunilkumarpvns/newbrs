package com.elitecore.diameterapi.diameter.common.peers;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.selector.PeerCommunicatorGroupSelector;

public class PeerSelector {
	private RouterContext diameterRouterContext;
	private PeerCommunicatorGroupSelector peerCommunicatorGroupSelector;

	public PeerSelector(@Nonnull PeerCommunicatorGroupSelector peerCommunicatorGroupSelector, 
			@Nonnull final RouterContext diameterRouterContext) {
		this.peerCommunicatorGroupSelector = peerCommunicatorGroupSelector;
		this.diameterRouterContext = diameterRouterContext;
	}
	
	/**
	 * This checks if Peer Communicator (irrespective of Alive or Dead)
	 * is known to Peer Selector
	 * 
	 * NOTE: Only peers known to Diameter Stack are considered here.
	 * If peer is unknown, then it is probably case of 
	 * proxy-to-proxy routing.  
	 * 
	 * @param hostIdentity of Peer
	 * @return true if peer can be selected by this Peer Selector 
	 */
	public boolean isKnown(String hostIdentity) {
		PeerData peerData = diameterRouterContext.getPeerData(hostIdentity);
		if (peerData != null) {
			return peerCommunicatorGroupSelector.peers().contains(peerData.getPeerName());
		}
		return true;
	}
	
	/**
	 * This method selects Peer Communicator Group out of 
	 * All Communicator Groups of the Routing Entry.
	 * 
	 * @param diameterRequest
	 * @return Peer Communicator Group out of all groups
	 */
	private DiameterPeerCommunicatorGroup selectCommunicatorGroup(DiameterRequest diameterRequest){
		return peerCommunicatorGroupSelector.select(diameterRequest);
	}

	/**
	 * Select Next Peer from Communicator Groups based on Diameter Request.
	 * 
	 * @param diameterRequest
	 * @return Peer Communicator
	 */
	public String selectNextPeer (DiameterRequest diameterRequest){
		DiameterPeerCommunicatorGroup communicatorGroup = selectCommunicatorGroup(diameterRequest);
		if (communicatorGroup != null) {
			return communicatorGroup.getNextPeer();
		}
		return null;
	}
	
	/**
	 * Selects a Secondary Communicator from the all groups
	 * 
	 * @param diameterRequest
	 * @param ignorePeers is the Array of Peers 
	 * 		  such that Secondary Peer does not belong to this List. 
	 * 
	 * @return Peer Communicator Name
	 */
	public String selectSecondaryPeer(DiameterRequest diameterRequest, String... ignorePeers){	

		DiameterPeerCommunicatorGroup peerCommGroup = selectCommunicatorGroup(diameterRequest);
		if(peerCommGroup == null){
			return null;
		}
		DiameterPeerCommunicator[] tempIgnoreCommList = new DiameterPeerCommunicator[ignorePeers.length];
		int size = 0;
		for(int i=0; i<ignorePeers.length; i++){
			if(ignorePeers[i]!= null){
				DiameterPeerCommunicator peerCommunicator =  diameterRouterContext.getPeerCommunicator(ignorePeers[i]);
				if(peerCommunicator != null){
					tempIgnoreCommList[i] = peerCommunicator;
					size++;
				}
			}
		}
		DiameterPeerCommunicator[] ignoreCommunicatorList = new DiameterPeerCommunicator[size];
		System.arraycopy(tempIgnoreCommList, 0, ignoreCommunicatorList, 0, size);
		return peerCommGroup.getSecondaryPeer(ignoreCommunicatorList);
	}

}
