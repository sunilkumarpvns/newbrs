package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ApplicableBoDByBasePackagePredicate implements java.util.function.Predicate<BoDPackage> {


    private List<String> pccProfileNames;

    private ApplicableBoDByBasePackagePredicate(List<String> pccProfileNames) {
        this.pccProfileNames = pccProfileNames;
    }

    public static ApplicableBoDByBasePackagePredicate create(List<String> pccProfieNames) {
        return new ApplicableBoDByBasePackagePredicate(pccProfieNames);
    }

    @Override
    public boolean test(BoDPackage boDPackage) {
        if(CollectionUtils.isEmpty(pccProfileNames)){
            return false;
        }

        if (CollectionUtils.isEmpty(boDPackage.getApplicableQosProfiles())) {
            return true;
        }
        return pccProfileNames.stream().anyMatch(s -> boDPackage.getApplicableQosProfiles().contains(s));
    }


}
