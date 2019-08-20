package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.quota.RnCTopUpQuotaProfileDetail;

public class RnCQuotaProfileFactory extends com.elitecore.corenetvertex.pm.factory.RnCQuotaProfileFactory {

    public RnCQuotaProfileFactory(String id, String name) {
        super(id, name);
    }

    public RnCTopUpQuotaProfileDetail createTopUpQuotaProfileDetail() {
        return new RnCTopUpQuotaProfileDetail(id,
                name,
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
                rate, rateUnit,
                QuotaUsageType.VOLUME,
                VolumeUnitType.TOTAL,
				true,
				pccProfileName,
                volumeCarryForwardLimit,
                timeCarryForwardLimit,
                revenueDetail);
    }

    @Override
    public RnCQuotaProfileDetail create() {
        return new RnCQuotaProfileDetail(id,
                name,
                dataServiceType,
                level,
                ratingGroup,
                keyToAllowedUsage,
                volumePulse,
                timePulse,
                volumePulse,
                timePulse,
                DataUnit.BYTE.name()
                , TimeUnit.SECOND.name(),
                rate,
                rateUnit,
                QuotaUsageType.HYBRID,
                VolumeUnitType.TOTAL,
				true,
				pccProfileName,
                volumeCarryForwardLimit,
                timeCarryForwardLimit,
                revenueDetail);
    }

    public RnCQuotaProfileDetail createQuotaProfileDetailForVolume() {
        return new RnCQuotaProfileDetail(id,
                name,
                dataServiceType,
                level,
                ratingGroup,
                keyToAllowedUsage,
                volumePulse,
                timePulse,
                volumePulse,
                timePulse,
                DataUnit.BYTE.name()
                , TimeUnit.SECOND.name(),
                rate,
                rateUnit,
                QuotaUsageType.VOLUME,
                VolumeUnitType.TOTAL,
				true,
				pccProfileName,
                volumeCarryForwardLimit,
                timeCarryForwardLimit,
                revenueDetail);
    }

    public RnCQuotaProfileDetail createQuotaProfileDetailForTime() {
        return new RnCQuotaProfileDetail(id,
                name,
                dataServiceType,
                level,
                ratingGroup,
                keyToAllowedUsage,
                volumePulse,
                timePulse,
                volumePulse,
                timePulse,
                DataUnit.BYTE.name()
                , TimeUnit.SECOND.name(),
                rate,
                rateUnit,
                QuotaUsageType.TIME,
                VolumeUnitType.TOTAL,
				true,
				pccProfileName,
                volumeCarryForwardLimit,
                timeCarryForwardLimit,
                revenueDetail);
    }

    @Override
    public RnCQuotaProfileFactory withRGId(long rgId) {
        return (RnCQuotaProfileFactory)super.withRGId(rgId);
    }

    @Override
    public RnCQuotaProfileFactory randomBalanceWithRate() {
        return (RnCQuotaProfileFactory) super.randomBalanceWithRate();
    }

    @Override
    public RnCQuotaProfileFactory withRateOn(UsageType usageType) {
        return (RnCQuotaProfileFactory) super.withRateOn(usageType);
    }

    @Override
    public RnCQuotaProfileFactory withPulse(long volumePulse, long timePulse) {
        return (RnCQuotaProfileFactory)super.withPulse(volumePulse, timePulse);
    }

    @Override
    public RnCQuotaProfileFactory withRate(double rate) {
        return (RnCQuotaProfileFactory) super.withRate(rate);
    }

    @Override
    public RnCQuotaProfileFactory dataServiceType(DataServiceType serviceType) {
        return (RnCQuotaProfileFactory) super.dataServiceType(serviceType);
    }

    @Override
    public RnCQuotaProfileFactory ratingGroup(RatingGroup ratingGroup) {
        return (RnCQuotaProfileFactory) super.ratingGroup(ratingGroup);
    }

    @Override
    public RnCQuotaProfileFactory withPCCProfileName(String pccProfileName) {
        return (RnCQuotaProfileFactory) super.withPCCProfileName(pccProfileName);
    }
}