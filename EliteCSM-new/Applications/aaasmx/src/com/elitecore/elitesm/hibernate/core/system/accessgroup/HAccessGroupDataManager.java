package com.elitecore.elitesm.hibernate.core.system.accessgroup;


import java.sql.Timestamp;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.AccessGroupDataManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HAccessGroupDataManager extends HBaseDataManager implements AccessGroupDataManager{
	private static final String OLD_VALUE = "OldValue";
	private static final String DISABLED = "Disabled";
	private static final String NEW_VALUE = "NewValue";

	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to create the group data.
	 */
	public IGroupData create(IGroupData groupData) throws DataManagerException{
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
			Criteria criteria = session.createCriteria(GroupData.class);
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
	public List getGroupActionRelList() throws DataManagerException{
		List lstGroupactionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupActionRelData.class);
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
	public String deleteAccessGroup(String groupId) throws DataManagerException{
		GroupData groupData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupData.class);
			groupData = (GroupData)criteria.add(Restrictions.like("groupId",groupId))
										   .uniqueResult();
			session.delete(groupData);
			
			return groupData.getName();
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
	public void deleteAccessGroupActionRel(String groupId) throws DataManagerException{
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupActionRelData.class)
									   .add(Restrictions.eq("groupId",groupId));
			List lstGroupActionRelList = criteria.list();
			
			for(int i=0;i<lstGroupActionRelList.size();i++){
				IGroupActionRelData groupActionRelData = (IGroupActionRelData)lstGroupActionRelList.get(i);
				session.delete(groupActionRelData);
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
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
	public List getAccessGroupList(IGroupData groupData) throws DataManagerException{
		List lstAccessGroupList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupData.class);
			
			if(Strings.isNullOrBlank(groupData.getGroupId()) == false)
				criteria.add(Restrictions.eq("groupId",groupData.getGroupId()));
			
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
	public List getGroupActionRelList(IGroupActionRelData groupActionRelData) throws DataManagerException{
		List lstGroupActionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupActionRelData.class);
			
			if(Strings.isNullOrBlank(groupActionRelData.getGroupId()) == false)
				criteria.add(Restrictions.eq("groupId",groupActionRelData.getGroupId()));
			
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
	public void updateAccessGroup(IGroupData igroupData,Timestamp statusChangeDate) throws DataManagerException{
		Session session = getSession();
		GroupData groupData = null;

		if(igroupData != null){
			try {
				Criteria criteria = session.createCriteria(GroupData.class);
				groupData = (GroupData)criteria.add(Restrictions.eq("groupId",igroupData.getGroupId()))
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
	public void updateActionGroup(List<GroupActionRelData> newGroupRelDataList,String groupId, IStaffData staffData) throws DataManagerException{
		List<GroupActionRelData> oldGroupActionRelList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupActionRelData.class);
			criteria.add(Restrictions.eq("groupId",groupId));
			oldGroupActionRelList = criteria.list();
			JSONObject oldJSONObject = listToJSONObject(oldGroupActionRelList, groupId);
			JSONObject newJSONObject = listToJSONObject(newGroupRelDataList, groupId);
			
			JSONArray jsonArray = ObjectDiffer.diff(oldJSONObject, newJSONObject);
		
			ListIterator listIterator = jsonArray.listIterator();
			while(listIterator.hasNext()) {
				JSONObject jsonObject = (JSONObject)listIterator.next();
				ListIterator valueIterator = jsonObject.getJSONArray("values").listIterator();
					while(valueIterator.hasNext()) {
						JSONObject innerJsonObject = (JSONObject)valueIterator.next();
						if(ConfigConstant.HYPHEN.equals(innerJsonObject.get(NEW_VALUE))) {
							innerJsonObject.put(NEW_VALUE, DISABLED);
						}else if(ConfigConstant.HYPHEN.equals(innerJsonObject.get(OLD_VALUE))) {
							innerJsonObject.put(OLD_VALUE, DISABLED);
						}
					}
			}
			int size = oldGroupActionRelList.size();
			if (size > 0) {
				for ( int i = 0; i < size; i++ ) {
					IGroupActionRelData groupActionRelData = (IGroupActionRelData)oldGroupActionRelList.get(i);
					session.delete(groupActionRelData);
				}
			}
			session.flush();
			
			for(GroupActionRelData data : newGroupRelDataList) {
				  session.save(data);
			}
			
			staffData.setAuditId(getAuditId(ConfigConstant.ACCESS_GROUP_ACTION));
			staffData.setAuditName(ConfigConstant.HYPHEN);
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_ACCESS_GROUP_ACTION);
			session.flush();
		}
	    catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @purpose This method is generated to delete the GroupActionRelData.
	 */
	public void deleteGroupAction(String actionId,long groupId) throws DataManagerException{
		try {
			Session session = getSession();
			
			if(actionId!=null && actionId.length()>0){
				Criteria criteria = session.createCriteria(GroupActionRelData.class)
										   .add(Restrictions.eq("actionId",actionId))
										   .add(Restrictions.eq("groupId",groupId));
				List lstGroupAction = criteria.list();
				
				if(lstGroupAction.size()>0){
					IGroupActionRelData groupActionRelData = (IGroupActionRelData)lstGroupAction.get(0);
					session.delete(groupActionRelData);
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
	public void createGroupAction(String actionId,String groupId) throws DataManagerException{
		GroupActionRelData groupActionRelData = new GroupActionRelData();
		try {
			Session session = getSession();
			groupActionRelData.setActionId(actionId);
			groupActionRelData.setGroupId(groupId);
			session.save(groupActionRelData);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public List getGroupActionRelList(String groupId) throws DataManagerException{
		List lstGroupActionRelList = null;
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(GroupActionRelData.class);
			criteria.add(Restrictions.eq("groupId",groupId));
			
			lstGroupActionRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupActionRelList;
	}
	public List getGroupActionRelList(List<String> groupIds) throws DataManagerException{
		List lstGroupActionRelList = null;

		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(GroupActionRelData.class);
			criteria.add(Restrictions.in("groupId",groupIds));
			
			lstGroupActionRelList = criteria.list();

		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstGroupActionRelList;
	}

	@Override
	public String getAccessGroupName(String groupId) throws DataManagerException {
		String groupName="";
		try {
			Session session = getSession();
			
			Criteria criteria=session.createCriteria(GroupData.class);
			GroupData data=(GroupData)criteria.add(Restrictions.eq("groupId", groupId)).uniqueResult();
			if(data != null){
				groupName=data.getName();
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return groupName;
	}
	
	private JSONObject listToJSONObject(List<GroupActionRelData> groupActionRelDataList, String groupId) {
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(GroupActionRelData actionRelData : groupActionRelDataList){
			if( actionRelData != null ){
				JSONObject object = actionRelData.toJson();
				jsonArray.add(object);
			}
		}

		jsonObject.put(EliteSMReferencialDAO.getAccessGroupNameByActionId(groupId), jsonArray);

		return jsonObject;
	}
}
