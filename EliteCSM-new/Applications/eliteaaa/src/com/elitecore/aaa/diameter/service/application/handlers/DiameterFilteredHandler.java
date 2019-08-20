package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class DiameterFilteredHandler implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {
	private DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler;
	private LogicalExpressionFilter filter;
	private final String rulesetString;
	
	/**
	 * Creates a new instance wrapping the handler provided and protecting
	 * it with the filter. Filter string can be null or blank, when this is the case
	 * then the filtered handler just acts as a pass-through.
	 * 
	 * @param rulesetString a nullable filter string
	 * @param handler any non-null handler
	 */
	public DiameterFilteredHandler(@Nullable String rulesetString, 
			@Nonnull DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
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
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return filter.apply(request, response)
				&& handler.isEligible(request, response);
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		handler.handleRequest(request, response, session);
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
	public void handleAsyncRequest(ApplicationRequest request, ApplicationResponse response,
			ISession session, DiameterBroadcastResponseListener listener) {
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> asyncHandler = (DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse>) handler;
		asyncHandler.handleAsyncRequest(request, response, session, listener);
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		return handler.isResponseBehaviorApplicable();
	}
}
