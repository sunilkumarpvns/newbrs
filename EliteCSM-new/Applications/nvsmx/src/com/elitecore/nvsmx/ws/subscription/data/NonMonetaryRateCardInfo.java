package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.Uom;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"rateCardId", "rateCardName","rateCardDescription","time","timeUnit","pulse","pulseUnit","event"})
public class NonMonetaryRateCardInfo {
    private String rateCardId;
    private String rateCardName;
    private String rateCardDescription;
    private Uom pulseUnit;
    private long pulse;
    private long time;
    private Uom timeUnit;
    private long event;
    public NonMonetaryRateCardInfo(){
        //required by method to have a default constructor
    }

    public NonMonetaryRateCardInfo(String rateCardId, String rateCardName, String rateCardDescription, long time, Uom timeUnit, long pulse, Uom pulseUnit,long event) {
        super();
        this.rateCardId = rateCardId;
        this.rateCardName = rateCardName;
        this.rateCardDescription = rateCardDescription;
        this.time = time;
        this.timeUnit = timeUnit;
        this.pulse = pulse;
        this.pulseUnit = pulseUnit;
        this.event = event;
    }

    public String getRateCardId() {
        return rateCardId;
    }

    public void setRateCardId(String rateCardId) {
        this.rateCardId = rateCardId;
    }

    public String getRateCardName() {
        return rateCardName;
    }

    public void setRateCardName(String rateCardName) {
        this.rateCardName = rateCardName;
    }

    public String getRateCardDescription() {
        return rateCardDescription;
    }

    public void setRateCardDescription(String rateCardDescription) {
        this.rateCardDescription = rateCardDescription;
    }

    public Uom getPulseUnit() {
        return pulseUnit;
    }

    public void setPulseUnit(Uom pulseUnit) {
        this.pulseUnit = pulseUnit;
    }

    public long getPulse() {
        return pulse;
    }

    public void setPulse(long pulse) {
        this.pulse = pulse;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Uom getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Uom timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getEvent() {
        return event;
    }

    public void setEvent(long event) {
        this.event = event;
    }
}
