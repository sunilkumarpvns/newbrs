package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.pm.util.PackageValidator;


import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class PromotionalPackageFactory {

	private static final String MODULE = "PROMOTIONAL-FCTRY";
	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private PackageFactory packageFactory;
	private DeploymentMode deploymentMode;
	private QoSProfileFactory qoSProfileFactory;
	private UsageNotificationSchemeFactory usageNotificationSchemeFactory;
	
	public PromotionalPackageFactory(QoSProfileFactory qoSProfileFactory,
									 UsageNotificationSchemeFactory usageNotificationSchemeFactory,
									 PackageFactory packageFactory,
									 DeploymentMode deploymentMode) {
		
		this.qoSProfileFactory = qoSProfileFactory;
		this.usageNotificationSchemeFactory = usageNotificationSchemeFactory;
		this.packageFactory = packageFactory;
		this.deploymentMode = deploymentMode;
	}
	
	public Package createPromotionalPackage(PkgData pkgData, PkgStatus availabilityStatus, List<RatingGroupData> ratingGroups) {

        List<String> failReasons = new ArrayList<String>();
        List<String> partialFailReasons = new ArrayList<String>();
        FactoryUtils.setDefaultValues(pkgData);
        List<QosProfileData> qosProfileDatas = pkgData.getQosProfiles();
        List<QoSProfile> qosProfiles = new ArrayList<QoSProfile>();

		if (DeploymentMode.PCRF != deploymentMode) {
			failReasons.add("Promotional package is not supported with deployment mode: " + deploymentMode);
		}

        if (isNullOrEmpty(pkgData.getQosProfiles())) {
            failReasons.add("No qos profiles configured");
        } else {
            for (QosProfileData qosProfileData : qosProfileDatas) {
                List<String> qosProfileFailReasons = new ArrayList<String>();
				PackageValidator.validPCCProfileWithDeploymentMode(deploymentMode, qosProfileData, qosProfileFailReasons);
                List<String> qosProfilePartialFailReasons = new ArrayList<String>();
                QoSProfile qosProfile = qoSProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, ratingGroups);

                if (qosProfilePartialFailReasons.isEmpty() == false) {
                    partialFailReasons.add("QOS profile (" + qosProfileData.getName() + ") parsing partially fail. Cause by:" + FactoryUtils.format(qosProfilePartialFailReasons));
                }

                if (qosProfileFailReasons.isEmpty() == false) {
                    failReasons.add("QOS profile (" + qosProfileData.getName() + ") parsing fail. Cause by:" + FactoryUtils.format(qosProfileFailReasons));
                } else {
                    qosProfiles.add(qosProfile);
                }
            }
        }

        PkgMode mode = PkgMode.getMode(pkgData.getPackageMode());
        if (mode == null) {
            failReasons.add("Invalid package mode(" + pkgData.getPackageMode() + ") given for package: " + pkgData.getName());
        }

        com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf promotionalConf
                = new com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf(pkgData, pkgData.getId(), pkgData.getName()
                , mode
                , pkgData.getDescription()
                , pkgData.getPrice()
                , pkgData.getAvailabilityStartDate()
                , pkgData.getAvailabilityEndDate(), COMMA_BASE_SPLITTER.split(pkgData.getGroups()), pkgData.getParam1(), pkgData.getParam2());

        UsageNotificationScheme usageNotificationScheme = null;
        if (pkgData.getQuotaProfileType() != QuotaProfileType.SY_COUNTER_BASED) {
            //Adding notification scheme
            usageNotificationScheme = usageNotificationSchemeFactory.createUsageNotificationScheme(pkgData, partialFailReasons);
        } else {
            getLogger().debug(MODULE, "Skipping creation of notification scheme. Reason: Package quota profile type is sy counter based");
        }

        ValidityPeriodUnit validityPeriodUnit = pkgData.getValidityPeriodUnit();

        if (validityPeriodUnit == null) {
            validityPeriodUnit = ValidityPeriodUnit.MID_NIGHT;
        }

        if (partialFailReasons.isEmpty() == false) {
            promotionalConf.withPartialFailReason(partialFailReasons.toString());
        }

        if (failReasons.isEmpty() == false) {
            promotionalConf.withFailReason(failReasons.toString());
        }

        if (pkgData.getAlwaysPreferPromotionalQoS() != null && pkgData.getAlwaysPreferPromotionalQoS()) {
            promotionalConf.preferPromotionalQoS();
        }
        
        List<GroupManageOrder> pkgGroupOrderConfs = FactoryUtils.createGroupManageOrders(pkgData.getPkgGroupWiseOrders());

        promotionalConf.addQoSProfiles(qosProfiles)
                .addQuotaProfileType(pkgData.getQuotaProfileType())
                .withMultipleSubscription(pkgData.isMultipleSubscription())
                .withValidityPeriodUnit(validityPeriodUnit)
                .withAvailabilityStatus(availabilityStatus)
                .withUsageNotificationScheme(usageNotificationScheme)
        		.withPkgGroupOrderConfs(pkgGroupOrderConfs);

        return packageFactory.createPromotionalPackage(promotionalConf);
    }
}
