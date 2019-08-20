package com.elitecore.netvertex.pm.rnc.pkg;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationScheme;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RnCPackage extends com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage {

	private static final String MODULE = "RnCPackage";

    public RnCPackage(String id, String name, String description,
					  List<String> groupIds, List<RateCardGroup> rateCardGroups, ThresholdNotificationScheme thresholdNotificationScheme,
					  String tag, RnCPkgType pkgType,
					  PkgMode packageMode, PkgStatus pkgStatus, PolicyStatus policyStatus,
					  String failReason, String partialFailReason, ChargingType chargingType,String currency){
    	super(id, name, description, groupIds, rateCardGroups, thresholdNotificationScheme, tag, pkgType, packageMode, pkgStatus,
				policyStatus, failReason, partialFailReason, chargingType,currency);

	}

	public boolean checkApplicability(RoPolicyContextImpl policyContext, Subscription subscription) {
    	return apply(policyContext, new QuotaReservation(), subscription);
	}

	private void processFinalUnitIndication(List<RateCardGroup> rateCardGroups, RoPolicyContextImpl policyContext,
											int rateCardListIndex, QuotaReservation quotaReservation, Subscription subscription) {

		Map.Entry<Long, MSCC> selectedMsccs = quotaReservation.get().iterator().next();

		if (selectedMsccs.getValue().getFinalUnitIndiacation() == null) {
			return;
		}

		for (int nextRateCardListIndex = rateCardListIndex + 1; nextRateCardListIndex < rateCardGroups.size(); nextRateCardListIndex++) {

			RateCardGroup nextRateCardGroup = rateCardGroups.get(nextRateCardListIndex);
			if (((com.elitecore.netvertex.pm.rnc.rcgroup.RateCardGroup)nextRateCardGroup).checkForApplicability(policyContext, subscription)) {

				selectedMsccs.getValue().setFinalUnitIndiacation(null);

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Removed final unit indication set for previous rate card. ");
				}

				break;
			} else {
				continue;
			}
		}
	}

	public boolean apply(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription)  {
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().print("[ " + MODULE + " ] : " + getName());
			policyContext.getTraceWriter().println();
		}


		try {
			if(Collectionz.isNullOrEmpty(getRateCardGroups())) {

				if (LogManager.getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().print(" - RateCardGroups not configured for RnCPackage : " + getName());
				}

				return false;
			}

			long timeOutInSec = getNextSessionTimeOut(policyContext.getCurrentTimePeriod());

			if(timeOutInSec > AccessTimePolicy.NO_TIME_OUT) {
				policyContext.setTimeout(timeOutInSec);
			}

			List<RateCardGroup> rateCardGroups = getRateCardGroups();


			for (int rateCardListIndex = 0; rateCardListIndex < rateCardGroups.size(); rateCardListIndex++) {
				RateCardGroup rateCardGroup = rateCardGroups.get(rateCardListIndex);

				if (Objects.isNull(rateCardGroup)){
					continue;
				}

				if (((com.elitecore.netvertex.pm.rnc.rcgroup.RateCardGroup)rateCardGroup).apply(policyContext, quotaReservation, subscription)) {

					processFinalUnitIndication(rateCardGroups, policyContext, rateCardListIndex, quotaReservation, subscription);

					return true;
				}
			}

			return false;

		} finally {
			if (LogManager.getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().decrementIndentation();
			}
		}

   }
}
