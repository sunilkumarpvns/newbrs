package com.elitecore.corenetvertex.sm.spinterface;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.spr.data.SPRFields;
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
 * Used to manage DB Sp Interface Operation
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.spinterface.SpDbInterfaceData")
@Table(name = "TBLM_DB_SP_INTERFACE")
public class DbSpInterfaceData implements Serializable {

    private String id;
    private transient DatabaseData databaseData;
    private String tableName;
    private String identityField;
    private Integer maxQueryTimeoutCount;
    private transient SpInterfaceData spInterfaceData;
    private List<SpInterfaceFieldMappingData> spInterfaceFieldMappingDatas;
    private String databaseId;

    public DbSpInterfaceData() {
        spInterfaceFieldMappingDatas = Collectionz.newArrayList();
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DATABASE_ID")
    @JsonIgnore
    public DatabaseData getDatabaseData() {
        return databaseData;
    }

    public void setDatabaseData(DatabaseData databaseData) {
        if(databaseData != null){
            setDatabaseId(databaseData.getId());
        }
        this.databaseData = databaseData;
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

    @Column (name = "MAX_QUERY_TIMEOUT_COUNT")
    public Integer getMaxQueryTimeoutCount() {
        return maxQueryTimeoutCount;
    }

    public void setMaxQueryTimeoutCount(Integer maxQueryTimeoutCount) {
        this.maxQueryTimeoutCount = maxQueryTimeoutCount;
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SP_INTERFACE_ID", updatable = false)
    public SpInterfaceData getSpInterfaceData() {
        return spInterfaceData;
    }

    public void setSpInterfaceData(SpInterfaceData spInterfaceData) {
        this.spInterfaceData = spInterfaceData;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "SP_INTERFACE_TYPE_ID" )
    public List<SpInterfaceFieldMappingData> getSpInterfaceFieldMappingDatas() {
        return spInterfaceFieldMappingDatas;
    }

    public void setSpInterfaceFieldMappingDatas(List<SpInterfaceFieldMappingData> spInterfaceFieldMappingDatas) {
        this.spInterfaceFieldMappingDatas = spInterfaceFieldMappingDatas;
    }

    @Transient
    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public JsonObject toJson(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.DATABASE_DATASOURCE ,databaseId);
        jsonObject.addProperty("Database Datasource Name: " , databaseData != null ? databaseData.getName() : "");
        jsonObject.addProperty(FieldValueConstants.TABLE_NAME,tableName);
        jsonObject.addProperty(FieldValueConstants.IDENTITY_FIELD,identityField);
        jsonObject.addProperty(FieldValueConstants.MAX_QUERY_TIMEOUT_COUNT,maxQueryTimeoutCount);

        if(spInterfaceFieldMappingDatas != null){
            JsonObject jsonArray = new JsonObject();
            for(SpInterfaceFieldMappingData spInterfaceFieldMappingData : spInterfaceFieldMappingDatas){
                if(spInterfaceFieldMappingData != null)
                    jsonArray.addProperty(SPRFields.fromSPRFields(spInterfaceFieldMappingData.getLogicalName()).displayName,spInterfaceFieldMappingData.getFieldName());
            }
            jsonObject.add("Field Mapping", jsonArray);
        }
        return jsonObject;

    }
}
