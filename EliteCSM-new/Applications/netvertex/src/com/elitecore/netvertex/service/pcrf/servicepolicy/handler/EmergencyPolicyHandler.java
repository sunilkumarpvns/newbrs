package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PackageProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jay Trivedi
 */
public class EmergencyPolicyHandler extends DataPolicyHandler {

    private static final String MODULE = "EMERGENCY-PLC-HDLR";

    public EmergencyPolicyHandler(PCRFServiceContext pcrfServiceContext) {
        super(pcrfServiceContext);
    }

    @Override
    public FinalQoSSelectionData applyPackage(PolicyContext policyContext, QoSInformation qosInformation) {

        PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
        PCRFResponse pcrfResponse = policyContext.getPCRFResponse();

        String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().println("Emergency QoS selection summary for subscriber(" +
                    subscriberIdentity + "):");
            policyContext.getTraceWriter().incrementIndentation();
        }
        EmergencyPackage appliedEmergencyPackage = null;

        List<String> sprGroupIds = policyContext.getSPInfo().getSPRGroupIds();

        if (Collectionz.isNullOrEmpty(sprGroupIds)) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping emergency handler. Reason: SPR Groups not configured");
            }
            return null;
        }

        List<EmergencyPackage> emergencyPackages = getServerContext().getPolicyRepository().getEmergencyPackages();

        if (Collectionz.isNullOrEmpty(emergencyPackages)) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping emergency handler. Reason: Emergency Packages not configured");
            }
            return null;
        }

        /*
         * This set is used to process Emergency package only single time
         *
         * > One package can fall in multiple groups and one SPR can fall in multiple groups
         * > Each package should apply one time only even if fall in multiple groups
         */
        Set<String> processedPackages = new HashSet<String>();

        boolean policySelected = false;

        for (int i = 0; i < sprGroupIds.size(); i++ ) {

            ArrayList<EmergencyPackage> emergencyPackagesOfGroup = getServerContext().getPolicyRepository().getEmergencyPackagesOfGroup(sprGroupIds.get(i));

            if (Collectionz.isNullOrEmpty(emergencyPackagesOfGroup)) {
                continue;
            }

            for (int packageIndex = 0; packageIndex < emergencyPackagesOfGroup.size(); packageIndex++) {

                EmergencyPackage emergencyPackage = emergencyPackagesOfGroup.get(packageIndex);

                if (processedPackages.add(emergencyPackage.getId()) == false) {
                    continue;
                }

                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Processing emergency package: " + emergencyPackage.getName());
                }

                if (isPolicyApplied(policyContext, qosInformation, emergencyPackage)) {
                    appliedEmergencyPackage = emergencyPackage;
                    policySelected = true;
                    break;
                }
            }

            if (policySelected == true) {
                break;
            }
        }

        if (appliedEmergencyPackage != null && qosInformation.getQoSProfileDetail() != null) {
            pcrfResponse.setEmergencySession(true);
            pcrfResponse.setAttribute(PCRFKeyConstants.EMERGENCY_PACKAGE.val, appliedEmergencyPackage.getName());
            pcrfResponse.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM1.val, appliedEmergencyPackage.getParam1());
            pcrfResponse.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM2.val, appliedEmergencyPackage.getParam2());
            pcrfResponse.setAttribute(PCRFKeyConstants.IPCAN_REDIRECT_URL.val, qosInformation.getQoSProfileDetail().getRedirectURL());
        }

        if (getLogger().isInfoLogLevel()) {

            policyContext.getTraceWriter().println("QoS selection summary for subscriber(" +
                    subscriberIdentity + "):");
            policyContext.getTraceWriter().incrementIndentation();
        }

        return qosInformation.endProcess();
    }

    @VisibleForTesting
    boolean isPolicyApplied(PolicyContext policyContext, QoSInformation qosInformation, EmergencyPackage emergencyPackage) {

        if (emergencyPackage.getStatus() == PolicyStatus.FAILURE) {
            getLogger().warn(MODULE, "Skipping emergency package:" + emergencyPackage.getName() + ". Reason:"
                    + emergencyPackage.getFailReason());
            return false;
        }

        if (emergencyPackage.getAvailabilityStartDate().getTime() > policyContext.getCurrentTime().getTimeInMillis()) {

            policyContext.setRevalidationTime(emergencyPackage.getAvailabilityStartDate());

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping future emergency package: " + emergencyPackage.getName() + ", Considering start-time("
                        + emergencyPackage.getAvailabilityStartDate()
                        + ") for session revalidation");

            }
            return false;
        }

        if (emergencyPackage.getAvailabilityEndDate() != null && emergencyPackage.getAvailabilityEndDate().getTime() <= policyContext.getCurrentTime().getTimeInMillis()) {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Emergency package: " + emergencyPackage.getName() + " availability end date is exceeded");
            }
            return false;
        }

        apply(policyContext, (com.elitecore.netvertex.pm.EmergencyPackage) emergencyPackage);

        if (qosInformation.getQoSProfileDetail() != null) {

            if (emergencyPackage.getAvailabilityEndDate() != null) {
                policyContext.setRevalidationTime(emergencyPackage.getAvailabilityEndDate());
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering end-time(" + emergencyPackage.getAvailabilityEndDate() + ") of emergenecy package: "
                            + emergencyPackage.getName() + " for session revalidation");
                }
            }
            return true;
        }

       return false;
    }

    private void apply(PolicyContext policyContext, com.elitecore.netvertex.pm.EmergencyPackage emergencyPackage) {
        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Applying emergency package: "+ getName());
        }

        PackageProcessor.apply(policyContext, emergencyPackage, null);

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Applying emergency package: "+ getName() + " completed");
    }

    @Override
    public String getName() {
        return MODULE;
    }

}
