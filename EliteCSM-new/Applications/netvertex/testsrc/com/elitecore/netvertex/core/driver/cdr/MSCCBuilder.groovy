package com.elitecore.netvertex.core.driver.cdr

import com.elitecore.corenetvertex.data.GyServiceUnits
import com.elitecore.netvertex.gateway.diameter.gy.MSCC

class MSCCBuilder {

    private  MSCC reportedMSCC
    @Delegate private GyServiceUnits gyServiceUnits = new GyServiceUnits();

    public MSCCBuilder() {
        gyServiceUnits = new GyServiceUnits();
        reportedMSCC = new MSCC();
        reportedMSCC.setUsedServiceUnits(gyServiceUnits)
    }


    public static MSCC mscc(@DelegatesTo(MSCCBuilder) Closure closure ){
        MSCCBuilder builder = new MSCCBuilder();
        def code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        code()
        return builder.reportedMSCC;

    }

    public void RG(long ratingGroup) {
        reportedMSCC.setRatingGroup(ratingGroup);
    }

    public void Services(long... services) {
        reportedMSCC.setServiceIdentifiers(Arrays.asList(services));
    }

    public void usu(@DelegatesTo(MSCCBuilder) Closure closure ){
        def code = closure.rehydrate(this, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        reportedMSCC.setUsedServiceUnits(gyServiceUnits)
        code()
    }

    public void gsu(@DelegatesTo(MSCCBuilder) Closure closure ){
        def code = closure.rehydrate(this, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        reportedMSCC.setGrantedServiceUnits(gyServiceUnits)
        code()
    }

    public void volume(long octets) {
        this.gyServiceUnits.setVolume(octets)
    }

    public void time(long seconds) {
        this.gyServiceUnits.setTime(seconds)
    }

    public void balanceId(String balanceId) {
        this.gyServiceUnits.setBalanceId(balanceId)
    }

    public void monetaryBalanceId(String monetaryBalanceId) {
        this.gyServiceUnits.setMonetaryBalanceId(monetaryBalanceId)
    }

    public void rate(double rate) {
        this.gyServiceUnits.setRate(rate)
    }

    public void volumePulse(long volumePulse) {
        this.gyServiceUnits.setVolumePulse(volumePulse)
    }

    public void timePulse(long timePulse) {
        this.gyServiceUnits.setTimePulse(timePulse)
    }

    public void calculatedVolumePulse(long calculatedVolumePulse) {
        this.gyServiceUnits.setCalculateVolumePulse(calculatedVolumePulse)
    }

    public void calculatedTimePulse(long calculatedTimePulse) {
        this.gyServiceUnits.setCalculatedTimePulse(calculatedTimePulse)
    }


}
