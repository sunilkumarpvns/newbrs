package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;

public class ChargingRuleBaseNameFactory {

	private PackageFactory packageFactory;
	private DataServiceTypeFactory dataServiceTypeFactory;
	
	public ChargingRuleBaseNameFactory(DataServiceTypeFactory dataServiceTypeFactory, PackageFactory packageFactory) {
		this.dataServiceTypeFactory = dataServiceTypeFactory;
		this.packageFactory = packageFactory;
	
	}
	@Nullable
	public ChargingRuleBaseName createChargingRuleBaseName(ChargingRuleBaseNameData chargingRuleBaseNameData, int fupLevel,
			List<String> chargingRuleBaseNameFailReasons) {


		validateChargingRuleBaseName(chargingRuleBaseNameData, chargingRuleBaseNameFailReasons);
		Map<String, DataServiceType> monitoringKeyToServiceType = new HashMap<String, DataServiceType>();
		for (ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas()) {

			DataServiceType dataServiceType = dataServiceTypeFactory.createServiceType(chargingRuleDataServiceTypeData.getDataServiceTypeData());
			monitoringKeyToServiceType.put(chargingRuleDataServiceTypeData.getMonitoringKey(), dataServiceType);
		}

		if (chargingRuleBaseNameFailReasons.isEmpty() == false) {
			return null;
		}

		Map<String, SliceInformation> monitoringKeyToSliceInformation = new HashMap<String, SliceInformation>();
		for (ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas()) {

			List<String> chargingRuleBaseNameFailReason = new ArrayList<String>();
			SliceInformation sliceInformation = createSliceInformation(chargingRuleDataServiceTypeData, chargingRuleBaseNameFailReason);

			if (chargingRuleBaseNameFailReason.isEmpty() == false) {
				chargingRuleBaseNameFailReasons.add("Slice Information (" + chargingRuleDataServiceTypeData.getDataServiceTypeData().getName()
						+ ") parsing Failed. Cause by: "
						+ FactoryUtils.format(chargingRuleBaseNameFailReason));
			} else {
				if (chargingRuleDataServiceTypeData.getMonitoringKey() != null) {
					monitoringKeyToSliceInformation.put(chargingRuleDataServiceTypeData.getMonitoringKey(), sliceInformation);
				}

			}
		}

		return packageFactory.createChargingRuleBaseName(
				chargingRuleBaseNameData.getId()
				, chargingRuleBaseNameData.getName()
				, monitoringKeyToServiceType
				, fupLevel, monitoringKeyToSliceInformation);

	}
	
	private static SliceInformation createSliceInformation(ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData,
			List<String> chargingRuleBaseNameFailReason) {

		if (chargingRuleDataServiceTypeData.getSliceTotal() == null
				&& chargingRuleDataServiceTypeData.getSliceDownload() == null
				&& chargingRuleDataServiceTypeData.getSliceUpload() == null
				&& chargingRuleDataServiceTypeData.getSliceTime() == null) {
			chargingRuleBaseNameFailReason.add("Slice Quota Not Configured");
		}

		if (chargingRuleBaseNameFailReason.isEmpty() == false) {
			return null;
		}

		return new SliceInformation(
				FactoryUtils.getDataInBytes(chargingRuleDataServiceTypeData.getSliceTotal(), chargingRuleDataServiceTypeData.getSliceTotalUnit())
				, FactoryUtils.getDataInBytes(chargingRuleDataServiceTypeData.getSliceUpload(), chargingRuleDataServiceTypeData.getSliceUploadUnit())
				, FactoryUtils.getDataInBytes(chargingRuleDataServiceTypeData.getSliceDownload(), chargingRuleDataServiceTypeData.getSliceDownloadUnit())
				, FactoryUtils.getTimeInSeconds(chargingRuleDataServiceTypeData.getSliceTime(), chargingRuleDataServiceTypeData.getSliceTimeUnit()));

	}

	private static void validateChargingRuleBaseName(ChargingRuleBaseNameData chargingRuleBaseNameData, List<String> failReasons) {
		if (Strings.isNullOrBlank(chargingRuleBaseNameData.getGroups())) {
			failReasons.add("No groups configured for charging rule base name: " + chargingRuleBaseNameData.getName());
		}

		if (Collectionz.isNullOrEmpty(chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas()) == false) {

			List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas();

			for (int outerIndex = 0; outerIndex < chargingRuleDataServiceTypeDatas.size() - 1; outerIndex++) {
				ChargingRuleDataServiceTypeData outerChargingRuleDataServiceTypeData = chargingRuleDataServiceTypeDatas.get(outerIndex);

				for (int innerIndex = outerIndex + 1; innerIndex < chargingRuleDataServiceTypeDatas.size(); innerIndex++) {
					ChargingRuleDataServiceTypeData innerchargingRuleDataServiceTypeData = chargingRuleDataServiceTypeDatas.get(innerIndex);

					if (outerChargingRuleDataServiceTypeData.getMonitoringKey().equalsIgnoreCase(innerchargingRuleDataServiceTypeData.getMonitoringKey())) {
						failReasons.add("Duplicate monitoring key configured for Charging Rule Base Name: " + chargingRuleBaseNameData.getName());
					}
				}
			}

		}
	}
	
}
