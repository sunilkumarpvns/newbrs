package com.elitecore.netvertex.usagemetering;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PackageUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.balance.Usage;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

public abstract class PackageUMHandler {
	private static final String MODULE = "PKG-UM-HDLR";
	
	private boolean isPreviousBillingCycleUsage;
	private boolean isPreviousWeekUsage;
	private boolean isPreviousDayUsage;
	private UserPackage subscriberPackage;
	private ExecutionContext executionContext;
	private long newBillingCycleTime;

	public PackageUMHandler(
			UserPackage subscriberPackage,
			ExecutionContext executionContext) {
		this.subscriberPackage = subscriberPackage;
		this.executionContext = executionContext;
	}
	
	public void init() {
		Map<String, SubscriberUsage> subscriptionUsage = getCurrentServiceUsage();
		
		if (Maps.isNullOrEmpty(subscriptionUsage) == false) {
			SubscriberUsage previousPackageUsage = subscriptionUsage.entrySet().iterator().next().getValue();
			this.isPreviousBillingCycleUsage = isPreviousBillingCycleUsage(executionContext.getCurrentTime(), previousPackageUsage);
			this.isPreviousWeekUsage = isPreviousWeekUsage(executionContext.getCurrentTime(), previousPackageUsage);
			this.isPreviousDayUsage = isPreviousDayUsage(executionContext.getCurrentTime(), previousPackageUsage);
			this.newBillingCycleTime = calculateBillingCycleTime(executionContext.getCurrentTime(), previousPackageUsage);
			resetDailyWeeklyBillingCycleUsage();
		}
	}
	
	private void resetDailyWeeklyBillingCycleUsage() {

		Map<String, SubscriberUsage> quotaProfileServiceWiseUsage = getCurrentServiceUsage();
		
		for (Entry<String, SubscriberUsage> entryQuotaProfileWiseUsage : quotaProfileServiceWiseUsage.entrySet()) {
			
			SubscriberUsage subscriberUsage = entryQuotaProfileWiseUsage.getValue();
			String key = entryQuotaProfileWiseUsage.getKey();
			
			if(isPreviousDayUsage) {
				subscriberUsage.resetDailyUsage();
				subscriberUsage.setDailyResetTime(0);
			}
			
			if(isPreviousWeekUsage) {
				subscriberUsage.resetWeeklyUsage();
				subscriberUsage.setWeeklyResetTime(0);
			}
			if(isPreviousBillingCycleUsage) {
				subscriberUsage.resetBillingCycleUsage();
				subscriberUsage.setBillingCycleResetTime(newBillingCycleTime);
			}
			
			if (isPreviousDayUsage || isPreviousWeekUsage || isPreviousBillingCycleUsage) {
				addUsageInUpdateList(key, (SubscriberUsage) subscriberUsage.clone());
			}
		}
	}

	public void addSessionLevelReportedUsage(UsageMonitoringInfo monitoringInfo, String quotaProfileId) {
		calculateAllServiceUsage(monitoringInfo, quotaProfileId);
		calculatePackageLevelSessionUsage(monitoringInfo, quotaProfileId, CommonConstants.ALL_SERVICE_ID);
	}

	public void addPCCLevelReportedUsage(UsageMonitoringInfo monitoringInfo) {
		QuotaProfile quotaProfile = this.subscriberPackage.getQuotaProfileByMonitoringKey(monitoringInfo.getMonitoringKey());
		
		if (quotaProfile == null) {
			getLogger().debug(MODULE, "Skipping PCC level usage. Reason: quota profile not found for monitoring key: " + monitoringInfo.getMonitoringKey());
			return;
		}
		
    	calculateAllServiceUsage(monitoringInfo, quotaProfile.getId());
    	calculateReportedPCCServiceLevelUsage(monitoringInfo, quotaProfile.getId());
    
    	String serviceId = this.subscriberPackage.getServiceId(monitoringInfo.getMonitoringKey());
    	calculatePackageLevelSessionUsage(monitoringInfo, quotaProfile.getId(), serviceId);
	}

	private void calculateAllServiceUsage(UsageMonitoringInfo reportedUsage, String quotaProfileId) {
		if (isServiceUsageReportingRequired() == false) {
			return;
		}
		
		updateUsageInDBList(quotaProfileId, CommonConstants.ALL_SERVICE_ID, reportedUsage);
		updateUsageInCurrentServiceUsage(quotaProfileId, CommonConstants.ALL_SERVICE_ID, reportedUsage);
	}
	
