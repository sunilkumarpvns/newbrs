package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

/**
 * 
 * @author narendra.pathai
 * @author harsh.patel
 *
 */
public class ClientApplicationRequestResponseListener implements ResponseListener {
	
	private final ResponseListener userListener;
	private final SessionReleaseIndiactor sessionReleaseIndicator;

	public ClientApplicationRequestResponseListener(
			SessionReleaseIndiactor sessionReleaseIndicator,
			ResponseListener listener) {
		this.sessionReleaseIndicator = sessionReleaseIndicator;
		this.userListener = listener;
	}
	
	@Override
	public void requestTimedout(String hostIdentity, DiameterSession session) {
		session.setParameter(DiameterSession.SESSION_RELEASE_KEY, hostIdentity);
		session.setParameter(DiameterAVPConstants.DESTINATION_HOST, hostIdentity);
	
		userListener.requestTimedout(hostIdentity, session);
	}

	@Override
	public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity,
			DiameterSession session) {

		if (sessionReleaseIndicator.isEligible(diameterAnswer)) {
			session.release();
		} else {
			session.setParameter(DiameterSession.SESSION_RELEASE_KEY, hostIdentity);
			session.setParameter(DiameterAVPConstants.DESTINATION_HOST, hostIdentity);
		}

		userListener.responseReceived(diameterAnswer, hostIdentity, session);

	}

}
