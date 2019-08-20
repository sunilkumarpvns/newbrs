package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PacketApplication;
import com.elitecore.corenetvertex.constants.RevalidationMode;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.constants.UMStandard;
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
import java.util.ArrayList;
import java.util.List;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData")
@Table(name="TBLM_DIAMETER_GATEWAY_PROFILE")
public class DiameterGatewayProfileData extends DefaultGroupResourceData implements Serializable {
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
    private String gxApplicationId;
    private String gyApplicationId;
    private String rxApplicationId;
    private String s9ApplicationId;
    private String syApplicationId;
    private Integer dwInterval = 60;
    private String supportedVendorList;
    private String supportedStandard;
    private String chargingRuleInstallMode;
    private Boolean sessionCleanUpCER;
    private Boolean sessionCleanUpDPR;
    private String cerAvps;
    private String dprAvps;
    private String dwrAvps;
    private Integer socketReceiveBufferSize;
    private Boolean sendDPRCloseEvent;
    private Integer socketSendBufferSize;
    private Boolean tcpNagleAlgorithm;
    private Integer initConnectionDuration = 60;
    private List<GroovyScriptData> groovyScriptDatas;
    private String umStandard;
    private String sessionLookUpKey = PCRFKeyConstants.CS_SESSION_IPV4.val + ',' + PCRFKeyConstants.CS_SESSION_IPV6.val;
    private List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings;
    private List<DiameterGwProfilePCCRuleMappingData> diameterGwProfilePCCRuleMappings;
    private transient List<DiameterGatewayData> diameterGatewayDatas;
    private transient List<DiameterGwProfilePacketMapData> pccToGWGxPacketMappings;
    private transient List<DiameterGwProfilePacketMapData> gwToPccGxPacketMappings;

    private transient List<DiameterGwProfilePacketMapData> pccToGWGyPacketMappings;
    private transient List<DiameterGwProfilePacketMapData> gwToPccGyPacketMappings;

    private transient List<DiameterGwProfilePacketMapData> pccToGWRxPacketMappings;
    private transient List<DiameterGwProfilePacketMapData> gwToPccRxPacketMappings;
    private List<ServiceGuidingData> serviceGuidingDatas;

