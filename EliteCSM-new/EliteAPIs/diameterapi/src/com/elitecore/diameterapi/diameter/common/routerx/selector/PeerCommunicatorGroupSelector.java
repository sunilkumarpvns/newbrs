package com.elitecore.diameterapi.diameter.common.routerx.selector;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroup;

public interface PeerCommunicatorGroupSelector {

	public void init(boolean addPeerListner) throws InitializationFailedException;
	
	public @Nonnull DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest);
	
	public @Nonnull List<String> peers();
}
