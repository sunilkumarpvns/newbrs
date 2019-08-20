package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;

import java.util.List;
import java.util.Set;

/**
 * Created by jaidiptrivedi on 13/7/17.
 */
public class GroupInfoSelectionUtil {

    /**
     * Gives combined GroupInfoList with selected and locked attributes
     * @param staffBelongingGroup
     * @param entityGroup
     * @return
     */
    public static List<GroupInfo> getCombinedGroupInfoList(List<GroupData> staffBelongingGroup, List<String> entityGroup) {

        Set<GroupData> combinedGroup = Collectionz.newHashSet();
        List<GroupInfo> groupInfoList = Collectionz.newArrayList();

        if(Collectionz.isNullOrEmpty(entityGroup) == false){
            combinedGroup.addAll(GroupDAO.getGroups(entityGroup));
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

        return groupInfoList;
    }

}
