package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.pm.util.PackageValidator;


import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class BasePackageFactory {

	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private static final String MODULE = "BASE-PKG-FCTRY";
	private PackageFactory packageFactory;
	private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
	private DeploymentMode deploymentMode;
	private QoSProfileFactory qoSProfileFactory;
	private UsageNotificationSchemeFactory usageNotificationSchemeFactory;

	public BasePackageFactory(QoSProfileFactory qoSProfileFactory,
							  UsageNotificationSchemeFactory usageNotificationSchemeFactory,
							  PackageFactory packageFactory,
							  QuotaNotificationSchemeFactory quotaNotificationSchemeFactory,
							  DeploymentMode deploymentMode) {

		this.qoSProfileFactory = qoSProfileFactory;
		this.usageNotificationSchemeFactory = usageNotificationSchemeFactory;
		this.packageFactory = packageFactory;
		this.quotaNotificationSchemeFactory = quotaNotificationSchemeFactory;
		this.deploymentMode = deploymentMode;
	}
	
	@Nullable public BasePackage createBasePackage(PkgData pkgData, PkgStatus availabilityStatus, List<RatingGroupData> ratingGroups) {

		FactoryUtils.setDefaultValues(pkgData);
		List<String> packageFailReasons = new ArrayList<String>();
		List<String> partialFailReasons = new ArrayList<String>();
		List<QosProfileData> qosProfileDatas = pkgData.getQosProfiles();
		List<QoSProfile> qosProfiles = new ArrayList<QoSProfile>();

		if (PackageValidator.isValidQuotaProfileTypeWithDeploymentMode(deploymentMode, pkgData.getQuotaProfileType()) == false) {
			packageFailReasons.add("Quota profile type: " + pkgData.getQuotaProfileType()
					+ " is not compatible with deployment mode: " + deploymentMode);
		}

		if (isNullOrEmpty(pkgData.getQosProfiles())) {
			packageFailReasons.add("No PCC profiles configured");
		} else {

			for (QosProfileData qosProfileData : qosProfileDatas) {

				List<String> qosProfileFailReasons = new ArrayList<String>();
				PackageValidator.validPCCProfileWithDeploymentMode(deploymentMode, qosProfileData, qosProfileFailReasons);
				List<String> qosProfilePartialFailReasons = new ArrayList<String>();
				QoSProfile qosProfile = qoSProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, ratingGroups);

				if(qosProfilePartialFailReasons.isEmpty() == false) {
					partialFailReasons.add("PCC profile(" + qosProfileData.getName() + ") parsing partially fail. Cause by:" + FactoryUtils.format(qosProfilePartialFailReasons));
				}

				if (qosProfileFailReasons.isEmpty() == false) {
					packageFailReasons.add("PCC profile(" + qosProfileData.getName() + ") parsing fail. Cause by:" + FactoryUtils.format(qosProfileFailReasons));
				} else {
					qosProfiles.add(qosProfile);
				}



			}
		}

		PkgMode mode = PkgMode.getMode(pkgData.getPackageMode());
		if (mode == null) {
			packageFailReasons.add("Invalid package mode(" + pkgData.getPackageMode() + ") given for package: " + pkgData.getName());
		}

		UsageNotificationScheme usageNotificationScheme = null;
		com.elitecore.corenetvertex.pm.pkg.datapackage.conf.BasePackageConf basePackageConf = new com.elitecore.corenetvertex.pm.pkg.datapackage.conf.BasePackageConf(pkgData, pkgData
				.getId(), pkgData.getName()
				, mode
				, pkgData.getDescription()
				, pkgData.getPrice()
				, pkgData.getAvailabilityStartDate()
				, pkgData.getAvailabilityEndDate()
				, COMMA_BASE_SPLITTER.split(pkgData.getGroups()), pkgData.getParam1(), pkgData.getParam2(),pkgData.getCurrency());

		QuotaNotificationScheme quotaNotificationScheme = null;
		if (pkgData.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
			usageNotificationScheme = usageNotificationSchemeFactory.createUsageNotificationScheme(pkgData, partialFailReasons);
		} else if (pkgData.getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
			quotaNotificationScheme = quotaNotificationSchemeFactory.createQuotaNotificationScheme(pkgData.getQuotaNotificationDatas(),
					qosProfiles, partialFailReasons);
		} else {
			getLogger().debug(MODULE, "Skipping creation of notification scheme. Reason: Package quota profile type is sy counter based");
		}

		if (partialFailReasons.isEmpty() == false) {
			basePackageConf.addPartialFailReason(partialFailReasons.toString());
		}

		if (packageFailReasons.isEmpty() == false) {
			basePackageConf.addFailReason(packageFailReasons.toString());
		}

		basePackageConf.addQuotaProfileType(pkgData.getQuotaProfileType())
				.addAvailabilityStatus(availabilityStatus)
				.withQoSProfiles(qosProfiles)
				.addUsageNotificationScheme(usageNotificationScheme)
				.addQuotaNotificationScheme(quotaNotificationScheme);

		return packageFactory.createBasePackage(basePackageConf);

	}

}
