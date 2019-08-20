package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.*;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Date;
import java.util.Map;

public class NonMonitoryBalanceBuilderJava {

    public UsageSpec billingCycleUsage;
    public UsageSpec dayUsage;
    public UsageSpec weekUsage;
    public ServiceInformation info;


    public NonMonitoryBalanceBuilderJava(){
        billingCycleUsage = new UsageSpec();
        weekUsage = new UsageSpec();
        dayUsage = new UsageSpec();
        info = new ServiceInformation();
    }

    public NonMonitoryBalanceBuilderJava(RncProfileDetail rnCQuotaProfileDetail) {
        billingCycleUsage = new UsageSpec();
        weekUsage = new UsageSpec();
        dayUsage = new UsageSpec();
        info = new ServiceInformation();
        info.leftShift(rnCQuotaProfileDetail);
    }


    public NonMonitoryBalanceBuilderJava allBalancesAreAvailable() {
        billingCycleUsage.download(CommonConstants.QUOTA_UNLIMITED).and().upload(CommonConstants.QUOTA_UNLIMITED).and().time(CommonConstants.QUOTA_UNLIMITED).and().total(CommonConstants.QUOTA_UNLIMITED);
        return this;
    }


    public NonMonetoryBalance build() {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder("test",
                info.serviceId,
                info.packageId, info.rgId, info.subscriberId, info.subscriptionId,info.level,info.quotaProfileId, ResetBalanceStatus.NOT_RESET, null, null)
        .withBillingCycleVolumeBalance(billingCycleUsage.total, billingCycleUsage.total)
        .withBillingCycleTimeBalance(billingCycleUsage.time, billingCycleUsage.time)
        .withDailyUsage(dayUsage.total, dayUsage.time)
        .withWeeklyUsage(weekUsage.total, weekUsage.time)
        .withDailyResetTime(CommonConstants.QUOTA_UNLIMITED)
        .withWeeklyResetTime(CommonConstants.QUOTA_UNLIMITED)
        .withBillingCycleResetTime(CommonConstants.QUOTA_UNLIMITED)
        .build();
    }

    public ServiceInformation info() {
        this.info = new ServiceInformation();
        return info;
    }



    public NonMonitoryBalanceBuilderJava billingCycleUsageExceeded() {
        billingCycleUsage.leftShift(new BillingCycleAllowedUsage(0,0,0,0,DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        return this;
    }

    public void day() {
        dayUsage = new UsageSpec();
        dayUsage.upload(0l);
        dayUsage.download(0l);
        dayUsage.total(0l);
        dayUsage.time(0l);
        dayUsage.resetOn(1000);
    }

    public void day(DailyAllowedUsage allowedUsage) {
        day();
    }


    public void week() {
        weekUsage = new UsageSpec();
        weekUsage.upload(0l);
        weekUsage.download(0l);
        weekUsage.total(0l);
        weekUsage.time(0l);
        weekUsage.resetOn(1000);

    }

    public void week(WeeklyAllowedUsage allowedUsage) {
        week();
    }

    NonMonitoryBalanceBuilderJava and() {
        return this;
    }

    NonMonitoryBalanceBuilderJava resetOn(Date date) {
        weekUsage.resetOn(date);
        billingCycleUsage.resetOn(date);
        dayUsage.resetOn(date);
        return this;
    }

    public class ServiceInformation {
        public String quotaProfileId = "test";
        public String packageId = "pkg";
        public long serviceId = RandomUtils.nextInt(0, Integer.MAX_VALUE);
        public long rgId = 0;
        public String subscriberId = "test";
        public int level = 0;
        public String subscriptionId = null;

        public NonMonitoryBalanceBuilderJava leftShift(RncProfileDetail rnCQuotaProfileDetail) {
            this.quotaProfileId = rnCQuotaProfileDetail.getQuotaProfileId();
            this.serviceId = rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier();
            this.rgId = rnCQuotaProfileDetail.getRatingGroup().getIdentifier();
            this.level = rnCQuotaProfileDetail.getFupLevel();
            return NonMonitoryBalanceBuilderJava.this;
        }

        public ServiceInformation packageId(String packageId) {
            this.packageId = packageId;
            return this;
        }

        public ServiceInformation leftShift(Map map) {

            if(map.containsKey("quotaProfileId")) {
                quotaProfileId = String.valueOf(map.get("quotaProfileId"));
            }
            if(map.containsKey("pkgId")) {
                packageId = String.valueOf(map.get("pkgId"));
            }

            if(map.containsKey("serviceId")) {
                serviceId = (long) map.get("serviceId");
            }

            if(map.containsKey("rgId")) {
                rgId = Integer.valueOf(map.get("rgId").toString());
            }

            if(map.containsKey("subscriberId")) {
                subscriberId = String.valueOf(map.get("subscriberId"));
            }

            if(map.containsKey("level")) {
                level = Integer.valueOf(map.get("level").toString());
            }

            if(map.containsKey("subscriptionId")) {
                subscriptionId = String.valueOf(map.get("subscriptionId"));
            }

            return this;
        }
    }

    public static class UsageSpec {
        long upload;
        long download;
        long total;
        long time;
        long resetTime;


        UsageSpec upload(long upload) {
            this.upload = upload;
            return this;
        }

        UsageSpec download(long download) {
            this.download = download;
            return this;
        }

        UsageSpec total(long total) {
            this.total = total;
            return this;
        }

        UsageSpec time(long time) {
            this.time = time;
            return this;
        }

        UsageSpec resetOn(long time) {
            this.resetTime = time;
            return this;
        }

        UsageSpec resetOn(Date time) {
            this.resetTime = time.getTime();
            return this;
        }



        UsageSpec leftShift(AllowedUsage allowedUsage) {
            download(allowedUsage.getUploadInBytes());
            upload(allowedUsage.getDownloadInBytes());
            total(allowedUsage.getTotalInBytes());
            time(allowedUsage.getTimeInSeconds());
            return this;
        }

        UsageSpec and() {
            return this;
        }
    }





}
