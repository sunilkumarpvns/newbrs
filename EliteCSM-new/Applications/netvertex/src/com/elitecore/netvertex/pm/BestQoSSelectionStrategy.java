package com.elitecore.netvertex.pm;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQosBasedComparator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.elitecore.commons.logging.LogManager.getLogger;


/**
 * 
 */
public class BestQoSSelectionStrategy implements QoSSelectionWithinGroupStrategy, QoSSelectionAcrossGroupStrategy{

	private static final BestQoSSelectionStrategy BASE_PACKAGE_STRATEGY = new BestQoSSelectionStrategy();
	private static final IPCANQosBasedComparator IPCAN_QOS_BASED_COMPARATOR = new IPCANQosBasedComparator();
	private static final String MODULE = "BEST-QOS-STRATEGY";

	public static boolean electSessionQoS(QoSProfileDetail currentQosProfileDetail, QoSProfileDetail finalQoSProfileDetail) {


		String fupLevel = currentQosProfileDetail.getFUPLevel() == 0 ? "HSQ" : ("FUP" + currentQosProfileDetail.getFUPLevel());
		if(currentQosProfileDetail.getAction() == QoSProfileAction.REJECT) {
			if(finalQoSProfileDetail != null && finalQoSProfileDetail.getAction() != QoSProfileAction.REJECT) {
				return false;
			} else {				
				LogManager.getLogger().debug(MODULE, "Applying QoS from QoS Profile: " + currentQosProfileDetail.getName() + ", Level: " + fupLevel);
				return true;
			}
		} else {
			
			if(finalQoSProfileDetail == null || finalQoSProfileDetail.getAction() == QoSProfileAction.REJECT) {
				LogManager.getLogger().debug(MODULE, "Applying QoS from QoS Profile: " + currentQosProfileDetail.getName() + ", Level: " + fupLevel);
				return true;
			}  else {
				
				if (finalQoSProfileDetail.getSessionQoS() != null && currentQosProfileDetail.getSessionQoS() != null) {
    				int result = IPCAN_QOS_BASED_COMPARATOR.compare(finalQoSProfileDetail.getSessionQoS(),currentQosProfileDetail.getSessionQoS());
    				if (result <= 0 ) {
    					LogManager.getLogger().debug(MODULE, "Applying QoS from QoS Profile: " + currentQosProfileDetail.getName() + ", Level: " + fupLevel);
    					return true;
    				}
				}
				return false;
			
			}	
		}
	
	}
	
	public static void electPccRules(PackageQoSSelectionData currentQoSSelection,
			FinalQoSSelectionData finalQoSSelectionData) {
		Map<Long, PCCRule> finalPccRules = finalQoSSelectionData.getPccRules();
		Map<Long, PCCRule> currentPCCRules = currentQoSSelection.getPccRules();

		if (currentPCCRules == null) {
			return;
		}

		String subscriptionIdPkgId = currentQoSSelection.getSubscriptionIdOrPkgId();

		if (finalPccRules == null) {
			for(java.util.Map.Entry<Long, PCCRule> pccRuleEntry : currentPCCRules.entrySet()) {
				finalQoSSelectionData.add(pccRuleEntry.getValue(), subscriptionIdPkgId, currentQoSSelection.getQoSProfile());
			}
		} else {
			for(java.util.Map.Entry<Long, PCCRule> currentPccRuleEntry : currentPCCRules.entrySet()) {
				PCCRule finalPccRule = finalPccRules.get(currentPccRuleEntry.getKey());

				if(finalPccRule != null) {

					if (finalPccRule.compareTo(currentPccRuleEntry.getValue()) <= 0) {
						finalQoSSelectionData.add(currentPccRuleEntry.getValue(),subscriptionIdPkgId, currentQoSSelection.getQoSProfile());
					}
				} else {
					finalQoSSelectionData.add(currentPccRuleEntry.getValue(), subscriptionIdPkgId, currentQoSSelection.getQoSProfile());
				}
			}
		}
	}
	
	
	
