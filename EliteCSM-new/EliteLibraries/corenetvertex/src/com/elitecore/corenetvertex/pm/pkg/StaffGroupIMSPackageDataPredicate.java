package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;

import java.util.List;

import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;

/**
 * Created by aditya on 5/19/17.
 */
public class StaffGroupIMSPackageDataPredicate implements Predicate<IMSPkgData> {

    private final Predicate<ResourceData> predicate;

    private StaffGroupIMSPackageDataPredicate (List<String> staffGroups) {
        this.predicate = createStaffBelongingPredicate(staffGroups);
    }

    public static Predicate<IMSPkgData> create(List<String> staffBelongingGroupsIds){
        return new StaffGroupIMSPackageDataPredicate(staffBelongingGroupsIds);
    }

    @Override
    public boolean apply(IMSPkgData input) {
        return predicate.apply(input);
    }
}