    public DiameterGatewayProfileData(){
        this.groovyScriptDatas = Collectionz.newArrayList();
        this.diameterGwProfilePacketMappings = Collectionz.newArrayList();
        this.diameterGwProfilePCCRuleMappings = Collectionz.newArrayList();
        this.pccToGWGxPacketMappings = new ArrayList<>();
        this.gwToPccGxPacketMappings = new ArrayList<>();
        this.pccToGWGyPacketMappings = new ArrayList<>();
        this.gwToPccGyPacketMappings = new ArrayList<>();
        this.pccToGWRxPacketMappings = new ArrayList<>();
        this.gwToPccRxPacketMappings = new ArrayList<>();
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



    @OneToMany(cascade = { CascadeType.ALL },mappedBy = "diameterGatewayProfileData" , fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<GroovyScriptData> getGroovyScriptDatas() {
        return groovyScriptDatas;
    }

    public void setGroovyScriptDatas(List<GroovyScriptData> groovyScriptsList) {
        this.groovyScriptDatas = groovyScriptsList;
    }

    @Column(name="DWR_AVPS")
    public String getDwrAvps() {
        return dwrAvps;
    }

    public void setDwrAvps(String dwrAvps) {
        this.dwrAvps = dwrAvps;
    }

    @Column(name="INIT_CONNECTION_DURATION")
    public Integer getInitConnectionDuration() {
        return initConnectionDuration;
    }

    public void setInitConnectionDuration(Integer initConnectionDuration) {
        this.initConnectionDuration = initConnectionDuration;
    }

    @Column(name="TCP_NAGLE_ALGORITHM")
    public Boolean getTcpNagleAlgorithm() {
        return tcpNagleAlgorithm;
    }

    public void setTcpNagleAlgorithm(Boolean tcpNagleAlgorithm) {
        this.tcpNagleAlgorithm = tcpNagleAlgorithm;
    }

    @Column(name="SOCKET_SEND_BUFFER_SIZE")
    public Integer getSocketSendBufferSize() {
        return socketSendBufferSize;
    }

    public void setSocketSendBufferSize(Integer socketSendBufferSize) {
        this.socketSendBufferSize = socketSendBufferSize;
    }

    @Column(name="SEND_DPR_CLOSE_EVENT")
    public Boolean getSendDPRCloseEvent() {
        return sendDPRCloseEvent;
    }

    public void setSendDPRCloseEvent(Boolean sendDPRCloseEvent) {
        this.sendDPRCloseEvent = sendDPRCloseEvent;
    }

    @Column(name="SOCKET_RECEIVE_BUFFER_SIZE")
    public Integer getSocketReceiveBufferSize() {
        return socketReceiveBufferSize;
    }

    public void setSocketReceiveBufferSize(Integer socketReceiveBufferSize) {
        this.socketReceiveBufferSize = socketReceiveBufferSize;
    }

    @Column(name="CER_AVPS")
    public String getCerAvps() {
        return cerAvps;
    }

    public void setCerAvps(String cerAvps) {
        this.cerAvps = cerAvps;
    }

    @Column(name="DPR_AVPS")
    public String getDprAvps() {
        return dprAvps;
    }

    public void setDprAvps(String dprAvps) {
        this.dprAvps = dprAvps;
    }

    @Column(name="SESSION_CLEANUP_ON_CER")
    public Boolean getSessionCleanUpCER() {
        return sessionCleanUpCER;
    }

    public void setSessionCleanUpCER(Boolean sessionCleanUpCER) {
        this.sessionCleanUpCER = sessionCleanUpCER;
    }

    @Column(name="SESSION_CLEANUP_ON_DPR")
    public Boolean getSessionCleanUpDPR() {
        return sessionCleanUpDPR;
    }

    public void setSessionCleanUpDPR(Boolean sessionCleanUpDPR) {
        this.sessionCleanUpDPR = sessionCleanUpDPR;
    }

    @Column(name="CHARGING_RULE_INSTALL_MODE")
    public String getChargingRuleInstallMode() {
        return chargingRuleInstallMode;
    }
    public void setChargingRuleInstallMode(String chargingRuleInstallMode) {
        this.chargingRuleInstallMode = chargingRuleInstallMode;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "diameterGatewayProfileData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    @JsonIgnore
    public List<DiameterGwProfilePacketMapData> getDiameterGwProfilePacketMappings() {
        return diameterGwProfilePacketMappings;
    }

    public void setDiameterGwProfilePacketMappings(List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings) {
        this.diameterGwProfilePacketMappings = diameterGwProfilePacketMappings;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "diameterGatewayProfileData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<DiameterGwProfilePCCRuleMappingData> getDiameterGwProfilePCCRuleMappings() {
        return diameterGwProfilePCCRuleMappings;
    }

    public void setDiameterGwProfilePCCRuleMappings(List<DiameterGwProfilePCCRuleMappingData> diameterGwProfilePCCRuleMappings) {
        this.diameterGwProfilePCCRuleMappings = diameterGwProfilePCCRuleMappings;
    }
    @Column(name = "TIMEOUT")
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Column(name="GX_APPLICATION_ID")
    public String getGxApplicationId() {
        return gxApplicationId;
    }

    public void setGxApplicationId(String gxApplicationId) {
        if(Strings.isNullOrBlank(gxApplicationId)){
            gxApplicationId = "10415:16777238";
        }
        this.gxApplicationId = gxApplicationId;
    }

    @Column(name="GY_APPLICATION_ID")
    public String getGyApplicationId() {
        return gyApplicationId;
    }

    public void setGyApplicationId(String gyApplicationId) {
        if(Strings.isNullOrBlank(gyApplicationId)){
            gyApplicationId = "10415:4";
        }
        this.gyApplicationId = gyApplicationId;
    }

    @Column(name="S9_APPLICATION_ID")
    public String getS9ApplicationId() {
        return s9ApplicationId;
    }

    public void setS9ApplicationId(String s9ApplicationId) {
        if(Strings.isNullOrBlank(s9ApplicationId)){
            s9ApplicationId = "10415:16777267";
        }
        this.s9ApplicationId = s9ApplicationId;
    }

    @Column(name="RX_APPLICATION_ID")
    public String getRxApplicationId() {
        return rxApplicationId;
    }

    public void setRxApplicationId(String rxApplicationId) {
        if(Strings.isNullOrBlank(rxApplicationId)){
            rxApplicationId = "10415:16777236";
        }
        this.rxApplicationId = rxApplicationId;
    }

    @Column(name="SY_APPLICATION_ID")
    public String getSyApplicationId() {
        return syApplicationId;
    }

    public void setSyApplicationId(String syApplicationId) {
        if(Strings.isNullOrBlank(syApplicationId)){
            syApplicationId = "10415:16777302";
        }
        this.syApplicationId = syApplicationId;
    }

    @Column(name="DW_INTERVAL")
    public Integer getDwInterval() {
        return dwInterval;
    }

    public void setDwInterval(Integer dwInterval) {
        this.dwInterval = dwInterval;
    }

    @Column(name="SUPPORTED_VENDOR_LIST")
    public String getSupportedVendorList() {
        return supportedVendorList;
    }

    public void setSupportedVendorList(String supportedVendorList) {
        this.supportedVendorList = supportedVendorList;
    }

    @Column(name="SUPPORTED_STANDARD")
    public String getSupportedStandard() {
        return supportedStandard;
    }

    public void setSupportedStandard(String supportedStandard) {
        this.supportedStandard = supportedStandard;
    }

    @Column(name="UM_STANDARD")
    public String getUmStandard() {
        return umStandard;
    }

    public void setUmStandard(String umStandard) {
        if (Strings.isNullOrBlank(umStandard)) {
            this.umStandard = UMStandard.TGPPR9.name();
        } else {
            this.umStandard = umStandard;
        }
    }

    @Column(name="SESSION_LOOKUP_KEY")
    public String getSessionLookUpKey() {
        return sessionLookUpKey;
    }

    public void setSessionLookUpKey(String sessionLookUpKey) {
        this.sessionLookUpKey = sessionLookUpKey;
    }


    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION,description);
        jsonObject.addProperty(FieldValueConstants.GATEWAY_TYPE, GatewayComponent.valueOf(gatewayType) != null ? GatewayComponent.valueOf(gatewayType).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.VENDOR_ID, vendorId);
        jsonObject.addProperty(FieldValueConstants.VERSION_NAME, vendorData.getName());
        jsonObject.addProperty(FieldValueConstants.FIRMWARE, firmware);
        jsonObject.addProperty(FieldValueConstants.USAGE_REPORTING_TYPE, UsageReportingType.valueOf(usageReportingType)!= null ? UsageReportingType.valueOf(usageReportingType).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.REVALIDATION_MODE, RevalidationMode.valueOf(revalidationMode) != null ? RevalidationMode.valueOf(revalidationMode).getValue() : null);
        jsonObject.addProperty(FieldValueConstants.TIMEOUT,timeout);
        jsonObject.addProperty(FieldValueConstants.GX_APPLICATION_ID, gxApplicationId);
        jsonObject.addProperty(FieldValueConstants.GY_APPLICATION_ID, gyApplicationId);
        jsonObject.addProperty(FieldValueConstants.RX_APPLICATION_ID, rxApplicationId);
        jsonObject.addProperty(FieldValueConstants.S9_APPLICATION_ID, s9ApplicationId);
        jsonObject.addProperty(FieldValueConstants.SY_APPLICATION_ID, syApplicationId);
        jsonObject.addProperty(FieldValueConstants.DW_INTERVAL, dwInterval);
        jsonObject.addProperty(FieldValueConstants.SUPPORTED_VENDOR_LIST, supportedVendorList);
        jsonObject.addProperty(FieldValueConstants.SUPPORTED_STANSARD, SupportedStandard.valueOf(supportedStandard) != null ? SupportedStandard.valueOf(supportedStandard).getName() : null);
        jsonObject.addProperty(FieldValueConstants.CHARGING_RULE_INSTALL_MODE, ChargingRuleInstallMode.valueOf(chargingRuleInstallMode) != null ? ChargingRuleInstallMode.valueOf(chargingRuleInstallMode).val : null);
        jsonObject.addProperty(FieldValueConstants.SESSION_CLEAN_UP_ON_CER,sessionCleanUpCER);
        jsonObject.addProperty(FieldValueConstants.SESSION_CLEAN_UP_ON_DPR, sessionCleanUpDPR);
        jsonObject.addProperty(FieldValueConstants.CER_AVPS, cerAvps);
        jsonObject.addProperty(FieldValueConstants.DPR_AVPS, dprAvps);
        jsonObject.addProperty(FieldValueConstants.DWR_AVPS, dwrAvps);
        jsonObject.addProperty(FieldValueConstants.SOCKET_RECEIVE_BUFFER_SIZE,socketReceiveBufferSize);
        jsonObject.addProperty(FieldValueConstants.SEND_DPR_CLOSE_EVENT,sendDPRCloseEvent);
        jsonObject.addProperty(FieldValueConstants.SOCKET_SEND_BUFFER_SIZE,socketSendBufferSize);
        jsonObject.addProperty(FieldValueConstants.TCP_NAGLE_ALGORITHM,tcpNagleAlgorithm);
        jsonObject.addProperty(FieldValueConstants.INIT_CONNECTION_DURATION,initConnectionDuration);
        jsonObject.addProperty(FieldValueConstants.UM_STANDARD, UMStandard.valueOf(umStandard) != null ? UMStandard.valueOf(umStandard).value : null);
        jsonObject.addProperty(FieldValueConstants.SESSION_LOOKUP_KEY, sessionLookUpKey);
        if(Collectionz.isNullOrEmpty(getGwToPccGxPacketMappings()) == false){
            JsonObject gwToPCCGxPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getGwToPccGxPacketMappings()){
                gwToPCCGxPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Gx Gateway To PCC Mapping", gwToPCCGxPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(getPccToGWGxPacketMappings()) == false){
            JsonObject pccToGwGxPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getPccToGWGxPacketMappings()){
                pccToGwGxPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Gx PCC To Gateway Mapping", pccToGwGxPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(getGwToPccGyPacketMappings()) == false){
            JsonObject gwToPCCGyPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getGwToPccGyPacketMappings()){
                gwToPCCGyPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Gy Gateway To PCC Mapping", gwToPCCGyPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(getPccToGWGyPacketMappings()) == false){
            JsonObject pccToGwGyPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getPccToGWGyPacketMappings()){
                pccToGwGyPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Gy PCC To Gateway Mapping", pccToGwGyPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(getGwToPccRxPacketMappings()) == false){
            JsonObject gwToPCCRxPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getGwToPccRxPacketMappings()){
                gwToPCCRxPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Rx Gateway To PCC Mapping", gwToPCCRxPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(getPccToGWRxPacketMappings()) == false){
            JsonObject pccToGwRxPacketMapping = new JsonObject();
            for(DiameterGwProfilePacketMapData diameterGwProfilePacketMap : getPccToGWRxPacketMappings()){
                pccToGwRxPacketMapping.add(diameterGwProfilePacketMap.getPacketMappingData().getName(),diameterGwProfilePacketMap.toJson());
            }
            jsonObject.add("Rx PCC To Gateway Mapping", pccToGwRxPacketMapping);
        }
        if(Collectionz.isNullOrEmpty(diameterGwProfilePCCRuleMappings) == false){
            JsonObject gwProfilePccRuleMapping = new JsonObject();
            for(DiameterGwProfilePCCRuleMappingData diameterGwProfilePccRuleMap : diameterGwProfilePCCRuleMappings){
                gwProfilePccRuleMapping.add(diameterGwProfilePccRuleMap.getPccRuleMappingData().getName(),diameterGwProfilePccRuleMap.toJson());
            }
            jsonObject.add(FieldValueConstants.DIAMETER_GW_PROFILE_PCCRULE_MAP, gwProfilePccRuleMapping);
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

    @Transient
    public String getVendorId() {
        return vendorId;
    }


    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="diameterGatewayProfileData")
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    @JsonIgnore
    public List<DiameterGatewayData> getDiameterGatewayData() {
        return diameterGatewayDatas;
    }

    public void setDiameterGatewayData(List<DiameterGatewayData> diameterGatewayDatas) {
        this.diameterGatewayDatas = diameterGatewayDatas;
    }


    @Transient
    public List<DiameterGwProfilePacketMapData> getPccToGWGxPacketMappings() {
        if (this.pccToGWGxPacketMappings.isEmpty() == false) {
            return this.pccToGWGxPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false) {
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if (ConversionType.PCC_TO_GATEWAY.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType())
                        && PacketApplication.GX.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())) {
                    pccToGWGxPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }
        return pccToGWGxPacketMappings;
    }

    public void setPccToGWGxPacketMappings(List<DiameterGwProfilePacketMapData> pccToGWGxPacketMappings) {
        this.pccToGWGxPacketMappings = pccToGWGxPacketMappings;
    }

    @Transient
    public List<DiameterGwProfilePacketMapData> getGwToPccGxPacketMappings() {
        if (this.gwToPccGxPacketMappings.isEmpty() == false) {
            return this.gwToPccGxPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false){
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if(ConversionType.GATEWAY_TO_PCC.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType()) && PacketApplication.GX.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())){
                    gwToPccGxPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }

        return gwToPccGxPacketMappings;
    }

