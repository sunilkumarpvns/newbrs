package com.elitecore.diameterapi.core.common.peer.api;

import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AnswerMemorizingResponseListener implements ResponseListener {
	public DiameterAnswer diameterAnswer;
	public String hostIdentity;
	
	@Override
	public void requestTimedout(String hostIdentity, DiameterSession session) {
		this.hostIdentity = hostIdentity;
	}

	@Override
	public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
		this.diameterAnswer = diameterAnswer;
		this.hostIdentity = hostIdentity;
	}

}
