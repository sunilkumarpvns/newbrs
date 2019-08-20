package com.elitecore.corenetvertex.ldap;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * Used to manage LDAP Datasource related information with DB
 * Created by dhyani on 23/8/17.
 */
@Entity
@Table(name = "TBLM_LDAP_DS")
public class LdapData extends ResourceData implements Serializable{

    private String name;
    private String address;
    private Integer queryTimeout = 1000;
    private Long sizeLimit;
    private String administrator;
    private transient String password;
    private String userDnPrefix = "uid=";
    private Integer maximumPool = 1;
    private Integer minimumPool = 1;
    private Integer statusCheckDuration = 120;
    private List<LdapBaseDn> ldapBaseDns;
    private Integer version;

    public LdapData() {
        ldapBaseDns = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ADDRESS")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "QUERY_TIMEOUT")
    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @Column(name = "SIZE_LIMIT")
    public Long getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(Long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    @Column(name = "ADMINISTRATOR")
    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    @JsonIgnore
    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "USER_DN_PREFIX")
    public String getUserDnPrefix() {
        return userDnPrefix;
    }

    public void setUserDnPrefix(String userDnPrefix) {
        this.userDnPrefix = userDnPrefix;
    }

    @Column(name = "MINIMUM_POOL")
    public Integer getMaximumPool() {
        return maximumPool;
    }

    public void setMaximumPool(Integer maximumPool) {
        this.maximumPool = maximumPool;
    }

    @Column(name = "MAXIMUM_POOL")
    public Integer getMinimumPool() {
        return minimumPool;
    }

    public void setMinimumPool(Integer minimumPool) {
        this.minimumPool = minimumPool;
    }

    @Column(name = "STATUS_CHECK_DURATION")
    public Integer getStatusCheckDuration() {
        return statusCheckDuration;
    }

    public void setStatusCheckDuration(Integer statusCheckDuration) {
        this.statusCheckDuration = statusCheckDuration;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "ldapData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    public List<LdapBaseDn> getLdapBaseDns() {
        return ldapBaseDns;
    }

    public void setLdapBaseDns(List<LdapBaseDn> ldapBaseDns) {
        this.ldapBaseDns = ldapBaseDns;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Transient
    @XmlTransient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Column(name = "VERSION")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.ADDRESS, address);
        jsonObject.addProperty(FieldValueConstants.QUERY_TIMEOUT, queryTimeout);
        jsonObject.addProperty(FieldValueConstants.SIZE_LIMIT, sizeLimit);
        jsonObject.addProperty(FieldValueConstants.ADMINISTRATOR, administrator);
        jsonObject.addProperty(FieldValueConstants.USER_DN_PREFIX, userDnPrefix);
        jsonObject.addProperty(FieldValueConstants.MINIMUM_POOL, minimumPool);
        jsonObject.addProperty(FieldValueConstants.MAXIMUM_POOL, maximumPool);
        jsonObject.addProperty(FieldValueConstants.STATUS_CHECK_DURATION, statusCheckDuration);
        if(version != null) {
            jsonObject.addProperty("Version", LdapVersion.fromVersion(version).name());
        }
        if(ldapBaseDns != null){
            JsonArray jsonArray = new JsonArray();
            for(LdapBaseDn ldapBaseDn : ldapBaseDns){
                jsonArray.add(ldapBaseDn.toJson());
            }
            jsonObject.add("Base DNs", jsonArray);
        }
        return jsonObject;
    }
}
