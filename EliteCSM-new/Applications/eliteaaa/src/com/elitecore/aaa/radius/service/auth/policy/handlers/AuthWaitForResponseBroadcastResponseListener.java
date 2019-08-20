package com.elitecore.aaa.radius.service.auth.policy.handlers;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler.ProcessingStrategy;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.OrderedAsyncRequestExecutor;

public class AuthWaitForResponseBroadcastResponseListener implements BroadcastResponseListener<RadAuthRequest, RadAuthResponse> {
	private static final String MODULE = "AUTH-BROADCAST-WAIT-RES-LSTNR";
	private final RadAuthServiceContext serviceContext;
	@GuardedBy("request")
	private int receivedCount = 0;
	private List<OrderedAsyncRequestExecutor<RadAuthRequest, RadAuthResponse>> executors = Collectionz.newArrayList();
	private final RadAuthResponse response;
	private final RadAuthRequest request;
	/*
	 * This value is used in two ways:
	 * 1) Firstly the broadcasting response aggregation should occur in the order in which the request
	 * was forwarded, so ordering is important. For this order is used by external communication handler
	 * to get the next order.
	 * 2) Secondly in case of wait broadcasting, the aggregation of only the selected communication entries
	 * is to be done, because the entry is protected by filter. If out of 5 entries, only 1 filter is satisfied
	 * then aggregation of only single response should occur. So for that count too, this order is used.
	 */
	private int order = 0; 
	
	public AuthWaitForResponseBroadcastResponseListener(RadAuthServiceContext serviceContext,
			RadAuthRequest request, RadAuthResponse response) {
		this.serviceContext = serviceContext;
		this.request = request;
		this.response = response;
	}

	/*
	 * Any logic inside this method needs to take care of holding appropriate locks
	 * as this method is called in parallel by listener threads. So adding any logic
	 * without locks will make it non thread-safe
	 */
	@Override
	public void responseReceived(RadUDPRequest radUDPRequest, RadUDPResponse radUDPResponse, ISession session) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Response received: " + radUDPResponse);
		}
		onResponse();
	}

	/*
	 * Any logic inside this method needs to take care of holding appropriate locks
	 * as this method is called in parallel by listener threads. So adding any logic
	 * without locks will make it non thread-safe
	 */
	@Override
	public void requestDropped(RadUDPRequest radUDPRequest) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Response dropped for request: " + radUDPRequest);
		}
		onResponse();
	}

	/*
	 * Any logic inside this method needs to take care of holding appropriate locks
	 * as this method is called in parallel by listener threads. So adding any logic
	 * without locks will make it non thread-safe
	 */
	@Override
	public void requestTimeout(RadUDPRequest radUDPRequest) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Broadcast Request timedout for request: " + radUDPRequest);
		}
		onResponse();
	}

	@GuardedBy("request")
	private void onResponse() {
		/**
		 * This logic needs to be protected from race condition due to the following reason:
		 * Suppose there are 4 entries in broadcast communication with different ruleset, if
		 * the first ruleset is satisfied and request is forwarded to external system. In this
		 * state we will have order with value 1, which means we have forwarded one request. Now
		 * suppose we don't lock on request and response from external system is received before
		 * completion of broadcast handler. Now as we have sent 1 request and response is received
		 * for 1, the logic will feel that response from all external systems has been received and
		 * we will move for further execution. 
		 * 
		 * But this is not a valid situation, we need to wait for whole broadcast handler processing 
		 * to complete before we can proceed. So we are acquiring lock on request before processing.
		 * This logic is called by response listener thread. And the request handler thread acquires
		 * lock before handling request. So we get mutual exclusion. 
		 */
		synchronized (request) {
			receivedCount++;
			if (receivedCount == order) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE,
					"Response from all external systems received, Proceeding for further execution");
				}
				serviceContext.submitAsyncRequest(request, response, new BroadcastAsyncRequestExecutor());
			}
		}
	}

	/*
	 * This method needs to be protected using lock on "this" because if we have
	 * forwarded two requests with wait for response as true, and response for both
	 * the requests is received at same time; then two listener threads will simultaneously
	 * call this method which can lead to problems as ArrayList is not thread safe.
	 * So we need to guard access to ArrayList using "this".
	 */
	@Override
	@GuardedBy("this")
	public synchronized void addAsyncRequestExecutor(OrderedAsyncRequestExecutor<RadAuthRequest, RadAuthResponse> executor) {
		executors.add(executor);
	}

	/*
	 * This method is never called in parallel by different threads 
	 * but have put synchronized block for safe side
	 */
	@Override
	@GuardedBy("this")
	public synchronized int getNextOrder() {
		return ++order;
	}

	class BroadcastAsyncRequestExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			Collections.sort(executors);
			ProcessingStrategy processingStrategy = new RadiusChainHandler.DefaultProcessingStrategy();
			for (AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> executor : executors) {
				executor.handleServiceRequest(serviceRequest, serviceResponse);
				if (processingStrategy.shouldContinue(serviceRequest, serviceResponse) == false) {
					break;
				}
			}
		}
	}
}