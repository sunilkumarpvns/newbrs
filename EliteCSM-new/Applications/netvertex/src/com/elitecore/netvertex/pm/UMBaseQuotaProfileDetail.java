package com.elitecore.netvertex.pm;

import java.util.Map;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

public class UMBaseQuotaProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail {

	private static final long serialVersionUID = 1L;
	public UMBaseQuotaProfileDetail(String quotaProfileId, String name, String serviceId, int fupLevel, String serviceName,
			Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage) {
		super(quotaProfileId, name,  serviceId, fupLevel, serviceName, aggregationKeyToAllowedUsage);
	}
	
	public TotalBalance getTotalBalance(SubscriberUsage subscriberUsage, PolicyContext policyContext) {
		return new TotalBalance(getDailyAllowedUsage().getBalance(subscriberUsage, policyContext.getCurrentTime()),
								getWeeklyAllowedUsage().getBalance(subscriberUsage, policyContext.getCurrentTime()),
								 getCustomAllowedUsage().getBalance(subscriberUsage, policyContext.getCurrentTime()),
								 getBillingCycleAllowedUsage().getBalance(subscriberUsage, policyContext.getCurrentTime()));
	}
	
	@Override
	public AllowedUsage getDailyAllowedUsage() {
		AllowedUsage allowedUsage = super.getDailyAllowedUsage();
		if(allowedUsage == null) {
			allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
		}
		return allowedUsage; 
	}
	
	@Override
	public AllowedUsage getWeeklyAllowedUsage() {
		AllowedUsage allowedUsage = super.getWeeklyAllowedUsage();
		if(allowedUsage == null) {
			allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
		}
		return allowedUsage;
	}
	
	@Override
	public AllowedUsage getBillingCycleAllowedUsage() {
		AllowedUsage allowedUsage = super.getBillingCycleAllowedUsage();
		if(allowedUsage == null) {
			allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
		}
		return allowedUsage;
	}

	@Override
	public AllowedUsage getCustomAllowedUsage() {
		AllowedUsage allowedUsage = super.getCustomAllowedUsage();
		if(allowedUsage == null) {
			allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
		}
		return allowedUsage;
	}
}
