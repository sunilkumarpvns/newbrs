package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by aditya on 4/7/17.
 */
public class GlobalPkgValidations {



    public static boolean isGlobalPlan(PkgData pkgData) {
        return PkgType.getGlobalPlan().contains(PkgType.valueOf(pkgData.getType()));
    }


    public static boolean isGroupWiseLimitReach(Class clazz, String id, String name, String mode,String pkgType,String groups, SessionProvider sessionProvider, List<String> subReasons) throws Exception {
        Map<String, List<ResourceData>> groupWisePkgs = ImportExportCRUDOperationUtil.fetchGroupWisePkgMap(clazz, id, name, mode, pkgType, sessionProvider);

        if(Maps.isNullOrEmpty(groupWisePkgs) == true){
            return false;
        }

        // check if package has only one group & that is Default Group then check limit for Default Group
        if (CommonConstants.DEFAULT_GROUP_ID.equalsIgnoreCase(groups) == true) {
            return checkLimitForGroups(groupWisePkgs, CommonConstants.DEFAULT_GROUP_MAX_GLOBAL_PKGS, subReasons,sessionProvider,CommonConstants.DEFAULT_GROUP_ID);

        }

        // check if package has multiple groups & one of the group is Default Group then check limit for Default Group
        final List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(groups);
        if (groupIds.contains(CommonConstants.DEFAULT_GROUP_ID) == true) {
            return checkLimitForGroups(groupWisePkgs, CommonConstants.DEFAULT_GROUP_MAX_GLOBAL_PKGS, subReasons,sessionProvider, CommonConstants.DEFAULT_GROUP_ID);

        }

        // check limit for individual group
        String[] groupIdArray = new String[groupIds.size()];
        return checkLimitForGroups(groupWisePkgs, CommonConstants.GROUP_WISE_MAX_GLOBAL_PKGS, subReasons,sessionProvider, groupIds.toArray(groupIdArray));
    }

    private static Boolean checkLimitForGroups(Map<String, List<ResourceData>> groupWisePkgs, int groupWiseLimit, List<String> subReasons, SessionProvider sessionProvider, String... groupIds) throws Exception {
        for(String groupId : groupIds){
            List<ResourceData> groupWiseList = groupWisePkgs.get(groupId);
            if (Collectionz.isNullOrEmpty(groupWiseList) == true) {
                continue;
            }
            if (groupWiseList.size() >= groupWiseLimit) {
                GroupData groupData = ImportExportCRUDOperationUtil.get(GroupData.class,groupId,sessionProvider);
                subReasons.add("Group Wise Limit has been reached for Group: " + groupData.getName());
                return true;
            }
        }
        return false;
    }
}
