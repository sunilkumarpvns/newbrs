package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Comparator.comparing;

public class UMBasedQuotaProfileFactory {

	private static final String MODULE = "UM-QUOTA-FCTRY";
	private PackageFactory packageFactory;

	public UMBasedQuotaProfileFactory(PackageFactory packageFactory) {

		this.packageFactory = packageFactory;
	}

	public QuotaProfile createUMBaseQuotaProfile(QuotaProfileData quotaProfileData, List<String> quotaProfileFailReasons, List<String> partialFailReason) {

		if (isNullOrEmpty(quotaProfileData.getQuotaProfileDetailDatas())) {
			quotaProfileFailReasons.add("No quota profile details configured for quota profile: " + quotaProfileData.getName());
			return null;
		}

		TreeMap<Integer, FUPLevelQuotaProfileDetailData> fupQuotaProfileBuilders = new TreeMap<>();

		List<QuotaProfileDetailData> quotaProfileDetailDatas = quotaProfileData.getQuotaProfileDetailDatas().stream().sorted(comparing(QuotaProfileDetailData::getFupLevel)).collect(Collectors.toList());

		for (QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas) {
			List<String> failReasons = new ArrayList<>();
			validateQuotaProfileDetailData(quotaProfileDetailData, failReasons);

			if (failReasons.isEmpty() == false) {
				quotaProfileFailReasons.add("Quota Profile Detail(" + quotaProfileDetailData.getId() + " parsing fail. Cause by:"
						+ FactoryUtils.format(failReasons));
				continue;
			}

			Integer fupLevel = quotaProfileDetailData.getFupLevel();


			FUPLevelQuotaProfileDetailData fupLevelQuotaProfileDetailData = fupQuotaProfileBuilders.get(fupLevel);

			if (fupLevelQuotaProfileDetailData == null) {
				fupLevelQuotaProfileDetailData = new FUPLevelQuotaProfileDetailData(fupQuotaProfileBuilders.get(fupLevel-1), fupLevel);
				fupQuotaProfileBuilders.put(fupLevel, fupLevelQuotaProfileDetailData);
			}
			fupLevelQuotaProfileDetailData.add(quotaProfileDetailData, quotaProfileFailReasons, partialFailReason);

		}

		if (RenewalIntervalUnit.fromRenewalIntervalUnit(quotaProfileData.getRenewalIntervalUnit())==null) {
			quotaProfileFailReasons.add("Invalid Renewal Interval Value set for Quota profile: " + quotaProfileData.getName());
		}

		if (quotaProfileFailReasons.isEmpty() == false) {
			getLogger().info(MODULE, "Skip to create Quota profie for :" + quotaProfileData.getName() + ". Reason: "
					+ FactoryUtils.format(quotaProfileFailReasons));
			return null;
		}

		ArrayList<Map<String, QuotaProfileDetail>> quotaProfileDetails = new ArrayList<>();


		for (int fupLevel = 0; fupLevel <= fupQuotaProfileBuilders.lastKey(); fupLevel++) {
			FUPLevelQuotaProfileDetailData builders = fupQuotaProfileBuilders.get(fupLevel);
			Map<String, QuotaProfileDetail> serviceToQuotaProfileDetails = builders.createQuotaProfileDetails(packageFactory, quotaProfileData, fupLevel);
			quotaProfileDetails.add(serviceToQuotaProfileDetails);

		}

		return packageFactory.createUMBaseQuotaProfile(quotaProfileData.getName(), quotaProfileData.getPkgData().getName()
				, quotaProfileData.getId()
				, quotaProfileData.getRenewalInterval()==null?0:quotaProfileData.getRenewalInterval()
				,  RenewalIntervalUnit.fromRenewalIntervalUnit(quotaProfileData.getRenewalIntervalUnit())
				, QuotaProfileType.USAGE_METERING_BASED, quotaProfileDetails
				, quotaProfileData.getUsagePresence() == null ? CounterPresence.MANDATORY : CounterPresence.fromValue(quotaProfileData
						.getUsagePresence())
				, quotaProfileData.getBalanceLevel());
	}

