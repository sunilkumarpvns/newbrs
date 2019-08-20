package com.elitecore.corenetvertex.pm.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.bod.BoDData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BoDDataPredicates {
    public static Predicate<BoDData> createGroupFilter(List<String> groupIds){
        return boDData->{
            if (Strings.isNullOrBlank(boDData.getGroups())) {
                return true;
            }

            List<String> bodGroupIds = CommonConstants.COMMA_SPLITTER.split(boDData.getGroups());

            for (String inputGroupId : groupIds) {
                if (bodGroupIds.contains(inputGroupId)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<BoDData> createNameFilter(String... names){
        return boDData-> Arrays.asList(names).contains(boDData.getName());
    }
}
