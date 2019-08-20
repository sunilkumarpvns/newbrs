package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Collectionz;

import java.util.List;

public enum ApplicationPacketType {
    CREDIT_CONTROL_REQUEST("CCR" ,  ConversionType.GATEWAY_TO_PCC,PacketApplication.GX,PacketApplication.GY),
    CREDIT_CONTROL_RESPONSE("CCA" , ConversionType.PCC_TO_GATEWAY,PacketApplication.GX,PacketApplication.GY),

    RE_AUTH_REQUEST("RAR" , ConversionType.PCC_TO_GATEWAY,PacketApplication.GX,PacketApplication.GY),
    RE_AUTH_RESPONSE("RAA" ,ConversionType.GATEWAY_TO_PCC,PacketApplication.GX,PacketApplication.GY),

    ABORT_SESSION_REQUEST("ASR" , ConversionType.PCC_TO_GATEWAY,PacketApplication.RX),
    ABORT_SESSION_RESPONSE("ASA" , ConversionType.GATEWAY_TO_PCC,PacketApplication.RX),

    AUTHENTICATE_AUTHORIZE_REQUEST("AAR" , ConversionType.GATEWAY_TO_PCC,PacketApplication.RX),
    AUTHENTICATE_AUTHORIZE_RESPONSE("AAA" , ConversionType.PCC_TO_GATEWAY,PacketApplication.RX),

    SESSION_TERMINATION_REQUEST("STR" , ConversionType.GATEWAY_TO_PCC,PacketApplication.RX),
    SESSION_TERMINATION_RESPONSE("STA" ,  ConversionType.PCC_TO_GATEWAY,PacketApplication.RX);


    private final String type;
    private final ConversionType conversionType;
    private PacketApplication[] applicationType;

    private ApplicationPacketType(String packetName,
                       ConversionType conversionType,
                                  PacketApplication... applicationType){
        this.type =packetName;
        this.conversionType=conversionType;
        this.applicationType = applicationType;
    }

    public static List<String> getPacketTypeFromApplication(PacketApplication packetApplication,ConversionType conversionType){
        List<String> packetTypeBasedOnApplicationAndConversationType= Collectionz.newArrayList();
        for(ApplicationPacketType applicationPacketType : values()){
           for(PacketApplication applicationVal : applicationPacketType.applicationType){
               if(packetApplication.name().equals(applicationVal.name()) && applicationPacketType.conversionType.name().equals(conversionType.name())){
                packetTypeBasedOnApplicationAndConversationType.add(applicationPacketType.name());
               }
           }
        }
        return packetTypeBasedOnApplicationAndConversationType;
    }
}
