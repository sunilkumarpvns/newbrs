package com.elitecore.corenetvertex.sm.driver.dbcdr;

import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Contains field mapping information of DB CDR Driver Management
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverFieldMappingData")
@Table(name = "TBLM_DB_CDR_DRIVER_FIELD_MAP")
public class DbCdrDriverFieldMappingData implements Serializable {

    private String id;
    private String pcrfKey;
    private String dbField;
    private String dataType;
    private String defaultValue;

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "PCRFKEY")
    public String getPcrfKey() {
        return pcrfKey;
    }

    public void setPcrfKey(String pcrfKey) {
        this.pcrfKey = pcrfKey;
    }

    @Column(name = "DB_FIELD")
    public String getDbField() {
        return dbField;
    }

    public void setDbField(String dbField) {
        this.dbField = dbField;
    }

    @Column(name = "DATA_TYPE")
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Column(name = "DEFAULT_VALUE")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public JsonObject toJson() {
        JsonObject jsonObject =  new JsonObject();
        jsonObject.addProperty("PCRF Key",pcrfKey);
        jsonObject.addProperty("DB Field",dbField);
        jsonObject.addProperty("Data Type",dataType);
        jsonObject.addProperty("Default Value",defaultValue);
        return jsonObject;
    }
}
