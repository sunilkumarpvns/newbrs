package com.elitecore.corenetvertex.sm.spinterface;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Used to manage Sp Interface Operations
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData")
@Table(name = "TBLM_SP_INTERFACE")
public class SpInterfaceData extends DefaultGroupResourceData implements Serializable {

    private String name;
    private String description;
    private String spInterfaceType;
    private DbSpInterfaceData dbSpInterfaceData;
    private LdapSpInterfaceData ldapSpInterfaceData;

    public SpInterfaceData(){
        dbSpInterfaceData = new DbSpInterfaceData();
        ldapSpInterfaceData = new LdapSpInterfaceData();
    }

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

    @Column(name = "SP_INTERFACE_TYPE")
    public String getSpInterfaceType() {
        return spInterfaceType;
    }

    public void setSpInterfaceType(String spInterfaceType) {
        this.spInterfaceType = spInterfaceType;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "spInterfaceData")
    @Fetch(FetchMode.SELECT)
    public DbSpInterfaceData getDbSpInterfaceData() {
        return dbSpInterfaceData;
    }

    public void setDbSpInterfaceData(DbSpInterfaceData dbSpInterfaceData) {
        this.dbSpInterfaceData = dbSpInterfaceData;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "spInterfaceData")
    @Fetch(FetchMode.SELECT)
    public LdapSpInterfaceData getLdapSpInterfaceData() {
        return ldapSpInterfaceData;
    }

    public void setLdapSpInterfaceData(LdapSpInterfaceData ldapSpInterfaceData) {
        this.ldapSpInterfaceData = ldapSpInterfaceData;
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.SP_INTERFACE_TYPE, spInterfaceType != null? SpInterfaceType.fetchFromName(spInterfaceType) : "" );
        if(SpInterfaceType.DB_SP_INTERFACE.name().equalsIgnoreCase(spInterfaceType)) {
            if (dbSpInterfaceData != null) {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(dbSpInterfaceData.toJson());
                jsonObject.add("DB Sp Interface", jsonArray);
            }
        }
        if(SpInterfaceType.LDAP_SP_INTERFACE.name().equalsIgnoreCase(spInterfaceType)) {
            if (ldapSpInterfaceData != null) {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(ldapSpInterfaceData.toJson());
                jsonObject.add("LDAP Sp Interface", jsonArray);
            }
        }
        return jsonObject;
    }

}
