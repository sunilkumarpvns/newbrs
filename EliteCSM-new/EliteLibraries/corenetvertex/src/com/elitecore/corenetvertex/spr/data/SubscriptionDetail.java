package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
public class SubscriptionDetail {
	
	private Subscription addOnSubscription;
	private List<SubscriberUsage> usages;
	private List<NonMonetoryBalance> nonMonetoryBalances;
	private List<RnCNonMonetaryBalance> rncNonMonetaryBalances;
	
	public SubscriptionDetail() {
		// Used in JAXB
	}
	public SubscriptionDetail(Subscription addOnSubscription, List<SubscriberUsage> usages) {

		this.addOnSubscription = addOnSubscription;
		this.usages = usages;
	}

	@XmlElement(name = "addon-subscription")
	public Subscription getAddOnSubscription() {
		return addOnSubscription;
	}

	public void setAddOnSubscription(Subscription addOnSubscription) {
		this.addOnSubscription = addOnSubscription;
	}

	@XmlElement(name = "subscription-usage")
	public List<SubscriberUsage> getUsages() {
		return usages;
	}

	public void setUsages(List<SubscriberUsage> usages) {
		this.usages = usages;
	}


	@XmlTransient
	public List<NonMonetoryBalance> getNonMonetoryBalances() {
		return nonMonetoryBalances;
	}

	public void setNonMonetoryBalances(List<NonMonetoryBalance> nonMonetoryBalances) {
		this.nonMonetoryBalances = nonMonetoryBalances;
	}

	@XmlTransient
	public List<RnCNonMonetaryBalance> getRnCNonMonetoryBalances() {
		return rncNonMonetaryBalances;
	}

	public void setRnCNonMonetoryBalances(List<RnCNonMonetaryBalance> rncNonMonetaryBalances) {
		this.rncNonMonetaryBalances = rncNonMonetaryBalances;
	}
	
	@Override
	public String toString() {
		return "AddOnSubscriptionDetail [addOnSubscription=" + addOnSubscription + ", usages=" + usages + "]";
	}


}
