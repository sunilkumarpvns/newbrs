package com.elitecore.aaa.radius.util;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * A helper class that provides methods for controlling the radius flow.
 * While doing asynchronous communication we need to remember which flags should be 
 * set, which is error prone, so this class takes care of it. Helpers for dropping, rejecting
 * the request are provided for use.
 *  
 * @author narendra.pathai
 *
 */
public class RadiusProcessHelper {

	/**
	 * Stops the execution of the present request for asynchronous communication. No more 
	 * handlers are applied and response is <b>NOT</b> sent. The processing resumes 
	 * when the request is submitted in queue on async response arrival.
	 * 
	 * @param request request that is being sent asynchronously
	 * @param response the original response
	 */
	public static void onExternalCommunication(RadServiceRequest request, RadServiceResponse response) {
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(false);
		request.stopFurtherExecution();
	}

	/**
	 * Sets various flags required to drop the response. No further processing is carried out.
	 * 
	 * @param response response to be dropped
	 */
	public static void dropResponse(RadServiceResponse response) {
		response.markForDropRequest();
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
	}

	/**
	 * Sets the packet type of response as ACCESS_REJECT and sets the flags required to reject the response. 
	 * No further processing is carried out.
	 * 
	 * @param response response to be rejected
	 * @param replyMessage the message that should be sent in the response
	 */
	public static void rejectResponse(RadServiceResponse response, String replyMessage) {
		response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
		response.setResponseMessage(replyMessage);
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
	}
}
