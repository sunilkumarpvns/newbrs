package com.elitecore.corenetvertex.pm.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ProductOfferPredicates {

    public static Predicate<ProductOfferData> createGroupFilter(List<String> groupIds){
        return productOfferData->{
            if (Strings.isNullOrBlank(productOfferData.getGroups())) {
                return true;
            }

            List<String> pkgGroupIds = CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups());

            for (String inputGroupId : groupIds) {
                if (pkgGroupIds.contains(inputGroupId)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<ProductOfferData> createNameFilter(String... names){
        return productOfferData-> Arrays.asList(names).contains(productOfferData.getName());
    }
}
