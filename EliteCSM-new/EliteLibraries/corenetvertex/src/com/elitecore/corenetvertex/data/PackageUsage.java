package com.elitecore.corenetvertex.data;

import javax.annotation.Nullable;

import com.elitecore.corenetvertex.spr.balance.Usage;

import java.io.Serializable;

public class PackageUsage implements Cloneable,Serializable{

	private String packageId;
	private String subscriptionId;
	private String quotaProfileId;
	private String serviceId;
	private Usage usage;
	
	public PackageUsage(String packageId, @Nullable String subscriptionId,
			String quotaProfileId, String serviceId, Usage usage) {
		this.packageId = packageId;
		this.subscriptionId = subscriptionId;
		this.quotaProfileId = quotaProfileId;
		this.serviceId = serviceId;
		this.usage = usage;
	}

	public String getPackageId() {
		return packageId;
	}
	
	public String getQuotaProfileId() {
		return quotaProfileId;
	}
	
	public String getSubscriptionId() {
		return subscriptionId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public Usage getUsage() {
		return usage;
	}
	
	@Override
	public PackageUsage clone() throws CloneNotSupportedException {
		PackageUsage newPackageUsage = (PackageUsage) super.clone();
		newPackageUsage.usage = (Usage) usage.clone();
		return newPackageUsage;
	}
}
