package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequestHandlerPreProcessor;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class CalledPartyLRNAndPrefixConfigurationOrchestrator implements PCRFRequestHandlerPreProcessor {

    private static final String MODULE = "LRN-CONFIG-ORCHSTR";
    private NetVertexServerContext serverContext;

    public CalledPartyLRNAndPrefixConfigurationOrchestrator(NetVertexServerContext netVertexServerContext) {
        this.serverContext = netVertexServerContext;
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {
        if (processLRNEnrichment(request, response)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "LRN Configuration successfully applied. So skipping Prefix based enrichment");
            }
            return;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing Prefix Enrichment as LRN enrichment is not applied");
        }

        processPrefixEnrichment(request, response);
    }

    private boolean processLRNEnrichment(PCRFRequest request, PCRFResponse response) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Started LRN Enrichment process");
        }

        String lrn = request.getAttribute(PCRFKeyConstants.LRN.getVal());
        if(Strings.isNullOrBlank(lrn)){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "LRN configuration information can't be set. Reason: LRN is null");
            }
            return false;
        }

        LRNConfiguration lrnConfiguration = serverContext.getLRNConfigurationRepository().getLRNConfigurationByLRN(lrn);
        if(lrnConfiguration == null){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "LRN configuration information can't be set." +
                        " Reason: No LRN configuration found with lrn: " + lrn);
            }
            return false;
        }

        NetworkConfiguration networkConfiguration = lrnConfiguration.getNetworkConfiguration();
        response.setAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal(), networkConfiguration.getCountryName());
        response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), networkConfiguration.getOperator());
        response.setAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal(), networkConfiguration.getNetworkName());

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "LRN Enrichment process completed");
        }
        return true;
    }

    private void processPrefixEnrichment(PCRFRequest request, PCRFResponse response) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Prefix Enrichment process started");
        }

        String calledPartyNumber = request.getAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal());
        String callDirection = request.getAttribute(PCRFKeyConstants.CS_CALL_DIRECTION.getVal());

        if (callDirection != null && callDirection.equalsIgnoreCase(PCRFKeyValueConstants.CALL_DIRECTION_INCOMING.val)) {
            calledPartyNumber = request.getAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.getVal());
        }

        if (calledPartyNumber == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping prefix enrichment handling for session Id :" + request.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) +
                        ". Reason: " + PCRFKeyConstants.CS_CALLED_STATION_ID.getVal()
                        + " not found from request");
            }
            return;
        }

        PrefixConfiguration calledPrefixConfiguration = serverContext.getPrefixRepository().getBestMatch(calledPartyNumber);
        if (calledPrefixConfiguration == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping prefix enrichment handling for session Id : " + request.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) +
                        ". Reason: Prefix configuration not found for Called Party Address: " + calledPartyNumber);
            }
            return;
        }

        response.setAttribute(PCRFKeyConstants.CS_CALLED_PREFIX.getVal(), calledPrefixConfiguration.getPrefix());
        response.setAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal(), String.valueOf(calledPrefixConfiguration.getCountry()));
        response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), String.valueOf(calledPrefixConfiguration.getOperator()));
        response.setAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal(), String.valueOf(calledPrefixConfiguration.getNetworkName()));

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Called prefix enrichment keys :- " + PCRFKeyConstants.CS_CALLED_STATION_ID.getVal() +
                    " : " + calledPartyNumber + " - Prefix : " + calledPrefixConfiguration.getPrefix() +
                    " - Country : " + calledPrefixConfiguration.getCountry() +
                    " - Operator: " + calledPrefixConfiguration.getOperator() +
                    " - Network Name: " + calledPrefixConfiguration.getNetworkName());
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Prefix Enrichment process completed");
        }
    }
}