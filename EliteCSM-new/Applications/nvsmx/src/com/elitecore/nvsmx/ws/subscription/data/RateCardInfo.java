package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"rateCardId", "rateCardName"})
public class RateCardInfo {
    private String rateCardId;
    private String rateCardName;
    public RateCardInfo(){
        //required by method to have a default constructor
    }

    public RateCardInfo(String rateCardId, String rateCardName) {
        super();
        this.rateCardId = rateCardId;
        this.rateCardName = rateCardName;
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

}
