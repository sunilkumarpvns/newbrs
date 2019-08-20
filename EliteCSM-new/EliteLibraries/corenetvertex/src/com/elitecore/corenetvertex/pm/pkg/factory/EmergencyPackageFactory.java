package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;

public class EmergencyPackageFactory {

	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private PackageFactory packageFactory;
	private QoSProfileFactory qoSProfileFactory;
	
	public EmergencyPackageFactory(QoSProfileFactory qoSProfileFactory, PackageFactory packageFactory) {

		this.qoSProfileFactory = qoSProfileFactory;
		this.packageFactory = packageFactory;
	}
	
	public EmergencyPackage createEmergencyPackage(PkgData pkgData, PkgStatus availabilityStatus, List<RatingGroupData> ratingGroups) {

		List<String> packageFailReasons = new ArrayList<>();
		List<String> packagePartialFaileReasons = new ArrayList<>();
		List<QosProfileData> qosProfileDatas = pkgData.getQosProfiles();
		List<QoSProfile> qosProfiles = new ArrayList<>();

		if (isNullOrEmpty(pkgData.getQuotaProfiles()) == false) {
			packageFailReasons.add("Emergency package must not contain any quota profile");
		}

		if (isNullOrEmpty(pkgData.getQosProfiles())) {
			packageFailReasons.add("No qos profiles configured");
		} else {

			for (QosProfileData qosProfileData : qosProfileDatas) {

				List<String> qosProfileFailReasons = new ArrayList<>();
				List<String> qosProfilePartialFailReasons = new ArrayList<>();
				QoSProfile qosProfile = qoSProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, ratingGroups);

				if(qosProfilePartialFailReasons.isEmpty() == false) {
					packagePartialFaileReasons.add("QoS profile(" + qosProfileData.getName() + ") parsing partially fail. Cause by:" + FactoryUtils.format(qosProfilePartialFailReasons));
				}

				if (qosProfileFailReasons.isEmpty() == false) {
					packageFailReasons.add("QoS profile(" + qosProfileData.getName() + ") parsing fail. Cause by:" + FactoryUtils.format(qosProfileFailReasons));
				} else {
					qosProfiles.add(qosProfile);
				}
			}
		}

		if (pkgData.getAvailabilityStartDate() == null) {
			packageFailReasons.add("Emergency package must contain availability start date");
		}

		PolicyStatus status = PolicyStatus.SUCCESS;
		if (isNullOrEmpty(packageFailReasons) == false) {
			status = PolicyStatus.FAILURE;
		}

		PkgMode mode = PkgMode.getMode(pkgData.getPackageMode());
		if (mode == null) {
			packageFailReasons.add("Invalid package mode(" + pkgData.getPackageMode() + ") given for package: " + pkgData.getName());
		}

		List<GroupManageOrder> groupManageOrders = FactoryUtils.createGroupManageOrders(pkgData.getPkgGroupWiseOrders());

		return packageFactory.createEmergencyPackage(pkgData,pkgData.getId(), pkgData.getName(), availabilityStatus, qosProfiles
				, mode
				, pkgData.getDescription()
				, pkgData.getPrice()
				, pkgData.getAvailabilityStartDate(), pkgData.getAvailabilityEndDate(), COMMA_BASE_SPLITTER.split(pkgData.getGroups()), status
				, isNullOrEmpty(packageFailReasons) ? null : packageFailReasons.toString()
				, isNullOrEmpty(packagePartialFaileReasons) ? null : packagePartialFaileReasons.toString()
				, pkgData.getParam1(), pkgData.getParam2()
				, groupManageOrders);

	}
	
}
