package com.elitecore.netvertex.pm;


import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.factory.QoSProfileDetailFactory;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasePackageFactory {

    public static BasePackage create(String id, String name) {

        String quotaProfileId = UUID.randomUUID().toString();
        QuotaProfile quotaProfile = new QuotaProfileFactory.Builder(quotaProfileId).withHSQLevelRandomQuotaFor(CommonConstants.ALL_SERVICE_ID)
                .build();

        QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile()
                .hasQuotaProfileDetail(quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID))
                .forEachServicesHasPCCRule().build();

        QoSProfile qosProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).hasHSQLevelQoSProfileDetail(quotaProfileDetail)
                .build();

        return new BasePackageFactory.BasePackageBuilder(id, name).
                addQoSProfile(qosProfile).build();
    }


    /*

	public static BasePackage createReplacableByAddOnBasePackage(String name) {

		String quotaProfileId = UUID.randomUUID().toString();
		QuotaProfile quotaProfileDetail = new QuotaProfileFactory.Builder(quotaProfileId).withHSQLevelRandomQuotaFor(CommonConstants.ALL_SERVICE_ID)
				.build();

		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile()
				.hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID))
				.forEachServicesHasPCCRule().build();

		QoSProfile qosProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail)
				.build();

		return new BasePackage.BasePackageBuilder(quotaProfileId, name).
				addQoSProfile(qosProfile).replacableByAddOn().build();
	}
*/
    /*
	public static BasePackage createNotReplacableByAddOnBasePackage(String name) {

		String quotaProfileId = UUID.randomUUID().toString();
		QuotaProfile quotaProfileDetail = new QuotaProfileFactory.Builder(quotaProfileId).withHSQLevelRandomQuotaFor(CommonConstants.ALL_SERVICE_ID)
				.build();

		QoSProfileDetail quotaProfileDetail = QoSProfileDetailFactory.createQoSProfile()
				.hasQuotaProfileDetail(quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID))
				.forEachServicesHasPCCRule().build();

		QoSProfile qosProfile = QosProfileFactory.createQosProfile().quotaProfileDetail(quotaProfileDetail).hasHSQLevelQoSProfileDetail(quotaProfileDetail)
				.build();

		return new BasePackage.BasePackageBuilder(quotaProfileId, name).
				addQoSProfile(qosProfile).build();
	}
	*/
/*
	public static IPCANQoS findHigestQoS(Package basePackage) {

		IPCANQoS higestSessionQoS = null;
		for (QoSProfile qosProfile : basePackage.getQoSProfiles()) {
			QoSProfileDetail qoSProfileDetail = qosProfile.getHsqLevelQoSDetail();

			if (qoSProfileDetail.getAction() == QoSProfileAction.REJECT) {
				continue;
			}

			IPCANQoS ipcanQoS = qoSProfileDetail.getSessionQoS();

			if (higestSessionQoS == null) {
				higestSessionQoS = ipcanQoS;
			} else {
				if (higestSessionQoS.compareTo(ipcanQoS) < 0) {
					higestSessionQoS = ipcanQoS;
				}
			}

		}

		if (higestSessionQoS == null) {
			throw new RuntimeException(basePackage.getName() + " does not contain any QoS");
		}

		return higestSessionQoS;

	}
*/

    public static class BasePackageBuilder {

        private final String name;
        private final String id;
        private QuotaProfileType quotaProfileType;
        private List<QoSProfile> qosProfiles;
        private PolicyStatus status;
        private String failReason;
        private String partialFailReason;
        private PkgStatus availabilityStatus;
        private UsageNotificationScheme usageNotificationScheme;
        public BasePackageBuilder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public BasePackageBuilder addQoSProfile(QoSProfile qosProfile) {
            if(qosProfiles == null){
                qosProfiles = new ArrayList<QoSProfile>();
            }

            qosProfiles.add(qosProfile);

            return this;
        }

        public BasePackageBuilder withQuotaProfileType(QuotaProfileType type) {
            quotaProfileType = type;
            return this;
        }

        public BasePackageBuilder addQoSProfiles(List<QoSProfile> qosProfiles) {
            if(this.qosProfiles == null){
                this.qosProfiles = new ArrayList<QoSProfile>();
            }
            this.qosProfiles.addAll(qosProfiles);

            return this;
        }

        public BasePackageBuilder withAvailabilityStatus(PkgStatus availabilityStatus) {
            this.availabilityStatus = availabilityStatus;
            return this;
        }

        public BasePackageBuilder withUsageNotificationScheme(UsageNotificationScheme usageNotificationScheme) {
            this.usageNotificationScheme = usageNotificationScheme;
            return this;
        }

        public BasePackageBuilder withFailReason(String failReason) {
            if (failReason != null) {
                status = PolicyStatus.FAILURE;
                this.failReason = failReason;
            }
            return this;
        }

        public BasePackageBuilder withPartialFailReason(String partialFailReason) {
            if (partialFailReason != null) {
                if (status != PolicyStatus.FAILURE) {
                    status = PolicyStatus.PARTIAL_SUCCESS;
                }
                this.partialFailReason = partialFailReason;
            }
            return this;
        }

        public BasePackageBuilder withQoSProfiles(List<QoSProfile> qosProfiles) {
            this.qosProfiles = qosProfiles;
            return this;
        }

        public BasePackageBuilder withPolicyStatus(PolicyStatus policyStatus) {
            this.status = policyStatus;
            return this;
        }

        public com.elitecore.netvertex.pm.BasePackage build() {
            com.elitecore.netvertex.pm.BasePackage basePackage = new com.elitecore.netvertex.pm.BasePackage(id,
                    name,
                    quotaProfileType,
                    availabilityStatus,
                    qosProfiles,
                    usageNotificationScheme,
                    PkgMode.LIVE,
                    "description",
                    10d,
                    null,
                    null,
                    null,
                    status,
                    failReason,
                    partialFailReason,
                    "param1",
                    "param2", null,"INR");

            basePackage.init();

            return basePackage;
        }

    }

}
