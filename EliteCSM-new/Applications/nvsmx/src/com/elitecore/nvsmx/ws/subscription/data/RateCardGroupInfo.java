package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder={"rateCardGroupId", "rateCardGroupName","rateCardGroupDescription", "advanceCondition", "peakRateCard","offPeakRateCard","offPeakTimeSlot"})
public class RateCardGroupInfo {
    private String rateCardGroupId;
    private String rateCardGroupName;
    private String rateCardGroupDescription;
    private String advanceCondition;
    private RateCardInfo peakRateCard;
    private RateCardInfo offPeakRateCard;
    private List<TimeSlotInformation> offPeakTimeSlot;


    public RateCardGroupInfo(){
        //required by method to have a default constructor
    }

    public RateCardGroupInfo(String rateCardGroupId, String rateCardGroupName, String rateCardGroupDescription, String advanceCondition,RateCardInfo peakRateCard, RateCardInfo offPeakRateCard) {
        super();
        this.rateCardGroupId = rateCardGroupId;
        this.rateCardGroupName = rateCardGroupName;
        this.rateCardGroupDescription = rateCardGroupDescription;
        this.advanceCondition = advanceCondition;
        this.peakRateCard = peakRateCard;
        this.offPeakRateCard = offPeakRateCard;
    }

    public String getRateCardGroupId() {
        return rateCardGroupId;
    }

    public void setRateCardGroupId(String rateCardGroupId) {
        this.rateCardGroupId = rateCardGroupId;
    }

    public String getRateCardGroupName() {
        return rateCardGroupName;
    }

    public void setRateCardGroupName(String rateCardGroupName) {
        this.rateCardGroupName = rateCardGroupName;
    }

    public String getRateCardGroupDescription() {
        return rateCardGroupDescription;
    }

    public void setRateCardGroupDescription(String rateCardGroupDescription) {
        this.rateCardGroupDescription = rateCardGroupDescription;
    }

    public String getAdvanceCondition() {
        return advanceCondition;
    }

    public void setAdvanceCondition(String advanceCondition) {
        this.advanceCondition = advanceCondition;
    }

    public RateCardInfo getPeakRateCard() {
        return peakRateCard;
    }

    public void setPeakRateCard(RateCardInfo peakRateCard) {
        this.peakRateCard = peakRateCard;
    }

    public RateCardInfo getOffPeakRateCard() {
        return offPeakRateCard;
    }

    public void setOffPeakRateCard(RateCardInfo offPeakRateCard) {
        this.offPeakRateCard = offPeakRateCard;
    }

    public List<TimeSlotInformation> getOffPeakTimeSlot() {
        return offPeakTimeSlot;
    }

    public void setOffPeakTimeSlot(List<TimeSlotInformation> offPeakTimeSlot) {
        this.offPeakTimeSlot = offPeakTimeSlot;
    }
}
