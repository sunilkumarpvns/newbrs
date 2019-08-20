package com.elitecore.corenetvertex.data;

import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RADIUSGatewayDataBuilder {

    public static AttributeMappingData createAttributeMappingData() {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        return attributeMappingData;
    }

    public static PacketMappingData createPacketMappingData() {
        PacketMappingData packetMappingData = new PacketMappingData();
        packetMappingData.setType("AR");
        packetMappingData.setApplicationType("AR");
        packetMappingData.setAttributeMappingData(createAttributeMappingData());
        packetMappingData.setPacketType(PacketType.ACCESS_REQUEST.name());
        return packetMappingData;
    }

    public static List<RadiusGwProfilePacketMapData> createRADIUSGwProfilePacketMapData() {
        RadiusGwProfilePacketMapData radiusGwProfilePacketMapData = new RadiusGwProfilePacketMapData();
        radiusGwProfilePacketMapData.setCondition("\"1\"=\"1\"");
        radiusGwProfilePacketMapData .setPacketMappingData(createPacketMappingData());
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = new ArrayList<>();
        radiusGwProfilePacketMappings.add(radiusGwProfilePacketMapData);
        return radiusGwProfilePacketMappings;
    }

    public static RadiusGatewayProfileData getRADIUSGatewayProfileData() {
        RadiusGatewayProfileData radiusGatewayProfileData = new RadiusGatewayProfileData();
        radiusGatewayProfileData.setName(UUID.randomUUID().toString());
        radiusGatewayProfileData.setRadiusGwProfilePacketMappings(createRADIUSGwProfilePacketMapData());
        return radiusGatewayProfileData;
    }
}
