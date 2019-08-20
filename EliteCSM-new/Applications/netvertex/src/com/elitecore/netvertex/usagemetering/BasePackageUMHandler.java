package com.elitecore.netvertex.usagemetering;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PackageUsage;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class BasePackageUMHandler extends PackageUMHandler {

	static final String MODULE = "BASE-PKG-UM-HDLR";
	@Nullable private ServiceUsage currentServiceUsage;
	@Nullable private SessionUsage currentSessionUsage;
	private String subscriberIdentity;
	private Map<String, SubscriberUsage> insertList;
	private Map<String, SubscriberUsage> updateList;
	private String productOfferId;
	
	public BasePackageUMHandler(
			String subscriberIdentity,
			Package subscriberPackage,
			String productOfferId,
			ExecutionContext executionContext,
			@Nullable ServiceUsage currentServiceUsage,
			@Nullable SessionUsage currentSessionUsage) {
		super(subscriberPackage,  executionContext);
		
		this.updateList = new HashMap<String, SubscriberUsage>();
		this.insertList = new HashMap<String, SubscriberUsage>();
		this.currentServiceUsage = currentServiceUsage;
		this.subscriberIdentity = subscriberIdentity;
		this.currentSessionUsage = currentSessionUsage;
		this.productOfferId = productOfferId;
	}
	
	@Override
	protected SubscriberUsage getUsageFromDBUpdateList(String quotaProfileId, String serviceId) {
		
		String usageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
		SubscriberUsage subscriberUsage = updateList.get(usageKey);
		if (subscriberUsage == null) {
			subscriberUsage = insertList.get(usageKey);

			if (subscriberUsage == null) {
				subscriberUsage = getCurrentServiceUsage().get(usageKey);
				
				if (subscriberUsage == null) {
					subscriberUsage = new SubscriberUsage(SubscriberUsage.NEW_ID, quotaProfileId, subscriberIdentity, serviceId, null, getPackageId(),productOfferId);
					subscriberUsage.setBillingCycleResetTime(calculateBillingCycleResetTime(quotaProfileId));
					insertList.put(usageKey, subscriberUsage);
				} else {
					subscriberUsage = new SubscriberUsage(subscriberUsage.getId(), quotaProfileId, subscriberIdentity, serviceId, null, getPackageId(),subscriberUsage.getProductOfferId());
					updateList.put(usageKey, subscriberUsage);
				}
			}
		}
		
		return subscriberUsage;
	}

	private long calculateBillingCycleResetTime(String quotaProfileId) {
		if(getPackage()!=null){
			QuotaProfile quotaProfile = getPackage().getQuotaProfile(quotaProfileId);

			if(quotaProfile!=null && quotaProfile.getRenewalInterval()>0 && quotaProfile.getRenewalIntervalUnit()!=null){
				return quotaProfile.getRenewalIntervalUnit()
						.addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval());
			}
		}
		return 0l;
	}

	@Override
	protected SubscriberUsage getUsageFromCurrentServiceUsage(String quotaProfileId, String serviceId) {
		
		Map<String, SubscriberUsage> quotaProfileServiceWiseUsage = getCurrentServiceUsage();
		
		String usageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
		
		SubscriberUsage subscriberUsage = quotaProfileServiceWiseUsage.get(usageKey);
		
		if (subscriberUsage == null) {
			subscriberUsage = new SubscriberUsage(SubscriberUsage.NEW_ID,
					quotaProfileId,
					subscriberIdentity,
					serviceId,
					null,
					getPackageId(),productOfferId);
			quotaProfileServiceWiseUsage.put(usageKey, subscriberUsage);
		}

		return subscriberUsage;
	}

	@Override
	protected @Nullable Map<String, PackageUsage> getCurrentSessionUsage() {
		if(this.currentSessionUsage == null) {
			return null;
		}
		
		Map<String, PackageUsage> quotaProfileServiceWiseSessionUsage = currentSessionUsage.getPackageUsage(getPackageId());
		
		if (quotaProfileServiceWiseSessionUsage == null) {
			quotaProfileServiceWiseSessionUsage = new HashMap<String, PackageUsage>();
			currentSessionUsage.setPackageUsage(getPackageId(), quotaProfileServiceWiseSessionUsage);
		}
		
		return quotaProfileServiceWiseSessionUsage;
	}

	@Override
	protected @Nullable Map<String, SubscriberUsage> getCurrentServiceUsage() {
		
		if (this.currentServiceUsage == null) {
			return null;
		}
		
		Map<String, SubscriberUsage> packageUsage = currentServiceUsage.getPackageUsage(getPackageId());
		
		if (packageUsage == null) {
			packageUsage = new HashMap<String, SubscriberUsage>();
			currentServiceUsage.setSubscriptionWiseUsage(getPackageId(), packageUsage);
		}
		
		return packageUsage;
	}
	
	@Override
	protected boolean isPreviousBillingCycleUsage(Calendar currentTime, SubscriberUsage subscriberPreviousUsage) {
		return currentTime.getTimeInMillis() >= subscriberPreviousUsage.getBillingCycleResetTime();
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
	public Collection<SubscriberUsage> getInsertList() {
		return insertList.values();
	}
	
	@Override
	protected long calculateBillingCycleTime(Calendar calendar, SubscriberUsage subscriberUsage) {

		if(isPreviousBillingCycleUsage()==false){
			return 0;
		}

		QuotaProfile quotaProfile = getPackage().getQuotaProfile(subscriberUsage.getQuotaProfileId());

		long resetTimeInMillies = subscriberUsage.getBillingCycleResetTime();
		if(quotaProfile!=null && quotaProfile.getRenewalInterval()>0 && quotaProfile.getRenewalIntervalUnit()!=null){
			do{
				resetTimeInMillies = quotaProfile.getRenewalIntervalUnit().addTime(resetTimeInMillies, quotaProfile.getRenewalInterval());
			}while (resetTimeInMillies<System.currentTimeMillis());

			return resetTimeInMillies;

		} else {
			return CommonConstants.FUTURE_DATE;
		}
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
