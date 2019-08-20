package com.elitecore.corenetvertex.sm.session;

import com.elitecore.corenetvertex.constants.FieldMappingDataType;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * This class is used to manipulate field mapping for Session Configuration
 * @author dhyani.raval
 */
@Entity
@Table(name = "TBLM_SESSION_FIELD_MAPPING")
public class SessionConfigurationFieldMappingData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    @SerializedName(FieldValueConstants.REFERRING_ATTRIBUTE)private String referringAttribute;
    @SerializedName(FieldValueConstants.FIELD_NAME)private String fieldName ;
    @JsonAdapter(SessionDataTypeGsonAdaptor.class)
    @SerializedName(FieldValueConstants.DATA_TYPE)private Integer dataType;
    transient private SessionConfigurationData sessionConfigurationData;

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "REFERRING_ATTR")
    public String getReferringAttribute() {
        return referringAttribute;
    }

    public void setReferringAttribute(String referringAttribute) {
        this.referringAttribute = referringAttribute;
    }

    @Column(name = "FIELD_NAME")
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Column(name = "DATA_TYPE")
    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SESSION_CONF_ID")
    @JsonIgnore
    public SessionConfigurationData getSessionConfigurationData() {
        return sessionConfigurationData;
    }

    public void setSessionConfigurationData(SessionConfigurationData sessionConfigurationData) {
        this.sessionConfigurationData = sessionConfigurationData;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.FIELD_NAME, fieldName);
        jsonObject.addProperty(FieldValueConstants.REFERRING_ATTRIBUTE, referringAttribute);
        jsonObject.addProperty(FieldValueConstants.DATA_TYPE, FieldMappingDataType.fetchDisplayValueFromValue(dataType));
        return jsonObject;
    }

}
