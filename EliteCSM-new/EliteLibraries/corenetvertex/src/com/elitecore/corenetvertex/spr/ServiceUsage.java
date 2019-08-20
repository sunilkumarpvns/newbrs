package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServiceUsage implements Cloneable {

	@Nonnull private Map<String, Map<String, SubscriberUsage>> subscriptionWiseUsage;
	
	public ServiceUsage() {
		this.subscriptionWiseUsage = new HashMap<String, Map<String, SubscriberUsage>>();
	}

	public ServiceUsage(Map<String, Map<String, SubscriberUsage>> subscriprionWiseUsage) {
		if (subscriprionWiseUsage == null) {
			this.subscriptionWiseUsage = new HashMap<String, Map<String,SubscriberUsage>>();
		} else  {
			this.subscriptionWiseUsage = subscriprionWiseUsage;
		}
	}
	
	public Map<String, SubscriberUsage> getPackageUsage(String packageOrSubscriptionId) {
		return subscriptionWiseUsage.get(packageOrSubscriptionId);
	}

	public void setSubscriptionWiseUsage(String packageOrSubscriptionId, @Nullable Map<String, SubscriberUsage> quotaProfileServiceWiseUsage) {
		if (quotaProfileServiceWiseUsage == null) {
			return;
		}
		
		this.subscriptionWiseUsage.put(packageOrSubscriptionId, quotaProfileServiceWiseUsage);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ServiceUsage clonedServiceUsage = (ServiceUsage) super.clone();
		
		Map<String, Map<String, SubscriberUsage>> clonedSubscriptionWiseUsage = new HashMap<String, Map<String,SubscriberUsage>>();
		for (Entry<String, Map<String, SubscriberUsage>> entrySubscriptionWiseUsage: subscriptionWiseUsage.entrySet()) {
			Map<String, SubscriberUsage> clonedQuotaProfileServiceWiseUsage = new HashMap<String, SubscriberUsage>();
			
			for (Entry<String, SubscriberUsage> entryQuotaProfileWiseUsage : entrySubscriptionWiseUsage.getValue().entrySet()) {
				clonedQuotaProfileServiceWiseUsage.put(entryQuotaProfileWiseUsage.getKey(), (SubscriberUsage) entryQuotaProfileWiseUsage.getValue().clone());
			}
			
			clonedSubscriptionWiseUsage.put(entrySubscriptionWiseUsage.getKey(), clonedQuotaProfileServiceWiseUsage);
		}
		
		clonedServiceUsage.subscriptionWiseUsage = clonedSubscriptionWiseUsage;
		
		return clonedServiceUsage;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
		toString(out);
		out.close();
		return stringWriter.toString();
	}

	public void toString(IndentingWriter stringWriter) {

		if (Maps.isNullOrEmpty(this.subscriptionWiseUsage)) {
			stringWriter.println("No usage found");
		} else {

			for(Map<String, SubscriberUsage> packageUsage : subscriptionWiseUsage.values()) {
				for(SubscriberUsage subscriberUsage : packageUsage.values()) {
					subscriberUsage.toString(stringWriter);
				}
			}
		}
	}
}
