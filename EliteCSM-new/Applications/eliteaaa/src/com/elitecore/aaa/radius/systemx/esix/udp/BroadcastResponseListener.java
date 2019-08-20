package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.servicex.OrderedAsyncRequestExecutor;

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
public interface BroadcastResponseListener<T extends RadServiceRequest, V extends RadServiceResponse> extends RadResponseListener {
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
	public void addAsyncRequestExecutor(OrderedAsyncRequestExecutor<T, V> executor);
}
