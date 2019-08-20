package com.elitecore.netvertexsm.blmanager.datasource;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ReferenceFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.LDAPDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;


public class LDAPDatasourceBLManager extends BaseBLManager{

	private String MODULE="LDAP";

	public LDAPDSDataManager getLDAPDSDataManager(IDataManagerSession session) {
		LDAPDSDataManager databaseDSDataManager = (LDAPDSDataManager) DataManagerFactory.getInstance().getDataManager(LDAPDSDataManager.class, session);
		return databaseDSDataManager; 
	}

	public void create(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String actionAlias) throws DataManagerException{		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);		

		if (ldapDSDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			ldapDSDataManager.create(ldapDatasourceData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}					
	}

	public List search(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);
		
		List listOfSearchData = new ArrayList();

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			listOfSearchData = ldapDSDataManager.search(ldapDatasourceData);
			return listOfSearchData;
		}catch(Exception e){
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}						
	}

	public LDAPDatasourceData getLDAPData(long name) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			LDAPDatasourceData ldapData = ldapDSDataManager.getLdapData(name);
			return ldapData;
		}catch(Exception de){
			throw new DataManagerException("Action Failed .",de.getMessage());
		}finally{
			session.close();
		}

	}

	public List getLDAPBaseBnDetailByLdapId(long ldapId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			List listOfBaseDnDetail = ldapDSDataManager.getLdapBaseDnDetailDataByLdapId(ldapId);
			return listOfBaseDnDetail ;
		}catch(Exception dme){
			throw new DataManagerException("Action Failed .",dme.getMessage());
		}finally{
			session.close();
		}			
	}
	public void update(ILDAPDatasourceData ldapDatasourceData , long ldapDsId,IStaffData staffData,String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if (ldapDSDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			ldapDSDataManager.update(ldapDatasourceData, ldapDsId);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed .",e.getMessage());
		}finally{
			session.close();
		}
	}
	
	public void delete(List ldapDsIds,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if (ldapDSDataManager == null && systemAuditDataManager == null	)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			ldapDSDataManager.delete(ldapDsIds);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(ReferenceFoundException rf){
			session.rollback();
			throw rf;
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed .",e.getMessage());
		}finally{
			session.close();
		}
	}

	public List<LDAPDatasourceData> getLDAPDSList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		LDAPDSDataManager ldapDSDataManager = getLDAPDSDataManager(session);

		if (ldapDSDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			List<LDAPDatasourceData> data = ldapDSDataManager.getLDAPDSList();				
			return data;
		}catch(Exception e){
			throw new DataManagerException("Action Failed .",e.getMessage());
		}finally{
			session.close();
		}	
	}

	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
}	