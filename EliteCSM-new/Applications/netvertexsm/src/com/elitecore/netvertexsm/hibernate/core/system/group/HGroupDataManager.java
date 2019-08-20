package com.elitecore.netvertexsm.hibernate.core.system.group;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.group.GroupDataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;

public class HGroupDataManager extends HBaseDataManager implements GroupDataManager {

	@Override
	public void create(GroupData groupData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(groupData);
			session.flush();
		}catch(ConstraintViolationException hExp){
	        throw hExp;
	    }catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}

	@Override
	public void update(GroupData groupData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupData.class);
			criteria.add(Restrictions.eq(getId(), groupData.getId()));
			
			GroupData data = null;
			
			List list = criteria.list();
			if(Collectionz.isNullOrEmpty(list) == false){
				data = (GroupData) list.get(0);
				data.setName(groupData.getName());
				data.setDescription(groupData.getDescription());
				data.setModifiedDate(EliteUtility.getCurrentTimeStamp());
				//data.setModifiedByStaff(EliteUtility.getCurrentUserId());
			}
			
			session.update(data);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	private String getId() {
		return "id";
	}

	@Override
	public void delete(String[] groupDataIds) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(GroupData.class).add(Restrictions.in(getId(), groupDataIds)).list();
			deleteObjectList(list,session);
			
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}

	@Override
	public GroupData getGroupData(String groupDataId) throws DataManagerException {
		GroupData groupData=null;
		try{
			Session session = getSession();			
			List list = session.createCriteria(GroupData.class).add(Restrictions.eq(getId(), groupDataId)).list();
			if(Collectionz.isNullOrEmpty(list) == false){
				groupData = (GroupData) list.get(0);
			}
			return groupData;
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    
	}

	@Override
	public PageList search(GroupData groupData, int pageNo, int pageSize)
			throws DataManagerException {
		PageList pageList = null;
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroupData.class);
            
            if(Strings.isNullOrBlank(groupData.getName()) == false){
            	criteria.add(Restrictions.ilike("name",groupData.getName(),MatchMode.ANYWHERE));
            }
            
            int totalItems = criteria.list().size();

        	criteria.setFirstResult(((pageNo-1) * pageSize));

        	if (pageSize > 0 ){
        		criteria.setMaxResults(pageSize);
        	}
        	
        	List groupDatas = criteria.list();
           
            long totalPages = (long)Math.ceil(totalItems/10);
            pageList = new PageList(groupDatas, pageNo, totalPages ,totalItems);
            
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public List<GroupData> getGroupDatas() throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(GroupData.class).list();
			return list;
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public String getGroupNames(List<String> groupIds) throws DataManagerException{
		StringBuilder belonginsGroups = new StringBuilder();
	    Criteria criteria= getSession().createCriteria(GroupData.class);
	    criteria.add(Restrictions.in("id", groupIds));
	    Projection groupName = Projections.property("name");
	    criteria.setProjection(groupName);
	    List<String> groupNamelist = criteria.list();
	    if(Collectionz.isNullOrEmpty(groupNamelist)==false){
            for(String group:groupNamelist){
            	 belonginsGroups.append(group).append(",");
            }
	    	
	    }
	    belonginsGroups.deleteCharAt(belonginsGroups.lastIndexOf(","));
	    return belonginsGroups.toString();
	  }

	@Override
	public IRoleData getRoleDataForGroupData(String groupId) throws DataManagerException {
		try{
			Session session = getSession();
			GroupData groupData = new GroupData();
			groupData.setId(groupId);
			Criteria criteria = session.createCriteria(StaffGroupRoleRelData.class)
					.add(Restrictions.eq("groupData", groupData));
			List<StaffGroupRoleRelData> staffGroupRoleRelations =  criteria.list();
			if(Collectionz.isNullOrEmpty(staffGroupRoleRelations) == false){
				StaffGroupRoleRelData staffGroupRoleRelation = staffGroupRoleRelations.get(0);
				return staffGroupRoleRelation.getRoleData();
			}
			return null;
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public List<GroupData> getGroupDataFromIds(List<String> groupIds) throws DataManagerException {
		Criteria criteria = getSession().createCriteria(GroupData.class);
		criteria.add(Restrictions.in("id", groupIds));
		return criteria.list();
	}

}
