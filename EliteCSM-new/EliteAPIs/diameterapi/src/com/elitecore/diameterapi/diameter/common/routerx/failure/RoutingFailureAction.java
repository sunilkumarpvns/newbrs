package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
/**
 * This handles the processing of Failure Actions of Routing Entry
 * 
 * @author monica.lulla
 *
 */
public interface RoutingFailureAction {
	
	/**
	 * This method processes the Failure Action Configured in Routing Entry.
	 * 
	 * @param failureAnswer is the Diameter Answer containing Error Result - code of a Routing Entry 
	 * 						that has a Failure Configuration for this Error code.
	 * 
	 * @param routingSession
	 * 
	 * @param originRequest is the respective Request of failureAnswer
	 * 
	 * @param originPeerName is Name of Diameter Peer from which we received failureAnswer
	 * 
	 * @return Result of Failure Action 
	 * 
	 * @see {@link FailureActionResult}
	 */
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName);
	
	/**
	 * Initialization of Failure Arguments.
	 * 
	 */
	public void init();

	public List<String> getWarnings();

	

	
	
}
