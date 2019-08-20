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

import java.util.Objects;

public class SubscriberNetworkInfoOrchestrator implements PCRFRequestHandlerPreProcessor {

    private static final String MODULE = "SUB-NTWRK-ORCHSTR";
    private NetVertexServerContext serverContext;

    public SubscriberNetworkInfoOrchestrator(NetVertexServerContext serverContext) {

        this.serverContext = serverContext;
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {



        String subscriberMccMnc = request.getAttribute(PCRFKeyConstants.SUB_MCC_MNC.getVal());

        if(Objects.nonNull(subscriberMccMnc)) {
            NetworkConfiguration networkConfiguration = getNetworkConfiguration(request.getAttribute(PCRFKeyConstants.SUB_MCC_MNC.getVal()));
            if(Objects.nonNull(networkConfiguration)){
                setSubscriberNetworkInformation(response, networkConfiguration);
            }
        } else {
            setNetworkConfFromSubscriberIMSI(request, response);
        }


    }

    private void setNetworkConfFromSubscriberIMSI(PCRFRequest request, PCRFResponse response) {
        String subscriberImsi = request.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal());

        if (Strings.isNullOrBlank(subscriberImsi)) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Subscriber Network Information can't be set. Reason: IMSI not received in request");
            }
            return;
        }

        String subscriberMccMnc = getSubscriberMccMncOf6Digit(subscriberImsi);

        if(Objects.nonNull(subscriberMccMnc)) {
            NetworkConfiguration network = getNetworkConfiguration(subscriberMccMnc);
            if (network != null) {
                setSubscriberNetworkInformation(response, network);
                response.setAttribute(PCRFKeyConstants.SUB_MCC_MNC.getVal(), subscriberMccMnc);
                return;
            }
        }


        subscriberMccMnc = getSubscriberMccMncOf5Digit(subscriberImsi);

        if (Strings.isNullOrBlank(subscriberMccMnc)) {
            return;
        }

        NetworkConfiguration network = getNetworkConfiguration(subscriberMccMnc);
        if (Objects.nonNull(network)) {
            response.setAttribute(PCRFKeyConstants.SUB_MCC_MNC.getVal(), subscriberMccMnc);
            setSubscriberNetworkInformation(response, network);
        }

    }

    private String getSubscriberMccMncOf6Digit(String imsi) {
        if(imsi.length() < 6){
            return null;
        }
        return imsi.substring(0, 6);
    }

    private String getSubscriberMccMncOf5Digit(String imsi) {
        if(imsi.length() < 5){
            return null;
        }
        return imsi.substring(0, 5);
    }



    private void setSubscriberNetworkInformation(PCRFResponse response, NetworkConfiguration network) {
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_OPERATOR.getVal(), network.getOperator());
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_BRAND.getVal(), network.getBrand());
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal(), network.getCountryName());
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_GEOGRAPHY.getVal(), network.getGeography());
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_NAME.getVal(), network.getNetworkName());
        response.setAttribute(PCRFKeyConstants.SUB_NETWORK_TECHNOLOGY.getVal(), network.getTechnology());
    }


    public NetworkConfiguration getNetworkConfiguration(String subscriberImsiMccMnc) {

        NetworkConfiguration network = serverContext.getLocationRepository().getNetworkInformationByMCCMNC(subscriberImsiMccMnc);
        if (network == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Subscriber Network Information can't be set. Reason: No network information found by subscriber IMSI MCC-MNC:" + subscriberImsiMccMnc);
            }

        }

        return network;
    }
}
