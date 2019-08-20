package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.PackageProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author Jay Trivedi
 */

public class PromotionalPolicyHandler {

    private static final String MODULE = "PROMOTIONAL-PLC-HDLR";
    private NetVertexServerContext serverContext;

    public PromotionalPolicyHandler(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void applyPackage(PolicyContext policyContext) {

        if (Collectionz.isNullOrEmpty(serverContext.getPolicyRepository().getPromotionalPackages())) {
            return;
        }

        PCRFResponse pcrfResponse = policyContext.getPCRFResponse();

        String subscriberId = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
            if (getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Skipping promotional Packages execution.Reason: Gateway does not support pcc level metering");
            return;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Applying promotional packages for subscriber ID: " + subscriberId);
        }

        List<String> sprGroupIds = policyContext.getSPInfo().getSPRGroupIds();

        if (Collectionz.isNullOrEmpty(sprGroupIds)) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping promotional handler. Reason: SPR Groups not configured");
            }
            return;
        }

        /*
         * This set is used to process promotional package only single time
         *
         * > One package can fall in multiple groups and one SPR can fall in multiple groups
         * > Each promotional package should apply one time only even if fall in multiple groups
         */
        Set<String> processedPackages = new HashSet<String>();

        for (int i = 0; i < sprGroupIds.size(); i++) {
            ArrayList<PromotionalPackage> promotionalPackagesOfGroup = serverContext.getPolicyRepository().getPromotionalPackagesOfGroup(sprGroupIds.get(i));

            if (Collectionz.isNullOrEmpty(promotionalPackagesOfGroup)) {
                continue;
            }

            for (int policyIndex=0; policyIndex < promotionalPackagesOfGroup.size(); policyIndex++) {
                PromotionalPackage currentPackage = promotionalPackagesOfGroup.get(policyIndex);

                if (processedPackages.add(currentPackage.getId()) == false) {
                    continue;
                }

                apply(policyContext, subscriberId, currentPackage);
            }
        }
    }

    @VisibleForTesting
    void apply(PolicyContext policyContext, String subscriberId,
               PromotionalPackage promotionalPackage) {

        if (promotionalPackage.getStatus() == PolicyStatus.FAILURE) {
            getLogger().warn(MODULE, "Skipping promotional package: " + promotionalPackage.getName() + " for subscriber ID: " + subscriberId
                    + ". Reason: Promotional package: " + promotionalPackage.getName() + " has status FAILURE. Reason: " + promotionalPackage.getFailReason());
            return;
        }

        if (getLogger().isLogLevel(LogLevel.DEBUG))
            getLogger().debug(MODULE, "Applying promotional package(" + promotionalPackage.getName() + ")");

        if (promotionalPackage.getAvailabilityStartDate().getTime() > policyContext.getCurrentTime().getTimeInMillis()) {

            policyContext.setRevalidationTime(promotionalPackage.getAvailabilityStartDate());

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping future promotional package(name: " + promotionalPackage.getName()
                        + "), Considering start-time(" + promotionalPackage.getAvailabilityStartDate()
                        + ") for session revalidation");
            }
            return;
        }

        if (promotionalPackage.getAvailabilityEndDate() != null && promotionalPackage.getAvailabilityEndDate().getTime() <= policyContext.getCurrentTime().getTimeInMillis()) {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Promotional package: " + promotionalPackage.getName() + " availability end date is exceeded");
            }
            return;
        }

        if (getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Applying promotional package: " + promotionalPackage.getName() + "-" + promotionalPackage.getId());

        PackageProcessor.apply(policyContext, promotionalPackage, null);

        if (getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Promotional package:" + promotionalPackage.getName() + "-" + promotionalPackage.getId() + " completed");
        
    }
}