    public void setGwToPccGxPacketMappings(List<DiameterGwProfilePacketMapData> gwToPccGxPacketMappings) {
        this.gwToPccGxPacketMappings = gwToPccGxPacketMappings;
    }


    @Transient
    public List<DiameterGwProfilePacketMapData> getPccToGWGyPacketMappings() {
        if (this.pccToGWGyPacketMappings.isEmpty() == false) {
            return this.pccToGWGyPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false){
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if(ConversionType.PCC_TO_GATEWAY.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType()) && PacketApplication.GY.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())){
                    pccToGWGyPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }
        return pccToGWGyPacketMappings;
    }

    public void setPccToGWGyPacketMappings(List<DiameterGwProfilePacketMapData> pccToGWGyPacketMappings) {
        this.pccToGWGyPacketMappings = pccToGWGyPacketMappings;
    }

    @Transient
    public List<DiameterGwProfilePacketMapData> getGwToPccGyPacketMappings() {
        if (this.gwToPccGyPacketMappings.isEmpty() == false) {
            return this.gwToPccGyPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false){
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if(ConversionType.GATEWAY_TO_PCC.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType()) && PacketApplication.GY.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())){
                    gwToPccGyPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }

        return gwToPccGyPacketMappings;
    }

    public void setGwToPccGyPacketMappings(List<DiameterGwProfilePacketMapData> gwToPccGyPacketMappings) {
        this.gwToPccGyPacketMappings = gwToPccGyPacketMappings;
    }

    //RX

    @Transient
    public List<DiameterGwProfilePacketMapData> getPccToGWRxPacketMappings() {
        if (this.pccToGWRxPacketMappings.isEmpty() == false) {
            return this.pccToGWRxPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false){
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if(ConversionType.PCC_TO_GATEWAY.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType()) && PacketApplication.RX.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())){
                    pccToGWRxPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }
        return pccToGWRxPacketMappings;
    }

    public void setPccToGWRxPacketMappings(List<DiameterGwProfilePacketMapData> pccToGWRxPacketMappings) {
        this.pccToGWRxPacketMappings = pccToGWRxPacketMappings;
    }

    @Transient
    public List<DiameterGwProfilePacketMapData> getGwToPccRxPacketMappings() {
        if (this.gwToPccRxPacketMappings.isEmpty() == false) {
            return this.gwToPccRxPacketMappings;
        }

        if(Collectionz.isNullOrEmpty(getDiameterGwProfilePacketMappings()) == false){
            getDiameterGwProfilePacketMappings().forEach(diameterGatewayProfilePacketMapping -> {
                if(ConversionType.GATEWAY_TO_PCC.name().equalsIgnoreCase(diameterGatewayProfilePacketMapping.getPacketMappingData().getType()) && PacketApplication.RX.name().equals(diameterGatewayProfilePacketMapping.getApplicationType())){
                    gwToPccRxPacketMappings.add(diameterGatewayProfilePacketMapping);
                }
            });
        }

        return gwToPccRxPacketMappings;
    }

    public void setGwToPccRxPacketMappings(List<DiameterGwProfilePacketMapData> gwToPccRxPacketMappings) {
        this.gwToPccRxPacketMappings = gwToPccRxPacketMappings;
    }

    @OneToMany(cascade = { CascadeType.ALL },mappedBy = "diameterGatewayProfileData" , fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNumber ASC")
    public List<ServiceGuidingData> getServiceGuidingDatas() {
        return serviceGuidingDatas;
    }

    public void setServiceGuidingDatas(List<ServiceGuidingData> serviceGuidingDatas) {
        this.serviceGuidingDatas = serviceGuidingDatas;
    }
}