package com.elitecore.aaa.radius.service.base.policy.handler;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.plugins.script.ExternalRadiusScript;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctBroadcastHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthBroadcastHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.BroadcastResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponseImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommGroupImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPRequestImpl;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.OrderedAsyncRequestExecutor;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * Provides the support for all the handlers that need to do external communication.
 * This class is responsible for application of external communication level Groovy
 * scripts and translation mappings.
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public abstract class ExternalCommunicationHandler<T extends RadServiceRequest, V extends RadServiceResponse> implements AsyncRadServiceHandler<T, V> {
	private final RadServiceContext<T, V> serviceContext;
	private final ExternalCommunicationEntryData data;
	private RadUDPCommGroup group;
	private final CommunicatorExceptionPolicy exceptionPolicy;

	public ExternalCommunicationHandler(RadServiceContext<T, V> serviceContext, ExternalCommunicationEntryData data) {
		this(serviceContext, data, CommunicatorExceptionPolicy.ABORT);
	}

	public ExternalCommunicationHandler(RadServiceContext<T, V> serviceContext, ExternalCommunicationEntryData data, CommunicatorExceptionPolicy exceptionPolicy) {
		this.serviceContext = serviceContext;
		this.data = data;
		this.exceptionPolicy = exceptionPolicy;
	}

	@Override
	public void init() throws InitializationFailedException {
		group = createGroup();
	}

	protected RadUDPCommGroup createGroup() throws InitializationFailedException {

		group = new RadUDPCommGroupImpl(serviceContext.getServerContext());

		for (CommunicatorData serverData : data.getCommunicatorGroupData().getCommunicatorDataList()) {
			String esId = serverData.getId();
			Optional<DefaultExternalSystemData> udpES = serviceContext.getServerContext().getServerConfiguration().getRadESConfiguration().getESData(String.valueOf(esId));
			try {
				if (udpES.isPresent()) {
					UDPCommunicator esCommunicator = serviceContext.getServerContext().getRadUDPCommunicatorManager()
							.findCommunicatorByIDOrCreate(esId, serviceContext.getServerContext(), udpES.get());
					group.addCommunicator(esCommunicator, serverData.getLoadFactor());

				} else {
					exceptionPolicy.communicatorDetailsNotFound(esId, getModule());
				}
			} catch (InitializationFailedException e) {
				exceptionPolicy.exception(esId, e, getModule());
			} 
		}

		return group;
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		return group.isAlive() == false;
	}

	@Override
	public final void handleRequest(T request, V response, ISession session) {
		handlePreRequest(request, response);
		
		RadiusProcessHelper.onExternalCommunication(request, response);
		
		TranslationHelper translationHelper = new TranslationHelper(data.getTranslationMapping(), 
				request, response, includeInfoAttributes());
		RadiusPacket translatedRequest = translationHelper.translate();
		
		if (translationHelper.isDummyMappingApplicable()) {
			byte[] responseBytes = translationHelper.getDummyResponseBytes();

			RadUDPRequestImpl udpRequest = new RadUDPRequestImpl(request.getRequestBytes(), 
					response.getClientData().getSharedSecret(request.getPacketType()), 
					null);

			RadUDPResponseImpl udpResponse = new RadUDPResponseImpl(responseBytes, "dummy esi");
			udpRequest.setResponse(udpResponse);
			udpResponse.getRadiusPacket();
			new DummyResponseListener(request, response).responseReceived(udpRequest, udpResponse, session);
			return;
		}


		if(Strings.isNullOrBlank(data.getScript()) == false){
			try{
				serviceContext.getServerContext()
				.getExternalScriptsManager()
				.execute(data.getScript(), ExternalRadiusScript.class, 
						"preRequest", 
						new Class<?>[]{RadServiceRequest.class, RadiusPacket.class},
						new Object[]{request, translatedRequest});
			}catch (Throwable ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(getModule(), "Error in executing UDP script: " + data.getScript() + ". Reason: " + ex.getMessage());

				LogManager.getLogger().trace(ex);
			}
		}

		group.handleRequest(translatedRequest.getBytes(includeInfoAttributes()),
				response.getClientData().getSharedSecret(request.getPacketType()),
				new ResponseListener(request, response, translationHelper), session);
	}

	@Override
	public final void handleAsyncRequest(T request, V response, ISession session, BroadcastResponseListener<T, V> listener) {
		handlePreRequest(request, response);

		TranslationHelper translationHelper = new TranslationHelper(data.getTranslationMapping(), 
				request, response, includeInfoAttributes());
		RadiusPacket translatedRequest = translationHelper.translate();
		
		if (translationHelper.isDummyMappingApplicable()) {
			byte[] responseBytes = translationHelper.getDummyResponseBytes();

			RadUDPRequestImpl udpRequest = new RadUDPRequestImpl(request.getRequestBytes(), 
					response.getClientData().getSharedSecret(request.getPacketType()), 
					null);

			RadUDPResponseImpl udpResponse = new RadUDPResponseImpl(responseBytes, "dummy esi");
			udpRequest.setResponse(udpResponse);
			udpResponse.getRadiusPacket();

			new DummyBroadcastResponseListenerInternal(listener.getNextOrder(), listener)
			.responseReceived(udpRequest, udpResponse, session);
			
			return;
		}


		if (Strings.isNullOrBlank(data.getScript()) == false) {
			try {
				serviceContext.getServerContext()
				.getExternalScriptsManager()
				.execute(data.getScript(), ExternalRadiusScript.class,
						"preRequest", 
						new Class<?>[]{RadServiceRequest.class, RadiusPacket.class},
						new Object[]{request, translatedRequest});
				
			} catch (Throwable ex) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(getModule(), "Error in executing UDP script: " + data.getScript() + ". Reason: " + ex.getMessage());
				}

				LogManager.getLogger().trace(ex);
			}
		}

		group.handleRequest(translatedRequest.getBytes(includeInfoAttributes()),
				response.getClientData().getSharedSecret(request.getPacketType()),
				new BroadcastResponseListenerInternal(listener.getNextOrder(),
						listener, translationHelper), session);
	}

	protected ExternalCommunicationEntryData getData() {
		return data;
	}

	protected RadServiceContext<T, V> getServiceContext() {
		return serviceContext;
	}

	protected abstract String getModule();
	protected abstract boolean includeInfoAttributes();
	/**
	 * The executor that should be used when response is received successfully from 
	 * the external system
	 * 
	 * @param remoteRequest the remote request sent
	 * @param remoteResponse the remote response received
	 * @return a non-null executor
	 */
	protected abstract AsyncRequestExecutor<T, V> newResponseReceivedExecutor(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse);

	protected void handlePreRequest(T request, V response) {
		//no-op
	}

	/**
	 * The executor that should be used when request is times out. Default behavior
	 * is that the request is dropped if accept on timeout is false.
	 * 
	 * @param remoteRequest the remote request sent
	 * @return a non-null executor
	 */
	protected AsyncRequestExecutor<T, V> newRequestTimeoutExecutor(RadUDPRequest remoteRequest) {
		return new RequestTimeoutExecutor();
	}

	/**
	 * The executor that should be used when request is dropped. Default behavior
	 * is that the request is dropped.
	 * 
	 * @param remoteRequest the remote request sent
	 * @return a non-null executor
	 */
	protected AsyncRequestExecutor<T, V> newRequestDroppedExecutor(RadUDPRequest remoteRequest) {
		return new RequestDroppedExecutor();
	}

	/**
	 * Specifies what policy to use when some communicator fails to initialize or the detail of
	 * the communicator is not found.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public interface CommunicatorExceptionPolicy {
		public static final ContinuePolicy CONTINUE = new ContinuePolicy();
		public static final AbortPolicy ABORT = new AbortPolicy();

		/**
		 * This method is called when some exception occurs while initializing the communicator.
		 * 
		 * @param esiId non-null id of communicator which failed to initialize
		 * @param e the occurred exception 
		 * @throws InitializationFailedException if the policy fails
		 */
		public void exception(@Nonnull String esiId,
				@Nonnull InitializationFailedException e,
				@Nonnull String module) throws InitializationFailedException;

		/**
		 * This method is called when details for some communicator are not found in system. 
		 * This can occur due to some configuration issue in communicator
		 *  
		 * @param esId the id of communicator for which details were not found
		 * @throws InitializationFailedException if policy fails
		 */
		public void communicatorDetailsNotFound(String esId, @Nonnull String module)
		throws InitializationFailedException;
	}

	/**
	 * Will continue further initialization if some communicator fails to initialize
	 * 
	 * @author narendra.pathai
	 *
	 */
	private static class ContinuePolicy implements CommunicatorExceptionPolicy {
		@Override
		public void exception(String esiId, InitializationFailedException e, String module) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(module, "Error in initializing External Communicator with id: " + esiId
						+ ". Reason: " + e.getMessage() + ", Ignoring that communicator");
				LogManager.getLogger().trace(module, e);
			}
		}

		@Override
		public void communicatorDetailsNotFound(String esId, String module)
				throws InitializationFailedException {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(module, "Error in initializing External Communicator with id: " + esId
						+ ". Reason: Configuration not found, Ignoring that communicator");
			}
		}
	}

	/**
	 * Re-throws exception occurred and stops further initialization
	 * 
	 * @author narendra.pathai
	 *
	 */
	private static class AbortPolicy implements CommunicatorExceptionPolicy {
		
		@Override
		public void exception(String esiId, InitializationFailedException e, String module) throws InitializationFailedException {
			throw e;
		}

		@Override
		public void communicatorDetailsNotFound(String esId, String module)
				throws InitializationFailedException {
			throw new InitializationFailedException("Error in initializing communicator with id: " + esId
					+ ". Reason: Configuration not found");
		}
	}

	/**
	 * Will ignore exceptions until the last one, at least one communicator must be successful.
	 * 
	 * If the group is of n communicators, n-1 exceptions will be ignored.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public static class MaximumFailuresPolicy implements CommunicatorExceptionPolicy {

		private int n;

		/**
		 * Will create policy for n communicators, and ignore n-1 exceptions. At least
		 * one communicator in the group MUST be initialized.
		 * 
		 * @param n number of communicators
		 */
		public MaximumFailuresPolicy(int n) {
			this.n = n;
		}

		@Override
		public void exception(String esId, InitializationFailedException e, String module) throws InitializationFailedException {
			if (--n == 0) {
				CommunicatorExceptionPolicy.ABORT.exception(esId, e, module);
			} else {
				CommunicatorExceptionPolicy.CONTINUE.exception(esId, e, module);
			}
		}

		@Override
		public void communicatorDetailsNotFound(String esId, String module)
				throws InitializationFailedException {
			if (--n == 0) {
				CommunicatorExceptionPolicy.ABORT.communicatorDetailsNotFound(esId, module);
			} else {
				CommunicatorExceptionPolicy.CONTINUE.communicatorDetailsNotFound(esId, module);
			}
		}
	}

	private class ResponseListener implements RadResponseListener {
		private final T request;
		private final V response;
		private @Nonnull TranslationHelper translationHelper;

		public ResponseListener(@Nonnull T request, @Nonnull V response,
				@Nonnull TranslationHelper translationHelper) {
			this.request = request;
			this.response = response;
			this.translationHelper = translationHelper;
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Unable to forward request to target system");
			}

			getServiceContext().submitAsyncRequest(request, response, newRequestDroppedExecutor(radUDPRequest));
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Response received from Proxy Server: " + radUDPResponse);
			}

			getServiceContext().submitAsyncRequest(request, response, wrap(radUDPRequest, radUDPResponse, 
					newResponseReceivedExecutor(radUDPRequest, radUDPResponse), translationHelper));
		}
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Request Timeout Response Received");
			}

			getServiceContext().submitAsyncRequest(request, response, newRequestTimeoutExecutor(radUDPRequest));
		}

	}
	
	class BroadcastResponseListenerInternal implements RadResponseListener {
		private final int order;
		private @Nonnull final BroadcastResponseListener<T, V> listener;
		private @Nonnull final TranslationHelper translationHelper;

		public BroadcastResponseListenerInternal(
				int order, 
				@Nonnull BroadcastResponseListener<T, V> listener,
				@Nonnull TranslationHelper translationHelper) {
			this.order = order;
			this.listener = listener;
			this.translationHelper = translationHelper;
		}
		
		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,
				RadUDPResponse radUDPResponse, ISession session) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Response received from Proxy Server: " + radUDPResponse);
			}
			
			listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
					wrap(radUDPRequest, radUDPResponse, 
							newResponseReceivedExecutor(radUDPRequest, radUDPResponse),
								translationHelper),
							order));
			listener.responseReceived(radUDPRequest, radUDPResponse, session);
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Unable to forward request to target system");
			}

			listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
					newRequestDroppedExecutor(radUDPRequest), order));
			listener.requestDropped(radUDPRequest);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Request Timeout Response Received");
			}
			
			listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
					newRequestTimeoutExecutor(radUDPRequest), order));
			listener.requestTimeout(radUDPRequest);
		}
		
	}
	
	private class DummyResponseListener implements RadResponseListener {
		private @Nonnull final T request;
		private @Nonnull final V response;

		public DummyResponseListener(@Nonnull T request, @Nonnull V response) {
			this.request = request;
			this.response = response;
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Unable to forward request to target system");
			}

			getServiceContext().submitAsyncRequest(request, response, newRequestDroppedExecutor(radUDPRequest));
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest, RadUDPResponse radUDPResponse, ISession session) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Response received from Proxy Server: " + radUDPResponse);
			}

			getServiceContext().submitAsyncRequest(request, response, newResponseReceivedExecutor(radUDPRequest, radUDPResponse));
		}
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Request Timeout Response Received");
			}

			getServiceContext().submitAsyncRequest(request, response, newRequestTimeoutExecutor(radUDPRequest));
		}
	}
	
	/**
	 * Response aggregation process should occur only once, when the request handling 
	 * thread has broadcasted request to all the eligible systems. So for that the
	 * request handler thread holds lock on request to achieve mutual exclusion.
	 * So when response arrives from external system; before request is forwarded
	 * to all eligible broadcast entities; using some other thread, it 
	 * will not be able to acquire lock as request handling is not yet complete.
	 * 
	 * <p>But in case of dummy response mapping we cannot send response using same thread
	 * because the java monitor object is reentrant, so the same thread will be able 
	 * to acquire lock again. If that occurs then aggregation logic will not be mutually
	 * exclusive and logic can fail due to that. Please refer {@link AuthBroadcastHandler}
	 * and {@link AcctBroadcastHandler} for aggregation logic. So we are using single execution
	 * task so that the thread for sending response changes.
	 * 
	 * @author narendra.pathai
	 */
	class DummyBroadcastResponseListenerInternal implements RadResponseListener {
		private final int order;
		private final BroadcastResponseListener<T, V> listener;

		public DummyBroadcastResponseListenerInternal(
				int order, 
				@Nonnull BroadcastResponseListener<T, V> listener) {
			this.order = order;
			this.listener = checkNotNull(listener, "listener is null");
		}
		
		@Override
		public void responseReceived(final RadUDPRequest radUDPRequest,
				final RadUDPResponse radUDPResponse, final ISession session) {
			serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new BaseSingleExecutionAsyncTask() {
				
				@Override
				public void execute(AsyncTaskContext context) {
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(getModule(), "Response received from Proxy Server: " + radUDPResponse);
					}
					
					listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
							newResponseReceivedExecutor(radUDPRequest, radUDPResponse), order));
					listener.responseReceived(radUDPRequest, radUDPResponse, session);
				}
			});
		}

		@Override
		public void requestDropped(final RadUDPRequest radUDPRequest) {
			serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new BaseSingleExecutionAsyncTask() {
				
				@Override
				public void execute(AsyncTaskContext context) {
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(getModule(), "Unable to forward request to target system");
					}
					
					listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
							newRequestDroppedExecutor(radUDPRequest), order));
					listener.requestDropped(radUDPRequest);
				}
			});
		}

		@Override
		public void requestTimeout(final RadUDPRequest radUDPRequest) {
			serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new BaseSingleExecutionAsyncTask() {
				
				@Override
				public void execute(AsyncTaskContext context) {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(getModule(), "Request Timeout Response Received");
					}
					
					listener.addAsyncRequestExecutor(OrderedAsyncRequestExecutor.from(
							newRequestTimeoutExecutor(radUDPRequest), order));
					listener.requestTimeout(radUDPRequest);
				}
			});
		}
	}
	
	/**
	 * Wrap the executor into our executor which applied response time Groovy script and
	 * translation mapping and then calls the wrapped executor. This is necessary as the
	 * groovy script and the translation mappings should be applied in the order in which 
	 * the request was forwarded.
	 *   
	 */
	private AsyncRequestExecutor<T, V> wrap(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse,
			AsyncRequestExecutor<T, V> executor, TranslationHelper translationHelper) {
		return new ExternalCommunicationAsyncRequestExecutor(remoteRequest, remoteResponse, executor, translationHelper);
	}

	/**
	 * Wrap the executor to provide groovy script and translation mapping support
	 * 
	 * @author narendra.pathai
	 */
	private class ExternalCommunicationAsyncRequestExecutor implements AsyncRequestExecutor<T, V> {

		private @Nonnull final AsyncRequestExecutor<T, V> executor;
		private @Nonnull final RadUDPRequest remoteRequest;
		private @Nonnull final RadUDPResponse remoteResponse;
		private @Nonnull final TranslationHelper translationHelper;

		public ExternalCommunicationAsyncRequestExecutor(
				@Nonnull RadUDPRequest remoteRequest,
				@Nonnull RadUDPResponse remoteResponse, 
				@Nonnull AsyncRequestExecutor<T, V> executor,
				@Nonnull TranslationHelper translationHelper) {
			this.remoteRequest = remoteRequest;
			this.remoteResponse = remoteResponse;
			this.executor = executor;
			this.translationHelper = translationHelper;
		}

		@Override
		public void handleServiceRequest(T serviceRequest, V serviceResponse) {
			
			RadiusPacket translatedResponse = (RadiusPacket) remoteResponse.getRadiusPacket();
			/** set remoteRequest(RHS side AAA) 's shared secret used in radius translator's translate response method */
			byte[] responseBytes = translationHelper.getResponseBytes(translatedResponse,(RadiusPacket)remoteRequest.getRadiusPacket(),remoteRequest.getSharedSecret());
			if(responseBytes!=null){
				translatedResponse.setBytes(responseBytes);
			}

			//calling the script
			if(Strings.isNullOrBlank(data.getScript()) == false){
				try{
					serviceContext.getServerContext().getExternalScriptsManager().execute(data.getScript(),
							ExternalRadiusScript.class, "postRequest", 
							new Class<?>[]{RadServiceRequest.class,RadiusPacket.class,RadServiceResponse.class,RadiusPacket.class}, 
							new Object[]{serviceRequest,remoteRequest.getRadiusPacket(),serviceResponse,translatedResponse});
				}catch (Throwable ex) {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(getModule(), "Error in executing UDP script" + data.getScript() + ". Reason: " + ex.getMessage());

					LogManager.getLogger().trace(ex);
				}
			}

			copySatisfiedESIAttribute(serviceResponse);
			
			executor.handleServiceRequest(serviceRequest, serviceResponse);
		}

		/* copies ELITE-SATISFIED-ESI attribute from remote response to service response
		 * and removes it from remote response so that aggregation logic does not copy it 
		 * twice.
		 */
		private void copySatisfiedESIAttribute(RadServiceResponse serviceResponse) {
			RadiusPacket remoteRadiusPacket = (RadiusPacket) remoteResponse.getRadiusPacket();
			
			IRadiusAttribute satisfiedESIAttribute = 
				remoteRadiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, true,
					RadiusAttributeConstants.ELITE_SATISFIED_ESI);
			
			// somebody can delete it from script so have to be defensive
			if (satisfiedESIAttribute == null) {
				return;			
			}
			
			serviceResponse.addInfoAttribute(satisfiedESIAttribute);
			remoteRadiusPacket.removeInfoAttribute(satisfiedESIAttribute);
		}
	}

	private class RequestDroppedExecutor implements AsyncRequestExecutor<T, V> {
		@Override
		public void handleServiceRequest(T serviceRequest, V serviceResponse) {
			LogManager.getLogger().info(getModule(), "No Response Received from Target System, Droping request");
			RadiusProcessHelper.dropResponse(serviceResponse);
		}
	}

	private class RequestTimeoutExecutor implements AsyncRequestExecutor<T, V> {

		@Override
		public void handleServiceRequest(T serviceRequest, V serviceResponse) {
			if(getData().isAcceptOnTimeout() == false) {
				LogManager.getLogger().info(getModule(), "No Response Received from Target System, Droping request");
				RadiusProcessHelper.dropResponse(serviceResponse);
			}
		}
	}
}
