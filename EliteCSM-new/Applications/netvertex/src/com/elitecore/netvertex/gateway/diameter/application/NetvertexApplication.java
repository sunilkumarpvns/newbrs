package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

public interface NetvertexApplication {

	void sendRequest(DiameterSession session, DiameterRequest request, DiameterPeerGroupParameter parameter,
			String preferredPeer) throws CommunicationException;
	void sendAnswer(DiameterSession session, DiameterRequest request, DiameterAnswer answer)
			throws CommunicationException;
}
