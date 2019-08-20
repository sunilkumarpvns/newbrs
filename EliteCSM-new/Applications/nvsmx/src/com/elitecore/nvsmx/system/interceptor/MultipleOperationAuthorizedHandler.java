package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *A handler that will authorize the multiple actions performed by a staff. An action performed by a staff is validated for
 * staff belonging roles and groups.
 */
public class MultipleOperationAuthorizedHandler {

    private static MultipleOperationAuthorizedHandler multipleOperationAuthorizedHandler = new MultipleOperationAuthorizedHandler();

    public boolean doAuthorize(Map<String,List<String>> groups,String staffBelongingGroups, Map<String,RoleData> staffBelongingRoleMap,Set<GroupData> groupDataSet, String moduleName, String methodName,Map<String,String> notAllowedGroupNames) {
        for(Map.Entry<String,List<String>> group : groups.entrySet()) {

            boolean isMultiOperationAllowed = false;
            for (String commaSepGroupIds : group.getValue()) {

                String[] groupIdArray = commaSepGroupIds.split(",");


                for (String groupId : groupIdArray) {
                    groupId = groupId.trim();

                    if (Strings.isNullOrEmpty(groupId) == false && staffBelongingGroups.contains(groupId)) {

                        RoleData roleData = staffBelongingRoleMap.get(groupId);
                        if (roleData != null) {
                            setGroupIdAndName(notAllowedGroupNames, groupId, groupDataSet);
                            List<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();
                            for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
                                if (roleModuleActionData.getModuleName().equalsIgnoreCase(moduleName)
                                        && roleModuleActionData.getActions().contains(methodName)) {
                                    notAllowedGroupNames.remove(groupId);
                                    isMultiOperationAllowed = true;
                                }
                            }
                        }
                    }
                }
                if (isMultiOperationAllowed == false) {
                    return isMultiOperationAllowed;
                }

            }


        }

        return true;
    }



    public static MultipleOperationAuthorizedHandler getHandler() {
        return multipleOperationAuthorizedHandler;
    }
    protected void setGroupIdAndName(Map<String, String> notAllowedGroupNames, String groupId, Set<GroupData> groups) {
        for (GroupData group : groups) {
            if (group.getId().equalsIgnoreCase(groupId)) {
                notAllowedGroupNames.put(group.getId(), group.getName());
            }
        }
    }
}
