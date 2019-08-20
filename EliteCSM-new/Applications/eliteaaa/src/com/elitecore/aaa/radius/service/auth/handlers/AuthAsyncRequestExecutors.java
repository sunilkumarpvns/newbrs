package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.Collection;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class AuthAsyncRequestExecutors {
	private static final String MODULE = "AUTH-ASYNC-REQUEST-EXECUTOR";

	private AuthAsyncRequestExecutors() {
	}

	public static class AuthResponseReceivedExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {
		private RadUDPRequest udpRequest;
		private RadUDPResponse udpResponse;

		public AuthResponseReceivedExecutor(RadUDPRequest udpRequest,RadUDPResponse udpResponse) {
			this.udpRequest = udpRequest;
			this.udpResponse = udpResponse;
		}

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {

			IRadiusPacket proxyResponse = udpResponse.getRadiusPacket();

			if (proxyResponse.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
				serviceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				String replyMsg = AuthReplyMessageConstant.AUTHENTICATION_FAILED;
				IRadiusAttribute replyMessageAttribute = proxyResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
				if(replyMessageAttribute !=null && replyMessageAttribute.getStringValue().trim().length() > 0) {
					replyMsg = replyMessageAttribute.getStringValue();
				}
				serviceResponse.setResponseMessage(replyMsg);

				serviceResponse.setFurtherProcessingRequired(false);
				serviceResponse.setProcessingCompleted(true);
			} 

			//	re-encrypting value of any encryptable attribute
			((RadiusPacket)proxyResponse).reencryptAttributes(udpRequest.getRadiusPacket().getAuthenticator(), udpRequest.getSharedSecret(), serviceRequest.getAuthenticator(), serviceResponse.getClientData().getSharedSecret(serviceResponse.getPacketType()));
			proxyResponse.refreshPacketHeader();

			Collection<IRadiusAttribute> radiusAttributes = proxyResponse.getRadiusAttributes(true);
			for (IRadiusAttribute radAttribute:radiusAttributes){
				if (radAttribute.isVendorSpecific()) {
					Collection<IRadiusAttribute> list = ((VendorSpecificAttribute)radAttribute).getAttributes();
					if (list != null){
						for (IRadiusAttribute radiusAttribute : list) {
							serviceResponse.addAttribute(radiusAttribute);
						}
					}				
				} else if(radAttribute.getVendorID() == 0 && radAttribute.getID() == RadiusAttributeConstants.REPLY_MESSAGE){
					serviceResponse.setResponseMessage(radAttribute.getStringValue());
				} else{
					serviceResponse.addAttribute(radAttribute);
				}
			}
			serviceResponse.setPacketType(proxyResponse.getPacketType());
		}

	}

	public static class AuthRequestTimeoutExecutor implements AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> {

		private boolean acceptOnTimeout;

		public AuthRequestTimeoutExecutor(boolean acceptOnTimeout) {
			this.acceptOnTimeout = acceptOnTimeout;
		}

		@Override
		public void handleServiceRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
			if (acceptOnTimeout) {
				RadiusProcessHelper.rejectResponse(serviceResponse, AuthReplyMessageConstant.TIMEOUT_RESPONSE_RECEIVED_FOR_PROXY_COMMUNICATION);
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "No Response Received from Target System, Droping request");
				}
				RadiusProcessHelper.dropResponse(serviceResponse);
			}
		}
	}

}
