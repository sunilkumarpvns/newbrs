package com.elitecore.corenetvertex.pm.factory;

import java.util.Collections;
import java.util.List;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;

//FIXME- how to give basePAckage.init() method
public class QuotaTopUpPackageFactory {
    public static QuotaTopUp create(String id, String name, QuotaProfile quotaProfile) {

        return new QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder(null, id, name, quotaProfile).
                withAvailabilityStatus(PkgStatus.ACTIVE).withQuotaProfile(quotaProfile).build();
    }

    public static class QuotaTopUpPackageBuilder {

        private final String name;
        private final String id;
        private QuotaProfileType quotaProfileType;
        private PolicyStatus status;
        private String failReason;
        private String partialFailReason;
        private PkgStatus availabilityStatus;
        private QuotaProfile quotaProfile;
        private PkgData pkgData;
        private TopUpType topUpType;
        private QuotaNotificationScheme quotaNotificationScheme;
        private List<String> applicablePCCProfiles;

        public QuotaTopUpPackageBuilder(PkgData pkgData,String id,String name, QuotaProfile quotaProfile) {
            this.pkgData = pkgData;
            this.id = id;
            this.name = name;
            this.quotaProfile = quotaProfile;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withQuotaProfileType(QuotaProfileType type) {
            quotaProfileType = type;
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withAvailabilityStatus(PkgStatus availabilityStatus) {
            this.availabilityStatus = availabilityStatus;
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withFailReason(String failReason) {
            if (failReason != null) {
                status = PolicyStatus.FAILURE;
                this.failReason = failReason;
            }
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withPartialFailReason(String partialFailReason) {
            if (partialFailReason != null) {
                if (status != PolicyStatus.FAILURE) {
                    status = PolicyStatus.PARTIAL_SUCCESS;
                }
                this.partialFailReason = partialFailReason;
            }
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withPolicyStatus(PolicyStatus policyStatus) {
            this.status = policyStatus;
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withTopUpType(TopUpType topUpType) {
            this.topUpType = topUpType;
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withQuotaProfile(QuotaProfile quotaProfile) {
            this.quotaProfile = quotaProfile;
            return this;
        }

        public QuotaTopUpPackageFactory.QuotaTopUpPackageBuilder withQuotaNotificationScheme(QuotaNotificationScheme quotaNotificationScheme) {
            this.quotaNotificationScheme = quotaNotificationScheme;
            return this;
        }



        public QuotaTopUp build() {
            QuotaTopUp basePackage = new QuotaTopUp(id,
                    name,
                    false,
                    30,
                    ValidityPeriodUnit.DAY,
                    PkgMode.LIVE,
                    topUpType,
                    "description",
                    10d,
                    null,
                    null,
                    availabilityStatus,
                    Collections.emptyList(),
                    null,
                    null,
                    null,
                    status,
                    "param1",
                    "param2", quotaNotificationScheme,null,
					null,0L,null,0L,
					null, applicablePCCProfiles);

            return basePackage;
        }

    }
}
