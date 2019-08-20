package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;

/**
 * It is an implementation of {@link RoutingFailureAction} , handling PASSTHROUGH Failure Action
 * @author monica.lulla
 *
 */
public class PassthroughFailureAction implements RoutingFailureAction {
	
	private static final String MODULE = "PASSTHR-FLR-ACT";
	
	public PassthroughFailureAction(){}

	/**
	 * 
	 * this method performs Passthrough Failure Action for Failed Answer.
	 * 
	 * @return FailureActionResult
	 */
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName) {			
		
		String sessionId = failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID);
		int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.PASSTHROUGH + " Failure Action." + 
			" for Session-ID="+sessionId + " HbH-ID=" + hopByHopKey);
		return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, failureAnswer);
	}

	@Override
	public void init() {
	}

	@Override
	public List<String> getWarnings() {
		return null;
	}
}
