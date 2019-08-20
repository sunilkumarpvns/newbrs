package com.elitecore.netvertexsm.blmanager.core.system.accessgroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.AccessGroupDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.web.core.system.accessgroup.TreeNode;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class AccessGroupBLManager extends BaseBLManager{

	public static final String MODULE = "ACCESS-GRP-BL-MGR";

	/**
	 * @author  dhavalraval
	 * @param   groupData
	 * @throws  DataManagerException
	 * @purpose This method is generated to create the GroupData.
	 */
	public IRoleData create(IRoleData groupData, List lstActionlist,List<RoleModuleActionData> roleModuleActionRelList,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(accessGroupDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementathion not found for " + AccessGroupDataManager.class.getSimpleName());
		
		try {
			session.beginTransaction();
			groupData = accessGroupDataManager.create(groupData);
			accessGroupDataManager.updateActionRole(lstActionlist,groupData.getRoleId());
			accessGroupDataManager.createRoleActionRelation(roleModuleActionRelList,groupData.getRoleId());
			String transactionId = String.valueOf(groupData.getRoleId());
	        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
			session.commit();
			return groupData;
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}finally{
			session.close();
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
		List lstAccessGroupList;
        try{
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
				
		lstAccessGroupList = accessGroupDataManager.getAccessGroupList();
        }finally{
        	session.close();
        }		
		return lstAccessGroupList;
	}
	public List getAccessGroupList(IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		List lstAccessGroupList;

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		try{
		lstAccessGroupList = accessGroupDataManager.getAccessGroupList();
		}finally{
        	session.close();
        }
		return lstAccessGroupList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method is generated to return list of GroupActionRelData.
	 */
	public List getRoleActionRelList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		List lstRoleActionRelList;
	    try{ 	
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		lstRoleActionRelList = accessGroupDataManager.getRoleActionRelList();
	    }finally{
	    	session.close();
	    }
		return lstRoleActionRelList;
	}
	
	public List getRoleActionRelList(Long roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		List lstGroupActionRelList;
        try{		
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		lstGroupActionRelList = accessGroupDataManager.getRoleActionRelList(roleId);
        }finally{
        	session.close();
        }
		return lstGroupActionRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstRoleId
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete GroupData.
	 */
	public void deleteAccessGroup(List lstRoleId,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+ AccessGroupDataManager.class.getSimpleName());
		
		try {
			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(lstRoleId) == false){
				for(int i=0;i<lstRoleId.size();i++){
					if(lstRoleId.get(i) != null){
						long roleId = (Long)lstRoleId.get(i);
						//get Staff based on Role
						IStaffData staffInfo = accessGroupDataManager.getStaffBelongingRole(roleId);
						if(staffInfo != null){
							throw new ConstraintViolationException("Role is configured with staff data");
						}

						//delete role action relation of Netvertexsm
						accessGroupDataManager.deleteAccessGroupActionRel(roleId);

						//delete role module action relation of NVSMX
						accessGroupDataManager.deleteRoleModuleActionRelation(roleId);

						//deleting role data
						accessGroupDataManager.deleteAccessGroup(roleId);
						
				        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,String.valueOf(roleId));
					}
				}
				session.commit();
			}else{
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
			}
		} catch (DataManagerException hExp) {
			session.rollback();
			throw new DataManagerException("Delete Role Action failed :"+hExp.getMessage());
		}finally{
			session.close();
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   roleId
	 * @return  IGroupData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to return GroupData object on the basis of roleId.
	 */
	public IRoleData getRoleData(long roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		IRoleData roleData = new RoleData();
		roleData.setRoleId(roleId);
		try{
		
		List lstRoleList = accessGroupDataManager.getAccessGroupList(roleData);
		
		if(lstRoleList != null && lstRoleList.size() >=1){
			roleData = (RoleData)lstRoleList.get(0);
		}else{
			roleData = null;
		}
		}finally{
			session.close();
		}
		return roleData;
	}
	
	/*public IGroupActionRelData getGroupActionRelData(String roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		IGroupActionRelData groupActionRelData = new GroupActionRelData();
		groupActionRelData.setGroupId(roleId);
		
		List lstGroupActionRelList = accessGroupDataManager.getGroupActionRelList(groupActionRelData);
		
		if(lstGroupActionRelList != null && lstGroupActionRelList.size() >=1){
			groupActionRelData = (GroupActionRelData)lstGroupActionRelList.get(0);
		}else{
			groupActionRelData = null;
		}
		session.close();
		return groupActionRelData;
	}*/
	public List getRoleActionRelData(long roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		List lstGroupActionRelList;
        try{		
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		lstGroupActionRelList = accessGroupDataManager.getRoleActionRelList(roleId);
        }catch(DataManagerException e){
        	throw e;
        }finally{
        	session.close();
        }
		return lstGroupActionRelList;
	}
	
	
	public List getRoleActionRelData(List<Long> roleIds) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		List lstGroupActionRelList;
		try{
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		lstGroupActionRelList = accessGroupDataManager.getRoleActionRelList(roleIds);
		}catch(DataManagerException e){
			throw e;
		}finally{
			session.close();
		}
		return lstGroupActionRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   roleData
	 * @return  IRoleData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to return GroupData object on the basis of groupData.
	 */
	public IRoleData getRoleData(IRoleData roleData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
       try{		
		List lstRoleData = accessGroupDataManager.getAccessGroupList(roleData);
		
		if(lstRoleData != null && lstRoleData.size() >=1){
			roleData = (IRoleData)lstRoleData.get(0);
		}
		}catch(DataManagerException e){
			throw e;
		}finally{
			session.close();
		}
		return roleData;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   groupData
	 * @throws  DataManagerException
	 * @purpose This method is generated to update GroupData.
	 */
	public void updateAccessGroup(IRoleData groupData, List lstActionlist,List<RoleModuleActionData> roleModuleAction,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		Timestamp currentDate = new Timestamp((new Date()).getTime());

		if(accessGroupDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		}
		
		try {
			session.beginTransaction();
			accessGroupDataManager.updateAccessGroup(groupData,currentDate);
			accessGroupDataManager.updateActionRole(lstActionlist,groupData.getRoleId());
			accessGroupDataManager.updateRoleActionRelation(roleModuleAction, groupData.getRoleId());
			String transactionId = String.valueOf(groupData.getRoleId());
	        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
			session.commit();
		} catch (Exception exp) {
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}finally{
			session.close();
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   roleId
	 * @throws  DataManagerException
	 * @purpose This method is generated to update GroupActionData.
	 */
	public void updateGroupAction(List lstActionId,String roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);

		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		try {
			session.beginTransaction();
			accessGroupDataManager.updateActionRole(lstActionId,Long.valueOf(roleId));
		    session.commit();
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Update Group Action failed :"+exp.getMessage());
		}finally{
			session.close();
		}
	}
	
	 
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   roleId
	 * @param   accessGroupDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete the GroupActionRelData.
	 */
	public void deleteGroupAction(List lstActionId,String roleId,AccessGroupDataManager accessGroupDataManager) throws DataManagerException{
		if(lstActionId != null){
			for(int i=0;i<lstActionId.size();i++){
				if(lstActionId.get(i) != null){
					accessGroupDataManager.deleteRoleAction(lstActionId.get(i).toString(),Long.valueOf(roleId));
				}
		  }
		}else{
			throw new DataManagerException("Data Manager implementation not found for deleteGroupAction");
	    }
	}
	
	/**
	 * @author  dhavalraval
	 * @param   lstActionId
	 * @param   roleId
	 * @param   accessGroupDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to create the GroupActionRelData.
	 */
	public void createRoleAction(List lstActionId,String roleId,AccessGroupDataManager accessGroupDataManager) throws DataManagerException{
		if(lstActionId != null){
			for(int i=0;i<lstActionId.size();i++){
				if(lstActionId.get(i) != null){
					accessGroupDataManager.createRoleAction(lstActionId.get(i).toString(),Long.valueOf(roleId));
				}
			}
		}else{
			throw new DataManagerException("Data Manager implementation not found for createGroupAction");
		}
	}
	public List getRoleModuleActionRelData(long roleId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessGroupDataManager accessGroupDataManager = getAccessGroupDataManager(session);
		
		List roleModuleActionRelList;
		try{
		if(accessGroupDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + AccessGroupDataManager.class.getSimpleName());
		
		roleModuleActionRelList = accessGroupDataManager.getRoleModuleActionRelList(roleId);
		}catch(DataManagerException e){
			throw e;
		}finally{
			session.close();
		}
		return roleModuleActionRelList;
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
	
	
	public static boolean generateList(TreeNode node,List<RoleModuleActionData> roleModuleActionList){
		boolean isUpdateBasicDetail = false;
		if(node.getState().isChecked()==false){
			return false;
		}
		
		StringBuilder allowedActions = new StringBuilder();
		if(Collectionz.isNullOrEmpty(node.getNodes())==false){
			for(TreeNode subNode: node.getNodes()){
				if(subNode.getState().isChecked()==false){
					continue;
				}
				ACLAction action = ACLAction.fromName(subNode.getText());
				if(action == null){
					generateList(subNode, roleModuleActionList);
					continue;
				}
				if(action == ACLAction.UPDATE){
					if(Collectionz.isNullOrEmpty(subNode.getNodes()))
						allowedActions.append(subNode.getText()).append(",");
					else{
						isUpdateBasicDetail = generateList(subNode, roleModuleActionList);
						if(isUpdateBasicDetail == true){
							allowedActions.append(ACLAction.UPDATE.name()).append(",");
							allowedActions.append(ACLAction.UPDATE_BASIC_DETAIL.name()).append(",");
						}
					}
				}else if(action == ACLAction.UPDATE_BASIC_DETAIL){
					isUpdateBasicDetail = true;
				}else{
					allowedActions.append(subNode.getText()).append(",");
				}
			}
		}
		if(node.getText().equals(ACLAction.UPDATE.getVal()) == false){
			if(Strings.isNullOrBlank(allowedActions.toString())) {
				LogManager.getLogger().warn(MODULE, "No Action is configured with Module: " + node.getText() + " .So skipping further processing");
				return isUpdateBasicDetail;
			}

			RoleModuleActionData roleModuleActionData = new RoleModuleActionData();
			roleModuleActionData.setModuleName(ACLModules.fromVal(node.getText()).name());
			roleModuleActionData.setActions(allowedActions.toString());
			roleModuleActionList.add(roleModuleActionData);
		}
		return isUpdateBasicDetail;
	}
}