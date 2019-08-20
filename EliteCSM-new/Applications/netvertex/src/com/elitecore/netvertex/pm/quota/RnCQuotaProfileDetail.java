package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.QuotaProfileBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QuotaProfileDetail;
import com.elitecore.netvertex.rnc.PulseCalculator;
import com.elitecore.netvertex.rnc.RateCalculator;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RnCQuotaProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail implements QuotaProfileDetail {

    private static final String MODULE = "RnCQuotaProfileDetail";
    private static final long serialVersionUID = 1L;
    private transient PulseCalculator volumePulseCalculator;
    private transient PulseCalculator timePulseCalculator;
    private transient RateCalculator rateCalculator;
    private static ConcurrencyConfiguration concurrencyConfiguration = new ConcurrencyConfiguration();

    public RnCQuotaProfileDetail(String quotaProfileId,
                                 String name,
                                 DataServiceType dataServiceType,
                                 int fupLevel,
                                 RatingGroup ratingGroup,
                                 Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage,
                                 long volumePulse, long timePulse, long volumePulseInBytes, long timePulseInSeconds,
                                 String voulumePulseUnit, String timePulseUnit, double rate, UsageType rateUnit,
                                 QuotaUsageType quotaType, VolumeUnitType unitType, boolean isHsqLevel, String pccProfileName,
                                 long volumeCarryForwardLimit, long timeCarryForwardLimit, String revenueDetail) {
        super(name, dataServiceType, fupLevel, ratingGroup, aggregationKeyToAllowedUsage, volumePulse, timePulse,
				volumePulseInBytes, timePulseInSeconds, voulumePulseUnit, timePulseUnit,
                rate, rateUnit, quotaType, unitType, quotaProfileId, isHsqLevel, pccProfileName,volumeCarryForwardLimit, timeCarryForwardLimit, revenueDetail);

        timePulseCalculator = new PulseCalculator(timePulseInSeconds);
        volumePulseCalculator = new PulseCalculator(volumePulseInBytes);

        if (rate > 0) {
            if (rateUnit == UsageType.VOLUME) {
                rateCalculator = new RateCalculator.VolumeBaseRateCalculator(rate);
            } else {
                rateCalculator = new RateCalculator.TimeBaseRateCalculator(rate);
            }
        } else {
            rateCalculator = new RateCalculator.VolumeBaseRateCalculator(1);
        }

    }


    @Override
    public AllowedUsage getDailyAllowedUsage() {
        AllowedUsage allowedUsage = super.getDailyAllowedUsage();
        if (allowedUsage == null) {
            allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
        }
        return allowedUsage;
    }

    @Override
    public AllowedUsage getWeeklyAllowedUsage() {
        AllowedUsage allowedUsage = super.getWeeklyAllowedUsage();
        if (allowedUsage == null) {
            allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
        }
        return allowedUsage;
    }

    public TotalBalance getTotalBalance(NonMonetoryBalance serviceRgNonMonitoryBalance, Balance unaccountedUsage,
                                        Calendar currentTime) {
        TotalBalance totalBalance = new TotalBalance(getDailyAllowedUsage().getBalance(serviceRgNonMonitoryBalance, currentTime),
                getWeeklyAllowedUsage().getBalance(serviceRgNonMonitoryBalance, currentTime),
                AllowedUsage.ALWAYS_ALLOWED.getBalance(serviceRgNonMonitoryBalance, currentTime),
                getBillingCycleAllowedUsage().getBalance(serviceRgNonMonitoryBalance, currentTime));

        totalBalance.subtract(unaccountedUsage);
        return totalBalance;
    }

    public boolean hasRatingDetail() {
        return getRate() != 0.0;
    }


    public RateCalculator getRateCalculator() {
        return rateCalculator;
    }

    public PulseCalculator getVolumePulseCalculator() {
        return volumePulseCalculator;
    }

    public PulseCalculator getTimePulseCalculator() {
        return timePulseCalculator;
    }


    @Override
    public boolean apply(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation) {

                                                                                                                                                                                                        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Selection Started for "
                    + ", Quota Profile: " + getQuotaProfileId()
                    + ", Package: " + packageId);
        }

        String subscriptionId = Objects.isNull(subscription) ? null : subscription.getId();
        NonMonetoryBalance nonMonetoryBalance;
        QuotaProfileBalance quotaProfileBalance;
        try {
            quotaProfileBalance = policyContext.getCurrentBalance().getPackageBalance(Objects.isNull(subscriptionId) ? packageId : subscriptionId)
                    .getBalance(getQuotaProfileId());
            nonMonetoryBalance = getBalanceForLevel(quotaProfileBalance, getFupLevel());
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Quota profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching non monetary balance, Cause: "
                    + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
            return false;
        }

        MonetaryBalance monetaryBalance = null;
        if (isRateConfigured()) {
            try {
                monetaryBalance = policyContext.getCurrentMonetaryBalance().getServiceBalance(PCRFKeyValueConstants.DATA_SERVICE_ID.val);
                if (Objects.isNull(monetaryBalance)) {
                    if (getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, "Quota profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ", Monetary balance does not exist.");
                    }
                    return false;
                }
            } catch (OperationFailedException e) {
                getLogger().error(MODULE, "Quota profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching monetary balance, Cause: "
                        + e.getMessage());
                if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                    getLogger().trace(MODULE, e);
                }

                return false;
            }
        }

        if (isBalanceExist(nonMonetoryBalance) == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Quota profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ", Non monetary balance or monetary quota does not sufficient.");
            }
            return false;
        }

        // DOING RESERVATION
        MSCC newMSCC = createMSCC(nonMonetoryBalance, monetaryBalance, quotaProfileBalance, packageId, subscriptionId, policyContext.getPolicyRepository().getSliceConfiguration());

        if (newMSCC == null) {
            return false;
        }

        setQuotaReservationFlag(newMSCC, policyContext);
        quotaReservation.put(newMSCC);

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Selected Service: " + newMSCC.getServiceIdentifiers()
                    + ", RG: " + newMSCC.getRatingGroup()
                    + ", Quota Profile: " + getQuotaProfileId()
                    + ", Package: " + packageId
                    + ", Subscription: " + subscriptionId
                    + ", FUP Level: " + getFupLevel());
        }
        return true;
    }

    private boolean isBalanceExist(NonMonetoryBalance nonMonetoryBalance) {
        boolean isExist;

        if(QuotaUsageType.VOLUME == getQuotaUnit()) {
            isExist = nonMonetoryBalance.getActualVolumeBalance() > 0;
        } else if(QuotaUsageType.TIME == getQuotaUnit()) {
            isExist = nonMonetoryBalance.getActualTimeBalance() > 0;
        } else {
            isExist = nonMonetoryBalance.getActualVolumeBalance() > 0 && nonMonetoryBalance.getActualTimeBalance() > 0;
        }

        return isExist;
    }

    public NonMonetoryBalance getBalanceForLevel(QuotaProfileBalance quotaProfileBalance, int level) {
        return quotaProfileBalance.getBalance(getDataServiceType().getServiceIdentifier(), getRatingGroup().getIdentifier(), level);
    }


    private MSCC createMSCC(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance, QuotaProfileBalance quotaProfileBalance, String packageId,
                            String subscriptionId, DataSliceConfiguration dataSliceConfiguration) {

        //FIXME  Slice configuration should get initialized only once and not every time.
        boolean finalSlice = false;
        boolean isVolumeReservationWatermarkReached = false;
        boolean isTimeReservationWatermarkReached = false;
        boolean isMonetaryFinalSlice = false;
        Slice volumeSlice = null;
        Slice timeSlice = null;

        if (CommonConstants.QUOTA_UNDEFINED != nonMonetoryBalance.getBillingCycleTotalVolume()) {

            if (nonMonetoryBalance.getDailyVolume() >= getDailyAllowedUsage().getTotalInBytes()
                    || nonMonetoryBalance.getWeeklyVolume() >= getWeeklyAllowedUsage().getTotalInBytes()) {
                return null;
            }

            LongRange volumeSliceRange = LongRange.closed(dataSliceConfiguration.getVolumeMinimumSlice(),
                    dataSliceConfiguration.getVolumeMaximumSlice());

            volumeSlice = volumeSlice(nonMonetoryBalance, monetaryBalance, dataSliceConfiguration);
            finalSlice = volumeSlice.isFinalSlice();
            isMonetaryFinalSlice = volumeSlice.isMonetaryFinalSlice();

            long totalPossibleSlice = volumeSliceRange
                    .restrict((nonMonetoryBalance.getBillingCycleTotalVolume() / 100) * dataSliceConfiguration.getVolumeSlicePercentage());
            long totalReservationWatermark = totalPossibleSlice * concurrencyConfiguration.getConcurrencyValue();
            isVolumeReservationWatermarkReached = nonMonetoryBalance.getBillingCycleAvailableVolume() <= totalReservationWatermark;
        }

        if (CommonConstants.QUOTA_UNDEFINED != nonMonetoryBalance.getBillingCycleTime()) {

            if (nonMonetoryBalance.getDailyTime() >= getDailyAllowedUsage().getTimeInSeconds()
                    || nonMonetoryBalance.getWeeklyTime() >= getWeeklyAllowedUsage().getTimeInSeconds()) {
                return null;
            }

            timeSlice = timeSlice(nonMonetoryBalance, monetaryBalance, dataSliceConfiguration);
            finalSlice = finalSlice || timeSlice.isFinalSlice();
            isMonetaryFinalSlice = isMonetaryFinalSlice || timeSlice.isMonetaryFinalSlice();

            LongRange timeSliceRange = LongRange.closed(dataSliceConfiguration.getTimeMinimumSlice(),
                    dataSliceConfiguration.getTimeMaximumSlice());
            long possibleTimeSlice = timeSliceRange
                    .restrict((nonMonetoryBalance.getBillingCycleTime() / 100) * dataSliceConfiguration.getTimeSlicePercentage());
            long timeReservationWatermark = possibleTimeSlice * concurrencyConfiguration.getConcurrencyValue();
            isTimeReservationWatermarkReached = nonMonetoryBalance.getBillingCycleAvailableTime() <= timeReservationWatermark;
        }

        GyServiceUnits allocatedServiceUnits = new GyServiceUnits();
        allocatedServiceUnits.setFupLevel(getFupLevel());
        MSCC mscc = new MSCC();
        if (isVolumeReservationWatermarkReached || isTimeReservationWatermarkReached) {
            allocatedServiceUnits.setReservationRequired(true);
            LogManager.getLogger().info(MODULE, "Billing cycle available balance below/equals total reservation watermark");
            LogManager.getLogger().info(MODULE, "Billing cycle available - " + nonMonetoryBalance.getBillingCycleAvailableVolume());
        }

        allocatedServiceUnits.setQuotaProfileIdOrRateCardId(getQuotaProfileId());
        allocatedServiceUnits.setPackageId(packageId);
        allocatedServiceUnits.setProductOfferId(nonMonetoryBalance.getProductOfferId());
        allocatedServiceUnits.setSubscriptionId(subscriptionId);
        allocatedServiceUnits.setBalanceId(nonMonetoryBalance.getId());
        allocatedServiceUnits.setRate(getRate());

        if (Objects.nonNull(volumeSlice)) {
            allocatedServiceUnits.setVolume(volumeSlice.getValue());
            allocatedServiceUnits.setReservedMonetaryBalance(volumeSlice.getReservedMonetaryBalance());
            if(volumeSlice.getReservedMonetaryBalance() > 0.0) {
                allocatedServiceUnits.setMonetaryBalanceId(monetaryBalance.getId());
            }
            if(isMonetaryFinalSlice || finalSlice) {
                mscc.setVolumeQuotaThreshold(volumeSlice.getValue());
            } else{
                mscc.setVolumeQuotaThreshold(volumeSlice.getValue() * dataSliceConfiguration.getVolumeSliceThreshold() / 100);
            }
        }

        if (Objects.nonNull(timeSlice)) {
            allocatedServiceUnits.setTime(timeSlice.getValue());
            allocatedServiceUnits.setReservedMonetaryBalance(timeSlice.getReservedMonetaryBalance());
            if(timeSlice.getReservedMonetaryBalance() > 0.0) {
                allocatedServiceUnits.setMonetaryBalanceId(monetaryBalance.getId());
            }
            if(isMonetaryFinalSlice || finalSlice) {
                mscc.setTimeQuotaThreshold(timeSlice.getValue());
            } else{
                mscc.setTimeQuotaThreshold(timeSlice.getValue() * dataSliceConfiguration.getTimeSliceThreshold() / 100);
            }
        }

        if (isMonetaryFinalSlice || (finalSlice && isOtherQuotaAvailable(quotaProfileBalance) == false)) {
            FinalUnitIndication finalUnitIndication = new FinalUnitIndication();
            finalUnitIndication.setAction(FinalUnitAction.TERMINATE);
            mscc.setFinalUnitIndiacation(finalUnitIndication);
        }
        mscc.setGrantedServiceUnits(allocatedServiceUnits);
        mscc.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        setValidityTime(mscc, nonMonetoryBalance);
        return mscc;
    }

    private boolean isOtherQuotaAvailable(QuotaProfileBalance quotaProfileBalance) {

        int startLevel = getFupLevel() + 1;

        while (startLevel <= 2) {
            NonMonetoryBalance nonMonetoryBalance = getBalanceForLevel(quotaProfileBalance, startLevel);

            if (nonMonetoryBalance == null) {
                return false;
            }

            if (CommonConstants.QUOTA_UNDEFINED != nonMonetoryBalance.getBillingCycleTotalVolume()) {
                if (nonMonetoryBalance.getBillingCycleAvailableVolume() > 0) {
                    return true;
                }
            }

            if((CommonConstants.QUOTA_UNDEFINED != nonMonetoryBalance.getBillingCycleTime())){
                if (nonMonetoryBalance.getBillingCycleAvailableTime() > 0) {
                    return true;
                }
            }

            startLevel++;
        }

        return false;
    }

    private void setValidityTime(MSCC mscc, NonMonetoryBalance nonMonetoryBalance) {

        long currentTime = System.currentTimeMillis();
        long remainingDailyTime = nonMonetoryBalance.getDailyResetTime() - currentTime;
        long remainingWeeklyTime = nonMonetoryBalance.getWeeklyResetTime() - currentTime;
        long remainingBillingCycleTime = nonMonetoryBalance.getBillingCycleResetTime() - currentTime;

        if (remainingDailyTime > remainingWeeklyTime) {
            remainingDailyTime = remainingWeeklyTime;
        }

        if (remainingDailyTime > remainingBillingCycleTime) {
            remainingDailyTime = remainingBillingCycleTime;
        }

        mscc.setValidityTime(TimeUnit.MILLISECONDS.toSeconds(remainingDailyTime));
    }

    //FIXME move this code in reservation handler or Data RnC Handler -- Chetan
    private void setQuotaReservationFlag(MSCC currentMSCC, PolicyContext policyContext) {
        QuotaReservation quotaReservation = policyContext.getPCRFRequest().getQuotaReservation();
        if (Objects.nonNull(quotaReservation)) {
            MSCC oldMSCC = quotaReservation.get(currentMSCC.getRatingGroup());

            if (Objects.nonNull(oldMSCC)) {
                if (currentMSCC.getGrantedServiceUnits().getBalanceId().equals(oldMSCC.getGrantedServiceUnits().getBalanceId()) == false) {
                    policyContext.getPCRFResponse().setQuotaReservationChanged(true);
                }
            }
        } else {
			policyContext.getPCRFResponse().setQuotaReservationChanged(true);
		}
    }

    private Slice volumeSlice(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance, DataSliceConfiguration dataSliceConfiguration) {

        long dailyAvailableVolume = getDailyAllowedUsage().getTotalInBytes() - nonMonetoryBalance.getDailyVolume();
        long weeklyAvailableVolume = getWeeklyAllowedUsage().getTotalInBytes() - nonMonetoryBalance.getWeeklyVolume();
        long billingCycleTotalVolume = nonMonetoryBalance.getBillingCycleTotalVolume();
        long availableBalance = nonMonetoryBalance.getActualVolumeBalance();
        long sliceValue;
        double deductableBalance = 0.0;
        double monetaryAvailableBalance;

        sliceValue = (billingCycleTotalVolume / 100) * dataSliceConfiguration.getVolumeSlicePercentage();
        if (sliceValue < dataSliceConfiguration.getVolumeMinimumSlice()) {
            sliceValue = dataSliceConfiguration.getVolumeMinimumSlice();
        }
        if (sliceValue > dataSliceConfiguration.getVolumeMaximumSlice()) {
            sliceValue = dataSliceConfiguration.getVolumeMaximumSlice();
        }

        boolean isMonetaryFinalSlice = false;

        if (Objects.nonNull(monetaryBalance) && hasRatingDetail() && getRateUnit() == UsageType.VOLUME) {
            monetaryAvailableBalance = monetaryBalance.getUsableBalance();
            double rate = getRate();
            deductableBalance = (double) sliceValue / getVolumePulseInBytes() * rate;

            if (monetaryAvailableBalance <= deductableBalance) {
                sliceValue = (long) ((monetaryAvailableBalance * getVolumePulseInBytes()) / rate);
                deductableBalance = monetaryAvailableBalance;
                isMonetaryFinalSlice = true;
            }
        }

        boolean isFinalSlice = false;
        if (availableBalance <= sliceValue) {
            sliceValue = availableBalance;
            isFinalSlice = true;
        }

        if (dailyAvailableVolume <= sliceValue) {
            sliceValue = dailyAvailableVolume;
            isFinalSlice = true;
        }

        if (weeklyAvailableVolume <= sliceValue) {
            sliceValue = weeklyAvailableVolume;
            isFinalSlice = true;
        }

        return new Slice(sliceValue, deductableBalance, isFinalSlice, isMonetaryFinalSlice);
    }

    private Slice timeSlice(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance, DataSliceConfiguration dataSliceConfiguration) {

        long dailyAvailableTime = getDailyAllowedUsage().getTimeInSeconds() - nonMonetoryBalance.getDailyTime();
        long weeklyAvailableTime = getWeeklyAllowedUsage().getTimeInSeconds() - nonMonetoryBalance.getWeeklyTime();
        long value = nonMonetoryBalance.getBillingCycleTime();
        long availableBalance = nonMonetoryBalance.getActualTimeBalance();
        long sliceValue;
        double deductableBalance = 0.0;
        double monetaryAvailableBalance;

        sliceValue = (value / 100) * dataSliceConfiguration.getTimeSlicePercentage();
        if (sliceValue < dataSliceConfiguration.getTimeMinimumSlice()) {
            sliceValue = dataSliceConfiguration.getTimeMinimumSlice();
        }
        if (sliceValue > dataSliceConfiguration.getTimeMaximumSlice()) {
            sliceValue = dataSliceConfiguration.getTimeMaximumSlice();
        }

        boolean isMonetaryFinalSlice = false;

        if (Objects.nonNull(monetaryBalance) && hasRatingDetail() && getRateUnit() == UsageType.TIME) {
            monetaryAvailableBalance = monetaryBalance.getUsableBalance();
            double rate = getRate();
            deductableBalance = (double) sliceValue / getTimePulseInSeconds() * rate;

            if (monetaryAvailableBalance <= deductableBalance) {
                sliceValue = (long) ((monetaryAvailableBalance * getTimePulseInSeconds()) / rate);
                deductableBalance = monetaryAvailableBalance;
                isMonetaryFinalSlice = true;
            }
        }

        boolean isFinalSlice = false;

        if (availableBalance <= sliceValue) {
            sliceValue = availableBalance;
            isFinalSlice = true;
        }

        if (dailyAvailableTime <= sliceValue) {
            sliceValue = dailyAvailableTime;
            isFinalSlice = true;
        }

        if (weeklyAvailableTime <= sliceValue) {
            sliceValue = weeklyAvailableTime;
            isFinalSlice = true;
        }

        return new Slice(sliceValue, deductableBalance, isFinalSlice, isMonetaryFinalSlice);
    }

    @Override
    public boolean applyRG(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation, long ratingGroup) {
        if (ratingGroup != getRatingGroup().getIdentifier()) {
            return false;
        }
        return apply(policyContext, packageId, subscription, quotaReservation);
    }
}
