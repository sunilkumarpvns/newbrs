package com.elitecore.diameterapi.diameter.common.peers.api;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;

/**
 * 
 * @author harsh.patel
 * @author narendra.pathai
 *
 */
public class DiameterPeerSpy {

	private static final String MODULE = "DIAMETER-PEER-OPERATION";
	private DiameterPeerExt diameterPeer;
	private DummyStackContext stackContext;

	public DiameterPeerSpy(DummyStackContext stackContext, PeerData peerData) {
		this.stackContext = stackContext;
		diameterPeer = Mockito.spy(new DiameterPeerExt(peerData));
	}

	public DiameterPeerExt getDiameterPeer() {
		return diameterPeer;
	}

	public void markAlive(){
		diameterPeer.markAlive();
	}

	public void markDead() {
		diameterPeer.markDead();
	}

	public DiameterPeerSpy sendsAnswer(DiameterAnswer answer, DiameterSession session) {
		LogManager.getLogger().info(MODULE, "Received answer from peer: " + diameterPeer.getHostIdentity() + " = " + answer);
		diameterPeer.getResponseListener().responseReceived(answer, diameterPeer.getHostIdentity(), session);
		return this;
	}

	public DiameterPeerSpy doesNotAnswerWithinRequestTimeout(DiameterSession session) {
		LogManager.getLogger().info(MODULE, "Request timeout answer from peer: " + diameterPeer.getHostIdentity());
		diameterPeer.getResponseListener().requestTimedout(diameterPeer.getHostIdentity(), session);
		return this;
	}

	public DiameterPacket getLastSentDiameterPacket() {
		return diameterPeer.lastSentDiameterPacket;
	}

	public class DiameterPeerExt extends DiameterPeer {

		private ResponseListener listener;
		private DiameterPacket lastSentDiameterPacket;
		private List<DiameterPeerStatusListener> diameterPeerStatusListeners = new ArrayList<>();
		private DiameterPeerState status = DiameterPeerState.I_Open;
		
		public DiameterPeerExt(PeerData peerData) {
			super(peerData, stackContext, new DiameterRouter(stackContext, Collections.<RoutingEntryData>emptyList()), new SessionFactoryManagerImpl(stackContext), new DiameterAppMessageHandler(stackContext), Mockito.mock(ExplicitRoutingHandler.class), new DuplicateDetectionHandler(stackContext));
		}

		public ResponseListener getResponseListener() {
			return listener;
		}
		
		@Override
		public DiameterPeerState registerStatusListener(DiameterPeerStatusListener listener) {
			this.diameterPeerStatusListeners.add(listener);
			return status;
		}
		
		public void markAlive(){
			for(DiameterPeerStatusListener diameterPeerStatusListener : diameterPeerStatusListeners) {
				diameterPeerStatusListener.markOpen();
			}

			status = DiameterPeerState.I_Open;
		}

		public void markDead() {
			for(DiameterPeerStatusListener diameterPeerStatusListener : diameterPeerStatusListeners) {
				diameterPeerStatusListener.markClosed();
			}
			
			status = DiameterPeerState.Closed;
		}
		
		@Override
		public boolean isAlive() {
			return DiameterPeerState.I_Open == status;
		}

		@Override
		public void sendDiameterRequest(DiameterRequest diameterRequest, ResponseListener listener)
				throws UnhandledTransitionException {
			if ( isAlive() == false) {
				throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed.");
			}
			
			this.lastSentDiameterPacket = diameterRequest;
			this.listener = listener;
			LogManager.getLogger().info("DUMMY_DIAMETER_PEER", "Sent diameter request to " + getHostIdentity()
			+ " -> " + diameterRequest);
		}
		
		@Override
		public void sendDiameterAnswer(DiameterAnswer diameterAnswer) throws UnhandledTransitionException {
			if ( isAlive() == false) {
				throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed.");
			}
			
			this.lastSentDiameterPacket = diameterAnswer;
			LogManager.getLogger().info("DUMMY_DIAMETER_PEER", "Sent diameter answer to " + getHostIdentity()
			+ " -> " + diameterAnswer);
		}
	}

	public DiameterPeerSpy verifyRequestReceipt() {
		verify(getDiameterPeer(), times(1)).sendDiameterRequest(Mockito.any(DiameterRequest.class), Mockito.any(ResponseListener.class));
		return this;
	}

	public DiameterPeerSpy verifyRequestReceipts(int count) {
		verify(getDiameterPeer(), times(count)).sendDiameterRequest(Mockito.any(DiameterRequest.class), Mockito.any(ResponseListener.class));
		return this;
	}

	public DiameterPeerSpy verifyAnswerReceipt() {
		verify(getDiameterPeer(), times(1)).sendDiameterAnswer(Mockito.any(DiameterAnswer.class));
		return this;
	}

	public DiameterPeerSpy verifyAnswerReceipts(int count) {
		verify(getDiameterPeer(), times(count)).sendDiameterAnswer(Mockito.any(DiameterAnswer.class));
		return this;
	}

	public void sendsTooBusyAnswer(DiameterSession session) {
		DiameterAnswer tooBusy = DiameterPacketBuilder.answerBuilder(getLastSentDiameterPacket().getAsDiameterRequest())
				.resultCode(ResultCode.DIAMETER_TOO_BUSY)
				.addOrReplaceAVP(DiameterAVPConstants.ORIGIN_HOST, getDiameterPeer().getHostIdentity())
				.addOrReplaceAVP(DiameterAVPConstants.ORIGIN_REALM, getDiameterPeer().getRealm())
				.build();
		sendsAnswer(tooBusy, session);
	}

	public void verifyRequestNotReceived() {
		verify(getDiameterPeer(), never()).sendDiameterRequest(Mockito.any(DiameterRequest.class), Mockito.any(ResponseListener.class));
	}

	public void verifyAnswerNotReceived() {
		verify(getDiameterPeer(), never()).sendDiameterAnswer(Mockito.any(DiameterAnswer.class));
	}
	
	public static class DiameterPeerGroupOperation {

		private DiameterPeerSpy[] diameterPeerOperations;

		public DiameterPeerGroupOperation(DiameterPeerSpy... diameterPeerOperations) {
			this.diameterPeerOperations = diameterPeerOperations;
		}
		
		public void doNotAnswerWithinRequestTimeout(DiameterSession session) {
			for (DiameterPeerSpy peerOperation : diameterPeerOperations) {
				peerOperation.doesNotAnswerWithinRequestTimeout(session);
			}
		}
		
		public void sendTooBusyAnswer(DiameterSession session) {
			for (DiameterPeerSpy peerOperation : diameterPeerOperations) {
				peerOperation.sendsTooBusyAnswer(session);
			}
		}
		
		public void areDead() {
			for (DiameterPeerSpy peerOperation : diameterPeerOperations) {
				peerOperation.markDead();
			}
		}
	}
	
	public static DiameterPeerGroupOperation allOf(DiameterPeerSpy... diameterPeerOperations) {
		return new DiameterPeerGroupOperation(diameterPeerOperations);
	}
}
