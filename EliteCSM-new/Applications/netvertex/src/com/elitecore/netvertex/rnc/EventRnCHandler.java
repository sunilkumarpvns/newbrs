package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.Cache;
import com.elitecore.corenetvertex.util.PartitioningCache;
import com.elitecore.corenetvertex.util.SystemPropertyReaders;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.core.util.TaskSchedulerAdapter;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.AddOnSubscriptionComparator;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class EventRnCHandler extends ServiceHandler {
	private static final String MODULE = "EVENT-RnC-HNDLR";
	private RnCEventABMFHandler rnCEventABMFHandler;
	private Cache<String, MSCC> sessionIdToLastUsedPackageCache;
	private static final String SYS_PARAM_EVENT_RESERVATION_RETENTION_DURATION = "event.reservation.retention.duration";
	private static final int DEFAULT_EVENT_RESERVATION_RETENTION_DURATION = 60;
	private final long eventReservationRetentionDuration;

	public EventRnCHandler(PCRFServiceContext serviceContext) {
		super(serviceContext);
		this.rnCEventABMFHandler = new RnCEventABMFHandler();
		this.eventReservationRetentionDuration = new SystemPropertyReaders.NumberReaderBuilder(SYS_PARAM_EVENT_RESERVATION_RETENTION_DURATION)
				.onFail(DEFAULT_EVENT_RESERVATION_RETENTION_DURATION, "Unable to get system property: " + SYS_PARAM_EVENT_RESERVATION_RETENTION_DURATION)
				.build().read().longValue();
		this.sessionIdToLastUsedPackageCache = new PartitioningCache.CacheBuilder<String, MSCC>(new TaskSchedulerAdapter(serviceContext.getServerContext()
				.getTaskScheduler()), eventReservationRetentionDuration).build();
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFRequest request = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;

		String requestedServiceId = request.getAttribute(PCRFKeyConstants.CS_SERVICE.val);
		Service requestedService = getServerContext().getPolicyRepository().getService().byId(requestedServiceId);

		if (requestedService == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Rejecting request for subscriber ID:"
						+ request.getSPRInfo().getSubscriberIdentity() + ". Reason: Service " + requestedServiceId + " is Inactive.");
			}
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

		response.setReportSummary(new ReportSummary());

		if (PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val.equals(request.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val))) {
			try {
				handleDirectDebitProcessing(request, response, executionContext);
			} catch (Exception e) {
				LogManager.getLogger().trace(e);
			}
		} else if (PCRFKeyValueConstants.REQUESTED_ACTION_REFUND_ACCOUNT.val.equals(response.getAttribute(PCRFKeyConstants.REQUESTED_ACTION.val))) {
			handleRefundProcessing(request, response, executionContext);
		}

		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, response.getReportSummary().toString());
		}
	}

	private void handleRefundProcessing(PCRFRequest request, PCRFResponse response, ExecutionContext executionContext) {
		String coreSessionId = request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);

		MSCC mscc;
		try {
			mscc = sessionIdToLastUsedPackageCache.remove(coreSessionId);
		} catch (Exception e) {
			getLogger().error(MODULE, "Skipping refund processing for core-session ID: " + coreSessionId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_END_USER_SERVICE_DENIED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

		if (mscc == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Skipping refund processing for subscriber ID: " + request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)
						+ ". Reason: No reserved units found from cache with core-session ID: " + coreSessionId);
			}
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_END_USER_SERVICE_DENIED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

		rnCEventABMFHandler.handleRefund(response, mscc, executionContext);
	}

	private void handleDirectDebitProcessing(PCRFRequest request, PCRFResponse response, ExecutionContext executionContext) throws OperationFailedException {
		RnCPackage baseRnCPackage = getBasePackage(request);

		String productOfferId = getServerContext().getPolicyRepository().getProductOffer().byName(request.getSPRInfo().getProductOffer()).getId();

		RoPolicyContextImpl roPolicyContext = new RoPolicyContextImpl(request, response, baseRnCPackage, executionContext, getServerContext().getPolicyRepository(), productOfferId);

		List<Subscription> monetaryRnCAddons = Collectionz.newArrayList();
		List<Subscription> nonMonetaryRnCAddOns = Collectionz.newArrayList();

		createSortedSubscriptionList(roPolicyContext.getSubscriptions(), monetaryRnCAddons, nonMonetaryRnCAddOns, request);

		if (Objects.isNull(baseRnCPackage) && monetaryRnCAddons.isEmpty() && nonMonetaryRnCAddOns.isEmpty()) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Rejecting request for subscriber ID:"
						+request.getSPRInfo().getSubscriberIdentity()+". Reason: No base RnC package or RnC Addon subscriptions");
			}
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}


		applyRoPackages(roPolicyContext, monetaryRnCAddons, nonMonetaryRnCAddOns, baseRnCPackage, executionContext);

		response.setQuotaReservation(roPolicyContext.getGrantedAllMSCC());

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, roPolicyContext.getTrace());
		}

		MSCC grantedMSCC = roPolicyContext.getReservations().get().iterator().next().getValue();

		if (ResultCode.SUCCESS != grantedMSCC.getResultCode()) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Rejecting request for subscriber ID:"
						+ request.getSPRInfo().getSubscriberIdentity() + ". Reason: No package satisfied");
			}
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_END_USER_SERVICE_DENIED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

		setCostInformationAVP(response);

		processCache(response, grantedMSCC);

		rnCEventABMFHandler.handleDirectDebit(response, executionContext);
	}

	public void applyRoPackages(RoPolicyContextImpl roPolicyContextImpl, List<Subscription> monetaryRnCAddons, List<Subscription> nonMonetaryAddOns,
								 RnCPackage baseRnCPackage, ExecutionContext executionContext) throws OperationFailedException {

		boolean isAddOnApplied = false;
		if(CollectionUtils.isNotEmpty(nonMonetaryAddOns)){
			isAddOnApplied = applyAddOnSubscriptions(nonMonetaryAddOns,roPolicyContextImpl,executionContext);
		}


		if(isAddOnApplied == true) {
			return;
		}

		if(baseRnCPackage!=null){
			processPackage(roPolicyContextImpl, baseRnCPackage, null);
		}

		if(CollectionUtils.isNotEmpty(monetaryRnCAddons)) {
			if(roPolicyContextImpl.getCurrentMonetaryBalance().isServiceBalanceExist(roPolicyContextImpl.getPCRFRequest().getAttribute(PCRFKeyConstants.CS_SERVICE.val))) {
				QuotaReservation quotaReservation = roPolicyContextImpl.getReservations();
				if((quotaReservation.isNonMonetaryBalanceReserved() == false) || quotaReservation.isMonetaryBalanceReserved()){
					applyAddOnSubscriptions(monetaryRnCAddons, roPolicyContextImpl, executionContext);
				}
			}
		}


	}

	public boolean applyAddOnSubscriptions(List<Subscription> rncAddOnSubscriptions,
											RoPolicyContextImpl roPolicyContextImpl, ExecutionContext executionContext) {

		Timestamp currentTime = new Timestamp(executionContext.getCurrentTime().getTimeInMillis());

		for(int rncAddOnSubscriptionsIndex=0; rncAddOnSubscriptionsIndex<rncAddOnSubscriptions.size(); rncAddOnSubscriptionsIndex++){

			Subscription subscription = rncAddOnSubscriptions.get(rncAddOnSubscriptionsIndex);
			RnCPackage rnCPackage = (RnCPackage)getServerContext().getPolicyRepository().getRnCPackage().byId(subscription.getPackageId());
			if (validateSubscription(roPolicyContextImpl, currentTime, subscription) == false) {
				continue;
			}

			if(Strings.isNullOrBlank(subscription.getFnFGroupName()) == false){
				String calledPartyNumber = roPolicyContextImpl.getPCRFRequest().getAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal());
				if(Collectionz.isNullOrEmpty(subscription.getFnFGroupMembers()) || subscription.getFnFGroupMembers().contains(calledPartyNumber) == false){
					continue;
				}
			}

			if(processPackage(roPolicyContextImpl, rnCPackage, subscription)){
				return true;
			}

		}
		return false;
	}

	private boolean validateSubscription(RoPolicyContextImpl roPolicyContextImpl, Timestamp currentTime, Subscription subscription) {
		if(subscription.getStartTime().after(currentTime)){
			roPolicyContextImpl.setTimeout(java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(subscription.getStartTime().getTime()-currentTime.getTime()));
			return false;
		}
		return true;
	}




	private boolean processPackage(RoPolicyContextImpl roPolicyContextImpl, RnCPackage rncPackage, Subscription subscription) {
		return rncPackage.apply(roPolicyContextImpl, roPolicyContextImpl.getReservations(), subscription);
	}

	private void createSortedSubscriptionList(LinkedHashMap<String, Subscription> addOnSubscriptions, List<Subscription> monetaryRnCAddons, List<Subscription> nonMonetaryRnCAddOns, PCRFRequest request) {
		for (Subscription subscription : addOnSubscriptions.values()) {

			com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage rnCPackage = getServerContext().getPolicyRepository().getRnCPackage().byId(subscription.getPackageId());

			if (rnCPackage == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping subscription( " + subscription.getId()
							+ ") for subscriber ID: " + request.getSPRInfo().getSubscriberIdentity()
							+ ". Reason: Could not find RnC package with Id: " + subscription.getPackageId() + ".");
				}

				continue;
			}

			ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byId(subscription.getProductOfferId());

			if (productOffer ==null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping subscription(id: " + subscription.getId() + ") for subscriber ID: " + request.getSPRInfo().getSubscriberIdentity()
							+ ". Reason: Could not find product offer with Id: " + subscription.getProductOfferId() + ".");
				}
				continue;
			}

			if(productOffer.isRnCPackageAttachedWithService(rnCPackage.getId(),request.getAttribute(PCRFKeyConstants.CS_SERVICE.val))==false){
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skip subscription(id: " + subscription.getId() + ") for subscriber ID: " + request.getSPRInfo().getSubscriberIdentity()
							+ ". Reason: Subscription is not for the service " + request.getAttribute(PCRFKeyConstants.CS_SERVICE.val));
				}
				continue;
			}

			if (rnCPackage.getPolicyStatus() == PolicyStatus.FAILURE) {
				getLogger().warn(MODULE, "Skip subscription(id: " + subscription.getId() + ") for subscriber ID: " + request.getSPRInfo().getSubscriberIdentity()
						+ ". Reason: Subscription package(name:"+rnCPackage.getName()+") has status FAILURE.");
				continue;
			}

			if(RnCPkgType.MONETARY_ADDON == rnCPackage.getPkgType()) {
				monetaryRnCAddons.add(subscription);
			} else if(RnCPkgType.NON_MONETARY_ADDON == rnCPackage.getPkgType()){
				nonMonetaryRnCAddOns.add(subscription);
			}
		}
		Collections.sort(monetaryRnCAddons, AddOnSubscriptionComparator.instance().reversed());
		Collections.sort(nonMonetaryRnCAddOns, AddOnSubscriptionComparator.instance().reversed());
	}

	private void processCache(PCRFResponse response, MSCC grantedMSCC) {
		String coreSessionId = response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
		sessionIdToLastUsedPackageCache.put(coreSessionId, grantedMSCC);
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		return PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val));
	}

	private RnCPackage getBasePackage(PCRFRequest pcrfRequest) {

		String subscriberPackage = null;

		if(Objects.nonNull(pcrfRequest.getSPRInfo())) {
			subscriberPackage = pcrfRequest.getSPRInfo().getProductOffer();
		}

		String subscriberId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

		if (Objects.isNull(subscriberPackage)) {
			if (getLogger().isErrorLogLevel()){
				getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId +
						". Reason: RnC package information not found in subscriber profile");
			}
			return null;
		}

		ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(subscriberPackage);
		if(Objects.isNull(productOffer)){
			if (getLogger().isErrorLogLevel()){
				getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: product offer(" + subscriberPackage + ") not found in policy repository");
			}
			return null;
		}

		List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
		if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList)){
			if (getLogger().isErrorLogLevel()){
				getLogger().error(MODULE, "Not applying base RnC for subscriber ID: " + subscriberId + ". Reason: RnC package not configured in product offer(" + subscriberPackage + ")");
			}
			return null;
		}

		RnCPackage rncPackage = null;
		for (ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList) {
			Service service = productOfferServicePkgRel.getServiceData();
			RnCPackage selectedRncPackage = (RnCPackage) productOfferServicePkgRel.getRncPackageData();

			if (service.getId().equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val))) {
				rncPackage = selectedRncPackage;
				break;
			} else {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Not applying base RnC package(" + selectedRncPackage.getName() + ") for subscriber ID: " + subscriberId + ". Reason: RnC package is for service " + service.getId());
				}
			}
		}

		if (Objects.isNull(rncPackage)) {
			if (getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Not applying base RnC package for subscriber ID: " + subscriberId + ". Reason: subscriber RnC package(" + subscriberPackage + ") not found in policy repository");
			}
			return null;
		}

		if (rncPackage.getPolicyStatus() == PolicyStatus.FAILURE) {
			if (getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Not applying base RnC package for subscriber ID: " + subscriberId
						+ ". Reason: subscriber RnC package(" + subscriberPackage + ") has status FAILURE. Reason: "
						+ rncPackage.getFailReason());
			}
			return null;
		}

		if (ChargingType.EVENT != rncPackage.getChargingType()) {
			if (getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Not applying base RnC package for subscriber ID: " + subscriberId
						+ ". Reason: Subscriber RnC package(" + subscriberPackage + ") has Charging Type: " + rncPackage.getChargingType());
			}
			return null;
		}

		return rncPackage;
	}


	@Override
	public String getName() {
		return MODULE;
	}

	public void setCostInformationAVP(PCRFResponse response) {

		GyServiceUnits grantedServiceUnits = response.getGrantedMSCCs().get(0).getGrantedServiceUnits();
		if(Objects.isNull(grantedServiceUnits) || grantedServiceUnits.getReservedMonetaryBalance() == 0.0){
			return;
		}

		String systemCurrency = getServerContext().getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency();
		int exponent = Currency.getInstance(systemCurrency).getDefaultFractionDigits();
		long valueDigit = BigDecimal.valueOf(grantedServiceUnits.getReservedMonetaryBalance()).multiply((BigDecimal.valueOf(10).pow(exponent))).longValue();
		response.setAttribute(PCRFKeyConstants.CI_VALUE_DIGIT.val, String.valueOf(valueDigit));
		response.setAttribute(PCRFKeyConstants.CI_COST_UNIT.val, ChargingType.EVENT.name());
	}
}
