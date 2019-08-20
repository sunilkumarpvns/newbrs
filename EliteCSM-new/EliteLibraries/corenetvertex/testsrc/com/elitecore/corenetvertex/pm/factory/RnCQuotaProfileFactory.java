package com.elitecore.corenetvertex.pm.factory;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsageBuilderJava;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class RnCQuotaProfileFactory {
    protected final String id;
    protected final String name;
    protected RatingGroup ratingGroup = new RatingGroup(randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString(),
            nextInt(1, Integer.MAX_VALUE));
    protected DataServiceType dataServiceType = new DataServiceType(randomUUID().toString(), randomUUID().toString(), nextInt(1, Integer.MAX_VALUE),
            null, Arrays.asList(ratingGroup));
    protected int level = 0;
    protected long volumePulse = 1;
    protected long timePulse = 1;
    protected double rate = 0.0;
    protected UsageType rateUnit;
    protected Map<AggregationKey, AllowedUsage> keyToAllowedUsage;
    protected QuotaUsageType quotaUsageType = QuotaUsageType.VOLUME;
    protected VolumeUnitType volumeUnitType;
    protected String pccProfileName;
    protected long volumeCarryForwardLimit = 0;
    protected long timeCarryForwardLimit = 0;
    protected String revenueDetail = "test";

    public RnCQuotaProfileFactory(String id, String name) {
        this.id = id;
        this.name = name;
        this.keyToAllowedUsage = new HashMap<>();
    }

    public RnCQuotaProfileFactory withRGId(long rgId) {
        this.ratingGroup = new RatingGroup(randomUUID().toString(),
                randomUUID().toString(),
                randomUUID().toString(),
                rgId);
        return this;
    }

    public RnCQuotaProfileFactory withQuotaUsageType(QuotaUsageType quotaUsageType) {
        this.quotaUsageType = quotaUsageType;
        return this;
    }

    public RnCQuotaProfileFactory randomBalanceWithRate() {
        keyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, AllowedUsageBuilderJava.billingCycle());
        keyToAllowedUsage.put(AggregationKey.CUSTOM, AllowedUsageBuilderJava.custom());
        keyToAllowedUsage.put(AggregationKey.DAILY, AllowedUsageBuilderJava.daily());
        keyToAllowedUsage.put(AggregationKey.WEEKLY, AllowedUsageBuilderJava.weekly());
        rate = 100;
        return this;
    }

    public RnCQuotaProfileFactory withBillingCycleUsage(BillingCycleAllowedUsage billingCycleUsage) {
        keyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, billingCycleUsage);
        return this;
    }

    public RnCQuotaProfileFactory withDailyCycleUsage(DailyAllowedUsage dailyAllowedUsage) {
        keyToAllowedUsage.put(AggregationKey.DAILY, dailyAllowedUsage);
        return this;
    }

    public RnCQuotaProfileFactory withWeeklyCycleUsage(WeeklyAllowedUsage weeklyAllowedUsage) {
        keyToAllowedUsage.put(AggregationKey.WEEKLY, weeklyAllowedUsage);
        return this;
    }

    public RnCQuotaProfileFactory withPCCProfileName(String pccProfileName){
        this.pccProfileName = pccProfileName;
        return this;
    }

    public RncProfileDetail create() {
        return new RncProfileDetail(name,
                dataServiceType,
                level,
                ratingGroup,
                keyToAllowedUsage,
                volumePulse,
                timePulse,
                volumePulse,
                timePulse,
                DataUnit.BYTE.name(),
                TimeUnit.SECOND.name(),
                rate,
                rateUnit,
                quotaUsageType,
                volumeUnitType,
                "quotaProfileId",
                true, pccProfileName,volumeCarryForwardLimit,timeCarryForwardLimit,
                revenueDetail);
    }



    public RnCQuotaProfileFactory dataServiceType(DataServiceType serviceType) {
        this.dataServiceType = serviceType;
        return this;
    }

    public RnCQuotaProfileFactory ratingGroup(RatingGroup ratingGroup) {
        this.ratingGroup = ratingGroup;
        return this;
    }

    public RnCQuotaProfileFactory withRate(double rate) {
        this.rate = rate;
        return this;
    }

    public RnCQuotaProfileFactory withRateOn(UsageType usageType) {
        this.rateUnit = usageType;
        return this;
    }

    public RnCQuotaProfileFactory withRateOnVolume(double rate) {
        this.rate = rate;
        this.rateUnit = UsageType.VOLUME;
        return this;
    }

    public RnCQuotaProfileFactory withRateOnTime(double rate) {
        this.rate = rate;
        this.rateUnit = UsageType.TIME;
        return this;
    }

    public RnCQuotaProfileFactory level(int level) {
        this.level = level;
        return this;
    }

    public RnCQuotaProfileFactory withPulse(long volumePulse, long timePulse) {
        this.volumePulse = volumePulse;
        this.timePulse = timePulse;

        return this;
    }

    public RnCQuotaProfileFactory withVolumeUnitType(VolumeUnitType volumeUnitType) {
        this.volumeUnitType = volumeUnitType;
        return this;
    }

    public RnCQuotaProfileFactory withVolumeCarryForwardLimit(Long volumeCarryForwardLimit) {
        this.volumeCarryForwardLimit = volumeCarryForwardLimit;
        return this;
    }

    public RnCQuotaProfileFactory withTimeCarryForwardLimit(Long timeCarryForwardLimit) {
        this.timeCarryForwardLimit = timeCarryForwardLimit;
        return this;
    }
}
