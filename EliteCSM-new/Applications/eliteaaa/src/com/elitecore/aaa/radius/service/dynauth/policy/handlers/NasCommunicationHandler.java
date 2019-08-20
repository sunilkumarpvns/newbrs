package com.elitecore.aaa.radius.service.dynauth.policy.handlers;

import java.util.List;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * Handler that communicates with NAS type of external systems. It is meant to be used
 * in Dynamic Authorization service and adds a parameter to request suggesting that one
 * NAS communication is initiated so that other communications can be skipped.
 * 
 * @author narendra.pathai
 * @author sanjay.dhamelia
 *
 */
public class NasCommunicationHandler extends ExternalCommunicationHandler<RadDynAuthRequest, RadDynAuthResponse> {

	private static final String MODULE = "NAS-COMM-HNDLR";

	public NasCommunicationHandler(RadDynAuthServiceContext serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	public boolean isEligible(RadDynAuthRequest request,RadDynAuthResponse response) {
		return true;
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected boolean includeInfoAttributes() {
		return true;
	}

	/**
	 * This parameter is added to check whether the nas-communication occur or not,
	 * if true than other nas-communication handler will skip.
	 * This parameter is check in the {@link FirstCommunicatorSelectedStrategy}
	 */
	@Override
	protected void handlePreRequest(RadDynAuthRequest request,RadDynAuthResponse response) {
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Communicating via configured NAS");
		}
		
		request.setParameter(RadiusConstants.NAS_COMMUNICATOR_SELECTED, true);
	}
	
	@Override
	protected AsyncRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> newResponseReceivedExecutor(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new ResponseReceivedExecutor(remoteRequest, remoteResponse);
	}
	
	class ResponseReceivedExecutor implements AsyncRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> {
		
    	RadUDPRequest udpRequest;
		RadUDPResponse udpResponse;
		
		public ResponseReceivedExecutor(RadUDPRequest udpRequest, RadUDPResponse udpResponse) {
			this.udpRequest = udpRequest;
			this.udpResponse = udpResponse;
		}

		@Override
		public void handleServiceRequest(RadDynAuthRequest serviceRequest, RadDynAuthResponse serviceResponse) {
			
			RadDynAuthRequest radDynAuthRequest = (RadDynAuthRequest) serviceRequest;
			RadDynAuthResponse radDynAuthResponse = (RadDynAuthResponse) serviceResponse;

			IRadiusPacket targetServerResponse = udpResponse.getRadiusPacket();
			
			//	re-encrypting value of any encryptable attribute
			((RadiusPacket)targetServerResponse).reencryptAttributes(udpRequest.getRadiusPacket().getAuthenticator(), udpRequest.getSharedSecret(), ((RadServiceRequest)serviceRequest).getAuthenticator(), radDynAuthResponse.getClientData().getSharedSecret(radDynAuthRequest.getPacketType()));
			targetServerResponse.refreshPacketHeader();
			
			List<IRadiusAttribute> radiusAttributes = (List<IRadiusAttribute>) targetServerResponse.getRadiusAttributes();
			for (IRadiusAttribute radAttribute:radiusAttributes) {
				radDynAuthResponse.addAttribute(radAttribute);
			}
			radDynAuthResponse.setPacketType(targetServerResponse.getPacketType());
		}
	}
}