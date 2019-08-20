package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class RnCEventABMFHandler {
	private static final String MODULE = "RnC-EVENT-ABMF-HNDLR";

	public void handleDirectDebit(PCRFResponse response, ExecutionContext executionContext) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Event RnC ABMF processing started");
		}

		List<RnCNonMonetaryBalance> directDebitNonMonetaryBalances = null;
		List<MonetaryBalance> directDebitMonetaryBalances = null;

		if(Objects.isNull(response.getQuotaReservation())) {
			return;
		}
		for (Map.Entry<Long, MSCC> msccEntry : response.getQuotaReservation().get()) {

			MSCC mscc = msccEntry.getValue();

			if (mscc.getResultCode() != ResultCode.SUCCESS) {
				continue;
			}

			String nonMonetoryBalanceId = mscc.getGrantedServiceUnits().getBalanceId();

			RnCNonMonetaryBalance nonMonetoryBalance = processAndGetNonMonetaryBalance(mscc, response, nonMonetoryBalanceId, executionContext);
			if (Objects.nonNull(nonMonetoryBalance)) {
				if (Objects.isNull(directDebitNonMonetaryBalances)) {
					directDebitNonMonetaryBalances = new ArrayList<>();
				}
				directDebitNonMonetaryBalances.add(nonMonetoryBalance);
			}

			String monetoryBalanceId = mscc.getGrantedServiceUnits().getMonetaryBalanceId();
			MonetaryBalance monetoryBalance = processAndGetMonetaryBalance(mscc, response, monetoryBalanceId, executionContext);

			if (Objects.nonNull(monetoryBalance)) {
				if (Objects.isNull(directDebitMonetaryBalances)) {
					directDebitMonetaryBalances = new ArrayList<>();
				}
				directDebitMonetaryBalances.add(monetoryBalance);
			}
		}


		try {
			if (Objects.nonNull(directDebitNonMonetaryBalances)) {
				executionContext.getDDFTable().reportRnCBalance(directDebitNonMonetaryBalances.get(0).getSubscriberIdentity(), directDebitNonMonetaryBalances);
			}
			if (Objects.nonNull(directDebitMonetaryBalances)) {
				executionContext.getDDFTable().getMonetaryBalanceOp().directDebitBalance(directDebitMonetaryBalances);
			}
		} catch (OperationFailedException ex) {
			getLogger().error(MODULE, "Error while performing report RnC balance for Subscriber ID: "
					+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
					+ ". Reason:" + ex.getMessage());
			if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == ex.getErrorCode()) {
				getLogger().trace(MODULE, ex);
			}
		}



		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Event RnC ABMF processing completed");
		}
	}

	private RnCNonMonetaryBalance processAndGetNonMonetaryBalance(MSCC mscc, PCRFResponse response, String nonMonetoryBalanceId, ExecutionContext executionContext) {
		if (nonMonetoryBalanceId == null) {
			return null;
		}
		SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance = null;
		try {
			subscriberRnCNonMonetaryBalance = executionContext.getCurrentRnCNonMonetaryBalance();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing RnC balance update for Subscriber ID: "
					+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
					+ ". Reason:" + e.getMessage());
			if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
		if (subscriberRnCNonMonetaryBalance == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to update non-monetary balance for Subscriber ID:"
						+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val) + ". Reason: Non Monetory balance not found from request");
			}

			return null;
		}

		response.setPreviousRnCNonMonetaryBalance(subscriberRnCNonMonetaryBalance.copy());

		long serviceSpecificUnits = mscc.getGrantedServiceUnits().getServiceSpecificUnits();
		if(serviceSpecificUnits <= 0){
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping processing as granted service specific units is " + serviceSpecificUnits);
			}
		}

		RnCNonMonetaryBalance rnCNonMonetaryBalance = subscriberRnCNonMonetaryBalance.getBalanceById(nonMonetoryBalanceId);
		RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance = rnCNonMonetaryBalance.copy();
		updateABMFCopy(mscc, abmfCopyOfRnCNonMonetaryBalance);
		updateInMemoryCopy(mscc, rnCNonMonetaryBalance);
		setReportSummary(response, mscc);
		return abmfCopyOfRnCNonMonetaryBalance;
	}

	private void updateInMemoryCopy(MSCC mscc, RnCNonMonetaryBalance inMemoryCopyOfBalance) {
		// performing Direct Debit for InMemory Balance object. This will be used in Notification Processing
		inMemoryCopyOfBalance.setBillingCycleAvailable(inMemoryCopyOfBalance.getBillingCycleAvailable() - mscc.getGrantedServiceUnits().getServiceSpecificUnits());
	}

	private void updateABMFCopy(MSCC mscc, RnCNonMonetaryBalance abmfCopyOfRnCNonMonetaryBalance) {
		abmfCopyOfRnCNonMonetaryBalance.setBillingCycleAvailable(mscc.getGrantedServiceUnits().getServiceSpecificUnits());
	}

	private MonetaryBalance processAndGetMonetaryBalance(MSCC mscc, PCRFResponse response, String monetoryBalanceId, ExecutionContext executionContext) {
		if (monetoryBalanceId == null && mscc.getGrantedServiceUnits().getReservedMonetaryBalance() <= 0) {
			return null;
		}

		SubscriberMonetaryBalance subscriberMonetaryBalance = null;

		try {
			subscriberMonetaryBalance = executionContext.getCurrentMonetaryBalance();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing RnC balance update for Subscriber ID: "
					+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
					+ ". Reason:" + e.getMessage());
			if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}

		if (subscriberMonetaryBalance == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to update monetary balance for Subscriber ID:"
						+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val) + ". Reason: Monetary balance not found");
			}

			return null;
		}

		MonetaryBalance monetaryBalance = subscriberMonetaryBalance.getBalanceById(monetoryBalanceId);
		MonetaryBalance copyOfMonetoryBalance = monetaryBalance.copy();
		copyOfMonetoryBalance.setAvailBalance(mscc.getGrantedServiceUnits().getReservedMonetaryBalance());
		copyOfMonetoryBalance.setInitialBalance(0);
		setReportSummary(response, mscc);
		setReportedUsageSummaryForMonetaryBalance(response, mscc, null);
		return copyOfMonetoryBalance;
	}

	public void setReportSummary(PCRFResponse response, MSCC mscc) {
		ReportedUsageSummary reportedUsageSummary = new ReportedUsageSummary(mscc.getRatingGroup(), mscc.getServiceIdentifiers());
		reportedUsageSummary.setReportingReason(mscc.getReportingReason());
		reportedUsageSummary.setReportOperation(ReportOperation.fromReportingReason(mscc.getReportingReason()));

		GyServiceUnits grantedServiceUnits = mscc.getGrantedServiceUnits();
		reportedUsageSummary.setRateCardName(grantedServiceUnits.getRateCardName());
		reportedUsageSummary.setRateCardId(grantedServiceUnits.getRateCardId());
		reportedUsageSummary.setRateCardGroupName(grantedServiceUnits.getRateCardGroupName());
		reportedUsageSummary.setPackageId(grantedServiceUnits.getPackageId(), PolicyManager.getInstance().getPackageName(grantedServiceUnits.getPackageId()));
		reportedUsageSummary.setRevenueCode(grantedServiceUnits.getRevenueCode());

		ProductOffer productOffer = PolicyManager.getInstance().getProductOffer().byId(grantedServiceUnits.getProductOfferId());

		reportedUsageSummary.setProductOfferName(Objects.isNull(productOffer) ? null : productOffer.getName());
		reportedUsageSummary.setCurrency(Objects.isNull(productOffer) ? null : productOffer.getCurrency());

		if (nonNull(productOffer) && productOffer.isFnFOffer()) {
			reportedUsageSummary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_FNF.val);
		} else {
			reportedUsageSummary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_NORMAL.val);
		}
		reportedUsageSummary.setReportedEvent(grantedServiceUnits.getServiceSpecificUnits());
		reportedUsageSummary.setRequestedAction(response.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val));
		reportedUsageSummary.setCoreSessionId(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

		response.getReportSummary().add(reportedUsageSummary);
	}

	private void setReportedUsageSummaryForMonetaryBalance(PCRFResponse response, MSCC mscc, String currency) {
		ReportedUsageSummary reportedUsageSummary = response.getReportSummary().getReportedUsageSummaries().get(0);
		GyServiceUnits grantedServiceUnits = mscc.getGrantedServiceUnits();
		reportedUsageSummary.setDiscount(grantedServiceUnits.getDiscount());
		reportedUsageSummary.setRate(grantedServiceUnits.getActualRate());
		reportedUsageSummary.setRateMinorUnit(grantedServiceUnits.getRateMinorUnit());
		reportedUsageSummary.setDeductedMonetaryBalance(grantedServiceUnits.getReservedMonetaryBalance());
		reportedUsageSummary.setDiscountAmount(grantedServiceUnits.getDiscountAmount());
		ProductOffer productOffer = PolicyManager.getInstance().getProductOffer().byId(mscc.getGrantedServiceUnits().getProductOfferId());
		if(isNull(productOffer)==false){
			reportedUsageSummary.setCurrency(productOffer.getCurrency());

		}
	}

	public void handleRefund(PCRFResponse response, MSCC mscc, ExecutionContext executionContext) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Refund Event RnC ABMF processing started");
		}

		String nonMonetoryBalanceId = mscc.getGrantedServiceUnits().getBalanceId();
		RnCNonMonetaryBalance nonMonetoryBalance = processAndGetNonMonetaryBalance(mscc, response, nonMonetoryBalanceId, executionContext);
		if (Objects.nonNull(nonMonetoryBalance)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Refunding Non Monetary balance with ID: " + nonMonetoryBalanceId);
				getLogger().debug(MODULE, "Refundable Non Monetary balance: " + nonMonetoryBalance.getBillingCycleAvailable());
			}

			try {
				executionContext.getDDFTable().refundRnCBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance));
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while performing refund RnC balance for Subscriber ID: "
						+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
						+ ". Reason:" + e.getMessage());
				if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
			}

			return;
		}

		String monetoryBalanceId = mscc.getGrantedServiceUnits().getMonetaryBalanceId();
		MonetaryBalance monetoryBalance = processAndGetMonetaryBalance(mscc, response, monetoryBalanceId, executionContext);

		if (Objects.nonNull(monetoryBalance)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Refunding Monetary balance with ID: " + monetoryBalanceId);
				getLogger().debug(MODULE, "Refundable Monetary balance: " + monetoryBalance.getAvailBalance());
			}
			try {
				executionContext.getDDFTable().updateMonetaryBalance(monetoryBalance.getSubscriberId(),
						new SubscriberMonetaryBalanceWrapper(monetoryBalance, null, null,
								"RnC Refund Event", ActionType.UPDATE.name(),null),
						"Refund Event with session Id: "
						+ response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),null);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while performing refund RnC balance for Subscriber ID: "
						+ response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
						+ ". Reason:" + e.getMessage());
				if (com.elitecore.corenetvertex.spr.exceptions.ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
			}

		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Refund Event RnC ABMF processing completed");
		}
	}
}
