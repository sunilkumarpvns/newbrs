package com.elitecore.corenetvertex.sm.spr;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Used to manage SPR Operation
 *@author dhyani.raval
 */

@Entity(name = "com.elitecore.corenetvertex.sm.spr.SprData")
@Table(name = "TBLM_SPR")
public class SprData extends ResourceData implements Serializable{

    private String name;
    private String description;
    private DatabaseData databaseData;
    private String alternateIdField;
    private SpInterfaceData spInterfaceData;
    private Integer batchSize;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DATABASE_ID")
    public DatabaseData getDatabaseData() {
        return databaseData;
    }

    public void setDatabaseData(DatabaseData databaseData) {
        this.databaseData = databaseData;
    }

    @Column(name = "ALTERNATE_ID_FIELD")
    public String getAlternateIdField() {
        return alternateIdField;
    }

    public void setAlternateIdField(String alternateIdField) {
        this.alternateIdField = alternateIdField;
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SP_INTERFACE_ID")
    public SpInterfaceData getSpInterfaceData() {
        return spInterfaceData;
    }

    public void setSpInterfaceData(SpInterfaceData spInterfaceData) {
        this.spInterfaceData = spInterfaceData;
    }

    @Column(name = "BATCH_SIZE")
    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
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
            DatabaseData databaseData = new DatabaseData();
            databaseData.setId(databaseId);
            this.databaseData = databaseData;
        }
    }

    @Transient
    public String getSpInterfaceId() {
        if(this.getSpInterfaceData()!=null){
            return getSpInterfaceData().getId();
        }
        return null;
    }

    public void setSpInterfaceId(String spInterfaceId) {
        if(Strings.isNullOrBlank(spInterfaceId) == false){
            SpInterfaceData spInterfaceData = new SpInterfaceData();
            spInterfaceData.setId(spInterfaceId);
            this.spInterfaceData = spInterfaceData;
        }
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION,description);
        jsonObject.addProperty(FieldValueConstants.DATABASE_DATASOURCE,getDatabaseId());
        jsonObject.addProperty("Database Name",databaseData != null ? databaseData.getName() : null);
        jsonObject.addProperty(FieldValueConstants.ALTERNATE_ID_FIELD,alternateIdField);
        jsonObject.addProperty(FieldValueConstants.SP_INTERFACE,getSpInterfaceId());
        jsonObject.addProperty("Sp Interface Name",spInterfaceData != null ? spInterfaceData.getName() : null);
        jsonObject.addProperty(FieldValueConstants.BATCH_SIZE,batchSize);
        return jsonObject;
    }

}
