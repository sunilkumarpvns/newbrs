package com.elitecore.netvertex.restapi;

import com.elitecore.corenetvertex.GatewayStatusInfo;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiameterGatewayStatusInfoProviderImpl implements GatewayStatusInfoProvider {


    private DiameterStack diameterStack;

    public DiameterGatewayStatusInfoProviderImpl(DiameterStack diameterStack){

        this.diameterStack = diameterStack;
    }

    @Override
    public Map<String, List<GatewayStatusInfo>> getGatewayStatusInfo() {
        Map<String, List<GatewayStatusInfo>> protocolToGatewayStatus = new HashMap<>();
        List<GatewayStatusInfo> gatewayStatusInformation = new ArrayList<>();

        for (Map.Entry<String, IStateEnum> entry : diameterStack.getPeersState().entrySet()) {
            gatewayStatusInformation.add(new GatewayStatusInfo(CommunicationProtocol.DIAMETER.name(), entry.getKey(), entry.getValue().toString()));
        }
        protocolToGatewayStatus.put(CommunicationProtocol.DIAMETER.name(), gatewayStatusInformation);

        return protocolToGatewayStatus;
    }
}
