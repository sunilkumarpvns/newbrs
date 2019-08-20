package com.elitecore.nvsmx.sm.controller.packetmapping;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.constants.ProtocolType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.VendorInformation;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/packetmapping")
@Results({
        @Result(name= SUCCESS, type= RestGenericCTRL.REDIRECT_ACTION ,params = {NVSMXCommonConstants.ACTION_NAME,"radius-packet-mapping"}),
})
public class RadiusPacketMappingCTRL extends RestGenericCTRL<PacketMappingData> {

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
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"called index() method");
        }
        List<PacketMappingData> packetMappings = CRUDOperationUtil.findAll(PacketMappingData.class);
        List<PacketMappingData> radiusPacketMappings = Collectionz.newArrayList();
        packetMappings.stream().forEach(packetMappingData -> {
            if(ProtocolType.RADIUS.getProtocolType().equals(packetMappingData.getCommProtocol())) {
                radiusPacketMappings.add(packetMappingData);
            }
        });
        setActionChainUrl("sm/packetmapping/packet-mapping/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.RADIUS.name());
        setList(radiusPacketMappings);
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @Override
    public HttpHeaders create() {
        PacketMappingData packetMapping = (PacketMappingData) getModel();
        packetMapping.setCommProtocol(ProtocolType.RADIUS.name());
        packetMapping.getAttributeMappingData().setPacketMappingData(packetMapping);
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        PacketMappingData packetMapping = (PacketMappingData) getModel();
        if (StringUtils.isEmpty(packetMapping.getId())) {
            getLogger().error(getLogModule(), "Error while updating Radius Packet Mapping.Reason: Radius Packet Mapping Id Not found");
            addActionError(getModule().getDisplayLabel() + " Id Not Found ");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }


        PacketMappingData packetMappingFromDB = CRUDOperationUtil.get(PacketMappingData.class, packetMapping.getId());
        if (Objects.isNull(packetMappingFromDB)) {
            getLogger().error(getLogModule(), "Error while updating Radius Packet Mapping.Reason: Radius Packet Mapping not found with given Id: " + packetMapping.getId());
            addActionError(getModule().getDisplayLabel() + " Not Found with given id: " + packetMapping.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        packetMapping.setCommProtocol(ProtocolType.RADIUS.name());
        packetMapping.getAttributeMappingData().setPacketMappingData(packetMapping);
        packetMapping.setRadiusGatewayProfilePacketMappings(packetMappingFromDB.getRadiusGatewayProfilePacketMappings());
        return super.update();
    }


    @Override
    protected boolean prepareAndValidateDestroy(PacketMappingData packetMapping) {
        if(Collectionz.isNullOrEmpty(packetMapping.getRadiusGatewayProfilePacketMappings()) == false){
            addActionError("Packet Mapping "+ packetMapping.getName() +" is associated with Radius Gateway Profiles");
            String attachedGatewayProfiles = Strings.join(",", packetMapping.getRadiusGatewayProfilePacketMappings(), gwProfilePacketMapData -> gwProfilePacketMapData.getRadiusGatewayProfileData().getName());
            getLogger().error(getLogModule(),"Error while deleting packet mapping " + packetMapping.getName() +".Reason: packet mapping  is associated with "+attachedGatewayProfiles +" Radius Gateway Profiles");
            return false;
        }
        return true;
    }

    @Override
    public void validate(){
        super.validate();
        PacketMappingData packetMappingData = (PacketMappingData) getModel();
        List<PacketType> packetTypes = PacketType.getPacketTypeList(ProtocolType.RADIUS, ConversionType.valueOf(packetMappingData.getType()));
        if(packetTypes.contains(PacketType.valueOf(packetMappingData.getPacketType())) == false){
            addActionError("Invalid Packet Type " + packetMappingData.getPacketType() + " is configured");
        }
        String[] mappings = packetMappingData.getAttributeMappingData().getMappings();
        if(mappings.length == 0){
            addFieldError("attributeMappingData.mappings",getText("attribute.mapping.required"));
        } else if(mappings.length > CommonConstants.DEFAULT_MAPPING_COUNT){
            addFieldError("attributeMappingData.mappings",getText("attribute.mapping.maximum.count"));
        }
    }


    @Override
    public ACLModules getModule() {
        return ACLModules.PACKETMAPPING;
    }

    @Override
    public PacketMappingData createModel() {
        return new PacketMappingData();
    }

    public String getRadiusPacketMappingListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        for(PacketMappingData packetMappingData : getList()){
            packetMappingData.setAttributeMappingData(null);
        }
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<PacketMappingData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("commProtocol", CommunicationProtocol.RADIUS.name());
    }

    public void prepareShow() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called prepareShow()");
        }
        prepareValuesForSubClass();
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


    public String getPCRFKeySuggestions() {
        Set<String> pcrfKeySuggestions = new HashSet<>();
        for (PCRFKeyConstants pccAttribute : PCRFKeyConstants.values(PCRFKeyType.REQUEST)) {
            pcrfKeySuggestions.add(pccAttribute.getVal());
        }

        for (PCRFKeyConstants pccAttribute : PCRFKeyConstants.values(PCRFKeyType.RESPONSE)) {
            pcrfKeySuggestions.add(pccAttribute.getVal());
        }

        Gson gson = GsonFactory.defaultInstance();
        return gson.toJson(pcrfKeySuggestions);
    }
}
