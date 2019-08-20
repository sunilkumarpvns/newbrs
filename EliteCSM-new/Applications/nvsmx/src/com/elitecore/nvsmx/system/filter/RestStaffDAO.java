package com.elitecore.nvsmx.system.filter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatus;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RestStaffDAO {
     private static final String MODULE = "REST-STAFF-DAO";

    public StaffData prepareStaffDataFromSSO(KeycloakSecurityContext ssoSession, String loginUserName, Session session) {
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
                RoleData roleData = getRoleData(role,session);
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

    private StaffGroupRoleRelData createStaffGroupRoleRelationWithDefaultGroup(StaffData staffData) {
        StaffGroupRoleRelData staffGroupRoleRelData = new StaffGroupRoleRelData();
        GroupData defaultGroupData = new GroupData();
        defaultGroupData.setId(CommonConstants.DEFAULT_GROUP_ID);
        staffGroupRoleRelData.setGroupData(defaultGroupData);
        staffGroupRoleRelData.setStaffData(staffData);
        return staffGroupRoleRelData;
    }
    private  RoleData getReadOnlyRole(){
        RoleData readOnly = new RoleData();
        readOnly.setId(NVSMXCommonConstants.READ_ONLY_ROLE_ID);
        return readOnly;
    }
    private static RoleData getRoleData(String roleName,Session session){
        Criteria nameCriteria = session.createCriteria(RoleData.class);
        nameCriteria.add(Restrictions.eq("name", roleName));
        List<RoleData> list = nameCriteria.list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public StaffData getStaffData(String userName) {
        Session session = null;
        try {
            session = createNewSession();
            Criteria criteria = session.createCriteria(StaffData.class).add(Restrictions.eq("userName", userName));
            List list = criteria.list();

            if (Collectionz.isNullOrEmpty(list) == false) {
                return (StaffData) list.get(0);
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to fetch staff data by userName: " + userName);
            getLogger().trace(MODULE, e);
        }finally {
            HibernateSessionUtil.closeSession(session);
        }
        return null;
    }

    public void setGroupsForSuperAdmin(HttpServletRequest request) {
        Session session = null;
        try {
            session = createNewSession();
            Criteria criteria = session.createCriteria(GroupData.class);
            List<GroupData> allGroups = criteria.list();
            request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getGroupIds(allGroups));
            request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP, allGroups);

        }catch (Exception e ){
            getLogger().error(MODULE, "Unable to get list of groups");
            getLogger().trace(MODULE, e);
        }finally {
            HibernateSessionUtil.closeSession(session);
        }

    }

    public StaffData prepareAndSaveStaffData(KeycloakSecurityContext ssoSession, String userName) {
        StaffData createdStaff = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = createNewSession();
            transaction = session.getTransaction();
            if(transaction.isActive() == false){
                transaction = session.beginTransaction();
            }
            createdStaff = prepareStaffDataFromSSO(ssoSession, userName,session);
            session.save(createdStaff);
            HibernateSessionUtil.commitTransaction(transaction);
        } catch (Exception e) {
            HibernateSessionUtil.rollBackTransaction(transaction);
            getLogger().error(MODULE, "Error while creating staff for user name:" + userName);
            getLogger().trace(MODULE, e);
        } finally {
            HibernateSessionUtil.closeSession(session);
        }
        return createdStaff;
    }

    private Session createNewSession(){
        Session session = HibernateSessionFactory.getNewSession();
        if (session.getTransaction().isActive() == false) {
            session.beginTransaction();
        }
        return session;
    }

    public void setGroupsForOtherUser(HttpServletRequest request, StaffData staffData) {
        request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP,new ArrayList<>(staffData.getGroupDatas()));
        request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getStaffBelongingsGroup(staffData));
        request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, staffData.getGroupIdRoleDataMap());
        request.getSession().setAttribute(Attributes.STAFF_ROLES_SET, staffData.getRoleDatas());
    }
}
