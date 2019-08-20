package com.elitecore.elitesm.blmanager.radius.radiuspolicygroup;

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
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.RadiusPolicyGroupDataManager;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class RadiusPolicyGroupBLManager extends BaseBLManager {
	
	public RadiusPolicyGroupDataManager getRadiusPolicyGroupDataManager(IDataManagerSession session) {
		RadiusPolicyGroupDataManager radiusPolicyDataManager = (RadiusPolicyGroupDataManager)DataManagerFactory.getInstance().getDataManager(RadiusPolicyGroupDataManager.class,session);
		return radiusPolicyDataManager; 
	}
	
	
	public void verifyRadiusPolicyName(String policyId, String policyName)throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyGroupDataManager radiusPolicyGroupDataManager = getRadiusPolicyGroupDataManager(session);
		boolean isPolicyName;

		if (radiusPolicyGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		isPolicyName = radiusPolicyGroupDataManager.verifyRadiusPolicyGroupName(policyId, policyName);
		if(isPolicyName){
			throw new DuplicateRadiusPolicyNameException("Duplicate Radius Policy Name Exception");
		}
		closeSession(session);
	}
	
	
	public PageList search(RadiusPolicyGroup radiusPolicyGroup, int pageNo, int pageSize, IStaffData staffData) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyGroupDataManager radiusPolicyGroupDataManager = getRadiusPolicyGroupDataManager(session);
		
		PageList lstRadiusPolicyList;

		if (radiusPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			
			session.beginTransaction();	
			
			lstRadiusPolicyList = radiusPolicyGroupDataManager.search(radiusPolicyGroup, pageNo, pageSize);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_RADIUS_POLICY_GROUP);
			
			commit(session);
			
			return lstRadiusPolicyList;
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	

	public RadiusPolicyGroup getRadiusPolicyGroupById(String radiusPolicyGroupId) throws DataManagerException {
		
		return getRadiusPolicyGroup(radiusPolicyGroupId, BY_ID);
		
	}

	public RadiusPolicyGroup getRadiusPolicyGroupByName(String radiusPolicyGroupName) throws DataManagerException {
		
		return getRadiusPolicyGroup(radiusPolicyGroupName.trim(), BY_NAME);
		
	}
	
	private RadiusPolicyGroup getRadiusPolicyGroup(Object radiusPolicyGroupIdOrName, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyGroupDataManager radiusPolicyGroupDataManager = getRadiusPolicyGroupDataManager(session);
		
		if (radiusPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			RadiusPolicyGroup radiusPolicyGroup;
			
			if (isIdOrName) {
				radiusPolicyGroup = radiusPolicyGroupDataManager.getRadiusPolicyGroupDataById((String)radiusPolicyGroupIdOrName);
			} else {
				radiusPolicyGroup = radiusPolicyGroupDataManager.getRadiusPolicyGroupDataByName((String)radiusPolicyGroupIdOrName);
			}
			
			return radiusPolicyGroup;
			
		} catch (DataManagerException exp) {
			throw exp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createRadiusPolicyGroup(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData) throws DataManagerException {
		
		List<RadiusPolicyGroup> radiusPolicyGroups = new ArrayList<RadiusPolicyGroup>();
		radiusPolicyGroups.add(radiusPolicyGroup);
		createRadiusPolicyGroup(radiusPolicyGroups, staffData, "false");
	}
	
	public Map<String, List<Status>> createRadiusPolicyGroup(List<RadiusPolicyGroup> radiusPolicyGroups, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(RadiusPolicyGroupDataManager.class, radiusPolicyGroups, staffData, ConfigConstant.CREATE_RADIUS_POLICY_GROUP, partialSuccess);
	}

	
	public void updateRadiusPolicyGroupById(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData) throws DataManagerException {
		
		updateRadiusPolicyGroupByName(radiusPolicyGroup, staffData, null);
		
	}
	
	public void updateRadiusPolicyGroupByName(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String radiusPolicyGroupName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyGroupDataManager radiusPolicyGroupDataManager = getRadiusPolicyGroupDataManager(session);
		
		if (radiusPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			
			session.beginTransaction();
			
			if (radiusPolicyGroupName == null) {
				radiusPolicyGroupDataManager.updateRadiusPolicyGroupById(radiusPolicyGroup, staffData, radiusPolicyGroup.getPolicyId());
			} else {
				radiusPolicyGroupDataManager.updateRadiusPolicyGroupByName(radiusPolicyGroup, staffData, radiusPolicyGroupName.trim());
			}

			commit(session);
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	

	public void deleteRadiusPolicyGroupById(List<String> radiusPolicyGroupIds, IStaffData staffData) throws DataManagerException {
		
		deleteRadiusPolicyGroup(radiusPolicyGroupIds, staffData, BY_ID);
		
	}
	
	public void deleteRadiusPolicyGroupByName(List<String> radiusPolicyGroupNames, IStaffData staffData) throws DataManagerException {
		
		deleteRadiusPolicyGroup(radiusPolicyGroupNames, staffData, BY_NAME);
		
	}


	private void deleteRadiusPolicyGroup(List<String> radiusPolicyGroupIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyGroupDataManager radiusPolicyGroupDataManager = getRadiusPolicyGroupDataManager(session);
		
		if (radiusPolicyGroupDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			
			session.beginTransaction();
			
			String radiusPolicyGroupName;
			
			if (Collectionz.isNullOrEmpty(radiusPolicyGroupIdOrNames) == false) {
				
				int size = radiusPolicyGroupIdOrNames.size();
				for (int i = 0; i < size; i++) {
					
					if (Strings.isNullOrBlank(radiusPolicyGroupIdOrNames.get(i)) == false) {
						
						String radiusPolicyGroupIdOrName = radiusPolicyGroupIdOrNames.get(i).trim();
						
						if (isIdOrName) {
							radiusPolicyGroupName = radiusPolicyGroupDataManager.deleteRadiusPolicyGroupById(radiusPolicyGroupIdOrName);
						} else {
							radiusPolicyGroupName = radiusPolicyGroupDataManager.deleteRadiusPolicyGroupByName(radiusPolicyGroupIdOrName);
						}
						
						staffData.setAuditName(radiusPolicyGroupName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_RADIUS_POLICY_GROUP);
						
					}
					
				}
				
				commit(session);
				
			}
			
		} catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
}
