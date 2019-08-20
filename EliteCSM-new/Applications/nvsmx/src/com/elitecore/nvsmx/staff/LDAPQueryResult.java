package com.elitecore.nvsmx.staff;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author dhyani.raval
 */
public class LDAPQueryResult implements Serializable {

    public static final long serialVersionUID = 1L;

    private Map<String,String[]> queryResults = new HashMap<>();

    public LDAPQueryResult (Map<String,String[]> queryResults) {
        this.queryResults = queryResults;
    }

    public String[] getValue(String key) {
        return queryResults.get(key);
    }

    public Set<String> getKey() {
        return queryResults.keySet();
    }



    public static Map<String,String[]> createAttributes(LDAPSearchResults ldapSearchResults) throws LDAPException {

        Map<String,String[]> queryResults = new HashMap<>(10);
        if (ldapSearchResults.hasMoreElements()) {
            LDAPEntry ldapEntry = ldapSearchResults.next();
            LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
            Enumeration enumeration = attributeSet.getAttributes();
            while (enumeration.hasMoreElements()) {
                LDAPAttribute ldapAttribute = (LDAPAttribute) enumeration.nextElement();
                queryResults.put(ldapAttribute.getName(),ldapAttribute.getStringValueArray());
            }
        }
        return queryResults;
    }
}
