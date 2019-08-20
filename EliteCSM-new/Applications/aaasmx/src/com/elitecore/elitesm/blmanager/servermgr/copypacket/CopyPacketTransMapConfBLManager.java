package com.elitecore.elitesm.blmanager.servermgr.copypacket;

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
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.CopyPacketTransMapConfDataManager;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class CopyPacketTransMapConfBLManager extends BaseBLManager {
	
	private CopyPacketTransMapConfDataManager getCopyPacketTransMapConfDataManager(IDataManagerSession session) {	
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = (CopyPacketTransMapConfDataManager) DataManagerFactory.getInstance().getDataManager(CopyPacketTransMapConfDataManager.class, session);
		return copyPacketTransMapConfDataManager;		
	}
	
	public List<TranslatorTypeData> getFromTranslatorTypeDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<TranslatorTypeData> translatorTypeList =null;
		
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try{				
			translatorTypeList = copyPacketTransMapConfDataManager.getFromTranslatorTypeData();
			return translatorTypeList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp); 
		} finally {
			closeSession(session);
		}
	}
	
	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		TranslatorTypeData translatorTypeData =null;
		
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			translatorTypeData = copyPacketTransMapConfDataManager.getTranslatorTypeData(translatorTypeId);
			
			return translatorTypeData;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public List<TranslatorTypeData> getToTranslatorTypeDataList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<TranslatorTypeData> translatorTypeData =null;
		
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			translatorTypeData = copyPacketTransMapConfDataManager.getToTranslatorTypeData();
			
			return translatorTypeData;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally{
			closeSession(session);
		}
	}
	
	public List<CopyPacketTranslationConfData> getCopyPacketTransMapConfigList(String toType,String fromType) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapDataManager = getCopyPacketTransMapConfDataManager(session);
		List<CopyPacketTranslationConfData> copyPacketTransMapConfDataList =null;
		
		if (copyPacketTransMapDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			copyPacketTransMapConfDataList = copyPacketTransMapDataManager.getCopyPacketTransMapConfigList(toType,fromType);
			
			return copyPacketTransMapConfDataList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally{
			closeSession(session);
		}
	}
	
	public List<CopyPacketTranslationConfData> getCopyPacketTansMapConfigList(CopyPacketTranslationConfData copyPacketTranslationConfData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<CopyPacketTranslationConfData> copyPacketTranslationConfDataList =null;
		
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			copyPacketTranslationConfDataList = copyPacketTransMapConfDataManager.getCopyPacketTransMapConfigList(copyPacketTranslationConfData);
			
			return copyPacketTranslationConfDataList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally{
			closeSession(session);
		}
	}
	
	public Map<String, List<Status>> create(List<CopyPacketTranslationConfData> copyPacketConfigDataList, IStaffData staffData, String partialSuccess) throws  DataManagerException {
		return insertRecords(CopyPacketTransMapConfDataManager.class, copyPacketConfigDataList, staffData, ConfigConstant.CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG, partialSuccess);
	}

	public PageList search(CopyPacketTranslationConfData copyPacketTranslationConfData,int requiredPageNo, Integer pageSize) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
			if (copyPacketTransMapConfDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
		    PageList pageList = copyPacketTransMapConfDataManager.search(copyPacketTranslationConfData, requiredPageNo, pageSize);
			
			return pageList;
			
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally{
			closeSession(session);
		}
	}

	public void deleteById(List<String> copyPacketConfigIds, IStaffData staffData) throws DataManagerException {
		delete(copyPacketConfigIds, staffData, BY_ID);
	}

	public void updateById(CopyPacketTranslationConfData copyPacketTranslationConfData, IStaffData staffData) throws DataManagerException {
		updateByName(copyPacketTranslationConfData, staffData, null);
		
	}
	
	public void updateBasicDetail(CopyPacketTranslationConfData copyPacketTranslationConfData,String copyPacketTransMapConfId,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
			if (copyPacketTransMapConfDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			session.beginTransaction();
			
		    copyPacketTransMapConfDataManager.updateBasicDetail(copyPacketTranslationConfData,copyPacketTransMapConfId,staffData,actionAlias);
			commit(session);
			
		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public CopyPacketTranslationConfData getCopyPacketTransMapConfigData(String copyPacketTransConfId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		CopyPacketTranslationConfData copyPacketTranslationConfData = null;
		
		if (copyPacketTransMapConfDataManager == null) { 
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			copyPacketTranslationConfData = copyPacketTransMapConfDataManager.getCopyPacketTransMapConfigData(copyPacketTransConfId);
			
			return copyPacketTranslationConfData;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	
	public CopyPacketTranslationConfData getCopyPacketTransMapConfigDetailDataById(String copyPacketTransConfId) throws DataManagerException {
		return getCopyPacketTransMapConfigDetailData(copyPacketTransConfId, BY_ID);
	}

	public List<CopyPacketTranslationConfData> getCopyPacketMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<CopyPacketTranslationConfData> copyPacketTranslationConfDataList =null;
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			copyPacketTranslationConfDataList = copyPacketTransMapConfDataManager.getCopyPacketMappingList();
			
			return copyPacketTranslationConfDataList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<CopyPacketTranslationConfData> getdiaTodiaCopyPacketMapping() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<CopyPacketTranslationConfData> copyPacketTranslationConfDataList =null;
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			copyPacketTranslationConfDataList = copyPacketTransMapConfDataManager.getdiaTodiaCopyPacketMapping();
			
			return copyPacketTranslationConfDataList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<CopyPacketTranslationConfData> getdiaToradCopyPacketMapping() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		List<CopyPacketTranslationConfData> copyPacketTranslationConfDataList =null;
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{				
			copyPacketTranslationConfDataList = copyPacketTransMapConfDataManager.getdiaToradCopyPacketMapping();
			
			return copyPacketTranslationConfDataList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public CopyPacketTranslationConfData getCopyPacketTransMapConfigDetailDataByName(String copyPacketConfName) throws DataManagerException {
		return getCopyPacketTransMapConfigDetailData(copyPacketConfName.trim(), BY_NAME);
	}
	
	private CopyPacketTranslationConfData getCopyPacketTransMapConfigDetailData(Object searchVal, boolean isByIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);

		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		CopyPacketTranslationConfData copyPacketTranslationConfData = null;
		try {	
			if (isByIdOrName) {
				copyPacketTranslationConfData = copyPacketTransMapConfDataManager.getCopyPacketTransMapConfDetailDataById((String) searchVal);
			} else {
				copyPacketTranslationConfData = copyPacketTransMapConfDataManager.getCopyPacketTransMapConfDetailDataByName((String) searchVal);
			}

			return copyPacketTranslationConfData;

		} catch(DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}

	public void deleteByName(List<String> copyPacketConfigNameList, StaffData staffData) throws DataManagerException {
		delete(copyPacketConfigNameList, staffData, BY_NAME);
	}
	
	private void delete(List<String> copyPacketConfigIdOrNameList, IStaffData staffData, boolean byIdOrName) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);
		
		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try{
			
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(copyPacketConfigIdOrNameList) == false) {
				int noOfCopyPacketConfig = copyPacketConfigIdOrNameList.size();
				for (int i=0; i < noOfCopyPacketConfig; i++) {
					if(Strings.isNullOrEmpty(copyPacketConfigIdOrNameList.get(i)) == false) {
						String copyPacketIdOrName =  copyPacketConfigIdOrNameList.get(i).trim();
						String name = null;
						if (byIdOrName) {
							name  = copyPacketTransMapConfDataManager.deleteById(copyPacketIdOrName);
						} else {
							name = copyPacketTransMapConfDataManager.deleteByName(copyPacketIdOrName);		
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG);
					}
				}
				commit(session);
			} 
			
		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public void create(CopyPacketTranslationConfData copyPacketData, IStaffData staffData) throws DataManagerException {
		List<CopyPacketTranslationConfData> copyPacketConfigDataList = new ArrayList<CopyPacketTranslationConfData>();
		copyPacketConfigDataList.add(copyPacketData);
		create(copyPacketConfigDataList, staffData, "false");
	}

	public void updateByName(CopyPacketTranslationConfData copyPacketData, IStaffData staffData, String queryOrPathParam) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		CopyPacketTransMapConfDataManager copyPacketTransMapConfDataManager = getCopyPacketTransMapConfDataManager(session);

		if (copyPacketTransMapConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try{
			session.beginTransaction();
			if (queryOrPathParam == null) {
				copyPacketTransMapConfDataManager.updateById(copyPacketData, staffData, copyPacketData.getCopyPacketTransConfId());
			} else {
				copyPacketTransMapConfDataManager.updateByName(copyPacketData, staffData, queryOrPathParam.trim());
			}
			commit(session);

		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
}
