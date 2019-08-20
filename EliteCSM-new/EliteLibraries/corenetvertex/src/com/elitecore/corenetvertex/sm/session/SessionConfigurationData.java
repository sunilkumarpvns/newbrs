package com.elitecore.corenetvertex.sm.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.BatchUpdateMode;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * This class is used to manipulate session configuration information
 * @author dhyani.raval
 */
@Entity
@Table(name = "TBLM_SESSION_CONFIGURATION")
public class SessionConfigurationData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient DatabaseData databaseData;
    @SerializedName(FieldValueConstants.BATCH_MODE)private Integer batchMode = BatchUpdateMode.FALSE.getValue();
    @SerializedName(FieldValueConstants.BATCH_SIZE)private Integer batchSize = 100;
    @SerializedName(FieldValueConstants.QUERY_TIMEOUT)private Integer queryTimeout = 10;
    private List<SessionConfigurationFieldMappingData> sessionConfigurationFieldMappingDatas;
    private @SerializedName(FieldValueConstants.DATABASE_DATASOURCE) String databaseId;

    public SessionConfigurationData(){
        sessionConfigurationFieldMappingDatas = Collectionz.newArrayList();
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="DATABASE_ID")
    public DatabaseData getDatabaseData() {
        return databaseData;
    }

    public void setDatabaseData(DatabaseData databaseData) {
        if(databaseData != null){
            setDatabaseId(databaseData.getId());
        }
        this.databaseData = databaseData;
    }

    @Column(name = "BATCH_UPDATE" )
    public Integer getBatchMode() {
        return batchMode;
    }

    public void setBatchMode(Integer batchMode) {
        this.batchMode = batchMode;
    }

    @Column(name = "BATCH_SIZE")
    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }


    @Column(name = "QUERY_TIMEOUT")
    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "sessionConfigurationData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    public List<SessionConfigurationFieldMappingData> getSessionConfigurationFieldMappingDatas() {
        return sessionConfigurationFieldMappingDatas;
    }

    public void setSessionConfigurationFieldMappingDatas(List<SessionConfigurationFieldMappingData> sessionConfigurationFieldMappingDatas) {
        this.sessionConfigurationFieldMappingDatas = sessionConfigurationFieldMappingDatas;
    }

    @Transient
    @Override
    public String getResourceName() {
        return "session";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.DATABASE_DATASOURCE,databaseId);
        jsonObject.addProperty("Database Name",databaseData != null ? databaseData.getName() : null);
        jsonObject.addProperty(FieldValueConstants.BATCH_MODE, BatchUpdateMode.fromValue(batchMode).getDisplayValue());
        jsonObject.addProperty(FieldValueConstants.BATCH_SIZE, batchSize);
        jsonObject.addProperty(FieldValueConstants.QUERY_TIMEOUT, queryTimeout);


        if(sessionConfigurationFieldMappingDatas != null){
            JsonArray jsonArray = new JsonArray();
            for(SessionConfigurationFieldMappingData sessionConfigurationFieldMappingData : sessionConfigurationFieldMappingDatas){
                jsonArray.add(sessionConfigurationFieldMappingData.toJson());
            }
            jsonObject.add("Field Mapping", jsonArray);
        }
        return jsonObject;
    }

    @Transient
    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
           this.databaseId = databaseId;

    }

}
