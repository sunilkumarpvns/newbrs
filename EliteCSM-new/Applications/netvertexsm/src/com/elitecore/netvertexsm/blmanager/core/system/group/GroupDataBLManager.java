package com.elitecore.netvertexsm.blmanager.core.system.group;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.Collector;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.group.GroupDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;

import javax.annotation.Nonnull;

public class GroupDataBLManager  extends BaseBLManager {
	
	private static GroupDataBLManager groupDataBLManager;
	
	private GroupDataManager getGroupDataManager(IDataManagerSession session) {
		GroupDataManager groupDataManager = (GroupDataManager)DataManagerFactory.getInstance().getDataManager(GroupDataManager.class, session);
        return groupDataManager;
	}
	
	public static final GroupDataBLManager getInstance(){
        if (groupDataBLManager == null) {
            synchronized (GroupDataBLManager.class) {
                if (groupDataBLManager == null){
                	groupDataBLManager = new GroupDataBLManager();
                }
            }
        }
        return groupDataBLManager;
    }
	
	private GroupDataBLManager() {
	}
	
	public void create(GroupData groupData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        GroupDataManager groupDataManager = getGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (groupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	groupDataManager.create(groupData);
        	//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Group Detail found. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }
	
	public void update(GroupData groupData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        GroupDataManager groupDataManager = getGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (groupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	groupDataManager.update(groupData);
        	//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	
        	session.commit();
        	
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Group Detail found. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }
	
	public void delete(String[] groupDataIds,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		GroupDataManager groupDataManager = getGroupDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (groupDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();
        	groupDataManager.delete(groupDataIds);
        	//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	
        	session.commit();
        	
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
        
    }
	public PageList search(GroupData groupData,int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		GroupDataManager groupDataManager = getGroupDataManager(session);
        
        PageList pageList = null;
        if (groupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	pageList = groupDataManager.search(groupData,pageNo,pageSize);
        	return pageList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
       
	}
	public GroupData getGroupData(String groupDataId) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		GroupDataManager groupDataManager = getGroupDataManager(session);
        
		if (groupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	return groupDataManager.getGroupData(groupDataId);
       
        
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}
	
	public List<GroupData> getGroupDatas() throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		GroupDataManager groupDataManager = getGroupDataManager(session);
        
		if (groupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	return groupDataManager.getGroupDatas();
       
        
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
	}

	public String getGroupNames(List<String> groupIds) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		GroupDataManager groupDataManager = getGroupDataManager(session);
		if (groupDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	return groupDataManager.getGroupNames(groupIds);
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }

	}

	public IRoleData getRoleDataForGroupData(String groupId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GroupDataManager groupDataManager = getGroupDataManager(session);
		if (groupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			return groupDataManager.getRoleDataForGroupData(groupId);

		} catch (DataManagerException exp) {
			throw new DataManagerException("Action failed : " + exp.getMessage());
		} finally {
			session.close();
		}
	}

	public List<GroupData> getGroupDataFromIds(List<String> groupIds) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GroupDataManager groupDataManager = getGroupDataManager(session);
		if (groupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			return groupDataManager.getGroupDataFromIds(groupIds);
		} catch (DataManagerException exp) {
			throw new DataManagerException("Action failed : " + exp.getMessage());
		} finally {
			session.close();
		}

	}

	public
	@Nonnull
	String getGroupNamesOrderedByGroupID (@Nonnull List<String> groupIds, @Nonnull List<GroupData> groupDataList) {

		GroupDataCollector groupDataCollector = new GroupDataCollector();
		groupDataCollector.start();
		for (String groupId : groupIds) {
			for (GroupData groupData : groupDataList) {
				if (groupId.equals(groupData.getId())) {
					groupDataCollector.collect(groupData);
					break;
				}
			}
		}
		groupDataCollector.stop();
		return groupDataCollector.get();
	}

	public List<GroupData> getStaffAccessibleGroups(String actionAlias, String userName, Map<String, RoleData> groupIdVsStaffGroupRoleRelMap) throws DataManagerException {

		if (isAdmin(userName)) {
			return getGroupDatas();
		}

		return filterGroupData(actionAlias, groupIdVsStaffGroupRoleRelMap);
	}


	public List<GroupData> filterGroupData(String actionAlias, Map<String, RoleData> groupIdVsStaffGroupRoleRelMap) throws DataManagerException {
		List<String> filteredGroupIds = Collectionz.newArrayList();
		for (Map.Entry<String, RoleData> roleDataEntry : groupIdVsStaffGroupRoleRelMap.entrySet()) {
			if (roleDataEntry.getValue().getActionsAlias().contains(actionAlias)) {
				filteredGroupIds.add(roleDataEntry.getKey());
			}
		}
		if (Collectionz.isNullOrEmpty(filteredGroupIds) == false) {
			return getGroupDataFromIds(filteredGroupIds);
		}
		return null;
	}

	private final class GroupDataCollector implements Collector<GroupData, String> {

		private StringBuilder groupDataName;

		@Override
		public void start() {
			groupDataName = new StringBuilder();
		}

		@Override
		public void stop() {
			if (groupDataName.length() > 0) {
				groupDataName.deleteCharAt(groupDataName.length() - 1);
			}
		}

		@Override
		public void collect(GroupData groupData) {
			groupDataName.append(groupData.getName()).append(CommonConstants.COMMA);
		}

		@Override
		public String get() {
			if (groupDataName != null) {
				return groupDataName.toString();
			} else {
				return null;
			}

		}
	}
}
