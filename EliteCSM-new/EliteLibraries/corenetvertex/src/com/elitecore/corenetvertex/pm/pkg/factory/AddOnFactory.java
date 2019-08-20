package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
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
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.pm.util.PackageValidator;


import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class AddOnFactory {

	private static final String MODULE = "ADDON-FCTRY";
	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private QoSProfileFactory qoSProfileFactory;
	private UsageNotificationSchemeFactory usageNotificationSchemeFactory;
	private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
	private DeploymentMode deploymentMode;
	private PackageFactory packageFactory;
	
	public AddOnFactory(QoSProfileFactory qoSProfileFactory,
						UsageNotificationSchemeFactory usageNotificationSchemeFactory,
						PackageFactory packageFactory, QuotaNotificationSchemeFactory quotaNotificationSchemeFactory,
						DeploymentMode deploymentMode)  {
		
		this.qoSProfileFactory = qoSProfileFactory;
		this.usageNotificationSchemeFactory = usageNotificationSchemeFactory;
		this.packageFactory = packageFactory;
		this.quotaNotificationSchemeFactory = quotaNotificationSchemeFactory;
		this.deploymentMode = deploymentMode;
	}
	
	@Nullable
	public AddOn createAddOn(PkgData pkgData, PkgStatus availabilityStatus, List<RatingGroupData> ratingGroups) {

		List<String> failReasons = new ArrayList<String>();
		List<String> partialFailReasons = new ArrayList<String>();
		setDefaultValues(pkgData);
		List<QosProfileData> qosProfileDatas = pkgData.getQosProfiles();
		List<QoSProfile> qosProfiles = new ArrayList<QoSProfile>();

		if (PackageValidator.isValidQuotaProfileTypeWithDeploymentMode(deploymentMode, pkgData.getQuotaProfileType()) == false) {
			failReasons.add("Quota profile type: " + pkgData.getQuotaProfileType()
					+ " is not compatible with deployment mode: " + deploymentMode);
		}

		if (isNullOrEmpty(pkgData.getQosProfiles())) {
			failReasons.add("No PCC profiles configured");
		} else {
			for (QosProfileData qosProfileData : qosProfileDatas) {
				List<String> qosProfileFailReasons = new ArrayList<String>();
				PackageValidator.validPCCProfileWithDeploymentMode(deploymentMode, qosProfileData, qosProfileFailReasons);
				List<String> qosProfilePartialFailReasons = new ArrayList<String>();
				QoSProfile qosProfile = qoSProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, ratingGroups);

				if(qosProfilePartialFailReasons.isEmpty() == false) {
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

		com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf addOnConf = new com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf(pkgData, pkgData
				.getId(), pkgData.getName()
				, mode
				, pkgData.getDescription()
				, pkgData.getPrice()
				, pkgData.getAvailabilityStartDate()
				, pkgData.getAvailabilityEndDate(), COMMA_BASE_SPLITTER.split(pkgData.getGroups()), pkgData.getParam1(), pkgData.getParam2(),pkgData.getCurrency());

		UsageNotificationScheme usageNotificationScheme = null;

		QuotaNotificationScheme quotaNotificationScheme = null;
		if (pkgData.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
			usageNotificationScheme = usageNotificationSchemeFactory.createUsageNotificationScheme(pkgData, partialFailReasons);
		} else if (pkgData.getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
			quotaNotificationScheme = quotaNotificationSchemeFactory.createQuotaNotificationScheme(pkgData.getQuotaNotificationDatas(),
					qosProfiles, partialFailReasons);
		} else {
			getLogger().debug(MODULE, "Skipping creation of notification scheme. Reason: Package quota profile type is sy counter based");
		}

		ValidityPeriodUnit validityPeriodUnit = pkgData.getValidityPeriodUnit();

		if (validityPeriodUnit == null) {
			validityPeriodUnit = ValidityPeriodUnit.MID_NIGHT;
		}

		if (partialFailReasons.isEmpty() == false) {
			addOnConf.withPartialFailReason(partialFailReasons.toString());
		}

		if (failReasons.isEmpty() == false) {
			addOnConf.withFailReason(failReasons.toString());
		}

		if (pkgData.isExclusiveAddOn()) {
			addOnConf.exclusiveAddOn();
		}

		addOnConf.addQoSProfiles(qosProfiles)
				.addQuotaProfileType(pkgData.getQuotaProfileType())
				//.setValidityPeriod(pkgData.getValidityPeriod())
				.withMultipleSubscription(pkgData.isMultipleSubscription())
				.withValidityPeriodUnit(validityPeriodUnit)
				.withAvailabilityStatus(availabilityStatus)
				.withUsageNotificationScheme(usageNotificationScheme)
				.addQuotaNotificationScheme(quotaNotificationScheme);

		return packageFactory.createAddOn(addOnConf);
	}
	
	private static void setDefaultValues(PkgData pkgData) {
		if (pkgData.isExclusiveAddOn() == null) {
			pkgData.setExclusiveAddOn(false);
		}

		if (pkgData.isMultipleSubscription() == null) {
			pkgData.setMultipleSubscription(true);
		}

		if (pkgData.isReplaceableByAddOn() == null) {
			pkgData.setReplaceableByAddOn(true);
		}
	}
	
}
