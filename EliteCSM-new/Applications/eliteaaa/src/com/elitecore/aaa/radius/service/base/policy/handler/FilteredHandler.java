package com.elitecore.aaa.radius.service.base.policy.handler;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * Wraps a piece of business logic (another {@link RadServiceHandler}) 
 * that should be applied only when some filter is satisfied. Filtered handler
 * can work in both synchronous as well as asynchronous manner.
 * 
 * <pre>
 *           FILTERED HANDLER          
 * +-------------------------------------+
 * |                                     |
 * |      +---------+     +---------+    |
 * | REQ  |         | YES |         |    |
 * +------> FILTER  +-----> HANDLER |    |
 * |      |         |     |         |    |
 * |      +----+----+     +---------+    |
 * |           |                         |
 * +-------------------------------------+
 *             |                          
 *             v                          
 *            NO                          
 * </pre>
 * For example, only when the request contains attribute 0:31 with value starting with
 * 0A-0B, some processing should be applied. Then the filtered handler can be used with
 * filter string {@code 0:1="0A-0B*"}
 * 
 * <p><b>NOTE:</b> Filtered handler just acts as a pass-through when the filter string is {@code null}
 * or blank. 
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request
 * @param <V> type of response
 */
public class FilteredHandler<T extends RadServiceRequest, V extends RadServiceResponse>
implements AsyncRadServiceHandler<T, V>, RadServiceHandler<T, V> {
	private RadServiceHandler<T, V> handler;
	private LogicalExpressionFilter<T, V> filter;
	private final String rulesetString;
	
	/**
	 * Creates a new instance wrapping the handler provided and protecting
	 * it with the filter. Filter string can be null or blank, when this is the case
	 * then the filtered handler just acts as a pass-through.
	 * 
	 * @param rulesetString a nullable filter string
	 * @param handler any non-null handler
	 */
	public FilteredHandler(@Nullable String rulesetString, 
			@Nonnull RadServiceHandler<T, V> handler) {
		this.rulesetString = rulesetString;
		this.handler = checkNotNull(handler, "handler is null");
	}
	
	/**
	 * Returns true if the filter is satisfied and the wrapped handler is eligible, 
	 * false otherwise.
	 * 
	 * @param request the request packet
	 * @param response the response packet
	 * @return true is filter is satisfied and wrapped handler is eligible, 
	 * false otherwise
	 */
	@Override
	public boolean isEligible(T request, V response) {
		return filter.apply(request, response)
				&& handler.isEligible(request, response);
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		handler.handleRequest(request, response, session);
	}

	@Override
	public void handleAsyncRequest(T request, V response, ISession session, BroadcastResponseListener<T, V> listener) {
		AsyncRadServiceHandler<T, V> asyncHandler = (AsyncRadServiceHandler<T, V>) handler;
		asyncHandler.handleAsyncRequest(request, response, session, listener);
	}

	@Override
	public void init() throws InitializationFailedException {
		filter = LogicalExpressionFilter.create(rulesetString);
		handler.init();
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return handler.isResponseBehaviorApplicable();
	}
}