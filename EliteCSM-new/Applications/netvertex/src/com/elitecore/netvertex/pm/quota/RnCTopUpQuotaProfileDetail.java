package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QuotaProfileDetail;

import java.util.Collection;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RnCTopUpQuotaProfileDetail extends RnCQuotaProfileDetail implements QuotaProfileDetail {

    private static final String MODULE = "RNC-TOPUP-QUOTA-DETAIL";

    public RnCTopUpQuotaProfileDetail(String quotaProfileId,
									  String name,
									  DataServiceType dataServiceType,
									  int fupLevel,
									  RatingGroup ratingGroup,
									  Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage,
									  long volumePulse,
									  long timePulse,
									  long volumePulseInBytes,
									  long timePulseInSeconds,
									  String voulumePulseUnit,
									  String timePulseUnit,
									  double rate,
									  UsageType rateUnit,
									  QuotaUsageType quotaType,
									  VolumeUnitType unitType,
									  boolean isHsqLevel,
                                      String pccProfileName,
                                      long volumeCarryForwardLimit,
                                      long timeCarryForwardLimit,
                                      String revenueDetail) {
        super(quotaProfileId,
                name,
                dataServiceType,
                fupLevel,
                ratingGroup,
                aggregationKeyToAllowedUsage,
                volumePulse,
                timePulse,
                volumePulseInBytes,
                timePulseInSeconds,
                voulumePulseUnit,
                timePulseUnit,
                rate,
                rateUnit,
                quotaType,
                unitType,
                isHsqLevel,
				pccProfileName,
                volumeCarryForwardLimit,
                timeCarryForwardLimit,
                revenueDetail);
    }

    @Override
    public boolean apply(PolicyContext policyContext, String basePackageId, Subscription currentSubscription,
                         QuotaReservation quotaReservation) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Pre-TopUp subscriptions started.");
        }

        /* Apply Pre TopUps First, If applied skip processing */
        if (policyContext.isPreTopUpChecked() == false && applyTopUps(policyContext, quotaReservation, policyContext.getPreTopUpSubscriptions())) {
            policyContext.preTopUpChecked();
            return true;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Pre-TopUp subscription completed.");
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Base Package " + basePackageId + " started.");
        }

        /* Apply Base package, If applied skip processing */
        if (applyBasePackage(policyContext, basePackageId, null, quotaReservation)) {
            return true;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Base Package completed.");
        }
        /* Apply Spare TopUps only if HSQ level*/

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Spare-TopUp subscription started.");
        }

        boolean result = isHsqLevel() && applyTopUps(policyContext, quotaReservation, policyContext.getSpareTopUpSubscriptions());

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Processing for Spare-TopUp subscription completed.");
        }

        return result;
    }

    @VisibleForTesting
    boolean applyBasePackage(PolicyContext policyContext, String packageId, Subscription currentSubscription, QuotaReservation quotaReservation) {
        return super.apply(policyContext, packageId, currentSubscription, quotaReservation);
    }

    @VisibleForTesting
    boolean applyTopUps(PolicyContext policyContext, QuotaReservation quotaReservation, Collection<Subscription> preTopUpSubscriptions) {

        if (Collectionz.isNullOrEmpty(preTopUpSubscriptions)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping TopUp subscription processing. Reason: No topup subscriptions found");
            }
            return false;
        }

        for (Subscription subscription : preTopUpSubscriptions) {
            QuotaTopUp quotaTopUp = policyContext.getPolicyRepository().getQuotaTopUpById(subscription.getPackageId());
            RnCQuotaProfileDetail quotaProfileDetail = (RnCQuotaProfileDetail) quotaTopUp.getQuotaProfile()
                    .getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID);

			if (quotaTopUp.isTopUpIsEligibleToApply(getPccProfileName()) == false) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping Data TopUp(" + quotaTopUp.getName()
							+ ". Reason: Current PCC Profile(" + getPccProfileName() + ") is not eligible for this TopUp");
				}
				continue;
			}

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Applying Data TopUp: " + quotaTopUp.getName());
			}

            boolean isTopUpApplied = quotaProfileDetail.apply(policyContext, quotaTopUp.getId(), subscription, quotaReservation);
            if (isTopUpApplied == true) {
                MSCC selectedMSCC = quotaReservation.remove(quotaProfileDetail.getRatingGroup().getIdentifier());
                selectedMSCC.setRatingGroup(getRatingGroup().getIdentifier());
                quotaReservation.put(selectedMSCC);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Data TopUp(" + quotaTopUp.getName() + ") is applied successfully");
				}
                return true;
            }

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Data TopUp(" + quotaTopUp.getName() + ") is not applied successfully");
			}
        }

        return false;
    }

}
