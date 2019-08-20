package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import java.util.Random;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pm.util.Usage;

public class AllowedUsageBuilderJava {


    private long total = new Random().nextInt(Integer.MAX_VALUE);
    private long download = new Random().nextInt(Integer.MAX_VALUE);
    private long upload = new Random().nextInt(Integer.MAX_VALUE);
    private long time = new Random().nextInt(Integer.MAX_VALUE);
    private DataUnit totalUnit = DataUnit.BYTE;
    private DataUnit downloadUnit = DataUnit.BYTE;
    private DataUnit uploadUnit = DataUnit.BYTE;
    private TimeUnit timeUnit = TimeUnit.SECOND;

    public AllowedUsageBuilderJava() {

    }


    public static BillingCycleAllowedUsage billingCycle(){

        AllowedUsageBuilderJava builder = new AllowedUsageBuilderJava();
        return builder.createBillingCycleAllowedUsage();
    }

    public static DailyAllowedUsage daily() {
        AllowedUsageBuilderJava builder = new AllowedUsageBuilderJava();
        return builder.createDailyAllowedUsage();
    }

    public static WeeklyAllowedUsage weekly() {
        AllowedUsageBuilderJava builder = new AllowedUsageBuilderJava();
        return builder.createWeeklyAllowedUsage();
    }

    public static AllowedUsage custom() {
        AllowedUsageBuilderJava builder = new AllowedUsageBuilderJava();
        return builder.createCustomAllowedUsage();
    }

    public DailyAllowedUsage createDailyAllowedUsage() {
        return new DailyAllowedUsage(total, download, upload, time, totalUnit, downloadUnit, uploadUnit, timeUnit);
    }

    public WeeklyAllowedUsage createWeeklyAllowedUsage() {
        return new WeeklyAllowedUsage(total, download, upload, time, totalUnit, downloadUnit, uploadUnit, timeUnit);
    }

    public BillingCycleAllowedUsage createBillingCycleAllowedUsage() {
        return new BillingCycleAllowedUsage(total, download, upload, time, totalUnit, downloadUnit, uploadUnit, timeUnit);
    }

    public CustomAllowedUsage createCustomAllowedUsage() {
        return new CustomAllowedUsage(total, download, upload, time, totalUnit, downloadUnit, uploadUnit, timeUnit);
    }

    public AllowedUsageBuilderJava total(long allowedUsage, DataUnit dataUnit) {
        this.total = allowedUsage;
        this.totalUnit = dataUnit;
        return this;
    }

    public AllowedUsageBuilderJava total(Usage usage) {
        total(usage.getNoOfunit(), usage.getUnit());
        return this;
    }

    public AllowedUsageBuilderJava upload(long allowedUsage, DataUnit dataUnit) {
        this.upload = allowedUsage;
        this.uploadUnit = dataUnit;
        return this;
    }

    public AllowedUsageBuilderJava download(long allowedUsage, DataUnit dataUnit) {
        this.download = allowedUsage;
        this.downloadUnit = dataUnit;
        return this;
    }

    public AllowedUsageBuilderJava time(long allowedTime, TimeUnit timeUnit) {
        this.time = allowedTime;
        this.timeUnit = timeUnit;
        return this;
    }



}