package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.PeerSelector;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * It is an implementation of {@link RoutingFailureAction} , handling FAILOVER Failure Action
 * 
 * @author monica.lulla
 *
 */

public class FailoverFailureAction implements RoutingFailureAction {

	private static final String MODULE = "FAILOVR-FLR-ACT";
	private static final int SECONDARY_PEER = 0;
	private static final int NEXT_PEER = 1;
	private static final int SPECIFIC_PEER = 2;
	private String failureArgs;
	private long transactionTimeout = 3000;
	private RouterContext routerContext;
	private PeerSelector peerSelector;
	private int peerCriteria;
	private List<String> warnings;
	
	public FailoverFailureAction(RouterContext routerContext, String failureArgs, 
			long transactionTimeout, PeerSelector peerSelector){
		this.failureArgs = failureArgs;
		this.transactionTimeout = transactionTimeout;
		this.routerContext = routerContext;
		this.peerSelector = peerSelector;
		this.warnings = new ArrayList<String>();
	}

	/**
	 * This method initializes Failure Arguments for Failover Action.
	 */
	@Override
	public void init() {
		if(failureArgs == null || failureArgs.trim().length() == 0){
			warnings.add("No Diameter Peer found for " + DiameterFailureConstants.FAILOVER + " Failure Action");
			return;
		}
		failureArgs = failureArgs.trim();
		if("0.0.0.0".equals(failureArgs)){
			peerCriteria = SECONDARY_PEER;
		}else if("255.255.255.255".equals(failureArgs)){
			peerCriteria = NEXT_PEER;
		}else{
			DiameterPeerCommunicator peerComm = routerContext.getPeerCommunicator(failureArgs);
			if(peerComm == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Diameter Peer: " + failureArgs + 
					" is not registered for " + DiameterFailureConstants.FAILOVER + " Failure Action");
			}
			peerCriteria = SPECIFIC_PEER;
		}
	}

	/**
	 * 
	 * This method performs Failover Action for Failed Answer.
	 */
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, 
			DiameterSession routingSession,
			DiameterRequest originRequest,
			DiameterRequest remoteRequest,
			String remotePeerHostIdentity,
			String originPeerName) {		
		String sessionId = failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID);
		int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.FAILOVER + 
			" Failure Action with Failure Argument " + failureArgs + 
			" for Session-ID="+sessionId + " HbH-ID=" + hopByHopKey);
		
		FailureActionResult failureActionResult = null;
		//check for E2E Time
		long endToEndTime = System.currentTimeMillis() - originRequest.creationTimeMillis();
		if(endToEndTime > transactionTimeout){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Transaction timeout has exceeded for Session-ID=" + 
				sessionId + ", Sending DIAMETER_UNABLE_TO_DELIVER");
			DiameterAnswer answer = new DiameterAnswer(originRequest,ResultCode.DIAMETER_UNABLE_TO_DELIVER);
			failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR , answer);
			return failureActionResult;
		}
		
		remoteRequest.addFailedPeer(routerContext.getPeerData(remotePeerHostIdentity).getPeerName());

		List<String> failedPeerList = remoteRequest.getFailedPeerList();
		if(failedPeerList == null){
			failedPeerList = new ArrayList<String>(0);
		}
		//get configured Destination Peer
		String destPeerName = getNextFailoverPeer(originRequest, failedPeerList);
		if(destPeerName == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Sending "+ ResultCode.DIAMETER_UNABLE_TO_DELIVER + 
				", Reason: All peers in the Peer Group are Exhausted for Session-ID=" + sessionId);
			DiameterAnswer answer = new DiameterAnswer(originRequest,ResultCode.DIAMETER_UNABLE_TO_DELIVER);
			failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR , answer);
			return failureActionResult;
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Sending request to: " + destPeerName);
		
		//Destination Peer Available get its Peer data for fetching its Host ID
		PeerData peerData = routerContext.getPeerData(destPeerName);
		
		//Replace Dest Host if exists in Request
		String destHost = remoteRequest.getAVPValue(DiameterAVPConstants.DESTINATION_HOST);
		if(destHost != null && destHost.equals(remotePeerHostIdentity)){
			remoteRequest.getAVP(DiameterAVPConstants.DESTINATION_HOST).setStringValue(peerData.getHostIdentity());
		}
		//form Result
		failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_REQUEST_TO_PEER, remoteRequest, destPeerName);
		return failureActionResult;
	}

	
	private String getNextFailoverPeer(DiameterRequest originRequest, List<String> failedPeerList) {	
		String peerName = null;
		switch (peerCriteria) {
		case SECONDARY_PEER:
			return peerSelector.selectSecondaryPeer(originRequest, 
					failedPeerList.toArray(new String[failedPeerList.size()]));
		case NEXT_PEER:
			peerName = peerSelector.selectNextPeer(originRequest);
			if(peerName != null && failedPeerList.contains(peerName)== false){
				return peerName;
			}
			return peerSelector.selectSecondaryPeer(originRequest, 
					failedPeerList.toArray(new String[failedPeerList.size()]));
		case SPECIFIC_PEER:
			DiameterPeerCommunicator peerCommunicator =  routerContext.getPeerCommunicator(failureArgs);
			if(peerCommunicator != null && peerCommunicator.isAlive()) {
				peerName =  peerCommunicator.getName();
				if(failedPeerList.contains(peerName)== false){
					return peerName;
				}
			}
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Configured Host " + failureArgs + " is not Available.");
			break;
		}
		return null;
	}
	
	@Override
	public List<String> getWarnings() {
		return warnings;
	}
}
