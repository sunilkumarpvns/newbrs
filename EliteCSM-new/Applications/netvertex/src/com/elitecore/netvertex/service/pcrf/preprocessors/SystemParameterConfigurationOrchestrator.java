package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequestHandlerPreProcessor;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class SystemParameterConfigurationOrchestrator implements PCRFRequestHandlerPreProcessor {

    private static final String MODULE = "SYSTEM-PARAM-ORCHESTRATOR";

    private final NetVertexServerContext serverContext;

    public SystemParameterConfigurationOrchestrator(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "System parameter enrichment process started");
        }

        setCallingCountry(response);
        setCallingOperator(response);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "System parameter enrichment process completed");
        }
    }

    private void setCallingCountry(PCRFResponse response) {
        String callingCountry = response.getAttribute(PCRFKeyConstants.SYSTEM_COUNTRY.val);
        if (callingCountry != null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping system country enrichment. Reason: PCRF key: "
                        + PCRFKeyConstants.SYSTEM_COUNTRY.val + " is already exist in response");
            }
            return;
        }

        String systemCountry = serverContext.getServerConfiguration().getSystemParameterConfiguration().getCountry();
        if (Strings.isNullOrBlank(systemCountry)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping system country enrichment. Reason: Country is not configured in System Parameter");
            }
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, createLogMessage(systemCountry, PCRFKeyConstants.SYSTEM_COUNTRY));
        }
        response.setAttribute(PCRFKeyConstants.SYSTEM_COUNTRY.val, systemCountry);
    }

    public void setCallingOperator(PCRFResponse response) {
        String callingOperator = response.getAttribute(PCRFKeyConstants.SYSTEM_OPERATOR.val);
        if (callingOperator != null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping system operator enrichment. Reason: PCRF key: "
                        + PCRFKeyConstants.SYSTEM_OPERATOR.val + " is already exist in response");
            }
            return;
        }

        String systemOperator = serverContext.getServerConfiguration().getSystemParameterConfiguration().getOperator();
        if (Strings.isNullOrBlank(systemOperator)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping system country enrichment. Reason: Operator is not configured in System Parameter");
            }
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, createLogMessage(systemOperator, PCRFKeyConstants.SYSTEM_OPERATOR));
        }
        response.setAttribute(PCRFKeyConstants.SYSTEM_OPERATOR.val, systemOperator);
    }

    private String createLogMessage(String systemCountry, PCRFKeyConstants pcrfkey) {
        return "Adding PCRF key: " + pcrfkey.val
                + " with value: " + systemCountry + " in response";
    }
}
