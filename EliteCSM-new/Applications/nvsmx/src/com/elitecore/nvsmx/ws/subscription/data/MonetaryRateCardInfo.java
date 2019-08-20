package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.Uom;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder={"rateCardId", "rateCardName","rateCardDescription","pulseUnit","rateUnit", "keyOne", "keyTwo","rateCardVersionDetails"})
public class MonetaryRateCardInfo {
    private String rateCardId;
    private String rateCardName;
    private String rateCardDescription;
    private Uom pulseUnit;
    private Uom rateUnit;
    private String keyOne;
    private String keyTwo;
    private List<RateCardVersionDetailInfo> rateCardVersionDetails;

    public MonetaryRateCardInfo(){
        //required by method to have a default constructor
    }

    public MonetaryRateCardInfo(String rateCardId, String rateCardName, String rateCardDescription, String keyOne, String keyTwo, Uom rateUnit, Uom pulseUnit) {
        super();
        this.rateCardId = rateCardId;
        this.rateCardName = rateCardName;
        this.rateCardDescription = rateCardDescription;
        this.keyOne = keyOne;
        this.keyTwo = keyTwo;
        this.rateUnit = rateUnit;
        this.pulseUnit = pulseUnit;
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

    public Uom getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(Uom rateUnit) {
        this.rateUnit = rateUnit;
    }

    public String getKeyOne() {
        return keyOne;
    }

    public void setKeyOne(String keyOne) {
        this.keyOne = keyOne;
    }

    public String getKeyTwo() {
        return keyTwo;
    }

    public void setKeyTwo(String keyTwo) {
        this.keyTwo = keyTwo;
    }

    public List<RateCardVersionDetailInfo> getRateCardVersionDetails() {
        return rateCardVersionDetails;
    }

    public void setRateCardVersionDetails(List<RateCardVersionDetailInfo> rateCardVersionDetails) {
        this.rateCardVersionDetails = rateCardVersionDetails;
    }
}
