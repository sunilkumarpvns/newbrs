package com.elitecore.diameterapi.diameter.common.util.dictionary;

import com.elitecore.diameterapi.diameter.common.packet.avps.BaseAVPBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiameterDictionaryModel {

    private Map<String, VendorInformation> idToVendorinformation = new HashMap<>();
    private List<String> vendorIds = new ArrayList<>();
    private Map<String, AttributeData> idToAttributeData = new HashMap<>();
    private Map<AttributeData, BaseAVPBuilder> attributeDataToAVPBuilder = new HashMap<>();

    public DiameterDictionaryModel(Map<String, VendorInformation> idToVendorinformation,
                                   List<String> vendorIds) {
        this.idToVendorinformation = idToVendorinformation;
        this.vendorIds = vendorIds;
    }

    public Map<String, VendorInformation> getIdtoVendorInformation() {
        return idToVendorinformation;
    }

    public List<String> getVendorIds() {
        return vendorIds;
    }

    public void setVendorIds(List<String> vendorIds) {
        this.vendorIds = vendorIds;
    }

    public Map<String, AttributeData> getIdToAttributeData() {
        return idToAttributeData;
    }

    public void setIdToAttributeData(Map<String, AttributeData> idToAttributeData) {
        this.idToAttributeData = idToAttributeData;
    }

    public Map<AttributeData, BaseAVPBuilder> getAttributeDataToAVPBuilder() {
        return attributeDataToAVPBuilder;
    }

    public void setAttributeDataToAVPBuilder(Map<AttributeData, BaseAVPBuilder> attributeDataToAVPBuilder) {
        this.attributeDataToAVPBuilder = attributeDataToAVPBuilder;
    }

}
