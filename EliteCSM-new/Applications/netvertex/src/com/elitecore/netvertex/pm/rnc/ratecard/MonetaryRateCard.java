package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;

import java.util.List;
import java.util.Objects;

public class MonetaryRateCard extends com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCard {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "RATE-CARD";

	public MonetaryRateCard(String id, String name, String description, String keyOne, String keyTwo,
							List<MonetaryRateCardVersion> rateCardVersions, Uom rateUnit, Uom pulseUnit,
							String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName) {
		super(id, name, description, keyOne, keyTwo, rateCardVersions, rateUnit, pulseUnit,
				rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName);
	}

	public boolean apply(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription){

		if (LogManager.getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().print("[ " + MODULE + " ] : " + getName());
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
		}

		boolean rateApplied = false;

		if (LogManager.getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().print("Trying to apply rate card : " + getName());
		}


		try {
			if(Collectionz.isNullOrEmpty(getRateCardVersions())) {
				return false;
			}

			for (MonetaryRateCardVersion rateCardVersion : getRateCardVersions()) {

				if (Objects.isNull(rateCardVersion))
					continue;

				if (LogManager.getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println();
					policyContext.getTraceWriter().print("Trying to select an entry from version: " + rateCardVersion.getName());
				}

				if ( ((com.elitecore.netvertex.pm.rnc.ratecard.RateCardVersion)rateCardVersion).apply(policyContext, quotaReservation, subscription, getKeyOne(), getKeyTwo())) {
					rateApplied = true;
					break;
				}
			}
			return rateApplied;
		} finally {
			if (LogManager.getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().decrementIndentation();
				policyContext.getTraceWriter().decrementIndentation();
			}
		}

	}
}
