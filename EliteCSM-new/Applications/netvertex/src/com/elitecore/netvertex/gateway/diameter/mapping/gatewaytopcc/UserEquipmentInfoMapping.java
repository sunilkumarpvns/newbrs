package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUserEquipmentInfoValue;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.Set;

/**
 * Created by harsh on 6/28/17.
 */
public class UserEquipmentInfoMapping implements DiameterToPCCPacketMapping {

    private final String userEquipmentInfoId;

    public UserEquipmentInfoMapping(String userEquipmentInfoId) {
        this.userEquipmentInfoId = userEquipmentInfoId;
    }

    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        IDiameterAVP userEquipmentInfo = valueProvider.getDiameterRequest().getAVP(userEquipmentInfoId);

        if(userEquipmentInfo == null) {
            return;
        }

        Set<String> keySet = userEquipmentInfo.getKeySet();

        if(Collectionz.isNullOrEmpty(keySet)) {
            return;
        }

        for(String key : keySet){
            if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.MAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_MAC.getVal(), userEquipmentInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.SVN)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_SVN.getVal(), userEquipmentInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.SNR)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_SNR.getVal(), userEquipmentInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.TAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_TAC.getVal(), userEquipmentInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.MODIFIED_EUI64)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_MODIFIED_EUI64.getVal(), userEquipmentInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserEquipmentInfoValue.EUI64)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_EUI64.getVal(), userEquipmentInfo.getKeyStringValue(key));
            }
        }
    }
}
