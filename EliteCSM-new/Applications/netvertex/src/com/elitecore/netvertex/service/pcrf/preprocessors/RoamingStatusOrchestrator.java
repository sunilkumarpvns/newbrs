package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequestHandlerPreProcessor;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RoamingStatusOrchestrator implements PCRFRequestHandlerPreProcessor {

    private static final String MODULE = "SUBSCRIBER-ROAMING-STATUS-ORCHESTRATOR";


    public RoamingStatusOrchestrator(NetVertexServerContext serverContext) {
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Pre-process for subscriber roaming status started");
        }

        String subscriberHomeCountry = response.getAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.val);
        String subscriberLocationCountry = request.getAttribute(PCRFKeyConstants.LOCATION_COUNTRY.val);

        if(Strings.isNullOrBlank(subscriberHomeCountry)){
            subscriberHomeCountry = response.getAttribute(PCRFKeyConstants.SYSTEM_COUNTRY.val);
        }

        if(Strings.isNullOrBlank(subscriberLocationCountry)){
            subscriberLocationCountry = response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_COUNTRY.val);
        }

        decideSubscriberRoamingStatus(response, subscriberHomeCountry, subscriberLocationCountry);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Pre-process for subscriber roaming status completed");
        }

    }

    private void decideSubscriberRoamingStatus(PCRFResponse response, String subscriberHomeCountry, String subscriberLocationCountry) {
        if(Strings.isNullOrBlank(subscriberLocationCountry) || Strings.isNullOrBlank(subscriberHomeCountry)) {
            response.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);
            return;
        }

        if(subscriberLocationCountry.equals(subscriberHomeCountry)){
            response.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);
            return;
        }

        response.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val);

    }

}
