package com.elitecore.corenetvertex.pm.pkg;


import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.PkgData;

import java.util.List;

import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;


/**
 * Created by aditya on 5/18/17.
 */
public class  StaffGroupPackagePredicate implements Predicate<PkgData> {
    private final Predicate<ResourceData> predicate;

    private StaffGroupPackagePredicate (List<String> staffGroups) {
        this.predicate = createStaffBelongingPredicate(staffGroups);
    }
    public static Predicate<PkgData> create(List<String> staffBelongingGroupsIds){
        return new StaffGroupPackagePredicate(staffBelongingGroupsIds);
    }

    @Override
    public boolean apply(PkgData pkgData) {
        return predicate.apply(pkgData);
    }


}
