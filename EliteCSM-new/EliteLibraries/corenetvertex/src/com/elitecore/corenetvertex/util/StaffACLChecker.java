package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * StaffACLChecker manages group name to module actions
 *
 * @author Chetan.Sankhala
 */
public class StaffACLChecker {

    private static final String MODULE = "STAFF-ACL-CHCKR";
    private Map<String, Map<ACLModules, Set<ACLAction>>> groupIdToModuleActions;
    private Map<String, RoleData> groupIdRoleDataMap;
    private static EnumSet<ACLAction> subscriberViewActions = EnumSet.of(ACLAction.VIEW_SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION, ACLAction.VIEW_DELETED_SUBSCRIBER);

    public StaffACLChecker(Map<String, Map<ACLModules, Set<ACLAction>>> groupIdToModuleActions, Map<String, RoleData> groupIdRoleDataMap) {
        this.groupIdToModuleActions = groupIdToModuleActions;
        this.groupIdRoleDataMap = groupIdRoleDataMap;
    }


    /**
     * Checks requested action is allowed for this staff
     *
     * @param groupIds,           belongs to requestedModule
     * @param requestedACLModule, belongs to requestedACLAction
     * @param requestedACLAction, permission check required for this action
     * @return true if requestedACLAction is accessible for this staff, otherwise false
     */
    public boolean isAccessibleAction(List<String> groupIds, ACLModules requestedACLModule, ACLAction requestedACLAction) {

        if (MapUtils.isEmpty(groupIdToModuleActions)) {
            return false;
        }
        RequestedACLPredicate requestAclPredicate = new RequestedACLPredicate(requestedACLAction, requestedACLModule);
        List<String> notAllowedGroups = new ArrayList<>();
        for (String groupId : groupIds) {
            if (requestAclPredicate.test(groupId) == false) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Requested action "+requestedACLAction.getVal()+" is not allowed for module "+requestedACLModule.getDisplayLabel()+" in group "+groupId);
                }
                notAllowedGroups.add(groupId);
            }
        }
        return CollectionUtils.isEmpty(notAllowedGroups);
    }

    private class RequestedACLPredicate implements Predicate<String> {

        private ACLAction requestedACLAction;
        private ACLModules requestedACLModule;

        private RequestedACLPredicate(ACLAction requestedACLAction, ACLModules requestedACLModule) {
            this.requestedACLAction = requestedACLAction;
            this.requestedACLModule = requestedACLModule;
        }

        @Override
        public boolean test(String groupId) {
            RoleData roleData = groupIdRoleDataMap.get(groupId);
            if (RoleType.ADMIN.name().equalsIgnoreCase(roleData.getRoleType())) {
                return true;
            }

            if (RoleType.READ_ONLY.name().equalsIgnoreCase(roleData.getRoleType())) {
                if (subscriberViewActions.contains(requestedACLAction)) {
                    return true;
                }
                return false;
            }
            Map<ACLModules, Set<ACLAction>> moduleToActionsMap = groupIdToModuleActions.get(groupId);
            if (MapUtils.isEmpty(moduleToActionsMap)) {
                return false;
            }
            Set<ACLAction> allowedACLActions = moduleToActionsMap.get(requestedACLModule);
            if (CollectionUtils.isEmpty(allowedACLActions)) {
                return false;
            }
            return allowedACLActions.contains(requestedACLAction);
        }
    }

}
