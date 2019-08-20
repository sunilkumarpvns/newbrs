package com.elitecore.netvertex.pm.rnc.ratecard;

import java.util.List;
import java.util.Objects;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.RequestedAction;
import com.elitecore.netvertex.pm.quota.ConcurrencyConfiguration;
import com.elitecore.netvertex.pm.quota.LongRange;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.quota.Slice;
import com.elitecore.netvertex.pm.quota.VoiceSliceConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class NonMonetaryRateCard extends com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "NON-MONETARY-RATE-CARD";
	private static VoiceSliceConfiguration voiceSliceConfiguration = new VoiceSliceConfiguration();
	private static ConcurrencyConfiguration concurrencyConfiguration = new ConcurrencyConfiguration();

	public NonMonetaryRateCard(String id, String name, String description, Uom timeUom, long time, long timeMinorUnit,
							   long event, Uom pulseUom, long pulse, long pulseMinorUnit, String rncPackageId, String rncPackageName,
							   String rateCardGroupId, String rateCardGroupName, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit,boolean proration){
		super(id, name, description, timeUom, time, timeMinorUnit, event, pulseUom, pulse, pulseMinorUnit, rncPackageId,
				rncPackageName, rateCardGroupId, rateCardGroupName,renewalInterval,renewalIntervalUnit,proration);
	}

	public boolean apply(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription) {

		RnCNonMonetaryBalance nonMonetaryBalance = null;
		ChargingType chargingType;

		try {
			if (Objects.nonNull(subscription)) {
				nonMonetaryBalance = policyContext.getCurrentRnCBalance().getPackageBalance(subscription.getId()).getBalance(getId());
				chargingType = policyContext.getPolicyRepository().getRnCPackage().byId(subscription.getPackageId()).getChargingType();
			}else{
				chargingType = policyContext.getRnCPackage().getChargingType();
				nonMonetaryBalance = policyContext.getCurrentRnCBalance().getPackageBalance(getRncPackageId()).getBalance(getId());
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "RateCard (" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching non monetary balance, Cause: "
					+ e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}

			return false;
		}


		if (Objects.isNull(nonMonetaryBalance) || nonMonetaryBalance.isExist() == false) {
			if (getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().println("RateCard (" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ", Non monetary balance not exist.");
			}
			return false;
		}

		MSCC newMSCC;
		if (ChargingType.EVENT == chargingType) {
			newMSCC = processEvent(policyContext, nonMonetaryBalance, Objects.nonNull(subscription) ? subscription.getId() : null);
		} else {
			newMSCC = createMSCC(nonMonetaryBalance, Objects.nonNull(subscription) ? subscription.getId() : null);
		}

		if(newMSCC != null) {
			quotaReservation.put(newMSCC);

			if (LogManager.getLogger().isInfoLogLevel()) {

				policyContext.getTraceWriter().println();
				policyContext.getTraceWriter().println(" Reserved RnC Quota Details : ");
				policyContext.getTraceWriter().print(" RnC Package: " + getRncPackageName());
				policyContext.getTraceWriter().print(" RateCard Group: " + getRateCardGroupName());
				policyContext.getTraceWriter().print(" RateCard " + getName());
				policyContext.getTraceWriter().println();
				policyContext.getTraceWriter().print(" Pulse " + newMSCC.getGrantedServiceUnits().getTimePulse());
				policyContext.getTraceWriter().println();
				policyContext.getTraceWriter().print("CC-Time " + newMSCC.getGrantedServiceUnits().getTime());
				policyContext.getTraceWriter().print("Validity Time " + newMSCC.getValidityTime());
				policyContext.getTraceWriter().println();
				policyContext.getTraceWriter().print("CC-Service-Specific-Units " + newMSCC.getGrantedServiceUnits().getServiceSpecificUnits());

			}
			return true;
		}
		return false;
	}

	private MSCC processEvent(RoPolicyContextImpl policyContext, RnCNonMonetaryBalance nonMonetaryBalance, String subscriptionId) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Event processing started");
		}

		PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
		List<MSCC> reportedMSCCs = pcrfRequest.getReportedMSCCs();

		MSCC requestedMSCC = reportedMSCCs.get(0);
		long serviceSpecificUnits = requestedMSCC.getRequestedServiceUnits().getServiceSpecificUnits();
		GyServiceUnits satisfiedUnits = new GyServiceUnits();

		if (PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val))) {
			if (serviceSpecificUnits > nonMonetaryBalance.getActualBalance()) {
				if (getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println("Not Satisfied. Reason: Requested service specific units: " + serviceSpecificUnits
							+ " is higher than available balance: " + nonMonetaryBalance.getActualBalance());
				}
				return null;
			}
			satisfiedUnits.setRequestedAction(RequestedAction.DIRECT_DEBITING.getVal());
		} else if (PCRFKeyValueConstants.REQUESTED_ACTION_REFUND_ACCOUNT.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val))) {
			satisfiedUnits.setRequestedAction(RequestedAction.REFUND_ACCOUNT.getVal());
		}

		MSCC mscc = new MSCC();
		mscc.setRatingGroup(requestedMSCC.getRatingGroup());
		satisfiedUnits.setSubscriptionId(subscriptionId);
		satisfiedUnits.setPackageId(getRncPackageId());
		satisfiedUnits.setProductOfferId(nonMonetaryBalance.getProductOfferId());
		satisfiedUnits.setBalanceId(nonMonetaryBalance.getId());
		satisfiedUnits.setRateCardName(getName());
		satisfiedUnits.setRateCardId(getId());
		satisfiedUnits.setQuotaProfileIdOrRateCardId(getId());
		satisfiedUnits.setRateCardGroupName(getRateCardGroupName());
		satisfiedUnits.setRateCardGroupId(getRateCardGroupId());
		satisfiedUnits.setReservationRequired(true);
		/// setting units in SSU
		satisfiedUnits.setServiceSpecificUnits(serviceSpecificUnits);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Eligible Event Units: " + serviceSpecificUnits);
		}

		mscc.setGrantedServiceUnits(satisfiedUnits);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Event processing completed");
		}
		return mscc;
	}

	private MSCC createMSCC(RnCNonMonetaryBalance rnCNonMonetaryBalance, String subscriptionId) {

		Slice timeSlice = null;
		boolean isTimeReservationWatermarkReached = false;

		GyServiceUnits allocatedServiceUnits = new GyServiceUnits();
		timeSlice = timeSlice(rnCNonMonetaryBalance);

		if(timeSlice == null) {
			return null;
		}

		LongRange timeSliceRange = LongRange.closed(voiceSliceConfiguration.getMinimumTimeSlice(),
				voiceSliceConfiguration.getMaximumTimeSlice());
		long possibleTimeSlice = timeSliceRange
				.restrict((rnCNonMonetaryBalance.getBillingCycleTotal() / 100) * voiceSliceConfiguration.getTimeReservationSlicePercentage());
		long timeReservationWatermark = possibleTimeSlice * concurrencyConfiguration.getConcurrencyValue();
		isTimeReservationWatermarkReached = rnCNonMonetaryBalance.getBillingCycleAvailable() <= timeReservationWatermark;
		if (isTimeReservationWatermarkReached) {
			allocatedServiceUnits.setReservationRequired(true);
			LogManager.getLogger().info(MODULE, "Billing cycle available time below/equals total reservation watermark");
			LogManager.getLogger().info(MODULE, "Billing cycle available - " + rnCNonMonetaryBalance.getBillingCycleAvailable());
		}

		MSCC mscc = new MSCC();

		allocatedServiceUnits.setSubscriptionId(subscriptionId);
		allocatedServiceUnits.setPackageId(getRncPackageId());
		allocatedServiceUnits.setProductOfferId(rnCNonMonetaryBalance.getProductOfferId());
		allocatedServiceUnits.setBalanceId(rnCNonMonetaryBalance.getId());
		allocatedServiceUnits.setTimePulse(getPulseMinorUnit());
		allocatedServiceUnits.setTime(timeSlice.getValue());
		allocatedServiceUnits.setRateCardName(getName());
		allocatedServiceUnits.setRateCardId(getId());
		allocatedServiceUnits.setQuotaProfileIdOrRateCardId(getId());
		allocatedServiceUnits.setRateCardGroupName(getRateCardGroupName());
		allocatedServiceUnits.setRateCardGroupId(getRateCardGroupId());

		if (timeSlice.isFinalSlice()) {
			FinalUnitIndication finalUnitIndication = new FinalUnitIndication();
			finalUnitIndication.setAction(FinalUnitAction.TERMINATE);
			mscc.setFinalUnitIndiacation(finalUnitIndication);
		}

		mscc.setGrantedServiceUnits(allocatedServiceUnits);

		return mscc;
	}

	private Slice timeSlice(RnCNonMonetaryBalance rnCNonMonetaryBalance) {

		boolean isFinalSlice = false;
		long sliceValue = getPulseMinorUnit() * voiceSliceConfiguration.getSlicePulse();
		long availableBalance = rnCNonMonetaryBalance.getActualBalance();

		if(availableBalance < getPulseMinorUnit()){
			return null;
		}

		if (availableBalance <= sliceValue) {
			sliceValue = availableBalance;
			isFinalSlice = true;
		}

		return new Slice(sliceValue, 0, isFinalSlice);
	}
}