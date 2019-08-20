package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

public class StaffGroupBoDPackagePredicate{

    public static Predicate<BoDPackage> create(List<String> staffGroups) {
        return (boDPkg)->{
            if(Collectionz.isNullOrEmpty(boDPkg.getGroupIds())){
                return true;
            }
            if (CollectionUtils.isEmpty(staffGroups)) {
                return true;
            }
            for(String bodPkgGroupId : boDPkg.getGroupIds()){
                if(staffGroups.contains(bodPkgGroupId)){
                    return true;
                }
            }
            return false;
        };
    }


}
