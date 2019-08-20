package com.elitecore.diameterapi.diameter.common.peers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;

/**
 * 
 * A test helper peer provider that allows test firendly operations on peer table.
 * 
 * Register new peers post creation of provider, etc.
 * 
 * @author harsh.patel
 * @author narendra.pathai
 *
 */
public class DummyPeerProvider implements PeerProvider {


	private Map<String, DiameterPeer> peerByName;
	private Map<String, DiameterPeer> peerByHostIdentity;
	private List<DiameterPeer> diameterPeers;

	public DummyPeerProvider() {
		this(Collections.<DiameterPeerSpy.DiameterPeerExt>emptyList());
	}
	
	public DummyPeerProvider(List<DiameterPeerSpy.DiameterPeerExt> peers) {

		peerByHostIdentity = new HashMap<String, DiameterPeer>();
		peerByName = new HashMap<String, DiameterPeer>();
		diameterPeers = new ArrayList<DiameterPeer>();
		for(DiameterPeer peer : peers) {
			peerByHostIdentity.put(peer.getHostIdentity(), peer);
			peerByName.put(peer.getPeerName(), peer);
			diameterPeers.add(peer);
		}
	}

	@Override
	public Collection<DiameterPeer> getPeerList() {
		return diameterPeers;
	}

	public void addPeer(DiameterPeer peer) {
		peerByHostIdentity.put(peer.getHostIdentity(), peer);
		peerByName.put(peer.getPeerName(), peer);
		diameterPeers.add(peer);
	}

	@Override
	public DiameterPeer getPeer(String strPeerHostIdentity) {
		return peerByHostIdentity.get(strPeerHostIdentity);
	}

	@Override
	public DiameterPeer getPeerByName(String peerName) {
		return peerByName.get(peerName);
	}

	@Override
	public PeerData getPeerData(String strPeerHostIdentity) {
		return peerByName.get(strPeerHostIdentity).getPeerData();
	}

}