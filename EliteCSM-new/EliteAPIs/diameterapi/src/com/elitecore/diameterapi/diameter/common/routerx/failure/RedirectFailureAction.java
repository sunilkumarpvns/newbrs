package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.PeerSelector;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * It is an implementation of {@link RoutingFailureAction} , handling REDIRECT Failure Action
 * 
 * @author monica.lulla
 *
 */
public class RedirectFailureAction implements RoutingFailureAction {

	private static final String MODULE = "REDIRECT-FLR-ACT";
	private static final String SECONDARY_PEER = "0.0.0.0";
	private static final String NEXT_PEER = "255.255.255.255";
	private String failureArgs;
	private boolean attachedRedirection;
	private RouterContext routerContext;
	private PeerSelector peerSelector;	
	//Forming Redirect AVPs
	private List<IDiameterAVP> answerAVPs;
	private List<String> redirectHosts;
	private List<String> warnings;
	
	public RedirectFailureAction(RouterContext diameterRouterContext, String failureArgs, 
			boolean attacheRedirection, PeerSelector peerSelector){
		
		this.failureArgs = failureArgs;
		this.attachedRedirection = attacheRedirection;
		this.routerContext = diameterRouterContext;
		this.answerAVPs = new ArrayList<IDiameterAVP>();
		this.peerSelector = peerSelector;
		this.warnings = new ArrayList<String>();
	}

	public void init(){

		if(failureArgs == null || failureArgs.trim().length() == 0){
			warnings.add("No redirection AVPs found for " + DiameterFailureConstants.REDIRECT + " Failure Action");
			return;
		}

		StringTokenizer st = new StringTokenizer(failureArgs, ",");

		//Traversing Redirection AVPs in Failure Arguments 
		while (st.hasMoreTokens()){

			String nextTokenString = st.nextToken();

			//Fetching AVP Id and value from Token
			StringTokenizer avpIdValuePair = new StringTokenizer(nextTokenString,"=");

			//ID Value Pair must have 2 tokens --> ID, Value
			if (avpIdValuePair.countTokens() != 2) {
				warnings.add("Invalid AVP: " + nextTokenString  + " for " + 
							DiameterFailureConstants.REDIRECT + " Failure Action" +
							", it should be in key value format i.e. <AVP_ID>=<VALUE>");
				continue;
			}

			String avpId = avpIdValuePair.nextToken().trim();
			String avpValue = avpIdValuePair.nextToken().trim();

			if(avpId.length() == 0 || avpValue.length() == 0){
				warnings.add("Invalid AVP: " + nextTokenString + " for " +
							DiameterFailureConstants.REDIRECT + " Failure Action");
				continue;
			}

			IDiameterAVP avp  = DiameterDictionary.getInstance().getKnownAttribute(avpId);
			if (avp == null) {
				warnings.add("Invalid AVP: " + nextTokenString + " for " + 
							DiameterFailureConstants.REDIRECT + " Failure Action");
				continue;
			}

			//Checking for Redirect Host AVP 
			if(avp.getAVPCode() == DiameterAVPConstants.REDIRECT_HOST_INT){

				if(redirectHosts == null){
					this.redirectHosts = new ArrayList<String>();
				}
				//Adding Redirect - Host AVP to String List
				//Dynamic AVP value will be added to AVP List Run-time.
				redirectHosts.add(avpValue);

			}else{
				//For Host Usage and Cache Time, Adding to AVP List.
				avp.setStringValue(avpValue);
				answerAVPs.add(avp);
			}
		} 

		//checking if atleast one Redirect-Host AVP is configured.
		if (redirectHosts == null) {	
			warnings.add("No Redirect-Host AVPs could be parsed for " + 
						DiameterFailureConstants.REDIRECT + "  Failure Action");
		}				
	}

