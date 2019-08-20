package com.elitecore.corenetvertex.spr;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;


public interface LDAPConnectionProvider {
    
    public LDAPConnection getConnection() throws LDAPException;
    public void close(LDAPConnection connection);

}
