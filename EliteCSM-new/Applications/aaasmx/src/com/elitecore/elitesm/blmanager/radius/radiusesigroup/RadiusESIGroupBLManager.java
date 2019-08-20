package com.elitecore.elitesm.blmanager.radius.radiusesigroup;

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
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.RadiusESIGroupDatamanager;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
/**
 * 
 * @author Tejas Shah
 *
 */
public class RadiusESIGroupBLManager extends BaseBLManager{

	public RadiusESIGroupDatamanager getRadiusESIGroupDatamanager(IDataManagerSession session) { 
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = (RadiusESIGroupDatamanager) DataManagerFactory.getInstance().getDataManager(RadiusESIGroupDatamanager.class, session);
		return radiusESIGroupDatamanager;
	}
	
	
	public List<RadiusESIGroupData> getRadiusESIGroupDataList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = getRadiusESIGroupDatamanager(session);
		if(radiusESIGroupDatamanager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		List<RadiusESIGroupData> radiusESIGroupDataList = null;
		try{

			radiusESIGroupDataList = radiusESIGroupDatamanager.getRadiusESIGroupDataList();
			
			return radiusESIGroupDataList;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
				
	}
	
	
	public PageList searchRadiusESIGroup(RadiusESIGroupData radiusESIGroupData, IStaffData staffData, int requiredPageNo, Integer pageSize) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = getRadiusESIGroupDatamanager(session);

		if(radiusESIGroupDatamanager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			PageList pluginList = radiusESIGroupDatamanager.searchRadiusESIGroupData(radiusESIGroupData, requiredPageNo, pageSize);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_RADIUS_ESI_GROUP);
            
        	commit(session);
        	
			return pluginList;

		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public RadiusESIGroupData getRadiusESIGroupById(String radiusESIGroupId) throws DataManagerException {
		
		return getRadiusESIGroup(radiusESIGroupId, BY_ID);
		
	}

	public RadiusESIGroupData getRadiusESIGroupByName(String radiusESIGroupName) throws DataManagerException {
		
		return getRadiusESIGroup(radiusESIGroupName.trim(), BY_NAME);
		
	}

	private RadiusESIGroupData getRadiusESIGroup(Object radiusESIGroupIdOrName, boolean isIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = getRadiusESIGroupDatamanager(session);
		 
		if(radiusESIGroupDatamanager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			RadiusESIGroupData radiusESIGroup;
			
			if (isIdOrName) {
				radiusESIGroup = radiusESIGroupDatamanager.getRadiusESIGroupById((String)radiusESIGroupIdOrName);
			} else {
				radiusESIGroup = radiusESIGroupDatamanager.getRadiusESIGroupByName((String) radiusESIGroupIdOrName);
			}
			
			return radiusESIGroup;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	

	public void createRadiusESIGroup(RadiusESIGroupData radiusESIGroup, IStaffData staffData) throws DataManagerException {

		List<RadiusESIGroupData> radiusESIGroups = new ArrayList<RadiusESIGroupData>();
		radiusESIGroups.add(radiusESIGroup);
		createRadiusESIGroup(radiusESIGroups, staffData, "");
		
	}
	
	public Map<String, List<Status>> createRadiusESIGroup(List<RadiusESIGroupData> radiusESIGroups, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(RadiusESIGroupDatamanager.class, radiusESIGroups, staffData, ConfigConstant.CREATE_RADIUS_ESI_GROUP, partialSuccess);
	}
	

	public void updateRadiusESIGroupById(RadiusESIGroupData radiusESIGroup, IStaffData staffData) throws DataManagerException {
		
		updateRadiusESIGroup(radiusESIGroup, staffData, null);
		
	}
	
	public void updateRadiusESIGroupByName(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupName) throws DataManagerException {
		
		updateRadiusESIGroup(radiusESIGroup, staffData, radiusESIGroupName);
		
	}
	
	private void updateRadiusESIGroup(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = getRadiusESIGroupDatamanager(session);
		 
		if(radiusESIGroupDatamanager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (radiusESIGroupName == null) {
				radiusESIGroupDatamanager.updateRadiusESIGroupById(radiusESIGroup, staffData, radiusESIGroup.getId());
			} else {
				radiusESIGroupDatamanager.updateRadiusESIGroupByName(radiusESIGroup, staffData, radiusESIGroupName.trim());
			}

			commit(session);
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	

	public void deleteRadiusESIGroupById(List<String> radiusESIGroupIds, IStaffData staffData) throws DataManagerException {
		
		deleteRadiusESIGroup(radiusESIGroupIds, staffData, BY_ID);
		
	}

	public void deleteRadiusESIGroupByName(List<String> radiusESIGroupNames, IStaffData staffData) throws DataManagerException {
		
		deleteRadiusESIGroup(radiusESIGroupNames, staffData, BY_NAME);
		
	}

	private void deleteRadiusESIGroup(List<String> radiusESIGroupIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusESIGroupDatamanager radiusESIGroupDatamanager = getRadiusESIGroupDatamanager(session);
		 
		if(radiusESIGroupDatamanager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(radiusESIGroupIdOrNames) == false) {

				int size = radiusESIGroupIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(radiusESIGroupIdOrNames.get(i)) == false) {

						String radiusESIGroupIdOrName = radiusESIGroupIdOrNames.get(i).trim();
						
						String radiusESIGroupName;

						if (isIdOrName) {
							radiusESIGroupName = radiusESIGroupDatamanager.deleteRadiusESIGroupById(radiusESIGroupIdOrName);
						} else {
							radiusESIGroupName = radiusESIGroupDatamanager.deleteRadiusESIGroupByName(radiusESIGroupIdOrName);
						}

						staffData.setAuditName(radiusESIGroupName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_RADIUS_ESI_GROUP);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
}
