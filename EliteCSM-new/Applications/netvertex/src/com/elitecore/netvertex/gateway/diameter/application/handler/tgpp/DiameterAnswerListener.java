package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public interface DiameterAnswerListener {
	
	public void answerReceived(DiameterAnswer diameterAnswer, DiameterRequest diameterRequest) throws CommunicationException;

	
}
