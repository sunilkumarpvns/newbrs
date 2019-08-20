package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class LDAPDatasourceNameAdapter extends XmlAdapter<String, String> {

	private LDAPDatasourceBLManager ldapDataSourceBLManager = new LDAPDatasourceBLManager();
	@Override
	public String unmarshal(String databaseName) throws Exception {
		String databaseId = null;
		if(Strings.isNullOrEmpty(databaseName) == false){
			try {
				if(Strings.isNullOrBlank(databaseName) == false){
					databaseId = ldapDataSourceBLManager.getLdapDatasourceIdByName(databaseName);
					if(databaseId == null){
						return RestValidationMessages.INVALID;
					}
				} else {
					databaseId = RestValidationMessages.INVALID;
				}
			} catch(Exception e){
				databaseId = RestValidationMessages.INVALID ;
			}
		}
		return databaseId;
	}

	@Override
	public String marshal(String ldapDatabaseId) throws Exception {
		ILDAPDatasourceData ldapDatabase = null;
		try {
			ldapDatabase = ldapDataSourceBLManager.getLDAPDatabaseDataById(ldapDatabaseId);
			return ldapDatabase.getName();
		} catch(Exception e){
			return "";
		}
	}
}