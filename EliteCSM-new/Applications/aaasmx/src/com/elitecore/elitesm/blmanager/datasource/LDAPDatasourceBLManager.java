package com.elitecore.elitesm.blmanager.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.ldap.LDAPDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;


public class LDAPDatasourceBLManager extends BaseBLManager{

	public LDAPDSDataManager getLDAPDSDataManager(IDataManagerSession session) {
		LDAPDSDataManager databaseDSDataManager = (LDAPDSDataManager) DataManagerFactory.getInstance().getDataManager(LDAPDSDataManager.class, session);
		return databaseDSDataManager; 
	}
	
	public void create(LDAPDatasourceData ldapDatasourceData,IStaffData staffData) throws DataManagerException {
		List<LDAPDatasourceData> ldapDatasourceDataList = new ArrayList<LDAPDatasourceData>();
		ldapDatasourceDataList.add(ldapDatasourceData);
		create(ldapDatasourceDataList, staffData, "");
	}
	
	public Map<String, List<Status>> create(List<LDAPDatasourceData> ldapDatasourceDataList,IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(LDAPDSDataManager.class, ldapDatasourceDataList, staffData, ConfigConstant.CREATE_LDAP_DATASOURCE, partialSuccess);
	}

	public PageList search(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,int pageNo, Integer pageSize) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

			if(ldapDSDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			
			PageList pageList = ldapDSDataManager.search(ldapDatasourceData, pageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_LDAP_DATASOURCE);
			
			session.commit();
			return pageList;

		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException("Failed to search LDAP Datasource, Reason:"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public List<LDAPBaseDnDetailData> getLDAPBaseBnDetailByLdapId(String ldapId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			List<LDAPBaseDnDetailData> listOfBaseDnDetail = ldapDSDataManager.getLdapBaseDnDetailDataByLdapId(ldapId);
			return listOfBaseDnDetail;
		
		} catch (DataManagerException dme) {
			throw dme;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateLDAPDatasourceDataById(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData) throws DataManagerException{
		updateLDAPDatasourceDataByName(ldapDatasourceData, staffData, null);
	}

	public void updateLDAPDatasourceDataByName(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String queryOrPathParam) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager dataManager = getLDAPDSDataManager(session);

		if (dataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		try{
			session.beginTransaction();
			
			if(queryOrPathParam == null){
				dataManager.updateById(ldapDatasourceData, staffData, ldapDatasourceData.getLdapDsId());
			}else{
				dataManager.updateByName(ldapDatasourceData, staffData, queryOrPathParam);
			}
			commit(session);
		} catch(DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch(Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public void deleteByName(List<String> digestConfs, IStaffData staffData) throws DataManagerException{
		delete(digestConfs, staffData, BY_NAME);
	}

	public void deleteById(List<String> digestConfs, IStaffData staffData) throws DataManagerException{
		delete(digestConfs, staffData, BY_ID);
	}

	private void delete(List<String> ldapDSIdOrName,IStaffData staffData,boolean byIdOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager dataManager = getLDAPDSDataManager(session);

		if (dataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		String ldapDatasourceName = "";
		
		try{
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(ldapDSIdOrName) == false) {
				int size = ldapDSIdOrName.size();
				for (int i=0;i<size;i++) {
					if(Strings.isNullOrBlank(ldapDSIdOrName.get(i)) == false){
						String idOrName = ldapDSIdOrName.get(i).trim();
						if ( byIdOrName ) {
							ldapDatasourceName = dataManager.deleteById(idOrName);
						} else {
							ldapDatasourceName = dataManager.deleteByName(idOrName);			
						}
						staffData.setAuditName(ldapDatasourceName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_LDAP_DATASOURCE);
					}
				}
				commit(session);
			}
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch(Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public List<LDAPDatasourceData> getListOfLDAP() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			List<LDAPDatasourceData> data = ldapDSDataManager.getListOfLDAP();				
			session.commit();
			return data;
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Failed to search LDAP Datasource. Reason :",e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public ILDAPDatasourceData getLDAPDatabaseDataById(String digestConfigId) throws DataManagerException{
		return getLDAPDatabaseData(digestConfigId,BY_ID);
	}

	public ILDAPDatasourceData getLDAPDatabaseDataByName(String digestConfigName) throws DataManagerException{
		return getLDAPDatabaseData(digestConfigName,BY_NAME);
	}

	private ILDAPDatasourceData getLDAPDatabaseData(Object ldapDSIdOrName,boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager dataManager = getLDAPDSDataManager(session);
		
		if (dataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		LDAPDatasourceData ldapDatasourceData = null;
		try{
			if( byIdOrName ){
				ldapDatasourceData = dataManager.getLDAPDatasourceDataById((String)(ldapDSIdOrName)); 
			}else{
				ldapDatasourceData = dataManager.getLDAPDatasourceDataByName((String)ldapDSIdOrName); 
			}
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return ldapDatasourceData;
	}

	public String encryptPassword(String password) throws NoSuchEncryptionException, EncryptionFailedException{
		String encryptedPassword = PasswordEncryption.getInstance().crypt(password,PasswordEncryption.ELITE_PASSWORD_CRYPT);
		return encryptedPassword;
	}
	
	public String getLdapDatasourceIdByName(String ldapDataSourceName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager dataManager = getLDAPDSDataManager(session);
		
		if (dataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		String ldapDataSourceId = null;
		try {
			ldapDataSourceId = dataManager.getDatabaseIdFromName(ldapDataSourceName);
		} catch(DataManagerException e){
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
		return ldapDataSourceId;
		
	}
}	
