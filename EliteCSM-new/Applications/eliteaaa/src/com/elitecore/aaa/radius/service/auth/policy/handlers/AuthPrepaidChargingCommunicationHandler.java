package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.PrepaidChargingCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.aaa.radius.util.converters.IPrepaidConverter;
import com.elitecore.aaa.radius.util.converters.PrepaidConverterFactory;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthPrepaidChargingCommunicationHandler extends PrepaidChargingCommunicationHandler<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {
	private static final String MODULE = "AUTH-PREPAID-COMM-HNDLR";

	public AuthPrepaidChargingCommunicationHandler(RadAuthServiceContext serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	protected void handlePreRequest(RadAuthRequest request,
			RadAuthResponse response) {
		super.handlePreRequest(request, response);
		applyPrepaidConveter(request);
	}

	private void applyPrepaidConveter(RadAuthRequest request) {
		String prepaidStandard = getServiceContext().getServerContext().getServerConfiguration().getRadClientConfiguration().getPrepaidStandard(request.getClientIp());
		if(prepaidStandard != null){
			IPrepaidConverter prepaidConverter = PrepaidConverterFactory.getInstance().createPrepaidConverter(prepaidStandard);
			if(prepaidConverter != null){
				try {
					prepaidConverter.convertFromStandard((RadAuthRequest)request);
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE,"Request packet after applying prepaid conversion: " + request.toString());
					}						
				} catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
						LogManager.getLogger().trace(MODULE, "Not able to convert standard prepaid attribute, reason: " + e.getMessage());
					}						
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "No converter yet available for " + prepaidStandard + " prepaid standard.");
				}						
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No value for prepaid standard defined.");
			}
		}

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
			
			if(proxyResponse.getPacketType() == RadiusConstants.TIMEOUT_MESSAGE) {
				if(getData().isAcceptOnTimeout() == false){
					serviceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					serviceResponse.setResponseMessage(AuthReplyMessageConstant.OPERATION_FAILED_FOR_PREPAID_COMMUNICATION);
				}
			} else if(proxyResponse.getPacketType() == RadiusConstants.NO_RM_COMMUNICATION_MESSAGE) {
				serviceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				serviceResponse.setResponseMessage(AuthReplyMessageConstant.OPERATION_FAILED_FOR_PREPAID_COMMUNICATION);
			} else {
				processPacket(proxyResponse, serviceRequest, serviceResponse);
			}
		}
	}

	class RequestDroppedExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.OPERATION_FAILED_FOR_PREPAID_COMMUNICATION);
		}
	}

	class RequestTimeoutExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			if(getData().isAcceptOnTimeout() == false){
				RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.TIMEOUT_RESPONSE_RECEIVED_FOR_PREPAID_COMMUNICATION);
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Received timeout, but proceeding further as accept on timeout is true");
				}
			}
		}
	}

	@Override
	protected String getModule() {
		return MODULE;
	}
}
