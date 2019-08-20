package com.elitecore.elitesm.blmanager.radius.clientprofile;

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
import com.elitecore.elitesm.datamanager.radius.clientprofile.ClientProfileDataManager;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;

public class ClientProfileBLManager extends BaseBLManager{

	private final static String MODULE="CLIENT_PROFILE_BL_MANAGER";
	public  ClientProfileDataManager getClientProfileDataManager(IDataManagerSession  session){
		ClientProfileDataManager clientProfileDataManager = (ClientProfileDataManager)DataManagerFactory.getInstance().getDataManager(ClientProfileDataManager.class, session);
		return clientProfileDataManager;
	}

	public void create(RadiusClientProfileData clientProfileData,IStaffData staffData) throws DataManagerException,DuplicateInstanceNameFoundException{
		List<RadiusClientProfileData> radiusClientProfileDatas = new ArrayList<RadiusClientProfileData>();
		radiusClientProfileDatas.add(clientProfileData);
		create(radiusClientProfileDatas, staffData, "");
	}

	public Map<String, List<Status>> create(List<RadiusClientProfileData> radiusClientProfileDatas,IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(ClientProfileDataManager.class, radiusClientProfileDatas, staffData, ConfigConstant.CREATE_CLIENT_PROFILE, partialSuccess);
	}
	public List<VendorData> getVendorList() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<VendorData> vendorList =null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: "+  getClass().getName());
		}
		
		try{
			vendorList = clientProfileDataManager.getVendorList();
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return vendorList;
	}


	public List<ClientTypeData> getClientTypeList() throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<ClientTypeData> clientTypeDataList =null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			clientTypeDataList = clientProfileDataManager.getClientTypeList();
			Logger.logDebug(MODULE, "IN BL MANAGER SIZE IS:"+clientTypeDataList.size());
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return clientTypeDataList;
	}

	public VendorData getVendorData(String vendorInstanceId) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		VendorData vendorData =null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			vendorData = clientProfileDataManager.getVendorData(vendorInstanceId);
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return vendorData;
	}

	public ClientTypeData getClientTypeData(Long clientTypeId) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		ClientTypeData clientTypeData =null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			clientTypeData = clientProfileDataManager.getClientTypeData(clientTypeId);
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return clientTypeData;
	}

	public PageList search(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,int requiredPageNo, Integer pageSize) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		PageList clientProfileList;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager  implementation not found for: " + getClass().getName());
		}
		
		try{

			session.beginTransaction();	

			clientProfileList = clientProfileDataManager.search(radiusClientProfileData, requiredPageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_CLIENT_PROFILE);
			commit(session);
			
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		} catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

		return clientProfileList; 
	}
	
	public List<RadiusClientProfileData> getRadiusClientProfileList() throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<RadiusClientProfileData> clientProfileList=null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			clientProfileList = clientProfileDataManager.getRadiusClientProfileList();
		} catch (DataManagerException e) {
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

		return clientProfileList; 
	}

	public void deleteById(List<String> clientProfileIdOrNames, IStaffData staffData) throws DataManagerException {
		delete(clientProfileIdOrNames, staffData,BY_ID);
	}
	
	public void deleteByName(List<String> clientProfileIdOrNames, IStaffData staffData) throws DataManagerException {
		delete(clientProfileIdOrNames, staffData, BY_NAME);
	}
	private void delete(List<String> clientProfileIdOrNames, IStaffData staffData,boolean isProfileIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}

		try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(clientProfileIdOrNames) == false) {
				int size = clientProfileIdOrNames.size();
				for (int i=0;i<size;i++) {
					if (Strings.isNullOrBlank(clientProfileIdOrNames.get(i)) == false) {
						String profileIdOrName = clientProfileIdOrNames.get(i).trim();
						String name = null ;
						if (isProfileIdOrName) {
							name = clientProfileDataManager.deleteById(profileIdOrName);		    	
						} else {
							name = clientProfileDataManager.deleteByName(profileIdOrName.trim());				    	
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_CLIENT_PROFILE);
					}
				}
			}
			commit(session);
		
		} catch ( DataManagerException dme ){
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public void updateBasicDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException,DuplicateInstanceNameFoundException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: "+ getClass().getName());
		}
		
		try{
			radiusClientProfileData.setLastModifiedByStaffId(staffData.getStaffId());
			session.beginTransaction();
			clientProfileDataManager.updateBasicDetails(radiusClientProfileData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException e){
			e.printStackTrace();
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

	public void updateAdvanceDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		try{
			radiusClientProfileData.setLastModifiedByStaffId(staffData.getStaffId());
			session.beginTransaction();
			clientProfileDataManager.updateAdvanceDetails(radiusClientProfileData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException e){
			e.printStackTrace();
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

	public RadiusClientProfileData getClientProfileDataById(String clientProfileId) throws DataManagerException {
		return getClientProfileData(clientProfileId, BY_ID);
	}
	
	public RadiusClientProfileData getClientProfileDataByName(String clientProfileName) throws DataManagerException {
		return getClientProfileData(clientProfileName, BY_NAME);
	}
	
	private RadiusClientProfileData getClientProfileData(Object clientProfileNameOrId, boolean byIdOrName) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusClientProfileData radiusClientProfileData = null;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if (clientProfileDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			if(byIdOrName) {
				radiusClientProfileData=clientProfileDataManager.getClientProfileDataById(clientProfileNameOrId.toString());
			} else {
				radiusClientProfileData=clientProfileDataManager.getClientProfileByName(clientProfileNameOrId.toString());
			}
			
		} catch (DataManagerException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return radiusClientProfileData;
	}
	
	
	public void updateRadiusClientProfileDataByName(RadiusClientProfileData clientProfileData,IStaffData staffData,String clientProfileName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if (clientProfileDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		try {
			session.beginTransaction();
			clientProfileDataManager.update(clientProfileData, staffData,ConfigConstant.UPDATE_CLIENT_PROFILE,clientProfileName);
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
	
	/**
	 * This is used to get vendor instance id on base of vendor name
	 * @param vendorName contains valid name of vendor
	 * @return vendor id
	 * @throws DataManagerException
	 */
	public String getVendorIdFromName(String vendorName)throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		String vendorId;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if (clientProfileDataManager == null) {
			throw new DataManagerException("Data manager implementation not found for: "+ getClass().getName());
		}
		
		try {
			vendorId = clientProfileDataManager.getVendorIdFromName(vendorName.trim());
			
		} catch (DataManagerException e) {
			throw e;
		}finally{
			closeSession(session);
		}
		return vendorId;
	}
	
	/**
	 * This is used to fetch client type id on base of client type
	 * @param clientType contains name of client like(NAS,Radius etc..)
	 * @return client type id
	 * @throws DataManagerException
	 */
	public long getClientTypeIdFromName(String clientType)throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		long clientTypeId ;
		
		ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
		if(clientProfileDataManager==null){
			throw new DataManagerException("Data manager implementation not found for: " + getClass().getName());
		}
		
		try{
			clientTypeId = clientProfileDataManager.getClientTypeIdFromName(clientType.trim());
		} catch (DataManagerException e) {
			throw e;
		}finally{
			closeSession(session);
		}
		
		return clientTypeId;
	}
}
