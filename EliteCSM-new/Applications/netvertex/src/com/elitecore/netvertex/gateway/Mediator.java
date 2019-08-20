package com.elitecore.netvertex.gateway;

import java.util.Map;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * Responsible for communicating between two gateway/protocol whether it is compatible or not.
 * 
 * <p>
 * Generally Communication between two parties required following
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp; 
 * 1) <b>Receiver </b> - During communication sometimes sender just know that it need to send message to some party but doesn't know who.
 * 				  Receiver just has a hint how to reach there.
 * 				  <br/>
 * 					ex: On Receiving SNR Sy Application doesn't know who is the receiver,
 * 						it just had session-id on which anyone can fetch the session and find the receiver.
 * 	<br/>		
 * 	2) <b>Message</b> - what kind of packet receiver understand.
 * 					<br/>
 * 					ex: RADIUS gateway doesn't know hot to trigge RAR. what is the medatary parameter for RAR etc.
 * </p>
 * <br>
 * Mediator hide the complexity how to find receiver and message translation.Sender just need to say what message it need to send to receiver.
 * 
 * @author harsh
 *
 */
public interface Mediator {

	/**
	 * ReAuthorize each session found in NetVetrtex Session.
	 * 
	 *    
	 *  @param key
	 *            Logical column name on which session need to look-up
	 * @param val
	 *            value of Logical column
	 * @param reAuthCause
	 * @param forcefullReAuth
	 */
	GatewayMediator.ResultCodes reauthorize(PCRFKeyConstants pcrfKey, String value, String reAuthCause, boolean forcefullReAuth, Map<PCRFKeyConstants, String> addtionalParams);

	void disconnect(PCRFRequest pcrfRequest);

}
