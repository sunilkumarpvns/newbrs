package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class RARAppSpecifMapping implements PCRFToDiameterPacketMapping {

	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();

		accumalator.addAvp(DiameterAVPConstants.RE_AUTH_REQUEST_TYPE, DiameterAVPConstants.AUTHORIZE_ONLY_INT);

		String value = pcrfResponse.getAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal());
		if (value != null) {
			accumalator.addAvp(DiameterAVPConstants.TGPP_SESSION_RELEASE_CAUSE, value);
		}

	}

}
