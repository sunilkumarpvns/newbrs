package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

public class StaffGroupRnCPackagePredicate{

    public static Predicate<RnCPackage> create(List<String> staffGroups) {
        return (rnCPackage)->{
            if(Collectionz.isNullOrEmpty(rnCPackage.getGroupIds())){
                return true;
            }

            if (CollectionUtils.isEmpty(staffGroups)) {
                return true;
            }

            for(String userPackageGroupId : rnCPackage.getGroupIds()){
                if(staffGroups.contains(userPackageGroupId)){
                    return true;
                }
            }

            return false;
        };
    }


}
