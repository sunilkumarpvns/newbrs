package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;

import java.util.Date;
import java.util.List;

public class ReportedUsageSummary implements ToStringable{
    private long calculatedVolumePulse;
    private long volumePulse;
    private long calculatedTimePulse;
    private long timePulse;
    private long deductedVolumeBalance;
    private long deductedTimeBalance;
    private double deductedMonetaryBalance;
    private long previousUnAccountedVolume;
    private long previousUnAccountedTime;
    private long currentUnAccountedVolume;
    private long currentUnAccountedTime;
    private long reportedVolume;
    private long reportedTime;
    private long reportedEvent;
    private long reserveVolume;
    private long reserveTime;
    private double reserveMonetaryBalance;
    private ReportingReason reportingReason;
    private ReportOperation reportOperation;
    private long ratingGroup;
    private List<Long> serviceIds;
    private double rate;
    private double discountAmount;
    private double discount;
    private long rateMinorUnit;
    private String currency;
    private String subscriptionId;
    private String packageId;
    private String packageName;
    private String productOfferName;
    private String requestedAction;
    private int level;
    private String quotaProfileId;
    private String quotaProfileName;
    private String rateCardId;
    private String rateCardName;
    private String coreSessionId;
    private String rateCardGroupName;
    private Date sessionStopTime;
    private int exponent;
    private String tariffType;
    private String revenueCode;

