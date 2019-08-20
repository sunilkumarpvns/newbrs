package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;

import java.util.*;
import java.util.stream.Collectors;

public enum RadApplicationPacketType {

    AR ("AR", PacketType.ACCESS_REQUEST, ConversionType.GATEWAY_TO_PCC) {
        @Override
        public RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings,
                                                           RadiusGatewayProfileData radiusGatewayProfileData) {
            return new RadiusToPCCMapping.RADIUSToPCCMappingBuilder().withARMappings().withConfiguredMapping(mappings).build();
        }

        @Override
        public PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                           PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                           RadiusGatewayProfileData radiusGatewayProfileData,
                                                           RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping) {
            throw new UnsupportedOperationException("PCC to RADIUS mapping is not supported for " + this.name());
        }
    },

    AA ("AA", PacketType.ACCESS_ACCEPT, ConversionType.PCC_TO_GATEWAY) {
        @Override
        public RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings,
                                                           RadiusGatewayProfileData radiusGatewayProfileData) {
            throw new UnsupportedOperationException("RADIUS to PCC mapping is not supported for " + this.name());
        }

        @Override
        public PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                           PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                           RadiusGatewayProfileData radiusGatewayProfileData,
                                                           RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping) {
            return new PCCToRadiusMapping.PCCToRADIUSMappingBuilder().withDefaultPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE).withConfiguredMapping(mappings).build();
        }
    },

    COA ("COA", PacketType.CHANGE_OF_AUTHORIZATION, ConversionType.PCC_TO_GATEWAY) {
        @Override
        public RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings, RadiusGatewayProfileData radiusGatewayProfileData) {
            throw new UnsupportedOperationException("RADIUS to PCC mapping is not supported for " + this.name());
        }

        @Override
        public PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                           PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                           RadiusGatewayProfileData radiusGatewayProfileData,
                                                           RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping) {
            return new PCCToRadiusMapping.PCCToRADIUSMappingBuilder().withDefaultPacketType(RadiusConstants.COA_REQUEST_MESSAGE).withConfiguredMapping(mappings).build();
        }
    },

    ACR ("ACR", PacketType.ACCOUNTING_REQUEST, ConversionType.GATEWAY_TO_PCC) {
        @Override
        public RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings,
                                                           RadiusGatewayProfileData radiusGatewayProfileData) {
            return new RadiusToPCCMapping.RADIUSToPCCMappingBuilder().withACRMappings().withConfiguredMapping(mappings).build();
        }

        @Override
        public PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                           PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                           RadiusGatewayProfileData radiusGatewayProfileData,
                                                           RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping) {
            throw new UnsupportedOperationException("PCC to RADIUS mapping is not supported for " + this.name());
        }
    },

    DCR ("DCR", PacketType.DISCONNECT_REQUEST, ConversionType.PCC_TO_GATEWAY) {
        @Override
        public RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings,
                                                           RadiusGatewayProfileData radiusGatewayProfileData) {
            throw new UnsupportedOperationException("RADIUS to PCC mapping is not supported for " + this.name());
        }

        @Override
        public PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                           PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                           RadiusGatewayProfileData radiusGatewayProfileData,
                                                           RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping) {
            return new PCCToRadiusMapping.PCCToRADIUSMappingBuilder().withDefaultPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE).withConfiguredMapping(mappings).build();
        }
    };

    private String applicationInterface;
    private final PacketType packetType;
    private ConversionType conversionType;
    private static Map<String, RadApplicationPacketType> applicationPacketTypeByName;
    private static final List<String> radApplicationPacketTypes;

    static {

        applicationPacketTypeByName = new HashMap<>();
        for (RadApplicationPacketType radApplicationPacketType : RadApplicationPacketType.values()) {
            applicationPacketTypeByName.put(radApplicationPacketType.packetType.getPacketType()
                    , radApplicationPacketType);

        }

        radApplicationPacketTypes = Arrays.stream(RadApplicationPacketType.values()).map(t -> t.applicationInterface).collect(Collectors.toList());
    }

    RadApplicationPacketType(String applicationInterface, PacketType packetType, ConversionType conversionType) {
        this.applicationInterface = applicationInterface;
        this.packetType = packetType;
        this.conversionType = conversionType;
    }

    public static RadApplicationPacketType fromString(String packetType) {
        return applicationPacketTypeByName.get(packetType);
    }

    private boolean isPccToGateway() {
        return this.conversionType == ConversionType.PCC_TO_GATEWAY;
    }

    private boolean isGatewayToPcc() {
        return this.conversionType == ConversionType.GATEWAY_TO_PCC;
    }

    public static List<RadApplicationPacketType> getGatwayToPccAppPacketType() {
        return Arrays.stream(RadApplicationPacketType.values()).
                filter(RadApplicationPacketType::isGatewayToPcc).
                collect(Collectors.toList());
    }

    public static List<RadApplicationPacketType> getPccToGatetypeAppPacketType() {
        return Arrays.stream(RadApplicationPacketType.values()).
                filter(RadApplicationPacketType::isPccToGateway).
                collect(Collectors.toList());
    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public abstract RadiusToPCCMapping createRadiusToPCCMapping(LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> mappings,
                                                                RadiusGatewayProfileData radiusGatewayProfileData);

    public abstract PCCToRadiusMapping createPCCToRadiusMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings,
                                                                PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                                                RadiusGatewayProfileData radiusGatewayProfileData,
                                                                RadiusChargingRuleDefinitionPacketMapping radiusChargingRuleDefinitionPacketMapping);

    public static boolean contains(String type) {
        return radApplicationPacketTypes.contains(type);
    }

    public static List<String> getRadApplicationPacketTypes() {
        return Arrays.stream(RadApplicationPacketType.values()).map(t -> t.applicationInterface).collect(Collectors.toList());
    }
}
