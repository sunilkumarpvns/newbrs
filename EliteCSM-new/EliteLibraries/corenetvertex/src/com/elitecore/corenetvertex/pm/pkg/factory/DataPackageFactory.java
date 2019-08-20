package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

public class DataPackageFactory {

	private static final String MODULE = "PKG-FCTRY";
	
	private AddOnFactory addOnFactory;
	private BasePackageFactory basePackageFactory;
	private EmergencyPackageFactory emergencyPackageFactory;
	private PromotionalPackageFactory promotionalPackageFactory;
	
	public DataPackageFactory(AddOnFactory addOnFactory, BasePackageFactory basePackageFactory, EmergencyPackageFactory emergencyPackageFactory
			, PromotionalPackageFactory promotionalPackageFactory, PackageFactory packageFactory) {
				
		this.addOnFactory = addOnFactory;
		this.basePackageFactory = basePackageFactory;
		this.emergencyPackageFactory = emergencyPackageFactory;
		this.promotionalPackageFactory = promotionalPackageFactory;
	}
	
	public List<Package> create(List<PkgData> pkgDatas, List<RatingGroupData> ratingGroups) {

		getLogger().info(MODULE, "Creating package from package data started");

		List<Package> packages = new ArrayList<Package>();
		Collections.sort(pkgDatas, new Comparator<PkgData>() {

			@Override
			public int compare(PkgData pkg1, PkgData pkg2) {

				if (pkg1.getOrderNumber() == null && pkg2.getOrderNumber() == null) {
					return 0;
				}

				if (pkg1.getOrderNumber() == null) {
					return -1;
				}

				if (pkg2.getOrderNumber() == null) {
					return 1;
				}

				return pkg1.getOrderNumber().compareTo(pkg2.getOrderNumber());
			}
		});

		for (PkgData pkgData : pkgDatas) {

			PkgStatus availabilityStatus;
			if (Strings.isNullOrBlank(pkgData.getStatus())) {

				getLogger().info(MODULE, "Considering default package availability status: " + PkgStatus.ACTIVE.val +
						". Reason: No status provided");
				availabilityStatus = PkgStatus.ACTIVE;
			} else {

				availabilityStatus = PkgStatus.fromVal(pkgData.getStatus());
				if (availabilityStatus == null) {

					getLogger().info(MODULE, "Considering default package availability status: " + PkgStatus.ACTIVE.val +
							". Reason: Invalid status(" + pkgData.getStatus() + ") configured");
					availabilityStatus = PkgStatus.ACTIVE;
				}
			}

			Package pkg = null;
			if (PkgType.ADDON.name().equalsIgnoreCase(pkgData.getType())) {

				pkg = addOnFactory.createAddOn(pkgData, availabilityStatus, ratingGroups);
			} else if (PkgType.BASE.name().equalsIgnoreCase(pkgData.getType())) {

				pkg = basePackageFactory.createBasePackage(pkgData, availabilityStatus, ratingGroups);
			} else if (PkgType.EMERGENCY.name().equalsIgnoreCase(pkgData.getType())) {

				pkg = emergencyPackageFactory.createEmergencyPackage(pkgData, availabilityStatus, ratingGroups);
			} else if (PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgData.getType())) {

				pkg = promotionalPackageFactory.createPromotionalPackage(pkgData, availabilityStatus, ratingGroups);
			} else {
				getLogger().warn(MODULE, "Unknown package type(" + pkgData.getType() + ") configured for package: " + pkgData.getName());
				continue;
			}

			if (Strings.isNullOrBlank(pkg.getFailReason())) {
				pkg.init();
				pkgData.setPartialFailReasons(pkg.getPartialFailReason());
				if (pkg.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Package(" + pkgData.getName() + ") parsing partially failed. " + pkg.getPartialFailReason());
					}
					pkgData.setPolicyStatus(PolicyStatus.PARTIAL_SUCCESS);
				}

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Package(" + pkg.getName() + ") parsed successfully");
				}
			}

			packages.add(pkg);
		}

		getLogger().info(MODULE, "Creating data packages from package data completed");

		return packages;
	}

}
