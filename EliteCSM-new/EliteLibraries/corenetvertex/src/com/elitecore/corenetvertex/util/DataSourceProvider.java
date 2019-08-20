package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.ldap.LdapData;

public interface DataSourceProvider {
    DBDataSource getDBDataSource(DatabaseData databaseData, FailReason failReason);
    LDAPDataSource getLDAPDataSource(LdapData ldapData, FailReason failReason);
}
