package com.elitecore.netvertexsm.web.servermgr.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.util.logger.Logger;

import java.util.List;
import java.util.Set;

/**
 * Created by jaidiptrivedi on 13/7/17.
 */
public class GroupInfoSelectionUtil {

    private static final String MODULE = "GROUP-INFO-UTIL";

    /**
     * Gives combined GroupInfoList with selected and locked attributes
     * @param staffBelongingGroup
     * @param entityGroup
     * @return
     */
    public static List<GroupInfo> getCombinedGroupInfoList(Set<GroupData> staffBelongingGroup, List<String> entityGroup) {
        Set<GroupData> combinedGroup = Collectionz.newHashSet();
        List<GroupInfo> groupInfoList = Collectionz.newArrayList();
        try {

            if (Collectionz.isNullOrEmpty(entityGroup) == false) {

                combinedGroup.addAll(GroupDataBLManager.getInstance().getGroupDataFromIds(entityGroup));

            } else {
                entityGroup = Collectionz.newArrayList();
            }

            combinedGroup.addAll(staffBelongingGroup);

            for (GroupData groupData : combinedGroup) {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setId(groupData.getId());
                groupInfo.setName(groupData.getName());

                if (entityGroup.contains(groupData.getId())) {
                    groupInfo.setSelected("selected");
                }
                if (staffBelongingGroup.contains(groupData) == false) {
                    groupInfo.setLocked(true);
                }
                groupInfoList.add(groupInfo);
            }
        } catch (DataManagerException e) {
            Logger.logError(MODULE,"Error while creating combined group info list");
            Logger.logTrace(MODULE,e);
        }
        return groupInfoList;
    }

}
