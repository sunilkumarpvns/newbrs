package com.elitecore.aaa.radius.service.base.policy.handler;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.OrderedAsyncRequestExecutor;

/**
 * Encapsulates some processing that may take place in an asynchronous manner.
 *
 * @param <T> type of request packet 
 * @param <V> type of response packet
 * 
 * @author narendra.pathai
 */
public interface AsyncRadServiceHandler<T extends RadServiceRequest, V extends RadServiceResponse> extends RadServiceHandler<T, V> {
	/**
	 * This method is called when the processing should take place in an asynchronous manner, typically while broadcasting.
	 * This method is called in a synchronous manner, it is up to the implementation to honor the code
	 * and do as little synchronous processing as possible and release the thread. The implementation
	 * has no need to stop the execution when request is being processed in an asynchronous manner.
	 * When doing async communication the implementation MUST register the processing, that should be carried
	 * out on receiving the response, to the {@code listener} using {@link BroadcastResponseListener#addAsyncRequestExecutor(OrderedAsyncRequestExecutor)}.
	 * Broadcasting response accumulation is done in an ordered fashion, so the implementation MUST acquire 
	 * an {@code order} from the {@code listener} to know its participation order in the request processing.
	 * The implementation MUST store that order and provide it when adding the {@code AsyncRequestExecutor} to the
	 * {@link BroadcastResponseListener}. The {@linkplain AsyncRequestExecutor} can be converted to an ordered
	 * counter-part using the {@link OrderedAsyncRequestExecutor#from(AsyncRequestExecutor, Integer)}, where the order
	 * should be provided.  
	 *   
	 * @param request the type of request
	 * @param response the type of response
	 * @param session TODO
	 * @param listener allows execution of logic on response arrival in an ordered fashion
	 */
	public void handleAsyncRequest(T request, V response, ISession session, BroadcastResponseListener<T, V> listener);
}
