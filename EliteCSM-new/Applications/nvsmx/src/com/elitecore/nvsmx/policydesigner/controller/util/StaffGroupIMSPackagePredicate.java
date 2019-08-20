package com.elitecore.nvsmx.policydesigner.controller.util;

import java.util.List;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

/**
 * Created by aditya on 5/22/17.
 */
public class StaffGroupIMSPackagePredicate implements Predicate<IMSPackage> {

   private final List<String> staffGroups;

    private StaffGroupIMSPackagePredicate(@Nonnull List<String> staffGroups) {
        this.staffGroups = staffGroups;
    }

    public static StaffGroupIMSPackagePredicate create(List<String> staffGroups){
     return new StaffGroupIMSPackagePredicate(staffGroups);
    }

    public boolean apply(IMSPackage imsPackage) {
        if(Collectionz.isNullOrEmpty(imsPackage.getGroupIds())){
            return true;
        }
        for(String userPackageGroupId : imsPackage.getGroupIds()){
            if(staffGroups.contains(userPackageGroupId)){
                return true;
            }
        }

        return false;
    }
}
