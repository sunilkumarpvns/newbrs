package com.elitecore.netvertex.rnc;

import java.util.Map;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

public abstract class ReportedQuotaProcessor {

    private PolicyRepository policyRepository;

    public ReportedQuotaProcessor(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    protected RnCQuotaProfileDetail getQuotaProfileDetailAndSetPackageDetail(NonMonetoryBalance balance, ReportedUsageSummary reportedUsageSummary) {

        reportedUsageSummary.setSubscriptionId(balance.getSubscriptionId());
        QuotaProfile selectedQuotaProfile = getQuotaProfileAndSetPackageDetail(balance, reportedUsageSummary);

        if (selectedQuotaProfile == null) {
            return null;
        }

        reportedUsageSummary.setQuotaProfile(selectedQuotaProfile.getId(), selectedQuotaProfile.getName());

        Map<String, QuotaProfileDetail> quotaProfileDetails = selectedQuotaProfile.getServiceWiseQuotaProfileDetails(balance.getLevel());
        for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
            RnCQuotaProfileDetail rncQuotaProfileDetail = (RnCQuotaProfileDetail) quotaProfileDetail;
            if ((rncQuotaProfileDetail.getDataServiceType().getServiceIdentifier() == balance.getServiceId())
                    && rncQuotaProfileDetail.getRatingGroup().getIdentifier() == balance.getRatingGroupId()) {
                reportedUsageSummary.setLevel(rncQuotaProfileDetail.getFupLevel());
                reportedUsageSummary.setRate(rncQuotaProfileDetail.getRate());
                return rncQuotaProfileDetail;
            }
        }

        return null;
    }

    private QuotaProfile getQuotaProfileAndSetPackageDetail(NonMonetoryBalance balance, ReportedUsageSummary reportedUsageSummary) {
        UserPackage dataPackage = policyRepository.getPkgDataById(balance.getPackageId());

        if (dataPackage == null) {
            QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(balance.getPackageId());
            if (quotaTopUp == null) {
                return null;
            }

            reportedUsageSummary.setPackageId(quotaTopUp.getId(), quotaTopUp.getName());

            //ASK whether needed to check quota profile id
            return quotaTopUp.getQuotaProfile();
        } else {
            reportedUsageSummary.setPackageId(dataPackage.getId(), dataPackage.getName());
            return dataPackage.getQuotaProfile(balance.getQuotaProfileId());
        }
    }

    public PolicyRepository getPolicyRepository() {
        return policyRepository;
    }


    public abstract void handle();

    public MSCC getUnAccountedUsage() {
        return null;
    }

    public abstract ReportedUsageSummary getReportedUsageSummary();
}
