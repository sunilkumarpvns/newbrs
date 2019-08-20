package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class PCCToDiameterGyMapping implements PCRFToDiameterPacketMapping {

    @Nonnull
    private final MSCCBuilder msccBuilder;
	private final CostInformationAVPMapping costInformationAVPMapping;

    public PCCToDiameterGyMapping() {
        this(new MSCCBuilder());
    }

    //Used for test cases
    public PCCToDiameterGyMapping(MSCCBuilder msccBuilder) {
        this.msccBuilder = msccBuilder;
		this.costInformationAVPMapping = new CostInformationAVPMapping();
    }

    @Override
    public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();
		if (PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val)) == false) {
			msccBuilder.addMSCCAVPs(valueProvider, accumalator);
		}
		costInformationAVPMapping.apply(valueProvider, accumalator);

        String eventTimeStamp = valueProvider.getPcrfResponse().getAttribute(PCRFKeyConstants.EVENT_TIMESTAMP.val);
        if (eventTimeStamp != null) {
            accumalator.addAvp(DiameterAVPConstants.EVENT_TIMESTAMP, new Date(TimeUnit.SECONDS.toMillis(Long.parseLong(eventTimeStamp))));
        }
    }

}