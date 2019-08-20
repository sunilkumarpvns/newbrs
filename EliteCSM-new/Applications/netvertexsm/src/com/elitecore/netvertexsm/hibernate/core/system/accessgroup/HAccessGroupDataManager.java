package com.elitecore.netvertexsm.hibernate.core.system.accessgroup;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.AccessGroupDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

public class HAccessGroupDataManager extends HBaseDataManager implements AccessGroupDataManager{
	private static final String ROLE_ID = "roleId";
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to create the group data.
	 */
	public IRoleData create(IRoleData groupData) throws DataManagerException{
		try {
			Session session = getSession();
			session.save(groupData);
			return groupData;
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author dhavalraval
	 * @return List
	 * @throws DataManagerException
	 * @purpose This method returns the list of all the records of GroupData
	 */
	public List getAccessGroupList() throws DataManagerException{
		List lstAccessGroupList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleData.class);
			lstAccessGroupList = criteria.list();
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstAccessGroupList;
	}
	
	
	/**
	 * @author dhavalraval
	 * @return List
	 * @throws DataManagerException
	 * @purpose This method returns the list of all the records of
	 *          GroupActionRelData
	 */
	public List getRoleActionRelList() throws DataManagerException{
		List lstGroupactionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleActionRelData.class);
			lstGroupactionRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupactionRelList;
	}
	
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to remove specific group using groupid.
	 */
	public void deleteAccessGroup(long roleId) throws DataManagerException{
		RoleData roleData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleData.class);
			roleData = (RoleData)criteria.add(Restrictions.like(ROLE_ID,roleId))
										   .uniqueResult();
			session.delete(roleData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception  exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @purpose This method is generated to remove the contents from GroupActionRelData using the groupid.
	 */
	public void deleteAccessGroupActionRel(long roleId) throws DataManagerException{
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleActionRelData.class)
									   .add(Restrictions.eq(ROLE_ID,roleId));
			List lstRoleActionRelList = criteria.list();

			for(int i=0;i<lstRoleActionRelList.size();i++){
				IRoleActionRelData roleActionRelData = (IRoleActionRelData)lstRoleActionRelList.get(i);
				session.delete(roleActionRelData);
			}

		} catch (Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @return  List
	 * @purpose This method returns the list of all the records of GroupData.
	 */
	public List getAccessGroupList(IRoleData roleData) throws DataManagerException{
		List lstAccessGroupList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleData.class);
			
			if(roleData.getRoleId() != 0)
				criteria.add(Restrictions.eq(ROLE_ID,roleData.getRoleId()));
			
			lstAccessGroupList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstAccessGroupList;
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @return  List
	 * @purpose This method returns the list of all the records of GroupActionRelData.
	 */
	public List getRoleActionRelList(IRoleActionRelData groupActionRelData) throws DataManagerException {
		List lstGroupActionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleActionRelData.class);
			
			if(groupActionRelData.getRoleId() != 0)
				criteria.add(Restrictions.eq(ROLE_ID,groupActionRelData.getRoleId()));
			
			lstGroupActionRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupActionRelList;
	}
	
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to update the groupData
	 */
	public void updateAccessGroup(IRoleData igroupData,Timestamp statusChangeDate) throws DataManagerException{
		Session session = getSession();
		RoleData groupData = null;

		if(igroupData != null){
			try {
				Criteria criteria = session.createCriteria(RoleData.class);
				groupData = (RoleData)criteria.add(Restrictions.eq(ROLE_ID,igroupData.getRoleId()))
											   .uniqueResult();
				groupData.setName(igroupData.getName());
				groupData.setDescription(igroupData.getDescription());
				groupData.setCreateDate(igroupData.getCreateDate());
				groupData.setLastModifiedDate(statusChangeDate);
				session.update(groupData);
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp){
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException();
		}
	}
	
	

	/**
	 * @author  dhavalraval 
	 * @throws  DataManagerException 
	 * @purpose This method is generated to update the groupData
	 */
	public void updateActionRole(List lstLDAPDatasourceSchemaData,long roleId) throws DataManagerException{
//		List ldapDatasourceSchemaList = null;
		List lstRoleActionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleActionRelData.class);
			criteria.add(Restrictions.eq(ROLE_ID,roleId));
//				                 ldapDatasourceSchemaList = criteria.list();
			lstRoleActionRelList = criteria.list();
				        
				         /*   if (ldapDatasourceSchemaList.size() > 0) {
				                for ( int i = 0; i < ldapDatasourceSchemaList.size(); i++ ) {
				                    IRoleActionRelData ldapDatasourceSchemaData = (IRoleActionRelData) ldapDatasourceSchemaList.get(i);
				                    session.delete(ldapDatasourceSchemaData);
				                }
				            }*/
			if (lstRoleActionRelList.size() > 0) {
				for ( int i = 0; i < lstRoleActionRelList.size(); i++ ) {
//					IRoleActionRelData ldapDatasourceSchemaData = (IRoleActionRelData) lstGroupActionRelList.get(i);
					IRoleActionRelData groupActionRelData = (IRoleActionRelData)lstRoleActionRelList.get(i);
					session.delete(groupActionRelData);
				}
			}
			session.flush();
//				            Iterator itLdapDatasourceSchemaData = lstLDAPDatasourceSchemaData.iterator();
			Iterator iterator = lstLDAPDatasourceSchemaData.iterator();
				            /*while (itLdapDatasourceSchemaData.hasNext()) {
				            	
				            	String actionId = (String)itLdapDatasourceSchemaData.next();
				            	GroupActionRelData gro = new GroupActionRelData();
				            	
				            	
				            	gro.setActionId(actionId);
				            	gro.setGroupId(roleId);
				            	
//				                IRoleActionRelData ldapDatasourceSchemaData = (IRoleActionRelData) itLdapDatasourceSchemaData.next();
//				                ldapDatasourceSchemaData.setLdapDatasourceId(ldapDatasourceId);
				                session.save(gro);
				            }*/
			while (iterator.hasNext()) {
            	 String actionId = (String)iterator.next();
//			     GroupActionRelData gro = new GroupActionRelData();
            	 RoleActionRelData groupActionRelData = new RoleActionRelData();
			            	
            	 groupActionRelData.setActionId(actionId);
            	 groupActionRelData.setRoleId(roleId);
			            	
//			                IRoleActionRelData ldapDatasourceSchemaData = (IRoleActionRelData) itLdapDatasourceSchemaData.next();
//			                ldapDatasourceSchemaData.setLdapDatasourceId(ldapDatasourceId);
			     session.save(groupActionRelData);
			}
		}
	    catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	
	/**
	 * @author  ishani.bhatt
	 * @throws  DataManagerException 
	 * @purpose This method is used to create Role-Action Relation for NVSMX
	 */
	public void createRoleActionRelation(List<RoleModuleActionData> lstRoleModuleActionRelList,long roleId) throws DataManagerException{
		try {
			Session session = getSession();
			for(RoleModuleActionData relData : lstRoleModuleActionRelList){
				//relData.setRoleId(roleId);
				session.save(relData);
			}
		
		}
	    catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  ishani.bhatt
	 * @throws  DataManagerException 
	 * @purpose This method is used to create Role-Action Relation for NVSMX
	 */
	public void updateRoleActionRelation(List<RoleModuleActionData> lstRoleModuleActionRelList,long roleId) throws DataManagerException{
		List<RoleModuleActionData> oldRoleModuleActionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleModuleActionData.class);
			criteria.add(Restrictions.eq(ROLE_ID,roleId));
			oldRoleModuleActionRelList = criteria.list();
			if (Collectionz.isNullOrEmpty(oldRoleModuleActionRelList) == false) {
				for (RoleModuleActionData roleModuleActionData  : oldRoleModuleActionRelList) {
					session.delete(roleModuleActionData);
				}
			}
			session.flush();
			
			for(RoleModuleActionData relData : lstRoleModuleActionRelList){
				//relData.setRoleId(roleId);
				session.save(relData);
			}
		
		}
	    catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
		}
	}


	/**
	 * @author  ishani.bhatt
	 * @throws  DataManagerException
	 * @purpose This method is used to delete Role-Module-lAction Relation for NVSMX
	 */
	public void deleteRoleModuleActionRelation(long roleId) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoleModuleActionData.class)
					.add(Restrictions.eq(ROLE_ID,roleId));
			List<RoleModuleActionData> lstRoleModuleActionRel = criteria.list();

			if(Collectionz.isNullOrEmpty(lstRoleModuleActionRel) == false){
				for(RoleModuleActionData roleModuleActionRel : lstRoleModuleActionRel) {
					session.delete(roleModuleActionRel);
				}

			}

		} catch (Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public IStaffData getStaffBelongingRole(long roleId) throws DataManagerException {
		try {

			Session session = getSession();
			IRoleData roleData = new RoleData();
			roleData.setRoleId(roleId);
			Criteria criteria = session.createCriteria(StaffGroupRoleRelData.class)
					.add(Restrictions.eq("roleData",roleData));
			List<StaffGroupRoleRelData> staffGroupRoleRelations =  criteria.list();
			if(Collectionz.isNullOrEmpty(staffGroupRoleRelations) == false){
				StaffGroupRoleRelData staffGroupRoleRelation = staffGroupRoleRelations.get(0);
				return staffGroupRoleRelation.getStaffData();
			}
			return null;
		} catch (Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to delete the GroupActionRelData.
	 */
	public void deleteRoleAction(String actionId,long roleId) throws DataManagerException{
		try {
			Session session = getSession();
			
			if(actionId!=null && actionId.length()>0){
				Criteria criteria = session.createCriteria(RoleActionRelData.class)
										   .add(Restrictions.eq("actionId",actionId))
										   .add(Restrictions.eq(ROLE_ID,roleId));
				List lstRoleAction = criteria.list();
				
				if(lstRoleAction.size()>0){
					IRoleActionRelData roleActionRelData = (IRoleActionRelData)lstRoleAction.get(0);
					session.delete(roleActionRelData);
				}
			}
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	
	
	
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to create GroupActionData.
	 */
	public void createRoleAction(String actionId,long roleId) throws DataManagerException{
		RoleActionRelData groupActionRelData = new RoleActionRelData();
		try {
			Session session = getSession();
			groupActionRelData.setActionId(actionId);
			groupActionRelData.setRoleId(roleId);
			session.save(groupActionRelData);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public List getRoleActionRelList(long roleId) throws DataManagerException{
		List lstGroupActionRelList = null;
//		System.out.println("@@@@@@@@@@ here at the Hibernate Side the value of the GroupActionRelList are @@@@@"+roleId);
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(RoleActionRelData.class);
			criteria.add(Restrictions.eq(ROLE_ID,roleId));
			
			lstGroupActionRelList = criteria.list();
//			System.out.println("@!#$!@#$@#$ here the total size of the list are  :"+lstGroupActionRelList.size());
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupActionRelList;
	}
	public List getRoleActionRelList(List<Long> roleIds) throws DataManagerException{
		List lstGroupActionRelList = null;

		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(RoleActionRelData.class);
			criteria.add(Restrictions.in(ROLE_ID,roleIds));
			
			lstGroupActionRelList = criteria.list();

		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupActionRelList;
	}
	
	public List getRoleModuleActionRelList(long roleId) throws DataManagerException{
		List roleModuleActionRelList = null;
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(RoleModuleActionData.class);
			criteria.add(Restrictions.eq(ROLE_ID,roleId));
			
			roleModuleActionRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return roleModuleActionRelList;
	}
}