	private void calculateReportedPCCServiceLevelUsage(UsageMonitoringInfo reportedUsage, String quotaProfileId) {
		
		if (isServiceUsageReportingRequired() == false) {
			return;
		}
		
		String serviceId = this.subscriberPackage.getServiceId(reportedUsage.getMonitoringKey());
		
		if (CommonConstants.ALL_SERVICE_ID.equals(serviceId)) {
			return;
		}

		updateUsageInDBList(quotaProfileId, serviceId, reportedUsage);
		updateUsageInCurrentServiceUsage(quotaProfileId, serviceId, reportedUsage);
	}

	private void calculatePackageLevelSessionUsage(UsageMonitoringInfo reportedUsage, String quotaProfileId, String serviceId) {
		
		if (isSessionUsageReportingRequired() == false) {
			return;
		}
		
		updateUsageInSessionUsage(reportedUsage, quotaProfileId, CommonConstants.ALL_SERVICE_ID);
		
		if (CommonConstants.ALL_SERVICE_ID.equals(serviceId)) {
			return;
		}
		
		updateUsageInSessionUsage(reportedUsage, quotaProfileId, serviceId);
	}
	
	private void updateUsageInSessionUsage(UsageMonitoringInfo reportedUsage, String quotaProfileId, String serviceId) {
		
		String usageKey = quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
		Map<String, PackageUsage> quotaProfileServiceWiseSessionUsage = getCurrentSessionUsage();
		
		if (quotaProfileServiceWiseSessionUsage == null) {
			return;
		}
		
		PackageUsage packageUsage = quotaProfileServiceWiseSessionUsage.get(usageKey);
	
		if (packageUsage == null) {
			packageUsage = new PackageUsage(subscriberPackage.getId(), null,
					quotaProfileId, serviceId, createUsage(reportedUsage));
			quotaProfileServiceWiseSessionUsage.put(usageKey, packageUsage);
		} else {
			ServiceUnit usedServiceUnit = reportedUsage.getUsedServiceUnit();
			Usage newUsage = packageUsage.getUsage();
			newUsage.setTotalOctets(newUsage.getTotalOctets() + usedServiceUnit.getTotalOctets());
			newUsage.setDownloadOctets(newUsage.getDownloadOctets() + usedServiceUnit.getOutputOctets());
			newUsage.setUploadOctets(newUsage.getUploadOctets() + usedServiceUnit.getInputOctets());
			newUsage.setTime(newUsage.getTime() + usedServiceUnit.getTime());
		}	
	}

	private void updateUsageInDBList(String quotaProfileId, String serviceId, UsageMonitoringInfo reportedUsage) {
		SubscriberUsage subscriberUsage = getUsageFromDBUpdateList(quotaProfileId, serviceId);
		
		if (subscriberUsage == null) {
			return;
		}
		
		updateDailyUsage(subscriberUsage, reportedUsage);
		updateWeeklyUsage(subscriberUsage, reportedUsage);
		updateCustomUsage(subscriberUsage, reportedUsage);
		updateBillingCycleUsage(subscriberUsage, reportedUsage);
	}
	