	public static void electPccRules(FinalQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
		Map<Long, PCCRule> finalPccRules = finalQoSSelectionData.getPccRules();
		Map<Long, PCCRule> currentPCCRules = currentQoSSelection.getPccRules();
		
		if(currentPCCRules == null) {
			return;
		}
		 
		if(finalPccRules == null) {
			for(Entry<Long, PCCRule> pccRuleEntry : currentPCCRules.entrySet()) {
				PCCRule pccRule = pccRuleEntry.getValue();
				String packageIdOrSubscriptionId = currentQoSSelection.getPccRuleIdToSubscriptionOrPackageId().get(pccRule.getId());
				QoSProfile qoSProfile = currentQoSSelection.getQoSProfile(pccRule);
				finalQoSSelectionData.add(pccRule, packageIdOrSubscriptionId, qoSProfile);
			}
		} else {
			for(Entry<Long, PCCRule> currentPccRuleEntry : currentPCCRules.entrySet()) {
				PCCRule finalPccRule = finalPccRules.get(currentPccRuleEntry.getKey());


				PCCRule pccRule = currentPccRuleEntry.getValue();
				String packageIdOrSubscriptionId = currentQoSSelection.getPccRuleIdToSubscriptionOrPackageId().get(pccRule.getId());
				QoSProfile qoSProfile = currentQoSSelection.getQoSProfile(pccRule);

				if(finalPccRule != null) {
					if (finalPccRule.compareTo(pccRule) <= 0) {
						finalQoSSelectionData.add(pccRule, packageIdOrSubscriptionId, qoSProfile);
					}
				} else {
					finalQoSSelectionData.add(pccRule, packageIdOrSubscriptionId, qoSProfile);
				}
			}
		}
	}

	public static BestQoSSelectionStrategy instance() {
		return BASE_PACKAGE_STRATEGY;
	}

	@Override
	public void select(FinalQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection started between " + currentQoSSelection.getPackageType() + " and " + finalQoSSelectionData.getPackageType());
		}

		if (electSessionQoS(currentQoSSelection.getQosProfileDetail(), finalQoSSelectionData.getQosProfileDetail())) {
			finalQoSSelectionData.setQosProfileDetail(currentQoSSelection.getQosProfileDetail(), currentQoSSelection
					.getSelectedSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
			finalQoSSelectionData.setQoSBalance(currentQoSSelection.getQoSBalance());
		}

		electPccRules(currentQoSSelection, finalQoSSelectionData);
		electChargingRuleBaseNames(currentQoSSelection, finalQoSSelectionData);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection completed between " + currentQoSSelection.getPackageType() + " and " + finalQoSSelectionData.getPackageType());
		}
	}

	@Override
	public void select(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection started for type " + currentQoSSelection.getPackageType());
		}

		if (electSessionQoS(currentQoSSelection.getQosProfileDetail(), finalQoSSelectionData.getQosProfileDetail())) {
			finalQoSSelectionData.setQosProfileDetail(currentQoSSelection.getQosProfileDetail(), currentQoSSelection.getSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
			finalQoSSelectionData.setQoSBalance(currentQoSSelection.getQoSBalance());
		}

		electPccRules(currentQoSSelection, finalQoSSelectionData);
		electChargingRuleBaseNames(currentQoSSelection, finalQoSSelectionData);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection completed for type " + currentQoSSelection.getPackageType());
		}
	}

	public static void electChargingRuleBaseNames(FinalQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
		List<ChargingRuleBaseName> finalChargingRulesBaseNames = finalQoSSelectionData.getChargingRuleBaseNames();
		List<ChargingRuleBaseName> currentChargingRuleBaseNames = currentQoSSelection.getChargingRuleBaseNames();

		if(currentChargingRuleBaseNames == null) {
			return;
		}
		finalChargingRulesBaseNames.addAll(currentChargingRuleBaseNames);
	}

	public static void electChargingRuleBaseNames(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
		List<ChargingRuleBaseName> finalChargingRulesBaseNames = finalQoSSelectionData.getChargingRuleBaseNames();
		List<ChargingRuleBaseName> currentChargingRuleBaseNames = currentQoSSelection.getChargingRuleBaseNames();

		if(currentChargingRuleBaseNames == null) {
			return;
		}
		finalChargingRulesBaseNames.addAll(currentChargingRuleBaseNames);
	}
}
