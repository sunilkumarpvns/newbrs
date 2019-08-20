package com.elitecore.netvertexsm.datamanager.datasource.ldap;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;


public interface LDAPDSDataManager extends DataManager{
	
	  public void create(ILDAPDatasourceData ldapDatasourceData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	  public List getLDAPDatasourceDataByName(String name);	
	  public List search (ILDAPDatasourceData ldapDatasourceData);
	  public List getLDAPDSList() ;
	  public LDAPDatasourceData getLdapData(long ldapDsId) throws DataManagerException;
	  public List getLdapBaseDnDetailDataByLdapId (long ldapDsId) throws DataManagerException;
	  public void update(ILDAPDatasourceData ldapDatasourceData,long ldapDsId) throws DataManagerException;
	  public void delete(List ldapDsIds) throws DataManagerException;
}
