package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harsh on 6/28/17.
 *
 * This Mapping will be applied to all Diameter Request irrespective of Application
 */
public class DiameterRequestAVPMapping implements DiameterToPCCPacketMapping {

    private List<DiameterToPCCPacketMapping> defaultMappingList;

    public  DiameterRequestAVPMapping(String locationAvpId, String ratTypeId, String ipCanType, String userEquipmentInfoId) {
        defaultMappingList = new ArrayList<>();
        defaultMappingList.add(new LocationInfoAvpMapping(locationAvpId));
        defaultMappingList.add(new UserEquipmentInfoMapping(userEquipmentInfoId));
        defaultMappingList.add(new AccessNetworkAvpMapping(ratTypeId, ipCanType));
        defaultMappingList.add(new SubscriptionIdAvpMapping());
    }

    public  DiameterRequestAVPMapping(List<DiameterToPCCPacketMapping> mappings) {
        defaultMappingList = new ArrayList<>();
        defaultMappingList.addAll(mappings);
    }
    
    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        DiameterRequest diameterPacket = valueProvider.getDiameterRequest();
        //	set Framed-ip-Address AVP
        if(diameterPacket.getAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX) != null) {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_IPV6.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.FRAMED_IPV6_PREFIX).substring(5));
        }

        if (diameterPacket.getAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS) != null) {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.FRAMED_IP_ADDRESS));
        }

        if(diameterPacket.getAVP(DiameterAVPConstants.CALLED_STATION_ID) != null) {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.CALLED_STATION_ID));
        }
        
        if (diameterPacket.getAVP(DiameterAVPConstants.CALLING_STATION_ID) != null) {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.CALLING_STATION_ID));
        }

        IDiameterAVP networkRequestSupportAVP = diameterPacket.getAVP(DiameterAVPConstants.NETWORK_REQUEST_SUPPORT);
        if(networkRequestSupportAVP != null) {
            if (networkRequestSupportAVP.getInteger() == DiameterAttributeValueConstants.NETWORK_REQUEST_SUPPORTED) {
                pcrfRequest.setAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_SUPPORTED.val);
            } else {
                pcrfRequest.setAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_NOT_SUPPORTED.val);
            }
        }


        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.TGPP_SGSN_MCC_MNC));
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
        //	set Origin-Host AVP
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
        //	set Origin-Realm
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_REALM));
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(),diameterPacket.getInfoAVPValue(DiameterAVPConstants.EC_REQUESTER_ID));

        pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.val, GatewayTypeConstant.DIAMETER.val);
        pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_IP.val, diameterPacket.getInfoAVPValue(DiameterAVPConstants.EC_SOURCE_IP_ADDRESS));
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, valueProvider.getConfiguration().getName());
        
        defaultMappingList.forEach(mapping -> mapping.apply(valueProvider));
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Diameter Request AVP Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.newline();
        builder.appendChildObject("Default Mapping", defaultMappingList);
    }
}
