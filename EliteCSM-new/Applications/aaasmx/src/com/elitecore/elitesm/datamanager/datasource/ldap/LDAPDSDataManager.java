package com.elitecore.elitesm.datamanager.datasource.ldap;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;


public interface LDAPDSDataManager extends DataManager{
	  
	  @Override
	  public String create(Object object) throws DataManagerException;
	  public PageList search (ILDAPDatasourceData ldapDatasourceData,int pageNo,int pageSize) throws DataManagerException;
	  public List getListOfLDAP() ;
	  public List<LDAPBaseDnDetailData> getLdapBaseDnDetailDataByLdapId (String ldapDsId) throws DataManagerException;
	  public List getList(ILDAPDatasourceData ldapDataSourceData) throws DataManagerException;
	  public ILDAPDatasourceData getLDAPDSByName(String databaseName) throws DataManagerException;
	  public LDAPDatasourceData getLDAPDatasourceDataById(String ldapDSId) throws DataManagerException;
	  public LDAPDatasourceData getLDAPDatasourceDataByName(String ldapDSName) throws DataManagerException;
	  public String deleteById(String ldapDSId) throws DataManagerException;
	  public String deleteByName(String ldapDSName) throws DataManagerException;
	  public void updateById(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String ldapDSId) throws DataManagerException;
	  public void updateByName(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String queryOrPathParam) throws DataManagerException;
  	  public String getDatabaseIdFromName(String ldapDataSourceName) throws DataManagerException;
}
