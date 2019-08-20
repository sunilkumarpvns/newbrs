package com.elitecore.aaa.radius.service.base.policy.handler;

import java.util.Iterator;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * Represents sequence of handlers that support broadcast communication
 * 
 * @param <T> type of request packet
 * @param <V> type of response packet
 * @param <H> type of handlers
 * @see RadiusChainHandler
 * 
 * @author narendra.pathai
 * 
 */
public abstract class AsyncRadiusChainHandler<T extends RadServiceRequest,
V extends RadServiceResponse, H extends AsyncRadServiceHandler<T, V>>
extends RadiusChainHandler<T, V, H> implements AsyncRadServiceHandler<T, V> { 
	
	public AsyncRadiusChainHandler() {
		super();
	}
	
	public AsyncRadiusChainHandler(ProcessingStrategy processingStrategy) {
		super(processingStrategy);
	}
	
	@Override
	public void handleAsyncRequest(T request, V response, ISession session, BroadcastResponseListener<T, V> listener) {
		// if the processing is already completed then no point in handling request
		if (shouldContinue(request, response) == false) {
			return;
		}
		
		Iterator<H> iterator = iterator();
		saveSession(request, iterator);
		process(request, response, session, iterator, listener);
		
		/* After handling if processing is stalled due to external communication
		 * then the handler process is not yet complete so should not remove session.
		 * But if processing is completed then session must be removed.
		 */
		if (shouldContinue(request, response)) {
			getExecutor(request).removeHandlerSession();
			getExecutor(request).removeHandler();
		}
	}

	private void process(T request, V response,
			ISession session, Iterator<H> iterator,
			BroadcastResponseListener<T, V> listener) {
		while (iterator.hasNext()) {
			H next = iterator.next();
			if (next.isEligible(request, response) == false) {
				continue;
			}
			next.handleAsyncRequest(request, response, session, listener);
			if (shouldContinue(request, response) == false) {
				break;
			}
		}
	}
}
