package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FinalQoSSelectionData {
	
	private Map<String, String> pccRuleIdToSubscriptionOrPackageId;
	@Nullable private Map<Long,PCCRule> pccRules;
	@Nullable private Map<PCCRule,QoSProfile> pccRuleToQoSProfile;
    @Nullable private Map<ChargingRuleBaseName,QoSProfile> crbnTpQoSProfile;
	@Nullable private QoSProfileDetail qosProfileDetail;
	@Nullable private QoSProfile qoSProfile;
	@Nullable private String subscriptionIdOrPkgId;
	private PackageType packageType;
	private TotalBalance qosBalance;

	private Map<String, String> chargingRuleIdToSubscriptionOrPackageId;
	@Nullable private List<ChargingRuleBaseName> chargingRuleBaseNames;


	public FinalQoSSelectionData(PackageType packageType) {
		this.packageType = packageType;
		pccRuleIdToSubscriptionOrPackageId = new HashMap<>();
		chargingRuleIdToSubscriptionOrPackageId = new HashMap<>();
		pccRuleToQoSProfile = new IdentityHashMap<>();
		crbnTpQoSProfile = new IdentityHashMap<>();
	}
	
	public PackageType getPackageType() {
		return packageType;
	}
	
	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}

	public Map<String, String> getPccRuleIdToSubscriptionOrPackageId() {
		return pccRuleIdToSubscriptionOrPackageId;
	}
	 
	public void setPccRuleIdToSubscriptionOrPackageId(LinkedHashMap<String, String> pccRuleIdToSubscriptionOrPackageId) {
		this.pccRuleIdToSubscriptionOrPackageId = pccRuleIdToSubscriptionOrPackageId;
	}
	
	public Map<Long, PCCRule> getPccRules() {
		return pccRules;
	}
	
	public QoSProfileDetail getQosProfileDetail() {
		return qosProfileDetail;
	}

	public void setQosProfileDetail(QoSProfileDetail qosProfileDetail, String subscritionIdOrPkgId, QoSProfile qoSProfile) {
		this.qosProfileDetail = qosProfileDetail;
		this.subscriptionIdOrPkgId = subscritionIdOrPkgId;
		this.qoSProfile = qoSProfile;
	}
	
	public String getSelectedSubscriptionIdOrPkgId() {
		return subscriptionIdOrPkgId;
	}
	
	public void add(PCCRule pccRule,String subscriptionIdOrPkgId, QoSProfile qoSProfile) {
		if(this.pccRules == null) {
			this.pccRules = new HashMap<>();
		}

		if(Objects.nonNull(pccRules.get(pccRule.getServiceIdentifier()))){
			pccRuleToQoSProfile.remove(pccRules.get(pccRule.getServiceIdentifier()));
		}
		this.pccRules.put(pccRule.getServiceIdentifier(), pccRule);
		this.pccRuleIdToSubscriptionOrPackageId.put(pccRule.getId(), subscriptionIdOrPkgId);
		this.pccRuleToQoSProfile.put(pccRule, qoSProfile);
	}

	public void flush() {
		pccRuleIdToSubscriptionOrPackageId.clear();
		if (pccRules != null) {
			pccRules.clear();
		}

		chargingRuleIdToSubscriptionOrPackageId.clear();
		if(chargingRuleBaseNames != null){
			chargingRuleBaseNames.clear();
		}

		qosProfileDetail = null;
		qosBalance = null;
	}
	
	public void replaceQoS(PackageQoSSelectionData currentQoSSelection) {

		flush();
		setQosProfileDetail(currentQoSSelection.getQosProfileDetail(), currentQoSSelection.getSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
		setQoSBalance(currentQoSSelection.getQoSBalance());
		
		Map<Long, PCCRule> currentPCCRules = currentQoSSelection.getPccRules();
		
		if( currentPCCRules != null ) {
			for (java.util.Map.Entry<Long, PCCRule> pccRuleEntry : currentPCCRules.entrySet()) {
				add(pccRuleEntry.getValue(), currentQoSSelection.getSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
			}
		}

		List<ChargingRuleBaseName> chargingRuleBaseNames = currentQoSSelection.getChargingRuleBaseNames();
		if( chargingRuleBaseNames == null ){
			return ;
		}

		for(ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames){
			add(chargingRuleBaseName, currentQoSSelection.getSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
		}
	}

	public void replaceQoS(FinalQoSSelectionData currentQoSSelection) {

		flush();
		setQosProfileDetail(currentQoSSelection.getQosProfileDetail(), currentQoSSelection.getSelectedSubscriptionIdOrPkgId(), currentQoSSelection.getQoSProfile());
		setQoSBalance(currentQoSSelection.getQoSBalance());
		Map<Long, PCCRule> currentPCCRules = currentQoSSelection.getPccRules();

		if (currentPCCRules != null) {

			for (java.util.Map.Entry<Long, PCCRule> pccRuleEntry : currentPCCRules.entrySet()) {
				PCCRule pccRule = pccRuleEntry.getValue();
				String subscriptionOrPackageId = currentQoSSelection.getPccRuleIdToSubscriptionOrPackageId().get(pccRule.getId());
				QoSProfile qoSProfile = currentQoSSelection.getQoSProfile(pccRule);
				add(pccRule, subscriptionOrPackageId, qoSProfile);
			}
		}

		List<ChargingRuleBaseName> chargingRuleBaseNames = currentQoSSelection.getChargingRuleBaseNames();
		if (chargingRuleBaseNames == null) {
			return;
		}

		for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
			String subscriptionIdOrPackageId = currentQoSSelection.getChargingRuleIdToSubscriptionOrPackageId().get(chargingRuleBaseName.getId());
			add(chargingRuleBaseName, subscriptionIdOrPackageId, currentQoSSelection.getQoSProfile(chargingRuleBaseName));
		}
	}

	public void setQoSBalance(TotalBalance qosBalance) {
		this.qosBalance = qosBalance;
}

	public TotalBalance getQoSBalance() {
		return qosBalance;
	}

	public Map<String, String> getChargingRuleIdToSubscriptionOrPackageId(){
		return chargingRuleIdToSubscriptionOrPackageId;
	}

	public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
		return chargingRuleBaseNames;
	}

	public void add(ChargingRuleBaseName chargingRuleBaseName,String subscriptionIdOrPkgId, QoSProfile qoSProfile) {
		if(this.chargingRuleBaseNames == null) {
			this.chargingRuleBaseNames= new ArrayList<>();
		}
		this.chargingRuleBaseNames.add(chargingRuleBaseName);
		this.chargingRuleIdToSubscriptionOrPackageId.put(chargingRuleBaseName.getId(), subscriptionIdOrPkgId);
		this.crbnTpQoSProfile.put(chargingRuleBaseName, qoSProfile);
	}

	public QoSProfile getQoSProfile(PCCRule pccRule) {
	    return pccRuleToQoSProfile.get(pccRule);
    }

    public QoSProfile getQoSProfile(ChargingRuleBaseName crbn) {
        return crbnTpQoSProfile.get(crbn);
    }

	public QoSProfile getQoSProfile() {
		return qoSProfile;
	}

	public Map<PCCRule, QoSProfile> getPccRuleToQoSProfile() {
		return pccRuleToQoSProfile;
	}
}
