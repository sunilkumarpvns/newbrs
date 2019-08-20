package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;
import com.elitecore.netvertex.pm.quota.GyPolicyContextImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.SubscriberPolicySelectionEngine;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.PackageSelectionState;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.ServiceSelectionState;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ReservationHandler {

	private SubscriberPolicySelectionEngine subscriberPolicySelectionEngine;

	public ReservationHandler(PCRFServiceContext serviceContext) {
		this.subscriberPolicySelectionEngine = new SubscriberPolicySelectionEngine(serviceContext);
	}

	public void handle(PCRFRequest request, ExecutionContext executionContext, GyPolicyContextImpl gyPolicyContextImpl) throws OperationFailedException {

		RatingGroupSelectionState pccProfileSelectionState = request.getPCCProfileSelectionState();

		if(Objects.isNull(pccProfileSelectionState) || pccProfileSelectionState.getPackageSelectionStates().isEmpty()) {
			subscriberPolicySelectionEngine.applyPackage(gyPolicyContextImpl);
		}else{
			List<MSCC> msccs = request.getReportedMSCCs();
			if(Collectionz.isNullOrEmpty(msccs)){
				processAllRG(pccProfileSelectionState, executionContext, gyPolicyContextImpl);
			}else{
				processMSCC(msccs, pccProfileSelectionState, executionContext, gyPolicyContextImpl);
			}
		}

		gyPolicyContextImpl.setGrantedMSCCs();
	}

	private void processAllRG(RatingGroupSelectionState pccProfileSelectionState, ExecutionContext executionContext,
							  GyPolicyContextImpl gyPolicyContextImpl) throws OperationFailedException {
		for(Map.Entry<Long, ServiceSelectionState> ratingGroupToStateEntry : pccProfileSelectionState.getPackageSelectionStates().entrySet()){
			processSelectionState(executionContext, gyPolicyContextImpl, ratingGroupToStateEntry.getKey(), ratingGroupToStateEntry.getValue());
		}
	}

	private void processMSCC(List<MSCC> msccs, RatingGroupSelectionState pccProfileSelectionState, ExecutionContext executionContext,
							 GyPolicyContextImpl gyPolicyContextImpl) throws OperationFailedException {

		for (MSCC mscc : msccs) {

			if (ReportingReason.FINAL == mscc.getReportingReason()) {
				continue;
			}

			ServiceSelectionState serviceSelectionState = pccProfileSelectionState.getServiceSelectionState(mscc.getRatingGroup());
			processSelectionState(executionContext, gyPolicyContextImpl, mscc.getRatingGroup(), serviceSelectionState);
		}
	}

	private void processSelectionState(ExecutionContext executionContext, GyPolicyContextImpl gyPolicyContextImpl,
									   long ratingGroup, ServiceSelectionState serviceSelectionState) throws OperationFailedException {

		for (PackageSelectionState packageSelectionState : serviceSelectionState.getAll().values()) {

            UserPackage aPackage = packageSelectionState.getPackage();
            if (QuotaProfileType.RnC_BASED != aPackage.getQuotaProfileType()) {
                continue;
            }

            QoSProfile qosProfile = packageSelectionState.getQoSProfile();

            Subscription subscription = executionContext.getSubscriptions().get(packageSelectionState.getSubscriptionId());

            if (qosProfile.getDataRateCard() != null) {
                ((DataRateCard) qosProfile.getDataRateCard()).applyReservation(gyPolicyContextImpl, aPackage.getId(),
                        subscription, gyPolicyContextImpl.getReservations());
            } else if (qosProfile.getQuotaProfile() != null) {
                ((com.elitecore.netvertex.pm.QuotaProfile) qosProfile.getQuotaProfile()).applyWithRG(gyPolicyContextImpl, aPackage.getId(),
                        subscription, gyPolicyContextImpl.getReservations(), packageSelectionState.getLevel(), ratingGroup);
            }
		}
	}

	public ILogger getLogger() {
		return LogManager.getLogger();
	}
}