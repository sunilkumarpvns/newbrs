package com.elitecore.nvsmx.commons.model.acl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

public class GroupDAO {


	private static final Function<GroupData, String> GROUP_DATA_TO_GROUP_ID_FUNCTION = groupData -> groupData.getId();

	private GroupDAO(){
	}

	public static String getGroupNames(List<String> ids) {
		ids.sort((o1, o2) -> (o1.compareTo(o2)));
	    StringBuilder belonginsGroups = new StringBuilder();
	    Criteria criteria= HibernateSessionFactory.getSession().createCriteria(GroupData.class);
	    criteria.add(Restrictions.in("id", ids));
	    Projection groupName = Projections.property("name");
	    criteria.setProjection(groupName);
	    List<String> groupNamelist = criteria.list();
	    if(Collectionz.isNullOrEmpty(groupNamelist)==false){
            for(String group:groupNamelist){
            	 belonginsGroups.append(group).append(",");
            }
	    	
	    }
		if (belonginsGroups.length() > 0) {
			belonginsGroups.deleteCharAt(belonginsGroups.lastIndexOf(","));
		}
	    return belonginsGroups.toString();
	    
	}
	public static String getGroupName(String groupId){
		List<String> groupList = Collectionz.newArrayList();
		groupList.add(groupId);
		return getGroupNames(groupList);
	}


	/**
	 * Name: getGroups()
	 * Work: prepares a list of available group names.  
	 * @param staffBelongingGroupIds
	 * @return list of String
	 */
	public static List<String> getGroups(String staffBelongingGroupIds) {
		String[] groupArray = CommonConstants.COMMA_SPLITTER.splitToArray(staffBelongingGroupIds);
		List<String> groups = Collectionz.newArrayList();
		for(String string : groupArray){
			groups.add(string);
		}
		return groups;
	}


	public static List<GroupData> getGroups(){
		Criteria criteria= HibernateSessionFactory.getSession().createCriteria(GroupData.class);
		return (List<GroupData>)criteria.list();

	}


	public static List<GroupData> getGroups(List<String> ids) {
		Criteria criteria= HibernateSessionFactory.getSession().createCriteria(GroupData.class);
		criteria.add(Restrictions.in("id", ids));
		List<GroupData> groupDataList = criteria.list();
		if(Collectionz.isNullOrEmpty(groupDataList) == false){
		     return groupDataList;
		}
		return null;
	}


	public static Map<String, GroupData> getGroupMapFromGroupList(List<GroupData> staffGroups){
		Map<String, GroupData> staffGroupDataMap = Collectionz.asHashMap(staffGroups, GROUP_DATA_TO_GROUP_ID_FUNCTION);
		return staffGroupDataMap;
	}


}
