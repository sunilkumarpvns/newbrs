package com.elitecore.netvertex.usagemetering;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PackageUsage;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class SubscriptionUMHandler extends PackageUMHandler{

	@Nullable private ServiceUsage currentServiceUsage;
	@Nullable private SessionUsage currentSessionUsage;
	private Map<String, SubscriberUsage> updateList;
	private Subscription subscription;
	
	public SubscriptionUMHandler(
			Subscription addOnSubscription,
			UserPackage subscribedPackage,
			ExecutionContext executionContext,
			@Nullable ServiceUsage currentServiceUsage,
			@Nullable SessionUsage currentSessionUsage) {
		super(subscribedPackage,  executionContext);
		this.subscription = addOnSubscription;
		this.updateList = new HashMap<String, SubscriberUsage>();
		this.currentServiceUsage = currentServiceUsage;
		this.currentSessionUsage = currentSessionUsage;
	}
	
	
	@Override
	protected long calculateBillingCycleTime(Calendar calendar, SubscriberUsage subscriberUsage) {
		
		if (isPreviousBillingCycleUsage() == false) {
			return 0;
		}
		
		SubscriptionPackage subscriptionPackage = (SubscriptionPackage)getPackage();

		long resetTimeInMillies = subscriberUsage.getBillingCycleResetTime();
		long endTimeInMillies = subscription.getEndTime().getTime();

		QuotaProfile quotaProfile = subscriptionPackage.getQuotaProfile(subscriberUsage.getQuotaProfileId());

		if(quotaProfile!=null && quotaProfile.getRenewalInterval()>0 && quotaProfile.getRenewalIntervalUnit()!=null){

			do{
				resetTimeInMillies = quotaProfile.getRenewalIntervalUnit().addTime(resetTimeInMillies, quotaProfile.getRenewalInterval());
			}while (resetTimeInMillies<System.currentTimeMillis());

			if (endTimeInMillies < resetTimeInMillies) {
				resetTimeInMillies = endTimeInMillies;
			}
		} else {
			resetTimeInMillies = endTimeInMillies;
		}

		return resetTimeInMillies;
	}
	
	@Override
	protected SubscriberUsage getUsageFromDBUpdateList(String quotaProfileId, String serviceId) {
		String usageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
		SubscriberUsage subscriberUsage = updateList.get(usageKey);
		
		if (subscriberUsage == null) {
			Map<String, SubscriberUsage> quotaProfileServiceWiseServiceUsage = getCurrentServiceUsage();

			if (quotaProfileServiceWiseServiceUsage == null) {
				return null;
			}

			subscriberUsage = quotaProfileServiceWiseServiceUsage.get(usageKey);

			if (subscriberUsage == null) {
				return null;
			}

			subscriberUsage = new SubscriberUsage(subscriberUsage.getId(), quotaProfileId,
					subscriberUsage.getSubscriberIdentity(), serviceId, subscription.getId(), getPackageId(),subscriberUsage.getProductOfferId());

			updateList.put(usageKey, subscriberUsage);

		}

		return subscriberUsage;
	}


	@Override
	protected SubscriberUsage getUsageFromCurrentServiceUsage(String quotaProfileId, String serviceId) {
		
		String usageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
		
		Map<String, SubscriberUsage> quotaProfileAndServiceWiseCurrentUsages = getCurrentServiceUsage();
		
		if (quotaProfileAndServiceWiseCurrentUsages == null) {
			return null;
		}
		
		return quotaProfileAndServiceWiseCurrentUsages.get(usageKey);
	}
	
	@Override
	protected @Nullable Map<String, PackageUsage> getCurrentSessionUsage() {
		if(this.currentSessionUsage == null) {
			return null;
		}
		
		Map<String, PackageUsage> quotaProfileServiceWiseSessionUsage = currentSessionUsage.getPackageUsage(subscription.getId());
		
		if (quotaProfileServiceWiseSessionUsage == null) {
			quotaProfileServiceWiseSessionUsage = new HashMap<String, PackageUsage>();
			currentSessionUsage.setPackageUsage(subscription.getId(), quotaProfileServiceWiseSessionUsage);
		}
		
		return quotaProfileServiceWiseSessionUsage;
	}

	@Override
	protected @Nullable Map<String, SubscriberUsage> getCurrentServiceUsage() {
		if (this.currentServiceUsage == null) {
			return null;
		}
		
		return currentServiceUsage.getPackageUsage(subscription.getId());
	}


	@Override
	protected void addUsageInUpdateList(String key, SubscriberUsage subscriberUsage) {
		updateList.put(key, subscriberUsage);
	}

	@Override
	public Collection<SubscriberUsage> getUpdateList() {
		return updateList.values();
	}


	@Override
	public @Nullable Collection<SubscriberUsage> getInsertList() {
		return null;
	}


	@Override
	protected boolean isServiceUsageReportingRequired() {
		return currentServiceUsage != null;
	}


	@Override
	protected boolean isSessionUsageReportingRequired() {
		return currentSessionUsage != null;
	}
}
