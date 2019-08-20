package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.sm.acl.RoleType;
import com.elitecore.nvsmx.policydesigner.controller.util.GroupInfoSelectionUtil;

import java.util.List;
import java.util.Map;

/**
 *A handler that will authorize the staff based on the roles and group defined. An action performed by a staff is validated for
 * staff belonging role and group.
 */
public class SingleOperationAuthorizedHandler{

    public static final String MODULE = "SINGLE_AUTH-HNDLR";
    private static SingleOperationAuthorizedHandler singleOperationAuthorizedHandler= new SingleOperationAuthorizedHandler();

    public  static SingleOperationAuthorizedHandler getHandler() {
        return singleOperationAuthorizedHandler;
    }

    /**
     * Return groupIds for which staff doesn't have access rights
     * @param groupIds
     * @param staffBelongingGroupIdRoleMap
     * @param moduleName
     * @param methodName
     * @return
     */
    public List<String> isAuthorizedForAllGroup(List<String> groupIds, Map<String, RoleData> staffBelongingGroupIdRoleMap, String moduleName, String methodName) {
        List<String> notAllowedGroups = Collectionz.newArrayList();
        for (String groupId : groupIds) {
            RoleData roleData = staffBelongingGroupIdRoleMap.get(groupId);
            if (roleData != null) {

                if(RoleType.ADMIN.name().equalsIgnoreCase(roleData.getRoleType())){
                    continue;
                }

                if(RoleType.READ_ONLY.name().equalsIgnoreCase(roleData.getRoleType())){
                    notAllowedGroups.add(groupId);
                }else {
                    List<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();

                    boolean isAllowed = false;
                    for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
                        if (roleModuleActionData.getModuleName().equalsIgnoreCase(moduleName) && roleModuleActionData.getActions().contains(methodName)) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (isAllowed == false) {
                        notAllowedGroups.add(groupId);
                    }
                }
            } else {
                notAllowedGroups.add(groupId);
            }
        }
        if(Collectionz.isNullOrEmpty(notAllowedGroups) == false){
            LogManager.getLogger().debug(MODULE, moduleName + " '" + methodName + "' not allowed for '" + notAllowedGroups + "' Group");
        }
        return notAllowedGroups;
    }

    /**
     * Return true if staff has access rights in any group from given groupIds.
     * ADMIN Role type will have all access rights.
     * READ_ONLY Role type will not have any access rights except View and Search
     * For other role types it required to check access rights
     * @param groupIds
     * @param staffBelongingGroupIdRoleMap
     * @param moduleName
     * @param methodName
     * @return
     */
    public boolean isAuthorizedForAnyGroup(List<String> groupIds, Map<String,RoleData> staffBelongingGroupIdRoleMap, String moduleName, String methodName) {
        for (String groupId : groupIds) {
            RoleData roleData = staffBelongingGroupIdRoleMap.get(groupId);

            if (roleData != null) {
                /* If roleType is Admin then staff will have all access rights. So, returning true. */
                if(roleData.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
                    return true;
                }
                /* If roleType is not READ_ONLY then check for access rights.
                   For READ_ONLY roleType no need to check access Rights. */
                if(!roleData.getRoleType().equals(RoleType.READ_ONLY.name())) {
                    List<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();
                    for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
                        if (roleModuleActionData.getModuleName().equalsIgnoreCase(moduleName) && roleModuleActionData.getActions().contains(methodName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return non belonging removed groups2
     * @param staffBelongingGroupDatas
     * @param entityGroups
     * @param oldGroups
     * @return
     */
    public List<String> checkOldGroups(List<GroupData> staffBelongingGroupDatas, List<String> entityGroups, List<String> oldGroups){

        List<String> notAllowedGroups = Collectionz.newArrayList();
        List<GroupInfo> groupInfoList = GroupInfoSelectionUtil.getCombinedGroupInfoList(staffBelongingGroupDatas, oldGroups);
        for (GroupInfo groupInfo : groupInfoList) {
            if (groupInfo.isLocked() == true && entityGroups.contains(groupInfo.getId()) == false) {
                notAllowedGroups.add(groupInfo.getId());
            }
        }
        return notAllowedGroups;
    }
}
