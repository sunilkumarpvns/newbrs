package com.elitecore.netvertex.core.mapping;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUserEquipmentInfoValue;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.packet.avps.threegpp.AvpUserLocationInfo;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;

import java.util.Date;

public class DiameterRequestBuilder {
   
    DiameterRequest diameterRequest = new DiameterRequest();

    public DiameterRequestBuilder() {
        
        diameterRequest = MappingUtil.createDefaultDiameterRequest();
    }

    public DiameterRequestBuilder addAvp(String id, String value) {
        diameterRequest.addAvp(id, value);
        return this;
    }

    public DiameterRequestBuilder addAvp(String id, long value) {
        diameterRequest.addAvp(id, value);
        return this;
    }

    public DiameterRequestBuilder addAvp(String id, Date value) {
        diameterRequest.addAvp(id, value);
        return this;
    }
    
    public DiameterRequestBuilder addUserEquipmentInfoAVPs() {
        
        AvpGrouped userEquipmentInfoAVP = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO);
        
        IDiameterAVP userEquipmentInfoType = dictionary().getAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE);
        userEquipmentInfoType.setInteger(0L);
        
        AvpUserEquipmentInfoValue userEquipmentInfoValue = (AvpUserEquipmentInfoValue) dictionary().getAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_VALUE);
        userEquipmentInfoValue.setStringValue("0x33353831383230363236303238343031");
        
        userEquipmentInfoValue.getIMEISV();
        
        userEquipmentInfoAVP.addSubAvp(userEquipmentInfoType);
        userEquipmentInfoAVP.addSubAvp(userEquipmentInfoValue);
        
        diameterRequest.addAvp(userEquipmentInfoAVP);
        
        return this;
    }

    private DiameterDictionary dictionary() {
        return DummyDiameterDictionary.getInstance();
    }

    public DiameterRequest build() {
        return diameterRequest;
    }

    public DiameterRequestBuilder addLocationInformationAVPs(String locationHax) {

        AvpUserLocationInfo avpUserLocationInfo =  (AvpUserLocationInfo) dictionary().getAttribute(DiameterAVPConstants.TGPP_USER_LOCATION_INFO);
        avpUserLocationInfo.setStringValue(locationHax);
    
        diameterRequest.addAvp(avpUserLocationInfo);
        
        return this;
    }

    public DiameterRequestBuilder addSubscriptionAVPs(String msisdnString, String imsiString, String sipString, String naiString) {
        
        IDiameterAVP msisdn = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
        msisdn.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_E164);
        IDiameterAVP msisdnVal = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
        msisdnVal.setStringValue(msisdnString);
        
        IDiameterAVP imsi = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
        imsi.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI);
        IDiameterAVP imsiVal = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
        imsiVal.setStringValue(imsiString);
        
        IDiameterAVP sip = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
        sip.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_SIP_URI);
        IDiameterAVP sipVal = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
        sipVal.setStringValue(sipString);

        /* IDiameterAVP nai = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
        nai.setInteger(DiameterAttributeValueConstants.DIAMETER_END_USER_NAI);
        IDiameterAVP naiVal = dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
        naiVal.setStringValue("1234456985");*/
        
        AvpGrouped subscriptionId1 = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
        subscriptionId1.addSubAvp(msisdn);
        subscriptionId1.addSubAvp(msisdnVal);

        AvpGrouped subscriptionId2 = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
        subscriptionId2.addSubAvp(imsi);
        subscriptionId2.addSubAvp(imsiVal);

        AvpGrouped subscriptionId3 = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
        subscriptionId3.addSubAvp(sip);
        subscriptionId3.addSubAvp(sipVal);

        diameterRequest.addAvp(subscriptionId1);
        diameterRequest.addAvp(subscriptionId2);
        diameterRequest.addAvp(subscriptionId3);
        
        return this;
    }

    public DiameterRequestBuilder addRequstedQoSMappings(String qciString, String aambrdlString, String mbrdlString, String aambrulString, String mbrulString) {

        AvpGrouped tgppQoSInfo = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.TGPP_QOS_INFORMATION);
        
        IDiameterAVP qci = dictionary().getAttribute(DiameterAVPConstants.TGPP_QCI);
        qci.setStringValue(qciString);
        tgppQoSInfo.addSubAvp(qci);
        
        IDiameterAVP aambrdl = dictionary().getAttribute(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_DL);
        aambrdl.setStringValue(aambrdlString);
        tgppQoSInfo.addSubAvp(aambrdl);
        
        IDiameterAVP mbrdl = dictionary().getAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_DL);
        mbrdl.setStringValue(mbrdlString);
        tgppQoSInfo.addSubAvp(mbrdl);
        
        IDiameterAVP aambrul = dictionary().getAttribute(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_UL);
        aambrul.setStringValue(aambrulString);
        tgppQoSInfo.addSubAvp(aambrul);
        
        IDiameterAVP mbrul = dictionary().getAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_UL);
        mbrul.setStringValue(mbrulString);
        tgppQoSInfo.addSubAvp(mbrul);
        
        AvpGrouped priority = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.TGPP_ALLOCATION_RETENTION_PRIORIY);
        
        IDiameterAVP priorityLevel = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRIORIY_LEVEL);
        priorityLevel.setStringValue("1");
        priority.addSubAvp(priorityLevel);
        
        IDiameterAVP preCapability = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_CAPABILITY);
        preCapability.setStringValue("1");
        priority.addSubAvp(preCapability);
        
        IDiameterAVP preVulnerability = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_VALNERABILITY);
        preVulnerability.setStringValue("1");
        priority.addSubAvp(preVulnerability);
        
        tgppQoSInfo.addSubAvp(priority);
        
        diameterRequest.addAvp(tgppQoSInfo);
        
        return this;
    }

    public DiameterRequestBuilder addDefaultEPSBearerAVPs(String qciString) {

        AvpGrouped defaultEPSBearerQoSAVP = (AvpGrouped)dictionary().getAttribute(DiameterAVPConstants.TGPP_DEFAULT_EPS_BEARER_QOS);
        
        IDiameterAVP qci = dictionary().getAttribute(DiameterAVPConstants.TGPP_QCI);
        qci.setStringValue(qciString);
        defaultEPSBearerQoSAVP.addSubAvp(qci);
        
        AvpGrouped priority = (AvpGrouped) dictionary().getAttribute(DiameterAVPConstants.TGPP_ALLOCATION_RETENTION_PRIORIY);
        
        IDiameterAVP priorityLevel = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRIORIY_LEVEL);
        priorityLevel.setStringValue("1");
        priority.addSubAvp(priorityLevel);
        
        IDiameterAVP preCapability = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_CAPABILITY);
        preCapability.setStringValue("1");
        priority.addSubAvp(preCapability);
        
        IDiameterAVP preVulnerability = dictionary().getAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_VALNERABILITY);
        preVulnerability.setStringValue("1");
        priority.addSubAvp(preVulnerability);
        
        defaultEPSBearerQoSAVP.addSubAvp(priority);
        
        diameterRequest.addAvp(defaultEPSBearerQoSAVP);
        
        
        return this;
    }

    public DiameterRequestBuilder addCCRMapping(String defaultSourceGw, String defaultCalledStation, String defaultReqNum
            , String defaultSessionId, String requestType, String ipv6, long nwRequestSupported) {

        diameterRequest = MappingUtil.createDefaultDiameterRequest(defaultSourceGw, defaultCalledStation, defaultReqNum
                , defaultSessionId, requestType, ipv6, nwRequestSupported);
        
        return this;
    }

}