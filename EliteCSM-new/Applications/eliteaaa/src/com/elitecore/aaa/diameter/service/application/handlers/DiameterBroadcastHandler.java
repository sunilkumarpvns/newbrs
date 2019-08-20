package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

public abstract class DiameterBroadcastHandler implements DiameterApplicationHandler<ApplicationRequest,ApplicationResponse> {

	public static final String HIGHEST_PRIORITY_RESULT_CODE = "HIGHEST_PRIORITY_RESULT_CODE";
	public static final String HIGHEST_PRIORITY_NEGATIVE_ANSWER = "HIGHEST_PRIORITY_NEGATIVE_ANSWER";
	
	private DiameterServiceContext serviceContext;
	protected DiameterBroadcastCommunicationHandlerData data;
	private DiameterAsyncChainHandler noWaitForResponseChain;
	private DiameterAsyncChainHandler waitForResponseFilterChain;

	public DiameterBroadcastHandler(DiameterServiceContext context,
			DiameterBroadcastCommunicationHandlerData diameterAsynchronousCommunicationHandlerData) {
		this.serviceContext = context;
		this.data = diameterAsynchronousCommunicationHandlerData;
		this.noWaitForResponseChain = new DiameterAsyncChainHandler();
		this.waitForResponseFilterChain = new DiameterAsyncChainHandler() {
		
			@Override
			protected void processOnNoHandlerEligible(ApplicationRequest request, ApplicationResponse response) {
				
				DiameterBroadcastHandler.this.processOnNoHandlerEligible(request, response);
	}
		};
	}

	protected abstract void processOnNoHandlerEligible(ApplicationRequest request, ApplicationResponse response);
	
	@Override
	public void init() throws InitializationFailedException {

		for (DiameterBroadcastCommunicationEntryData entryData : data.getEntries()) {
			
			DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> proxyCommunicationHandler = newProxyCommunicationHandler(entryData);
			
			if (entryData.isWait()) {
				DiameterWaitBroadcastFilteredHandler waitFilteredHandler = new DiameterWaitBroadcastFilteredHandler(entryData.getRuleset(), proxyCommunicationHandler);
				waitFilteredHandler.init();
				proxyCommunicationHandler.init();
				waitForResponseFilterChain.addHandler(waitFilteredHandler);
			} else {
				DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(entryData.getRuleset(), proxyCommunicationHandler);
				filteredHandler.init();
				noWaitForResponseChain.addHandler(filteredHandler);
			}
		}
	}

	protected abstract DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> newProxyCommunicationHandler(DiameterBroadcastCommunicationEntryData entryData);

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (waitForResponseFilterChain.size() > 0) {
			noWaitForResponseChain.handleAsyncRequest(request, response, 
					session, new NoWaitBroadcastResponseListener());
			
			WaitBroadcastResponseListener waitResponseListener = new WaitBroadcastResponseListener(request, response);
			waitForResponseFilterChain.handleAsyncRequest(request, response, 
					session, waitResponseListener);
			
			waitResponseListener.getEligibleChain().handleAsyncRequest(request, response, session, waitResponseListener);
		} else {
			serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new RequestBroadcastingTask(request, response, session));
		}
	}

	class WaitBroadcastResponseListener implements DiameterBroadcastResponseListener {

		private static final String MODULE = "WAIT-BROADCAST-RES-LSTNR";
		
		private DiameterAsyncChainHandler eligibleChain = new DiameterAsyncChainHandler();
		
		@GuardedBy("this")
		private List<DiameterOrderedAsyncRequestExecutor> executors 
			= new ArrayList<DiameterOrderedAsyncRequestExecutor>();
		
		private int nextOrder;
		private AtomicInteger receivedCount = new AtomicInteger();

		private final ApplicationRequest originalRequest;

		private final ApplicationResponse originalResponse;

		public WaitBroadcastResponseListener(ApplicationRequest request, 
				ApplicationResponse response) {
			this.originalRequest = request;
			this.originalResponse = response;
		}

		@Override
		public int getNextOrder() {
			return nextOrder++;
		}

		public DiameterAsyncChainHandler getEligibleChain() {
			return eligibleChain;
		}

		@GuardedBy("this")
		@Override
		public synchronized void addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor executor) {
			executors.add(executor);
		}

		@Override
		public void responseReceived(final DiameterRequest remoteRequest, final DiameterAnswer remoteAnswer,
				final ISession session) {

			if (receivedCount.incrementAndGet() == eligibleChain.size()) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE,
							"Response from all external systems received, Proceeding for further execution");
				}

