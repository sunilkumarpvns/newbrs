package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequestHandlerPreProcessor;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class OperatorNetworkInfoOrchestrator implements PCRFRequestHandlerPreProcessor {
    private static final String MODULE = "OPRTR-NTWRK-ORCHSTR";
    private NetVertexServerContext serverContext;

    public OperatorNetworkInfoOrchestrator(NetVertexServerContext serverContext) {

        this.serverContext = serverContext;
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {
        String locationMCC = getLocationMCC(request);
        String locationMNC = getLocationMNC(request);

        NetworkConfiguration operatorNetwork = getOperatorNetworkInformationByLocationMCCMNC(locationMCC, locationMNC);
        if (operatorNetwork == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No operator network information found by location mcc mnc.Trying to get by SGSN MCC MNC");
            }
            operatorNetwork = getOperatorNetworkInformationBySGSNMCCMNC(request);
        }
        if (operatorNetwork == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Operator network information can't be set. Reason: No network information found by SGSN MCC MNC");
            }
            return;
        }
        setOperateNetworkInformation(response, operatorNetwork);
    }


    private NetworkConfiguration getOperatorNetworkInformationBySGSNMCCMNC(PCRFRequest request) {

        String sgsnMccMnc = request.getAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal());

        if (Strings.isNullOrBlank(sgsnMccMnc)) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Operator Network Information can't be set based on SGSN MCC MNC.Reason: SGSN MCC MNC not received in request");
            }
            return null;
        }

        return getNetworkConfiguration(sgsnMccMnc);
    }

    private NetworkConfiguration getOperatorNetworkInformationByLocationMCCMNC(String locationMCC, String locationMNC) {
        if (Strings.isNullOrBlank(locationMCC)) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Operator Network Information can't be set based on location MCC MNC.Reason: MCC is null");
            }
            return null;
        }
        if (Strings.isNullOrBlank(locationMNC)) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Operator Network Information can't be set based on location MCC MNC.Reason: MNC is null");
            }
            return null;
        }
        return getNetworkConfiguration(String.valueOf(locationMCC)+ String.valueOf(locationMNC));
    }

    private String getLocationMCC(PCRFRequest request) {
        return request.getAttribute(PCRFKeyConstants.LOCATION_MCC.getVal());
    }

    private String getLocationMNC(PCRFRequest request) {
        return request.getAttribute(PCRFKeyConstants.LOCATION_MNC.getVal());
    }


    NetworkConfiguration getNetworkConfiguration(String mccMnc) {
        return serverContext.getLocationRepository().getNetworkInformationByMCCMNC(mccMnc);
    }

    private void setOperateNetworkInformation(PCRFResponse response, NetworkConfiguration network) {
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_NAME.getVal(), network.getOperator());
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_BRAND.getVal(), network.getBrand());
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_COUNTRY.getVal(), network.getCountryName());
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_GEOGRAPHY.getVal(), network.getGeography());
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_NAME.getVal(), network.getNetworkName());
        response.setAttribute(PCRFKeyConstants.OPERATOR_NETWORK_TECHNOLOGY.getVal(), network.getTechnology());
    }

}
