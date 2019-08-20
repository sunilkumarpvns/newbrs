package com.elitecore.corenetvertex.data;

import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePacketMapData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiameterGatewayDataBuilder {

    public static AttributeMappingData createAttributeMappingData() {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        return attributeMappingData;
    }

    public static PacketMappingData createPacketMappingData() {
        PacketMappingData packetMappingData = new PacketMappingData();
        packetMappingData.setType("CCR");
        packetMappingData.setAttributeMappingData(createAttributeMappingData());
        packetMappingData.setPacketType(PacketType.CREDIT_CONTROL_REQUEST.name());
        return packetMappingData;
    }

    public static List<DiameterGwProfilePacketMapData> createDiameterGwProfilePacketMapData() {
        DiameterGwProfilePacketMapData diameterGwProfilePacketMapData = new DiameterGwProfilePacketMapData();
        diameterGwProfilePacketMapData.setCondition("\"1\"=\"1\"");
        diameterGwProfilePacketMapData.setApplicationType("GX");
        diameterGwProfilePacketMapData.setPacketMappingData(createPacketMappingData());
        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = new ArrayList<>();
        diameterGwProfilePacketMappings.add(diameterGwProfilePacketMapData);
        return diameterGwProfilePacketMappings;
    }

    public static DiameterGatewayProfileData getDiameterGatewayProfileData() {
        DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
        diameterGatewayProfileData.setName(UUID.randomUUID().toString());
        diameterGatewayProfileData.setChargingRuleInstallMode(ChargingRuleInstallMode.GROUPED.name());
        diameterGatewayProfileData.setDiameterGwProfilePacketMappings(createDiameterGwProfilePacketMapData());
        return diameterGatewayProfileData;
    }
}
