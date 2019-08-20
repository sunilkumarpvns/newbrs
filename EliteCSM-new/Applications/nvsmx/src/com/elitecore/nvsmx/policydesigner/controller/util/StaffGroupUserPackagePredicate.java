package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by aditya on 5/22/17.
 */
public class StaffGroupUserPackagePredicate implements Predicate<UserPackage> {

    private final List<String> staffGroups;

    private StaffGroupUserPackagePredicate(List<String> staffGroups) {
        this.staffGroups = staffGroups;
    }

    public static StaffGroupUserPackagePredicate create(@Nonnull List<String> staffGroups){
        return new StaffGroupUserPackagePredicate(staffGroups);
    }

    public boolean apply(UserPackage userPackage) {
        if(Collectionz.isNullOrEmpty(userPackage.getGroupIds())){
            return true;
        }

        if (CollectionUtils.isEmpty(staffGroups)) {
            return true;
        }

        for(String userPackageGroupId : userPackage.getGroupIds()){
            if(staffGroups.contains(userPackageGroupId)){
                return true;
            }
        }

        return false;
    }
}
