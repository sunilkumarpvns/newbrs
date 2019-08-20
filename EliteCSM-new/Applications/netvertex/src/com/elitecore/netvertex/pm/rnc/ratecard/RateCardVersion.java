package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVeresionDetail;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.quota.Slice;
import com.elitecore.netvertex.pm.quota.VoiceSliceConfiguration;
import com.elitecore.netvertex.rnc.RnCPulseCalculator;
import com.elitecore.netvertex.rnc.RnCRateCalculator;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RateCardVersion extends MonetaryRateCardVersion {

	private static final String MODULE = "RC-VERSION";
	private static VoiceSliceConfiguration voiceSliceConfiguration = new VoiceSliceConfiguration();

	public RateCardVersion(String id, String name, List<MonetaryRateCardVeresionDetail> ratingBehaviors,
						   String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
						   String rateCardId,String rateCardName) {
		super(id, name, ratingBehaviors, rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName, rateCardId, rateCardName);
	}

	public boolean apply(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription, String keyOne, String keyTwo) {

		boolean isApplied = false;

		if (LogManager.getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().print("[ " + MODULE + " ] : " + getName());
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
		}

		try {

			if(Collectionz.isNullOrEmpty(getRatingBehaviors())) {
				return false;
			}

			for (MonetaryRateCardVeresionDetail ratingBehavior : getRatingBehaviors()) {

				if (Objects.isNull(ratingBehavior)) {
					continue;
				}

				if (isApplicable(ratingBehavior, policyContext, keyOne, keyTwo) == false) {
					if (getLogger().isInfoLogLevel()) {
						policyContext.getTraceWriter().println("Version detail not satisfied for configured keys: " + keyOne + " : " + ratingBehavior.getLabel1() + ", " + keyTwo
								+ " : " + ratingBehavior.getLabel2());
					}
					continue;
				} else {
					if (getLogger().isInfoLogLevel()) {
						policyContext.getTraceWriter().println("Version detail satisfied for configured keys: " + keyOne + " : " + ratingBehavior.getLabel1() + ", " + keyTwo
								+ " : " + ratingBehavior.getLabel2());
					}
				}

				isApplied = applyRatingBehavior(policyContext, quotaReservation, subscription, ratingBehavior);
				if(isApplied) {
					break;
				}

			}

			return isApplied;
		} finally {
			if (LogManager.getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().decrementIndentation();
			}
		}


	}

	private boolean isApplicable(MonetaryRateCardVeresionDetail ratingBehavior, PolicyContext policyContext, String keyOne, String keyTwo) {
		return isKeyApplied(policyContext, keyOne, ratingBehavior.getLabel1()) && isKeyApplied(policyContext, keyTwo, ratingBehavior.getLabel2());
	}

	private boolean isKeyApplied(PolicyContext policyContext, String key, String keyValue) {
		if (Strings.isNullOrBlank(key)) {
			if (getLogger().isDebugLogLevel()) {
				policyContext.getTraceWriter().println("Satisfied. Reason: Key is not configured in rate card");
			}
			return true;
		}

		if (Strings.isNullOrBlank(keyValue)) {
			if (getLogger().isDebugLogLevel()) {
				policyContext.getTraceWriter().println("Satisfied. Reason: value not configured");
			}
			return true;
		}

		String keyAttributeValue = policyContext.getPCRFResponse().getAttribute(key);
		if (Strings.isNullOrBlank(keyAttributeValue)) {
			if (getLogger().isDebugLogLevel()) {
				policyContext.getTraceWriter().println("Not Satisfied. Reason: Value not found for PCRF key: " + key + " from PCRF Response");
			}
			return false;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Value found from PCRF response: " + key + "=" + keyAttributeValue
					+ ", Value Configured: " + keyValue);
		}

		return keyValue.equals(keyAttributeValue);
	}

	private boolean applyRatingBehavior(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription, MonetaryRateCardVeresionDetail ratingBehavior) {

		boolean isApplied = false;

		if(Collectionz.isNullOrEmpty(ratingBehavior.getSlabs())) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Rate slabs not found");
			}
			return false;
		}

		for (RateSlab rateSlab : ratingBehavior.getSlabs()) {

			if (Objects.isNull(rateSlab))
				continue;

			MonetaryBalance monetaryBalance;

			try {
				monetaryBalance = policyContext.getCurrentMonetaryBalance().getServiceBalance(policyContext.getPCRFRequest().getAttribute(PCRFKeyConstants.CS_SERVICE.val));
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "RateCard version(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching monetary balance, Cause: "
						+ e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}

				return false;
			}

			if (Objects.isNull(monetaryBalance) || monetaryBalance.isExist() == false) {

				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "RateCard version(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ", Monetary balance not exist.");
				}
				return false;
			}

			ChargingType chargingType;
			if (Objects.nonNull(subscription)) {
				chargingType = policyContext.getPolicyRepository().getRnCPackage().byId(subscription.getPackageId()).getChargingType();
			} else {
				chargingType = policyContext.getRnCPackage().getChargingType();
			}

			MSCC newMSCC;
			if (ChargingType.EVENT == chargingType) {
				newMSCC = processEvent(policyContext, monetaryBalance, subscription, rateSlab);
			} else {
				newMSCC = createMSCC(monetaryBalance, subscription, rateSlab, policyContext.getProductOfferId());
			}

			if(newMSCC != null) {

				quotaReservation.put(newMSCC);

				if (LogManager.getLogger().isInfoLogLevel()) {

					policyContext.getTraceWriter().println();
					policyContext.getTraceWriter().println(" Reserved RnC Quota Details : ");
					policyContext.getTraceWriter().println(" RnC Package " + getRncPackageName());
					policyContext.getTraceWriter().println(" RateCard Group " + getRateCardGroupName());
					policyContext.getTraceWriter().println(" RateCard " + getRateCardName());
					policyContext.getTraceWriter().println(" Actual Rate " + newMSCC.getGrantedServiceUnits().getActualRate());
					policyContext.getTraceWriter().println(" Discount(%) " + newMSCC.getGrantedServiceUnits().getDiscount());
					policyContext.getTraceWriter().println(" Rate " + newMSCC.getGrantedServiceUnits().getRate());
					policyContext.getTraceWriter().println(" RateMinorUnit " + newMSCC.getGrantedServiceUnits().getRateMinorUnit());
					policyContext.getTraceWriter().println(" Pulse " + newMSCC.getGrantedServiceUnits().getTimePulse());
					policyContext.getTraceWriter().println();
					policyContext.getTraceWriter().println("Reserved Monetary " + newMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());
					policyContext.getTraceWriter().println("CC-Time " + newMSCC.getGrantedServiceUnits().getTime());
					policyContext.getTraceWriter().println("CC-Service-Specific-Units " + newMSCC.getGrantedServiceUnits().getServiceSpecificUnits());
					policyContext.getTraceWriter().println("Validity Time " + newMSCC.getValidityTime());

				}

				return true;
			}

		}

		return isApplied;
	}

	private MSCC processEvent(RoPolicyContextImpl policyContext, MonetaryBalance monetaryBalance, Subscription subscription, RateSlab flatSlab) {

		PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
		List<MSCC> reportedMSCCs = pcrfRequest.getReportedMSCCs();
		if (Collectionz.isNullOrEmpty(reportedMSCCs)) {
			return null;
		}

		MSCC requestedMSCC = reportedMSCCs.get(0);
		long serviceSpecificUnits = requestedMSCC.getRequestedServiceUnits().getServiceSpecificUnits();

		BigDecimal rate = flatSlab.getRate();
		if (PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val))) {

			if(flatSlab.getDiscount() != null && flatSlab.getDiscount() > 0){
				rate = rate.subtract(rate.multiply(BigDecimal.valueOf(flatSlab.getDiscount())).divide(BigDecimal.valueOf(100), CommonConstants.RATE_PRECESION, RoundingMode.HALF_UP));
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, "Discount applied to subscriber: " + flatSlab.getDiscount() +"%");
					LogManager.getLogger().debug(MODULE, "Deductable discounted rate for subscriber: " + rate);
				}
			}
		}

		double reservedMonetaryBalance = calculateReservedMonetaryBalance(serviceSpecificUnits, rate.doubleValue());
		double actualDeductableMoney = calculateReservedMonetaryBalance(serviceSpecificUnits, flatSlab.getRate().doubleValue());

		if (reservedMonetaryBalance > monetaryBalance.getUsableBalance()) {
			if(getLogger().isInfoLogLevel()){
				getLogger().info(MODULE, "Not Satisfied. Reason: Money required: " + reservedMonetaryBalance
						+ " is higher than available balance: " + monetaryBalance.getUsableBalance());
				policyContext.getTraceWriter().println("Not Satisfied. Reason: Money required: " + reservedMonetaryBalance
						+ " is higher than available balance: " + monetaryBalance.getUsableBalance());
			}
			return null;
		}

		GyServiceUnits satisfiedUnits = new GyServiceUnits();
		if(Objects.nonNull(subscription)){
			satisfiedUnits.setSubscriptionId(subscription.getId());
			satisfiedUnits.setProductOfferId(subscription.getProductOfferId());
		}else{
			satisfiedUnits.setProductOfferId(policyContext.getProductOfferId());
		}

		satisfiedUnits.setMonetaryBalanceId(monetaryBalance.getId());
		satisfiedUnits.setActualRate(flatSlab.getRate().doubleValue());
		satisfiedUnits.setDiscount((flatSlab.getDiscount() != null) ? flatSlab.getDiscount(): 0);
		satisfiedUnits.setRate(rate.doubleValue());
		satisfiedUnits.setRateMinorUnit(flatSlab.getRateMinorUnit());
		satisfiedUnits.setRateCardName(getRateCardName());
		satisfiedUnits.setRateCardId(getRateCardId());
		satisfiedUnits.setPackageId(getRncPackageId());
		satisfiedUnits.setQuotaProfileIdOrRateCardId(getRateCardId());
		satisfiedUnits.setRateCardGroupName(getRateCardGroupName());
		satisfiedUnits.setRateCardGroupId(getRateCardGroupId());
		satisfiedUnits.setServiceSpecificUnits(serviceSpecificUnits);
		satisfiedUnits.setReservedMonetaryBalance(reservedMonetaryBalance);
		satisfiedUnits.setDiscountAmount(actualDeductableMoney-reservedMonetaryBalance);
		satisfiedUnits.setRevenueCode(flatSlab.getRevenueDetail());

		MSCC mscc = new MSCC();
		mscc.setGrantedServiceUnits(satisfiedUnits);

		return mscc;
	}


	private MSCC createMSCC(MonetaryBalance monetaryBalance, Subscription subscription, RateSlab flatSlab, String productOfferId) {

		Slice timeSlice = null;

		GyServiceUnits allocatedServiceUnits = new GyServiceUnits();
		BigDecimal rate = flatSlab.getRate();
		if(flatSlab.getDiscount() != null && flatSlab.getDiscount() > 0){
			rate = rate.subtract(rate.multiply(BigDecimal.valueOf(flatSlab.getDiscount())).divide(BigDecimal.valueOf(100), CommonConstants.RATE_PRECESION, RoundingMode.HALF_UP));
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Discount applied to subscriber: " + flatSlab.getDiscount() +"%");
				LogManager.getLogger().debug(MODULE, "Deductable discounted rate for subscriber: " + rate);
			}
		}
		timeSlice = timeSlice(monetaryBalance, flatSlab, rate.doubleValue());

		if(timeSlice == null) {
			return null;
		}

		MSCC mscc = new MSCC();

		if(Objects.nonNull(subscription)){
			allocatedServiceUnits.setSubscriptionId(subscription.getId());
			allocatedServiceUnits.setProductOfferId(subscription.getProductOfferId());
		}else{
			allocatedServiceUnits.setProductOfferId(productOfferId);
		}

		allocatedServiceUnits.setMonetaryBalanceId(monetaryBalance.getId());
		allocatedServiceUnits.setActualRate(flatSlab.getRate().doubleValue());
		allocatedServiceUnits.setDiscount((flatSlab.getDiscount() != null) ? flatSlab.getDiscount(): 0);
		allocatedServiceUnits.setRate(rate.doubleValue());
		allocatedServiceUnits.setRateMinorUnit(flatSlab.getRateMinorUnit());
		allocatedServiceUnits.setTimePulse(flatSlab.getPulseMinorUnit());
		allocatedServiceUnits.setTime(timeSlice.getValue());
		allocatedServiceUnits.setReservedMonetaryBalance(timeSlice.getReservedMonetaryBalance());
		allocatedServiceUnits.setRateCardName(getRateCardName());
		allocatedServiceUnits.setRateCardId(getRateCardId());
		allocatedServiceUnits.setPackageId(getRncPackageId());
		allocatedServiceUnits.setQuotaProfileIdOrRateCardId(getRateCardId());
		allocatedServiceUnits.setRateCardGroupName(getRateCardGroupName());
		allocatedServiceUnits.setRateCardGroupId(getRateCardGroupId());
		allocatedServiceUnits.setRevenueCode(flatSlab.getRevenueDetail());

		if (timeSlice.isFinalSlice()) {
			FinalUnitIndication finalUnitIndication = new FinalUnitIndication();
			finalUnitIndication.setAction(FinalUnitAction.TERMINATE);
			mscc.setFinalUnitIndiacation(finalUnitIndication);
		}

		mscc.setGrantedServiceUnits(allocatedServiceUnits);

		return mscc;

	}

	private Slice timeSlice(MonetaryBalance monetaryBalance, RateSlab flatSlab, double rate) {

		long sliceValue;
		double deductableBalance = 0.0;
		boolean isFinalSlice = false;
		double singlePulseAmt;

		Slice slice;

		sliceValue = flatSlab.getPulseMinorUnit() * voiceSliceConfiguration.getSlicePulse();

		if(rate > 0.0) {

			deductableBalance = RnCRateCalculator.calculateMoney(sliceValue, rate, flatSlab.getRateMinorUnit());

			if(deductableBalance > monetaryBalance.getUsableBalance()) {

				deductableBalance = monetaryBalance.getUsableBalance();

				singlePulseAmt = RnCRateCalculator.calculateMoney(flatSlab.getPulseMinorUnit(), rate, flatSlab.getRateMinorUnit());

				if(deductableBalance < singlePulseAmt)
					return null;

				sliceValue = RnCRateCalculator.calculateUnit(deductableBalance, rate, flatSlab.getRateMinorUnit());

				sliceValue = RnCPulseCalculator.floor(sliceValue, flatSlab.getPulseMinorUnit());

				sliceValue = RnCPulseCalculator.multiply(sliceValue, flatSlab.getPulseMinorUnit());

				deductableBalance = RnCRateCalculator.calculateMoney(sliceValue, rate, flatSlab.getRateMinorUnit());

				isFinalSlice = true;

			}else if(deductableBalance == monetaryBalance.getUsableBalance()) {

				isFinalSlice = true;
			}

		}

		slice = new Slice(sliceValue, deductableBalance, isFinalSlice);

		return slice;
	}

	private double calculateReservedMonetaryBalance(long serviceSpecificUnits, double rate) {

		double deductableBalance = 0.0;
		if(rate > 0.0) {
			deductableBalance = RnCRateCalculator.calculateMoney(serviceSpecificUnits, rate);
		}
		return deductableBalance;
	}

}
