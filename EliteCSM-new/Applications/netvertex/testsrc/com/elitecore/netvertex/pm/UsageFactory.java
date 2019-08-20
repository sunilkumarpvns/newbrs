package com.elitecore.netvertex.pm;


public class UsageFactory {/*
	
	public static Builder createUsageFor(Package package1) {
		return new Builder(package1);
		
	}
	
	public static class Builder {
		private final Package package1;
		private Map<String, SubscriberUsage> subscriberUsages = new HashMap<String, SubscriberUsage>();;
		
		public Builder(Package package1) {
			this.package1 = package1;
		}
		
		
		public Builder hasUsageExceededForAllServices() {
			
			for(IQoSProfile qoSProfile : package1.getQoSProfiles()) {
				QuotaProfile quotaProfileDetail = ((QoSProfile)qoSProfile).getQuotaProfileByMonitoringKey();
				if(quotaProfileDetail != null) {
					for(QuotaProfileDetail quotaProfileDetail : quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().values()) {
						if(UsageFactory.hasUnlimtedQuota(quotaProfileDetail) == true){ 
							throw new RuntimeException(package1.getName() + " has unlimited quota");
						}
						
						SubscriberUsage subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder(UUID.randomUUID().toString(), "1234", 
								quotaProfileDetail.getServiceId(), 
								quotaProfileDetail.getQuotaProfileIdOrRateCardId(),
								package1.getId()).withAllTypeUsage(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE).build();
						
						subscriberUsages.put(((UMBaseQuotaProfileDetail)quotaProfileDetail).getUsageKey(), subscriberUsage);
					}
				}
			}
			return this;
		}
		
		public Builder hasUsageLowerThanHSQForAllServices() {
			
			for(IQoSProfile qoSProfile : package1.getQoSProfiles()) {
				QuotaProfile quotaProfileDetail = ((QoSProfile)qoSProfile).getQuotaProfileByMonitoringKey();
				if(quotaProfileDetail != null) {
					for(QuotaProfileDetail quotaProfileDetail : quotaProfileDetail.getHsqLevelServiceWiseQuotaProfileDetails().values()) {
						if(UsageFactory.hasUnlimtedQuota(quotaProfileDetail) == true){ 
							throw new RuntimeException(package1.getName() + " has unlimited quota");
						}
						
						SubscriberUsage subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder(UUID.randomUUID().toString(), "1234", 
								quotaProfileDetail.getServiceId(), 
								quotaProfileDetail.getQuotaProfileIdOrRateCardId(),
								package1.getId()).withAllTypeUsage(1, 1, 1, 1).build();
						
						subscriberUsages.put(((UMBaseQuotaProfileDetail)quotaProfileDetail).getUsageKey(), subscriberUsage);
					}
				}
			}
			return this;
		}
		
		public Map<String,SubscriberUsage> build() {
			if(subscriberUsages.isEmpty()) {
				return null;
			}
			
			return subscriberUsages;
		}
		
	}

	public static boolean hasUnlimtedQuota(QuotaProfileDetail quotaProfileDetail) {
		SubscriberUsage subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder("1", "1", "1", "1", "1").withAllTypeUsage(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE).build();
		
		if(quotaProfileDetail.isUsageExceeded(subscriberUsage) == true) {
			return false;
		} else {
			return true;
		}
		return true;
		
	}
	

*/}
