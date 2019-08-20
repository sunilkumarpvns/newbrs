package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;

import java.util.List;

/**
 * Created by aditya on 5/19/17.
 */
public class ResourceDataPredicates {


    public static StaffBelongingPredicate createStaffBelongingPredicate(List<String> staff) {
        return new StaffBelongingPredicate(staff);
    }

    private static class StaffBelongingPredicate implements Predicate<ResourceData> {

        private final List<String> staffGroups;

        private StaffBelongingPredicate(List<String> staffGroups) {
            this.staffGroups = staffGroups;
        }

        @Override
        public boolean apply(ResourceData pkgData) {
            if(Strings.isNullOrBlank(pkgData.getGroups())){
                return true;
            }

            List<String> pkgGroupIds = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());

            for(String pkgGroupId : pkgGroupIds) {
                if(staffGroups.contains(pkgGroupId)){
                    return true;
                }
            }
            return false;
        }
    }
}
