package com.elitecore.aaa.radius.service.base.policy.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.naming.ServiceUnavailableException;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class CoADMHandler<T extends RadServiceRequest, V extends RadServiceResponse>
implements RadServiceHandler<T, V> {

	private RadServiceContext<T, V> serviceContext;
	private CoADMHandlerEntryData data;

	public CoADMHandler(RadServiceContext<T, V> serviceContext,
			CoADMHandlerEntryData data) {
		this.serviceContext = serviceContext;
		this.data = data;
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModuleName(), "Handling request using COA-DM Handler");
		}

		/*
		 * This parameter is required to be set as only first entry whose rule set is
		 * satisfied should be considered and other entries should be skipped.   
		 */
		request.setParameter(FirstCoADMFilterSelectedStrategy.CoA_DM_FILTER_SELECTED, true);

		try {
			RadClientData localClientData = serviceContext.getServerContext()
					.getServerConfiguration()
					.getRadClientConfiguration()
					.getClientData(InetAddress.getLocalHost().getHostAddress());

			if (localClientData == null) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(getModuleName(), "Cannot generate CoA-DM request, " 
							+ "Reason: client data for localhost not found"); 
				}
				return;
			}

			/*
			 * Don't move this translation in the CoADMTask. This has been kept here due to performance
			 * reason.
			 * 
			 * If we move this translation inside the task then we need to clone the request and response.
			 * If we clone these objects then it will give impact on GC in future. While if we keep it here
			 * then it will take approximately 2ms more in sending the response to the client. This 2ms of
			 * penalty would be better than the impact on GC.
			 */
			TranslationHelper helper = new TranslationHelper(data.getTranslationMapping(),
					request, response, true);

			RadiusPacket translatedPacket = helper.translate();

			if (helper.isDummyMappingApplicable()) {
				processForDummyResponse(helper.getDummyResponseBytes());
			} else {
				processTranslatedPacket(response, localClientData, translatedPacket);
			}
			
		} catch (UnknownHostException e) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(getModuleName(), "Cannot generate CoA-DM request," +
						" Reason: localhost is unknown");
			}
			LogManager.getLogger().trace(e);
		}
	}

	private void processTranslatedPacket(V response,
			RadClientData localClientData, RadiusPacket translatedPacket) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getModuleName(), "Processing translated packet");
		}
		
		translatedPacket.setPacketType(data.getPacketType());

		final String oldSharedSecret = response.getClientData()
				.getSharedSecret(data.getPacketType());
		final String newSharedSecret = localClientData
				.getSharedSecret(data.getPacketType());

		//				re-encrypting value of any encryptable attribute
		translatedPacket.reencryptAttributes(translatedPacket.getAuthenticator(), 
				oldSharedSecret, translatedPacket.getAuthenticator(), newSharedSecret);
		translatedPacket.refreshPacketHeader();

		translatedPacket.setAuthenticator(RadiusUtility
				.generateRFC2866RequestAuthenticator(translatedPacket, newSharedSecret));
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getModuleName(), "Submitting CoA-DM task");
		}
		
		serviceContext.getServerContext().getTaskScheduler()
			.scheduleSingleExecutionTask(new CoADMTask(translatedPacket));
	}

	private void processForDummyResponse(byte[] dummyResponseBytes) {
		RadiusPacket packet = new RadiusPacket();
		packet.setBytes(dummyResponseBytes);
		packet.refreshPacketHeader();
		packet.refreshInfoPacketHeader();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(getModuleName(), "Received dummy response from DynAuth service: " + packet);
		}
	}

	private class CoADMTask extends BaseSingleExecutionAsyncTask {

		private RadiusPacket request;

		public CoADMTask(RadiusPacket request) {
			this.request = request;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			try {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(getModuleName(), "Sending request to DynaAuth service: " + request);
				}
				
				final SettableFuture<RadiusPacket> futureResponse = EliteAAAServiceExposerManager
						.getInstance().sendLocalRequest(request);
				
				futureResponse.addListener(new ResponseListener(futureResponse),
						serviceContext.getServerContext().getTaskScheduler());
				
			} catch (UnknownHostException e1) {
				LogManager.getLogger().error(getModuleName(), "Problem in submitting local CoA/DM request, Reason: " + e1.getMessage());
				LogManager.getLogger().trace(e1);
			} catch (ServiceUnavailableException e1) {
				LogManager.getLogger().error(getModuleName(), "Problem in submitting local CoA/DM request, Reason: " + e1.getMessage());
				LogManager.getLogger().trace(e1);
			}
		}
		
		@Override
		public long getInitialDelay() {
			return data.getScheduleAfterInMillis();
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	private class ResponseListener implements Runnable {

		private SettableFuture<RadiusPacket> futureResponse;

		public ResponseListener(SettableFuture<RadiusPacket> futureResponse) {
			this.futureResponse = futureResponse;
		}

		@Override
		public void run() {
			try {
				if (LogManager.getLogger().isInfoLogLevel()) {
					RadiusPacket response = futureResponse.get();
					if (response != null) {
						LogManager.getLogger().info(getModuleName(), "Response received from DynaAuth service: " + response.toString());
					} else {
						LogManager.getLogger().info(getModuleName(), "No response received from DynaAuth service, Reason: Request dropped/timed-out");
					}
				}
			} catch (InterruptedException e) {
				LogManager.getLogger().warn(getModuleName(), "Failed to receive response, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (ExecutionException e) {
				LogManager.getLogger().error(getModuleName(), "Failed to receive response, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	public String getModuleName() {return "CoA-DM-HNDLR";}
}
