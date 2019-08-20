package com.elitecore.corenetvertex.sm.spinterface;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.ldap.LdapData;
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
 * Used to manage LDAP Sp Interface Operation
 * @author dhyani.raval
 */
@Entity (name = "com.elitecore.corenetvertex.sm.spinterface.SpLdapInterfaceData")
@Table (name = "TBLM_LDAP_SP_INTERFACE")
public class LdapSpInterfaceData implements Serializable{

    private String id;
    private LdapData ldapData;
    private String expiryDatePattern;
    private Integer passwordDecryptType;
    private Integer maxQueryTimeoutCount = 1;
    private transient  SpInterfaceData spInterfaceData;
    private List<SpInterfaceFieldMappingData> spInterfaceFieldMappingDatas;
    private String ldapDataId;

    public LdapSpInterfaceData() {
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
    @JoinColumn(name = "LDAP_ID")
    @JsonIgnore
    public LdapData getLdapData() {
        return ldapData;
    }

    public void setLdapData(LdapData ldapData) {
        if(ldapData != null){
            setLdapDataId(ldapData.getId());
        }
        this.ldapData = ldapData;
    }

    @Column (name = "EXPIRYDATE_PATTERN")
    public String getExpiryDatePattern() {
        return expiryDatePattern;
    }

    public void setExpiryDatePattern(String expiryDatePattern) {
        this.expiryDatePattern = expiryDatePattern;
    }

    @Column (name = "PASSWORD_DECRYPT_TYPE")
    public Integer getPasswordDecryptType() {
        return passwordDecryptType;
    }

    public void setPasswordDecryptType(Integer passwordDecryptType) {
        this.passwordDecryptType = passwordDecryptType;
    }

    //TODO need to change the column name to MAX_QUERY_TIMEOUT_COUNT
    @Column (name = "QUERY_MAX_EXEC_TIME")
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
    public String getLdapDataId() {
      return ldapDataId;
    }

    public void setLdapDataId(String ldapDataId) {
        this.ldapDataId = ldapDataId;
    }

    public JsonObject toJson(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.LDAP_DATASOURCE ,ldapDataId);
        jsonObject.addProperty("LDAP Datasource Name" ,ldapData != null ? ldapData.getName() : "");
        jsonObject.addProperty(FieldValueConstants.EXPIRY_DATE_PATTERN,expiryDatePattern);
        jsonObject.addProperty(FieldValueConstants.PASSWORD_DECRYPT_TYPE,passwordDecryptType);
        jsonObject.addProperty(FieldValueConstants.MAX_QUERY_TIMEOUT_COUNT, maxQueryTimeoutCount);

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
