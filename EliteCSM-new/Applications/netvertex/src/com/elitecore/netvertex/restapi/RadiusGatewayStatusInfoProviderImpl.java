package com.elitecore.netvertex.restapi;

import com.elitecore.corenetvertex.GatewayStatusInfo;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadiusGatewayStatusInfoProviderImpl implements GatewayStatusInfoProvider {

    private RadiusGatewayController radiusGatewayController;

    public RadiusGatewayStatusInfoProviderImpl(RadiusGatewayController radiusGatewayController){

        this.radiusGatewayController = radiusGatewayController;
    }

    @Override
    public Map<String, List<GatewayStatusInfo>> getGatewayStatusInfo() {
        Map<String, List<GatewayStatusInfo>> protocolToGatewayStatus = new HashMap<>();
        List<GatewayStatusInfo> gatewayStatusInformation = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : radiusGatewayController.getRadiusGatewaysState().entrySet()) {
            gatewayStatusInformation.add(new GatewayStatusInfo(CommunicationProtocol.RADIUS.name(), entry.getKey(), entry.getValue() == true ? CommonConstants.STATUS_ACTIVE : CommonConstants.STATUS_INACTIVE));
        }
        protocolToGatewayStatus.put(CommunicationProtocol.RADIUS.name(), gatewayStatusInformation);

        return protocolToGatewayStatus;
    }
}
