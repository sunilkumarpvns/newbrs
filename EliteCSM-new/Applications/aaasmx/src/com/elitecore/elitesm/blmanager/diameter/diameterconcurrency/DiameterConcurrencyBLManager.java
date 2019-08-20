package com.elitecore.elitesm.blmanager.diameter.diameterconcurrency;

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
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.DiameterConcurrencyDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterConcurrencyBLManager extends BaseBLManager {
	
	public PageList search(DiameterConcurrencyData diameterConcurrencyData, int pageNo, int pageSize, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = getDiameterConcurrencyDataManager(session);
		
		PageList lstDiameterPolicyList;

		if (diameterConcurrencyDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();	
			lstDiameterPolicyList = diameterConcurrencyDataManager.search(diameterConcurrencyData, pageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_CONCURRENCY);
			session.commit();
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException("Failed to search Diameter Concurrency, Reason:"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return lstDiameterPolicyList; 
	}
	
	public void create(DiameterConcurrencyData diameterConcurrencyData, IStaffData staffData) throws DataManagerException {
		List<DiameterConcurrencyData> diameterConcurrencyDataList = new ArrayList<DiameterConcurrencyData>();
		diameterConcurrencyDataList.add(diameterConcurrencyData);
		create(diameterConcurrencyDataList, staffData, "");
	}
	
	public Map<String, List<Status>> create(List<DiameterConcurrencyData> diameterConcurrencyDataList, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(DiameterConcurrencyDataManager.class, diameterConcurrencyDataList, staffData, ConfigConstant.CREATE_DIAMETER_CONCURRENCY, partialSuccess);
	}
	
	public DiameterConcurrencyDataManager getDiameterConcurrencyDataManager(IDataManagerSession session) {
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = (DiameterConcurrencyDataManager)DataManagerFactory.getInstance().getDataManager(DiameterConcurrencyDataManager.class,session);
		return diameterConcurrencyDataManager; 
	}
	
	public void verifyDiameterConcurrencyName(String diaConConfigId, String name)throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = getDiameterConcurrencyDataManager(session);
		
		if (diameterConcurrencyDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			boolean isConcurrencyName;

			isConcurrencyName = diameterConcurrencyDataManager.verifyDiameterConcurrencyName(diaConConfigId, name);
			if(isConcurrencyName){
				throw new DuplicateRadiusPolicyNameException("Duplicate Radius Policy Name Exception");
			}
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to verify Diameter Concurrency name, reason : " + e.getMessage(),e);
		}
	}

	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterPolicyGroupDataManager = getDiameterConcurrencyDataManager(session);
		
		if (diameterPolicyGroupDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		List<DiameterConcurrencyData> diameterConcurrencyDataList = null;
		try{
			diameterConcurrencyDataList = diameterPolicyGroupDataManager.getDiameterConcurrencyDataList();
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Concurrency, Reason:"+ e.getMessage(),e.getCause());
		}finally{
			closeSession(session);
		}
		return diameterConcurrencyDataList;
	}

	public DiameterConcurrencyData getDiameterConcurrencyDataByName(String diameterConcurrencyName) throws DataManagerException {
		return getDiameterConcurrencyDataByIdOrName(diameterConcurrencyName, BY_NAME);
	}
	
	public DiameterConcurrencyData getDiameterConcurrencyDataById(String diameterConcurrencyId) throws DataManagerException {
		return getDiameterConcurrencyDataByIdOrName(diameterConcurrencyId,BY_ID);
	}
	
	private DiameterConcurrencyData getDiameterConcurrencyDataByIdOrName(Object dimeterConcurrencyIdOrName,boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = getDiameterConcurrencyDataManager(session);
		
		if (diameterConcurrencyDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		DiameterConcurrencyData diameterConcurrencyData = null;
		
		try{
			if( byIdOrName ){
				diameterConcurrencyData = diameterConcurrencyDataManager.getDiameterConcurrencyDataById((String)(dimeterConcurrencyIdOrName)); 
			}else{
				diameterConcurrencyData = diameterConcurrencyDataManager.getDiameterConcurrencyDataByName((String)dimeterConcurrencyIdOrName); 
			}
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return diameterConcurrencyData;
	}


	public void updateDiameterConcurrencyByName(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String queryOrPathParam) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = getDiameterConcurrencyDataManager(session);
		
		if(diameterConcurrencyDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			
			session.beginTransaction();
			if(queryOrPathParam == null){
				diameterConcurrencyData.setDiameterConcurrencyMandatoryFieldMappingsList(diameterConcurrencyData.getDiameterConcurrencyFieldMappingList());
				diameterConcurrencyDataManager.updateById(diameterConcurrencyData, staffData, diameterConcurrencyData.getDiaConConfigId());
			}else{
				diameterConcurrencyDataManager.updateByName(diameterConcurrencyData, staffData, queryOrPathParam.trim());
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateDiameterConcurrencyById(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData) throws DataManagerException {
		updateDiameterConcurrencyByName(diameterConcurrencyData, staffData, null);
	}

	public void deleteByName(List<String> diameterConcurrencyNames,IStaffData staffData) throws DataManagerException {
		delete(diameterConcurrencyNames, staffData, BY_NAME);
	}
	
	public void deleteById(List<String> diameterConcurrencyNames,IStaffData staffData) throws DataManagerException {
		delete(diameterConcurrencyNames, staffData, BY_ID);
	}
	
	private void delete(List<String> diameterConcurrencyIdOrName,IStaffData staffData,boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterConcurrencyDataManager diameterConcurrencyDataManager = getDiameterConcurrencyDataManager(session);
		
		if(diameterConcurrencyDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		String diameterConcurrencyName = null;
		try{
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(diameterConcurrencyIdOrName) == false) {
				int size = diameterConcurrencyIdOrName.size();
				for (int i=0;i<size;i++) {
					if(Strings.isNullOrBlank(diameterConcurrencyIdOrName.get(i)) == false){
						String idOrName = diameterConcurrencyIdOrName.get(i).trim();
						if ( byIdOrName ) {
							diameterConcurrencyName = diameterConcurrencyDataManager.deleteById(idOrName);
						} else {
							diameterConcurrencyName = diameterConcurrencyDataManager.deleteByName(idOrName.trim());			
						}
						staffData.setAuditName(diameterConcurrencyName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_DIAMETER_CONCURRENCY);
					}
				}
				commit(session);
			}
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
}
