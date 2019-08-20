package com.elitecore.nvsmx.commons.model.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatus;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.corenetvertex.util.StaffACLChecker;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * This class contains method related to StaffData
 *
 * @author aditya
 *
 */
public class StaffDAO {

	private static final String MODULE = "STAFF-DAO";

	private StaffDAO(){}

	/**
	 * Returns staff data based on user name
	 *
	 * @param userName
	 * @return {@code StaffData }
	 * @throws HibernateDataException
	 */
	public static StaffData getStaffByUserName(String userName)
			throws HibernateDataException {
		StaffData staffData = null;
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(StaffData.class).add(Restrictions.eq("userName", userName));
			List list = criteria.list();
			if (Collectionz.isNullOrEmpty(list) == false) {
				staffData = (StaffData) list.get(0);
			}
			return staffData;
		} catch (HibernateException he) {
			getLogger().error(MODULE,"Failed to fetch staff Data for user name: " + userName+ " .Reason: " + he.getMessage());
			getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			getLogger().error(MODULE,"Failed to fetch staff Data for user name: " + userName+ " .Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	public static StaffData getStaffById(String id) throws HibernateDataException {
		StaffData staffData = null;
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(StaffData.class).add(Restrictions.eq("id", id));
			List list = criteria.list();
			if (Collectionz.isNullOrEmpty(list) == false) {
				staffData = (StaffData) list.get(0);
			}
			return staffData;
		} catch (HibernateException he) {
			getLogger().error(MODULE,"Failed to fetch staff Data for user name: " + id + ". Reason: " + he.getMessage());
			getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			getLogger().error(MODULE,"Failed to fetch staff Data for user name: " + id + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}


	@SuppressWarnings("unchecked")
	public static List<StaffGroupRoleRelData> getStaffGroupRoleRelData(StaffGroupRoleRelData exampleInstance) throws HibernateDataException  {
		try{
		    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(StaffGroupRoleRelData.class);
		    	criteria.add(Restrictions.eq("staffId", exampleInstance.getId()));
		    	Example example=Example.create(exampleInstance).enableLike(MatchMode.EXACT).ignoreCase();
		    	criteria.add(example);
		    	return criteria.list();

		}catch (HibernateException he) {
			getLogger().error(MODULE, "Failed to get Staff Group Role Relation Data. Reason: "+he.getMessage());
			getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			getLogger().error(MODULE, "Failed to get Staff Group Role Relation Data. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}

       public static StaffProfilePictureData getStaffProfilePicture(String id){
		   try {
			   return (StaffProfilePictureData) HibernateSessionFactory.getSession().get(StaffProfilePictureData.class,id);
           } catch (HibernateException he) {
			   LogManager.getLogger().error(MODULE,"Failed to fetch profile picture for staff with id:  " + id + ". Reason: " + he.getMessage());
			   LogManager.getLogger().trace(MODULE, he);
			   throw new HibernateDataException(he.getMessage(), he);
		   } catch (Exception e) {
			   LogManager.getLogger().error(MODULE,"Failed to fetch profile picture for staff with id:  " + id + ". Reason: " + e.getMessage());
			   LogManager.getLogger().trace(MODULE, e);
			   throw new HibernateDataException(e.getMessage(), e);
		   }
	   }

	public static void changePassword(String staffId, String oldPassword,String newPassword,String newRecentPasswords) throws Exception{
		Session session = HibernateSessionFactory.getSession();
		StaffData staffData;
		Timestamp currentTimestamp = new Timestamp(new Date().getTime());
		try{
			if(Strings.isNullOrBlank(staffId) == false && oldPassword != null && newPassword != null){
				Criteria criteria = session.createCriteria(StaffData.class);
				staffData = (StaffData)criteria.add(Restrictions.eq("id",staffId)).list().get(0);

				staffData.setPassword(PasswordUtility.getEncryptedPassword(newPassword));
				staffData.setModifiedDate(currentTimestamp);
				staffData.setPasswordChangeDate(currentTimestamp);
				staffData.setRecentPasswords(newRecentPasswords);
				session.update(staffData);
			}
		}
		catch (Exception e) {
			getLogger().error(MODULE, "Failed to change password. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw e;
		}
	}

	public static void resetPassword (@Nonnull String staffId,@Nonnull String newPassword) throws Exception {
		Timestamp currentTimestamp = new Timestamp(new Date().getTime());
		StaffData staffData = CRUDOperationUtil.get(StaffData.class, staffId);
		if (staffData == null) {
			throw new Exception("staff data not found with given id: " + staffId);
		}
		staffData.setPassword(PasswordUtility.getEncryptedPassword(newPassword));
		staffData.setModifiedDate(currentTimestamp);
		staffData.setPasswordChangeDate(currentTimestamp);
		CRUDOperationUtil.update(staffData);
	}

	public static List<StaffData> getAllStaffMembersName() throws HibernateDataException {
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(StaffData.class);
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("id"));
			projectionList.add(Projections.property("userName"));
			List<StaffData> staffDatas = criteria.list();
			if (Collectionz.isNullOrEmpty(staffDatas)) {
				staffDatas = new ArrayList<StaffData>();
			}
			return staffDatas;
		}  catch (Exception e) {
			getLogger().error(MODULE,"Failed to fetch staff Datas. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	public static String getGroupIds(List<GroupData> groupDatas){
		StringBuilder groupIds = new StringBuilder(groupDatas.get(0).getId());
		for (int i = 1; i < groupDatas.size(); i++) {
			groupIds.append(",").append(groupDatas.get(i).getId());
		}

		return groupIds.toString();
	}

	public static String getStaffBelongingsGroup(StaffData staffData) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called getStaffBelogingsGroup()");
		}
		StringBuilder staffBelongingGroups =  new StringBuilder();
		try {
			Set<GroupData> staffGroups = staffData.getGroupDatas();
			for(GroupData groupData : staffGroups){
				String groupId = groupData.getId();
				staffBelongingGroups.append(groupId).append(",");
			}

			if(staffBelongingGroups.toString().contains(",")){
				staffBelongingGroups.deleteCharAt(staffBelongingGroups.lastIndexOf(","));
			}
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while taking staff-Group-role-actions. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return staffBelongingGroups.toString();
	}

	public static StaffACLChecker getStaffAclCheckerForSPROperations(StaffData staffData) {
		Map<ACLModules, Set<ACLAction>> moduleToActionMap = Maps.newHashMap();
		Map<String, Map<ACLModules, Set<ACLAction>>> groupIdToModuleActionsMap = Maps.newHashMap();

		for(StaffGroupRoleRelData staffGroupRoleRelData :  staffData.getStaffGroupRoleRelDataList()){
			List<RoleModuleActionData> roleModuleActionData = staffGroupRoleRelData.getRoleData().getRoleModuleActionData();
			for (RoleModuleActionData moduleActionData : roleModuleActionData) {
				if (moduleActionData.getAclModules() == null) {
					if(getLogger().isWarnLogLevel()){
						getLogger().warn(MODULE, "Invalid ACL module name found: " + moduleActionData.getModuleName() + " for staff: " + staffData.getUserName());
					}
				} else {
					moduleToActionMap.put(moduleActionData.getAclModules(), moduleActionData.getAclActions());
				}
			}
			groupIdToModuleActionsMap.put(staffGroupRoleRelData.getGroupData().getId(), moduleToActionMap);
		}
		return new StaffACLChecker(groupIdToModuleActionsMap,staffData.getGroupIdRoleDataMap());
	}

	public static StaffProfilePictureData getStaffProfilePictureBy(String id) throws Exception{
		return (StaffProfilePictureData) HibernateSessionFactory.getSession().get(StaffProfilePictureData.class, id);
	}

	public static StaffData prepareStaffDataFromSSO(KeycloakSecurityContext ssoSession, String loginUserName) {
		StaffData staffData = new StaffData();
		staffData.setId(ssoSession.getToken().getId());
		staffData.setUserName(loginUserName);
		if(StringUtils.isBlank(ssoSession.getToken().getName())){
			staffData.setName(loginUserName);
		}else{
			staffData.setName(ssoSession.getToken().getName());
		}
		staffData.setEmailAddress(ssoSession.getToken().getEmail());
		staffData.setPhone(ssoSession.getToken().getPhoneNumber());
		//TODO need to check for staff status
		staffData.setStatus(CommonStatus.ACTIVE.name());
		AccessToken.Access realmAccess = ssoSession.getToken().getRealmAccess();
		Set<String> roles = null;
		if (Objects.nonNull(realmAccess)) {
			roles = realmAccess.getRoles();
		}

		//creating user with default group
		if (CollectionUtils.isEmpty(roles)) {
			StaffGroupRoleRelData staffGroupRoleRelationWithDefaultGroup = createStaffGroupRoleRelationWithDefaultGroup(staffData);
			staffGroupRoleRelationWithDefaultGroup.setRoleData(getReadOnlyRole());
			staffData.getStaffGroupRoleRelDataList().add(staffGroupRoleRelationWithDefaultGroup);
		} else {
			for (String role : roles) {
				RoleData roleData = CRUDOperationUtil.findByName(RoleData.class, role);
				if (Objects.isNull(roleData)) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Role with name: " + role + " doesn't exist. So skipping this role mapping");
					}
					continue;
				}
				StaffGroupRoleRelData staffGroupRoleRelationWithDefaultGroup = createStaffGroupRoleRelationWithDefaultGroup(staffData);
				staffGroupRoleRelationWithDefaultGroup.setRoleData(roleData);
				staffData.getStaffGroupRoleRelDataList().add(staffGroupRoleRelationWithDefaultGroup);
			}
			if (CollectionUtils.isEmpty(staffData.getStaffGroupRoleRelDataList())) {
				StaffGroupRoleRelData staffGroupRoleRelData = createStaffGroupRoleRelationWithDefaultGroup(staffData);
				staffGroupRoleRelData.setRoleData(getReadOnlyRole());
				staffData.getStaffGroupRoleRelDataList().add(staffGroupRoleRelData);
			}
		}
		return staffData;
	}

	private static StaffGroupRoleRelData createStaffGroupRoleRelationWithDefaultGroup(StaffData staffData) {
		StaffGroupRoleRelData staffGroupRoleRelData = new StaffGroupRoleRelData();
		GroupData defaultGroupData = new GroupData();
		defaultGroupData.setId(CommonConstants.DEFAULT_GROUP_ID);
		staffGroupRoleRelData.setGroupData(defaultGroupData);
		staffGroupRoleRelData.setStaffData(staffData);
		return staffGroupRoleRelData;
	}
	private static RoleData getReadOnlyRole(){
		RoleData readOnly = new RoleData();
		readOnly.setId(NVSMXCommonConstants.READ_ONLY_ROLE_ID);
		return readOnly;
	}


}
