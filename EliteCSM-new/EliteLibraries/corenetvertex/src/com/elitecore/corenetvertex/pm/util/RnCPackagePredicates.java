package com.elitecore.corenetvertex.pm.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class RnCPackagePredicates {

    public static Predicate<RncPackageData> createGroupFilter(List<String> groupIds){
        return rncPackageData->{
            if (Strings.isNullOrBlank(rncPackageData.getGroups())) {
                return true;
            }

            List<String> pkgGroupIds = CommonConstants.COMMA_SPLITTER.split(rncPackageData.getGroups());

            for (String inputGroupId : groupIds) {
                if (pkgGroupIds.contains(inputGroupId)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<RncPackageData> createNameFilter(String... names){
        return rncPackageData-> Arrays.asList(names).contains(rncPackageData.getName());
    }
}
