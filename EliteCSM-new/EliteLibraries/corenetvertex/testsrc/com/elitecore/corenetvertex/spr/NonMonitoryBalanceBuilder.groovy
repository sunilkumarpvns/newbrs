package com.elitecore.corenetvertex.spr

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail

public class NonMonitoryBalanceBuilder extends NonMonetoryBalanceBuilder {


    static NonMonetoryBalance balance(@DelegatesTo(NonMonitoryBalanceBuilder)Closure closure){
        def builder = new NonMonitoryBalanceBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        return builder.build();
    }

    static NonMonetoryBalance balance(Map map, @DelegatesTo(NonMonitoryBalanceBuilder)Closure closure){
        def builder = new NonMonitoryBalanceBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        return builder.build();
    }

    static NonMonitoryBalanceBuilder balanceIs(@DelegatesTo(NonMonitoryBalanceBuilder)Closure closure){
        def builder = new NonMonitoryBalanceBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        return builder;
    }

    static NonMonitoryBalanceBuilder balanceIs(Map map, @DelegatesTo(NonMonitoryBalanceBuilder)Closure closure){
        def builder = new NonMonitoryBalanceBuilder()
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        return builder;
    }


    public void info(@DelegatesTo(ServiceInformation)Closure closure) {
        this.info = new ServiceInformation()
        def code = closure.rehydrate(info, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    NonMonetoryBalanceBuilder resetOn(Date date) {
      super.resetOn(date);
        return this;
    }


    public void billingCycle(Map map=[volume:0l, reserveVolume:0l, time:0l, reserveTime:0l, resetOn:1000]) {
        billingCycleUsage = new UsageSpec()
        billingCycleUsage << map
    }

    public void day(Map map=[volume:0l, time:0l, resetOn:1000]) {
        dayUsage = new CounterSpec();
        dayUsage << map
    }


    public def week(Map map=[volume:0l, time:0l, resetOn:1000]) {
        weekUsage = new CounterSpec();
        weekUsage << map
    }

    NonMonitoryBalanceBuilder and() {
        return this;
    }

    @Override
    protected com.elitecore.corenetvertex.spr.CounterSpec createCounterSpec() {
        return new CounterSpec();
    }

    @Override
    protected com.elitecore.corenetvertex.spr.UsageSpec createUsageSpec() {
        return new UsageSpec();
    }

    @Override
    protected NonMonetoryBalanceBuilder.ServiceInformation createServiceInfo() {
        return new ServiceInformation();
    }

    public static class ServiceInformation extends NonMonetoryBalanceBuilder.ServiceInformation{


        public ServiceInformation leftShift(RncProfileDetail rnCQuotaProfileDetail) {
            fromDetail(rnCQuotaProfileDetail);
            return this;
        }

        public ServiceInformation leftShift(Map map) {

            if(map.containsKey("quotaProfileId")) {
                quotaProfileId = map.get("quotaProfileId")
            }
            if(map.containsKey("pkgId")) {
                packageId = map.get("pkgId")
            }

            if(map.containsKey("serviceId")) {
                serviceId = map.get("serviceId")
            }

            if(map.containsKey("rgId")) {
                rgId = map.get("rgId")
            }

            if(map.containsKey("subscriberId")) {
                subscriberId = map.get("subscriberId")
            }

            if(map.containsKey("level")) {
                level = map.get("level")
            }

            if(map.containsKey("subscriptionId")) {
                subscriptionId = map.get("subscriptionId")
            }

            return this;
        }
    }

    public static class UsageSpec extends com.elitecore.corenetvertex.spr.UsageSpec{

        public UsageSpec leftShift(Map map=[volume:0l, reserveVolume:0l, time:0l, reserveTime:0l, resetOn:1000]) {
            volume(map.get("volume",0l));
            reserveVolume(map.get("reserveVolume",0l))
            time(map.get("time",0l))
            reserveTime(map.get("reserveTime",0l))
            resetOn(map.get("resetOn",1000l))
            return this;
        }

        UsageSpec and() {
            return this;
        }
    }

    public static class CounterSpec extends com.elitecore.corenetvertex.spr.CounterSpec{

        public CounterSpec leftShift(Map map=[volume:0l, time:0l, resetOn:1000]) {
            volume(map.get("volume",0l));
            time(map.get("time",0l))
            resetOn(map.get("resetOn",1000l))
            return this;
        }
    }





}
