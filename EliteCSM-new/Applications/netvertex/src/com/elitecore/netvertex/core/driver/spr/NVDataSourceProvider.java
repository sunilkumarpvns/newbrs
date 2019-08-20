package com.elitecore.netvertex.core.driver.spr;

import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.netvertex.core.conf.DatabaseDSConfiguration;
import com.elitecore.netvertex.core.conf.LDAPDSConfiguration;

public class NVDataSourceProvider implements DataSourceProvider {

    private DatabaseDSConfiguration databaseDSConfiguration;
    private LDAPDSConfiguration ldapdsConfiguration;

    public NVDataSourceProvider(DatabaseDSConfiguration databaseDSConfiguration, LDAPDSConfiguration ldapdsConfiguration) {
        this.databaseDSConfiguration = databaseDSConfiguration;
        this.ldapdsConfiguration = ldapdsConfiguration;
    }

    @Override
    public DBDataSource getDBDataSource(DatabaseData databaseData, FailReason failReason) {
        return databaseDSConfiguration.getDatasource(databaseData.getId());
    }

    @Override
    public LDAPDataSource getLDAPDataSource(LdapData ldapData, FailReason failReason) {
        return ldapdsConfiguration.getDatasourceById(ldapData.getId());
    }
}
