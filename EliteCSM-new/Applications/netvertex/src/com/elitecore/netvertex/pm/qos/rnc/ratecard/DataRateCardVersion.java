package com.elitecore.netvertex.pm.qos.rnc.ratecard;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.quota.QuotaReservation;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class DataRateCardVersion extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion
        implements RateCardVersion {
    private static final String MODULE = "RC-VERSION";

    public DataRateCardVersion(String rateCardId, String rateCardName, String name, List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail> versionDetails) {
        super(rateCardId, rateCardName, name, versionDetails);
    }

    @Override
    public boolean isApplicable(PolicyContext policyContext, String keyOne, String keyTwo) {

        com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail versionDetail = selectRatingBehavior(policyContext, keyOne, keyTwo);
        if (versionDetail == null) {
            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().println("Version detail not satisfied for configured keys: " + keyOne + ", " + keyTwo);
            }
            return false;
        }

        List<RateSlab> slabs = versionDetail.getSlabs();

        /* Intentionally fetched first slab, as phase 1 delivery has limited to one slab*/
        RateSlab rateSlab = slabs.get(0);

        if (rateSlab.isFree()) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().debug(MODULE, "Rate Slab is free so skipping monetary balance check");
            }
            return true;
        }

        MonetaryBalance monetaryBalance = getMonetaryBalanceIfExist(policyContext);
        return monetaryBalance != null;
    }

    @Override
    public boolean applyReservation(PolicyContext policyContext,
                                    String keyOne,
                                    String keyTwo,
                                    String packageId,
                                    Subscription subscription,
                                    QuotaReservation quotaReservation) {

        com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail versionDetail = selectRatingBehavior(policyContext, keyOne, keyTwo);

        if (versionDetail == null) {
            return false;
        }


        List<RateSlab> slabs = versionDetail.getSlabs();

        /* Intentionally fetched first slab, as phase 1 delivery is limited to one slab*/
        RateSlab rateSlab = slabs.get(0);

        String subscriptionId = Objects.isNull(subscription) ? null : subscription.getId();


        MSCC newMSCC = createMSCC(policyContext, packageId, subscriptionId, Objects.isNull(subscription) ? null : subscription.getProductOfferId(), rateSlab, versionDetail.revenueDetail());

        if (newMSCC == null) {
            return false;
        }

        setQuotaReservationFlag(newMSCC, policyContext);
        quotaReservation.put(newMSCC);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Selected Service: " + newMSCC.getServiceIdentifiers()
                    + ", RG: " + newMSCC.getRatingGroup()
                    + ", Rate Card: " + getRateCardId()
                    + ", Package: " + packageId
                    + ", Subscription: " + subscriptionId);
        }

        return true;
    }

    private MSCC createMSCC(PolicyContext policyContext, String packageId, String subscriptionId, String subscriptionProductOfferId, RateSlab rateSlab, String revenueCode) {
        return applyProcessing(policyContext, packageId, subscriptionId, subscriptionProductOfferId, rateSlab, revenueCode);
    }

    private MSCC applyProcessing(PolicyContext policyContext, String packageId, String subscriptionId, String subscriptionProductOfferId, RateSlab rateSlab, String revenueCode) {
        MonetaryBalance monetaryBalance = getMonetaryBalanceIfExist(policyContext);

        if (monetaryBalance == null) {
            return null;
        }

        DataSliceConfiguration dataSliceConfiguration = policyContext.getPolicyRepository().getSliceConfiguration();
        double actualBalance = monetaryBalance.getUsableBalance();

        /*
            Calculated Volume from moentary reservation
         */
        long volumeToBeGrantedBytesOrSeconds = 0;
        if(rateSlab.isFree() == false) {
            volumeToBeGrantedBytesOrSeconds  = calculateSlicePulse(rateSlab.getRateUomInBytesOrSeconds(), rateSlab.getRate(), dataSliceConfiguration.getMonetaryReservation());
        }

        boolean isFinalSlice = false;

        GyServiceUnits allocatedServiceUnits = new GyServiceUnits();

        if(Objects.nonNull(subscriptionId)){
            allocatedServiceUnits.setSubscriptionId(subscriptionId);
            allocatedServiceUnits.setProductOfferId(subscriptionProductOfferId);
        }else{
            allocatedServiceUnits.setProductOfferId(policyContext.getProductOfferId());
        }

        allocatedServiceUnits.setQuotaProfileIdOrRateCardId(getRateCardId());
        allocatedServiceUnits.setRate(rateSlab.getRate().doubleValue());
        allocatedServiceUnits.setPackageId(packageId);
        allocatedServiceUnits.setMonetaryBalanceId(monetaryBalance.getId());
        allocatedServiceUnits.setRateMinorUnit(rateSlab.getRateUomInBytesOrSeconds());
        allocatedServiceUnits.setPulseMinorUnit(rateSlab.getPulseInBytesOrSeconds());
        allocatedServiceUnits.setRateCardName(getRateCardName());
        allocatedServiceUnits.setRateCardId(getRateCardId());
        allocatedServiceUnits.setRevenueCode(revenueCode);

        MSCC mscc = new MSCC();
        if (rateSlab.isVolumeBasedRateDefined()) {

            if (volumeToBeGrantedBytesOrSeconds > dataSliceConfiguration.getVolumeMaximumSlice() || rateSlab.isFree()) {
                volumeToBeGrantedBytesOrSeconds = dataSliceConfiguration.getVolumeMaximumSlice();
            }

            if (volumeToBeGrantedBytesOrSeconds < dataSliceConfiguration.getVolumeMinimumSlice()) {
                volumeToBeGrantedBytesOrSeconds = dataSliceConfiguration.getVolumeMinimumSlice();
            }

            if(rateSlab.isFree() == false) {
                double tobeReservedMonetaryBalance = calculateTobeReservedMonetaryBalance(rateSlab, volumeToBeGrantedBytesOrSeconds);

                if (actualBalance < tobeReservedMonetaryBalance) {
                    volumeToBeGrantedBytesOrSeconds = calculateToBeGrantedSlice((long) actualBalance, volumeToBeGrantedBytesOrSeconds, (long) tobeReservedMonetaryBalance);

                    if (isCalculatedSliceIsLessThanPulse(rateSlab, volumeToBeGrantedBytesOrSeconds)) {

                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Skipping processing. Reason: To be granted slice : " + volumeToBeGrantedBytesOrSeconds +
                                    " is less than pulse : " + rateSlab.getPulseInBytesOrSeconds());
                        }

                        return null;
                    }
                    tobeReservedMonetaryBalance = actualBalance;
                    isFinalSlice = true;
                } else if (actualBalance == tobeReservedMonetaryBalance) {
                    isFinalSlice = true;
                }
                allocatedServiceUnits.setReservedMonetaryBalance(tobeReservedMonetaryBalance);
            }
            allocatedServiceUnits.setVolume(volumeToBeGrantedBytesOrSeconds);
            allocatedServiceUnits.setVolumePulse(rateSlab.getPulse());
            if(isFinalSlice) {
                mscc.setVolumeQuotaThreshold(volumeToBeGrantedBytesOrSeconds);
            } else{
                mscc.setVolumeQuotaThreshold(volumeToBeGrantedBytesOrSeconds * dataSliceConfiguration.getVolumeSliceThreshold() / 100);
            }
        } else {

            if (volumeToBeGrantedBytesOrSeconds > dataSliceConfiguration.getTimeMaximumSlice() || rateSlab.isFree()) {
                volumeToBeGrantedBytesOrSeconds = dataSliceConfiguration.getTimeMaximumSlice();
            }

            if (volumeToBeGrantedBytesOrSeconds < dataSliceConfiguration.getTimeMinimumSlice()) {
                volumeToBeGrantedBytesOrSeconds = dataSliceConfiguration.getTimeMinimumSlice();
            }

            if(rateSlab.isFree() == false) {
                double tobeReservedMonetaryBalance = calculateTobeReservedMonetaryBalance(rateSlab, volumeToBeGrantedBytesOrSeconds);

                if (actualBalance < tobeReservedMonetaryBalance) {
                    volumeToBeGrantedBytesOrSeconds = calculateToBeGrantedSlice((long) actualBalance, volumeToBeGrantedBytesOrSeconds, (long) tobeReservedMonetaryBalance);
                    if (isCalculatedSliceIsLessThanPulse(rateSlab, volumeToBeGrantedBytesOrSeconds)) {

                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Skipping processing. Reason: To be granted slice : " + volumeToBeGrantedBytesOrSeconds +
                                    " is less than pulse : " + rateSlab.getPulseInBytesOrSeconds());
                        }

                        return null;
                    }
                    tobeReservedMonetaryBalance = actualBalance;
                    isFinalSlice = true;
                }
                allocatedServiceUnits.setReservedMonetaryBalance(tobeReservedMonetaryBalance);
            }
            allocatedServiceUnits.setTime(volumeToBeGrantedBytesOrSeconds);
            allocatedServiceUnits.setTimePulse(rateSlab.getPulse());
            if(isFinalSlice) {
                mscc.setVolumeQuotaThreshold(volumeToBeGrantedBytesOrSeconds);
            } else{
                mscc.setVolumeQuotaThreshold(volumeToBeGrantedBytesOrSeconds * dataSliceConfiguration.getTimeSliceThreshold() / 100);
            }
        }

        mscc.setValidityTime(dataSliceConfiguration.getMinValidityTime());
        mscc.setGrantedServiceUnits(allocatedServiceUnits);
        mscc.setRatingGroup(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER);

        if (isFinalSlice) {
            FinalUnitIndication finalUnitIndication = new FinalUnitIndication();
            finalUnitIndication.setAction(FinalUnitAction.TERMINATE);
            mscc.setFinalUnitIndiacation(finalUnitIndication);
        }

        return mscc;
    }

    private long calculateSlicePulse(long rateInBytesOrSeconds, BigDecimal rate, long moentaryReservation) {
        return BigDecimal.valueOf(moentaryReservation).multiply(BigDecimal.valueOf(rateInBytesOrSeconds)).divide(rate).longValue();
    }

    private boolean isCalculatedSliceIsLessThanPulse(RateSlab rateSlab, long volumeToBeGrantedBytesOrSeconds) {
        return volumeToBeGrantedBytesOrSeconds < rateSlab.getPulseInBytesOrSeconds();
    }

    private long calculateToBeGrantedSlice(long actualBalance, long volumeToBeGrantedBytesOrSeconds, long tobeReservedMonetaryBalance) {
        return actualBalance * volumeToBeGrantedBytesOrSeconds / tobeReservedMonetaryBalance;
    }

    private double calculateTobeReservedMonetaryBalance(RateSlab rateSlab, long volumeToBeGrantedBytesOrSeconds) {
        return (rateSlab.getRate().multiply(BigDecimal.valueOf(volumeToBeGrantedBytesOrSeconds)))
                .divide(BigDecimal.valueOf(rateSlab.getRateUomInBytesOrSeconds()), CommonConstants.RATE_PRECESION, RoundingMode.HALF_UP).doubleValue();
    }

    private com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail selectRatingBehavior(PolicyContext policyContext, String keyOne,
                                                                                                                      String keyTwo) {
        for (com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail versionDetail : getVersionDetails()) {
            if (((VersionDetail)versionDetail).isApplicable(policyContext, keyOne, keyTwo)) {
                return versionDetail;
            }
        }
        return null;
    }

    private MonetaryBalance getMonetaryBalanceIfExist(PolicyContext policyContext) {
        SubscriberMonetaryBalance subscriberMonetaryBalance = null;
        try {
            subscriberMonetaryBalance = policyContext.getCurrentMonetaryBalance();
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Rate Card Version(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity()
                    + " on monetary balance unavailability. Reason: Error while fetching monetary balance, Cause: "
                    + e.getMessage());
            LogManager.ignoreTrace(e);
            if (getLogger().isDebugLogLevel()) {
                policyContext.getTraceWriter().println("Error While fetching monetary balance. Reason: " + e.getMessage());
            }
            return null;
        }

        MonetaryBalance monetaryBalance;

        if (subscriberMonetaryBalance != null) {

            monetaryBalance = subscriberMonetaryBalance.getServiceBalance(CommonConstants.MONEY_DATA_SERVICE);

            if (monetaryBalance != null && monetaryBalance.isExist()) {
                return monetaryBalance;
            }
        }

        if (getLogger().isDebugLogLevel()) {
            policyContext.getTraceWriter().println("Monetary balance is not found or exhausted");
        }

        return null;
    }

    private void setQuotaReservationFlag(MSCC currentMSCC, PolicyContext policyContext) {
        QuotaReservation quotaReservation = policyContext.getPCRFRequest().getQuotaReservation();
        if (Objects.nonNull(quotaReservation)) {
            MSCC oldMSCC = quotaReservation.get(currentMSCC.getRatingGroup());

            if (Objects.nonNull(oldMSCC) && currentMSCC.getGrantedServiceUnits().getMonetaryBalanceId().equals(oldMSCC.getGrantedServiceUnits().getMonetaryBalanceId()) == false) {
                policyContext.getPCRFResponse().setQuotaReservationChanged(true);
            }
        } else {
            policyContext.getPCRFResponse().setQuotaReservationChanged(true);
        }
    }
}