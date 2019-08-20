package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.ConcurrentLoginCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthConcurrentLoginCommunicationHandler extends ConcurrentLoginCommunicationHandler<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {
	private static final String MODULE = "AUTH-CONC-COMM-HNDLR";

	public AuthConcurrentLoginCommunicationHandler(RadAuthServiceContext serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new ResponseReceivedExecutor(remoteRequest, remoteResponse);
	}
	
	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newRequestDroppedExecutor(
			RadUDPRequest remoteRequest) {
		return new RequestDroppedExecutor();
	}
	
	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newRequestTimeoutExecutor(
			RadUDPRequest remoteRequest) {
		return new RequestTimeoutExecutor();
	}

	class ResponseReceivedExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		private final RadUDPRequest remoteRequest;
		private final RadUDPResponse remoteResponse;

		public ResponseReceivedExecutor(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
			this.remoteRequest = remoteRequest;
			this.remoteResponse = remoteResponse;
		}

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			IRadiusPacket proxyResponse = remoteResponse.getRadiusPacket();
			// re-encrypting value of any encryptable attribute
			((RadiusPacket)proxyResponse).reencryptAttributes(remoteRequest.getRadiusPacket().getAuthenticator(), remoteRequest.getSharedSecret(), ((RadServiceRequest)serviceRequest).getAuthenticator(), serviceResponse.getClientData().getSharedSecret(serviceResponse.getPacketType()));
			proxyResponse.refreshPacketHeader();
			
			processPacket(proxyResponse, serviceRequest, serviceResponse);
		}
	}

	class RequestTimeoutExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			if(getData().isAcceptOnTimeout() == false){
				RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.TIMEOUT_RESPONSE_RECEIVED_FOR_CONC_COMMUNICATION);
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Received timeout, but proceeding further as accept on timeout is true");
				}
			}
		}
	}

	class RequestDroppedExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.OPERATION_FAILED_FOR_CONC_COMMUNICATION);
		}
	}

	@Override
	protected String getModule() {
		return MODULE;
	}
}
