package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

public class IMSPackageFactory {

	private static final String MODULE = "IMS-PKG-FCTRY";
	private IMSBasePackageFactory imsBasePackageFactory;

	public IMSPackageFactory(IMSBasePackageFactory imsBasePackageFactory) {
		this.imsBasePackageFactory = imsBasePackageFactory;
	}
	
	public List<IMSPackage> create(List<IMSPkgData> imsPkgDatas) {

        if (getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Creating ims package from ims package data started");

        List<IMSPackage> imsPackages = new ArrayList<IMSPackage>();
        for (IMSPkgData imsPkgData : imsPkgDatas) {

            PkgStatus availabilityStatus;
            if (Strings.isNullOrBlank(imsPkgData.getStatus())) {
                if (getLogger().isInfoLogLevel())
                    getLogger().info(MODULE, "Considering default package availability status: " + PkgStatus.ACTIVE.val +
                            ". Reason: No status provided");
                availabilityStatus = PkgStatus.ACTIVE;
            } else {

                availabilityStatus = PkgStatus.fromVal(imsPkgData.getStatus());
                if (availabilityStatus == null) {
                    if (getLogger().isInfoLogLevel())
                        getLogger().info(MODULE, "Considering default package availability status: " + PkgStatus.ACTIVE.val +
                                ". Reason: Invalid status(" + imsPkgData.getStatus() + ") configured");
                    availabilityStatus = PkgStatus.ACTIVE;
                }
            }

            if (PkgType.BASE.val.equalsIgnoreCase(imsPkgData.getType())) {

                IMSPackage imsPackage;

                List<String> imsPackageFailReasons = new ArrayList<String>();

                imsPackage = imsBasePackageFactory.create(imsPkgData, imsPackageFailReasons, availabilityStatus);

                if (imsPackageFailReasons.isEmpty()) {

                    if (getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, "IMS package(" + imsPkgData.getName() + ") parsed successfully");
                    }
                }

                imsPackages.add(imsPackage);
            } else {
                getLogger().warn(MODULE, "Skip " + imsPkgData.getName() + ". Reason:Invalid packege type:" + imsPkgData.getType());
            }

        }

        getLogger().info(MODULE, "Creating ims package from package data completed");

        return imsPackages;
    
	}

}
