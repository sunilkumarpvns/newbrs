package com.elitecore.corenetvertex.spr.data;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

@XmlRootElement(name = "subscriber-info")
public class SubscriberInfo {

	private SPRInfo sprInfo;
	private List<SubscriptionDetail> addOnSubscriptionsDetails;
	private List<SubscriberUsage> basePackageUsages;
	private List<NonMonetoryBalance> basePackageNonMonetoryBalances;
	private List<RnCNonMonetaryBalance> basePackageRnCNonMonetaryBalances;
	private List<MonetaryBalance> monetaryBalances;

	public SubscriberInfo() {
		// Used in JAXB
	}
	public SubscriberInfo(SPRInfo sprInfo, List<SubscriptionDetail> addOnSubscriptionDetails, List<SubscriberUsage> basePackageUsages) {

		this.sprInfo = sprInfo;
		this.addOnSubscriptionsDetails = addOnSubscriptionDetails;
		this.basePackageUsages = basePackageUsages;
	}

	@XmlElement(name = "spr-info")
	public SPRInfo getSprInfo() {
		return sprInfo;
	}

	public void setSprInfo(SPRInfo sprInfo) {
		this.sprInfo = sprInfo;
	}

	@XmlElement(name = "addon-subscription-detail")
	public List<SubscriptionDetail> getAddOnSubscriptionsDetails() {
		return addOnSubscriptionsDetails;
	}
	
	public void setAddOnSubscriptionsDetails(List<SubscriptionDetail> addOnSubscriptionsDetails) {
		this.addOnSubscriptionsDetails = addOnSubscriptionsDetails;
	}
	
	@XmlElement(name = "base-package-usage")
	public List<SubscriberUsage> getBasePackageUsages() {
		return basePackageUsages;
	}

	public void setBasePackageUsages(List<SubscriberUsage> basePackageUsages) {
		this.basePackageUsages = basePackageUsages;
	}

	@XmlTransient
	public List<NonMonetoryBalance> getBasePackageNonMonetoryBalances() {
		return basePackageNonMonetoryBalances;
	}

	public void setBasePackageNonMonetoryBalances(List<NonMonetoryBalance> basePackageNonMonetoryBalances) {
		this.basePackageNonMonetoryBalances = basePackageNonMonetoryBalances;
	}

	@XmlTransient
	public List<RnCNonMonetaryBalance> getBasePackageRnCNonMonetaryBalances() {
		return basePackageRnCNonMonetaryBalances;
	}

	public void setBasePackageRnCNonMonetaryBalances(List<RnCNonMonetaryBalance> basePackageRnCNonMonetaryBalances) {
		this.basePackageRnCNonMonetaryBalances = basePackageRnCNonMonetaryBalances;
	}

	@XmlTransient
	public List<MonetaryBalance> getMonetaryBalances() {
		return monetaryBalances;
	}

	public void setMonetaryBalances(List<MonetaryBalance> monetaryBalances) {
		this.monetaryBalances = monetaryBalances;
	}

	@Override
	public String toString() {
		return "SubscriberInfo [sprInfo=" + sprInfo + ", addOnSubscriptionsDetails=" + addOnSubscriptionsDetails + ", basePackageUsages="
				+ basePackageUsages + "]";
	}

}
