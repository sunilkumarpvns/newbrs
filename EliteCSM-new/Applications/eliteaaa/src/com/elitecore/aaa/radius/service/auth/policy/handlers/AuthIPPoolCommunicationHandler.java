package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.IPPoolCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthIPPoolCommunicationHandler extends IPPoolCommunicationHandler<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {
	private static final String MODULE = "AUTH-IP-POOL-COMM-HNDLR";

	public AuthIPPoolCommunicationHandler(RadAuthServiceContext serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AuthIPPoolAsyncRequestExecutor(remoteRequest, remoteResponse);
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

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return super.isEligible(request, response) && isCommunicationRequired(request, response); 

	}

	/**
	 * According to specification if request already contains a Framed IP Address or Framed Pool, 
	 * then AAA does not need to assign an IP.
	 * <br/>
	 * In our case we are also check using response if somewhere from system a Framed IP address or Pool has been
	 * assigned.
	 * <br/>
	 * Presently we do not comply completely with IPv6 related specifications, so we are not communicating with IP Pool
	 * in case when request or response contains IPv6 Prefix.
	 * <br/>
	 * ELITEAAA-1763 With this implementation we need to incorporate IPv6 related changes.
	 */
	private boolean isCommunicationRequired(RadAuthRequest request, RadAuthResponse response) {
		if((response.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS) != null || request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS, true) != null)){
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "IP-POOL communication will not occur as, either request or response " +
						"contains the Framed-IP-Address attribute.");
			}
			return false;
		}
		
		if(response.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX) != null 
				|| request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX, true) != null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "IP-POOL communication will not occur as, either request or response " +
						"contains the Framed-IPV6-Prefix attribute.");
			}
			return false;
		}
				
		// FIXME We need to implement IPv6 related RFC and provide support of IPv6 Address attribute
		/*try{
			AttributeId framedIPv6AttrId = Dictionary.getInstance().getAttributeId(RadiusAttributeConstants.FRAMED_IPv6_ADDRESS_STR);
			if(response.getRadiusAttribute(framedIPv6AttrId.getVendorId(),framedIPv6AttrId.getAttrbuteId()) != null || request.getRadiusAttribute(framedIPv6AttrId.getVendorId(),framedIPv6AttrId.getAttrbuteId()) != null){
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "IP-POOL communication will not occur as, either request or response " +
							"itself contains the Framed-IPV6-Address.");
				}
				return false;
			}
		}catch(InvalidAttributeIdException ex){
			return true;
		}*/
		
		return true;
	}

	class AuthIPPoolAsyncRequestExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		private final RadUDPRequest remoteRequest;
		private final RadUDPResponse remoteResponse;

		public AuthIPPoolAsyncRequestExecutor(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
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

	class RequestDroppedExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.OPERATION_FAILD_FOR_IPPOOL_COMMUNICATION);
		}
	}

	class RequestTimeoutExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			if(getData().isAcceptOnTimeout() == false){
				RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.TIMEOUT_RESPONSE_RECEIVED_FOR_IPPOOL_COMMUNICATION);
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
