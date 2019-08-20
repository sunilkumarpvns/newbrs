package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.Objects;
/**
 * 
 * <i> This class contains mappings that can be used across applications </i>
 */

public class PCCToDiameterDefaultAVPMapping implements PCRFToDiameterPacketMapping {

	private static final String MODULE = "PCC-DIA-DEFAULT-MAPPING";

	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		PCRFResponse response = valueProvider.getPcrfResponse();
		DiameterPacket diameterPacket = valueProvider.getDiameterPacket();

		String gatewayName = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);
		if (gatewayName != null) {
			accumalator.addInfoAVP(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayName);
		} else {
			LogManager.getLogger().warn(MODULE, "Unable to add EC_ORIGINATOR_PEER_NAME " +
					"attribute. Reason: Gateway name not found from PCRF Response");
		}



		addResultCode(valueProvider, accumalator, response, diameterPacket);

	}

	private void addResultCode(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator, PCRFResponse response, DiameterPacket diameterPacket) {
		if(valueProvider.getDiameterPacket().isResponse()) {
			if (Objects.isNull(diameterPacket.getAVP(DiameterAVPConstants.EXPERIMENTAL_RESULT))) {
				String resultCode = response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal());
				IDiameterAVP resultCodeAVP = getResultCodeAVP(resultCode, (int) diameterPacket.getApplicationID());
				accumalator.add(resultCodeAVP);

				if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(resultCode) == false) {
					return;
				}
			}
		}
	}

	private IDiameterAVP getResultCodeAVP(String resultCodeString, int applicationId) {
		return ResultCodeMapping.getInstance().getResultCodeAVP(resultCodeString, applicationId);
	}

}
