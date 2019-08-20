package com.elitecore.corenetvertex.ldap;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;
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
 * Used to manage LDAP Datasource Base DN related information with DB
 * Created by dhyani on 23/8/17.
 */
@Entity
@Table(name = "TBLM_LDAP_BASE_DN_DETAIL")
public class LdapBaseDn implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    @SerializedName(FieldValueConstants.SEARCH_BASE_DN)private String searchBaseDn;
    transient private LdapData ldapData;

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "SEARCH_BASE_DN")
    public String getSearchBaseDn() {
        return searchBaseDn;
    }

    public void setSearchBaseDn(String searchBaseDn) {
        this.searchBaseDn = searchBaseDn;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "LDAP_DS_ID")
    @JsonIgnore
    public LdapData getLdapData() {
        return ldapData;
    }

    public void setLdapData(LdapData ldapData) {
        this.ldapData = ldapData;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.SEARCH_BASE_DN, searchBaseDn);
        return jsonObject;
    }
}
