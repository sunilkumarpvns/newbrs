package com.elitecore.diameterapi.diameter.common.routerx.selector;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroup;

public class ChainPeerGroupSelector implements PeerCommunicatorGroupSelector {

	private List<PeerCommunicatorGroupSelector> peerCommunicatorGroupSelectors;
	private List<String> peers;
	
	public ChainPeerGroupSelector() {
		peerCommunicatorGroupSelectors = new ArrayList<PeerCommunicatorGroupSelector>();
		peers = new ArrayList<String>();
	}
	
	public void add(PeerCommunicatorGroupSelector peerCommunicatorGroupSelector) {
		if(peerCommunicatorGroupSelector != null) {
			peerCommunicatorGroupSelectors.add(peerCommunicatorGroupSelector);
		}
	}
	
	@Override
	public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {
		
		for(int i = 0 ; i < peerCommunicatorGroupSelectors.size() ; i++) {
			DiameterPeerCommunicatorGroup peerGroup = peerCommunicatorGroupSelectors.get(i).select(diameterRequest);
			if (peerGroup != null) {
				return peerGroup;
			}
		}
		return null;
	}

	@Override
	public void init(boolean addPeerListner) throws InitializationFailedException {

		for(int i = 0 ; i < peerCommunicatorGroupSelectors.size() ; i++) {
			peerCommunicatorGroupSelectors.get(i).init(addPeerListner);
			peers.addAll(peerCommunicatorGroupSelectors.get(i).peers());
		}
	}

	@Override
	public List<String> peers() {
		return peers;
	}

}
