package com.elitecore.diameterapi.diameter.common.peers;

public interface DiameterPeerStatusListener {
	public void markOpen();
	public void markClosed();
}
