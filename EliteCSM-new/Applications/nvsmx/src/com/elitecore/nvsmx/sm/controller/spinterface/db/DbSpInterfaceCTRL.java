package com.elitecore.nvsmx.sm.controller.spinterface.db;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.spinterface.DbSpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceFieldMappingData;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to manage all the operation related to DB Sp Interface
 * @author dhyani.raval
 */
@ParentPackage(value = "sm")
@Namespace("/sm/spinterface")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","db-sp-interface"}),
})
public class DbSpInterfaceCTRL extends RestGenericCTRL<SpInterfaceData> {


    private List<DatabaseData> databaseDataList = Collectionz.newArrayList();

    public List<DatabaseData> getDatabaseDataList() {
        return databaseDataList;
    }

    public void setDatabaseDataList(List<DatabaseData> databaseDataList) {
        this.databaseDataList = databaseDataList;
    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH).append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("spInterfaceType",SpInterfaceType.DB_SP_INTERFACE.name());
    }


    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called index()");
        }
        List<SpInterfaceData> spInterfaceDatas = CRUDOperationUtil.findAll(SpInterfaceData.class);
        List<SpInterfaceData> dbSpInterfaceDatas = Collectionz.newArrayList();
        spInterfaceDatas.stream().forEach(dbSpInterfaceData -> {
            if(SpInterfaceType.DB_SP_INTERFACE.name().equals(dbSpInterfaceData.getSpInterfaceType())) {
                dbSpInterfaceDatas.add(dbSpInterfaceData);
            }
        });
        setList(dbSpInterfaceDatas);
        setActionChainUrl("sm/spinterface/sp-interface/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE,SpInterfaceType.DB_SP_INTERFACE.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @Override
    public void validate() {

        SpInterfaceData spInterfaceData = (SpInterfaceData) getModel();
        spInterfaceData.setSpInterfaceType(SpInterfaceType.DB_SP_INTERFACE.name());
        spInterfaceData.getDbSpInterfaceData().setSpInterfaceData(spInterfaceData);
        spInterfaceData.setLdapSpInterfaceData(null);
        String dbSpInterfaceId = spInterfaceData.getDbSpInterfaceData().getId();
        if(Strings.isNullOrBlank(dbSpInterfaceId) == false && ACLAction.CREATE.name().equalsIgnoreCase(getMethodName())) {
            DbSpInterfaceData dbSpInterfaceData = CRUDOperationUtil.get(DbSpInterfaceData.class, dbSpInterfaceId);
            if(dbSpInterfaceData != null){
                addFieldError("dbSpInterfaceData", "DB Sp Interface with Id already exist in DB");
            }
        }

        if(Strings.isNullOrBlank(spInterfaceData.getDbSpInterfaceData().getDatabaseId())){
            addFieldError("databaseId","Database Data Source is Required");
        } else {
            DatabaseData databaseDataTemp = CRUDOperationUtil.get(DatabaseData.class, spInterfaceData.getDbSpInterfaceData().getDatabaseId());
            if(databaseDataTemp == null) {
                addFieldError("databaseId","Database Data Source does not exist");
            } else {
                spInterfaceData.getDbSpInterfaceData().setDatabaseData(databaseDataTemp);
            }
        }
        List<SpInterfaceFieldMappingData> fieldMappingDataList = spInterfaceData.getDbSpInterfaceData().getSpInterfaceFieldMappingDatas();
        Collectionz.filter(fieldMappingDataList, Predicates.nonNull());
        if(Collectionz.isNullOrEmpty(fieldMappingDataList)){
            addFieldError("spInterfaceFieldMappingDatas",getText("sp.interface.field.mapping.required"));
        }else{
            fieldMappingDataList.forEach(fieldMapping -> {
               if(fieldMapping.getId() == null && fieldMapping.getFieldName() == null && fieldMapping.getLogicalName() == null){
                    addFieldError("spInterfaceFieldMappingDatas",getText("sp.interface.field.mapping.required"));
                }
            });
        }
        //validate Subscriber Identity Field
        boolean isSubscriberIdExist = isSubscriberIdMapped(fieldMappingDataList);
        if(isSubscriberIdExist == false) {
            addActionError(SPRFields.SUBSCRIBER_IDENTITY.name() + " Required");
        }

        //validate product offer
        boolean isProductOfferExist = isProductOfferMapped(fieldMappingDataList);
        if(isProductOfferExist == false) {
            addActionError(SPRFields.PRODUCT_OFFER.name() + " Required");
        }

        super.validate();
    }



    private boolean isSubscriberIdMapped(List<SpInterfaceFieldMappingData> fieldMappingDataList) {
        boolean isSubscriberIdExist = false;
        if(Collectionz.isNullOrEmpty(fieldMappingDataList) == false){
            for(SpInterfaceFieldMappingData fieldMappingData : fieldMappingDataList){
                if(fieldMappingData != null && SPRFields.SUBSCRIBER_IDENTITY.name().equals(fieldMappingData.getLogicalName())){
                    isSubscriberIdExist = true;
                    break;
                }

            }
        }
        return isSubscriberIdExist;
    }


    private boolean isProductOfferMapped(List<SpInterfaceFieldMappingData> fieldMappingDataList) {
        boolean isProductOfferExist = false;
        if(Collectionz.isNullOrEmpty(fieldMappingDataList) == false){
            for(SpInterfaceFieldMappingData fieldMappingData : fieldMappingDataList){
                if(fieldMappingData != null && SPRFields.PRODUCT_OFFER.name().equals(fieldMappingData.getLogicalName())){
                    isProductOfferExist = true;
                    break;
                }

            }
        }
        return isProductOfferExist;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.SPINTERFACE;
    }

    @Override
    public SpInterfaceData createModel() {
        return new SpInterfaceData();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception{
        setDatabaseDataList(CRUDOperationUtil.findAll(DatabaseData.class));
    }
    public String getSpInterfaceFieldMappingAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        SpInterfaceData spInterfaceData = (SpInterfaceData) getModel();
        return gson.toJsonTree(spInterfaceData.getDbSpInterfaceData().getSpInterfaceFieldMappingDatas(), new TypeToken<List<SpInterfaceData>>() {
        }.getType()).getAsJsonArray().toString();
    }

    public List<SPRFields> getPredefinedAttributes(){
        List<SPRFields> strings = Collectionz.newArrayList();
        strings.add(SPRFields.USERNAME);
        strings.add(SPRFields.PASSWORD);
        strings.add(SPRFields.CUSTOMER_TYPE);
        strings.add(SPRFields.PRODUCT_OFFER);
        strings.add(SPRFields.IMS_PACKAGE);
        strings.add(SPRFields.EXPIRY_DATE);
        strings.add(SPRFields.CUI);
        strings.add(SPRFields.IMSI);
        strings.add(SPRFields.SUBSCRIBER_IDENTITY);
        strings.add(SPRFields.PARENT_ID);
        return strings;
    }
}