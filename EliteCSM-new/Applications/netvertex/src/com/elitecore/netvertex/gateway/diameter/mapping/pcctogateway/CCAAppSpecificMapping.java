package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class CCAAppSpecificMapping implements PCRFToDiameterPacketMapping{

	private static final String MODULE = "CCA-MAPPING";

	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {
		
		PCRFResponse response = valueProvider.getPcrfResponse();
		DiameterPacket diameterPacket = valueProvider.getDiameterPacket();
		
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME);

		String gatewayName = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);
		if(gatewayName != null) {
			diameterPacket.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayName);
		} else {
			LogManager.getLogger().warn(MODULE, "Unable to add " + DiameterDictionary.getInstance().getAttributeName(diameterAVP.getVendorId(), diameterAVP.getAVPCode())
					+ " attribute. Reason: Gateway name not found from PCRF Response");
		}



	}
	

}
