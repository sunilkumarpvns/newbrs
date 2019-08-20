package com.elitecore.elitesm.blmanager.servermgr.eap;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.EAPConfigDataManager;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IEAPConfigData;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;

public class EAPConfigBLManager extends BaseBLManager {

	private String MODULE=" EAP CONFIG DATA MANAGER ";

	public EAPConfigDataManager getEAPConfigDataManager(IDataManagerSession session) { 
		EAPConfigDataManager  eapConfigDataManager   = (EAPConfigDataManager) DataManagerFactory.getInstance().getDataManager(EAPConfigDataManager.class, session);
		return eapConfigDataManager;
	}

	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}

	/**
	 * This creates multiple EAP Configurations
	 * @param eapConfigDatas list of EAP configuration
	 * @param staffData user information who make create operation
	 * @return 
	 * @throws DataManagerException
	 */
	public Map<String, List<Status>> create(List<EAPConfigData> eapConfigDatas, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(EAPConfigDataManager.class, eapConfigDatas, staffData, ConfigConstant.CREATE_EAP_CONFIGURATION, partialSuccess);
	}
	
	public void create(EAPConfigData eapConfigData, IStaffData staffData) throws DataManagerException,DuplicateInstanceNameFoundException {

		List<EAPConfigData> eapConfigDatas = new ArrayList<EAPConfigData>();
		eapConfigDatas.add(eapConfigData);
		create(eapConfigDatas, staffData, "false");
	}

	public void defaultEAPConfigCreate(IEAPConfigData eapConfigData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateInstanceNameFoundException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for EAPConfigDataManager");
		}

		try{
			session.beginTransaction();
			eapConfigDataManager.defaultEAPConfigCreate(eapConfigData);
			
			//Auditing parameters
			staffData.setAuditName(eapConfigData.getName());
			AuditUtility.doAuditing(session,staffData,ConfigConstant.CREATE_EAP_CONFIGURATION);
			commit(session);

		} catch(DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch(Exception exp) {
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}

	public List<EAPConfigData> getEapConfigurationList() throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		List<EAPConfigData> eapConfigList=null;
		
		if (eapConfigDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			eapConfigList= eapConfigDataManager.getEAPConfigList();

		} catch(DataManagerException exp){
			throw exp;
		} catch(Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

		return eapConfigList;
	}

	/**
	 * Perform delete operation by list of EAP Configuration names
	 * @param eapConfigNames list of EAP Configuration names
	 * @param staffData user information who perform this operation
	 * @throws DataManagerException
	 */
	public void deleteByName(List<String> eapConfigNames, IStaffData staffData) throws DataManagerException{
		delete(eapConfigNames, staffData, BY_NAME);
	}
	
	/**
	 * Perform delete operation by list of EAP Configuration ids
	 * @param eapConfigIds list of EAP Configuration ids
	 * @param staffData user information who perform this operation
	 * @throws DataManagerException
	 */
	public void deleteById(List<String> eapConfigIds, IStaffData staffData) throws DataManagerException{
		delete(eapConfigIds, staffData, BY_ID);
	}
	
	/**
	 * Perform delete operation by list of EAP Configuration ids or names
	 * @param eapConfigIdOrNames list of EAP Configuration ids or names
	 * @param staffData user information who perform this operation
	 * @param isEapIdOrName specify whether delete by id or name
	 * @throws DataManagerException
	 */
	private void delete(List<String> eapConfigIdOrNames, IStaffData staffData,boolean isEapIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);

		if (eapConfigDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{

			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(eapConfigIdOrNames) == false) {
				for(String eapConfigNameOrId : eapConfigIdOrNames) {
					if(Strings.isNullOrBlank(eapConfigNameOrId) == false) {
						String name = null ;
						if(isEapIdOrName) {
							name = eapConfigDataManager.deleteById(eapConfigNameOrId);
						} else {
							name = eapConfigDataManager.deleteByName(eapConfigNameOrId.trim());
						}

						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_EAP_CONFIGURATION);
					}
				}
			}
			commit(session);
		}catch(DataManagerException de) {
			rollbackSession(session);
			throw de;
		}catch(Exception exp){
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally {
			closeSession(session);
		}
	}

	public PageList search(EAPConfigData eapConfigData,String[] enableAuthMethodarry, int requiredPageNo, Integer pageSize) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);

		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
			
		PageList pageList = null;
		try {
			pageList = eapConfigDataManager.search(eapConfigData,enableAuthMethodarry,requiredPageNo,pageSize); 
		} catch(DataManagerException exp){
			Logger.logError(MODULE,"Failed to retrive list of EAP Configuration during pagging, Reason: " + exp.getMessage());
			throw exp;
		} catch(Exception exp) {
			Logger.logError(MODULE,"Failed to retrive list of EAP Configuration during pagging, Reason: " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		}finally {
			closeSession(session);
		}
		return pageList;
	}

	/**
	 * This is used to get EAP configuration by it's name
	 * @param eapConfigName name of EAP configuration
	 * @return EAPConfigData
	 * @throws DataManagerException
	 */
	public EAPConfigData getEapConfigurationDataByName(String eapConfigName) throws DataManagerException{
		return getEapConfigurationData(eapConfigName.trim(),BY_NAME);
	}
	
	/**
	 * This is used to get EAP configuration by it's id
	 * @param eapConfigId id of EAP configuration
	 * @return EAPConfigData
	 * @throws DataManagerException
	 */
	public EAPConfigData getEapConfigurationDataById(String eapConfigId) throws DataManagerException {
		return getEapConfigurationData(eapConfigId,BY_ID);	
	}
	
	/**
	 * this provides EAP Configuration data base on either name and id
	 * @param eapConfigIdOrName value of eap id or eap name
	 * @param byIdOrName specifies make search based on name or id
	 * @return EAPConfigData
	 * @throws DataManagerException
	 */
	private EAPConfigData getEapConfigurationData(Object eapConfigIdOrName, boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		EAPConfigData configData = null;
		
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			if (byIdOrName) {
				configData = eapConfigDataManager.getEAPConfigDataById((String)eapConfigIdOrName);
			} else {
				configData = eapConfigDataManager.getEAPConfigDataByName((String)eapConfigIdOrName);
			}

		} catch(DataManagerException exp) {
			Logger.logError(MODULE, "Failed to retrive EAP Configuration : " + exp.getMessage());
			throw exp;
		} catch(Exception exp) {
			exp.printStackTrace();
			Logger.logError(MODULE, "Failed to retrive EAP Configuration : " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		
		return configData;
	}
	

	public void updateEapBasicDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException,DuplicateInstanceNameFoundException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		
		if (eapConfigDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			eapConfigData.setLastModifiedByStaffId(staffData.getStaffId());
			eapConfigDataManager.updateEapBasicDetails(eapConfigData,staffData,actionAlias);
			commit(session);

		} catch(DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch(Exception exp) {
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}

	public void updateTLSDetails(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		
		if (eapConfigDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			eapConfigData.setLastModifiedByStaffId(staffData.getStaffId());
			eapConfigDataManager.updateEapTLSDetails(eapConfigData,staffData,actionAlias);
			commit(session);

		} catch(DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}
	
	public void updateEapGsmDetails(EAPConfigData eapConfigData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		if (eapConfigDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			eapConfigData.setLastModifiedByStaffId(staffData.getStaffId());
			eapConfigDataManager.updateEapGsmDetails(eapConfigData,staffData,actionAlias);
			commit(session);

		} catch(DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	
	public void updateSupportedAuthMethods(EAPConfigData eapConfigData,IStaffData staffData,String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			eapConfigData.setLastModifiedByStaffId(staffData.getStaffId());
			eapConfigDataManager.updateSupportedAuthMethods(eapConfigData,staffData,actionAlias);
			commit(session);

		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}

	public List<ServerCertificateData> getListOfServerCertificate() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		List<ServerCertificateData> lstServerCertificateData;

		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			lstServerCertificateData = eapConfigDataManager.getServerCertificateDataList();
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

		return lstServerCertificateData; 
	}
	
	public String getEapNameFromId(String eapId) throws DataManagerException {
		String eapConf = null;
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			eapConf = eapConfigDataManager.getEapNameFromId(eapId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return eapConf;
	}

	public String getServerCertificateIdFromName(String serverCertificateName) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		String serverCertificateId ;
		EAPConfigDataManager eapConfigDataManager = getEAPConfigDataManager(session);
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		try{

			
			serverCertificateId = eapConfigDataManager.getServerCertificateIdFromName(serverCertificateName.trim());
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return serverCertificateId;
	}
	
	public String getServerCertificateNameFromId(String serverCertificateId) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		String serverCertificateName ;
		EAPConfigDataManager eapConfigDataManager = getEAPConfigDataManager(session);
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		try{

			
			serverCertificateName = eapConfigDataManager.getServerCertificateNameFromId(serverCertificateId);
		} catch (DataManagerException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return serverCertificateName;
	}
	
	/**
	 * this update EAP configuration by it's name
	 * @param eapConfigData EAP configuration data to be update
	 * @param staffData user information who make upadte operation
	 * @param eapConfigName EAP configuration name 
	 * @throws DataManagerException
	 */
	public void updateEapConfigByName(EAPConfigData eapConfigData, IStaffData staffData, String eapConfigName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager = getEAPConfigDataManager(session);
		if (eapConfigDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			eapConfigDataManager.updateEapConfigDataByName(eapConfigData, staffData,ConfigConstant.UPDATE_EAP_CONFIGURATION,eapConfigName.trim());
			commit(session);
		} catch (DataManagerException e) {
			rollbackSession(session);
			throw e;
		} catch (Exception e) {
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
}