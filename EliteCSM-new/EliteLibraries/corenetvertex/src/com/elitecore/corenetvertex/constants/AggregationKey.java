package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.CustomAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;

public enum AggregationKey {


	BILLING_CYCLE("Billing Cycle") {
		@Override
		public AllowedUsage createAllowedUsage(QuotaProfileDetailData quotaProfileDetailData) {
			return new BillingCycleAllowedUsage(quotaProfileDetailData.getTotal()
					, quotaProfileDetailData.getDownload(), quotaProfileDetailData.getUpload(), quotaProfileDetailData.getTime()
					, DataUnit.valueOf(quotaProfileDetailData.getTotalUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getDownloadUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getUploadUnit())
					, TimeUnit.valueOf(quotaProfileDetailData.getTimeUnit()));
		}

		@Override
		public AllowedUsage createAllowedUsage(RncProfileDetailData rncProfileDetailData) {
			DataUnit volumeUnit = DataUnit.valueOf(rncProfileDetailData.getBalanceUnit());

			long total = CommonConstants.QUOTA_UNDEFINED;
			long upload = CommonConstants.QUOTA_UNDEFINED;
			long download = CommonConstants.QUOTA_UNDEFINED;
			long time = CommonConstants.QUOTA_UNDEFINED;


			QuotaUsageType quotaType = QuotaUsageType.valueOf(rncProfileDetailData.getRncProfileData().getQuotaType());

			boolean isVolumeDefined = quotaType == QuotaUsageType.VOLUME || quotaType == QuotaUsageType.HYBRID;
			boolean isTimeDefined = quotaType == QuotaUsageType.TIME || quotaType == QuotaUsageType.HYBRID;


			if (isVolumeDefined) {
				VolumeUnitType unitType = VolumeUnitType.valueOf(rncProfileDetailData.getRncProfileData().getUnitType());

				if (unitType == VolumeUnitType.TOTAL) {
					total = rncProfileDetailData.getBalance();
				} else if (unitType == VolumeUnitType.UPLOAD) {
					upload = rncProfileDetailData.getBalance();
				} else if (unitType == VolumeUnitType.DOWNLOAD) {
					download = rncProfileDetailData.getBalance();
				}
			}

			if(isTimeDefined) {
				time = rncProfileDetailData.getTimeBalance();
			}

			return new BillingCycleAllowedUsage(total,download, upload
					, time, volumeUnit, volumeUnit, volumeUnit
					, TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));
		}
	},
	CUSTOM("Custom") {
		@Override
		public AllowedUsage createAllowedUsage(QuotaProfileDetailData quotaProfileDetailData) {
			return new CustomAllowedUsage(quotaProfileDetailData.getTotal()
					, quotaProfileDetailData.getDownload(), quotaProfileDetailData.getUpload(), quotaProfileDetailData.getTime()
					, DataUnit.valueOf(quotaProfileDetailData.getTotalUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getDownloadUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getUploadUnit())
					, TimeUnit.valueOf(quotaProfileDetailData.getTimeUnit()));
			
		}

		@Override
		public AllowedUsage createAllowedUsage(RncProfileDetailData rncProfileDetailData) {
			return null;
		}
	},
	WEEKLY("Weekly") {
		@Override
		public AllowedUsage createAllowedUsage(QuotaProfileDetailData quotaProfileDetailData) {

			return new WeeklyAllowedUsage(quotaProfileDetailData.getTotal()
					, quotaProfileDetailData.getDownload(), quotaProfileDetailData.getUpload(), quotaProfileDetailData.getTime()
					, DataUnit.valueOf(quotaProfileDetailData.getTotalUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getDownloadUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getUploadUnit())
					, TimeUnit.valueOf(quotaProfileDetailData.getTimeUnit()));
		}

		@Override
		public AllowedUsage createAllowedUsage(RncProfileDetailData rncProfileDetailData) {
			DataUnit volumeUnit = DataUnit.valueOf(rncProfileDetailData.getUsageLimitUnit());

			long total = CommonConstants.QUOTA_UNDEFINED;
			long upload = CommonConstants.QUOTA_UNDEFINED;
			long download = CommonConstants.QUOTA_UNDEFINED;
			long time = CommonConstants.QUOTA_UNDEFINED;


			QuotaUsageType quotaType = QuotaUsageType.valueOf(rncProfileDetailData.getRncProfileData().getQuotaType());

			boolean isVolumeDefined = quotaType == QuotaUsageType.VOLUME || quotaType == QuotaUsageType.HYBRID;
			boolean isTimeDefined = quotaType == QuotaUsageType.TIME || quotaType == QuotaUsageType.HYBRID;


			if (isVolumeDefined) {
				VolumeUnitType unitType = VolumeUnitType.valueOf(rncProfileDetailData.getRncProfileData().getUnitType());

				if (unitType == VolumeUnitType.TOTAL) {
					total = rncProfileDetailData.getWeeklyUsageLimit();
				} else if (unitType == VolumeUnitType.UPLOAD) {
					upload = rncProfileDetailData.getWeeklyUsageLimit();
				} else if (unitType == VolumeUnitType.DOWNLOAD) {
					download = rncProfileDetailData.getWeeklyUsageLimit();
				}
			}

			if(isTimeDefined) {
				time = rncProfileDetailData.getWeeklyTimeLimit();
			}

			return new WeeklyAllowedUsage(total,download, upload
					, time, volumeUnit, volumeUnit, volumeUnit
					, TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));
		}
	},
	DAILY("Daily") {
		@Override
		public AllowedUsage createAllowedUsage(QuotaProfileDetailData quotaProfileDetailData) {
			return new DailyAllowedUsage(quotaProfileDetailData.getTotal()
					, quotaProfileDetailData.getDownload(), quotaProfileDetailData.getUpload(), quotaProfileDetailData.getTime()
					, DataUnit.valueOf(quotaProfileDetailData.getTotalUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getDownloadUnit())
					, DataUnit.valueOf(quotaProfileDetailData.getUploadUnit())
					, TimeUnit.valueOf(quotaProfileDetailData.getTimeUnit()));
			
		}

		@Override
		public AllowedUsage createAllowedUsage(RncProfileDetailData rncProfileDetailData) {
			DataUnit volumeUnit = DataUnit.valueOf(rncProfileDetailData.getUsageLimitUnit());

			long total = CommonConstants.QUOTA_UNDEFINED;
			long upload = CommonConstants.QUOTA_UNDEFINED;
			long download = CommonConstants.QUOTA_UNDEFINED;
			long time = CommonConstants.QUOTA_UNDEFINED;


			QuotaUsageType quotaType = QuotaUsageType.valueOf(rncProfileDetailData.getRncProfileData().getQuotaType());

			boolean isVolumeDefined = quotaType == QuotaUsageType.VOLUME || quotaType == QuotaUsageType.HYBRID;
			boolean isTimeDefined = quotaType == QuotaUsageType.TIME || quotaType == QuotaUsageType.HYBRID;


			if (isVolumeDefined) {
				VolumeUnitType unitType = VolumeUnitType.valueOf(rncProfileDetailData.getRncProfileData().getUnitType());

				if (unitType == VolumeUnitType.TOTAL) {
					total = rncProfileDetailData.getDailyUsageLimit();
				} else if (unitType == VolumeUnitType.UPLOAD) {
					upload = rncProfileDetailData.getDailyUsageLimit();
				} else if (unitType == VolumeUnitType.DOWNLOAD) {
					download = rncProfileDetailData.getDailyUsageLimit();
				}
			}

			if(isTimeDefined) {
				time = rncProfileDetailData.getDailyTimeLimit();
			}

			return new DailyAllowedUsage(total,download, upload
					, time, volumeUnit, volumeUnit, volumeUnit
					, TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));
		}
	};
	private static HashMap<String, AggregationKey> nameToAggregationKey;
	private String val;
	AggregationKey(String val){
		this.val = val;
	}
	public String getVal() {
		return this.val;
	}
	public abstract AllowedUsage createAllowedUsage(QuotaProfileDetailData quotaProfileDetailData);
	public abstract AllowedUsage createAllowedUsage(RncProfileDetailData rncProfileDetailData);

	static {
		nameToAggregationKey = new HashMap<>(1, 1);

		for (AggregationKey aggregationKey : values()) {
			nameToAggregationKey.put(aggregationKey.name(), aggregationKey);
		}
	}

	public static AggregationKey fromName(String name){
		return nameToAggregationKey.get(name);
	}
}
