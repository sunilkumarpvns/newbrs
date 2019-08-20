package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;

import javax.xml.bind.annotation.XmlType;

/**
 * A class that will describe the available balance for the subscriber based on rate card
 * @author ishani
 */
@XmlType(propOrder={"rateCardId", "rateCardName", "dailyBalance","weeklyBalance","billingCycleBalance",
        "balanceExpiryTime","dailyResetTime","weeklyResetTime",
        "startTime","reservationTime","lastUpdateTime","renewalInterval","proration"})
public class RnCNonMonetaryRateCardBalance {

    private String rateCardId;
    private String rateCardName;
    private RncBalance dailyBalance;
    private RncBalance weeklyBalance;
    private RncBalance billingCycleBalance;
    private Long balanceExpiryTime;
    private Long dailyResetTime;
    private Long weeklyResetTime;
    private Long startTime;
    private Long reservationTime;
    private Long lastUpdateTime;
    private String renewalInterval; //FIXME need to change to Long once changes have been taken in db
    private Boolean proration;

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


    public Long getBalanceExpiryTime() {
        return balanceExpiryTime;
    }

    public void setBalanceExpiryTime(Long balanceExpiryTime) {
        this.balanceExpiryTime = balanceExpiryTime;
    }


    public Long getDailyResetTime() {
        return dailyResetTime;
    }

    public void setDailyResetTime(Long dailyResetTime) {
        this.dailyResetTime = dailyResetTime;
    }


    public Long getWeeklyResetTime() {
        return weeklyResetTime;
    }

