package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDQosMultiplier;
import com.elitecore.corenetvertex.pm.bod.BoDServiceMultiplier;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.TopUpPCC;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSProfile;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BoDPackageHandler {

    private static final String MODULE = "BOD-PKG-HDLR";

    protected void applyBoDPackage(PolicyContext policyContext, FinalQoSSelectionData finalQoSSelectionData) {

        if (Objects.isNull(finalQoSSelectionData)
                || Objects.isNull(finalQoSSelectionData.getQoSProfile())
                || Objects.equals(policyContext.getBasePackage().getId(), finalQoSSelectionData.getQoSProfile().getPackageId()) == false
                || MapUtils.isEmpty(finalQoSSelectionData.getPccRuleToQoSProfile())) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping BoD package application process as base package is not applied");
            }
            return;
        }

        Collection<Subscription> boDPkgSubscriptions = policyContext.getBoDPkgSubscriptions();
        if (boDPkgSubscriptions.isEmpty()) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping BoD package application process as no BoD package subscriptions found");
            }
            return;
        }

        BoDPackage applicableBoDPackage = getApplicableBoDPackage(policyContext, boDPkgSubscriptions, finalQoSSelectionData.getQoSProfile());

        if (Objects.isNull(applicableBoDPackage)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping BoD package application process as no active or matching BoD package subscriptions found");
            }
            return;
        }

        Map<PCCRule, QoSProfile> pccRuleToQoSProfile = new HashMap<>();

        pccRuleToQoSProfile.putAll(finalQoSSelectionData.getPccRuleToQoSProfile());

        for (Map.Entry<PCCRule, QoSProfile> entry : pccRuleToQoSProfile.entrySet()) {

            PCCRule pccRule = entry.getKey();
            QoSProfile qoSProfile = entry.getValue();

            BoDQosMultiplier boDQosMultiplier = applicableBoDPackage.getFupLevelToBoDQosMultipliers().get(finalQoSSelectionData.getQosProfileDetail().getFUPLevel());
            if (Objects.isNull(boDQosMultiplier)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping BoD package application process for QoS Profile " + qoSProfile.getName()
                            + " as no QoS multiplier is configured for current FUP level");
                }
                continue;
            }

            Double multiplier;
            Map<Long, BoDServiceMultiplier> dataServiceIdToServiceMultipliers = boDQosMultiplier.getDataServiceIdToServiceMultipliers();
            if (MapUtils.isEmpty(dataServiceIdToServiceMultipliers)
                    || Objects.isNull(dataServiceIdToServiceMultipliers.get(pccRule.getServiceIdentifier()))) {
                multiplier = boDQosMultiplier.getSessionMultiplier();
            } else {
                multiplier = dataServiceIdToServiceMultipliers.get(pccRule.getServiceIdentifier()).getMultiplier();
            }

            PCCRule topUpPCC = new TopUpPCC(pccRule, (long) (pccRule.getGBRDL() * multiplier), (long) (pccRule.getGBRUL() * multiplier),
                    (long) (pccRule.getMBRDL() * multiplier), (long) (pccRule.getMBRUL() * multiplier));

            finalQoSSelectionData.add(topUpPCC, policyContext.getBasePackage().getId(), qoSProfile);
        }
    }

    private BoDPackage getApplicableBoDPackage(PolicyContext policyContext, Collection<Subscription> boDPkgSubscriptions, QoSProfile qoSProfile) {
        for (Subscription subscription : boDPkgSubscriptions) {
            BoDPackage boDPackage = policyContext.getPolicyRepository().getBoDPackage().byId(subscription.getPackageId());
            if (subscription.getStartTime().getTime() > policyContext.getCurrentTime().getTimeInMillis()) {
                policyContext.setRevalidationTime(subscription.getStartTime());
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping future BoD(name: " + boDPackage.getName()
                            + ") subscription(id: " + subscription.getId() + "), Considering start-time(" + subscription.getStartTime()
                            + ") for session re-validation");
                }
                continue;
            }

            if (Collectionz.isNullOrEmpty(boDPackage.getApplicableQosProfiles()) || boDPackage.getApplicableQosProfiles().contains(qoSProfile.getName())) {
                policyContext.setRevalidationTime(subscription.getEndTime());
                return boDPackage;
            }
        }
        return null;
    }

    public ILogger getLogger() {
        return LogManager.getLogger();
    }
}