package com.elitecore.corenetvertex.sm.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationFieldMapping")
@Table(name = "TBLM_LDAP_AUTH_CONF_FIELD_MAP")
public class LdapAuthConfigurationFieldMappingData implements Serializable {

    private String id;
    private String ldapAttribute;
    private String staffAttribute;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "LDAP_ATTRIBUTE")
    public String getLdapAttribute() {
        return ldapAttribute;
    }

    public void setLdapAttribute(String ldapAttribute) {
        this.ldapAttribute = ldapAttribute;
    }

    @Column(name = "STAFF_ATTRIBUTE")
    public String getStaffAttribute() {
        return staffAttribute;
    }

    public void setStaffAttribute(String staffAttribute) {
        this.staffAttribute = staffAttribute;
    }
}
