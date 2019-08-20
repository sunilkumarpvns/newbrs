package com.elitecore.netvertex.pm;


import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;

public class QuotaProfileFactory {

	public static QuotaProfile createSyQuotaProfileWithUnlimitedUsage(String quotaProfileId) {
		return null;
	}

	/*
	public static QuotaProfile createQuotaProfileWithUnlimitedUsage(String quotaProfileId) {
		return new Builder(quotaProfileId).build();
	}
	

	
	public static class Builder {
		
		private final String quotaProfileId;
		private List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais = new ArrayList<Map<String,QuotaProfileDetail>>();

		public Builder(String quotaProfileId) {
			this.quotaProfileId = quotaProfileId;
		}
		
		public QuotaProfile build() {
			
			if(fupLevelserviceWiseQuotaProfileDetais.get(0) == null) {
				withHSQLevelUnlimitedQuotaFor(CommonConstants.ALL_SERVICE_ID);
			}
			
			
			if(fupLevelserviceWiseQuotaProfileDetais.isEmpty() == false){
				for(int i = 0; i < fupLevelserviceWiseQuotaProfileDetais.size(); i++){
					Map<String, QuotaProfileDetail> quotaProfileDetails = fupLevelserviceWiseQuotaProfileDetais.get(i);
					if(quotaProfileDetails == null){
						int fupLevle = i + 1;
						withFUPUnlimitedQuotaFor(CommonConstants.ALL_SERVICE_ID, fupLevle);
					}
				}
			}
			

			return new QuotaProfile(quotaProfileId, quotaProfileId, fupLevelserviceWiseQuotaProfileDetais);
		}
		
		public Builder withFirstLevelFUPRandomQuotaFor(String serviceId) {
			return withFUPRandomQuotaFor(serviceId, 1);
		}
		
		public Builder withFirstLevelFUPRandomQuota(QuotaProfileDetail quotaProfileDetail) {
			return withFUPRandomQuota(quotaProfileDetail, 1);
		}
		
		public Builder withFirstLevelFUPUnlimitedQuotaFor(String serviceId) {
			return withFUPUnlimitedQuotaFor(serviceId,1);
		}
		
		public Builder withFirstLevelFUPUnlimitedQuotaFor(QuotaProfileDetail quotaProfileDetail) {
			return withFUPUnlimitedQuotaFor(quotaProfileDetail,1);
		}
		
		public Builder withSecondLevelFUPRandomQuotaFor(String serviceId) {
			return withFUPRandomQuotaFor(serviceId,2);
		}
		
		public Builder withSecondLevelFUPRandomQuota(QuotaProfileDetail quotaProfileDetail) {
			return withFUPRandomQuota(quotaProfileDetail,2);
		}
		
		public Builder withSecondLevelFUPUnlimitedQuotaFor(String serviceId) {
			return withFUPUnlimitedQuotaFor(serviceId,2);
		}
		
		public Builder withSecondLevelFUPUnlimitedQuotaFor(QuotaProfileDetail quotaProfileDetail) {
			return withFUPUnlimitedQuotaFor(quotaProfileDetail,2);
		}
		
		public Builder withThirdLevelFUPRandomQuotaFor(String serviceId) {
			return withFUPRandomQuotaFor(serviceId,3);
		}
		
		public Builder withThirdLevelFUPRandomQuota(QuotaProfileDetail quotaProfileDetail) {
			return withFUPRandomQuota(quotaProfileDetail,3);
		}
		
		public Builder withThirdLevelFUPUnlimitedQuotaFor(String serviceId) {
			return withFUPUnlimitedQuotaFor(serviceId,3);
		}
		
		public Builder withThirdLevelFUPUnlimitedQuotaFor(QuotaProfileDetail quotaProfileDetail) {
			return withFUPUnlimitedQuotaFor(quotaProfileDetail,3);
		}
		
		public Builder withFUPRandomQuotaFor(String serviceId,int fupLevle) {
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.createQuotaProfileDetailWithRandomUsageFor(quotaProfileId, serviceId);
			return withFUPRandomQuota(quotaProfileDetail,fupLevle);
		}
		
		public Builder withFUPRandomQuota(QuotaProfileDetail quotaProfileDetail,int fupLevle) {
			
			Map<String, QuotaProfileDetail> quotaProfileDetails = getQuotaProfilesOnFUPLeveL(fupLevle);
			quotaProfileDetails.put(quotaProfileDetail.getServiceId(), quotaProfileDetail);
			return this;
		}
		
		public Builder withFUPUnlimitedQuotaFor(String serviceId,int fupLevle) {
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.unlimitedUsageFor(quotaProfileId, serviceId);
			return withFUPUnlimitedQuotaFor(quotaProfileDetail,fupLevle);
		}
		
		public Builder withFUPUnlimitedQuotaFor(QuotaProfileDetail quotaProfileDetail,int fupLevle) {
			
			Map<String, QuotaProfileDetail> quotaProfileDetails = getQuotaProfilesOnFUPLeveL(fupLevle);
			quotaProfileDetails.put(quotaProfileDetail.getServiceId(), quotaProfileDetail);
			return this;
		}
		
		public Builder withHSQLevelRandomQuotaFor(String serviceId) {
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.createQuotaProfileDetailWithRandomUsageFor(quotaProfileId, serviceId);
			return withHSQLevelRandomQuota(quotaProfileDetail);
		}
		
		public Builder withHSQLevelRandomQuota(QuotaProfileDetail quotaProfileDetail) {
			withFUPUnlimitedQuotaFor(quotaProfileDetail, 0);
			return this;
		}
		
		public Builder withHSQLevelUnlimitedQuotaFor(String serviceId) {
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.unlimitedUsageFor(quotaProfileId, serviceId);
			return withHSQLevelUnlimitedQuotaFor(quotaProfileDetail);
		}
		
		public Builder withHSQLevelUnlimitedQuotaFor(QuotaProfileDetail quotaProfileDetail) {
			withFUPUnlimitedQuotaFor(quotaProfileDetail, 0);
			return this;
		}

		
		private Map<String, QuotaProfileDetail> getQuotaProfilesOnFUPLeveL(int fupLevel) {
			Map<String, QuotaProfileDetail> quotaProfileDetails = null;
			int index = fupLevel;
			
			try {
				quotaProfileDetails = fupLevelserviceWiseQuotaProfileDetais.get(index);
			}catch(IndexOutOfBoundsException indexOutOfBoundsException) {
				for(int i=0; i < index; i++){
					if(i >= fupLevelserviceWiseQuotaProfileDetais.size()) {
						fupLevelserviceWiseQuotaProfileDetais.set(i, null);
					}
				}
				
				quotaProfileDetails = new LinkedHashMap<String, QuotaProfileDetail>();
				
				fupLevelserviceWiseQuotaProfileDetais.set(index, quotaProfileDetails);
			}
			return quotaProfileDetails;
		}
	}

	public static QuotaProfile createQuotaProfileWithRandomUsage(String string) {
		return new Builder(string).withHSQLevelRandomQuotaFor(CommonConstants.ALL_SERVICE_ID).build();
	}

*/}
