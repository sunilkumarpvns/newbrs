package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class BoDFactory {
    public BoDPackage createBodPkg(String id, String name, String description, PkgMode packageMode, PkgStatus pkgStatus,
                                   Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit, List<String> applicableQosProfiles,
                                   Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers, List<String> groupIds,
                                   String failReason, PolicyStatus policyStatus, Double price, Timestamp availabilityStartDate,
                                   Timestamp availabilityEndDate, String param1, String param2){
        return new BoDPackage(id, name, description, packageMode, pkgStatus, validityPeriod, validityPeriodUnit
                , applicableQosProfiles, fupLevelToBoDQosMultipliers, groupIds, failReason, policyStatus, price,
                availabilityStartDate, availabilityEndDate, param1, param2);
    }
}