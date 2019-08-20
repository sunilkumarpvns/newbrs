package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class PCCToDiameterRequestMapping implements PCRFToDiameterPacketMapping{

	private static final String MODULE = "PCC-DIA-REQ-MAPPING";
	
	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {


		DiameterPacket diameterPacket = valueProvider.getDiameterPacket();
		PCRFResponse response = valueProvider.getPcrfResponse();
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(response.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

		/*
		 * Destination Host and Realm for Sy is set in PCCToDiameterSyMapping Module
		 * Session Id For Sy Session is generated and added in SyApplication Module
		 */
		if(SessionTypeConstant.SY != sessionType) {
			//	add Destination-Host AVP
			CommandCode commandCode = CommandCode.getCommandCode(diameterPacket.getCommandCode());
			String value = null;
			if (valueProvider.getServerConfiguration().getMiscellaneousParameterConfiguration().getServerInitiatedDestinationHost()
					|| commandCode.isServerInitiated == false) {

				value = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal());
				if (value != null) {
					accumalator.addAvp(DiameterAVPConstants.DESTINATION_HOST, value);
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(MODULE, "Error in adding Destination-Host (" + DiameterAVPConstants.DESTINATION_HOST + ") AVP. Reason: value not found from PCRF Response");
					}
				}
			}
			//	add Destination-Realm AVP
			value = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal());
			if (value != null) {
				accumalator.addAvp(DiameterAVPConstants.DESTINATION_REALM, value);
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Error in adding Destination-Realm (" + DiameterAVPConstants.DESTINATION_REALM + ") AVP. Reason: value not found from PCRF Response");
				}
			}


			accumalator.addAvp(DiameterAVPConstants.SESSION_ID, response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()));
		}

		valueProvider.getPcrfResponse().setAttribute(PCRFKeyConstants.GATEWAY_IP.getVal(), valueProvider.getDiameterGatewayConfiguration().getHostIPAddress());

		addApplicationIdAvp(valueProvider, accumalator);
	
	}

	private void addApplicationIdAvp(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator avpAccumalator) {
		DiameterGatewayConfiguration configuration = valueProvider.getDiameterGatewayConfiguration();
		DiameterRequest diameterRequest = valueProvider.getDiameterPacket().getAsDiameterRequest();
		PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));


		long applicationId;
		if(SessionTypeConstant.CISCO_GX == sessionType || SessionTypeConstant.GX == sessionType){
			applicationId = configuration.getGxApplicationId();
		} else if(SessionTypeConstant.RX == sessionType){
			applicationId = configuration.getRxApplicationId();
		} else if(SessionTypeConstant.GY == sessionType || SessionTypeConstant.CISCO_GY == sessionType){
			applicationId = configuration.getGyApplicationId();
		} else if(SessionTypeConstant.S9 == sessionType){
			applicationId = configuration.getS9ApplicationId();
		} else if(SessionTypeConstant.SY == sessionType) {
			applicationId = configuration.getSyApplicationId();
		} else {
			throw new IllegalStateException(sessionType + " is not supported for diameter");
		}

		diameterRequest.setApplicationID(applicationId);
		avpAccumalator.addAvp(DiameterAVPConstants.AUTH_APPLICATION_ID, applicationId);
	}
}
