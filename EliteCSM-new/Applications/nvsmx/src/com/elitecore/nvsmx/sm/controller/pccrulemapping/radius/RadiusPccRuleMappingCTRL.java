package com.elitecore.nvsmx.sm.controller.pccrulemapping.radius;

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
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/pccrulemapping")
@Results({
        @Result(name= SUCCESS, type= RestGenericCTRL.REDIRECT_ACTION ,params = {NVSMXCommonConstants.ACTION_NAME,"radius-pcc-rule-mapping"}),
})
public class RadiusPccRuleMappingCTRL extends RestGenericCTRL<PCCRuleMappingData> {

    public static final String ATTRIBUTE_NAME = "attributeName";
    public static final String TYPE = "type";
    private List<AttributeData> attributeList = Collectionz.newArrayList();

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[1]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("commProtocol", CommunicationProtocol.RADIUS.name());
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called index()");
        }
        List<PCCRuleMappingData> pccRuleMappings = CRUDOperationUtil.findAll(PCCRuleMappingData.class);
        List<PCCRuleMappingData> radiusPccRuleMappings = Collectionz.newArrayList();
        pccRuleMappings.stream().forEach(pccRuleMappingData -> {
            if(ProtocolType.RADIUS.getProtocolType().equals(pccRuleMappingData.getCommProtocol())) {
                radiusPccRuleMappings.add(pccRuleMappingData);
            }
        });
        setList(radiusPccRuleMappings);
        setActionChainUrl("sm/pccrulemapping/pcc-rule-mapping/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.RADIUS.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @Override
    public HttpHeaders create() {
        PCCRuleMappingData pccRuleMapping = (PCCRuleMappingData) getModel();
        pccRuleMapping.setCommProtocol(ProtocolType.RADIUS.name());
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
            getLogger().error(getLogModule(), "Error while updating Radius PCC Rule Mapping.Reason: Radius PCC Rule Mapping Id Not found");
            addActionError(getModule().getDisplayLabel() + " Id Not Found ");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        PCCRuleMappingData pccRuleMappingFromDB = CRUDOperationUtil.get(PCCRuleMappingData.class, pccRuleMapping.getId());
        if (Objects.isNull(pccRuleMappingFromDB)) {
            getLogger().error(getLogModule(), "Error while updating Radius PCC Rule Mapping.Reason: Radius PCC Rule Mapping not found with given Id: " + pccRuleMapping.getId());
            addActionError(getModule().getDisplayLabel() + " Not Found with given id: " + pccRuleMapping.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        pccRuleMapping.setCommProtocol(ProtocolType.RADIUS.name());
        setStaticAndDynamicAttributeMappings(pccRuleMapping);
        pccRuleMapping.setRadiusGatewayProfilePccRuleMappings(pccRuleMappingFromDB.getRadiusGatewayProfilePccRuleMappings());
        return super.update();
    }

    @Override
    public void validate() {
        super.validate();
        PCCRuleMappingData radiusPccRuleMappingData = (PCCRuleMappingData) getModel();
        boolean isValidMapping = validateAttributeMappings(radiusPccRuleMappingData.getStaticAttributeMappings());
        if(isValidMapping == false){
            isValidMapping = validateAttributeMappings(radiusPccRuleMappingData.getDynamicAttributeMappings());
            if (isValidMapping == false) {
                addActionError(getText("attribute.mapping.required"));
            }
        }else{
            if(radiusPccRuleMappingData.getStaticAttributeMappings() != null) {
                validatePolicyKeyValues(radiusPccRuleMappingData.getStaticAttributeMappings().getMappings());
                if (radiusPccRuleMappingData.getStaticAttributeMappings().getMappings().length > CommonConstants.DEFAULT_MAPPING_COUNT) {
                    addFieldError("staticAttributeMappings.mappings", getText("attribute.mapping.maximum.count"));
                }
            }
            if(radiusPccRuleMappingData.getDynamicAttributeMappings() != null) {
                validatePolicyKeyValues(radiusPccRuleMappingData.getDynamicAttributeMappings().getMappings());
                if (radiusPccRuleMappingData.getDynamicAttributeMappings().getMappings().length > CommonConstants.DEFAULT_MAPPING_COUNT) {
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

    public String getRadiusPccRuleMappingListAsJson(){
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
        for(AttributeData attributeData : attributeList) {
            if (attributeData.getParentAttributeData() == null) {
                VendorInformation vendorInfo = attributeData.getVendorInformation();
                String vendorId = vendorInfo.getVendorId();
                String attributeId = attributeData.getAttributeId();
                String attributeName = attributeData.getName();
                String type = attributeData.getType();
                String key = getKey(vendorId, attributeId);
                JsonObject value = new JsonObject();
                value.addProperty(ATTRIBUTE_NAME, attributeName);
                value.addProperty(TYPE, type);
                List<AttributeData> childAttributes = attributeData.getChildAttributes();
                generateChildAttributesList(key, childAttributes, jsonObject);
                jsonObject.add(key, value);
            }
        }
        return jsonObject.toString();
    }




    private void generateChildAttributesList(String key,List<AttributeData> childAttributes,JsonObject jsonObject){
        if(Collectionz.isNullOrEmpty(childAttributes) == false) {
            for(AttributeData attributeData : childAttributes){
                String attrId = attributeData.getAttributeId();
                String attrName = attributeData.getName();
                String childAttrKey = getKey(key, attrId);
                String type = attributeData.getType();
                generateChildAttributesList(childAttrKey, attributeData.getChildAttributes(), jsonObject);
                JsonObject value = new JsonObject();
                value.addProperty(ATTRIBUTE_NAME , attrName);
                value.addProperty(TYPE,type);
                jsonObject.add(childAttrKey,value);
            }
        }
    }


    public String getParentAttributes() {
        JsonObject jsonObject = new JsonObject();
        for (AttributeData attributeData : attributeList) {
            if (attributeData.getParentAttributeData() == null) {
                VendorInformation vendorInfo = attributeData.getVendorInformation();
                String vendorId = vendorInfo.getVendorId();
                String attributeId = attributeData.getAttributeId();
                String attributeName = attributeData.getName();
                String type = attributeData.getType();
                String key = getKey(vendorId, attributeId);
                JsonObject value = new JsonObject();
                value.addProperty(ATTRIBUTE_NAME, attributeName);
                value.addProperty(TYPE, type);
                jsonObject.add(key, value);
            }
        }
        return jsonObject.toString();
    }

    private String getKey(String parentId, String attributeId) {
        StringBuilder sb = new StringBuilder();
        sb.append(parentId);
        sb.append(CommonConstants.COLON);
        sb.append(attributeId);
        return sb.toString();
    }



    private void setSuggestedValues(){
        List<AttributeData> attributeMappings = CRUDOperationUtil.get(AttributeData.class, Order.asc("name"));
        attributeMappings.stream().forEach(attributeData -> {
            if(CommunicationProtocol.RADIUS.name().equalsIgnoreCase(attributeData.getDictionaryType())) {
                attributeList.add(attributeData);
            }
        });

    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setSuggestedValues();
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
    public boolean prepareAndValidateDestroy(PCCRuleMappingData pccRuleMapping) {
        if(Collectionz.isNullOrEmpty(pccRuleMapping.getRadiusGatewayProfilePccRuleMappings()) == false){
            addActionError("PCC Rule Mapping is associated with Radius Gateway Profile");
            getLogger().error(getLogModule(),"PCC Rule Mapping " + pccRuleMapping.getName() +" is associated with Radius Gateway Profile");
            return false;
        }
        return true;
    }

}
