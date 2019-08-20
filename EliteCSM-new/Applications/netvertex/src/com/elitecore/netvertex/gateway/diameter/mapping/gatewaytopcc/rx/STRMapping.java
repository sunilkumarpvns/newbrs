package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.rx;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class STRMapping implements DiameterToPCCPacketMapping {

	@Override
	public void apply(PCRFRequestMappingValueProvider valueProvider) {

		PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
		DiameterRequest diameterPacket = valueProvider.getDiameterRequest();
		
		if(diameterPacket.getAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX) != null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_IPV6.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.FRAMED_IPV6_PREFIX).substring(5,22));
		}
		
		IDiameterAVP serviceURNAvp = diameterPacket.getAVP(DiameterAVPConstants.TGPP_SERVICE_URN);
		if(serviceURNAvp != null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.SERVICE_URN.val, serviceURNAvp.getStringValue());
			pcrfRequest.setAttribute(PCRFKeyConstants.IMS_EMERGENCY_SESSION.val, PCRFKeyValueConstants.IMS_EMERGENCY_SESSION_TRUE.val);
		}
	
	}

}