	private void updateUsageInCurrentServiceUsage(String quotaProfileId, String serviceId, UsageMonitoringInfo reportedUsage) {
		SubscriberUsage currentUsage = getUsageFromCurrentServiceUsage(quotaProfileId, serviceId);
		
		if (currentUsage == null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to meter usage for serviceId:" + serviceId
						+ ", quota profile id:" + quotaProfileId
						+ " for monitoring key:" + reportedUsage.getMonitoringKey()
						+ ". Reason: current usage not found");
			}
			return;
		}
		
		updateDailyUsage(currentUsage, reportedUsage);
		updateWeeklyUsage(currentUsage, reportedUsage);
		updateCustomUsage(currentUsage, reportedUsage);
		updateBillingCycleUsage(currentUsage, reportedUsage);
	}

	private Usage createUsage(UsageMonitoringInfo monitoringInfo) {
		ServiceUnit usedServiceUnit = monitoringInfo.getUsedServiceUnit();
		return new Usage(usedServiceUnit.getInputOctets(), usedServiceUnit.getOutputOctets(), usedServiceUnit.getTotalOctets(), usedServiceUnit.getTime());
	}

	protected void updateDailyUsage(SubscriberUsage subscriberUsage, UsageMonitoringInfo monitoringInfo) {
		
		if (isPreviousDayUsage == false || executionContext.isSessionCreatedToday()) {			
			ServiceUnit usedServiceUnit = monitoringInfo.getUsedServiceUnit();
			subscriberUsage.addDailyDownload(usedServiceUnit.getOutputOctets());
			subscriberUsage.addDailyUpload(usedServiceUnit.getInputOctets());
			subscriberUsage.addDailyTotal(usedServiceUnit.getTotalOctets());
			subscriberUsage.addDailyTime(usedServiceUnit.getTime());	
		}
	}

	protected void updateWeeklyUsage(SubscriberUsage subscriberUsage, UsageMonitoringInfo reportedUsage) {
	
		if (isPreviousWeekUsage == false || executionContext.isSessionCreatedInCurrentWeek()) {
			ServiceUnit usedServiceUnit = reportedUsage.getUsedServiceUnit();
			
			subscriberUsage.addWeeklyDownload(usedServiceUnit.getOutputOctets());
			subscriberUsage.addWeeklyUpload(usedServiceUnit.getInputOctets());
			subscriberUsage.addWeeklyTotal(usedServiceUnit.getTotalOctets());
			subscriberUsage.addWeeklyTime(usedServiceUnit.getTime());
			
		}
	
	}

	protected void updateBillingCycleUsage(SubscriberUsage subscriberUsage, UsageMonitoringInfo reportedUsage) {
		
		if (this.isPreviousBillingCycleUsage == false) {
			ServiceUnit usedServiceUnit = reportedUsage.getUsedServiceUnit();
			subscriberUsage.addBillingCycleDownload(usedServiceUnit.getOutputOctets());
			subscriberUsage.addBillingCycleUpload(usedServiceUnit.getInputOctets());
			subscriberUsage.addBillingCycleTotal(usedServiceUnit.getTotalOctets());
			subscriberUsage.addBillingCycleTime(usedServiceUnit.getTime());
		}
	}

	protected void updateCustomUsage(SubscriberUsage subscriberUsage, UsageMonitoringInfo reportedUsage) {
	
		ServiceUnit usedServiceUnit = reportedUsage.getUsedServiceUnit();
		
		subscriberUsage.addCustomDownload(usedServiceUnit.getOutputOctets());
		subscriberUsage.addCustomUpload(usedServiceUnit.getInputOctets());
		subscriberUsage.addCustomTotal(usedServiceUnit.getTotalOctets());
		subscriberUsage.addCustomTime(usedServiceUnit.getTime());
	}
	

	protected boolean isPreviousWeekUsage(Calendar currentTime, @Nullable SubscriberUsage subscriberPreviousUsage) {
		if (subscriberPreviousUsage == null) {
			return false;
		}
		return currentTime.getTimeInMillis() >= subscriberPreviousUsage.getWeeklyResetTime();
	}

	protected boolean isPreviousDayUsage(Calendar currentTime, @Nullable SubscriberUsage subscriberUsage) {
		if (subscriberUsage == null) {
			return false;
		}
	
		return currentTime.getTimeInMillis() >= subscriberUsage.getDailyResetTime();
	}
	
	protected boolean isPreviousBillingCycleUsage(Calendar currentTime, SubscriberUsage subscriberPreviousUsage) {
		return currentTime.getTimeInMillis() >= subscriberPreviousUsage.getBillingCycleResetTime(); 
	}

	public String getPackageId() {
		return subscriberPackage.getId();
	}
	
	public UserPackage getPackage() {
		return subscriberPackage;
	}

	public boolean isPreviousWeekUsage() {
		return isPreviousWeekUsage;
	}

	public boolean isPreviousDayUsage() {
		return isPreviousDayUsage;
	}
	
	public boolean isPreviousBillingCycleUsage() {
		return isPreviousBillingCycleUsage;
	}
	
	@Nullable protected abstract Map<String, PackageUsage> getCurrentSessionUsage();
	@Nullable protected abstract Map<String, SubscriberUsage> getCurrentServiceUsage();
	protected abstract long calculateBillingCycleTime(Calendar calendar,SubscriberUsage subscriberUsage);
	protected abstract void addUsageInUpdateList(String key, SubscriberUsage subscriberUsage);
	public abstract Collection<SubscriberUsage> getUpdateList();
	@Nullable public abstract Collection<SubscriberUsage> getInsertList();
	@Nullable protected abstract SubscriberUsage getUsageFromDBUpdateList(String quotaProfileId, String serviceId);
	@Nullable protected abstract SubscriberUsage getUsageFromCurrentServiceUsage(String quotaProfileId, String allServiceId);
	protected abstract boolean isServiceUsageReportingRequired();
	protected abstract boolean isSessionUsageReportingRequired();
}