	private static void validateQuotaProfileDetailData(QuotaProfileDetailData quotaProfileDetailData, List<String> failReasons) {
		if (quotaProfileDetailData.getAggregationKey() == null) {
			failReasons.add("No aggregation key provided for quota profile detail: " + quotaProfileDetailData.getId());
		}

		DataServiceTypeData serviceType = quotaProfileDetailData.getDataServiceTypeData();
		if (serviceType == null) {
			failReasons.add("No data service type configured for quota profile detail: " + quotaProfileDetailData.getId());
		}

		if (quotaProfileDetailData.getAggregationKey() != null) {
		 	AggregationKey aggregationKey = AggregationKey.fromName(quotaProfileDetailData.getAggregationKey());

			if (aggregationKey == null) {
				failReasons.add("Invalid aggregation key(" + aggregationKey + ") provided for quota profile detail: " + quotaProfileDetailData.getId());
			}
		}

		if (quotaProfileDetailData.getTotal() == null) {
			quotaProfileDetailData.setTotal(CommonConstants.QUOTA_UNLIMITED);
		}

		if (quotaProfileDetailData.getUpload() == null) {
			quotaProfileDetailData.setUpload(CommonConstants.QUOTA_UNLIMITED);
		}

		if (quotaProfileDetailData.getDownload() == null) {
			quotaProfileDetailData.setDownload(CommonConstants.QUOTA_UNLIMITED);
		}

		if (quotaProfileDetailData.getTime() == null) {
			quotaProfileDetailData.setTime(CommonConstants.QUOTA_UNLIMITED);
		}
	}

	private static class FUPLevelQuotaProfileDetailData {
		private HashMap<String, List<QuotaProfileDetailData>> serviceToQuotaProfileDetails;
		// This map is maintained to check whether same service and same
		// aggregation key quota is already configured
		private Map<String, List<String>> serviceToAggregationKey;
		private FUPLevelQuotaProfileDetailData priorFupLevelQuotaProfileDetailData;
		private int fupLevel;

		FUPLevelQuotaProfileDetailData(FUPLevelQuotaProfileDetailData fupLevelQuotaProfileDetailData, int fupLevel) {
			this.priorFupLevelQuotaProfileDetailData = fupLevelQuotaProfileDetailData;
			this.fupLevel = fupLevel;
			serviceToQuotaProfileDetails = new HashMap<>();
			serviceToAggregationKey = new HashMap<>();
		}

		public boolean contains(String serviceId, String aggregationKey) {
			return serviceToAggregationKey.keySet().contains(serviceId) && serviceToAggregationKey.get(serviceId).contains(aggregationKey);
		}

		public void add(QuotaProfileDetailData quotaProfileDetailData, List<String> failReason, List<String> partialFailReason) {

			String serviceId = quotaProfileDetailData.getDataServiceTypeData().getId();
			String aggregationKey = quotaProfileDetailData.getAggregationKey();
			if (contains(serviceId, aggregationKey) == false) {
				List<QuotaProfileDetailData> quotaProfileDetailDatas = serviceToQuotaProfileDetails.get(serviceId);
				if (quotaProfileDetailDatas == null) {
					quotaProfileDetailDatas = new ArrayList<>();
					serviceToQuotaProfileDetails.put(serviceId, quotaProfileDetailDatas);
				}

				List<String> aggregationKeys = serviceToAggregationKey.get(serviceId);
				if (aggregationKeys == null) {
					aggregationKeys = new ArrayList<>(4);
					serviceToAggregationKey.put(serviceId, aggregationKeys);
				}


				if (priorFupLevelQuotaProfileDetailData != null) {
                    QuotaProfileDetailData higherFupLevelDetailData = priorFupLevelQuotaProfileDetailData.get(serviceId, aggregationKey);
					if (higherFupLevelDetailData != null) {
						plus(higherFupLevelDetailData, quotaProfileDetailData);
					} else {
						String serviceName = quotaProfileDetailData.getDataServiceTypeData().getName();
						String log = "Service(" + serviceName + ") was not found on upper level:" + (priorFupLevelQuotaProfileDetailData.fupLevel ==0 ? "hsq" : "fup" + fupLevel)
								+ " in Quota Profile Detail: " + quotaProfileDetailData.getId();
						partialFailReason.add(log);
						getLogger().warn(MODULE, log);
					}
				}

				quotaProfileDetailDatas.add(quotaProfileDetailData);
				aggregationKeys.add(quotaProfileDetailData.getAggregationKey());

			} else {
				String serviceName = quotaProfileDetailData.getDataServiceTypeData().getName();
				String log = "Same service(" + serviceName + ") and aggregation-key("
						+ quotaProfileDetailData.getAggregationKey() + ") found on level:" + (fupLevel== 0 ? "hsq" : "fup" + fupLevel)
						+ " in " + quotaProfileDetailData.getId();

				failReason.add(log);
			}
		}

