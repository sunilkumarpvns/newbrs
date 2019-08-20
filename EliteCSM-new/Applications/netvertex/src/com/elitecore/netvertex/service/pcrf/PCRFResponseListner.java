package com.elitecore.netvertex.service.pcrf;
/**
 * This interface is used as callback, whenever PCRF Service will be ready with PCRFResponse Packet it will response back to the 
 * caller using PCRFResponseListner.
 * 
 * @author Subhash Punani
 *
 */
public interface PCRFResponseListner {
	/**
	 * Called when PCRFService will be ready with PCRFResponse
	 * @param response
	 */
	void responseReceived(PCRFResponse response);
}
