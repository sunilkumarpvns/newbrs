package com.elitecore.elitesm.blmanager.externalsystem;

import java.sql.Timestamp;
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
import com.elitecore.elitesm.datamanager.externalsystem.ExternalSystemInterfaceDataManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class ExternalSystemInterfaceBLManager extends BaseBLManager {

	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInstanceDataList(long externalSystemTypeId) throws DataManagerException {
		List<ExternalSystemInterfaceInstanceData> externalSystemInstList;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ExternalSystemInterfaceDataManager externalSystemInterfaceDataManager = getESIDataManager(session);
			externalSystemInstList = externalSystemInterfaceDataManager.getExternalSystemInstanceDataList(externalSystemTypeId);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return externalSystemInstList;
	}
	
	public ExternalSystemInterfaceDataManager getESIDataManager(IDataManagerSession session) {		
		ExternalSystemInterfaceDataManager esiInstanceDataManager = (ExternalSystemInterfaceDataManager) DataManagerFactory.getInstance().getDataManager(ExternalSystemInterfaceDataManager.class, session);
		return esiInstanceDataManager;		
	}
	
	public List<ExternalSystemInterfaceTypeData> getListOfESIType() throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
			try{
				List esiTypeList = esiDataManager.getListOfESITypes();
				return esiTypeList;
			}catch(DataManagerException de){
				throw de;
			}catch(Exception de){
				de.printStackTrace();
				throw new DataManagerException(de.getMessage(), de);
			} finally {
				closeSession(session);
			}
	}
	
	/**
	 * This is used to create ESI Instance 
	 * @param esiInstanceData contains data of ESI instance
	 * @param staffData user information who do create operation
	 * @throws DataManagerException
	 */
	public void createESIInstance(ExternalSystemInterfaceInstanceData esiInstanceData, IStaffData staffData) throws DataManagerException {
		List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDatas = new ArrayList<ExternalSystemInterfaceInstanceData>();
		externalSystemInterfaceInstanceDatas.add(esiInstanceData);
		createESIInstance(externalSystemInterfaceInstanceDatas, staffData, "");
	}
	
	/**
	 * This is used to create multiple ESI Instance
	 * @param externalSystemInterfaceInstanceDatas contains list of ESI instance data
	 * @param staffData user information who do create operation
	 * @param partialSuccess 
	 * @return 
	 * @throws DataManagerException
	 */
	public Map<String, List<Status>> createESIInstance(List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDatas,IStaffData staffData, String partialSuccess) throws DataManagerException{

		if (Collectionz.isNullOrEmpty(externalSystemInterfaceInstanceDatas) == false) {
			for (ExternalSystemInterfaceInstanceData esiInstanceData : externalSystemInterfaceInstanceDatas) {

				esiInstanceData.setCreatedByStaffId(staffData.getStaffId());
				esiInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
				esiInstanceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
				esiInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
				esiInstanceData.setStatus("Y");
			}
		} 
		return insertRecords(ExternalSystemInterfaceDataManager.class, externalSystemInterfaceInstanceDatas, staffData, ConfigConstant.CREATE_EXTERNAL_SYSTEM, partialSuccess);
	}
	
	public List getESIInstanceAndTypeDetails(String name,long esiTypeId) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			List finalResultList = esiDataManager.getESIInstanceAndType(name, esiTypeId);
			return finalResultList;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}finally{
			closeSession(session);
		}
	}
	
	/**
	 * this method used to delete multiple ESI by ESI instance id
	 * @param esiInstanceIds list of ESI instance id
	 * @param staffData user information who make update operation
	 * @throws DataManagerException
	 */
	public void deleteByInstanceId(List<String> esiInstanceIds , IStaffData staffData) throws DataManagerException{
		delete(esiInstanceIds, staffData, BY_ID);
	}
	
	/**
	 * this method used to delete multiple ESI by ESI name
	 * @param esiInstanceNames list of ESI instance names
	 * @param staffData user information who make update operation
	 * @throws DataManagerException
	 */
	public void deleteByInstanceName(List<String> esiInstanceNames,IStaffData staffData) 
			throws DataManagerException {
		delete(esiInstanceNames, staffData, BY_NAME);
	}
	
	private void delete(List<String> esiInstanceIdOrNames , IStaffData staffData , boolean isEsiInstanceIdOrName) 
			throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for : " + getClass().getName());
		
		try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(esiInstanceIdOrNames) == false) {
				for(String esiIdOrName : esiInstanceIdOrNames){
					if(Strings.isNullOrBlank(esiIdOrName) == false){
						String name = null ;
						if (isEsiInstanceIdOrName){
							name = esiDataManager.deleteByInstaceId(esiIdOrName);
						} else {
							name = esiDataManager.deleteByInstaceName(esiIdOrName.trim());
						}
						//Auditing parameters
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_EXTERNAL_SYSTEM);
					}
				}
			} 
			commit(session);
		} catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public ESITypeAndInstanceData getESIInstanceDetail(String esiInstanceId) throws DataManagerException{
		

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			ESITypeAndInstanceData esiTypeAndInstanceData = esiDataManager.getESIInstanceDetail(esiInstanceId);
			return esiTypeAndInstanceData;
		}catch(DataManagerException de){
			throw de;
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	/**
	 * This method update ESI by ESI Instance Id
	 * @param esiInstanceData data to be update
	 * @param staffData user information who make update operation
	 * @throws DataManagerException
	 */
	public void updateByInstanceId(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData) throws DataManagerException{
		updateByInstanceName(esiInstanceData, staffData, null);
	}
	
	/**
	 * This method update ESI by ESI name
	 * @param esiInstanceData data to be update
	 * @param staffData user information who make update operation
	 * @param esiNameFromUrl ESI name 
	 * @throws DataManagerException
	 */
	public void updateByInstanceName(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData,String esiNameFromUrl) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			
			if(Strings.isNullOrBlank(esiNameFromUrl)){
				esiDataManager.updateByInstanceId(esiInstanceData, staffData, esiInstanceData.getEsiInstanceId());
			} else {
				esiDataManager.updateByInstanceName(esiInstanceData,staffData,esiNameFromUrl.trim());
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public PageList searchESIInstance(ESITypeAndInstanceData esiInstanceData, int requiredPageNo,Integer pageSize, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
			SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
			if(esiDataManager==null){
				throw new DataManagerException("Data manager implementation not found for : "+ getClass().getName());
			}
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager implementation not found for : "+ getClass().getName());
			}

		    PageList pageList = esiDataManager.search(esiInstanceData, requiredPageNo, pageSize);
		    
		    session.beginTransaction();	
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_EXTERNAL_SYSTEM);
			commit(session);
			return pageList;
			
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<ExternalSystemInterfaceInstanceData> getAllExternalSystemInstanceDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList = esiDataManager.getExternalSystemInterfaceInstanceDataList();
			return externalSystemInterfaceInstanceDataList;
		
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public List<ExternalSystemInterfaceInstanceData> getAcctFlowExternalSystemInstanceDataList(long authProxy) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList = esiDataManager.getAcctFlowExternalSystemInstanceDataList(authProxy);
			return externalSystemInterfaceInstanceDataList;
		
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	/** This method returns the Radius type ESI List
	 * @return  List<ExternalSystemInterfaceInstanceData> 
	 * @throws DataManagerException
	 */
	public List<ExternalSystemInterfaceInstanceData> getRadiusTypeExternalInterfaceInstanceDataList()throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList = esiDataManager.getRadiusTypeExternalInterfaceInstanceDataList();
			return externalSystemInterfaceInstanceDataList;
			
		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public String getRadiusESINameById(String esiId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		         
		String esiName = null;
		try{
	        if(esiDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			esiName = esiDataManager.getRadiusESIGroupNameById(esiId);
			return esiName;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public String getESIInstanceIdByESITypeId(String primaryESIId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
			String esiInstanceId = "";
		
		 if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		 try{
				session.beginTransaction();
				commit(session);
				return esiInstanceId;
			}catch(Exception e){
				e.printStackTrace();
				throw new DataManagerException(e.getMessage(), e);
			} finally {
				closeSession(session);
			}
	}
	
	public String getExternalSystemnameFromId(String esiInstanceId) throws DataManagerException{
		ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData = getExternalSystemInterfaceInstanceDataById(esiInstanceId);
		
		String name = "";
		if(externalSystemInterfaceInstanceData != null){
			name = externalSystemInterfaceInstanceData.getName();
		}
		return name;
	}
	
	/**
	 * This method is used to get external system interface data by it's instanceId
	 * @param esiInstanceId instance id to be search
	 * @return ExternalSystemInterfaceInstanceData
	 * @throws DataManagerException
	 */
	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataById(String esiInstanceId) throws DataManagerException{
		return getExternalSystemInterfaceInstanceData(esiInstanceId, BY_ID);
	}
	
	/**
	 * This method is used to get external system interface data by it's name
	 * @param esiInstanceName name to be search
	 * @return ExternalSystemInterfaceInstanceData
	 * @throws DataManagerException
	 */
	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataByName(String esiInstanceName) throws DataManagerException{
		return getExternalSystemInterfaceInstanceData(esiInstanceName.trim(), BY_NAME);
	}
	
	private ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceData(Object esiNameOrId, boolean byIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		ExternalSystemInterfaceInstanceData esiInstanceData = null;
		if (esiDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			if (byIdOrName){
				esiInstanceData = esiDataManager.getExternalSystemInterfaceInstanceDataById((String)esiNameOrId);
			} else {
				esiInstanceData = esiDataManager.getExternalSystemInterfaceInstanceDataByName((String)esiNameOrId);
			}
			
		} catch (DataManagerException e){
			throw e;
		}catch(Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return esiInstanceData;
	}
	
	public ExternalSystemInterfaceInstanceData getAcctExternalSystemInterfaceInstanceData(String esiName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		ExternalSystemInterfaceInstanceData esiInstanceData = null;
	
		if (esiDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			esiInstanceData = esiDataManager.getAcctExternalSystemInterfaceInstanceData(esiName);
			
		} catch (DataManagerException e){
			throw e;
		}catch(Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return esiInstanceData;
	}
	
	
	/**
	 * This is used get ESI type id from ESI type
	 * @param esiType ESI type
	 * @return ESI Type id
	 * @throws DataManagerException
	 */
	public Long getEsiTypeIdFromName(String esiType) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		Long esiTypeId ;
		try{
			esiTypeId = esiDataManager.getESITypeIdFromName(esiType);
		} catch (DataManagerException de){
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return esiTypeId;
	}
	
	/**
	 *  This is used get ESI type from ESI type id
	 * @param esiTypeId ESI type id
	 * @return ESI type 
	 * @throws DataManagerException
	 */
	public String getEsiTypeNameFromId(Long esiTypeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);
		String esiType = "" ;
		try{
			esiType = esiDataManager.getESITypeNameFromId(esiTypeId);
		} catch (DataManagerException de){
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return esiType.trim();
	}
	
	
	public String getRadiusESIIdByName(String radiusESIName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiDataManager = getESIDataManager(session);

		String esiId = "";

		try {

			if (esiDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			esiId = esiDataManager.getRadiusESIIdByName(radiusESIName);

			return esiId;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	
}
