package com.elitecore.corenetvertex.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GyServiceUnits {

	private long volume;
	private long time;
	private long money;
	private long serviceSpecificUnits;
	private String quotaProfileIdOrRateCardId;
	private boolean isReservationRequired;
	private String packageId;
	private String productOfferId;
	private String subscriptionId;
	private String balanceId;
    private String monetaryBalanceId;
    private String rateCardGroupId;
    private String rateCardId;
    private String rateCardName;
    private String rateCardGroupName;
    private double reservedMonetaryBalance;
	private double rate;
	private long rateMinorUnit;
	private long volumePulse;
	private long timePulse;
	private long calculateVolumePulse;
	private long calculatedTimePulse;
    private long pulseMinorUnit;
	private double actualRate;
	private double discount;
	private int fupLevel;
	private int requestedAction;
	private double discountAmount;
	private String revenueCode;

    public GyServiceUnits(){
	}


    public GyServiceUnits(String packageId,
						  long volume,
						  String quotaProfileIdOrRateCardId,
						  long time,
						  String subscriptionId,
						  long money,
						  String balanceId,
						  boolean isReservationRequired,
						  String monetaryBalanceId,
						  double reservedMonetaryBalance,
						  double rate,
						  long timePulse,
						  long rateMinorUnit,
						  long pulseMinorUnit,
						  long serviceSpecificUnits,
						  int requestedAction,
						  int fupLevel) {
        this.volume = volume;
        this.time = time;
        this.money = money;
        this.quotaProfileIdOrRateCardId = quotaProfileIdOrRateCardId;
        this.isReservationRequired = isReservationRequired;
        this.packageId = packageId;
        this.subscriptionId = subscriptionId;
        this.balanceId = balanceId;
        this.monetaryBalanceId = monetaryBalanceId;
        this.reservedMonetaryBalance = reservedMonetaryBalance;
        this.rate = rate;
        this.timePulse = timePulse;
        this.rateMinorUnit = rateMinorUnit;
		this.pulseMinorUnit = pulseMinorUnit;
		this.serviceSpecificUnits = serviceSpecificUnits;
		this.requestedAction = requestedAction;
		this.fupLevel = fupLevel;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getProductOfferId() {
    	return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
    	this.productOfferId = productOfferId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public void setQuotaProfileIdOrRateCardId(String quotaProfileId) {
		this.quotaProfileIdOrRateCardId = quotaProfileId;
	}

	public void setReservationRequired(boolean reservationRequired) {
		isReservationRequired = reservationRequired;
	}

	public String getQuotaProfileIdOrRateCardId() {
		return quotaProfileIdOrRateCardId;
	}

	public boolean isNonMonetaryReservationRequired() {
		return isReservationRequired;
	}

	public String getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(String balanceId) {
		this.balanceId = balanceId;
	}

    public String getMonetaryBalanceId() {
        return monetaryBalanceId;
    }

    public void setMonetaryBalanceId(String monetaryBalanceId) {
        this.monetaryBalanceId = monetaryBalanceId;
    }

	public double getReservedMonetaryBalance() {
		return reservedMonetaryBalance;
	}

	public void setReservedMonetaryBalance(double reservedMonetaryBalance) {
		this.reservedMonetaryBalance = reservedMonetaryBalance;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getActualRate() {
		return actualRate;
	}

	public void setActualRate(double actualRate) {
		this.actualRate = actualRate;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public long getRateMinorUnit() {
		return rateMinorUnit;
	}

	public void setRateMinorUnit(long rateMinorUnit) {
		this.rateMinorUnit = rateMinorUnit;
	}
	
	public long getVolumePulse() {
		return volumePulse;
	}

	public void setVolumePulse(long volumePulse) {
		this.volumePulse = volumePulse;
	}

	public String getRateCardGroupId() {
		return rateCardGroupId;
	}

	public void setRateCardGroupId(String rateCardGroupId) {
		this.rateCardGroupId = rateCardGroupId;
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

	public String getRateCardGroupName() {
		return rateCardGroupName;
	}

	public void setRateCardGroupName(String rateCardGroupName) {
		this.rateCardGroupName = rateCardGroupName;
	}

	public long getTimePulse() {
		return timePulse;
	}

	public void setTimePulse(long timePulse) {
		this.timePulse = timePulse;
	}

	public long getCalculateVolumePulse() {
		return calculateVolumePulse;
	}

	public void setCalculateVolumePulse(long calculateVolumePulse) {
		this.calculateVolumePulse = calculateVolumePulse;
	}

	public long getCalculatedTimePulse() {
		return calculatedTimePulse;
	}

	public void setCalculatedTimePulse(long calculatedTimePulse) {
		this.calculatedTimePulse = calculatedTimePulse;
	}

	public void addVolume(long volume) {
        this.volume = +volume;
    }

    public void addTime(long time) {
        this.time += time;
    }

    public void add(GyServiceUnits gyServiceUnits) {
        this.addTime(gyServiceUnits.getTime());
        this.addVolume(gyServiceUnits.getVolume());
        this.addReserveMonetaryBalance(gyServiceUnits.getReservedMonetaryBalance());
    }

	public void setServiceSpecificUnits(long serviceSpecificUnits) {
		this.serviceSpecificUnits = serviceSpecificUnits;
	}

	public long getServiceSpecificUnits() {
		return serviceSpecificUnits;
	}

	public int getRequestedAction() {
		return requestedAction;
	}

	public void setRequestedAction(int requestedAction) {
		this.requestedAction = requestedAction;
	}

	private void addReserveMonetaryBalance(double reservedMonetaryBalance) {
        this.reservedMonetaryBalance = +reservedMonetaryBalance;
    }

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
        IndentingWriter indentingPrintWriter = new IndentingPrintWriter(stringWriter);

		toString(indentingPrintWriter);

		return stringWriter.toString();
	}

	public void toString(IndentingWriter ipWriter) {

		if (volume > 0) {
			ipWriter.println("Volume: " + volume);
		}

		if (time > 0) {
			ipWriter.println("Time: " + time);
		}

		if (money > 0) {
			ipWriter.println("Money: " + money);
		}

		if (serviceSpecificUnits > 0) {
			ipWriter.println("Service Specific Units: " + serviceSpecificUnits);
		}

		ipWriter.println("Quota Profile Or Rate Card Id: " + quotaProfileIdOrRateCardId);
		ipWriter.println("Package ID: " + packageId);
		ipWriter.println("Subscription ID: " + subscriptionId);
		ipWriter.println("Level: " + fupLevel);
		ipWriter.println("Balance ID: " + balanceId);
		ipWriter.println("Monetary Balance ID: " + monetaryBalanceId);
		ipWriter.println("Reservation Required: " + isReservationRequired);
        ipWriter.println("Reserve Monetary Balance: " + reservedMonetaryBalance);
        ipWriter.println("Ratecard Name: " + rateCardName);
        ipWriter.println("Ratecard Group Name: " + rateCardGroupName);
        ipWriter.println("Rate: " + rate);
        ipWriter.println("Volume pulse: " + volumePulse);
        ipWriter.println("Time Pulse: " + timePulse);
        ipWriter.println("Rate Minor Unit: " + rateMinorUnit);
		ipWriter.println("Requested Action: " + requestedAction);
	}

    public GyServiceUnits copy() {
        GyServiceUnits copiedGyServiceUnits = new GyServiceUnits(packageId, volume, quotaProfileIdOrRateCardId, time, subscriptionId, money, balanceId, isReservationRequired, monetaryBalanceId, reservedMonetaryBalance, rate, timePulse, rateMinorUnit, pulseMinorUnit, serviceSpecificUnits, requestedAction, fupLevel);
        copiedGyServiceUnits.setRateCardName(rateCardName);
        copiedGyServiceUnits.setRateCardId(rateCardId);
        copiedGyServiceUnits.setRateCardGroupName(rateCardGroupName);
        copiedGyServiceUnits.setProductOfferId(productOfferId);
        copiedGyServiceUnits.setDiscount(discount);
		copiedGyServiceUnits.setActualRate(actualRate);
		copiedGyServiceUnits.setRevenueCode(revenueCode);
        return  copiedGyServiceUnits;
    }

    public void setPulseMinorUnit(long pulseMinorUnit) {
        this.pulseMinorUnit = pulseMinorUnit;
    }

    public long getPulseMinorUnit() {
        return pulseMinorUnit;
    }

	public long calculateFloorPulse(long reportedQuota) {
    	return (long) Math.floor((double) reportedQuota / pulseMinorUnit);
    }

	public long calculateCeilPulse(long reportedQuota) {
    	return (long) Math.ceil((double) reportedQuota / pulseMinorUnit);
    }

	public long calculateDeductableQuota(long calculatedPulse) {
		return calculatedPulse * pulseMinorUnit;
	}

	public BigDecimal calculateDeductableMoney(long calculatedPulse) {
		return BigDecimal.valueOf(calculatedPulse).multiply(BigDecimal.valueOf(rate)).divide(BigDecimal.valueOf(rateMinorUnit),
				CommonConstants.RATE_PRECESION, RoundingMode.HALF_UP);
	}

	public boolean isMonetaryBalanceReserved(){
		return monetaryBalanceId!=null;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public String getRevenueCode() {
		return revenueCode;
	}

	public void setRevenueCode(String revenueCode) {
		this.revenueCode = revenueCode;
	}

	public int getFupLevel() {
		return fupLevel;
	}

	public void setFupLevel(int fupLevel) {
		this.fupLevel = fupLevel;
	}
}
