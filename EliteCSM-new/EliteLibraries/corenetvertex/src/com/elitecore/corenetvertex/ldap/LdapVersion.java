package com.elitecore.corenetvertex.ldap;


/**
 * Enum defines the LDAP versions
 * @author dhyani.raval
 */
public enum LdapVersion {

    LDAP_V2(2),
    LDAP_V3(3),
    ;

    private Integer version;

    LdapVersion (Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public static LdapVersion fromVersion(Integer version) {
        for(LdapVersion ldapVersion : values()) {
            if(ldapVersion.version == version) {
                return ldapVersion;
            }
        }
        return null;
    }
}
