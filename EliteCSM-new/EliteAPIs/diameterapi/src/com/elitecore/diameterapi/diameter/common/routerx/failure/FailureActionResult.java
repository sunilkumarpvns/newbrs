package com.elitecore.diameterapi.diameter.common.routerx.failure;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;

/**
 * It is a wrapper class which contains elements for processing 
 * the result of Routing Failure Actions.<br />
 * Need: To maintain common semantics for Diameter Routing Failure Actions.  <br />
 * 
 * @author monica.lulla
 *
 */
public class FailureActionResult {
	
	private FailureActionResultCodes action;
	private DiameterPacket diameterPacket;
	private String peerName;

	public FailureActionResult(FailureActionResultCodes action, DiameterPacket diameterPacket) {
		this(action, diameterPacket, null);
	}
	
	public FailureActionResult(FailureActionResultCodes action, DiameterPacket diameterPacket,String peerName) {
		this.action = action;
		this.diameterPacket = diameterPacket;
		this.peerName = peerName;
	}

	/**
	 * 
	 * @return  Action to be taken after processing Diameter Routing Failure Action
	 */
	public FailureActionResultCodes getAction() {
		return action;
	}
	
	/**
	 * 
	 * @return Diameter Packet after processing Diameter Routing Failure Action
	 */
	public DiameterPacket getDiameterPacket() {
		return diameterPacket;
	}
	
	/**
	 * 
	 * @return Name of Diameter Peer to which Diameter Packet has to be sent 
	 * after processing Diameter Routing Failure Action
	 */
	public String getPeerName() {
		return peerName;
	}	
}
