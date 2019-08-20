package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.threegpp.AvpUserLocationInfo;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.Set;

/**
 * Created by harsh on 6/28/17.
 */
public class LocationInfoAvpMapping implements DiameterToPCCPacketMapping {
    
    private String locationInfoAVP;

    public LocationInfoAvpMapping(String locationInfoAVP) {
        this.locationInfoAVP = locationInfoAVP;
    }
    
    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        IDiameterAVP tgppUserLocationInfo = valueProvider.getDiameterRequest().getAVP(locationInfoAVP);

        if(tgppUserLocationInfo == null){
            return;
        }

        Set<String> keySet = tgppUserLocationInfo.getKeySet();
        if(Collectionz.isNullOrEmpty(keySet)){
            return;
        }

        for(String key : keySet){
            if(key.equalsIgnoreCase(AvpUserLocationInfo.LOCATION_TYPE)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_TYPE.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.CGI_CI)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_CI.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.CGI_LAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_LAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.CGI_MCC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.CGI_MNC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.ECGI_ECI)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_ECGI_ECI.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.ECGI_MCC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.ECGI_MNC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.ECGI_SPARE)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_ECGI_SPARE.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.RAI_LAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_LAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.RAI_MCC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.RAI_MNC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.RAI_RAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_RAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.SAI_LAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_LAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.SAI_MCC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.SAI_MNC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.SAI_SAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_SAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.TAC_MCC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.TAC_MNC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            } else if(key.equalsIgnoreCase(AvpUserLocationInfo.TAC_TAC)) {
                pcrfRequest.setAttribute(PCRFKeyConstants.LOCATION_TAC.getVal(), tgppUserLocationInfo.getKeyStringValue(key));
            }
        }
    }
}
