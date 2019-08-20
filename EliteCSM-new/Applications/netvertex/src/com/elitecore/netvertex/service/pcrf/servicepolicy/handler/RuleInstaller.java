package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RuleInstaller {

    private static final String MODULE = "RULE-INSTALL-BUILDER";
    private final NetVertexServerContext serverContext;


    public RuleInstaller(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }


    public void installRules(PCRFRequest pcrfRequest, PCRFResponse response, FinalQoSSelectionData finalQoSInfomationData) {
        setInstallableAndRemovablePCCRules(response, finalQoSInfomationData.getPccRules(), pcrfRequest.getActivePccRules(), finalQoSInfomationData.getPccRuleIdToSubscriptionOrPackageId());
        setInstallableAndRemovableChargingRuleBaseNames(response, finalQoSInfomationData.getChargingRuleBaseNames(), pcrfRequest.getActiveChargingRuleBaseNames(), finalQoSInfomationData.getChargingRuleIdToSubscriptionOrPackageId());
    }

    private void setInstallableAndRemovablePCCRules(PCRFResponse response,
                                                    Map<Long, PCCRule> selectedPCCRules,
                                                    Map<String, String> alreadyActivePCCRulesIdToSubscriptions,
                                                    Map<String, String> allPccRuleIdToSubscriptionOrPackageIdMap) {


        String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        List<String> removablePCCRules = new ArrayList<String>();
        /*
         * if no pcc rule satisfied then we need to move all previously active
         * pcc rule in removable pcc rules
         */
        if (Maps.isNullOrEmpty(selectedPCCRules)) {

            if (onNoPCCSelected(response, alreadyActivePCCRulesIdToSubscriptions, subscriberIdentity, removablePCCRules)) {
                return;
            }

        } else {

            if (getLogger().isInfoLogLevel()) {
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append("Selected PCC Rules:");
                for (PCCRule pccRule : selectedPCCRules.values()) {
                    logBuilder.append(pccRule.getName());
                }

                getLogger().info(MODULE, logBuilder.toString());
            }

            Map<String, String> activePccIdToSubscriptions = new HashMap<>(8);
            for (PCCRule selectedPCC : selectedPCCRules.values()) {
                activePccIdToSubscriptions.put(selectedPCC.getId(), allPccRuleIdToSubscriptionOrPackageIdMap.get(selectedPCC.getId()));
            }
            response.setActivePccRules(activePccIdToSubscriptions);

            if (alreadyActivePCCRulesIdToSubscriptions == null) {
                response.setInstallablePCCRules(new ArrayList<>(selectedPCCRules.values()));
            } else {
                addInstallableAndRemovablePCCRules(response, selectedPCCRules, alreadyActivePCCRulesIdToSubscriptions, allPccRuleIdToSubscriptionOrPackageIdMap, subscriberIdentity, removablePCCRules);
            }
        }
        response.setRemovablePCCRules(removablePCCRules);
    }

    private void addInstallableAndRemovablePCCRules(PCRFResponse response, Map<Long, PCCRule> selectedPCCRules, Map<String, String> alreadyActivePCCRulesIdToSubscriptions, Map<String, String> allPccRuleIdToSubscriptionOrPackageIdMap, String subscriberIdentity, List<String> removablePCCRules) {
        /*
         * here we create installable and removable pcc rules.
         *
         * Rule 1 : Removable pcc should be (already installed rule -
         * selected pcc rules) Rule 2 : Installable pcc rule should be
         * (selected pcc rules - already installed rule)
         *
         *
         * first we add all selected pcc rules in installable pcc rules.
         *
         * As we all selected pcc rules in installable pcc rules now we
         * need to only take care for already installable pcc rules. We
         * need to remove pcc rule from installable pcc rule is we found
         * already installable pcc rule selected again. if already
         * installed pcc rule not selected then we need to add rule in
         * removable pcc rules
         *
         *
         * procedure:
         *
         * for each already active PCCRule,
         *
         * check any pccrule is selected for same service
         *
         * if pccrule not select then no pcc rule satisfied for service
         * and need to move that pcc rule into removab pccrule not
         * selectle rule else if same pcc rule and subscription is
         * selected then we need to remove from installbel rule as it is
         * already installed then there is no meaning for installing it
         * again else as same is not selected we need remove the rule so
         * move that pcc rule into removable rule
         */

        List<PCCRule> installablePCCRules = new ArrayList<PCCRule>(selectedPCCRules.values());
        for (Map.Entry<String, String> pccEntry : alreadyActivePCCRulesIdToSubscriptions.entrySet()) {

            String alreadyActivPccId = pccEntry.getKey();

            PCCRule alreadyActivePccRule = serverContext.getPolicyRepository().getPCCRuleById(alreadyActivPccId);
            if (alreadyActivePccRule == null) {
                getLogger().warn(MODULE, "Skipping PCCRule with id:" + alreadyActivPccId
                        + " from installed rules for subscriber ID: " + subscriberIdentity
                        + ". Reason: pcc rule not found from repository");
                continue;
            }

            PCCRule selectedPCCRule = selectedPCCRules.get(alreadyActivePccRule.getServiceIdentifier());
            if (selectedPCCRule == null) {
                removablePCCRules.add(alreadyActivePccRule.getName());
            } else {

                String subscriptionId = pccEntry.getValue();
                if (selectedPCCRule.getId().equalsIgnoreCase(alreadyActivPccId)) {

                    /*
                     * NO NEED TO INSTALL PCC FOR BELOW CASES:
                     *
                     * - if PCC is static type.
                     *
                     * - if subscriptionId is null for previous active
                     * pcc.
                     *
                     * - if previous active pcc and current active pcc
                     * from same subscription.
                     */
                    if (selectedPCCRule.isPredifine() || subscriptionId == null
                            || allPccRuleIdToSubscriptionOrPackageIdMap.get(selectedPCCRule.getId()).equals(subscriptionId)) {
                        // NOTE: remove pcc by using already exist pcc
                        // reference only
                        installablePCCRules.remove(selectedPCCRule);
                    }
                } else {
                    removablePCCRules.add(alreadyActivePccRule.getName());
                }
            }
        }

        response.setInstallablePCCRules(installablePCCRules);
    }

    private boolean onNoPCCSelected(PCRFResponse response, Map<String, String> alreadyActivePCCRulesIdToSubscriptions, String subscriberIdentity, List<String> removablePCCRules) {
        response.setActivePccRules(null);
        if (alreadyActivePCCRulesIdToSubscriptions == null) {
            return true;
        }

        for (String alreadyActivPccId : alreadyActivePCCRulesIdToSubscriptions.keySet()) {
            PCCRule alreadyActivePccRule = serverContext.getPolicyRepository().getPCCRuleById(alreadyActivPccId);
            if (alreadyActivePccRule == null) {
                getLogger().warn(MODULE, "Skipping PCCRule with id:" + alreadyActivPccId
                        + " from removed rules for subscriber ID: " + subscriberIdentity
                        + ". Reason: pcc rule not found from repository");
                continue;
            }
            removablePCCRules.add(alreadyActivePccRule.getName());
        }
        return false;
    }

    private void setInstallableAndRemovableChargingRuleBaseNames(PCRFResponse response,
                                                                 List<ChargingRuleBaseName> selectedChargingRuleBaseNames,
                                                                 Map<String, String> alreadyActiveChargingRulesIdToSubscriptions,
                                                                 Map<String, String> allChargingRuleIdToSubscriptionOrPackageIdMap) {


        String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        List<String> removableChargingRuleBaseNames = new ArrayList<String>();
        /*
         * if no chargingRuleBaseName satisfied then we need to move all previously active
         * ChargingRuleBaseName in removable chargingRuleBaseNames
         */
        if (Collectionz.isNullOrEmpty(selectedChargingRuleBaseNames)) {

            response.setActiveChargingRuleBaseNames(null);
            if (alreadyActiveChargingRulesIdToSubscriptions == null) {
                return;
            }

            for (String alreadyActivChargingRuleId : alreadyActiveChargingRulesIdToSubscriptions.keySet()) {
                ChargingRuleBaseName alreadyActiveChargingRule = serverContext.getPolicyRepository().getChargingRuleBaseNameById(alreadyActivChargingRuleId);
                if (alreadyActiveChargingRule == null) {
                    getLogger().warn(MODULE, "Skipping ChargingRuleBaseName with id:" + alreadyActivChargingRuleId
                            + " from removed rules for subscriber ID: " + subscriberIdentity
                            + ". Reason: ChargingRuleBaseName not found from repository");
                    continue;
                }
                removableChargingRuleBaseNames.add(alreadyActiveChargingRule.getName());
            }

        } else {

            if (getLogger().isInfoLogLevel()) {
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append("Selected ChargingRuleBaseNames:");
                for (ChargingRuleBaseName chargingRuleBaseName : selectedChargingRuleBaseNames) {
                    logBuilder.append(chargingRuleBaseName.getName()).append(", ");
                }

                getLogger().info(MODULE, logBuilder.toString());
            }

            Map<String, String> activeChargingRuleBaseNameIdToSubscriptions = new HashMap<String, String>(8);
            for (ChargingRuleBaseName chargingRuleBaseName : selectedChargingRuleBaseNames) {
                activeChargingRuleBaseNameIdToSubscriptions.put(chargingRuleBaseName.getId(), allChargingRuleIdToSubscriptionOrPackageIdMap.get(chargingRuleBaseName.getId()));
            }
            response.setActiveChargingRuleBaseNames(activeChargingRuleBaseNameIdToSubscriptions);

            if (alreadyActiveChargingRulesIdToSubscriptions == null) {
                response.setInstallableChargingRuleBaseNames(new ArrayList<>(selectedChargingRuleBaseNames));
            } else {
                addInstallableAndRemovableCRBN(response,
                        selectedChargingRuleBaseNames,
                        alreadyActiveChargingRulesIdToSubscriptions,
                        allChargingRuleIdToSubscriptionOrPackageIdMap,
                        subscriberIdentity,
                        removableChargingRuleBaseNames);
            }
        }
        response.setRemovableChargingRuleBaseNames(removableChargingRuleBaseNames);
    }

    private void addInstallableAndRemovableCRBN(PCRFResponse response, List<ChargingRuleBaseName> selectedChargingRuleBaseNames, Map<String, String> alreadyActiveChargingRulesIdToSubscriptions, Map<String, String> allChargingRuleIdToSubscriptionOrPackageIdMap, String subscriberIdentity, List<String> removableChargingRuleBaseNames) {
        /*
         * Here we create installable and removable CHARGING_RULE_BASE_NAME.
         *
         * Rule 1 : Removable CHARGING_RULE_BASE_NAME should be (already installed CHARGING_RULE_BASE_NAMES -
         * selected CHARGING_RULE_BASE_NAMES)
         *
         * Rule 2 : Installable CHARGING RULE BASE NAME should be
         * (selected CHARGING_RULE_BASE_NAMES - already installed CHARGING_RULE_BASE_NAMES)
         *
         *
         * first we add all selected CHARGING_RULE_BASE_NAMES in installable CHARGING_RULE_BASE_NAMES.
         *
         * As we add all selected CHARGING_RULE_BASE_NAMES in installable CHARGING_RULE_BASE_NAMES, Now we
         * need to only take care for already Installed CHARGING_RULE_BASE_NAMES. We
         * need to remove CHARGING_RULE_BASE_NAME from installable CHARGING_RULE_BASE_NAMES if we found
         * already installed CHARGING_RULE_BASE_NAME in selected again. If already
         * installed CHARGING_RULE_BASE_NAME not in selected then we need to add CHARGING_RULE_BASE_NAME in
         * removable CHARGING_RULE_BASE_NAMES
         *
         *
         * procedure:
         *
         * for each already active CHARGING_RULE_BASE_NAME,
         *
         * check any CHARGING_RULE_BASE_NAME is selected for same service
         *
         * If CHARGING_RULE_BASE_NAME not select then no CHARGING_RULE_BASE_NAME satisfied for service
         * and need to move that CHARGING_RULE_BASE_NAMES into removable CHARGING_RULE_BASE_NAMES.
         * selected rule else if same CHARGING_RULE_BASE_NAME and subscription is
         * selected then we need to remove from installable CHARGING_RULE_BASE_NAMES as it is
         * already installed then there is no meaning for installing it
         * again else as same is not selected we need remove the rule so
         * move that CHARGING_RULE_BASE_NAME into removable rule
         */

        List<ChargingRuleBaseName> installableChargingRuleBaseNames = new ArrayList<ChargingRuleBaseName>(selectedChargingRuleBaseNames);
        for (Map.Entry<String, String> chargingRuleBaseNameEntry : alreadyActiveChargingRulesIdToSubscriptions.entrySet()) {

            String alreadyActiveChargingRuleBaseNameId = chargingRuleBaseNameEntry.getKey();

            ChargingRuleBaseName alreadyActiveChargingRuleBaseName = serverContext.getPolicyRepository().getChargingRuleBaseNameById(alreadyActiveChargingRuleBaseNameId);
            if (alreadyActiveChargingRuleBaseName == null) {
                getLogger().warn(MODULE, "Skipping ChargingRuleBaseName with id:" + alreadyActiveChargingRuleBaseNameId
                        + " from installed rules for subscriber ID: " + subscriberIdentity
                        + ". Reason: ChargingRuleBaseName not found from repository");
                continue;
            }


            ChargingRuleBaseName selectedChargingRuleBaseName = null;

            for (ChargingRuleBaseName chargingRuleBaseName : selectedChargingRuleBaseNames) {
                if (alreadyActiveChargingRuleBaseName.getId().equalsIgnoreCase(chargingRuleBaseName.getId())) {
                    selectedChargingRuleBaseName = chargingRuleBaseName;
                    break;
                }
            }

            if (selectedChargingRuleBaseName == null) {
                removableChargingRuleBaseNames.add(alreadyActiveChargingRuleBaseName.getName());
            } else {

                String subscriptionId = chargingRuleBaseNameEntry.getValue();
                if (selectedChargingRuleBaseName.getId().equalsIgnoreCase(alreadyActiveChargingRuleBaseNameId)) {

                    /*
                     * NO NEED TO INSTALL CHARGINGRULEBASENAME FOR BELOW CASES:
                     *
                     *
                     * - if subscriptionId is null for previous active
                     * CHARGINGRULEBASENAME.
                     *
                     * - if previous active CHARGINGRULEBASENAME and current active CHARGINGRULEBASENAME
                     * from same subscription.
                     */
                    if (subscriptionId == null || allChargingRuleIdToSubscriptionOrPackageIdMap.get(selectedChargingRuleBaseName.getId()).equals(subscriptionId)) {
                        // NOTE: remove CHARGING_RULE_BASE_NAME by using already exist CHARGING_RULE_BASE_NAMES
                        // reference only
                        installableChargingRuleBaseNames.remove(selectedChargingRuleBaseName);
                    }
                } else {
                    removableChargingRuleBaseNames.add(alreadyActiveChargingRuleBaseName.getName());
                }
            }
        }

        response.setInstallableChargingRuleBaseNames(installableChargingRuleBaseNames);
    }

}