//				serviceContext.resumeRequestInAsync((Session)session, originalRequest, originalResponse,
//						new AsyncRequestExecutor<ApplicationRequest, ApplicationResponse>() {
//
//					@Override
//					public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse) {
//						triggerOrderedCallbacks(originalRequest, originalResponse);
//					}
//				});
				
				serviceContext.resumeRequestInAsync((Session)session, originalRequest, originalResponse,
						new DiameterAsyncRequestExecutor() {
							
							@Override
							public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
									ISession session) {
								triggerOrderedCallbacks(originalRequest, originalResponse, session);
							}
						});
			}
		}

		private void triggerOrderedCallbacks(ApplicationRequest applicationRequest,
				ApplicationResponse applicationResponse, ISession session) {
			Collections.sort(executors);
			
			applicationResponse.setFurtherProcessingRequired(true);
			applicationResponse.setProcessingCompleted(true);
			
			for (int i = 0; i < executors.size(); i++) {
				executors.get(i).handleServiceRequest(applicationRequest, applicationResponse, session);
				if (originalResponse.isFurtherProcessingRequired() == false) {
					break;
				}
			}
			
			if (applicationResponse.getParameter(HIGHEST_PRIORITY_RESULT_CODE) != null) {
				int resultCode = (Integer) applicationResponse.getParameter("HIGHEST_PRIORITY_RESULT_CODE");
				DiameterAnswer negativeAnswer = (DiameterAnswer) applicationResponse.getParameter(HIGHEST_PRIORITY_NEGATIVE_ANSWER);
				
				if (negativeAnswer != null && 
						negativeAnswer.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger() == resultCode) {
					negativeAnswer.setHeaderFrom(originalRequest.getDiameterRequest());
					originalResponse.getDiameterAnswer().setBytes(negativeAnswer.getBytes());
					if (ResultCodeCategory.getResultCodeCategory(negativeAnswer.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()) == ResultCodeCategory.RC3XXX 
							|| negativeAnswer.isError()) {
						originalResponse.getDiameterAnswer().setErrorBit();
					}
				}
			}
			
			cleanupTemporaryParameters();
		}
		
		/*
		 * Parameters HIGHEST_PRIORITY_RESULT_CODE & HIGHEST_PRIORITY_NEGATIVE_ANSWER
		 * are added from DiaToDiaProxyCommunicationHandler in case a Broadcast
		 * handler is present. Removing these parameters here so that if there are multiple 
		 * broadcast handlers in one command code flow then their corresponding parameters
		 * don't affect each other.
		 * */
		private void cleanupTemporaryParameters() {
			originalResponse.getDiameterAnswer().removeParameter(HIGHEST_PRIORITY_RESULT_CODE);
			originalResponse.getDiameterAnswer().removeParameter(HIGHEST_PRIORITY_NEGATIVE_ANSWER);
		}

		@Override
		public void addHandler(DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
			eligibleChain.addHandler(handler);
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		// no op
	}

	/**
	 * In broadcasting context, if we have some entries with wait for response flag true, then 
	 * this filtered handler is used. So that if any of the filters is selected then it will
	 * halt the request execution.
	 * 
	 * <p>Wait for response is used when the response from the external system is to be aggregated,
	 * to achieve that we need to halt the further request processing.
	 * 
	 * @author narendra.pathai
	 */
	class DiameterWaitBroadcastFilteredHandler implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {
		private LogicalExpressionFilter filter;
		private final String rulesetString;
		private DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler;
		
		public DiameterWaitBroadcastFilteredHandler(String rulesetString,
				DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
			this.rulesetString = rulesetString;
			this.handler = handler;
		}
		
		@Override
		public void handleAsyncRequest(ApplicationRequest request, ApplicationResponse response,
				ISession session, DiameterBroadcastResponseListener listener) {
			
			DiameterProcessHelper.onExternalCommunication(request, response);

			listener.addHandler(handler);
		}

		@Override
		public void init() throws InitializationFailedException {
			filter = LogicalExpressionFilter.create(rulesetString);
		}

		@Override
		public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
			return filter.apply(request, response);
		}

		@Override
		public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
			throw new UnsupportedOperationException("Should not be called.");
		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return handler.isResponseBehaviorApplicable();
		}
	}
	
	class NoWaitBroadcastResponseListener implements DiameterBroadcastResponseListener {
		private static final String MODULE = "NO-WAIT-BROADCAST-RES-LSTNR";

		@Override
		public int getNextOrder() {
			return 0; //no need for maintaining ordering here
		}

		@Override
		public void addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor executor) {
			// no op
			
		}

		@Override
		public void responseReceived(DiameterRequest remoteRequest, DiameterAnswer remoteAnswer,
				ISession session) {
			
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Broadcast rreesponse received with Session-ID=" 
						+ remoteAnswer.getSessionID());
			}
			
		}

		@Override
		public void addHandler(DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
			
		}
	}
	
	/**
	 * Task that broadcasts the request to systems which have wait for response as false
	 */
	class RequestBroadcastingTask extends BaseSingleExecutionAsyncTask {
		
		private final ApplicationRequest request;
		private final ApplicationResponse response;
		private final ISession session;

		public RequestBroadcastingTask(ApplicationRequest request, ApplicationResponse response, ISession session) {
			this.request = request;
			this.response = response;
			this.session = session;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			noWaitForResponseChain.handleAsyncRequest(request, response,
					session, new NoWaitBroadcastResponseListener());
		}
	}
	

	@Override
	public boolean isResponseBehaviorApplicable() {
		return waitForResponseFilterChain.isResponseBehaviorApplicable();
	}
}