    public void setWeeklyResetTime(Long weeklyResetTime) {
        this.weeklyResetTime = weeklyResetTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Long reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getRenewalInterval() {
        return renewalInterval;
    }

    public void setRenewalInterval(String renewalInterval) {
        this.renewalInterval = renewalInterval;
    }

    public RncBalance getDailyBalance() {
        return dailyBalance;
    }

    public void setDailyBalance(RncBalance dailyBalance) {
        this.dailyBalance = dailyBalance;
    }

    public RncBalance getWeeklyBalance() {
        return weeklyBalance;
    }

    public void setWeeklyBalance(RncBalance weeklyBalance) {
        this.weeklyBalance = weeklyBalance;
    }

    public RncBalance getBillingCycleBalance() {
        return billingCycleBalance;
    }

    public void setBillingCycleBalance(RncBalance billingCycleBalance) {
        this.billingCycleBalance = billingCycleBalance;
    }

    public static RnCNonMonetaryRateCardBalance create(NonMonetaryRateCard nonMonetaryRateCard, RnCNonMonetaryBalance rncNonMonetaryBalance, ChargingType chargingType) {
        RnCNonMonetaryRateCardBalance rncNonMonetaryRateCardBalanceWS = new RnCNonMonetaryRateCardBalance();
        rncNonMonetaryRateCardBalanceWS.setRateCardName(nonMonetaryRateCard.getName());
        rncNonMonetaryRateCardBalanceWS.setRateCardId(nonMonetaryRateCard.getId());
        //setting information
        rncNonMonetaryRateCardBalanceWS.setBalanceExpiryTime(rncNonMonetaryBalance.getBalanceExpiryTime());
        createAggregateBalanceForRnC(rncNonMonetaryRateCardBalanceWS, rncNonMonetaryBalance, nonMonetaryRateCard, chargingType);

        //FIXME last updated time and start time
         //rncNonMonetaryRateCardBalanceWS.setLastUpdateTime(rncNonMonetaryBalance.get);
        rncNonMonetaryRateCardBalanceWS.setRenewalInterval(rncNonMonetaryBalance.getRenewalInterval());
        rncNonMonetaryRateCardBalanceWS.setReservationTime(rncNonMonetaryBalance.getReservationTime());
        rncNonMonetaryRateCardBalanceWS.setWeeklyResetTime(rncNonMonetaryBalance.getWeeklyResetTime());
        rncNonMonetaryRateCardBalanceWS.setProration(nonMonetaryRateCard.getProration());
        return rncNonMonetaryRateCardBalanceWS;

    }

    private static void createAggregateBalanceForRnC(RnCNonMonetaryRateCardBalance rncNonMonetaryRateCardBalanceWs, RnCNonMonetaryBalance rnCNonMonetaryBalance, NonMonetaryRateCard nonMonetaryRateCard, ChargingType chargingType){

        RncBalance dailyUsage = new RncBalance();
        RncBalance weeklyUsage = new RncBalance();
        RncBalance billingCycleBalance = new RncBalance();

        if(ChargingType.SESSION.name() == chargingType.name()) {
            dailyUsage.setTotal(getTotalUsage(CommonConstants.QUOTA_UNLIMITED, chargingType));
            weeklyUsage.setTotal(getTotalUsage(CommonConstants.QUOTA_UNLIMITED, chargingType));

            dailyUsage.setRemaining(getRemainingNonMonetaryQuota(CommonConstants.QUOTA_UNLIMITED , rnCNonMonetaryBalance.getDailyLimit(), chargingType));
            weeklyUsage.setRemaining(getRemainingNonMonetaryQuota(CommonConstants.QUOTA_UNLIMITED,rnCNonMonetaryBalance.getWeeklyLimit(), chargingType));

            dailyUsage.setActual(getActualNonMonetaryQuota(rnCNonMonetaryBalance.getDailyLimit(),rnCNonMonetaryBalance.getReservationTime(), chargingType));
            weeklyUsage.setActual(getActualNonMonetaryQuota(rnCNonMonetaryBalance.getWeeklyLimit(),rnCNonMonetaryBalance.getReservationTime(), chargingType));

            billingCycleBalance.setRemaining(new Usage(null, null, null, rnCNonMonetaryBalance.getBillingCycleAvailable(), null));
            if(rnCNonMonetaryBalance.getBillingCycleTotal()!=CommonConstants.QUOTA_UNDEFINED){
                if(rnCNonMonetaryBalance.getBillingCycleTotal()==CommonConstants.QUOTA_UNLIMITED){
                    billingCycleBalance.setRemainingPulse(CommonConstants.QUOTA_UNLIMITED);
                } else {
                    billingCycleBalance.setRemainingPulse(billingCycleBalance.getRemaining().getTime() / nonMonetaryRateCard.getPulseMinorUnit());
                }
            }
            billingCycleBalance.setTotal(getTotalUsage(rnCNonMonetaryBalance.getBillingCycleTotal(), chargingType));
            billingCycleBalance.setActual(getActualNonMonetaryQuota(rnCNonMonetaryBalance.getBillingCycleAvailable(),rnCNonMonetaryBalance.getReservationTime(), chargingType));
        } else{
            billingCycleBalance.setTotal(getTotalUsage(rnCNonMonetaryBalance.getBillingCycleTotal(), chargingType));
            billingCycleBalance.setRemaining(new Usage(null, null, null, null, rnCNonMonetaryBalance.getBillingCycleAvailable()));
            billingCycleBalance.setActual(getActualNonMonetaryQuota(rnCNonMonetaryBalance.getBillingCycleAvailable(),rnCNonMonetaryBalance.getReservationTime(), chargingType));
        }

        rncNonMonetaryRateCardBalanceWs.setDailyBalance(dailyUsage);
        rncNonMonetaryRateCardBalanceWs.setWeeklyBalance(weeklyUsage);
        rncNonMonetaryRateCardBalanceWs.setBillingCycleBalance(billingCycleBalance);

    }


    private static Usage getTotalUsage(long totalUsage, ChargingType chargingType) {
        if(ChargingType.SESSION.name() == chargingType.name()) {
            return new Usage(null, null, null, totalUsage, null);
        }
        return new Usage(null, null, null, null, totalUsage);
    }

    private static Usage getActualNonMonetaryQuota(long remainingTime, long reservationTime, ChargingType chargingType) {
        if(ChargingType.SESSION.name() == chargingType.name()) {
            return new Usage(null, null, null, remainingTime - reservationTime, null);
        }
        return new Usage(null, null, null, null, remainingTime - reservationTime);
    }

    private static Usage getRemainingNonMonetaryQuota(long total, long limit, ChargingType chargingType) {
        if(ChargingType.SESSION.name() == chargingType.name()) {
            return new Usage(null, null, null, total - limit, null);
        }
        return new Usage(null, null, null, null, total - limit);
    }

    public Boolean getProration() {
        return proration;
    }

    public void setProration(Boolean proration) {
        this.proration = proration;
    }
}
