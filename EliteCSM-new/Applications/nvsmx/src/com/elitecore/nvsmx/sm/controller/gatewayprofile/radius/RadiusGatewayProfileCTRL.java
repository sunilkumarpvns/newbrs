package com.elitecore.nvsmx.sm.controller.gatewayprofile.radius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.ProtocolType;
import com.elitecore.corenetvertex.constants.SupportedVendor;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.GroovyScriptData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by Ishani on 5/9/17.
 */
@ParentPackage(value = "sm")
@Namespace("/sm/gatewayprofile")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","radius-gateway-profile"}),
})
public class RadiusGatewayProfileCTRL extends RestGenericCTRL<RadiusGatewayProfileData> {

    private static final Predicate<RadiusGwProfilePacketMapData> RADIUS_GW_PROFILE_PACKET_MAP_DATA_PREDICATE = radiusGwProfilePacketMapData -> {
        if (radiusGwProfilePacketMapData == null) {
            return false;
        }
        return radiusGwProfilePacketMapData.getPacketMappingId() != null;

    };
    private static final Predicate<RadiusGwProfilePCCRuleMappingData> RADIUS_GW_PROFILE_PCCRULE_MAP_DATA_PREDICATE = radiusGwProfilePccRuleMapData -> {
        if (radiusGwProfilePccRuleMapData == null) {
            return false;
        }
        return radiusGwProfilePccRuleMapData.getPccRuleMappingId() != null;

    };
    private static final Predicate<GroovyScriptData> GROOVY_PREDICATE = groovyScript -> groovyScript == null ? false : groovyScript.getScriptName() !=null;

    private List<VendorData> vendorDataList;
    private List<String> supportedVendorDataList;
    private List<String> supportedStandardValuesForUpdate = Collectionz.newArrayList();
    private List<PacketMappingData> gatewayToPCCPacketMappingDataList = new ArrayList<>();

    private List<PacketMappingData> pccToGatewayPacketMappingDataList = new ArrayList<>();
    private List<PCCRuleMappingData> pccRuleMappings = new ArrayList<>();

    private static final Predicate<ServiceGuidingData> SERVICE_GUIDING_PREDICATE = serviceGuiding -> serviceGuiding == null ? false : serviceGuiding.getServiceId() !=null;
    private List<ServiceGuidingData> serviceGuidings = Collectionz.newArrayList();
    private List<ServiceData> serviceDatas = Collectionz.newArrayList();
    private String serviceDataJson;

    private List<GroovyScriptData> groovyScriptList = Collectionz.newArrayList();
    private static final Criterion PROTOCOL_CRITERIA = Restrictions.eq("commProtocol", ProtocolType.RADIUS.name());
    private static final Criterion GW_TO_PCRF_TYPE = Restrictions.eq("type", ConversionType.GATEWAY_TO_PCC.name());
    private static final Criterion GW_TO_PCRF_PACKET_MAPPING_CRITERIA = Restrictions.and(PROTOCOL_CRITERIA, GW_TO_PCRF_TYPE);

    private static final Criterion PCRF_TO_GW_TYPE = Restrictions.eq("type",ConversionType.PCC_TO_GATEWAY.name());
    private static final Criterion PCRF_TO_GW_PACKET_MAPPING_CRITERIA = Restrictions.and(PROTOCOL_CRITERIA,PCRF_TO_GW_TYPE);
    private List<RadiusGwProfilePacketMapData> packetMappingAssociationList = Collectionz.newArrayList();
    private List<RadiusGwProfilePCCRuleMappingData> pccRuleMappingAssociationList = Collectionz.newArrayList();

