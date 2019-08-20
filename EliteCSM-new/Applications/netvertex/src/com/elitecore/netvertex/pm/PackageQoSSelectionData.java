package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageQoSSelectionData {
	
	private UserPackage userPackage;
	private PackageType packageType;
	private String subscriptionIdOrPkgId;
	@Nullable private Map<Long,PCCRule> pccRules;
	@Nullable private QoSProfileDetail qosProfileDetail;
	@Nullable private TotalBalance qosBalance;
	@Nullable private List<ChargingRuleBaseName> chargingRuleBaseNames;
	private QoSProfile pccProfile;

	public PackageQoSSelectionData(UserPackage packageData, String subscriptionId) {
		this(packageData);
		this.subscriptionIdOrPkgId = subscriptionId;
	}

	public PackageQoSSelectionData(UserPackage pkgData) {
		this.userPackage = pkgData;
		this.subscriptionIdOrPkgId = pkgData.getId();
		setPackageType(pkgData);
	}

	private void setPackageType(UserPackage pkgData) {
        switch (pkgData.getPackageType()) {

            case BASE:
                this.packageType = PackageType.BASE;
                break;
            case ADDON:
				getPackageType((AddOn) pkgData);
				break;
            case EMERGENCY:
                this.packageType = PackageType.EMERGENCY;
                break;
            case PROMOTIONAL:
				getPackageType((PromotionalPackage) pkgData);
				break;
        }
    }

	private void getPackageType(AddOn pkgData) {
		if (pkgData.isExclusive()) {
            this.packageType = PackageType.EXCLUSIVEADDON;
        } else {
            this.packageType = PackageType.NONEXCLUSIVEADDON;
        }
	}

	private void getPackageType(PromotionalPackage pkgData) {
		if (pkgData.isPreferPromotionalQoS()) {
            this.packageType = PackageType.PREFERQOSPROMOTION;
        } else {
            this.packageType = PackageType.PROMOTION;
        }
	}

	public PackageType getPackageType() {
		return packageType;
	}
	
	public Map<Long, PCCRule> getPccRules() {
		return pccRules;
	}
	
	public String getSubscriptionIdOrPkgId() {
		return subscriptionIdOrPkgId;
	}
	
	public UserPackage getUserPackage() {
		return userPackage;
	}
	
	public void setPCCRules(List<PCCRule> pccRules) {
		if(Collectionz.isNullOrEmpty(pccRules)){
			return;
		}
		
		if(this.pccRules == null) {
			this.pccRules = new HashMap<>(pccRules.size());
		}
		
		for(int i=0; i < pccRules.size(); i++) {
			add(pccRules.get(i));
		}
 		
	}
	
	public QoSProfileDetail getQosProfileDetail() {
		return qosProfileDetail;
	}


	public void setQosProfileDetail(QoSProfileDetail qosProfileDetail) {
		if(this.qosProfileDetail == null ) {
			this.qosProfileDetail = qosProfileDetail;
		}
	}
	
	public void add(PCCRule pccRule) {
		if(this.pccRules == null) {
			this.pccRules = new HashMap<>();
		}
		
		/*
		 * Put PCC if absent in already selected PCCRules.
		 * 
		 * This check is put because if HSQ qos detail is partially applied, 
		 * We should select select only specific service from HSQ that is not satisfied in HSQ
		 * 
		 * If service is selected in HSQ then always HSQ service will be applied irrespective of higher speed in FUP.
		 */
		if(this.pccRules.get(pccRule.getServiceIdentifier()) == null) {			
			this.pccRules.put(pccRule.getServiceIdentifier(), pccRule);
		}
	}

	public void reset(UserPackage userPackage) {
		this.userPackage = userPackage;
		setPackageType(userPackage);
		if (pccRules != null) {
			pccRules.clear();
		}

		if(chargingRuleBaseNames != null){
			chargingRuleBaseNames.clear();
		}

		qosProfileDetail = null;
		qosBalance = null;
		if (userPackage != null) {
			subscriptionIdOrPkgId = userPackage.getId();
		}
		
	}
	
	public void reset(UserPackage userPackage, String subscriptionIdOrPkgId) {
		reset(userPackage);
		this.subscriptionIdOrPkgId = subscriptionIdOrPkgId;
	}
	
	public void setQoSBalance(TotalBalance qosbalance) {
		this.qosBalance = qosbalance;
}

	public TotalBalance getQoSBalance() {
		return qosBalance;
	}

	public List<ChargingRuleBaseName> getChargingRuleBaseNames(){

		return chargingRuleBaseNames;
	}

	public void setChargingRuleBaseNames(List<ChargingRuleBaseName> chargingRuleBaseNames){
		if(Collectionz.isNullOrEmpty(chargingRuleBaseNames)){
			return;
		}

		if(this.chargingRuleBaseNames == null) {
			this.chargingRuleBaseNames= new ArrayList<ChargingRuleBaseName>(chargingRuleBaseNames.size());
		}

		for(int i=0; i < chargingRuleBaseNames.size(); i++) {
			add(chargingRuleBaseNames.get(i));
		}
	}

	public void add(ChargingRuleBaseName chargingRuleBaseName) {
		if( chargingRuleBaseName == null ){
			return;
		}

		if(this.chargingRuleBaseNames == null) {
			this.chargingRuleBaseNames = new ArrayList<ChargingRuleBaseName>();
		}
		this.chargingRuleBaseNames.add(chargingRuleBaseName);
	}

	public void setPccProfile(QoSProfile pccprofile) {
		if(pccprofile != null) {
			this.pccProfile = pccprofile;
		}
	}

	public QoSProfile getQoSProfile() {
		return pccProfile;
	}

	public void removePCCProfileIfNotSelected() {
		if(qosProfileDetail == null) {
			pccProfile = null;
		}
	}
}
