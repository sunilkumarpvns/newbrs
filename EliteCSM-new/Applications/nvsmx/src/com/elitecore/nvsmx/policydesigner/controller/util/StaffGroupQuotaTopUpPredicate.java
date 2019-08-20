package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class StaffGroupQuotaTopUpPredicate  implements Predicate<QuotaTopUp> {

    private final List<String> staffGroups;

    private StaffGroupQuotaTopUpPredicate(List<String> staffGroups) {
        this.staffGroups = staffGroups;
    }

    public static StaffGroupQuotaTopUpPredicate create(@Nonnull List<String> staffGroups){
        Objects.requireNonNull(staffGroups, "Staff Group should not be null");
        return new StaffGroupQuotaTopUpPredicate(staffGroups);
    }

    public boolean apply(QuotaTopUp quotaTopUp) {
        if(Collectionz.isNullOrEmpty(quotaTopUp.getGroupIds())){
            return true;
        }
        for(String groupId : quotaTopUp.getGroupIds()){
            if(staffGroups.contains(groupId)){
                return true;
            }
        }

        return false;
    }
}
