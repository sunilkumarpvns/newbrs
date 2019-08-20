package com.elitecore.netvertex.restapi;

import com.elitecore.corenetvertex.GatewayStatusInfo;

import java.util.List;
import java.util.Map;

public interface GatewayStatusInfoProvider {

    Map<String, List<GatewayStatusInfo>> getGatewayStatusInfo();
}

