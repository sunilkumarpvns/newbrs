package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.RevalidationMode;
import com.elitecore.corenetvertex.constants.UsageReportingType;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData")
@Table(name = "TBLM_RADIUS_GATEWAY_PROFILE")
public class RadiusGatewayProfileData extends DefaultGroupResourceData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String gatewayType;
    private transient VendorData vendorData;
    private String vendorId;
    private String firmware;
    private String usageReportingType;
    private String revalidationMode;
    private Integer timeout;
    private Integer maxRequestTimeout;
    private Integer statusCheckDuration;
    private Integer retryCount;
    private Boolean icmpPingEnabled;
    private String supportedVendorList;
    private Boolean sendAccountingResponse;
    private Integer interimInterval;
    private transient List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings;
    private transient List<RadiusGwProfilePacketMapData> pccToGWPacketMappings;
    private transient List<RadiusGwProfilePacketMapData> gwToPCCPacketMappings;
    private List<GroovyScriptData> groovyScriptDatas;
    private List<RadiusGwProfilePCCRuleMappingData> radiusGwProfilePCCRuleMappings;
    private transient List<RadiusGatewayData> radiusGatewayDatas;
    private List<ServiceGuidingData> serviceGuidingDatas;

    public RadiusGatewayProfileData(){
        this.groovyScriptDatas = Collectionz.newArrayList();
        this.radiusGwProfilePacketMappings = Collectionz.newArrayList();
        this.radiusGwProfilePCCRuleMappings = Collectionz.newArrayList();
        this.gwToPCCPacketMappings = Collectionz.newArrayList();
        this.pccToGWPacketMappings = Collectionz.newArrayList();
        this.serviceGuidingDatas = Collectionz.newArrayList();
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="REVALIDATION_MODE")
    public String getRevalidationMode() {
        return revalidationMode;
    }

    public void setRevalidationMode(String revalidationMode) {
        this.revalidationMode = revalidationMode;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="TYPE")
    public String getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="VENDOR_ID")
    @JsonIgnore
    public VendorData getVendorData() {
        return vendorData;
    }

    public void setVendorData(VendorData vendorData) {
        setVendorId(vendorData.getId());
        this.vendorData = vendorData;
    }

    @Column(name="FIRMWARE")
    public String getFirmware() {
        return firmware;
    }
    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    @Column(name="USAGE_REPORTING_TYPE")
    public String getUsageReportingType() {
        return usageReportingType;
    }

    public void setUsageReportingType(String usageReportingType) {
        this.usageReportingType = usageReportingType;
    }

    @Column( name = "TIMEOUT")
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Column( name = "MAX_REQUEST_TIMEOUT")
    public Integer getMaxRequestTimeout() {
        return maxRequestTimeout;
    }

    public void setMaxRequestTimeout(Integer maxRequestTimeout) {
        this.maxRequestTimeout = maxRequestTimeout;
    }

    @Column( name = "STATUS_CHECK_DURATION")
    public Integer getStatusCheckDuration() {
        return statusCheckDuration;
    }

    public void setStatusCheckDuration(Integer statusCheckDuration) {
        this.statusCheckDuration = statusCheckDuration;
    }

    @Column( name = "RETRY_COUNT")
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Column( name = "IS_ICMP_PING_ENABLED")
    public Boolean getIcmpPingEnabled() {
        return icmpPingEnabled;
    }

    public void setIcmpPingEnabled(Boolean icmpPingEnabled) {
        this.icmpPingEnabled = icmpPingEnabled;
    }

    @Column( name = "SEND_ACCOUNTING_RESPONSE")
    public Boolean getSendAccountingResponse() {
        return sendAccountingResponse;
    }

    public void setSendAccountingResponse(Boolean sendAccountingResponse) {
        this.sendAccountingResponse = sendAccountingResponse;
    }

    @Column( name = "SUPPORTED_VENDOR_LIST")
    public String getSupportedVendorList() {
        return supportedVendorList;
    }

    public void setSupportedVendorList(String supportedVendorList) {
        this.supportedVendorList = supportedVendorList;
    }

    @Column( name = "INTERIM_INTERVAL")
    public Integer getInterimInterval() {
        return interimInterval;
    }

    public void setInterimInterval(Integer interimInterval) {
        this.interimInterval = interimInterval;
    }


    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "radiusGatewayProfileData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<RadiusGwProfilePCCRuleMappingData> getRadiusGwProfilePCCRuleMappings() {
        return radiusGwProfilePCCRuleMappings;
    }
    public void setRadiusGwProfilePCCRuleMappings(
            List<RadiusGwProfilePCCRuleMappingData> profileRuleMappingList) {
        this.radiusGwProfilePCCRuleMappings = profileRuleMappingList;

    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "radiusGatewayProfileData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    @JsonIgnore
    public List<RadiusGwProfilePacketMapData> getRadiusGwProfilePacketMappings() {
        return radiusGwProfilePacketMappings;
    }

    public void setRadiusGwProfilePacketMappings(
            List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings) {
        this.radiusGwProfilePacketMappings = radiusGwProfilePacketMappings;
    }

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "radiusGatewayProfileData" ,fetch = FetchType.LAZY, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<GroovyScriptData> getGroovyScriptDatas() {
        return groovyScriptDatas;
    }

    public void setGroovyScriptDatas(List<GroovyScriptData> groovyScriptDatas) {
        this.groovyScriptDatas = groovyScriptDatas;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Transient
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.GATEWAY_TYPE, GatewayComponent.valueOf(gatewayType) != null ? GatewayComponent.valueOf(gatewayType).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.VENDOR_ID, vendorId);
        jsonObject.addProperty(FieldValueConstants.VENDOR_NAME, vendorData.getName());
        jsonObject.addProperty(FieldValueConstants.FIRMWARE, firmware);
        jsonObject.addProperty(FieldValueConstants.USAGE_REPORTING_TYPE, UsageReportingType.valueOf(usageReportingType)!= null ? UsageReportingType.valueOf(usageReportingType).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.REVALIDATION_MODE, RevalidationMode.valueOf(revalidationMode) != null ? RevalidationMode.valueOf(revalidationMode).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.TIMEOUT, timeout);
        jsonObject.addProperty(FieldValueConstants.MAX_QUERY_TIMEOUT_COUNT, maxRequestTimeout);
        jsonObject.addProperty(FieldValueConstants.STATUS_CHECK_DURATION, statusCheckDuration);
        jsonObject.addProperty(FieldValueConstants.RETRY_COUNT, retryCount);
        jsonObject.addProperty(FieldValueConstants.ICMP_PING_ENABLE, icmpPingEnabled);
        jsonObject.addProperty(FieldValueConstants.SUPPORTED_VENDOR_LIST, supportedVendorList);
        jsonObject.addProperty(FieldValueConstants.SEND_ACCOUNTING_RESPONSE, sendAccountingResponse);
        jsonObject.addProperty(FieldValueConstants.INTERIM_INTERVAL, interimInterval);
        if(Collectionz.isNullOrEmpty(radiusGwProfilePacketMappings) == false){
            JsonObject gwProfilePacketMapping = new JsonObject();
            for(RadiusGwProfilePacketMapData radiusGwProfilePacketMap : radiusGwProfilePacketMappings){
                gwProfilePacketMapping.add(radiusGwProfilePacketMap.getPacketMappingData().getName(),radiusGwProfilePacketMap.toJson());
            }
            jsonObject.add(FieldValueConstants.RADIUS_GW_PROFILE_PACKET_MAP, gwProfilePacketMapping);
        }
        if(Collectionz.isNullOrEmpty(radiusGwProfilePCCRuleMappings) == false){
            JsonObject gwProfilePccRuleMapping = new JsonObject();
            for(RadiusGwProfilePCCRuleMappingData radiusGwProfilePccRuleMap : radiusGwProfilePCCRuleMappings){
                gwProfilePccRuleMapping.add(radiusGwProfilePccRuleMap.getPccRuleMappingData().getName(),radiusGwProfilePccRuleMap.toJson());
            }
            jsonObject.add(FieldValueConstants.RADIUS_GW_PROFILE_PCCRULE_MAP, gwProfilePccRuleMapping);
        }
        if(Collectionz.isNullOrEmpty(groovyScriptDatas) == false){
            JsonObject groovyScriptJson = new JsonObject();
            for(GroovyScriptData groovyScriptData : groovyScriptDatas){
                groovyScriptJson.add(groovyScriptData.getScriptName(),groovyScriptData.toJson());
            }
            jsonObject.add(FieldValueConstants.GROOVY_SCRIPT_DATA, groovyScriptJson);
        }
        if(Collectionz.isNullOrEmpty(serviceGuidingDatas) == false){
            JsonObject gwProfileServiceGuiding = new JsonObject();
            for(ServiceGuidingData serviceGuidingData : serviceGuidingDatas){
                gwProfileServiceGuiding.add(serviceGuidingData.getServiceData().getName(),serviceGuidingData.toJson());
            }
            jsonObject.add(FieldValueConstants.SERVICE_GUIDING, gwProfileServiceGuiding);
        }
        return jsonObject;
    }

    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="radiusGatewayProfileData")
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    @JsonIgnore
    public List<RadiusGatewayData> getRadiusGatewayDatas() {
        return radiusGatewayDatas;
    }

    public void setRadiusGatewayDatas(List<RadiusGatewayData> radiusGatewayDatas) {
        this.radiusGatewayDatas = radiusGatewayDatas;
    }



    @Transient
    public List<RadiusGwProfilePacketMapData> getPccToGWPacketMappings() {
        if(Collectionz.isNullOrEmpty(getRadiusGwProfilePacketMappings()) == false){
            getRadiusGwProfilePacketMappings().forEach(radiusGatewayProfilePacketMapping -> {
                if(ConversionType.PCC_TO_GATEWAY.name().equalsIgnoreCase(radiusGatewayProfilePacketMapping.getPacketMappingData().getType())){
                    pccToGWPacketMappings.add(radiusGatewayProfilePacketMapping);
                }
            });
        }
        return pccToGWPacketMappings;
    }

    public void setPccToGWPacketMappings(List<RadiusGwProfilePacketMapData> pccToGWPacketMappings) {
        this.pccToGWPacketMappings = pccToGWPacketMappings;
    }

    @Transient
    public List<RadiusGwProfilePacketMapData> getGwToPCCPacketMappings() {

        if(Collectionz.isNullOrEmpty(getRadiusGwProfilePacketMappings()) == false){
            getRadiusGwProfilePacketMappings().forEach(radiusGatewayProfilePacketMapping -> {
                if(ConversionType.GATEWAY_TO_PCC.name().equalsIgnoreCase(radiusGatewayProfilePacketMapping.getPacketMappingData().getType())){
                    gwToPCCPacketMappings.add(radiusGatewayProfilePacketMapping);
                }
            });
        }

        return gwToPCCPacketMappings;
    }

    public void setGwToPCCPacketMappings(List<RadiusGwProfilePacketMapData> gwToPCCPacketMappings) {
        this.gwToPCCPacketMappings = gwToPCCPacketMappings;
    }

    @OneToMany(cascade = { CascadeType.ALL },mappedBy = "radiusGatewayProfileData" , fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<ServiceGuidingData> getServiceGuidingDatas() {
        return serviceGuidingDatas;
    }

    public void setServiceGuidingDatas(List<ServiceGuidingData> serviceGuidingDatas) {
        this.serviceGuidingDatas = serviceGuidingDatas;
    }
}