		private void plus(QuotaProfileDetailData higherFupLevelDetailData, QuotaProfileDetailData quotaProfileDetailData) {

			getTotalBalance(higherFupLevelDetailData, quotaProfileDetailData);

			getUploadBalance(higherFupLevelDetailData, quotaProfileDetailData);

			getDownloadBalance(higherFupLevelDetailData, quotaProfileDetailData);

			getTimeBalance(higherFupLevelDetailData, quotaProfileDetailData);
		}

		private void getTotalBalance(QuotaProfileDetailData higherFupLevelDetailData, QuotaProfileDetailData quotaProfileDetailData) {
			if (isUnlimited(higherFupLevelDetailData.getTotal()) || isUnlimited(quotaProfileDetailData.getTotal())) {
				return;
			}

			DataUnit higherFupTotalUnit = DataUnit.fromName(higherFupLevelDetailData.getTotalUnit());
			DataUnit quotaProfileTotalUnit = DataUnit.fromName(quotaProfileDetailData.getTotalUnit());
			long higherFupTotal = higherFupLevelDetailData.getTotal();
			long quotaProfileTotal = quotaProfileDetailData.getTotal();

			if (higherFupTotalUnit.equals(quotaProfileTotalUnit)) {

				quotaProfileDetailData.setTotal(quotaProfileTotal + higherFupTotal);

			} else if (higherFupTotalUnit.isSmallerDataUnitThan(quotaProfileTotalUnit)) {
				long total = higherFupTotal + higherFupTotalUnit.fromBytes(quotaProfileTotalUnit.toBytes(quotaProfileTotal));

				quotaProfileDetailData.setTotal(total);
				quotaProfileDetailData.setTotalUnit(higherFupTotalUnit.name());

			} else {

				long total = quotaProfileTotal + quotaProfileTotalUnit.fromBytes(higherFupTotalUnit.toBytes(higherFupTotal));

				quotaProfileDetailData.setTotal(total);

			}

		}

		private void getUploadBalance(QuotaProfileDetailData higherFupLevelDetailData, QuotaProfileDetailData quotaProfileDetailData) {

			if (isUnlimited(higherFupLevelDetailData.getUpload()) || isUnlimited(quotaProfileDetailData.getUpload())) {
				return;
			}

			DataUnit higherFupUploadUnit = DataUnit.fromName(higherFupLevelDetailData.getUploadUnit());
			DataUnit quotaProfileUploadUnit = DataUnit.fromName(quotaProfileDetailData.getUploadUnit());
			long higherFupUpload = higherFupLevelDetailData.getUpload();
			long quotaProfileUpload = quotaProfileDetailData.getUpload();

			if (higherFupUploadUnit.equals(quotaProfileUploadUnit)) {

				quotaProfileDetailData.setUpload(quotaProfileUpload + higherFupUpload);

			} else if (higherFupUploadUnit.isSmallerDataUnitThan(quotaProfileUploadUnit)) {

				long upload = higherFupUpload + higherFupUploadUnit.fromBytes(quotaProfileUploadUnit.toBytes(quotaProfileUpload));

				quotaProfileDetailData.setUpload(upload);
				quotaProfileDetailData.setUploadUnit(higherFupUploadUnit.name());

			} else {

				long upload = quotaProfileUpload + quotaProfileUploadUnit.fromBytes(higherFupUploadUnit.toBytes(higherFupUpload));

				quotaProfileDetailData.setUpload(upload);

			}
		}

