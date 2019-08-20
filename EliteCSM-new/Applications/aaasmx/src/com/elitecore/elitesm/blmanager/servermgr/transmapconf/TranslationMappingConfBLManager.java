package com.elitecore.elitesm.blmanager.servermgr.transmapconf;

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
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.TranslationMappingConfDataManager;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class TranslationMappingConfBLManager extends BaseBLManager{

	private TranslationMappingConfDataManager getTranslationMappingConfDataManager(IDataManagerSession session) {		
		TranslationMappingConfDataManager translationMappingConfDataManager = (TranslationMappingConfDataManager) DataManagerFactory.getInstance().getDataManager(TranslationMappingConfDataManager.class, session);
		return translationMappingConfDataManager;		
	}

	public List<TranslatorTypeData> getFromTranslatorTypeDataList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslatorTypeData> translatorTypeList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorTypeList = translationMappingConfDataManager.getFromTranslatorTypeDataList();
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

	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		TranslatorTypeData translatorTypeData =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorTypeData = translationMappingConfDataManager.getTranslatorTypeData(translatorTypeId);
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
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslatorTypeData> translatorTypeList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorTypeList = translationMappingConfDataManager.getToTranslatorTypeDataList();
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

	public List<TranslationMappingConfData> getTranslationMappingConfigList(String toType,String fromType) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {				
			translatorMappingConfigList = translationMappingConfDataManager.getTranslationMappingConfigList(toType,fromType);
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<TranslationMappingConfData> getTranslationMappingConfigList(TranslationMappingConfData translationMappingConfData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigList = translationMappingConfDataManager.getTranslationMappingConfigList(translationMappingConfData);
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public void create(TranslationMappingConfData translationMappingConfData, IStaffData staffData) throws  DataManagerException {

		List<TranslationMappingConfData> translationMappingDataList = new ArrayList<TranslationMappingConfData>();
		translationMappingDataList.add(translationMappingConfData);
		create(translationMappingDataList, staffData, "");
	}

	public PageList search(TranslationMappingConfData translationMappingConfigData,int requiredPageNo, Integer pageSize) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		try{				     
			TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
			if (translationMappingConfDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			PageList pageList = translationMappingConfDataManager.search(translationMappingConfigData, requiredPageNo, pageSize);

			return pageList;

		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public void deleteById(List<String> translationMappingIds, IStaffData staffData) throws DataManagerException {
		delete(translationMappingIds, staffData, BY_ID);
	}

	public void deleteByName(List<String> translationMappingNames, IStaffData staffData) throws DataManagerException {
		delete(translationMappingNames, staffData, BY_NAME);
	}

	private void delete(List<String> translationMappingIdOrNameList, IStaffData staffData, boolean byIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingDataManager = getTranslationMappingConfDataManager(session);

		if(translationMappingDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try {
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(translationMappingIdOrNameList) == false) {
				int noOfTranslationMapping = translationMappingIdOrNameList.size();
				for (int i=0; i < noOfTranslationMapping; i++) {
					if(Strings.isNullOrEmpty(translationMappingIdOrNameList.get(i)) == false) {
						String translationMappingIdOrName =  translationMappingIdOrNameList.get(i).trim();
						String name = null;
						if (byIdOrName) {
							name  = translationMappingDataManager.deleteById(translationMappingIdOrName);
						} else {
							name = translationMappingDataManager.deleteByName(translationMappingIdOrName);		
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_TRANSLATION_MAPPING_CONFIG);
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

	public void updateBasicDetail(TranslationMappingConfData translationMappingConfData,String translationMappingId,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
			if (translationMappingConfDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			session.beginTransaction();

			translationMappingConfDataManager.updateBasicDetail(translationMappingConfData, translationMappingId, staffData, actionAlias);
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
	public TranslationMappingConfData getTranslationMappingConfData(String translationMapConfigId) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		TranslationMappingConfData translatorMappingConfigData =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigData = translationMappingConfDataManager.getTranslationMappingConfData(translationMapConfigId);
			return translatorMappingConfigData;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public TranslationMappingConfData getTranslationMappingConfDataById(String translationMappingId) throws DataManagerException {
		return getTranslationMappingConfDetailData(translationMappingId, BY_ID);
	}

	public TranslationMappingConfData getTranslationMappingConfDataByName(String translationMappingName) throws DataManagerException {
		return getTranslationMappingConfDetailData(translationMappingName.trim(), BY_NAME);
	}

	private TranslationMappingConfData getTranslationMappingConfDetailData(Object searchVal, boolean isByIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingDataManager = getTranslationMappingConfDataManager(session);

		if (translationMappingDataManager == null ) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		TranslationMappingConfData translationMappingConfData = null;
		try {
			if (isByIdOrName) {
				translationMappingConfData = translationMappingDataManager.getTranslationMappingConfDataById((String) searchVal);
			} else {
				translationMappingConfData = translationMappingDataManager.getTranslationMappingConfDataByName((String) searchVal);
			}

		} catch(DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return translationMappingConfData;

	}

	public List<TranslationMappingConfData> getRadiusToRadiusTranslationMapping() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigList = translationMappingConfDataManager.getRadiusToRadiusTranslationMappingList();
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<TranslationMappingConfData> getDiaToDiaTranslationMapping() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigList = translationMappingConfDataManager.getDiaToDiaTranslationMapping();
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<TranslationMappingConfData> getTranslationMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigList = translationMappingConfDataManager.getTranslationMappingList();
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public List<TranslationMappingConfData> getDiaToRadTranslationMappingList()  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);
		List<TranslationMappingConfData> translatorMappingConfigList =null;
		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				
			translatorMappingConfigList = translationMappingConfDataManager.getDiaToRadTranslationMappingList();
			return translatorMappingConfigList;
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public Map<String, List<Status>> create(List<TranslationMappingConfData> translationMappingList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(TranslationMappingConfDataManager.class, translationMappingList, staffData, ConfigConstant.CREATE_TRANSLATION_MAPPING_CONFIG, partialSuccess);
	}

	public void updateById(TranslationMappingConfData translationMappingData, IStaffData staffData) throws DataManagerException {
		updateByName(translationMappingData, staffData, null);

	}

	public void updateByName(TranslationMappingConfData translationMappingData, IStaffData staffData, String queryOrPathParam) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		TranslationMappingConfDataManager translationMappingConfDataManager = getTranslationMappingConfDataManager(session);

		if (translationMappingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			session.beginTransaction();

			if (queryOrPathParam == null) {
				translationMappingConfDataManager.updateById(translationMappingData, staffData, translationMappingData.getTranslationMapConfigId());
			} else {
				translationMappingConfDataManager.updateByName(translationMappingData, staffData, queryOrPathParam.trim());
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
