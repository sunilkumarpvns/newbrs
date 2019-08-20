package com.elitecore.corenetvertex.sm.driver.dbcdr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * Contains DB CDR Driver related information for Driver Management
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverData")
@Table(name = "TBLM_DB_CDR_DRIVER")
public class DbCdrDriverData implements Serializable {

    private String id;
    private DatabaseData databaseData;
    private Integer maxQueryTimeoutCount;
    private String batchUpdate;
    private Integer batchSize;
    private Integer batchUpdateQueryTimeout;
    private String tableName;
    private String identityField;
    private String sequenceName;
    private String storeAllCdr;
    private String timeStampFieldName;
    private String reportingType;
    private String sessionIdFieldName;
    private String createDateFieldName;
    private String lastModifiedDateFieldName;
    private String inputOctetsFieldName;
    private String outputOctetsFieldName;
    private String totalOctetsFieldName;
    private String usageTimeFieldName;
    private String usageKeyFieldName;
    private List<DbCdrDriverFieldMappingData> dbCdrDriverFieldMappingDataList;
    private transient DriverData driverData;

    public DbCdrDriverData() {
        dbCdrDriverFieldMappingDataList = Collectionz.newArrayList();
    }

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "DATABASE_ID")
    @JsonIgnore
    public DatabaseData getDatabaseData() {
        return databaseData;
    }

    public void setDatabaseData(DatabaseData databaseData) {
        this.databaseData = databaseData;
    }

    @Column(name = "MAX_QUERY_TIMEOUT_COUNT")
    public Integer getMaxQueryTimeoutCount() {
        return maxQueryTimeoutCount;
    }

    public void setMaxQueryTimeoutCount(Integer maxQueryTimeoutCount) {
        this.maxQueryTimeoutCount = maxQueryTimeoutCount;
    }

    @Column(name = "BATCH_UPDATE")
    public String getBatchUpdate() {
        return batchUpdate;
    }

    public void setBatchUpdate(String batchUpdate) {
        this.batchUpdate = batchUpdate;
    }

    @Column(name = "BATCH_SIZE")
    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Column(name = "BATCH_UPDATE_QUERY_TIMEOUT")
    public Integer getBatchUpdateQueryTimeout() {
        return batchUpdateQueryTimeout;
    }

    public void setBatchUpdateQueryTimeout(Integer batchUpdateQueryTimeout) {
        this.batchUpdateQueryTimeout = batchUpdateQueryTimeout;
    }

    @Column(name = "TABLE_NAME")
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Column(name = "IDENTITY_FIELD")
    public String getIdentityField() {
        return identityField;
    }

    public void setIdentityField(String identityField) {
        this.identityField = identityField;
    }

    @Column(name = "SEQUENCE_NAME")
    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Column(name = "STORE_ALL_CDR")
    public String getStoreAllCdr() {
        return storeAllCdr;
    }

    public void setStoreAllCdr(String storeAllCdr) {
        this.storeAllCdr = storeAllCdr;
    }

    @Column(name = "TIMESTAMP_FIELD_NAME")
    public String getTimeStampFieldName() {
        return timeStampFieldName;
    }

    public void setTimeStampFieldName(String timeStampFieldName) {
        this.timeStampFieldName = timeStampFieldName;
    }

    @Column(name = "REPORTING_TYPE")
    public String getReportingType() {
        return reportingType;
    }

    public void setReportingType(String reportingType) {
        this.reportingType = reportingType;
    }

    @Column(name = "SESSION_ID_FIELD_NAME")
    public String getSessionIdFieldName() {
        return sessionIdFieldName;
    }

    public void setSessionIdFieldName(String sessionIdFieldName) {
        this.sessionIdFieldName = sessionIdFieldName;
    }

    @Column(name = "CREATE_DATE_FIELD_NAME")
    public String getCreateDateFieldName() {
        return createDateFieldName;
    }

    public void setCreateDateFieldName(String createDateFieldName) {
        this.createDateFieldName = createDateFieldName;
    }

    @Column(name = "LAST_MODIFIED_DATE_FIELD_NAME")
    public String getLastModifiedDateFieldName() {
        return lastModifiedDateFieldName;
    }

    public void setLastModifiedDateFieldName(String lastModifiedDateFieldName) {
        this.lastModifiedDateFieldName = lastModifiedDateFieldName;
    }

    @Column(name = "INPUT_OCTETS_FIELD_NAME")
    public String getInputOctetsFieldName() {
        return inputOctetsFieldName;
    }

    public void setInputOctetsFieldName(String inputOctetsFieldName) {
        this.inputOctetsFieldName = inputOctetsFieldName;
    }

    @Column(name = "OUTPUT_OCTETS_FIELD_NAME")
    public String getOutputOctetsFieldName() {
        return outputOctetsFieldName;
    }

    public void setOutputOctetsFieldName(String outputOctetsFieldName) {
        this.outputOctetsFieldName = outputOctetsFieldName;
    }

    @Column(name = "TOTAL_OCTETS_FIELD_NAME")
    public String getTotalOctetsFieldName() {
        return totalOctetsFieldName;
    }

    public void setTotalOctetsFieldName(String totalOctetsFieldName) {
        this.totalOctetsFieldName = totalOctetsFieldName;
    }

    @Column(name = "USAGE_TIME_FIELD_NAME")
    public String getUsageTimeFieldName() {
        return usageTimeFieldName;
    }

    public void setUsageTimeFieldName(String usageTimeFieldName) {
        this.usageTimeFieldName = usageTimeFieldName;
    }

    @Column(name = "USAGE_KEY_FIELD_NAME")
    public String getUsageKeyFieldName() {
        return usageKeyFieldName;
    }

    public void setUsageKeyFieldName(String usageKeyFieldName) {
        this.usageKeyFieldName = usageKeyFieldName;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true )
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "DB_CDR_DRIVER_ID")
    public List<DbCdrDriverFieldMappingData> getDbCdrDriverFieldMappingDataList() {
        return dbCdrDriverFieldMappingDataList;
    }

    public void setDbCdrDriverFieldMappingDataList(List<DbCdrDriverFieldMappingData> dbCdrDriverFieldMappingDataList) {
        this.dbCdrDriverFieldMappingDataList = dbCdrDriverFieldMappingDataList;
    }

    @Transient
    public String getDatabaseId() {
        if(this.getDatabaseData()!=null){
            return getDatabaseData().getId();
        }
        return null;
    }

    public void setDatabaseId(String databaseId) {
        if(Strings.isNullOrBlank(databaseId) == false){
            DatabaseData databaseDataTemp = new DatabaseData();
            databaseDataTemp.setId(databaseId);
            this.databaseData = databaseDataTemp;
        }
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DRIVER_ID", updatable = false)
    public DriverData getDriverData() {
        return driverData;
    }

    public void setDriverData(DriverData driverData) {
        this.driverData = driverData;
    }

    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        if(databaseData != null) {
            jsonObject.addProperty("Database", databaseData.getName());
        }
        jsonObject.addProperty("Max Query Timeout Count",maxQueryTimeoutCount);
        jsonObject.addProperty("Batch Update",batchUpdate);
        jsonObject.addProperty("Batch Size",batchSize);
        jsonObject.addProperty("Batch Update Query Timeout",batchUpdateQueryTimeout);
        jsonObject.addProperty("Table Name",tableName);
        jsonObject.addProperty("Identity Field",identityField);
        jsonObject.addProperty("Sequence Name",sequenceName);
        jsonObject.addProperty("Store All CDR",storeAllCdr);
        jsonObject.addProperty("Time Stamp Field Name",timeStampFieldName);
        jsonObject.addProperty("Reporting Type",reportingType);
        jsonObject.addProperty("Session ID Field Name",sessionIdFieldName);
        jsonObject.addProperty("Create Date Field Name",createDateFieldName);
        jsonObject.addProperty("Last Modified Date Field Name",lastModifiedDateFieldName);
        jsonObject.addProperty("Reporting Type",reportingType);
        jsonObject.addProperty("Input Octets Field Name",inputOctetsFieldName);
        jsonObject.addProperty("Output Octets Field Name",outputOctetsFieldName);
        jsonObject.addProperty("Total Octets Field Name",totalOctetsFieldName);
        jsonObject.addProperty("Usage Time Field Name",usageTimeFieldName);
        jsonObject.addProperty("Usage Key Field Name",usageKeyFieldName);

        if(dbCdrDriverFieldMappingDataList != null) {
            JsonArray jsonArrayFieldMapping = new JsonArray();
            JsonObject jsonObjectFieldMapping = new JsonObject();
            for(DbCdrDriverFieldMappingData dbCdrDriverFieldMappingData : dbCdrDriverFieldMappingDataList) {
                if(dbCdrDriverFieldMappingData != null) {
                    jsonArrayFieldMapping.add(dbCdrDriverFieldMappingData.toJson());
                    jsonObjectFieldMapping.add(dbCdrDriverFieldMappingData.getPcrfKey(), jsonArrayFieldMapping);
                }
            }
            jsonObject.add("Field Mapping", jsonObjectFieldMapping);
        }

        return jsonObject;
    }
}
