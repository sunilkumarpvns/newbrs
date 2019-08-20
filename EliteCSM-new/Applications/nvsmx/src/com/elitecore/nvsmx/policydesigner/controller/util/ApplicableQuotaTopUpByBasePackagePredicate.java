package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ApplicableQuotaTopUpByBasePackagePredicate implements java.util.function.Predicate<QuotaTopUp> {


    private List<String> pccProfileNames;

    private ApplicableQuotaTopUpByBasePackagePredicate(List<String> pccProfileNames) {
        this.pccProfileNames = pccProfileNames;
    }

    public static ApplicableQuotaTopUpByBasePackagePredicate create(List<String> pccProfieNames) {
        return new ApplicableQuotaTopUpByBasePackagePredicate(pccProfieNames);
    }

    @Override
    public boolean test(QuotaTopUp quotaTopUp) {
        if(CollectionUtils.isEmpty(pccProfileNames)){
            return false;
        }

        if (CollectionUtils.isEmpty(quotaTopUp.getApplicablePCCProfiles())) {
            return true;
        }
        return pccProfileNames.stream().anyMatch(s -> quotaTopUp.getApplicablePCCProfiles().contains(s));
    }


}
