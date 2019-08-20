package com.elitecore.diameterapi.diameter.common.peers;

import java.util.Collection;

import com.elitecore.diameterapi.diameter.common.data.PeerData;

/**
 * @author Manjil Purohit
 *
 */
public class PeerProviderImpl implements PeerProvider {
	DiameterPeersTable diameterPeersTable;
	
	public PeerProviderImpl(DiameterPeersTable diameterPeersTable) {
		this.diameterPeersTable = diameterPeersTable;
	}

	@Override
	public Collection<DiameterPeer> getPeerList() {
		return diameterPeersTable.getPeerList();
	}

	@Override
	public DiameterPeer getPeer(String strPeerHostIdentity) {
		return diameterPeersTable.getPeer(strPeerHostIdentity);
	}

	@Override
	public DiameterPeer getPeerByName(String peerName) {
		return diameterPeersTable.getPeerByName(peerName);
	}

	@Override
	public PeerData getPeerData(String strPeerHostIdentity) {
		return diameterPeersTable.getPeerData(strPeerHostIdentity);
	}

}
