package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class UsageReservationProcessor {

    private static final String MODULE = "Usage-Reservation-Builder";

    private final NetVertexServerContext context;


    public UsageReservationProcessor(NetVertexServerContext context) {
        this.context = context;
    }

    public void process(PCRFResponse response, FinalQoSSelectionData qosInformation) {

        QoSProfileDetail qosProfileDetail = qosInformation.getQosProfileDetail();

        if (PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(response.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
            createSessionLevelUsageSupportedReservation(response, qosInformation, qosProfileDetail);
        } else {
            createPCCLevelUsageSupportedUsageReservation(response, qosInformation);
        }
    }

    private void createPCCLevelUsageSupportedUsageReservation(PCRFResponse response, FinalQoSSelectionData qosInformation) {
        // PCC_RULE BEGIN
        Map<String, String> alreadyActivePCCRules = response.getActivePccRules();

        if ( Maps.isNullOrEmpty(alreadyActivePCCRules) == false ) {


            Map<String, String> allPccToSubscriptions = qosInformation.getPccRuleIdToSubscriptionOrPackageId();
            Map<String, String> activeUsageReservation = new LinkedHashMap<String, String>(alreadyActivePCCRules.size());

            for (String pccRuleId : alreadyActivePCCRules.keySet()) {

                PCCRule pccRule = context.getPolicyRepository().getPCCRuleById(pccRuleId);
                if (pccRule == null) {
                    getLogger().warn(MODULE, "Skipping pcc rule(" + pccRuleId
                            + ")for usage reservation. Reason: PCC rule not found.");
                    continue;
                }

                if (pccRule.getUsageMetering() == UsageMetering.DISABLE_QUOTA) {
                    if (getLogger().isDebugLogLevel())
                        getLogger().debug(MODULE, "Skipping pcc rule(" + pccRuleId
                                + ")for usage reservation. Reason: Usage Monitoring is disabled.");
                    continue;
                }

                String subscriptionOrPackageId = allPccToSubscriptions.get(pccRule.getId());
                if (subscriptionOrPackageId != null) {
                    activeUsageReservation.put(pccRule.getMonitoringKey(), subscriptionOrPackageId);
                }
            }

            Map<String, String> oldUsageReservations = response.getUsageReservations();

            addUsageReservation(response, activeUsageReservation, oldUsageReservations);
        }
        // PCC_RULE END

        // CRBN BEGIN
        Map<String, String> alreadyActiveChargingRuleBaseNames = response.getActiveChargingRuleBaseNames();

        if ( Maps.isNullOrEmpty(alreadyActiveChargingRuleBaseNames) == false ) {

            Map<String, String> allChargingRuleBaseNameToSubscriptions = qosInformation.getChargingRuleIdToSubscriptionOrPackageId();
            Map<String, String> activeUsageReservation = new LinkedHashMap<String, String>(alreadyActiveChargingRuleBaseNames.size());

            for (String chargingRuleBaseNameId : alreadyActiveChargingRuleBaseNames.keySet()) {

                ChargingRuleBaseName chargingRuleBaseName = context.getPolicyRepository().getChargingRuleBaseNameById(chargingRuleBaseNameId);
                if (chargingRuleBaseName == null) {
                    getLogger().warn(MODULE, "Skipping ChargingRuleBaseName( " + chargingRuleBaseNameId
                            + ")for usage reservation. Reason: ChargingRuleBaseName not found.");
                    continue;
                }

                for (String monitoringKey : chargingRuleBaseName.getMonitoringKeySliceInformationMap().keySet()) {

                    if (chargingRuleBaseName.getUsageMetering(monitoringKey) == UsageMetering.DISABLE_QUOTA) {
                        if (getLogger().isDebugLogLevel())
                            getLogger().debug(MODULE, "Skipping ChargingRuleBaseName( " + chargingRuleBaseNameId
                                    + " ) for usage reservation. Reason: Usage Monitoring is disabled.");
                        continue;
                    }

                    String subscriptionOrPackageId = allChargingRuleBaseNameToSubscriptions.get(chargingRuleBaseName.getId());
                    if (subscriptionOrPackageId != null) {
                        activeUsageReservation.put(monitoringKey, subscriptionOrPackageId);
                    }
                }
            }

            Map<String, String> oldUsageReservations = response.getUsageReservations();

            addUsageReservation(response, activeUsageReservation, oldUsageReservations);
        }

        // CRBN END
    }

    private void addUsageReservation(PCRFResponse response, Map<String, String> activeUsageReservation, Map<String, String> oldUsageReservations) {
        if (Maps.isNullOrEmpty(oldUsageReservations)) {
            response.setUsageReservations(activeUsageReservation);
        } else {

            if (Maps.isNullOrEmpty(activeUsageReservation) == false) {
                for (Map.Entry<String, String> usageReservationEntry : activeUsageReservation.entrySet()) {
                    if (oldUsageReservations.containsKey(usageReservationEntry.getKey()) == false) {
                        response.addUsageReservation(usageReservationEntry.getKey(), usageReservationEntry.getValue());
                    }
                }
            }

        }
    }

    private void createSessionLevelUsageSupportedReservation(PCRFResponse response, FinalQoSSelectionData qosInformation, QoSProfileDetail qosProfileDetail) {
        if(qosProfileDetail.getUsageMonitoring() == false) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of usage reservation. Reason: Usage Monitoring is disabled for "
                        + qosProfileDetail.getName());
            return;
        }

			/*
				Without this check usage resevation key has quota profile value null.
				We skip this check in PCC_LEVEL_MONITORING becuase in that case it is very difficult to find exact qos in which PCC belongs.
				-- Harsh Patel
			 */
        if(qosProfileDetail.getAllServiceQuotaProfileDetail() == null) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of usage reservation. Reason: Quota profile is not attached for "
                        + qosProfileDetail.getName());
            return;
        }


        Map<String, String> usageReservations = response.getUsageReservations();
        String key = qosInformation.getSelectedSubscriptionIdOrPkgId();

        if (Maps.isNullOrEmpty(usageReservations)) {
            usageReservations = new LinkedHashMap<String, String>();
            usageReservations.put(key, qosProfileDetail.getQuotaProfileId());
            response.setUsageReservations(usageReservations);
        } else {

            String value = usageReservations.get(key);
            if (value == null) {
                response.addUsageReservation(key, qosProfileDetail.getQuotaProfileId());
            } else if (value.contains(qosProfileDetail.getQuotaProfileId()) == false) {
                response.addUsageReservation(key, value + CommonConstants.COMMA + qosProfileDetail.getQuotaProfileId());
            }

        }
    }
}
