package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

// TODO discuss whether this class is required. Whether we should keep session origin host stale until a subsequent request for same session is received by server application
public class ServerInitiatedRequestResponseListener implements ResponseListener {
	private final ResponseListener userListener;

	public ServerInitiatedRequestResponseListener(
			ResponseListener listener) {
		this.userListener = listener;
	}
	
	@Override
	public void requestTimedout(String hostIdentity, DiameterSession session) {
		session.setParameter(DiameterSession.SESSION_RELEASE_KEY, hostIdentity);
		userListener.requestTimedout(hostIdentity, session);
	}

	@Override
	public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
		session.setParameter(DiameterSession.SESSION_RELEASE_KEY, hostIdentity);
		session.setParameter(DiameterAVPConstants.ORIGIN_HOST, hostIdentity);
		userListener.responseReceived(diameterAnswer, hostIdentity, session);
	}

}
