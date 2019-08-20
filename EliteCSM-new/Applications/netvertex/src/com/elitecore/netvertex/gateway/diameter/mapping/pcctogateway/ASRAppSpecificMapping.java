package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ASRAppSpecificMapping implements PCRFToDiameterPacketMapping {

	private static final String MODULE = "ASR-MAPPING";
	private PCCToDiameterRequestMapping pccToDiameterRequestMapping;

	public ASRAppSpecificMapping() {
		this.pccToDiameterRequestMapping = new PCCToDiameterRequestMapping();
	}
	
	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		String value = valueProvider.getPcrfResponse().getAttribute(PCRFKeyConstants.ABORT_CAUSE.getVal());
		if (value != null) {
			accumalator.addAvp(DiameterAVPConstants.TGPP_ABORT_CAUSE, value);
		} else {
			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Set Bearer released as abort cause. Reason: value not found for pcrf attribute:"
						+ PCRFKeyConstants.ABORT_CAUSE.getVal() + " from PCRFResponse");
			}
			accumalator.addAvp(DiameterAVPConstants.TGPP_ABORT_CAUSE, DiameterAttributeValueConstants.TGPP_ABORT_CAUSE_BEARER_RELEASED);
		}

		pccToDiameterRequestMapping.apply(valueProvider, accumalator);
	}

}
