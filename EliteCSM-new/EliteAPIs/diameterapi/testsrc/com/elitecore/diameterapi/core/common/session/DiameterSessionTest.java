package com.elitecore.diameterapi.core.common.session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.peer.exception.PeerNotFoundException;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerCommGroup;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterSessionTest {

	@Test
	public void test_release_should_call_event_listener() {

		EventListenerImpl eventListener = mock(EventListenerImpl.class);
		DiameterSession diameterSession = new DiameterSession("test", eventListener);
		diameterSession.release();

		verify(eventListener, times(1)).removeSession(diameterSession);
	}

	public static DiameterRequest createDiameterRequest(IPeerListener peer) {

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.addAvp(DiameterAVPConstants.ORIGIN_HOST, peer.getHostIdentity());
		diameterRequest.addAvp(DiameterAVPConstants.ORIGIN_REALM, peer.getRealm());

		return diameterRequest;

	}

	public static DiameterAnswer createDiameterResponse(IPeerListener peer) {

		DiameterAnswer diameterResponse = new DiameterAnswer();
		diameterResponse.addAvp(DiameterAVPConstants.ORIGIN_HOST, peer.getHostIdentity());
		diameterResponse.addAvp(DiameterAVPConstants.ORIGIN_REALM, peer.getRealm());

		return diameterResponse;

	}

	public static class EventListenerImpl implements SessionEventListener {

		@Override
		public boolean removeSession(Session session) {
			return false;
		}

		@Override
		public void update(Session session) {
			
		}

	}

	public static class SpyingGroupFactory extends DiameterPeerGroupFactory {

		public SpyingGroupFactory(IStackContext stackContext) {
			super(stackContext);
		}

		@Override
		protected synchronized DiameterPeerCommGroup createInstance(
				DiameterPeerGroupParameter diameterPeerGroupParameter) throws PeerNotFoundException {
			return spy(super.createInstance(diameterPeerGroupParameter));
		}
	}
}
