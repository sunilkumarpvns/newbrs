package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile

import com.elitecore.corenetvertex.constants.DataUnit
import com.elitecore.corenetvertex.constants.TimeUnit
import com.elitecore.corenetvertex.pm.util.Usage

public class AllowedUsageBuilder {


    private long total;
    private long download;
    private long upload;
    private long time;
    private DataUnit totalUnit = DataUnit.BYTE;
    private DataUnit downloadUnit = DataUnit.BYTE;
    private DataUnit uploadUnit = DataUnit.BYTE;
    private TimeUnit timeUnit = TimeUnit.SECOND;

    AllowedUsageBuilder() {

    }


    public static BillingCycleAllowedUsage billingCycle(@DelegatesTo(AllowedUsageBuilder)Closure closure){

        def builder = new AllowedUsageBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.OWNER_FIRST
        code()
        return builder.createBillingCycleAllowedUsage()
    }

    static DailyAllowedUsage daily(@DelegatesTo(AllowedUsageBuilder)Closure closure) {
        def builder = new AllowedUsageBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        return builder.createDailyAllowedUsage()
    }

    static WeeklyAllowedUsage weekly(@DelegatesTo(AllowedUsageBuilder)Closure closure) {
        def builder = new AllowedUsageBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        return builder.createWeeklyAllowedUsage()
    }

    static AllowedUsage custom(@DelegatesTo(AllowedUsageBuilder)Closure closure) {
        def builder = new AllowedUsageBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        return builder.createCustomAllowedUsage()
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

    public void total(long allowedUsage, DataUnit dataUnit) {
        this.total = allowedUsage
        this.totalUnit = dataUnit
    }

    public void total(Usage usage) {
        total(usage.noOfunit, usage.unit)
    }

    public void upload(long allowedUsage, DataUnit dataUnit) {
        this.upload = allowedUsage;
        this.uploadUnit = dataUnit;
    }

    public void download(long allowedUsage, DataUnit dataUnit) {
        this.download = allowedUsage;
        this.downloadUnit = dataUnit;
    }

    public void time(long allowedTime, TimeUnit timeUnit) {
        this.time = allowedTime;
        this.timeUnit = timeUnit;
    }



}