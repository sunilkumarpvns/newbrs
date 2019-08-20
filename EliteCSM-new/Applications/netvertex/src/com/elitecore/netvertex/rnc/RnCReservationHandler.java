package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.AddOnSubscriptionComparator;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class RnCReservationHandler {

	private static final String MODULE = "RNC-RESERVATION-HANDLER";

	private PCRFServiceContext serviceContext;

	public RnCReservationHandler(PCRFServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public void handle(PCRFRequest request, PCRFResponse response, RoPolicyContextImpl roPolicyContextImpl, ExecutionContext executionContext) throws OperationFailedException {

		List<Subscription> monetaryRnCAddons = Collectionz.newArrayList();
		List<Subscription> nonMonetaryRnCAddOns = Collectionz.newArrayList();
		createSortedSubscriptionList(roPolicyContextImpl.getSubscriptions(), monetaryRnCAddons, nonMonetaryRnCAddOns, request);

		RnCPackage baseRnCPackage = roPolicyContextImpl.getRnCPackage();

 		if (Objects.isNull(baseRnCPackage) && monetaryRnCAddons.isEmpty() && nonMonetaryRnCAddOns.isEmpty()) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Rejecting request for subscriber ID:"
						+request.getSPRInfo().getSubscriberIdentity()+". Reason: No base RnC package or RnC Addon subscriptions");
			}
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            return;
        }

		applyRoPackages(roPolicyContextImpl, monetaryRnCAddons, nonMonetaryRnCAddOns, baseRnCPackage, executionContext);
		response.setQuotaReservation(roPolicyContextImpl.getGrantedAllMSCC());
	}

	private void applyRoPackages(RoPolicyContextImpl roPolicyContextImpl, List<Subscription> monetaryRnCAddons, List<Subscription> nonMonetaryAddOns,
								 RnCPackage baseRnCPackage, ExecutionContext executionContext) throws OperationFailedException {
		boolean isAddOnApplied = false;
		if(CollectionUtils.isNotEmpty(nonMonetaryAddOns)){
			isAddOnApplied = applyAddOnSubscriptions(nonMonetaryAddOns,roPolicyContextImpl,executionContext);
		}


		if(isAddOnApplied) {
			processFinalUnitIndication(roPolicyContextImpl, monetaryRnCAddons, baseRnCPackage, executionContext);
			return;
		}

		boolean isBaseRnCPackageApplied = false;

		if(baseRnCPackage!=null){
			isBaseRnCPackageApplied = processPackage(roPolicyContextImpl, baseRnCPackage, null);
		}

		if (isBaseRnCPackageApplied) {
			processFinalUnitIndication(roPolicyContextImpl, monetaryRnCAddons, null, executionContext);
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

	private void processFinalUnitIndication(RoPolicyContextImpl roPolicyContextImpl, List<Subscription> monetaryRnCAddons, RnCPackage baseRnCPackage, ExecutionContext executionContext) throws OperationFailedException {
		Map.Entry<Long, MSCC> selectedMsccs = roPolicyContextImpl.getReservations().get().iterator().next();
		if (selectedMsccs.getValue().getFinalUnitIndiacation() == null) {
			return;
        }

		RoPolicyContextImpl tempRoPolicyContext = new RoPolicyContextImpl(roPolicyContextImpl.getPCRFRequest(),
                roPolicyContextImpl.getPCRFResponse(),
                roPolicyContextImpl.getRnCPackage(),
                executionContext,
                roPolicyContextImpl.getPolicyRepository(),
                roPolicyContextImpl.getProductOfferId());

		applyRoPackages(tempRoPolicyContext, monetaryRnCAddons, new ArrayList<>(), baseRnCPackage, executionContext);

		if (isPackageApplied(tempRoPolicyContext)) {
            selectedMsccs.getValue().setFinalUnitIndiacation(null);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Removed final unit indication set in previous package. ");
			}
        }
	}

	private boolean isPackageApplied(RoPolicyContextImpl tempRoPolicyContext) {
		return tempRoPolicyContext.getReservations().get().isEmpty() == false;
	}

	private boolean applyAddOnSubscriptions(List<Subscription> rncAddOnSubscriptions,
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
				processFinalUnitIndication(rncAddOnSubscriptions, roPolicyContextImpl, currentTime, rncAddOnSubscriptionsIndex);
				roPolicyContextImpl.setTimeout(java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(subscription.getEndTime().getTime() - currentTime.getTime()));
				return true;
			}

		}
		return false;
	}

	private void processFinalUnitIndication(List<Subscription> rncAddOnSubscriptions, RoPolicyContextImpl roPolicyContextImpl,
													Timestamp currentTime, int currentExecutedSubscriptionIndex) {

		RnCPackage rnCPackage;
		Map.Entry<Long, MSCC> selectedMsccs = roPolicyContextImpl.getReservations().get().iterator().next();
		if (selectedMsccs.getValue().getFinalUnitIndiacation() == null) {
			return;
		}

		for (int nextRncAddOnSubscriptionsIndex=currentExecutedSubscriptionIndex + 1; nextRncAddOnSubscriptionsIndex<rncAddOnSubscriptions.size(); nextRncAddOnSubscriptionsIndex++) {
            Subscription subscription = rncAddOnSubscriptions.get(nextRncAddOnSubscriptionsIndex);
            rnCPackage = (RnCPackage)getServerContext().getPolicyRepository().getRnCPackage().byId(subscription.getPackageId());

            if (validateSubscription(roPolicyContextImpl, currentTime, subscription) == false) {
                continue;
            }

            if (rnCPackage.checkApplicability(roPolicyContextImpl, subscription)) {
                selectedMsccs.getValue().setFinalUnitIndiacation(null);
                break;
            }
        }
	}

	private boolean validateSubscription(RoPolicyContextImpl roPolicyContextImpl, Timestamp currentTime, Subscription subscription) {
		if(subscription.getStartTime().after(currentTime)){
            roPolicyContextImpl.setTimeout(java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(subscription.getStartTime().getTime()-currentTime.getTime()));
			return false;
        }
		return true;
	}

	private NetVertexServerContext getServerContext() {
		return serviceContext.getServerContext();
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


	private boolean processPackage(RoPolicyContextImpl roPolicyContextImpl, RnCPackage rncPackage, Subscription subscription) {
		return rncPackage.apply(roPolicyContextImpl, roPolicyContextImpl.getReservations(), subscription);
	}
	
	public ILogger getLogger() {
		return LogManager.getLogger();
	}
}