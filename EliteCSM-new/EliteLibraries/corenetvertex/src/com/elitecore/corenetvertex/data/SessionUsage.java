package com.elitecore.corenetvertex.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.balance.Usage;
/**
 * Keeps total usage of session and subscription wise individual usage
 * 
 * @author Chetan.Sankhala
 */
public class SessionUsage implements Cloneable,Serializable {

	/**
	 * subscriptionId wise package usage
	 */
	private Map<String, Map<String, PackageUsage>> subscriptionWiseUsage;

	public SessionUsage() {
		this.subscriptionWiseUsage = new HashMap<String, Map<String, PackageUsage>>();
	}
	
	public SessionUsage(Map<String, Map<String, PackageUsage>> subscriptionWiseUsage) {
		this.subscriptionWiseUsage = subscriptionWiseUsage;
	}

	public Usage getTotalUsage() {
		return calculateTotalUsage();
	}

	public void setPackageUsage(@Nullable Map<String, Map<String, PackageUsage>> subscriptionWiseUsage) {
		if (subscriptionWiseUsage == null) {
			return;
		}
		this.subscriptionWiseUsage = subscriptionWiseUsage;
	}

	public void setPackageUsage(String subscriptionOrPackageId, @Nullable Map<String, PackageUsage> quotaProfileServiceWisePackageUsage) {
		if(quotaProfileServiceWisePackageUsage == null) {
			return;
		}
		
		this.subscriptionWiseUsage.put(subscriptionOrPackageId, quotaProfileServiceWisePackageUsage);
	}

	private Usage calculateTotalUsage() {

		long uploadOctets = 0;
		long downloadOctets = 0;
		long time = 0;
		long totalOctets = 0;
		
		for (Entry<String, Map<String, PackageUsage>> entrySubscriptionWiseUsage : subscriptionWiseUsage
				.entrySet()) {
			
			for (Entry<String, PackageUsage> entrySubscriptionUsage : entrySubscriptionWiseUsage
					.getValue().entrySet()) {

				PackageUsage packageUsage = entrySubscriptionUsage.getValue();
				
				// Total only ALL SERVICE usage
				if (CommonConstants.ALL_SERVICE_ID.equals(packageUsage.getServiceId()) == false) {
					continue;
				}
				
				Usage usage = packageUsage.getUsage();
				totalOctets += usage.getTotalOctets();
				downloadOctets += usage.getDownloadOctets();
				uploadOctets += usage.getUploadOctets();
				time += usage.getTime();
			}
		}
		
		return new Usage(uploadOctets, downloadOctets, totalOctets, time);
	}
	
	public Map<String, PackageUsage> getPackageUsage(String packageOrSubscriptionId) {
		return subscriptionWiseUsage.get(packageOrSubscriptionId);
	}
	
	public Set<Entry<String, Map<String, PackageUsage>>> getAllSubscriptionWiseUsage() {
		return subscriptionWiseUsage.entrySet();
	}
	
	@Override
	public SessionUsage clone() throws CloneNotSupportedException {
		
		SessionUsage clonedSessionUsage = (SessionUsage) super.clone();
		
		Map<String, Map<String, PackageUsage>> clonedSubscriptionUsage = new HashMap<String, Map<String,PackageUsage>>();
		for (Entry<String, Map<String, PackageUsage>> entrySubscriptionUsage : subscriptionWiseUsage.entrySet()) {
			Map<String, PackageUsage> clonedQuotaProfileServiceWisePackageUsage = new HashMap<String, PackageUsage>();
			
			for (Entry<String, PackageUsage> entryQuotaProfileServiceWisePackageUsage : entrySubscriptionUsage.getValue().entrySet()) {
				clonedQuotaProfileServiceWisePackageUsage.put(entryQuotaProfileServiceWisePackageUsage.getKey(), (PackageUsage) entryQuotaProfileServiceWisePackageUsage.getValue().clone());
			}
			
			clonedSubscriptionUsage.put(entrySubscriptionUsage.getKey(), clonedQuotaProfileServiceWisePackageUsage);
		}

		clonedSessionUsage.subscriptionWiseUsage = clonedSubscriptionUsage;
		
		return clonedSessionUsage;
	}
}