    public ReportedUsageSummary(long ratingGroup, List<Long> serviceIds) {
        this.ratingGroup = ratingGroup;
        this.serviceIds = serviceIds;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public long getReserveVolume() {
        return reserveVolume;
    }

    public void setReserveVolume(long reserveVolumeQ) {
        this.reserveVolume = reserveVolumeQ;
    }

    public long getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(long reserveTime) {
        this.reserveTime = reserveTime;
    }

    public double getReserveMonetaryBalance() {
        return reserveMonetaryBalance;
    }

    public void setReserveMonetaryBalance(double reserveMonetaryBalance) {
        this.reserveMonetaryBalance = reserveMonetaryBalance;
    }

    public ReportingReason getReportingReason() {
        return reportingReason;
    }

    public void setReportingReason(ReportingReason reportingReason) {
        this.reportingReason = reportingReason;
    }

    public ReportOperation getReportOperation() {
        return reportOperation;
    }

    public void setReportOperation(ReportOperation reportOperation) {
        this.reportOperation = reportOperation;
    }

    public long getPreviousUnAccountedVolume() {
        return previousUnAccountedVolume;
    }

    public void setPreviousUnAccountedVolume(long previousUnAccountedVolume) {
        this.previousUnAccountedVolume = previousUnAccountedVolume;
    }

    public long getPreviousUnAccountedTime() {
        return previousUnAccountedTime;
    }

    public void setPreviousUnAccountedTime(long previousUnAccountedTime) {
        this.previousUnAccountedTime = previousUnAccountedTime;
    }

    public long getCurrentUnAccountedVolume() {
        return currentUnAccountedVolume;
    }

    public void setCurrentUnAccountedVolume(long currentUnAccountedVolume) {
        this.currentUnAccountedVolume = currentUnAccountedVolume;
    }

    public long getCurrentUnAccountedTime() {
        return currentUnAccountedTime;
    }

    public void setCurrentUnAccountedTime(long currentUnAccountedTime) {
        this.currentUnAccountedTime = currentUnAccountedTime;
    }

    public long getReportedVolume() {
        return reportedVolume;
    }

    public long getReportedTime() {
        return reportedTime;
    }

    public long getCalculatedVolumePulse() {
        return calculatedVolumePulse;
    }

    public void setCalculatedVolumePulse(long calculatedVolumePulse) {
        this.calculatedVolumePulse = calculatedVolumePulse;
    }

    public long getVolumePulse() {
        return volumePulse;
    }

    public void setVolumePulse(long volumePulse) {
        this.volumePulse = volumePulse;
    }

    public long getCalculatedTimePulse() {
        return calculatedTimePulse;
    }

    public void setCalculatedTimePulse(long calculatedTimePulse) {
        this.calculatedTimePulse = calculatedTimePulse;
    }

    public long getTimePulse() {
        return timePulse;
    }

    public void setTimePulse(long timePulse) {
        this.timePulse = timePulse;
    }

    public long getDeductedVolumeBalance() {
        return deductedVolumeBalance;
    }

    public void setDeductedVolumeBalance(long deductedVolumeBalance) {
        this.deductedVolumeBalance = deductedVolumeBalance;
    }

    public long getReportedEvent() {
        return reportedEvent;
    }

    public void setReportedEvent(long reportedEvent) {
        this.reportedEvent = reportedEvent;
    }

    public long getDeductedTimeBalance() {
        return deductedTimeBalance;
    }

    public void setDeductedTimeBalance(long deductedTimeBalance) {
        this.deductedTimeBalance = deductedTimeBalance;
    }

    public double getDeductedMonetaryBalance() {
        return deductedMonetaryBalance;
    }

    public void setDeductedMonetaryBalance(double deductedMonetaryBalance) {
        this.deductedMonetaryBalance = deductedMonetaryBalance;
    }

    public void setReportedVolume(long volume) {
        this.reportedVolume = volume;
    }

    public void setReportedTime(long time) {
        this.reportedTime = time;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId, String name) {
        this.packageId = packageId;
        this.packageName = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductOfferName() {
        return productOfferName;
    }

    public void setProductOfferName(String productOfferName) {
        this.productOfferName = productOfferName;
    }

    public String getRequestedAction() {
        return requestedAction;
    }

    public void setRequestedAction(String requestedAction) {
        this.requestedAction = requestedAction;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public String getQuotaProfileName() {
        return quotaProfileName;
    }

    public String getRateCardId() {
        return rateCardId;
    }

    public void setQuotaProfile(String quotaProfileId, String quotaProfileName) {
        this.quotaProfileId = quotaProfileId;
        this.quotaProfileName = quotaProfileName;
    }

    public void setRateCard(String rateCardId, String rateCardName) {
        this.rateCardId = rateCardId;
        this.rateCardName = rateCardName;
    }

    public long getRatingGroup() {
        return ratingGroup;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setCoreSessionId(String coreSessionId) {
        this.coreSessionId = coreSessionId;
    }

    public String getCoreSessionId() {
        return coreSessionId;
    }

    public void setRateMinorUnit(long rateUnitVal) {
        this.rateMinorUnit = rateUnitVal;
    }

    public long getRateMinorUnit() {
        return rateMinorUnit;
    }

    public String getRateCardName() {
        return rateCardName;
    }

    public void setRateCardName(String rateCardName) {
        this.rateCardName = rateCardName;
    }

    public void setRateCardId(String rateCardId) {
        this.rateCardId = rateCardId;
    }

    public String getRateCardGroupName() {
        return rateCardGroupName;
    }

    public void setRateCardGroupName(String rateCardGroupName) {
        this.rateCardGroupName = rateCardGroupName;
    }

    public Date getSessionStopTime() {
        return sessionStopTime;
    }

    public void setSessionStopTime(Date sessionStopTime) {
        this.sessionStopTime = sessionStopTime;
    }

    public int getExponent() {
        return exponent;
    }

    public void setExponent(int exponent) {
        this.exponent = exponent;
    }

    public String getTariffType() { return tariffType; }

    public void setTariffType(String tariffType) { this.tariffType = tariffType; }

    public String getRevenueCode() {
        return revenueCode;
    }

    public void setRevenueCode(String revenueCode) {
        this.revenueCode = revenueCode;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder toStringBuilder =  new IndentingToStringBuilder() ;

        toStringBuilder.appendHeading(" -- REPORTED USAGE SUMMARY -- ");

        toString(toStringBuilder);

        return toStringBuilder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Core Session Id", coreSessionId);
        builder.append("Rating Group", ratingGroup);
        builder.append("Service Id", serviceIds);
        builder.append("Subscription ", subscriptionId);
        builder.append("Package", packageName +"(" + packageId + ")");
        builder.append("Product Offer ", productOfferName);
        builder.append("Quota Profile " , quotaProfileName + "(" + quotaProfileId + "), Level" + level);
        builder.append("Rate Card " , rateCardName + "(" + rateCardId + ")");
        builder.append("Pulse", volumePulse + " Volume, " + timePulse + " Time");
        builder.append("Rate", rate);
        builder.append("Revenue Code", revenueCode);
        builder.append("Ratecard Group Name", rateCardGroupName);
        builder.append("RateMinorUnit", rateMinorUnit);
        builder.append("Currency", currency);
        builder.append("Exponent", exponent);
        builder.append("Report Operation", reportOperation);
        builder.append("Reporting Reason", reportingReason);
        builder.append("Calculated Pulse", calculatedTimePulse + " Volume, " + calculatedTimePulse + " Time ");
        builder.append("Calculated Pulse", calculatedTimePulse + " Volume, " + calculatedTimePulse + " Time ");
        builder.append("Reported Quota", reportedVolume + " Volume, " + reportedTime + " Time, " + reportedEvent + " Event");
        builder.append("Reserve Quota", reserveVolume + " Volume, "
                + reserveTime + " Time, "
                + reserveMonetaryBalance + " Monetary");
        builder.append("Deducated Balance", deductedVolumeBalance + " Volume, "
                + deductedTimeBalance + " Time, "
                + deductedMonetaryBalance + " Monetary");

        builder.append("Current UnAccounted", currentUnAccountedVolume + " Volume, " + currentUnAccountedTime + " Time ");
        builder.append("Previous UnAccounted", previousUnAccountedVolume + " Volume, " + previousUnAccountedTime + " Time ");

    }

}
