package com.elitecore.nvsmx.system.util;


import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by jaidiptrivedi on 14/9/17.
 */
public class ResourceDataGroupPredicate implements Predicate<ResourceData> {

    private final List<String> groups;

    public ResourceDataGroupPredicate(List<String> groups) {
        this.groups = groups;
    }

    public static ResourceDataGroupPredicate create(List<String> groups){ //these are the groups against which we need to test entities
        return new ResourceDataGroupPredicate(groups);
    }


    @Override
    public boolean test(ResourceData resourceData) {
        List<String> resourceGroups = CommonConstants.COMMA_SPLITTER.split(resourceData.getGroups());
        if (Strings.isNullOrBlank(resourceData.getGroups())) {
            return true;
        }
        for (String groupId : this.groups) {
            if (resourceGroups.contains(groupId)) {
                return true;
            }
        }
        return false;
    }
}
