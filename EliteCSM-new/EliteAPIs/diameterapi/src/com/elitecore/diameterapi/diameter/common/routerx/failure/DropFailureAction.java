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
 * It is an implementation of {@link RoutingFailureAction} , handling DROP Failure Action
 * @author monica.lulla
 *
 */
public class DropFailureAction implements RoutingFailureAction {
	
	private static final String MODULE = "DROP-FLR-ACT";
	
	public DropFailureAction(){}

	/**
	 * 
	 * This method performs Drop Failure Action for Failed Answer.
	 */
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName) {			
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.DROP + " Failure Action." +
			" for Session-ID="+ failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
			" HbH-ID=" + failureAnswer.getHop_by_hopIdentifier());
		
		return new FailureActionResult(FailureActionResultCodes.DROP, failureAnswer);
		
	}

	@Override
	public void init() {
	}

	@Override
	public List<String> getWarnings() {
		return null;
	}

}
