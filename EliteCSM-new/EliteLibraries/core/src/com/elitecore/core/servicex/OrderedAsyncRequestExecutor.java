package com.elitecore.core.servicex;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

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
public class OrderedAsyncRequestExecutor<T extends ServiceRequest, V extends ServiceResponse> implements Comparable<OrderedAsyncRequestExecutor<T, V>>, AsyncRequestExecutor<T, V> {
	private final AsyncRequestExecutor<T, V> executor;
	private final Integer order;

	private OrderedAsyncRequestExecutor(AsyncRequestExecutor<T, V> executor, Integer order) {
		this.executor = executor;
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}

	@Override
	public void handleServiceRequest(T serviceRequest, V serviceResponse) {
		executor.handleServiceRequest(serviceRequest, serviceResponse);
	}

	@Override
	public int compareTo(OrderedAsyncRequestExecutor<T, V> that) {
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
	public static <T extends ServiceRequest, V extends ServiceResponse> OrderedAsyncRequestExecutor<T, V> from(AsyncRequestExecutor<T, V> executor, Integer order) {
		return new OrderedAsyncRequestExecutor<T, V>(
				checkNotNull(executor, "executor is null"),
				checkNotNull(order, "order is null")
				);
	}
}
