package com.elitecore.nvsmx.sm.controller.driver.dbcdr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverFieldMappingData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author dhyani.raval
 */
@ParentPackage(value = "sm")
@Namespace("/sm/driver")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","db-cdr-driver"}),
})
public class DbCdrDriverCTRL extends RestGenericCTRL<DriverData> {

    private static final int MAXLENGTH_PCRFKEY = 255;
    private static final int MAXLENGTH_DBFIELD = 30;
    private static final int MAXLENGTH_DATATYPE = 64;
    private static final String RESTFIELD_PCRF_KEY = "pcrfKey";
    private static final String RESTFIELD_DBFIELD = "dbField";
    private static final String RESTFIELD_DATATYPE = "dataType";
    private static final String DISPLAY_PROPERTY_PCRF_KEY = "db.cdr.field.mapping.pcrf.key";
    private static final String DISPLAY_PROPERTY_DBFIELD = "db.cdr.field.mapping.db.field";
    private static final String DISPLAY_PROPERTY_DATATYPE = "db.cdr.field.mapping.datatype";

    private List<DatabaseData> databaseDataList = new ArrayList<>();

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
        List<DriverData> driverDatas = CRUDOperationUtil.findAll(DriverData.class);
        List<DriverData> dbCDRDriverDatas = Collectionz.newArrayList();
        driverDatas.stream().forEach(driverData -> {
            if(DriverType.DB_CDR_DRIVER.name().equals(driverData.getDriverType())) {
                dbCDRDriverDatas.add(driverData);
            }
        });
        setList(dbCDRDriverDatas);
        setActionChainUrl("sm/driver/driver/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, DriverType.DB_CDR_DRIVER.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }


    @Override
    public HttpHeaders create() {
        DriverData driverData = (DriverData) getModel();
        driverData.setDriverType(DriverType.DB_CDR_DRIVER.name());
        driverData.getDbCdrDriverData().setDriverData(driverData);
        driverData.setCsvDriverData(null);
        Collectionz.filter(driverData.getDbCdrDriverData().getDbCdrDriverFieldMappingDataList(), Predicates.nonNull());
        driverData.getDbCdrDriverData().setDatabaseData(CRUDOperationUtil.get(DatabaseData.class,driverData.getDbCdrDriverData().getDatabaseId()));
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        DriverData driverData = (DriverData) getModel();
        driverData.setDriverType(DriverType.DB_CDR_DRIVER.name());
        driverData.setCsvDriverData(null);
        driverData.getDbCdrDriverData().setDriverData(driverData);
        Collectionz.filter(driverData.getDbCdrDriverData().getDbCdrDriverFieldMappingDataList(), Predicates.nonNull());
        driverData.getDbCdrDriverData().setDatabaseData(CRUDOperationUtil.get(DatabaseData.class,driverData.getDbCdrDriverData().getDatabaseId()));
        return super.update();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception{
        setDatabaseDataList(CRUDOperationUtil.findAll(DatabaseData.class));
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.DRIVER;
    }

    @Override
    public DriverData createModel() {
        return new DriverData();
    }

    public List<DatabaseData> getDatabaseDataList() {
        return databaseDataList;
    }

    public void setDatabaseDataList(List<DatabaseData> databaseDataList) {
        this.databaseDataList = databaseDataList;
    }

    public String getDbCdrFieldMappingAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        DriverData driverData = (DriverData) getModel();
        return gson.toJsonTree(driverData.getDbCdrDriverData().getDbCdrDriverFieldMappingDataList(), new TypeToken<List<DriverData>>() {}.getType()).getAsJsonArray().toString();

    }
    public String getDbCdrDriverDataAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<DriverData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("driverType", DriverType.DB_CDR_DRIVER.name());
    }

    @Override
    public void validate() {
        DriverData driverData = (DriverData) getModel();
        validateBatchParameters(driverData);
        validateFieldMappings(driverData.getDbCdrDriverData().getDbCdrDriverFieldMappingDataList());
        super.validate();
    }

    private void validateFieldMappings(List<DbCdrDriverFieldMappingData> dbCdrDriverFieldMappingDataList) {
        if (dbCdrDriverFieldMappingDataList.isEmpty()) {
            addFieldError("dbCdrDriverFieldMappingDataList", "Field mapping is required");
            return;
        }


        Set<String> pcrfMapping = new HashSet<>();
        Set<String> dbFieldMapping = new HashSet<>();

        for (DbCdrDriverFieldMappingData data : dbCdrDriverFieldMappingDataList) {
            if (data == null) {
                continue;
            }
            validateRequiredField(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY);
            validateRequiredField(data.getDbField(), RESTFIELD_DBFIELD, DISPLAY_PROPERTY_DBFIELD);
            validateRequiredField(data.getDataType(), RESTFIELD_DATATYPE, DISPLAY_PROPERTY_DATATYPE);
            validateMaxLength(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY, MAXLENGTH_PCRFKEY);
            validateMaxLength(data.getDbField(), RESTFIELD_DBFIELD, DISPLAY_PROPERTY_DBFIELD, MAXLENGTH_DBFIELD);
            validateMaxLength(data.getDataType(), RESTFIELD_DATATYPE, DISPLAY_PROPERTY_DATATYPE, MAXLENGTH_DATATYPE);

            if(pcrfMapping.add(data.getPcrfKey().toLowerCase()) == false){
                addFieldError(RESTFIELD_PCRF_KEY,"Duplicate Mapping for PCRF Key '"+data.getPcrfKey() +"' in Field Mapping");
            }
            if(dbFieldMapping.add(data.getDbField().toLowerCase()) == false){
                addFieldError(RESTFIELD_PCRF_KEY,"Duplicate Mapping for DB field Key '"+data.getDbField() +"' in Field Mapping");
            }
        }
    }

    private void validateBatchParameters(DriverData driverData) {
        if(CommonStatusValues.ENABLE.getStringNameBoolean().equals(driverData.getDbCdrDriverData().getBatchUpdate())){
            Integer batchSize = driverData.getDbCdrDriverData().getBatchSize();
            if(batchSize <= 0){
                addFieldError("batchSize",getText("db.cdr.invalid.batchsize"));
            }else if(batchSize < CommonConstants.BATCH_SIZE_MIN || batchSize > CommonConstants.MAX_BATCH_SIZE){
                addFieldError("batchSize",getText("db.cdr.invalid.batchsize.range"));
            }
            if(driverData.getDbCdrDriverData().getBatchUpdateQueryTimeout() <= 0){
                addFieldError("batchUpdateQueryTimeout",getText("db.cdr.invalid.querytimeout"));
            }else if(driverData.getDbCdrDriverData().getBatchUpdateQueryTimeout() < CommonConstants.MIN_QUERY_TIMEOUT_IN_SEC || driverData.getDbCdrDriverData().getBatchUpdateQueryTimeout() > CommonConstants.MAX_QUERY_TIMEOUT_IN_SEC){
                addFieldError("batchUpdateQueryTimeout",getText("invalid.query.timeout"));
            }

        }

    }

}
