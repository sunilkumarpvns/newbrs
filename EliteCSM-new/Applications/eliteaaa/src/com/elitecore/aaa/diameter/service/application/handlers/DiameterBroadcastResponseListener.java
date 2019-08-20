package com.elitecore.aaa.diameter.service.application.handlers;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

/**
* A callback that is used in the broadcasting context to listen for the incoming responses
* of the requests sent in a broadcast manner. The implementation should call the executors
* registered in the order of request forwarded.
* <br/><br/>
* The listener provides with the next order for the request being forwarded, this order needs
* to be provided to the {@code OrderedAsyncRequestExecutor#from(AsyncRequestExecutor, Integer)}
* method for being able to process the request executors in proper order.
* 
* @author narendra.pathai
*
*/
public interface DiameterBroadcastResponseListener {

	boolean ASYNC_EXECUTION = true;
	boolean SYNC_EXECUTION = false;
	/**
	 * Provides the request forwarding order
	 * 
	 * @return request forwarding order
	 */
	public int getNextOrder();
	/**
	 * This method is used to register the ordered executor that should be applied
	 * when the response is received.
	 *  
	 * @param executor a non-null ordered executor
	 */
	public void addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor executor);

	/**
	 * 
	 * @param remoteRequest may be null if request could not be formed.
	 * @param remoteAnswer
	 */
	public void responseReceived(@Nullable DiameterRequest remoteRequest, DiameterAnswer remoteAnswer, 
			ISession session);

	// TODO please javadoc for the sake of others
	void addHandler(DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler);
	
}
