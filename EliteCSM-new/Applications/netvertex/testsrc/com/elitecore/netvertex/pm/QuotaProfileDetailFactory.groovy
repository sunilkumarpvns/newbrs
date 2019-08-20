package com.elitecore.netvertex.pm

import com.elitecore.corenetvertex.constants.AggregationKey
import com.elitecore.corenetvertex.constants.DataUnit
import com.elitecore.corenetvertex.constants.TimeUnit
import com.elitecore.corenetvertex.constants.UsageType
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType
import com.elitecore.corenetvertex.pm.pkg.DataServiceType
import com.elitecore.corenetvertex.pm.pkg.RatingGroup
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsageBuilder
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail
import groovy.transform.TypeChecked

import static com.elitecore.corenetvertex.constants.CommonConstants.UNLIMITED_QCF_QUOTA
import static com.elitecore.corenetvertex.constants.DataUnit.*
import static com.elitecore.corenetvertex.constants.TimeUnit.MINUTE

@TypeChecked
public class QuotaProfileDetailFactory {
	


	public QuotaProfileDetailFactory () {
		
	}
	
	public static QuotaProfileDetail unlimitedUsageFor(String quotaProfileId, String serviceId) {

		Map<AggregationKey, AllowedUsage> keyToAllowedUsage = [:]

		keyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, AllowedUsageBuilder.billingCycle { total Integer.MAX_VALUE, KB; upload Integer.MAX_VALUE, GB; download Integer.MAX_VALUE, MB;time Integer.MAX_VALUE, MINUTE})
		keyToAllowedUsage.put(AggregationKey.CUSTOM, AllowedUsageBuilder.custom { total Integer.MAX_VALUE, KB; upload Integer.MAX_VALUE, GB; download Integer.MAX_VALUE, MB;time Integer.MAX_VALUE, MINUTE})
		keyToAllowedUsage.put(AggregationKey.DAILY, AllowedUsageBuilder.daily { total Integer.MAX_VALUE, KB; upload Integer.MAX_VALUE, GB; download Integer.MAX_VALUE, MB;time Integer.MAX_VALUE, MINUTE})
		keyToAllowedUsage.put(AggregationKey.WEEKLY, AllowedUsageBuilder.weekly { total Integer.MAX_VALUE, KB; upload Integer.MAX_VALUE, GB; download Integer.MAX_VALUE, MB;time Integer.MAX_VALUE, MINUTE})


		println keyToAllowedUsage;

		return new UMBaseQuotaProfileDetail(quotaProfileId, quotaProfileId, serviceId ,1 , serviceId, keyToAllowedUsage)

	}



	public static QuotaProfileDetail lessThan(AllowedUsage allowedUsage) {

		return null;

	}

	public static RnCQuotaProfileDetail randomBalance(Map parameters) {

		Map<AggregationKey, AllowedUsage> keyToAllowedUsage = [:]

		Random random = new Random();

		keyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, AllowedUsageBuilder.billingCycle { total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.CUSTOM, AllowedUsageBuilder.custom { total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.DAILY, AllowedUsageBuilder.daily {total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.WEEKLY, AllowedUsageBuilder.weekly {total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		def ratingGroup = (RatingGroup) parameters.get("rg", new RatingGroup("0", "test", "test", 0));

		DataServiceType serviceType = new DataServiceType(parameters.get("service", "s1").toString(),
				parameters.get("service", "s1").toString(),
				new Random().nextInt(),
					Collections.<String>emptyList(),
		Arrays.<RatingGroup>asList(ratingGroup));

		println keyToAllowedUsage;

		return new RnCQuotaProfileDetail(parameters.get("name", "test").toString(),
                parameters.get("name", "test").toString(),
                serviceType,
                Integer.parseInt(parameters.get("level", 0).toString()),
                ratingGroup,
                keyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0d, UsageType.VOLUME,
                QuotaUsageType.HYBRID, VolumeUnitType.TOTAL, true, "pccProfileName", UNLIMITED_QCF_QUOTA, UNLIMITED_QCF_QUOTA, "test")

	}

	public static RnCQuotaProfileDetail randomBalanceWithRate(Map parameters, double rate) {

		Map<AggregationKey, AllowedUsage> keyToAllowedUsage = [:]

		Random random = new Random();

		keyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, AllowedUsageBuilder.billingCycle { total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.CUSTOM, AllowedUsageBuilder.custom { total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.DAILY, AllowedUsageBuilder.daily {total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		keyToAllowedUsage.put(AggregationKey.WEEKLY, AllowedUsageBuilder.weekly {total(random.nextInt(Integer.MAX_VALUE), KB); upload(random.nextInt(Integer.MAX_VALUE), GB); download (random.nextInt(Integer.MAX_VALUE), MB);time (random.nextInt(Integer.MAX_VALUE), MINUTE)})
		RatingGroup ratingGroup = (RatingGroup) parameters.get("rg", new RatingGroup("0", "test", "test", 0));
		DataServiceType serviceType = (DataServiceType) parameters.get("dataservice", new DataServiceType(parameters.get("service", "s1").toString(),
				parameters.get("service", "s1").toString(),
				new Random().nextInt(),
				Collections.<String>emptyList(),
				Arrays.<RatingGroup>asList(ratingGroup)));

		println keyToAllowedUsage;

		return new RnCQuotaProfileDetail(parameters.get("name", "test").toString(),
                parameters.get("name", "test").toString(),
                serviceType,
                Integer.parseInt(parameters.get("level", 0).toString()),
                ratingGroup,
                keyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), rate, UsageType.VOLUME,
                QuotaUsageType.HYBRID, VolumeUnitType.TOTAL, true, "pccProfileName", UNLIMITED_QCF_QUOTA, UNLIMITED_QCF_QUOTA, "test")
	}






	/*public static QuotaProfileDetail createQuotaProfileDetailWithRandomUsageFor(String quotaProfileId, String serviceId) {
		return new UMQuotaProfileDetail(quotaProfileId, serviceId, serviceId,1
				.withBillingCycleDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withBillingCycleTime(random.nextInt(Integer.MAX_VALUE))
				.withCustomDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withCustomTime(random.nextInt(Integer.MAX_VALUE))
				.withDailyDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withDailyTime(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyDownlodUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyUploadUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyTotalUsage(random.nextInt(Integer.MAX_VALUE))
				.withWeeklyTime(random.nextInt(Integer.MAX_VALUE))
				.build();
	}*/

	public static void main(String[] args) {
		unlimitedUsageFor("yrdy", "");
	}
	
	
}
