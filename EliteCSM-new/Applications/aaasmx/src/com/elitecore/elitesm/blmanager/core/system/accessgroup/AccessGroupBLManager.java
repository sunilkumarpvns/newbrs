package com.elitecore.elitesm.blmanager.core.system.accessgroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.AccessGroupDataManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.ProfileDataManager;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;

public class AccessGroupBLManager extends BaseBLManager{
	/**
	 * @author  dhavalraval
	 * @param   groupData
	 * @param staffData 
	 * @throws  DataManagerException
	 * @purpose This method is generated to create the GroupData.
	 */
	public IGroupData create(IGroupData groupData, List lstActionlist, IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		if(accessGroupDataManager == null )
			throw new DataManagerException("Data Manager implementathion not found for "+getClass().getName());
		
		try {
			session.beginTransaction();
			
			groupData = accessGroupDataManager.create(groupData);
			accessGroupDataManager.updateActionGroup(lstActionlist, groupData.getGroupId(), staffData);
			
			session.commit();
			return groupData;
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method is generated to return list of GroupData.
	 */
	public List getAccessGroupList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			List lstAccessGroupList;

			if(accessGroupDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
					
			lstAccessGroupList = accessGroupDataManager.getAccessGroupList();
			return lstAccessGroupList;

		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive access group list, reason : " + e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	public List getAccessGroupDetailsList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		List lstAccessGroupList;

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
		try{
			session.beginTransaction();	
				
			lstAccessGroupList = accessGroupDataManager.getAccessGroupList();
					
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :" + e.getMessage());
		} finally {
			closeSession(session);
		}
		return lstAccessGroupList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method is generated to return list of GroupActionRelData.
	 */
	public List getGroupActionRelList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			List lstGroupActionRelList;
			
			if(accessGroupDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
			lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList();
			return lstGroupActionRelList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive access group relation list, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public List getGroupActionRelList(String groupId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
	
		try {
			List lstGroupActionRelList;
			
			if(accessGroupDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
			lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList(groupId);
			
			return lstGroupActionRelList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive access group relation list, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstGroupId
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete GroupData.
	 */
	public void deleteAccessGroup(List lstGroupId,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		
		try {
			session.beginTransaction();
			if(lstGroupId != null){
				for(int i=0;i<lstGroupId.size();i++){
					if(lstGroupId.get(i) != null){
						
						String transactionId = (String)lstGroupId.get(i);
						accessGroupDataManager.deleteAccessGroupActionRel(transactionId);
						String groupName=accessGroupDataManager.deleteAccessGroup(transactionId);
						staffData.setAuditName(groupName);
						AuditUtility.doAuditing(session, staffData, actionAlias);
					}
				}
				session.commit();
			}else{
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
		} catch (DataManagerException hExp) {
			hExp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :"+hExp.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   groupId
	 * @return  IGroupData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to return GroupData object on the basis of groupId.
	 */
	public IGroupData getGroupData(String groupId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			IGroupData groupData = new GroupData();
			groupData.setGroupId(groupId);
			
			List lstGroupList = accessGroupDataManager.getAccessGroupList(groupData);
			
			if(lstGroupList != null && lstGroupList.size() >=1){
				groupData = (GroupData)lstGroupList.get(0);
			}else{
				groupData = null;
			}
			return groupData;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive group data, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/*public IGroupActionRelData getGroupActionRelData(String groupId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		IGroupActionRelData groupActionRelData = new GroupActionRelData();
		groupActionRelData.setGroupId(groupId);
		
		List lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList(groupActionRelData);
		
		if(lstGroupActionRelList != null && lstGroupActionRelList.size() >=1){
			groupActionRelData = (GroupActionRelData)lstGroupActionRelList.get(0);
		}else{
			groupActionRelData = null;
		}
		session.close();
		return groupActionRelData;
	}*/
	public List getGroupActionRelData(String groupId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			List lstGroupActionRelList;
			
			if(accessGroupDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
			lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList(groupId);
			return lstGroupActionRelList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive group action relation data, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public List getGroupActionRelData(List<String> groupIds) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			List lstGroupActionRelList;
			
			if(accessGroupDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
			lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList(groupIds);
			return lstGroupActionRelList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive group action relation data, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   groupData
	 * @return  IGroupData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to return GroupData object on the basis of groupData.
	 */
	public IGroupData getGroupData(IGroupData groupData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		try {
			List lstGroupData = accessGroupDataManager.getAccessGroupList(groupData);
			
			if(lstGroupData != null && lstGroupData.size() >=1){
				groupData = (IGroupData)lstGroupData.get(0);
			}
			return groupData;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive group action relation data, reason : " + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   groupData
	 * @throws  DataManagerException
	 * @purpose This method is generated to update GroupData.
	 */
	public void updateAccessGroup(IGroupData groupData, List<String> lstActionlist,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		Timestamp currentDate = new Timestamp((new Date()).getTime());

		if(accessGroupDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Dat Manager implementation not found for "+getClass().getName());
		}
		
		try {
			session.beginTransaction();
			List<GroupActionRelData> actionGroupRelDataList = new ArrayList<GroupActionRelData>();
			for(String actionId: lstActionlist){
				
				IActionData actionData = getActionData(actionId);
				GroupActionRelData data = new GroupActionRelData();

				data.setGroupId(groupData.getGroupId());
				data.setActionId(actionId);
				data.setActionData(actionData);
				
				actionGroupRelDataList.add(data);
				
			}
			accessGroupDataManager.updateAccessGroup(groupData,currentDate);
			accessGroupDataManager.updateActionGroup(actionGroupRelDataList, groupData.getGroupId(), staffData);
			String transactionId = String.valueOf(groupData.getGroupId());
	        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
			
			session.commit();
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   groupId
	 * @throws  DataManagerException
	 * @purpose This method is generated to update GroupActionData.
	 */
	public void updateGroupAction(List lstActionId,String groupId, IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		
		try {
			session.beginTransaction();
			accessGroupDataManager.updateActionGroup(lstActionId, groupId, staffData);
			
		    session.commit();
		} catch (DataManagerException exp) {
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Update Group Action failed :"+exp.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	 
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   groupId
	 * @param   accessGroupDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete the GroupActionRelData.
	 */
	public void deleteGroupAction(List lstActionId,String groupId,AccessGroupDataManager accessGroupDataManager) throws DataManagerException{
		if(lstActionId != null){
			for(int i=0;i<lstActionId.size();i++){
				if(lstActionId.get(i) != null){
					accessGroupDataManager.deleteGroupAction(lstActionId.get(i).toString(),Long.valueOf(groupId));
				}
		  }
		}else{
			throw new DataManagerException("Data Manager implementation not found for deleteGroupAction");
	    }
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   groupId
	 * @param   accessGroupDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to create the GroupActionRelData.
	 */
	public void createGroupAction(List lstActionId,String groupId,AccessGroupDataManager accessGroupDataManager) throws DataManagerException{
		if(lstActionId != null){
			for(int i=0;i<lstActionId.size();i++){
				if(lstActionId.get(i) != null){
					accessGroupDataManager.createGroupAction(lstActionId.get(i).toString(),groupId);
				}
			}
		}else{
			throw new DataManagerException("Data Manager implementation not found for createGroupAction");
		}
	}
	
	/**
     * @return Returns Data Manager instance for Group data.
     */
	public AccessGroupDataManager getAccessGroupDataManager(IDataManagerSession session){
    	AccessGroupDataManager accessGroupDataManager = (AccessGroupDataManager)DataManagerFactory.getInstance().getDataManager(AccessGroupDataManager.class,session);
        return accessGroupDataManager; 
    }
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }

	public String getAccessGroupName(String groupId)throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		String groupName="";
		if(accessGroupDataManager == null )
			throw new DataManagerException("Data Manager implementathion not found for "+getClass().getName());
		
		try {
			session.beginTransaction();
			groupName = accessGroupDataManager.getAccessGroupName(groupId);
			return groupName;
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
 private IActionData getActionData(String actionId) throws DataManagerException{
	        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
	        ProfileDataManager profileDataManager = getProfileDataManager(session);
	        
	        try {
	        	IActionData actionData = new ActionData();
	            actionData.setActionId(actionId);
	            
	            ActionData dbActionData = profileDataManager.getActionList(actionData);
	            
	            if(dbActionData == null){
	                return actionData;
	            }
	            return dbActionData;
	        } catch (DataManagerException e) {
				rollbackSession(session);
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName(),e);
			} finally {
				 closeSession(session);
			}
	    }
	
	private ProfileDataManager getProfileDataManager(IDataManagerSession session){
        return (ProfileDataManager)DataManagerFactory.getInstance().getDataManager(ProfileDataManager.class,session);
    }
}
