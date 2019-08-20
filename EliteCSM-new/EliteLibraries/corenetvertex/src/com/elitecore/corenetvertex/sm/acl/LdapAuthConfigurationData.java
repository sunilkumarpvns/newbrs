package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Entity(name = "com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationData")
@Table(name = "TBLM_LDAP_AUTH_CONF")
public class LdapAuthConfigurationData extends DefaultGroupResourceData implements Serializable {

    private LdapData ldapData;
    private String datePattern;
    private List<LdapAuthConfigurationFieldMappingData> ldapAuthConfigurationFieldMappingDataList;

    public LdapAuthConfigurationData(){
        this.ldapData = new LdapData();
        this.ldapAuthConfigurationFieldMappingDataList = Collectionz.newArrayList();
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LDAP_ID")
    @JsonIgnore
    public LdapData getLdapData() {
        return ldapData;
    }

    public void setLdapData(LdapData ldapData) {
        this.ldapData = ldapData;
    }

    @Column(name = "DATE_PATTERN")
    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "LDAP_AUTH_CONF_ID" )
    public List<LdapAuthConfigurationFieldMappingData> getLdapAuthConfigurationFieldMappingDataList() {
        return ldapAuthConfigurationFieldMappingDataList;
    }

    public void setLdapAuthConfigurationFieldMappingDataList(List<LdapAuthConfigurationFieldMappingData> ldapAuthConfigurationFieldMappingDataList) {
        this.ldapAuthConfigurationFieldMappingDataList = ldapAuthConfigurationFieldMappingDataList;
    }

    @Transient
    public String getLdapDataId() {
        if(this.getLdapData()!= null){
            return getLdapData().getId();
        }
        return null;
    }

    public void setLdapDataId(String ldapDataId) {
        if(Strings.isNullOrBlank(ldapDataId) == false){
            LdapData ldapDataTemp = new LdapData();
            ldapDataTemp.setId(ldapDataId);
            this.ldapData = ldapDataTemp;
        }
    }

    @Transient
    @Override
    public String getResourceName() {
        return "LdapAuthConfiguration";
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject();
    }

}
