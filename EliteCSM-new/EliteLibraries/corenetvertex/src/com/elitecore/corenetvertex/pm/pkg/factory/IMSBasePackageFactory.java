package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable;


import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;

public class IMSBasePackageFactory {

    private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private IMSServiceTableFactory imsServiceTableFactory;
	private PackageFactory packageFactory;
	
	public IMSBasePackageFactory(PackageFactory packageFactory, IMSServiceTableFactory imsServiceTableFactory) {
	
		this.packageFactory = packageFactory;
		this.imsServiceTableFactory = imsServiceTableFactory;
	}
	
	public IMSPackage create(IMSPkgData pkgData, List<String> packageFailReasons, PkgStatus availabilityStatus) {

        List<IMSPkgServiceData> imsPkgServiceDatas = pkgData.getImsPkgServiceDatas();
        Map<Long, List<IMSServiceTable>> serviceNameToServiceTables = new HashMap<Long, List<IMSServiceTable>>();
        if (isNullOrEmpty(imsPkgServiceDatas)) {
            packageFailReasons.add("No service configured");
        } else {

            for (IMSPkgServiceData imsPkgData : imsPkgServiceDatas) {

                List<String> serviceDataFailReasons = new ArrayList<String>();
                IMSServiceTable serviceTable = imsServiceTableFactory.create(imsPkgData, serviceDataFailReasons);

                if (serviceDataFailReasons.isEmpty() == false) {
                    packageFailReasons.add("Service data (" + imsPkgData.getName() + ") parsing fail. Cause by:" + FactoryUtils.format(serviceDataFailReasons));
                } else {

                    List<IMSServiceTable> serviceTables = serviceNameToServiceTables.get(imsPkgData.getMediaTypeData().getMediaIdentifier());
                    if (serviceTables == null) {
                        serviceTables = new ArrayList<IMSServiceTable>();
                        serviceNameToServiceTables.put(imsPkgData.getMediaTypeData().getMediaIdentifier(), serviceTables);
                    }
                    serviceTables.add(serviceTable);
                }
            }
        }

        PkgMode mode = PkgMode.getMode(pkgData.getPackageMode());
        if (mode == null) {
            packageFailReasons.add("Invalid package mode(" + pkgData.getPackageMode() + ") given for package: " + pkgData.getName());
        }

        PkgStatus status = PkgStatus.valueOf(pkgData.getStatus());
        if (status == null) {
            packageFailReasons.add("Invalid availability status(" + pkgData.getStatus() + ") given for package: " + pkgData.getName());
        }
        List<String> groupIds = COMMA_BASE_SPLITTER.split(pkgData.getGroups());

        if (packageFailReasons.isEmpty() == false) {
            return packageFactory.createIMSPackage(pkgData.getId(), pkgData.getName(), mode, availabilityStatus, PolicyStatus.FAILURE, packageFailReasons.toString().isEmpty() ? "" : packageFailReasons.toString(), "", pkgData.getPrice(), groupIds);
        }


        return packageFactory.createIMSBasePackage(pkgData.getId(), pkgData.getName(), serviceNameToServiceTables, mode, status, pkgData.getPrice(), groupIds);
    }

}
