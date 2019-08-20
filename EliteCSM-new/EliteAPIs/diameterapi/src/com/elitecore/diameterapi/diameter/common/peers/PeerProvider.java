package com.elitecore.diameterapi.diameter.common.peers;

import java.util.Collection;

import com.elitecore.diameterapi.diameter.common.data.PeerData;

/**
 * @author Manjil Purohit
 *
 */
public interface PeerProvider {

	public Collection<DiameterPeer> getPeerList();

	public DiameterPeer getPeer(String strPeerHostIdentity);
	
	public DiameterPeer getPeerByName(String peerName);
	
	public PeerData getPeerData(String strPeerHostIdentity);
	
}
