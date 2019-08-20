package com.elitecore.nvsmx.sm.controller.pccrulemapping.diameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.PacketMappingConstants;
import com.elitecore.corenetvertex.constants.ProtocolType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.VendorInformation;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/pccrulemapping")
@Results({
        @Result(name= SUCCESS, type= RestGenericCTRL.REDIRECT_ACTION ,params = {NVSMXCommonConstants.ACTION_NAME,"diameter-pcc-rule-mapping"}),
})
public class DiameterPccRuleMappingCTRL extends RestGenericCTRL<PCCRuleMappingData> {

    public static final String ATTRIBUTE_NAME = "attributeName";
    public static final String TYPE = "type";
    private List<AttributeData> attributeList = Collectionz.newArrayList();

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("commProtocol", CommunicationProtocol.DIAMETER.name());
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"called index() method");
        }
        List<PCCRuleMappingData> pccRuleMappings = CRUDOperationUtil.findAll(PCCRuleMappingData.class);
        List<PCCRuleMappingData> diameterPccRuleMappings = Collectionz.newArrayList();
        pccRuleMappings.stream().forEach(pccRuleMappingData -> {
            if(ProtocolType.DIAMETER.getProtocolType().equals(pccRuleMappingData.getCommProtocol())) {
                diameterPccRuleMappings.add(pccRuleMappingData);
            }
        });
        setList(diameterPccRuleMappings);
        setActionChainUrl("sm/pccrulemapping/pcc-rule-mapping/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.DIAMETER.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @SkipValidation
    @Override
    public String editNew() { // initCreate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called editNew()");
        }
        if(getModel() != null) {
            setModel(createModel());
        }
        PCCRuleMappingData pccRuleMapping = (PCCRuleMappingData) getModel();
        List<AttributeMappingData> attributeList = Collectionz.newArrayList();
        AttributeMappingData staticAttributeMapping = new AttributeMappingData();
        staticAttributeMapping.setType(PacketMappingConstants.STATIC.name());
        staticAttributeMapping.setMappings(getDefaultStaticMappings());
        attributeList.add(staticAttributeMapping);


        AttributeMappingData dynamicAttributeMappingData = new AttributeMappingData();
        dynamicAttributeMappingData.setType(PacketMappingConstants.DYNAMIC.name());
        dynamicAttributeMappingData.setMappings(getDefaultDynamicMappings());
        attributeList.add(dynamicAttributeMappingData);
        pccRuleMapping.setAttributeMappingDatas(attributeList);
        setModel(pccRuleMapping);
        setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    @Override
    public HttpHeaders create() {
        PCCRuleMappingData pccRuleMapping = (PCCRuleMappingData) getModel();
        pccRuleMapping.setCommProtocol(ProtocolType.DIAMETER.name());
        setStaticAndDynamicAttributeMappings(pccRuleMapping);
        return super.create();
    }

    private void setStaticAndDynamicAttributeMappings(PCCRuleMappingData pccRuleMapping) {
        List<AttributeMappingData> attributeMappings = Collectionz.newArrayList();
        AttributeMappingData staticAttributeMappings = pccRuleMapping.getStaticAttributeMappings();
        if(staticAttributeMappings != null) {
            staticAttributeMappings.setType(PacketMappingConstants.STATIC.name());
            staticAttributeMappings.setPccRuleMappingData(pccRuleMapping);
            attributeMappings.add(staticAttributeMappings);
        }
        AttributeMappingData dynamicAttributeMappings = pccRuleMapping.getDynamicAttributeMappings();
        if(dynamicAttributeMappings != null) {
            dynamicAttributeMappings.setType(PacketMappingConstants.DYNAMIC.name());
            dynamicAttributeMappings.setPccRuleMappingData(pccRuleMapping);
            attributeMappings.add(dynamicAttributeMappings);
        }
        pccRuleMapping.setAttributeMappingDatas(attributeMappings);
    }

    @Override
    public HttpHeaders update() {
        PCCRuleMappingData pccRuleMapping = (PCCRuleMappingData) getModel();
        if (StringUtils.isEmpty(pccRuleMapping.getId())) {
            getLogger().error(getLogModule(), "Error while updating Diameter PCC Rule Mapping.Reason: Diameter PCC Rule Mapping Id Not found");
            addActionError(getModule().getDisplayLabel() + " Id Not Found ");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }


        PCCRuleMappingData pccRuleMappingFromDB = CRUDOperationUtil.get(PCCRuleMappingData.class, pccRuleMapping.getId());
        if (Objects.isNull(pccRuleMappingFromDB)) {
            getLogger().error(getLogModule(), "Error while updating Diameter PCC Rule Mapping.Reason: Diameter PCC Rule Mapping not found with given Id: " + pccRuleMapping.getId());
            addActionError(getModule().getDisplayLabel() + " Not Found with given id: " + pccRuleMapping.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        pccRuleMapping.setCommProtocol(ProtocolType.DIAMETER.name());
        setStaticAndDynamicAttributeMappings(pccRuleMapping);
        pccRuleMapping.setDiameterGatewayProfilePccMappings(pccRuleMappingFromDB.getDiameterGatewayProfilePccMappings());
        return super.update();
    }


    @Override
    public void validate() {
        super.validate();
        PCCRuleMappingData pccRuleMappingData = (PCCRuleMappingData) getModel();


        boolean isValidMapping = validateAttributeMappings(pccRuleMappingData.getStaticAttributeMappings());
        if(isValidMapping == false){
            isValidMapping = validateAttributeMappings(pccRuleMappingData.getDynamicAttributeMappings());
            if (isValidMapping == false) {
                addActionError(getText("attribute.mapping.required"));
            }
        }else{
            if(pccRuleMappingData.getStaticAttributeMappings() != null) {
                validatePolicyKeyValues(pccRuleMappingData.getStaticAttributeMappings().getMappings());
                if (pccRuleMappingData.getStaticAttributeMappings().getMappings().length > CommonConstants.DEFAULT_MAPPING_COUNT) {
                    addFieldError("staticAttributeMappings.mappings", getText("attribute.mapping.maximum.count"));
                }
            }
            if(pccRuleMappingData.getDynamicAttributeMappings() != null) {
                validatePolicyKeyValues(pccRuleMappingData.getDynamicAttributeMappings().getMappings());
                if (pccRuleMappingData.getDynamicAttributeMappings().getMappings().length > CommonConstants.DEFAULT_MAPPING_COUNT) {
                    addFieldError("dynamicAttributeMappings.mappings", getText("attribute.mapping.maximum.count"));
                }
            }
        }
    }

    private void validatePolicyKeyValues(String[] mappings) {
        String invalidPolicyKeyValue = "\"PCCRule\"";
        for(String mapping : mappings){
            if(Strings.isNullOrBlank(mapping) == false && mapping.toLowerCase().contains(invalidPolicyKeyValue.toLowerCase())){
                addFieldError("mappings","Policy Key can not contain PCCRule as a value");
            }
        }
    }

    private boolean validateAttributeMappings(AttributeMappingData attributeMappingData) {
        return Arrays.asList(attributeMappingData.getMappings()).stream().filter(mapping -> Strings.isNullOrBlank(mapping) == false).findFirst().isPresent();
    }
    @Override
    public ACLModules getModule() {
        return ACLModules.PCCRULEMAPPING;
    }

    @Override
    public PCCRuleMappingData createModel() {
        return new PCCRuleMappingData();
    }

    public String getDiameterPccRuleMappingListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        for(PCCRuleMappingData pccRuleMappingData : getList()){
            pccRuleMappingData.setStaticAttributeMappings(null);
            pccRuleMappingData.setDynamicAttributeMappings(null);
        }
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<PCCRuleMappingData>>() {}.getType()).getAsJsonArray();

        return modelJson.toString();
    }


    public String getDictionaryData(){
        JsonObject jsonObject = new JsonObject();
        for(AttributeData attributeData : attributeList){
            VendorInformation vendorInfo = attributeData.getVendorInformation();
            String vendorId = vendorInfo.getVendorId();
            String attributeId = attributeData.getAttributeId();
            String attributeName = attributeData.getName();
            String type = attributeData.getType();
            String attributeStr = getAttributeName(vendorInfo, attributeId, attributeName);
            String key = getKey(vendorId, attributeId);
            JsonObject value = new JsonObject();
            value.addProperty(ATTRIBUTE_NAME, attributeStr);
            value.addProperty(TYPE,type);
            jsonObject.add(key,value);
        }
        return jsonObject.toString();


    }

    private String getKey(String vendorId, String attributeId) {
        StringBuilder key = new StringBuilder();
        key.append(vendorId);
        key.append(CommonConstants.COLON);
        key.append(attributeId);
        return key.toString();
    }

    private String getAttributeName(VendorInformation vendorInfo, String attributeId, String attributeName) {
        StringBuilder sb = new StringBuilder();
        sb.append(vendorInfo.getName());
        sb.append(CommonConstants.DASH);
        sb.append(attributeName);
        sb.append("[");
        sb.append(vendorInfo.getVendorId());
        sb.append(CommonConstants.COLON);
        sb.append(attributeId);
        sb.append("]");
        return sb.toString();
    }


    private void setSuggestedValues(){
       List<AttributeData> attributeMappings = CRUDOperationUtil.get(AttributeData.class, Order.asc("name"));
        attributeMappings.stream().forEach(attributeData -> {
            if(CommunicationProtocol.DIAMETER.name().equalsIgnoreCase(attributeData.getDictionaryType())) {
                attributeList.add(attributeData);
            }
        });


    }

    public String getPCRFKeySuggestions(){
        List<String> pcrfKeySuggestions = new ArrayList<>();
        for (PCRFKeyConstants val : PCRFKeyConstants.values(PCRFKeyType.PCC_RULE)) {
            if("PCCRule".equalsIgnoreCase(val.name()) == false) {
                pcrfKeySuggestions.add(val.getVal());
            }
        }
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJson(pcrfKeySuggestions);
    }



    @Override
    public void prepareValuesForSubClass() throws Exception {
        setSuggestedValues();
    }



    @Override
    public boolean prepareAndValidateDestroy(PCCRuleMappingData pccRuleMapping) {
        if(Collectionz.isNullOrEmpty(pccRuleMapping.getDiameterGatewayProfilePccMappings()) == false){
            addActionError("PCC Rule Mapping is associated with Diameter Gateway Profile");
            getLogger().error(getLogModule(),"PCC Rule Mapping " + pccRuleMapping.getName() +" is associated with Diameter Gateway Profile");
            return false;
        }
        return true;
    }


    private String[] getDefaultStaticMappings(){
        LinkedList<String> staticMappings = Collectionz.newLinkedList();
        staticMappings.add("{\"id\":\"3ba4d61f-10a6-465d-b5e8-bf59bf831f7d\",\"pid\":\"0\",\"attribute\":\"10415:1005\",\"policykey\":\"PCCRule.Name\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        return staticMappings.toArray(new String[]{});
    }

    private String[] getDefaultDynamicMappings(){
        LinkedList<String> dynamicMappings = Collectionz.newLinkedList();
        dynamicMappings.add("{\"id\":\"23ab54b8-84c6-4c97-q8ba-158987129723\",\"pid\":\"0\",\"attribute\":\"10415:1003\",\"policykey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"01ca34b3-84c6-4f84-a8dc-209934018165\",\"pid\":\"23ab54b8-84c6-4c97-q8ba-158987129723\",\"attribute\":\"0:432\",\"policykey\":\"PCCRule.ChargingKey\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"0fa83873-eefd-491a-aeb3-907865573efe\",\"pid\":\"23ab54b8-84c6-4c97-q8ba-158987129723\",\"attribute\":\"10415:1005\",\"policykey\":\"PCCRule.Name\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"pid\":\"23ab54b8-84c6-4c97-q8ba-158987129723\",\"attribute\":\"10415:1016\",\"policykey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"1f80033f-7332-4f10-a880-701f26470a54\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1028\",\"policykey\":\"PCCRule.QCI\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"fd400a63-b40a-4422-bc72-d4415da61eb7\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1025\",\"policykey\":\"PCCRule.GBRDL\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"77f90682-f1d3-4941-a280-e5d97ad6c593\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1026\",\"policykey\":\"PCCRule.GBRUL\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"ff3c7799-19c8-4750-8fec-72a10eb03130\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:515\",\"policykey\":\"PCCRule.MBRDL\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"26053131-6ef1-4667-8b9c-af42157974be\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:516\",\"policykey\":\"PCCRule.MBRUL\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"874bb21e-436a-4e04-bced-fdffb93d89c5\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1034\",\"policykey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"2ab97d3c-192c-40f8-8abf-796f4942a47f\",\"pid\":\"874bb21e-436a-4e04-bced-fdffb93d89c5\",\"attribute\":\"10415:1046\",\"policykey\":\"PCCRule.PriorityLevel\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"d4d7fcca-4653-461f-9448-4f27fe09b6b2\",\"pid\":\"874bb21e-436a-4e04-bced-fdffb93d89c5\",\"attribute\":\"10415:1047\",\"policykey\":\"PCCRule.PEC\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"c99c87c3-e382-41f5-bc29-cb339e874bf2\",\"pid\":\"874bb21e-436a-4e04-bced-fdffb93d89c5\",\"attribute\":\"10415:1048\",\"policykey\":\"PCCRule.PEV\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"abae1baf-23b3-41dd-b7ca-8861dd8d9ca2\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1010\",\"policykey\":\"PCCRule.Precedence\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"901ac6f6-438f-479b-8c6b-972591a9225a\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1066\",\"policykey\":\"PCCRule.MonitoringKey\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"7513272a-0735-4892-a747-be4ca89897d6\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1009\",\"policykey\":\"PCCRule.OnlineCharging\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"1bd97376-04f7-44b6-a7c5-8a6348d19b19\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1008\",\"policykey\":\"PCCRule.OfflineCharging\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"09639133-fd01-4331-a613-7411d6c00780\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:511\",\"policykey\":\"PCCRule.FlowStatus\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"b312143f-b4ea-446c-a237-612d3d6ca178\",\"pid\":\"cfe9a95a-3c59-4cbe-98c1-298a093cf465\",\"attribute\":\"10415:1058\",\"policykey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        dynamicMappings.add("{\"id\":\"c99e78e1-4eb3-4c69-844e-1cd2fb67717b\",\"pid\":\"b312143f-b4ea-446c-a237-612d3d6ca178\",\"attribute\":\"10415:507\",\"policykey\":\"PCCRule.ServiceDataFlow\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}");
        return dynamicMappings.toArray(new String[]{});
    }
}
