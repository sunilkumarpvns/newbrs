package com.elitecore.corenetvertex.pm.factory;


import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.CustomAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuotaProfileDetailFactory {
	
	private static Random random = new Random();

	public QuotaProfileDetailFactory () {
		
	}
	
	public static QuotaProfileDetail createQuotaProfileDetailWithUnlimiteUsageFor(String quotaProfileId, String serviceId) {
		return new QuotaProfileDetailBuilder(quotaProfileId, "name", "pkgName", serviceId,1).build();
	}

	public static QuotaProfileDetail createQuotaProfileDetailWithRandomUsageFor(String quotaProfileId, String serviceId) {
		return new QuotaProfileDetailBuilder(quotaProfileId, "name", "pkgName", serviceId,1)
				.withBillingCycleDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleTime(random.nextInt(Integer.MAX_VALUE))
				.withCustomDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomTime(random.nextInt(Integer.MAX_VALUE))
				.withDailyDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyTime(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyTime(random.nextInt(Integer.MAX_VALUE))
                .build();
	}

    public static class QuotaProfileDetailBuilder {

        private String quotaProfileId;
        private final String serviceId;
        private final String name;
        private final String pkgName;
        private final int fupLevel;

        private long dailyTotalUsage = Long.MAX_VALUE;
        private long dailyDownloadUsage = Long.MAX_VALUE;
        private long dailyUploadUsage = Long.MAX_VALUE;
        private long dailyTime = Long.MAX_VALUE;

        private long weeklyTotalUsage = Long.MAX_VALUE;
        private long weeklyDownloadUsage = Long.MAX_VALUE;
        private long weeklyUploadUsage = Long.MAX_VALUE;
        private long weeklyTime = Long.MAX_VALUE;

        private long customTotalUsage = Long.MAX_VALUE;
        private long customUploadUsage = Long.MAX_VALUE;
        private long customDownloadUsage = Long.MAX_VALUE;
        private long customTime = Long.MAX_VALUE;

        private long billingCycleTotalUsage = Long.MAX_VALUE;
        private long billingCycleDownloadUsage = Long.MAX_VALUE;
        private long billingCycleUploadUsage = Long.MAX_VALUE;
        private long billingCycleTime = Long.MAX_VALUE;

        public QuotaProfileDetailBuilder(String quotaProfileId, String name, String pkgName, String serviceName, int fupLevel) {
            this.quotaProfileId = quotaProfileId;
            this.name = name;
            this.pkgName = pkgName;
            this.serviceId = serviceName;
            this.fupLevel = fupLevel;
        }

        public QuotaProfileDetailBuilder withDailyTotalUsage(long usage) {

            if (usage > 0) {
                this.dailyTotalUsage = usage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withDailyDownlodUsage(long dailyDownloadUsage) {

            if (dailyDownloadUsage > 0) {
                this.dailyDownloadUsage = dailyDownloadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withDailyUploadUsage(long dailyUploadUsage) {

            if (dailyUploadUsage > 0) {
                this.dailyUploadUsage = dailyUploadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withDailyTime(long dailyTime) {

            if (dailyTime > 0) {
                this.dailyTime = dailyTime;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withWeeklyTotalUsage(long weeklyTotalUsage) {

            if (weeklyTotalUsage > 0) {
                this.weeklyTotalUsage = weeklyTotalUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withWeeklyDownlodUsage(long weeklyDownloadUsage) {

            if (weeklyDownloadUsage > 0) {
                this.weeklyDownloadUsage = weeklyDownloadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withWeeklyUploadUsage(long weeklyUploadUsage) {

            if (weeklyUploadUsage > 0) {
                this.weeklyUploadUsage = weeklyUploadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withWeeklyTime(long weeklyTime) {

            if (weeklyTime > 0) {
                this.weeklyTime = weeklyTime;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withCustomTotalUsage(long customTotalUsage) {

            if (customTotalUsage > 0) {
                this.customTotalUsage = customTotalUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withCustomDownlodUsage(long customDownloadUsage) {

            if (customDownloadUsage > 0) {
                this.customDownloadUsage = customDownloadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withCustomUploadUsage(long customUploadUsage) {

            if (customUploadUsage > 0) {
                this.customUploadUsage = customUploadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withCustomTime(long customTime) {

            if (customTime > 0) {
                this.customTime = customTime;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withBillingCycleTotalUsage(long billingCycleTotalUsage) {

            if (billingCycleTotalUsage > 0) {
                this.billingCycleTotalUsage = billingCycleTotalUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withBillingCycleDownlodUsage(long billingCycleDownloadUsage) {

            if (billingCycleDownloadUsage > 0) {
                this.billingCycleDownloadUsage = billingCycleDownloadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withBillingCycleUploadUsage(long billingCycleUploadUsage) {

            if (billingCycleUploadUsage > 0) {
                this.billingCycleUploadUsage = billingCycleUploadUsage;
            }
            return this;
        }

        public QuotaProfileDetailBuilder withBillingCycleTime(long billingCycleTime) {

            if (billingCycleTime > 0) {
                this.billingCycleTime = billingCycleTime;
            }
            return this;
        }

        public UMBaseQuotaProfileDetail build() {

            AllowedUsage dailyAllowedUsage = null;
            if (dailyTime == Long.MAX_VALUE && dailyTotalUsage == Long.MAX_VALUE && dailyDownloadUsage == Long.MAX_VALUE
                    && dailyUploadUsage == Long.MAX_VALUE) {
                dailyAllowedUsage = AllowedUsage.ALWAYS_ALLOWED;
            } else {
                dailyAllowedUsage = new DailyAllowedUsage(dailyTotalUsage, dailyDownloadUsage,
                        dailyUploadUsage, dailyTime, DataUnit.BYTE,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        TimeUnit.SECOND
                );
            }

            AllowedUsage weeklyAllowedUsage = null;
            if (weeklyTime == Long.MAX_VALUE && weeklyTotalUsage == Long.MAX_VALUE && weeklyDownloadUsage == Long.MAX_VALUE
                    && weeklyUploadUsage == Long.MAX_VALUE) {
                weeklyAllowedUsage = AllowedUsage.ALWAYS_ALLOWED;
            } else {
                weeklyAllowedUsage = new WeeklyAllowedUsage(weeklyTotalUsage,
                        weeklyDownloadUsage, weeklyUploadUsage, weeklyTime,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        TimeUnit.SECOND);
            }

            AllowedUsage customAllowedUsage = null;
            if (customTime == Long.MAX_VALUE && customTotalUsage == Long.MAX_VALUE && customDownloadUsage == Long.MAX_VALUE
                    && customUploadUsage == Long.MAX_VALUE) {
                customAllowedUsage = AllowedUsage.ALWAYS_ALLOWED;
            } else {
                customAllowedUsage = new CustomAllowedUsage(customTotalUsage,
                        customDownloadUsage, customUploadUsage, customTime,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        TimeUnit.SECOND);
            }

            AllowedUsage billingCycleAllowedUsage = null;
            if (billingCycleTime == Long.MAX_VALUE && billingCycleTotalUsage == Long.MAX_VALUE && billingCycleDownloadUsage == Long.MAX_VALUE
                    && billingCycleUploadUsage == Long.MAX_VALUE) {
                billingCycleAllowedUsage = AllowedUsage.ALWAYS_ALLOWED;
            } else {
                billingCycleAllowedUsage = new BillingCycleAllowedUsage(billingCycleTotalUsage, billingCycleDownloadUsage,
                        billingCycleUploadUsage, billingCycleTime,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        DataUnit.BYTE,
                        TimeUnit.SECOND);
            }

            Map<AggregationKey, AllowedUsage> allowedUsageMap = new HashMap<AggregationKey, AllowedUsage>();
            allowedUsageMap.put(AggregationKey.DAILY, dailyAllowedUsage);
            allowedUsageMap.put(AggregationKey.WEEKLY, weeklyAllowedUsage);
            allowedUsageMap.put(AggregationKey.CUSTOM, customAllowedUsage);
            allowedUsageMap.put(AggregationKey.BILLING_CYCLE, billingCycleAllowedUsage);


            return new UMBaseQuotaProfileDetail(quotaProfileId, name, serviceId,
                    fupLevel, "serviceName",
                    allowedUsageMap);
        }

        /*public QuotaProfileDetail buildRnCQuotaProfile() {
            RncProfileDetail rncProfileDetail = mock(RncProfileDetail.class);
            return rncProfileDetail;
        }*/
    }
	
}
