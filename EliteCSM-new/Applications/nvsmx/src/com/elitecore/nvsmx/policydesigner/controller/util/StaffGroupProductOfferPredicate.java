package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class StaffGroupProductOfferPredicate implements Predicate<ProductOffer> {

    private final List<String> staffGroups;

    private StaffGroupProductOfferPredicate(List<String> staffGroups) {
        this.staffGroups = staffGroups;
    }

    public static StaffGroupProductOfferPredicate create(List<String> staffGroups){
        return new StaffGroupProductOfferPredicate(staffGroups);
    }

    public boolean apply(ProductOffer productOffer) {
        if(Collectionz.isNullOrEmpty(productOffer.getGroups())){
            return true;
        }

        if (CollectionUtils.isEmpty(staffGroups)) {
            return true;
        }

        for(String userPackageGroupId : productOffer.getGroups()){
            if(staffGroups.contains(userPackageGroupId)){
                return true;
            }
        }

        return false;
    }
}
