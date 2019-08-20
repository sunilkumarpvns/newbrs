package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * Wraps the {@code AsyncRequestExecutor} to provide ordered interface to executors.
 * This is used in the broadcasting context when the response processing should occur
 * in the order in which the requests were forwarded. 
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public class DiameterOrderedAsyncRequestExecutor implements Comparable<DiameterOrderedAsyncRequestExecutor>, DiameterAsyncRequestExecutor {
	private final DiameterAsyncRequestExecutor executor;
	private final Integer order;

	private DiameterOrderedAsyncRequestExecutor(DiameterAsyncRequestExecutor executor, Integer order) {
		this.executor = executor;
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}

	@Override
	public void handleServiceRequest(ApplicationRequest request, ApplicationResponse response,
			ISession session) {
		executor.handleServiceRequest(request, response, session);
	}

	@Override
	public int compareTo(DiameterOrderedAsyncRequestExecutor that) {
		return this.order.compareTo(that.order);
	}
	
	/**
	 * Static factory method to create an ordered executor
	 * 
	 * @param <T> type of request packet
	 * @param <V> type of response packet
	 * @param executor a non-null executor which is to be wrapped
	 * @param order a non-null value
	 * @return a newly constructed {@code OrderedAsyncRequestExecutor} 
	 */
	public static DiameterOrderedAsyncRequestExecutor from(DiameterAsyncRequestExecutor executor, Integer order) {
		return new DiameterOrderedAsyncRequestExecutor(
				checkNotNull(executor, "executor is null"),
				checkNotNull(order, "order is null")
				);
	}
}
