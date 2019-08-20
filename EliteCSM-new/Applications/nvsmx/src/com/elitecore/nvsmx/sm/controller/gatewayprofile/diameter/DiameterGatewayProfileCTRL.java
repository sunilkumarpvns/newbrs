package com.elitecore.nvsmx.sm.controller.gatewayprofile.diameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.ApplicationPacketType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.PacketApplication;
import com.elitecore.corenetvertex.constants.ProtocolType;
import com.elitecore.corenetvertex.constants.SupportedVendor;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePacketMapData;
import com.elitecore.corenetvertex.sm.gateway.GroovyScriptData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
import com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData;
import com.elitecore.corenetvertex.sm.gateway.VendorData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Diameter Gateway Profile class that will perform CRUD operations.
 */
@ParentPackage(value = "sm")
@Namespace("/sm/gatewayprofile")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","diameter-gateway-profile"}),
})
public class DiameterGatewayProfileCTRL extends RestGenericCTRL<DiameterGatewayProfileData> {

    private static final Predicate<DiameterGwProfilePacketMapData> DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE = diameterGwProfilePacketMapData -> {
        if (diameterGwProfilePacketMapData == null) {
            return false;
        }
        return diameterGwProfilePacketMapData.getPacketMappingId() != null;

    };
    private static final Predicate<DiameterGwProfilePCCRuleMappingData> DIAMETER_GW_PROFILE_PCCRULE_MAP_DATA_PREDICATE = diameterGwProfilePccRuleMapData -> {
        if (diameterGwProfilePccRuleMapData == null) {
            return false;
        }
        return diameterGwProfilePccRuleMapData.getPccRuleMappingId() != null;

    };
    private static final Predicate<GroovyScriptData> GROOVY_PREDICATE = groovyScript -> groovyScript == null ? false : groovyScript.getScriptName() !=null;
    private static final Predicate<ServiceGuidingData> SERVICE_GUIDING_PREDICATE = serviceGuiding -> serviceGuiding == null ? false : true;
    private List<VendorData> vendorDataList;
    private List<String> supportedVendorDataList;
    private List<String> sessionKeyList;
    private List<String> supportedStandardValuesForUpdate = Collectionz.newArrayList();
    private List<String> sessionKeysForUpdate = Collectionz.newArrayList();
    private List<PacketMappingData> gatewayToPCCPacketMappingGxList = Collectionz.newArrayList();
    private List<PacketMappingData> pccToGatewayPacketMappingGxList = Collectionz.newArrayList();

    private List<PacketMappingData> gatewayToPCCPacketMappingGyList = Collectionz.newArrayList();
    private List<PacketMappingData> pccToGatewayPacketMappingGyList = Collectionz.newArrayList();

    private List<PacketMappingData> gatewayToPCCPacketMappingRxList = Collectionz.newArrayList();
    private List<PacketMappingData> pccToGatewayPacketMappingRxList = Collectionz.newArrayList();
    private List<DiameterGwProfilePacketMapData> packetMappingAssociationList = Collectionz.newArrayList();
    private List<DiameterGwProfilePCCRuleMappingData> pccRuleMappingAssociationList = Collectionz.newArrayList();

    private List<PCCRuleMappingData> pccRuleMappings = Collectionz.newArrayList();

    private List<GroovyScriptData> groovyScriptList = Collectionz.newArrayList();

    private List<ServiceGuidingData> serviceGuidings = Collectionz.newArrayList();
    private String serviceDataJson;
    private List<ServiceData> serviceDatas = Collectionz.newArrayList();

    private static final Criterion PROTOCOL_CRITERIA = Restrictions.eq("commProtocol", ProtocolType.DIAMETER.name());