    @Override
    @SkipValidation
    public HttpHeaders index() {
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.RADIUS.name());
        HttpHeaders result = super.index();
        setActionChainUrl("sm/gatewayprofile/gateway-profile/index");
        return result;
    }

    @Override
    public HttpHeaders create() { // update
        try {
            setVendorDataBasedOnIdAndPacketMappingAssociation();
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

    @Override
    public String edit() { // initUpdate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called edit()");
        }
        try {
            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            if(Strings.isNullOrBlank(radiusGatewayProfileData.getId()) == false) {
                radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class, radiusGatewayProfileData.getId());
                setModel(radiusGatewayProfileData);
            }
            List<String> supportedVendors = Strings.splitter(',').trimTokens().split(radiusGatewayProfileData.getSupportedVendorList());
            if (Collectionz.isNullOrEmpty(supportedVendors) == false) {
                setSupportedStandardValuesForUpdate(supportedVendors);
                supportedVendors.forEach(supportedVendor -> {
                    if (getSupportedVendorDataList().contains(supportedVendor) == false) {
                        getSupportedVendorDataList().add(supportedVendor);
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

    @Override
    public HttpHeaders update() { // update
        try {
            setVendorDataBasedOnIdAndPacketMappingAssociation();
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

    public String getGroovyScripts(){
        Gson gson = GsonFactory.defaultInstance();
        RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
        return gson.toJsonTree(radiusGatewayProfileData.getGroovyScriptDatas(), new TypeToken<List<RadiusGatewayProfileData>>() {}.getType()).getAsJsonArray().toString();

    }

    private void setVendorDataBasedOnIdAndPacketMappingAssociation() {
        RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
        VendorData vendor = CRUDOperationUtil.get(VendorData.class,radiusGatewayProfileData.getVendorId());
        radiusGatewayProfileData.setVendorData(vendor);
        setPacketMapping(radiusGatewayProfileData);
        setPCCRuleMapping(radiusGatewayProfileData);
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

    private void setPCCRuleMapping(RadiusGatewayProfileData radiusGatewayProfileData) {
        filterPccRuleMappings(radiusGatewayProfileData);
        int index = 0;
        for(RadiusGwProfilePCCRuleMappingData radiusGwProfilePCCMapping : radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings()){
            radiusGwProfilePCCMapping.setPccRuleMappingData(CRUDOperationUtil.get(PCCRuleMappingData.class,radiusGwProfilePCCMapping.getPccRuleMappingId()));
            radiusGwProfilePCCMapping.setOrderNumber(index+1);
            radiusGwProfilePCCMapping.setRadiusGatewayProfileData(radiusGatewayProfileData);
            index++;
        }
    }

    private void setGroovyScriptDatas() {
        RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
        filterGroovyScripts(radiusGatewayProfileData);
        List<GroovyScriptData> groovyScriptDatas = radiusGatewayProfileData.getGroovyScriptDatas();
        if(Collectionz.isNullOrEmpty(groovyScriptDatas) == false) {
            int index = 1;
            for (GroovyScriptData groovyScript : groovyScriptDatas) {
                groovyScript.setRadiusGatewayProfileData(radiusGatewayProfileData);
                groovyScript.setOrderNumber(index);
                index++;
            }
        }
    }

    private void setServiceGuidingDatas() {
        RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
        if(Collectionz.isNullOrEmpty(radiusGatewayProfileData.getServiceGuidingDatas()) == false) {
            int index = 1;
            for (ServiceGuidingData serviceGuidingData : radiusGatewayProfileData.getServiceGuidingDatas()) {
                serviceGuidingData.setRadiusGatewayProfileData(radiusGatewayProfileData);
                serviceGuidingData.setOrderNumber(index);
                index++;
            }
        }
    }

    private void filterServiceGuidings(List<ServiceGuidingData> serviceGuidingDatas) {
        if(Collectionz.isNullOrEmpty(serviceGuidingDatas) == false) {
            Collectionz.filter(serviceGuidingDatas, SERVICE_GUIDING_PREDICATE);
        }
    }

    private void filterPccRuleMappings(RadiusGatewayProfileData radiusGatewayProfileData) {
        Collectionz.filter(radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings(), RADIUS_GW_PROFILE_PCCRULE_MAP_DATA_PREDICATE);
    }

    private void filterGroovyScripts(RadiusGatewayProfileData radiusGatewayProfileData) {
        if(Collectionz.isNullOrEmpty(radiusGatewayProfileData.getGroovyScriptDatas()) == false) {
            Collectionz.filter(radiusGatewayProfileData.getGroovyScriptDatas(), GROOVY_PREDICATE);
        }
    }

    private void setPacketMapping(RadiusGatewayProfileData radiusGatewayProfileData) {

        Collectionz.filter(radiusGatewayProfileData.getGwToPCCPacketMappings(), RADIUS_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
        Collectionz.filter(radiusGatewayProfileData.getPccToGWPacketMappings(),RADIUS_GW_PROFILE_PACKET_MAP_DATA_PREDICATE);
        createPacketMapping(radiusGatewayProfileData,radiusGatewayProfileData.getGwToPCCPacketMappings());
        createPacketMapping(radiusGatewayProfileData,radiusGatewayProfileData.getPccToGWPacketMappings());
    }

    private void createPacketMapping(RadiusGatewayProfileData radiusGatewayProfileData, List<RadiusGwProfilePacketMapData> gwProfilePacketMapDataList){
        for(int i= 0; i < gwProfilePacketMapDataList.size() ; i++){
            RadiusGwProfilePacketMapData radiusGwProfilePacketMapData =  gwProfilePacketMapDataList.get(i);
            radiusGwProfilePacketMapData.setPacketMappingData(CRUDOperationUtil.get(PacketMappingData.class,radiusGwProfilePacketMapData.getPacketMappingId()));
            radiusGwProfilePacketMapData.setOrderNumber(i+1);
            radiusGwProfilePacketMapData.setRadiusGatewayProfileData(radiusGatewayProfileData);
        }
        radiusGatewayProfileData.getRadiusGwProfilePacketMappings().addAll(gwProfilePacketMapDataList);
    }


    @Override
    protected boolean prepareAndValidateDestroy(RadiusGatewayProfileData radiusGatewayProfileData) {
        if (Collectionz.isNullOrEmpty(radiusGatewayProfileData.getRadiusGatewayDatas()) == false) {
            addActionError("RADIUS Gateway Profile " + radiusGatewayProfileData.getName() + " is associated with RADIUS Gateway");
            String attachedGateways = Strings.join(",", radiusGatewayProfileData.getRadiusGatewayDatas(), RadiusGatewayData::getName);
            getLogger().error(getLogModule(), "Error while deleting radius gateway profile "+ radiusGatewayProfileData.getName() +".Reason: gateway profile is associated with "+attachedGateways +" RADIUS Gateways.");
            return false;
        }
        return true;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.GATEWAYPROFILE;
    }

    @Override
    public RadiusGatewayProfileData createModel() {
        return new RadiusGatewayProfileData();
    }

    @Override
    public void validate() {
        super.validate();
        RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
        if(Strings.isNullOrBlank(radiusGatewayProfileData.getVendorId()) == false){
            VendorData vendorData = CRUDOperationUtil.get(VendorData.class,radiusGatewayProfileData.getVendorId());
            if(vendorData == null){
                addActionError("Vendor id does not exist");
            }
            VendorData vendor = new VendorData();
            vendor.setId(radiusGatewayProfileData.getVendorId());
            radiusGatewayProfileData.setVendorData(vendor);
        }

        validatePacketMappings(radiusGatewayProfileData.getGwToPCCPacketMappings(),ConversionType.GATEWAY_TO_PCC.name());
        validatePacketMappings(radiusGatewayProfileData.getPccToGWPacketMappings(),ConversionType.PCC_TO_GATEWAY.name());
        validatePccRuleMappings(radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());
        validateGroovyScriptData(radiusGatewayProfileData.getGroovyScriptDatas());

        if (ACLAction.CREATE.getVal().equalsIgnoreCase(getMethodName())) {
            if (Collectionz.isNullOrEmpty(radiusGatewayProfileData.getServiceGuidingDatas()) == false) {
                filterServiceGuidings(radiusGatewayProfileData.getServiceGuidingDatas());
                filterDuplicateSerivces(radiusGatewayProfileData.getServiceGuidingDatas());
                verifyConfiguredServiceGuidings(radiusGatewayProfileData.getServiceGuidingDatas());
            }
        } else if (ACLAction.UPDATE.getVal().equalsIgnoreCase(getMethodName())) {
            RadiusGatewayProfileData radiusGatewayProfileFromDB = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayProfileData.getId());
            validateServiceGuidingDataWithExisting(radiusGatewayProfileData.getServiceGuidingDatas(), radiusGatewayProfileFromDB.getServiceGuidingDatas());
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
        if (Collectionz.isNullOrEmpty(newOrUpdatedServiceGuiding) == false) {
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
                    addFieldError("serviceId", "Service does not present in DB");
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

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            RadiusGatewayProfileData radiusGatewayProfileFromDB = CRUDOperationUtil.get(RadiusGatewayProfileData.class, radiusGatewayProfileData.getId());
            String type = getRequest().getParameter("type");
            List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMapData = Collectionz.newArrayList();
            if(Strings.isNullOrBlank(type) == false){
                if("gatewayToPCC".equalsIgnoreCase(type)){
                    radiusGwProfilePacketMapData = radiusGatewayProfileFromDB.getGwToPCCPacketMappings();
                }else if("pccToGateway".equalsIgnoreCase(type)){
                    radiusGwProfilePacketMapData = radiusGatewayProfileFromDB.getPccToGWPacketMappings();
                }
            }
            setPacketMappingAssociationList(radiusGwProfilePacketMapData);
            setActionChainUrl(getRedirectURL("packet-mapping-manageorder"));
            packetMappingAssociationList.sort(Comparator.comparing(RadiusGwProfilePacketMapData::getOrderNumber));
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

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            RadiusGatewayProfileData radiusGatewayProfileFromDB = CRUDOperationUtil.get(RadiusGatewayProfileData.class, radiusGatewayProfileData.getId());
            setServiceGuidings(radiusGatewayProfileFromDB.getServiceGuidingDatas());
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
    public HttpHeaders manageOrderForServiceGuiding(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrderForServiceGuiding()");
        }
        try{

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayProfileData.getId());
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
                CRUDOperationUtil.audit(radiusGatewayProfileData, serviceGuidingData.getServiceData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, radiusGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "RADIUS Gateway Profile's Service Guiding order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../radius-gateway-profile/"+radiusGatewayProfileData.getId()));
            addActionMessage("Service Guiding order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Service Guiding. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }


    @SkipValidation
    public HttpHeaders initManageOrderPccMapping(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrderPccMapping()");
        }
        try{

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            RadiusGatewayProfileData radiusGatewayProfileFromDB = CRUDOperationUtil.get(RadiusGatewayProfileData.class, radiusGatewayProfileData.getId());
            setPccRuleMappingAssociationList(radiusGatewayProfileFromDB.getRadiusGwProfilePCCRuleMappings());
            setActionChainUrl(getRedirectURL("pcc-rule-mapping-manageorder"));
            pccRuleMappingAssociationList.sort(Comparator.comparing(RadiusGwProfilePCCRuleMappingData::getOrderNumber));
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

            RadiusGatewayProfileData raidusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            RadiusGatewayProfileData radiusGatewayProfileFromDB = CRUDOperationUtil.get(RadiusGatewayProfileData.class, raidusGatewayProfileData.getId());
            setGroovyScriptList(radiusGatewayProfileFromDB.getGroovyScriptDatas());
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
    public HttpHeaders manageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrder()");
        }
        try{

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayProfileData.getId());
            String[] radiusGatewayPacketMappingDataIds = getRequest().getParameterValues("radiusGatewayPacketMapDataIds");
            int index = 1;
            for(String id: radiusGatewayPacketMappingDataIds){
                RadiusGwProfilePacketMapData radiusGwProfilePacketMappingData = CRUDOperationUtil.get(RadiusGwProfilePacketMapData.class,id);
                JsonObject oldJsonObject = radiusGwProfilePacketMappingData.toJson();
                int oldOrderNumber = radiusGwProfilePacketMappingData.getOrderNumber();

                radiusGwProfilePacketMappingData.setOrderNumber(index);
                int newOrderNumber = radiusGwProfilePacketMappingData.getOrderNumber();
                JsonObject newJsonObject = radiusGwProfilePacketMappingData.toJson();

                CRUDOperationUtil.update(radiusGwProfilePacketMappingData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + radiusGwProfilePacketMappingData.getPacketMappingData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(radiusGatewayProfileData, radiusGwProfilePacketMappingData.getPacketMappingData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, radiusGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Radius Gateway Profile's Packet Mapping order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../radius-gateway-profile/"+radiusGatewayProfileData.getId()));
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

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayProfileData.getId());
            String[] radiusGatewayPccRuleMappingDataIds = getRequest().getParameterValues("radiusGatewayPccRuleMapDataIds");
            int index = 1;
            for(String id: radiusGatewayPccRuleMappingDataIds){
                RadiusGwProfilePCCRuleMappingData radiusGwProfilePccRuleMappingData = CRUDOperationUtil.get(RadiusGwProfilePCCRuleMappingData.class,id);
                JsonObject oldJsonObject = radiusGwProfilePccRuleMappingData.toJson();
                int oldOrderNumber = radiusGwProfilePccRuleMappingData.getOrderNumber();

                radiusGwProfilePccRuleMappingData.setOrderNumber(index);
                int newOrderNumber = radiusGwProfilePccRuleMappingData.getOrderNumber();
                JsonObject newJsonObject = radiusGwProfilePccRuleMappingData.toJson();

                CRUDOperationUtil.update(radiusGwProfilePccRuleMappingData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + radiusGwProfilePccRuleMappingData.getPccRuleMappingData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(radiusGatewayProfileData, radiusGwProfilePccRuleMappingData.getPccRuleMappingData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, radiusGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Radius Gateway Profile's PCCRule Mapping order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../radius-gateway-profile/"+radiusGatewayProfileData.getId()));
            addActionMessage("PCCRule Mappimg order changed successfully");
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

            RadiusGatewayProfileData radiusGatewayProfileData = (RadiusGatewayProfileData) getModel();
            radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayProfileData.getId());
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
                CRUDOperationUtil.audit(radiusGatewayProfileData, groovyScriptData.getScriptName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, radiusGatewayProfileData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "RADIUS Gateway Profile's Groovy Script order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../radius-gateway-profile/"+radiusGatewayProfileData.getId()));
            addActionMessage("Groovy Script order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Groovy Script. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }


    private void validatePacketMappings(List<RadiusGwProfilePacketMapData> packetMappigs, String type) {
        if(Collectionz.isNullOrEmpty(packetMappigs) == false){
            packetMappigs.forEach(packetMapping -> {
                if(packetMapping != null) {
                    String packetMappingId = packetMapping.getPacketMappingId();
                    if (Strings.isNullOrBlank(packetMappingId)) {
                        addFieldError("packetMappingId", getText("required.packet.mapping"));
                    } else {
                        SimpleExpression expression = Restrictions.eq("type", type);
                        PacketMappingData packetMapData = CRUDOperationUtil.get(PacketMappingData.class, packetMappingId, expression);
                        if (packetMapData == null) {
                            addFieldError("packetMappingId", getText("invalid.packet.mapping"));
                        }
                    }
                }
            });
        }
    }

    private void validatePccRuleMappings(List<RadiusGwProfilePCCRuleMappingData> pccRuleMappings) {
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

    public List<String> getSupportedStandardValuesForUpdate() {
        return supportedStandardValuesForUpdate;
    }

    public void setSupportedStandardValuesForUpdate(List<String> supportedStandardValuesForUpdate) {
        this.supportedStandardValuesForUpdate = supportedStandardValuesForUpdate;
    }

    @Override
    public void prepareValuesForSubClass(){
        setVendorDataList(getVendorList());
        setSupportedVendorDataList(getSupportedVendors());
        setGatewayToPCCPacketMappingDataList(CRUDOperationUtil.findAll(PacketMappingData.class, GW_TO_PCRF_PACKET_MAPPING_CRITERIA));
        setPccToGatewayPacketMappingDataList(CRUDOperationUtil.findAll(PacketMappingData.class, PCRF_TO_GW_PACKET_MAPPING_CRITERIA));
        setPccRuleMappings(CRUDOperationUtil.findAll(PCCRuleMappingData.class,PROTOCOL_CRITERIA));
        setServiceDatas(CRUDOperationUtil.findAll(ServiceData.class));
        Gson gson = GsonFactory.defaultInstance();
        JsonArray serviceJson = gson.toJsonTree(getServiceDatas(),new TypeToken<List<ServiceData>>() {}.getType()).getAsJsonArray();
        setServiceDataJson(serviceJson.toString());
    }

    @Override
    public String getDataListAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        for(RadiusGatewayProfileData radiusGatewayProfileData : getList()){
            radiusGatewayProfileData.setRadiusGwProfilePacketMappings(null);
            radiusGatewayProfileData.setServiceGuidingDatas(null);
        }
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<RadiusGatewayProfileData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
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

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[1]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    public List<PacketMappingData> getGatewayToPCCPacketMappingDataList() {
        return gatewayToPCCPacketMappingDataList;
    }

    public void setGatewayToPCCPacketMappingDataList(List<PacketMappingData> gatewayToPCCPacketMappingDataList) {
        this.gatewayToPCCPacketMappingDataList = gatewayToPCCPacketMappingDataList;
    }


    public List<PacketMappingData> getPccToGatewayPacketMappingDataList() {
        return pccToGatewayPacketMappingDataList;
    }

    public void setPccToGatewayPacketMappingDataList(List<PacketMappingData> pccToGatewayPacketMappingDataList) {
        this.pccToGatewayPacketMappingDataList = pccToGatewayPacketMappingDataList;
    }

    public List<PCCRuleMappingData> getPccRuleMappings() {
        return pccRuleMappings;
    }

    public void setPccRuleMappings(List<PCCRuleMappingData> pccRuleMappings) {
        this.pccRuleMappings = pccRuleMappings;
    }


    public List<RadiusGwProfilePacketMapData> getPacketMappingAssociationList() {
        return packetMappingAssociationList;
    }

    public void setPacketMappingAssociationList(List<RadiusGwProfilePacketMapData> packetMappingAssociationList) {
        this.packetMappingAssociationList = packetMappingAssociationList;
    }

    public List<RadiusGwProfilePCCRuleMappingData> getPccRuleMappingAssociationList() {
        return pccRuleMappingAssociationList;
    }

    public void setPccRuleMappingAssociationList(List<RadiusGwProfilePCCRuleMappingData> pccRuleMappingAssociationList) {
        this.pccRuleMappingAssociationList = pccRuleMappingAssociationList;
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
