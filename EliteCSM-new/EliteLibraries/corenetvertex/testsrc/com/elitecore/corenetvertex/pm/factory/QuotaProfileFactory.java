package com.elitecore.corenetvertex.pm.factory;


import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuotaProfileFactory {

	public static QuotaProfile createSyQuotaProfileWithUnlimitedUsage(String quotaProfileId) {
		return null;
	}


	public static QuotaProfile createQuotaProfileWithUnlimitedUsage(String quotaProfileId) {
		return new Builder(quotaProfileId).build();
	}

	public static QuotaProfile createRnCProfileDetail() {
		return new RnCQuotaProfileBuilder("","").build();
	}

	public static class RnCQuotaProfileBuilder {

		private String quotaProfileId = null;
		private String packageId = null;
		private List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais = new ArrayList<Map<String,QuotaProfileDetail>>(4);
		private int renewalInterval = 2;
		private RenewalIntervalUnit renewalIntervalUnit = RenewalIntervalUnit.MONTH_END;
		private Boolean proration = CommonStatusValues.DISABLE.isBooleanValue();
		private Boolean carryForward = CommonStatusValues.DISABLE.isBooleanValue();
		private BalanceLevel balanceLevel;
		private CarryForwardStatus carryForwardStatus;

		public QuotaProfile build() {
			return new QuotaProfile("name", "pkgName", quotaProfileId, balanceLevel,renewalInterval, renewalIntervalUnit, QuotaProfileType.RnC_BASED, fupLevelserviceWiseQuotaProfileDetais,proration,carryForward);
		}

		public RnCQuotaProfileBuilder(String packageId, String quotaProfileId) {
			this.packageId = packageId;
			this.quotaProfileId = quotaProfileId;
		}

		public RnCQuotaProfileBuilder withBalanceLevel(BalanceLevel balanceLevel) {
			this.balanceLevel = balanceLevel;
			return this;
		}
		
		public RnCQuotaProfileBuilder withCarryForward(Boolean carryForward) {
			this.carryForward = carryForward;
			return this;
		}

		public RnCQuotaProfileBuilder withFupLevelserviceWiseQuotaProfileDetais(List<Map<String, QuotaProfileDetail>> fupLevelServiceWiseQuotaProfileDetails) {
			this.fupLevelserviceWiseQuotaProfileDetais = fupLevelServiceWiseQuotaProfileDetails;
			return this;
		}
	}

	public static class Builder {
		
		private final String quotaProfileId;
		private List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais = new ArrayList<Map<String,QuotaProfileDetail>>(4);
		private int renewalInterval = 2;
		private RenewalIntervalUnit renewalIntervalUnit = RenewalIntervalUnit.MONTH_END;
		private Boolean proration = CommonStatusValues.DISABLE.isBooleanValue();
		private Boolean carryForward = CommonStatusValues.DISABLE.isBooleanValue();
		
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

			return new QuotaProfile("name", "pkgName", quotaProfileId, BalanceLevel.HSQ,renewalInterval, renewalIntervalUnit, QuotaProfileType.USAGE_METERING_BASED, fupLevelserviceWiseQuotaProfileDetais,proration,carryForward);
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

		public Builder withRenewalInterval(int renewalInterval){
			this.renewalInterval = renewalInterval;
			return this;
		}

		public Builder withRenewalIntervalUnit(RenewalIntervalUnit renewalIntervalUnit){
			this.renewalIntervalUnit = renewalIntervalUnit;
			return this;
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
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.createQuotaProfileDetailWithUnlimiteUsageFor(quotaProfileId, serviceId);
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
			QuotaProfileDetail quotaProfileDetail = QuotaProfileDetailFactory.createQuotaProfileDetailWithUnlimiteUsageFor(quotaProfileId, serviceId);
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
				
				fupLevelserviceWiseQuotaProfileDetais.add(quotaProfileDetails);
			}
			return quotaProfileDetails;
		}
	}

	public static QuotaProfile createQuotaProfileWithRandomUsage(String string) {
		Builder builder = new Builder(string).withHSQLevelRandomQuotaFor(CommonConstants.ALL_SERVICE_ID);
		builder.withRenewalInterval(2);
		builder.withRenewalIntervalUnit(RenewalIntervalUnit.MONTH_END);
		return builder.build();
	}

}
