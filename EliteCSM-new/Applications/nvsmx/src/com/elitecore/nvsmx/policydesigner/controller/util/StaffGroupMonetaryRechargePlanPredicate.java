package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class StaffGroupMonetaryRechargePlanPredicate implements Predicate<MonetaryRechargePlan> {

    private final List<String> staffGroups;

    private StaffGroupMonetaryRechargePlanPredicate(List<String> staffGroups) {
        this.staffGroups = staffGroups;
    }

    public static StaffGroupMonetaryRechargePlanPredicate create(@Nonnull List<String> staffGroups){
        Objects.requireNonNull(staffGroups, "Staff Group should not be null");
        return new StaffGroupMonetaryRechargePlanPredicate(staffGroups);
    }

    public boolean apply(MonetaryRechargePlan monetaryRechargePlan) {
        if(Collectionz.isNullOrEmpty(monetaryRechargePlan.getGroupIds())){
            return true;
        }
        for(String groupId : monetaryRechargePlan.getGroupIds()){
            if(staffGroups.contains(groupId)){
                return true;
            }
        }

        return false;
    }
}
