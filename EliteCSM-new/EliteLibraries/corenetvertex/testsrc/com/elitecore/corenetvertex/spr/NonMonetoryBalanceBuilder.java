package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import org.apache.commons.lang3.RandomUtils;

import java.util.Date;

public class NonMonetoryBalanceBuilder {

    public UsageSpec billingCycleUsage;
    public CounterSpec dayUsage;
    public CounterSpec weekUsage;
    public ServiceInformation info;


    public NonMonetoryBalanceBuilder(){
        billingCycleUsage = createUsageSpec();
        weekUsage = createCounterSpec();
        dayUsage = createCounterSpec();
        info = createServiceInfo();
    }

    protected ServiceInformation createServiceInfo() {
        return new ServiceInformation();
    }

    protected UsageSpec createUsageSpec() {
        return new UsageSpec();
    }


    NonMonetoryBalanceBuilder allBalancesAreAvailable() {
        billingCycleUsage.volume(1).and().time(1);
        return this;
    }

    public ServiceInformation info() {
        return info;
    }

    public NonMonetoryBalance build() {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder("test",
                info.serviceId,
                info.packageId,
                info.rgId,
                info.subscriberId,
                info.subscriptionId,
                info.level,
                info.quotaProfileId, ResetBalanceStatus.NOT_RESET, null, null)
                .withBillingCycleVolumeBalance(billingCycleUsage.volume(), billingCycleUsage.volume())
                .withBillingCycleTimeBalance(billingCycleUsage.time(), billingCycleUsage.time())
                .withReservation(billingCycleUsage.reserveVolume(), billingCycleUsage.reserveTime())
                .withDailyUsage(dayUsage.volume(), dayUsage.time())
                .withWeeklyUsage(weekUsage.volume(), weekUsage.time())
                .withDailyResetTime(dayUsage.resetTime())
                .withWeeklyResetTime(weekUsage.resetTime())
                .withBillingCycleResetTime(billingCycleUsage.resetTime())
                .build();
    }



    public NonMonetoryBalanceBuilder billingCycle(long volume, long volumeReservation, long time, long timeReservation, long reserTime) {
        billingCycleUsage = createUsageSpec();
        billingCycleUsage.
                volume(volume)
                .and().time(time)
                .and().reserveTime(timeReservation)
                .and().reserveVolume(volumeReservation)
                .and().resetOn(reserTime);

        return this;
    }

    public NonMonetoryBalanceBuilder day(long volume, long time, long reserTime) {
        dayUsage = createCounterSpec();
        dayUsage.volume(volume)
                .and().time(time)
                .and().resetOn(reserTime);
        return this;
    }

    protected CounterSpec createCounterSpec() {
        return new CounterSpec();
    }

    public NonMonetoryBalanceBuilder week(long volume, long time, long reserTime) {
        weekUsage = createCounterSpec();
        weekUsage.volume(volume)
                .and().time(time)
                .and().resetOn(reserTime);
        return this;
    }

    NonMonetoryBalanceBuilder and() {
        return this;
    }

    NonMonetoryBalanceBuilder resetOn(Date date) {
        weekUsage.resetOn(date);
        billingCycleUsage.resetOn(date);
        dayUsage.resetOn(date);
        return this;
    }

    public static class ServiceInformation {
        public String quotaProfileId = "test";
        public String packageId = "test";
        public long serviceId = RandomUtils.nextInt(0, Integer.MAX_VALUE);
        public long rgId = 0;
        public String subscriberId = "test";

        public String getQuotaProfileId() {
            return quotaProfileId;
        }

        public void setQuotaProfileId(String quotaProfileId) {
            this.quotaProfileId = quotaProfileId;
        }

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public long getServiceId() {
            return serviceId;
        }

        public void setServiceId(long serviceId) {
            this.serviceId = serviceId;
        }

        public long getRgId() {
            return rgId;
        }

        public void setRgId(long rgId) {
            this.rgId = rgId;
        }

        public String getSubscriberId() {
            return subscriberId;
        }

        public void setSubscriberId(String subscriberId) {
            this.subscriberId = subscriberId;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public int level = 0;
        public String subscriptionId = null;

        public ServiceInformation fromDetail(RncProfileDetail rnCQuotaProfileDetail) {
            this.quotaProfileId = rnCQuotaProfileDetail.getQuotaProfileId();
            this.serviceId = rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier();
            this.rgId = rnCQuotaProfileDetail.getRatingGroup().getIdentifier();
            this.level = rnCQuotaProfileDetail.getFupLevel();
            return this;
        }




    }

}