		private void getDownloadBalance(QuotaProfileDetailData higherFupLevelDetailData, QuotaProfileDetailData quotaProfileDetailData) {
			if (isUnlimited(higherFupLevelDetailData.getDownload()) || isUnlimited(quotaProfileDetailData.getDownload())) {
				return;
			}

			DataUnit higherFupDownloadUnit = DataUnit.fromName(higherFupLevelDetailData.getDownloadUnit());
			DataUnit quotaProfileDownloadUnit = DataUnit.fromName(quotaProfileDetailData.getDownloadUnit());
			long higherFupDownload = higherFupLevelDetailData.getDownload();
			long quotaProfileDownload = quotaProfileDetailData.getDownload();

			if (higherFupDownloadUnit.equals(quotaProfileDownloadUnit)) {

				quotaProfileDetailData.setDownload(quotaProfileDownload + higherFupDownload);

			} else if (higherFupDownloadUnit.isSmallerDataUnitThan(quotaProfileDownloadUnit)) {

				long download = higherFupDownload + higherFupDownloadUnit.fromBytes(quotaProfileDownloadUnit.toBytes(quotaProfileDownload));

				quotaProfileDetailData.setDownload(download);
				quotaProfileDetailData.setDownloadUnit(higherFupDownloadUnit.name());

			} else {
				long download = quotaProfileDownload + quotaProfileDownloadUnit.fromBytes(higherFupDownloadUnit.toBytes(higherFupDownload));

				quotaProfileDetailData.setDownload(download);

			}
		}

		private void getTimeBalance(QuotaProfileDetailData higherFupLevelDetailData, QuotaProfileDetailData quotaProfileDetailData) {
			if (isUnlimited(higherFupLevelDetailData.getTime()) || isUnlimited(quotaProfileDetailData.getTime())) {
				return;
			}


			TimeUnit higherFupTimeUnit = TimeUnit.fromVal(higherFupLevelDetailData.getTimeUnit());
			TimeUnit quotaProfileTimeUnit = TimeUnit.fromVal(quotaProfileDetailData.getTimeUnit());
			long higherFupTime = higherFupLevelDetailData.getTime();
			long quotaProfileTime = quotaProfileDetailData.getTime();

			if (higherFupTimeUnit.equals(quotaProfileTimeUnit)) {

				quotaProfileDetailData.setTime(quotaProfileTime + higherFupTime);

			} else if (higherFupTimeUnit.isSmallerTimeUnitThan(quotaProfileTimeUnit)) {

				long timeBalance = higherFupTime + higherFupTimeUnit.fromSeconds(quotaProfileTimeUnit.toSeconds(quotaProfileTime));

				quotaProfileDetailData.setTime(timeBalance);
				quotaProfileDetailData.setTimeUnit(higherFupTimeUnit.name());


			} else {

				long timeBalance = quotaProfileTime + quotaProfileTimeUnit.fromSeconds(higherFupTimeUnit.toSeconds(higherFupTime));

				quotaProfileDetailData.setTime(timeBalance);

			}
		}


		public boolean isUnlimited(long value) {
            return CommonConstants.QUOTA_UNLIMITED == value;
        }

		private QuotaProfileDetailData get(String serviceId, String aggregationKey) {
			List<QuotaProfileDetailData> quotaProfileDetailDatas = serviceToQuotaProfileDetails.get(serviceId);
			if(quotaProfileDetailDatas != null) {
				return quotaProfileDetailDatas.stream()
						.filter(quotaProfileDetailData -> quotaProfileDetailData.getAggregationKey().equalsIgnoreCase(aggregationKey))
						.findFirst().orElse(null);
			}
			return null;
		}

		public Map<String, QuotaProfileDetail> createQuotaProfileDetails(PackageFactory packageFactory, QuotaProfileData quotaProfileData, int fupLevel) {
			HashMap<String, QuotaProfileDetail> quotaProfileDetailHashMap = new HashMap<>();
			for (Entry<String, List<QuotaProfileDetailData>> serviceToConf : this.serviceToQuotaProfileDetails.entrySet()) {

				DataServiceTypeData serviceType = serviceToConf.getValue().get(0).getDataServiceTypeData();
				List<QuotaProfileDetailData> quotaDetail = serviceToConf.getValue();
				UMBaseQuotaProfileDetail quotaProfileDetail = packageFactory.createUMBaseQuotaProfileDetail(quotaProfileData.getId(), quotaProfileData.getName(),
                        serviceType.getId(), fupLevel, serviceType.getName(), quotaDetail);
				quotaProfileDetailHashMap.put(serviceToConf.getKey(), quotaProfileDetail);
			}
			return quotaProfileDetailHashMap;
		}
	}
}