	/**
	 * 
	 * this method performs Redirect Failure Action for Failed Answer.
	 */
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName) {			
		
		String sessionId = failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID);
		int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.REDIRECT + 
			" Failure Action with Failure Argument " + failureArgs + 
			" for Session-ID="+sessionId + " HbH-ID=" + hopByHopKey);
		
		
		remoteRequest.addFailedPeer(originPeerName);
		boolean bRedirectHostAdded = false;

		//To maintain list of Redirect Hosts to avoid duplicate entries of Redirect Hosts in Answer.
		List<String> ignoreRedirectHosts = remoteRequest.getFailedPeerList();
		if(ignoreRedirectHosts == null)
			ignoreRedirectHosts = new ArrayList<String>(1);
		
		List<IDiameterAVP> redirectHostAVPs = new ArrayList<IDiameterAVP>(answerAVPs.size());
		
		for(int i=0 ; i<redirectHosts.size(); i++){
			String redirectHostValue = redirectHosts.get(i);
			redirectHostValue = getRedirectHostName(redirectHostValue, originRequest, ignoreRedirectHosts);
			
			if(redirectHostValue != null){
				ignoreRedirectHosts.add(redirectHostValue);
				IDiameterAVP redirectHostAVP = getRedirectionHostAVP(redirectHostValue);
				if(redirectHostAVP != null){
					redirectHostAVPs.add(redirectHostAVP);
					bRedirectHostAdded = true;
				}
			}
		}
		
		if (bRedirectHostAdded) {			
			DiameterAnswer answer = new DiameterAnswer (originRequest, ResultCode.DIAMETER_REDIRECT_INDICATION);
			
			for(int i=0 ; i< answerAVPs.size(); i++){
				try {
					answer.addAvp((IDiameterAVP)answerAVPs.get(i).clone());
				} catch (CloneNotSupportedException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, e);
				}
			}
			
			answer.addAvps(redirectHostAVPs);
			return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, answer);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No redirection AVPs could be parsed while failure in routing for session-ID=" 
				+ sessionId + ", Passing through Answer");
			return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, failureAnswer);
		}
	
	}
	
	private String getRedirectHostName(String redirectHostValue,
			DiameterRequest originRequest, List<String> ignorePeers) {
		
		String redirectHost = null;
		if(NEXT_PEER.equals(redirectHostValue)){
			redirectHost = peerSelector.selectNextPeer(originRequest);	
			if(redirectHost != null && ignorePeers.contains(redirectHost) == false)
				return redirectHost;
			return peerSelector.selectSecondaryPeer(originRequest, 
					ignorePeers.toArray(new String[ignorePeers.size()]));
		}else if(SECONDARY_PEER.equals(redirectHostValue)){
			return peerSelector.selectSecondaryPeer(originRequest, 
					ignorePeers.toArray(new String[ignorePeers.size()]));
		}else{
			DiameterPeerCommunicator redirectPeerCommunicator =  routerContext.getPeerCommunicator(redirectHostValue);
			if(redirectPeerCommunicator == null){
				return null;
			}
			if(attachedRedirection == false || redirectPeerCommunicator.isAlive()){
				redirectHost =  redirectPeerCommunicator.getName();
			}
			if(ignorePeers.contains(redirectHost) == false)
				return redirectHost;
		}
		return null;
	}
	
	private IDiameterAVP getRedirectionHostAVP(String redirectHostName) {

		PeerData redirectPeerData = routerContext.getPeerData(redirectHostName);
		if(redirectPeerData == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer configuration for redirect host " + 
				redirectHostName + " not found.");
			return null;
		}
		String redirectHostVal = redirectPeerData.getURI();

		if(redirectHostVal == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "DiameterURI not found for Peer " + redirectHostName);
			return null;
		}

		IDiameterAVP redirectHostAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.REDIRECT_HOST);
		redirectHostAVP.setStringValue(redirectHostVal);
		return redirectHostAVP;

	}

	@Override
	public List<String> getWarnings() {
		return warnings;
	}
}
