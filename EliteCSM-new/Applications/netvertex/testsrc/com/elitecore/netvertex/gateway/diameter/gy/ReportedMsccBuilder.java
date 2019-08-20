package com.elitecore.netvertex.gateway.diameter.gy;

import com.elitecore.corenetvertex.data.GyServiceUnits;

import static org.apache.commons.lang3.RandomUtils.nextLong;

public class ReportedMsccBuilder {
    private MSCC mscc;
    public ReportedMsccBuilder() {
        mscc = new MSCC();

        GyServiceUnits serviceUnits = new GyServiceUnits();
        serviceUnits.setTime(nextLong(1,100));
        serviceUnits.setVolume(nextLong(1,100));

        mscc.setRatingGroup(0);

        mscc.setUsedServiceUnits(serviceUnits);
    }

    public ReportedMsccBuilder reportingReasonFina() {
        mscc.setReportingReason(ReportingReason.FINAL);
        return this;
    }

    public ReportedMsccBuilder rg(long rg) {
        mscc.setRatingGroup(rg);
        return this;
    }

    public ReportedMsccBuilder volume(long volume) {
        mscc.getUsedServiceUnits().setVolume(volume);
        return this;
    }

    public ReportedMsccBuilder time(long time) {
        mscc.getUsedServiceUnits().setTime(time);
        return this;
    }

    public MSCC build() {
        return mscc;
    }
}