    private static final Criterion GW_TO_PCRF_TYPE_GX = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.GX,ConversionType.GATEWAY_TO_PCC));
    private static final Criterion GW_TO_PCC_PACKET_MAPPING_CRITERIA_GX = Restrictions.and(PROTOCOL_CRITERIA,GW_TO_PCRF_TYPE_GX);

    private static final Criterion GW_TO_PCRF_TYPE_GY = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.GY,ConversionType.GATEWAY_TO_PCC));
    private static final Criterion GW_TO_PCC_PACKET_MAPPING_CRITERIA_GY = Restrictions.and(PROTOCOL_CRITERIA,GW_TO_PCRF_TYPE_GY);

    private static final Criterion GW_TO_PCRF_TYPE_RX = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.RX,ConversionType.GATEWAY_TO_PCC));
    private static final Criterion GW_TO_PCC_PACKET_MAPPING_CRITERIA_RX = Restrictions.and(PROTOCOL_CRITERIA,GW_TO_PCRF_TYPE_RX);


    private static final Criterion PCC_TO_GW_TYPE_GX = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.GX,ConversionType.PCC_TO_GATEWAY));
    private static final Criterion PCC_TO_GW_PACKET_MAPPING_CRITERIA_GX = Restrictions.and(PROTOCOL_CRITERIA,PCC_TO_GW_TYPE_GX);

    private static final Criterion PCC_TO_GW_TYPE_GY = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.GY,ConversionType.PCC_TO_GATEWAY));
    private static final Criterion PCC_TO_GW_PACKET_MAPPING_CRITERIA_GY = Restrictions.and(PROTOCOL_CRITERIA,PCC_TO_GW_TYPE_GY);

    private static final Criterion PCC_TO_GW_TYPE_RX = Restrictions.in("packetType", ApplicationPacketType.getPacketTypeFromApplication(PacketApplication.RX,ConversionType.PCC_TO_GATEWAY));
    private static final Criterion PCC_TO_GW_PACKET_MAPPING_CRITERIA_RX = Restrictions.and(PROTOCOL_CRITERIA,PCC_TO_GW_TYPE_RX);

    @Override
    @SkipValidation
    public HttpHeaders index() {
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.DIAMETER.name());
        HttpHeaders result = super.index();
        setActionChainUrl("sm/gatewayprofile/gateway-profile/index");
        return result;
    }

    @Override
    public HttpHeaders create() { // update
        try {
            setVendorBasedOnId();
            setGroovyScriptDatas();
            setServiceGuidingDatas();
            return super.create();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Create Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    private void setServiceGuidingDatas() {
        DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
        if(Collectionz.isNullOrEmpty(diameterGatewayProfileData.getServiceGuidingDatas()) == false) {
            int index = 1;
            for (ServiceGuidingData serviceGuidingData : diameterGatewayProfileData.getServiceGuidingDatas()) {
                serviceGuidingData.setDiameterGatewayProfileData(diameterGatewayProfileData);
                serviceGuidingData.setOrderNumber(index);
                index++;
            }
        }
    }

    @Override
    public String edit() { // initUpdate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called edit()");
        }
        try {
            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            if(Strings.isNullOrBlank(diameterGatewayProfileData.getId()) == false) {
                diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
                setModel(diameterGatewayProfileData);
            }
            List<String> supportedVendors = Strings.splitter(',').trimTokens().split(diameterGatewayProfileData.getSupportedVendorList());
            if (Collectionz.isNullOrEmpty(supportedVendors) == false) {
                setSupportedStandardValuesForUpdate(supportedVendors);
                supportedVendors.forEach(supportedVendor -> {
                    if (getSupportedVendorDataList().contains(supportedVendor) == false) {
                        getSupportedVendorDataList().add(supportedVendor);
                    }
                });
            }
            List<String> sessionLookUpKeys = Strings.splitter(',').trimTokens().split(diameterGatewayProfileData.getSessionLookUpKey());
            if(Collectionz.isNullOrEmpty(sessionLookUpKeys) == false){
                setSessionKeysForUpdate(sessionLookUpKeys);
                sessionLookUpKeys.forEach(sessionLookUpKey -> {
                    if (getSessionKeyList().contains(sessionLookUpKey) == false) {
                        getSessionKeyList().add(sessionLookUpKey);
                    }
                });
            }
            setActionChainUrl(getRedirectURL(METHOD_EDIT));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch(Exception e){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation");
            return ERROR;
        }

    }

    private void setPacketMapping(DiameterGatewayProfileData diameterGatewayProfileData) {
        filterPacketMappings(diameterGatewayProfileData);

        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getGwToPccGxPacketMappings());
        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getPccToGWGxPacketMappings());
        //Gy Mapping Create
        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getGwToPccGyPacketMappings());
        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getPccToGWGyPacketMappings());
        //Rx Mapping Create
        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getGwToPccRxPacketMappings());
        createPacketMapping(diameterGatewayProfileData,diameterGatewayProfileData.getPccToGWRxPacketMappings());
    }

    private void filterPacketMappings(DiameterGatewayProfileData diameterGatewayProfileData) {
        Collectionz.filter(diameterGatewayProfileData.getGwToPccGxPacketMappings(), DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
        Collectionz.filter(diameterGatewayProfileData.getPccToGWGxPacketMappings(),DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);

        Collectionz.filter(diameterGatewayProfileData.getGwToPccGyPacketMappings(), DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
        Collectionz.filter(diameterGatewayProfileData.getPccToGWGyPacketMappings(),DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);

        Collectionz.filter(diameterGatewayProfileData.getGwToPccRxPacketMappings(), DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
        Collectionz.filter(diameterGatewayProfileData.getPccToGWRxPacketMappings(),DIAMETER_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
    }

    private void filterPccRuleMappings(DiameterGatewayProfileData diameterGatewayProfileData) {
        Collectionz.filter(diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings(), DIAMETER_GW_PROFILE_PCCRULE_MAP_DATA_PREDICATE);
    }

    private void filterGroovyScripts(DiameterGatewayProfileData diameterGatewayProfileData) {
        if(Collectionz.isNullOrEmpty(diameterGatewayProfileData.getGroovyScriptDatas()) == false) {
            Collectionz.filter(diameterGatewayProfileData.getGroovyScriptDatas(), GROOVY_PREDICATE);
        }
    }

    private void filterServiceGuidings(List<ServiceGuidingData> serviceGuidings) {
        if(Collectionz.isNullOrEmpty(serviceGuidings) == false) {
            Collectionz.filter(serviceGuidings, SERVICE_GUIDING_PREDICATE);
        }
    }

    private void createPacketMapping(DiameterGatewayProfileData diameterGatewayProfileData, List<DiameterGwProfilePacketMapData> gwProfilePacketMapDataList){
        for(int i= 0; i < gwProfilePacketMapDataList.size() ; i++){
            DiameterGwProfilePacketMapData diameterGwProfilePacketMapData =  gwProfilePacketMapDataList.get(i);
            diameterGwProfilePacketMapData.setPacketMappingData(CRUDOperationUtil.get(PacketMappingData.class,diameterGwProfilePacketMapData.getPacketMappingId()));
            diameterGwProfilePacketMapData.setOrderNumber(i+1);
            diameterGwProfilePacketMapData.setDiameterGatewayProfileData(diameterGatewayProfileData);
        }
        diameterGatewayProfileData.getDiameterGwProfilePacketMappings().addAll(gwProfilePacketMapDataList);
    }

    @Override
    public HttpHeaders update() { // update
        try {
            setVendorBasedOnId();
            setGroovyScriptDatas();
            setServiceGuidingDatas();
            return super.update();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    private void setGroovyScriptDatas() {
        DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
        filterGroovyScripts(diameterGatewayProfileData);
        if(Collectionz.isNullOrEmpty(diameterGatewayProfileData.getGroovyScriptDatas()) == false) {
            int index = 1;
            for (GroovyScriptData groovyScript : diameterGatewayProfileData.getGroovyScriptDatas()) {
                groovyScript.setDiameterGatewayProfileData(diameterGatewayProfileData);
                groovyScript.setOrderNumber(index);
                index++;
            }
        }
    }

    private void setVendorBasedOnId() {
        DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
        VendorData vendor = CRUDOperationUtil.get(VendorData.class,diameterGatewayProfileData.getVendorId());
        diameterGatewayProfileData.setVendorData(vendor);
        setPacketMapping(diameterGatewayProfileData);
        setPCCRuleMapping(diameterGatewayProfileData);
    }

    private void setPCCRuleMapping(DiameterGatewayProfileData diameterGatewayProfileData) {
        filterPccRuleMappings(diameterGatewayProfileData);
        int index = 0;
        for(DiameterGwProfilePCCRuleMappingData diameterGwProfilePCCMapping : diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings()){
            diameterGwProfilePCCMapping.setPccRuleMappingData(CRUDOperationUtil.get(PCCRuleMappingData.class,diameterGwProfilePCCMapping.getPccRuleMappingId()));
            diameterGwProfilePCCMapping.setOrderNumber(index+1);
            diameterGwProfilePCCMapping.setDiameterGatewayProfileData(diameterGatewayProfileData);
            index++;
        }
    }


    @Override
    public boolean prepareAndValidateDestroy(DiameterGatewayProfileData diameterGatewayProfileData) {
        if (Collectionz.isNullOrEmpty(diameterGatewayProfileData.getDiameterGatewayData()) == false) {
            addActionError("Diameter Gateway Profile " + diameterGatewayProfileData.getName() + " is associated with Diameter Gateway");
            String attachedGateways = Strings.join(",", diameterGatewayProfileData.getDiameterGatewayData(),DiameterGatewayData::getName);
            getLogger().error(getLogModule(), "Error while deleting diameter gateway profile" + diameterGatewayProfileData.getName() + ".Reason: gateway profile is associated with "+attachedGateways+" Diameter Gateways.");
            return false;
        }
        return true;
    }
    @Override
    public ACLModules getModule() {
        return ACLModules.GATEWAYPROFILE;
    }

    @Override
    public DiameterGatewayProfileData createModel() {
        return new DiameterGatewayProfileData();
    }

    @Override
    public void validate() {
        super.validate();
        DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
        if (Strings.isNullOrBlank(diameterGatewayProfileData.getVendorId()) == false) {
            VendorData vendorData = CRUDOperationUtil.get(VendorData.class, diameterGatewayProfileData.getVendorId());
            if (vendorData == null) {
                addActionError("Vendor Data id does not exist");
            }
            VendorData vendor = new VendorData();
            vendor.setId(diameterGatewayProfileData.getVendorId());
            diameterGatewayProfileData.setVendorData(vendor);
        }
        validatePacketMappings(diameterGatewayProfileData.getGwToPccGxPacketMappings(),ConversionType.GATEWAY_TO_PCC, PacketApplication.GX);
        validatePacketMappings(diameterGatewayProfileData.getPccToGWGxPacketMappings(),ConversionType.PCC_TO_GATEWAY,PacketApplication.GX);

        validatePacketMappings(diameterGatewayProfileData.getGwToPccGyPacketMappings(),ConversionType.GATEWAY_TO_PCC,PacketApplication.GY);
        validatePacketMappings(diameterGatewayProfileData.getPccToGWGyPacketMappings(),ConversionType.PCC_TO_GATEWAY,PacketApplication.GY);

        validatePacketMappings(diameterGatewayProfileData.getGwToPccRxPacketMappings(),ConversionType.GATEWAY_TO_PCC,PacketApplication.RX);
        validatePacketMappings(diameterGatewayProfileData.getPccToGWRxPacketMappings(),ConversionType.PCC_TO_GATEWAY,PacketApplication.RX);

        validatePccRuleMappings(diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());
        validateGroovyScriptData(diameterGatewayProfileData.getGroovyScriptDatas());
        if (ACLAction.CREATE.getVal().equalsIgnoreCase(getMethodName())) {
            if (Collectionz.isNullOrEmpty(diameterGatewayProfileData.getServiceGuidingDatas()) == false) {
                filterServiceGuidings(diameterGatewayProfileData.getServiceGuidingDatas());
                filterDuplicateSerivces(diameterGatewayProfileData.getServiceGuidingDatas());
                verifyConfiguredServiceGuidings(diameterGatewayProfileData.getServiceGuidingDatas());
            }
        } else if (ACLAction.UPDATE.getVal().equalsIgnoreCase(getMethodName())) {
            DiameterGatewayProfileData diameterGatewayProfileFromDB = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
            validateServiceGuidingDataWithExisting(diameterGatewayProfileData.getServiceGuidingDatas(), diameterGatewayProfileFromDB.getServiceGuidingDatas());
        }
    }

        private void validateServiceGuidingDataWithExisting(List<ServiceGuidingData> serviceGuidings, List<ServiceGuidingData> existingAttachedServices) {
        filterServiceGuidings(serviceGuidings);
        filterDuplicateSerivces(serviceGuidings);

        Set<String> serviceNames =
                existingAttachedServices.stream()
                        .map(data -> data.getServiceData().getId())
                        .collect(Collectors.toSet());

        List<ServiceGuidingData> newOrUpdatedServiceGuiding =
                serviceGuidings.stream()
                        .filter(e -> serviceNames.contains(e.getServiceId()) == false)
                        .collect(Collectors.toList());


        if(Collectionz.isNullOrEmpty(newOrUpdatedServiceGuiding) == false){
            verifyConfiguredServiceGuidings(newOrUpdatedServiceGuiding);
        }
    }

    private void verifyConfiguredServiceGuidings(List<ServiceGuidingData> serviceGuidings){
        serviceGuidings.forEach(serviceGuidingData -> {
            if (serviceGuidingData == null) {
                addActionError("Service Guiding must be configured");
            } else if (Strings.isNullOrBlank(serviceGuidingData.getServiceId())) {
                addActionError("Service must be configured");
            } else {
                ServiceData serviceData = CRUDOperationUtil.get(ServiceData.class, serviceGuidingData.getServiceId());
                if (serviceData == null) {
                    addFieldError("serviceId", "Service does not exist in DB");
                } else if (CommonConstants.STATUS_INACTIVE.equalsIgnoreCase(serviceData.getStatus())) {
                    addFieldError("serviceId", "InActive Service can not be configured");
                } else {
                    serviceGuidingData.setServiceData(serviceData);
                }
            }
        });
    }

    private void filterDuplicateSerivces(List<ServiceGuidingData> serviceGuidings) {
        List<String> serviceNames = Collectionz.newArrayList();
        serviceGuidings.forEach(serviceGuidingData -> {
            String serviceId = serviceGuidingData.getServiceId();
            if(serviceNames.contains(serviceId) == false){
                serviceNames.add(serviceId);
            }else{
                addFieldError("serviceId", "Service already configured with Service Guiding");
                return;
            }
        });
    }

    @SkipValidation
    public HttpHeaders initManageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            DiameterGatewayProfileData diameterGatewayProfileFromDB = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
            String type = getRequest().getParameter("type");
            List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMapData = Collectionz.newArrayList();
            if(Strings.isNullOrBlank(type) == false){
                if("gxGatewayToPCC".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getGwToPccGxPacketMappings();
                }else if("gxPccToGw".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getPccToGWGxPacketMappings();
                }else if("gyGatewayToPCC".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getGwToPccGyPacketMappings();
                }else if("gyPccToGw".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getPccToGWGyPacketMappings();
                }else if("rxGatewayToPCC".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getGwToPccRxPacketMappings();
                }else if("rxPccToGw".equalsIgnoreCase(type)){
                    diameterGwProfilePacketMapData = diameterGatewayProfileFromDB.getPccToGWRxPacketMappings();
                }
            }
            setPacketMappingAssociationList(diameterGwProfilePacketMapData);
            setActionChainUrl(getRedirectURL("packet-mapping-manageorder"));
            packetMappingAssociationList.sort(Comparator.comparing(DiameterGwProfilePacketMapData::getOrderNumber));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders initManageOrderPccMapping(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrderPccMapping()");
        }
        try{
            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            DiameterGatewayProfileData diameterGatewayProfileFromDB = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
            setPccRuleMappingAssociationList(diameterGatewayProfileFromDB.getDiameterGwProfilePCCRuleMappings());
            setActionChainUrl(getRedirectURL("pcc-rule-mapping-manageorder"));
            pccRuleMappingAssociationList.sort(Comparator.comparing(DiameterGwProfilePCCRuleMappingData::getOrderNumber));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders initManageOrderForGroovy(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            DiameterGatewayProfileData diameterGatewayProfileFromDB = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
            setGroovyScriptList(diameterGatewayProfileFromDB.getGroovyScriptDatas());
            groovyScriptList.sort(Comparator.comparing(GroovyScriptData::getOrderNumber));
            setActionChainUrl(getRedirectURL("groovy-script-manageorder"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders initManageOrderForServiceGuiding(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrderForServiceGuiding()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            DiameterGatewayProfileData diameterGatewayProfileFromDB = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayProfileData.getId());
            setServiceGuidings(diameterGatewayProfileFromDB.getServiceGuidingDatas());
            serviceGuidings.sort(Comparator.comparing(ServiceGuidingData::getOrderNumber));
            setActionChainUrl(getRedirectURL("service-guiding-manageorder"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders manageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrder()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class,diameterGatewayProfileData.getId());
            String[] diameterGatewayPacketMappingDataIds = getRequest().getParameterValues("diameterGatewayPacketMapDataIds");
            int index = 1;
            for(String id: diameterGatewayPacketMappingDataIds){
                DiameterGwProfilePacketMapData diameterGwProfilePacketMappingData = CRUDOperationUtil.get(DiameterGwProfilePacketMapData.class,id);
                JsonObject oldJsonObject = diameterGwProfilePacketMappingData.toJson();
                int oldOrderNumber = diameterGwProfilePacketMappingData.getOrderNumber();

                diameterGwProfilePacketMappingData.setOrderNumber(index);
                int newOrderNumber = diameterGwProfilePacketMappingData.getOrderNumber();
                JsonObject newJsonObject = diameterGwProfilePacketMappingData.toJson();

                CRUDOperationUtil.update(diameterGwProfilePacketMappingData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + diameterGwProfilePacketMappingData.getPacketMappingData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(diameterGatewayProfileData, diameterGwProfilePacketMappingData.getPacketMappingData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, diameterGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Diameter Gateway Profile's Packet Mapping order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../diameter-gateway-profile/"+diameterGatewayProfileData.getId()));
            addActionMessage("Packet Mapping order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Packet Mapping. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    @SkipValidation
    public HttpHeaders manageOrderPccMapping(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrderPccMapping()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class,diameterGatewayProfileData.getId());
            String[] diameterGatewayPccRuleMappingDataIds = getRequest().getParameterValues("diameterGatewayPccRuleMapDataIds");
            int index = 1;
            for(String id: diameterGatewayPccRuleMappingDataIds){
                DiameterGwProfilePCCRuleMappingData diameterGwProfilePccRuleMappingData = CRUDOperationUtil.get(DiameterGwProfilePCCRuleMappingData.class,id);
                JsonObject oldJsonObject = diameterGwProfilePccRuleMappingData.toJson();
                int oldOrderNumber = diameterGwProfilePccRuleMappingData.getOrderNumber();

                diameterGwProfilePccRuleMappingData.setOrderNumber(index);
                int newOrderNumber = diameterGwProfilePccRuleMappingData.getOrderNumber();
                JsonObject newJsonObject = diameterGwProfilePccRuleMappingData.toJson();

                CRUDOperationUtil.update(diameterGwProfilePccRuleMappingData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + diameterGwProfilePccRuleMappingData.getPccRuleMappingData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(diameterGatewayProfileData, diameterGwProfilePccRuleMappingData.getPccRuleMappingData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, diameterGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Diameter Gateway Profile's PCCRule Mapping order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../diameter-gateway-profile/"+diameterGatewayProfileData.getId()));
            addActionMessage("PCCRule Mapping order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of PCCRule Mapping. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    @SkipValidation
    public HttpHeaders manageOrderForGroovy(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrderForGroovy()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class,diameterGatewayProfileData.getId());
            String[] groovyScriptIds = getRequest().getParameterValues("groovyScriptIds");
            int index = 1;
            for(String id: groovyScriptIds){
                GroovyScriptData groovyScriptData = CRUDOperationUtil.get(GroovyScriptData.class,id);
                JsonObject oldJsonObject = groovyScriptData.toJson();
                int oldOrderNumber = groovyScriptData.getOrderNumber();

                groovyScriptData.setOrderNumber(index);
                int newOrderNumber = groovyScriptData.getOrderNumber();
                JsonObject newJsonObject = groovyScriptData.toJson();

                CRUDOperationUtil.update(groovyScriptData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + groovyScriptData.getScriptName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(diameterGatewayProfileData, groovyScriptData.getScriptName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, diameterGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Diameter Gateway Profile's Groovy Script order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../diameter-gateway-profile/"+diameterGatewayProfileData.getId()));
            addActionMessage("Groovy Script order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Groovy Script. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }


    @SkipValidation
    public HttpHeaders manageOrderForServiceGuiding(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrderForServiceGuiding()");
        }
        try{

            DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
            diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class,diameterGatewayProfileData.getId());
            String[] serviceGuidingIds = getRequest().getParameterValues("serviceGuidingIds");
            int index = 1;
            for(String id: serviceGuidingIds){
                ServiceGuidingData serviceGuidingData = CRUDOperationUtil.get(ServiceGuidingData.class,id);
                JsonObject oldJsonObject = serviceGuidingData.toJson();
                int oldOrderNumber = serviceGuidingData.getOrderNumber();

                serviceGuidingData.setOrderNumber(index);
                int newOrderNumber = serviceGuidingData.getOrderNumber();
                JsonObject newJsonObject = serviceGuidingData.toJson();

                CRUDOperationUtil.update(serviceGuidingData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + serviceGuidingData.getServiceData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(diameterGatewayProfileData, serviceGuidingData.getServiceData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, diameterGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Diameter Gateway Profile's Service Guiding order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../diameter-gateway-profile/"+diameterGatewayProfileData.getId()));
            addActionMessage("Service Guiding order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Service Guiding. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }


    public List<DiameterGwProfilePCCRuleMappingData> getPccRuleMappingAssociationList() {
        return pccRuleMappingAssociationList;
    }

    public void setPccRuleMappingAssociationList(List<DiameterGwProfilePCCRuleMappingData> pccRuleMappingAssociationList) {
        this.pccRuleMappingAssociationList = pccRuleMappingAssociationList;
    }


    private void validatePacketMappings(List<DiameterGwProfilePacketMapData> packetMappings, ConversionType type, PacketApplication applicationType) {
        if(Collectionz.isNullOrEmpty(packetMappings) == false){
            packetMappings.forEach(packetMapping -> {
                if(packetMapping != null) {
                    String packetMappingId = packetMapping.getPacketMappingId();
                    if (Strings.isNullOrBlank(packetMappingId)) {
                        addFieldError("packetMappingId", getText("required.packet.mapping"));
                    } else {
                        List<String> supportedPacketType = ApplicationPacketType.getPacketTypeFromApplication(applicationType,type);
                        SimpleExpression expression = Restrictions.eq("type", type.name());
                        PacketMappingData packetMapData = CRUDOperationUtil.get(PacketMappingData.class, packetMappingId, expression);

                        if (packetMapData == null) {
                            addFieldError("packetMappingId", getText("invalid.packet.mapping"));
                        }else if(supportedPacketType.contains(packetMapData.getPacketType()) == false){
                            addFieldError("packetMappingId", getText("invalid.application.packet.mapping"));
                        }
                    }
                }
            });
        }
    }

    private void validateGroovyScriptData(List<GroovyScriptData> scriptDatas){
        if(Collectionz.isNullOrEmpty(scriptDatas) == false){
            scriptDatas.forEach(groovyScriptData -> {
                if(groovyScriptData == null){
                    addActionError("Groovy Script Data must be configured");
                }else if(Strings.isNullOrBlank(groovyScriptData.getScriptName())){
                    addActionError("Groovy Script Name must be configured");
                }
            });
        }
    }

    private void validatePccRuleMappings(List<DiameterGwProfilePCCRuleMappingData> pccRuleMappings) {
        if(Collectionz.isNullOrEmpty(pccRuleMappings) == false){
            pccRuleMappings.forEach(pccRuleMapping -> {
                if(pccRuleMapping != null) {
                    String pccRuleMappingId = pccRuleMapping.getPccRuleMappingId();
                    if (Strings.isNullOrBlank(pccRuleMappingId)) {
                        addFieldError("pccRuleMappingId", getText("required.pccrule.mapping"));
                    }
                }
            });
        }
    }

    public List<VendorData> getVendorDataList() {
        return vendorDataList;
    }

    public void setVendorDataList(List<VendorData> vendorDataList) {
        this.vendorDataList = vendorDataList;
    }

    private List<VendorData> getVendorList() {
        return CRUDOperationUtil.findAll(VendorData.class);
    }

    public List<String> getSupportedVendorDataList() {
        return supportedVendorDataList;
    }

    public void setSupportedVendorDataList(List<String> supportedVendorList) {
        this.supportedVendorDataList = supportedVendorList;
    }

    private List<String> getSupportedVendors() {
        List<String> supportedVendors = Collectionz.newArrayList();
        for(SupportedVendor vendor : SupportedVendor.values()){
            supportedVendors.add(vendor.getName());
        }
        return supportedVendors;
    }

    private List<String> getSessionKeys() {
        List<String> sessionKeys = Collectionz.newArrayList();
        for (PCRFKeyConstants keyConstants : PCRFKeyConstants.values(PCRFKeyType.REQUEST)) {
            sessionKeys.add(keyConstants.getVal());

        }
        return sessionKeys;
    }



    public List<String> getSupportedStandardValuesForUpdate() {
        return supportedStandardValuesForUpdate;
    }

    public void setSupportedStandardValuesForUpdate(List<String> supportedStandardValuesForUpdate) {
        this.supportedStandardValuesForUpdate = supportedStandardValuesForUpdate;
    }

    @Override
    public void prepareValuesForSubClass() {
        setVendorDataList(getVendorList());
        setSessionKeyList(getSessionKeys());
        setSupportedVendorDataList(getSupportedVendors());
        /// set gateway to pcrf based on application
        /// set PCRF TO GATEWAY PACKET MAPPING FOR gx
        setGatewayToPCCPacketMappingGxList(CRUDOperationUtil.findAll(PacketMappingData.class, GW_TO_PCC_PACKET_MAPPING_CRITERIA_GX));
        setPccToGatewayPacketMappingGxList(CRUDOperationUtil.findAll(PacketMappingData.class, PCC_TO_GW_PACKET_MAPPING_CRITERIA_GX));

        setGatewayToPCCPacketMappingGyList(CRUDOperationUtil.findAll(PacketMappingData.class, GW_TO_PCC_PACKET_MAPPING_CRITERIA_GY));
        setPccToGatewayPacketMappingGyList(CRUDOperationUtil.findAll(PacketMappingData.class, PCC_TO_GW_PACKET_MAPPING_CRITERIA_GY));

        setGatewayToPCCPacketMappingRxList(CRUDOperationUtil.findAll(PacketMappingData.class, GW_TO_PCC_PACKET_MAPPING_CRITERIA_RX));
        setPccToGatewayPacketMappingRxList(CRUDOperationUtil.findAll(PacketMappingData.class, PCC_TO_GW_PACKET_MAPPING_CRITERIA_RX));

        //set pcc rule mapping
        setPccRuleMappings(CRUDOperationUtil.findAll(PCCRuleMappingData.class,PROTOCOL_CRITERIA));
        setServiceDatas(CRUDOperationUtil.findAll(ServiceData.class));
        Gson gson = GsonFactory.defaultInstance();
        JsonArray serviceJson = gson.toJsonTree(getServiceDatas(),new TypeToken<List<ServiceData>>() {}.getType()).getAsJsonArray();
        setServiceDataJson(serviceJson.toString());


    }

    public String getAdvanceConditions(){
        Gson gson = GsonFactory.defaultInstance();
        List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.RESPONSE);
        pcrfKeyConstants.sort((PCRFKeyConstants constant1, PCRFKeyConstants constant2)->constant1.getVal().compareTo(constant2.getVal()));
        String [] autoSuggestion = new String[pcrfKeyConstants.size()];
        short index= 0;
        for(PCRFKeyConstants keyConstants : pcrfKeyConstants){
            autoSuggestion[index] = keyConstants.getVal();
            index++;
        }

        return gson.toJson(autoSuggestion);

    }

    public String getSuggestionsGatewayToPCC(){
        Gson gson = GsonFactory.defaultInstance();
        List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.REQUEST);
        pcrfKeyConstants.sort((PCRFKeyConstants constant1, PCRFKeyConstants constant2)->constant1.getVal().compareTo(constant2.getVal()));
        String [] autoSuggestion = new String[pcrfKeyConstants.size()];
        short index= 0;
        for(PCRFKeyConstants keyConstants : pcrfKeyConstants){
            autoSuggestion[index] = keyConstants.getVal();
            index++;
        }
        return gson.toJson(autoSuggestion);
    }


    public List<String> getSessionKeyList() {
        return sessionKeyList;
    }

    public void setSessionKeyList(List<String> sessionKeyList) {
        this.sessionKeyList = sessionKeyList;
    }

    public List<String> getSessionKeysForUpdate() {
        return sessionKeysForUpdate;
    }

    public void setSessionKeysForUpdate(List<String> sessionKeysForUpdate) {
        this.sessionKeysForUpdate = sessionKeysForUpdate;
    }


    public List<PacketMappingData> getGatewayToPCCPacketMappingGxList() {
        return gatewayToPCCPacketMappingGxList;
    }

    public void setGatewayToPCCPacketMappingGxList(List<PacketMappingData> gatewayToPCCPacketMappingGxList) {
        this.gatewayToPCCPacketMappingGxList = gatewayToPCCPacketMappingGxList;
    }


    public List<PacketMappingData> getPccToGatewayPacketMappingGxList() {
        return pccToGatewayPacketMappingGxList;
    }

    public void setPccToGatewayPacketMappingGxList(List<PacketMappingData> pccToGatewayPacketMappingGxList) {
        this.pccToGatewayPacketMappingGxList = pccToGatewayPacketMappingGxList;
    }

    public String getGroovyScripts(){
        Gson gson = GsonFactory.defaultInstance();
        DiameterGatewayProfileData diameterGatewayProfileData = (DiameterGatewayProfileData) getModel();
        return gson.toJsonTree(diameterGatewayProfileData.getGroovyScriptDatas(), new TypeToken<List<DiameterGatewayProfileData>>() {}.getType()).getAsJsonArray().toString();

    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    public List<PacketMappingData> getGatewayToPCCPacketMappingGyList() {
        return gatewayToPCCPacketMappingGyList;
    }

    public void setGatewayToPCCPacketMappingGyList(List<PacketMappingData> gatewayToPCCPacketMappingGyList) {
        this.gatewayToPCCPacketMappingGyList = gatewayToPCCPacketMappingGyList;
    }

    public List<PacketMappingData> getPccToGatewayPacketMappingGyList() {
        return pccToGatewayPacketMappingGyList;
    }

    public void setPccToGatewayPacketMappingGyList(List<PacketMappingData> pccToGatewayPacketMappingGyList) {
        this.pccToGatewayPacketMappingGyList = pccToGatewayPacketMappingGyList;
    }

    public List<PacketMappingData> getGatewayToPCCPacketMappingRxList() {
        return gatewayToPCCPacketMappingRxList;
    }

    public void setGatewayToPCCPacketMappingRxList(List<PacketMappingData> gatewayToPCCPacketMappingRxList) {
        this.gatewayToPCCPacketMappingRxList = gatewayToPCCPacketMappingRxList;
    }

    public List<PacketMappingData> getPccToGatewayPacketMappingRxList() {
        return pccToGatewayPacketMappingRxList;
    }

    public void setPccToGatewayPacketMappingRxList(List<PacketMappingData> pccToGatewayPacketMappingRxList) {
        this.pccToGatewayPacketMappingRxList = pccToGatewayPacketMappingRxList;
    }


    public List<PCCRuleMappingData> getPccRuleMappings() {
        return pccRuleMappings;
    }

    public void setPccRuleMappings(List<PCCRuleMappingData> pccRuleMappings) {
        this.pccRuleMappings = pccRuleMappings;
    }

    public List<DiameterGwProfilePacketMapData> getPacketMappingAssociationList() {
        return packetMappingAssociationList;
    }

    public void setPacketMappingAssociationList(List<DiameterGwProfilePacketMapData> packetMappingAssociationList) {
        this.packetMappingAssociationList = packetMappingAssociationList;
    }

    public List<GroovyScriptData> getGroovyScriptList() {
        return groovyScriptList;
    }

    public void setGroovyScriptList(List<GroovyScriptData> groovyScriptList) {
        this.groovyScriptList = groovyScriptList;
    }

    public List<ServiceData> getServiceDatas() {
        return serviceDatas;
    }

    public void setServiceDatas(List<ServiceData> serviceDatas) {
        this.serviceDatas = serviceDatas;
    }

    public List<ServiceGuidingData> getServiceGuidings() {
        return serviceGuidings;
    }

    public void setServiceGuidings(List<ServiceGuidingData> serviceGuidings) {
        this.serviceGuidings = serviceGuidings;
    }

    public String getServiceDataJson() {
        return serviceDataJson;
    }

    public void setServiceDataJson(String serviceDataJson) {
        this.serviceDataJson = serviceDataJson;
    }
}
