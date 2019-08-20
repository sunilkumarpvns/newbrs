package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.PkgDataBuilder;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class RncProfileFactoryTest { //NOSONAR

	private static final PackageFactory PACKAGE_FACTORY = new PackageFactory();
	private RncProfileFactory rncProfileFactory;
	private RncProfileData rncProfileData;
	private DataServiceTypeFactory dataServiceTypeFactory;
	private String pccProfileName = "pccProfileName";

	@Before
	public void before() {
		rncProfileData = PkgDataBuilder.createRnCQuotaProfileData();
		RatingGroupFactory ratingGroupFactory = new RatingGroupFactory(PACKAGE_FACTORY);
		dataServiceTypeFactory = new DataServiceTypeFactory(ratingGroupFactory, PACKAGE_FACTORY);
		rncProfileFactory = new RncProfileFactory(PACKAGE_FACTORY, ratingGroupFactory, dataServiceTypeFactory);
	}
	
	public class QuotaProfileCreationFailedWhen {
		
		@Test
		public void noRatingGroup_attached() {
			
			for (RncProfileDetailData quotaDetail : rncProfileData.getRncProfileDetailDatas()) {
				quotaDetail.setRatingGroupData(null);
			}
			
			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);
			
			assertNull(quotaProfile);
			assertTrue(failReasons.size() == 1);
		}
		
		@Test
		public void noServiceType_attached() {
			
			for (RncProfileDetailData quotaDetail : rncProfileData.getRncProfileDetailDatas()) {
				quotaDetail.setDataServiceTypeData(null);
			}
			
			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);
			
			assertNull(quotaProfile);
			assertTrue(failReasons.size() == 1);
		}
		
		@Test
		public void noDetailsConfigured() {
			
			rncProfileData.setRncProfileDetailDatas(null);
			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);
			
			assertNull(quotaProfile);
			assertTrue(failReasons.size() == 1);
		}
		
		//This will apply on configuration like fupLevel2 is configured without configuring fupLevel1.
		@Test
		public void invalid_quotaDetail_configuration() {

			for (RncProfileDetailData quotaDetail : rncProfileData.getRncProfileDetailDatas()) {
				quotaDetail.setFupLevel(1);
			}

			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);

			assertNull(quotaProfile);
			assertTrue(failReasons.size() == 1);
		}

		@Test
		public void invalid_renewalIntervalUnit_Configured() {

			rncProfileData.setRenewalIntervalUnit("Hello");

			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);

			assertNull(quotaProfile);
			assertTrue(failReasons.size() == 1);
		}
	}


	public class SetUnlimitedValueWhenCounterLimitIsNotConfigured {

		private RncProfileDetailData rncProfileDetailData;

		@Before
		public void setUp() {
			this.rncProfileDetailData = rncProfileData.getRncProfileDetailDatas().get(0);
		}

		@Test
		public void dailyTotal() {
			rncProfileDetailData.setDailyUsageLimit(null);
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			executeAndAssert(quotaProfile, rncProfileDetailData.getDailyUsageLimit().longValue());
		}

		@Test
		public void dailyTime() {
			rncProfileDetailData.setDailyTimeLimit(null);
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			executeAndAssert(quotaProfile, rncProfileDetailData.getDailyTimeLimit().longValue());
		}

		private void executeAndAssert(QuotaProfile quotaProfile, long actual) {
			assertNotNull(quotaProfile);
			assertEquals(CommonConstants.QUOTA_UNLIMITED, actual);
		}

		@Test
		public void weeklyTotal() {
			rncProfileDetailData.setWeeklyUsageLimit(null);
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			executeAndAssert(quotaProfile, rncProfileDetailData.getWeeklyUsageLimit().longValue());
		}

		@Test
		public void weeklyTime() {
			rncProfileDetailData.setWeeklyTimeLimit(null);
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			executeAndAssert(quotaProfile, rncProfileDetailData.getWeeklyTimeLimit().longValue());
		}
	}


	public class TimeAndVolumePulseShouldBeConfiguredDefaultOneWhen{

		private RncProfileDetailData rncProfileDetailData;

		@Before
		public void setUp() {
			this.rncProfileDetailData = rncProfileData.getRncProfileDetailDatas().get(0);
		}

		@Test
		public void timePulseNotConfigured() {
			rncProfileDetailData.setPulseTime(null);
			rncProfileDetailData.setPulseTimeUnit(TimeUnit.HOUR.name());
			rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			assertEquals(rncProfileDetailData.getPulseTime().longValue(), 1l);
			assertEquals(rncProfileDetailData.getPulseTimeUnit(), TimeUnit.SECOND.name());
		}


		@Test
		public void volumePulseNotConfigured() {
			rncProfileDetailData.setPulseVolume(null);
			rncProfileDetailData.setPulseVolumeUnit(DataUnit.MB.name());
			rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			assertEquals(rncProfileDetailData.getPulseVolume().longValue(), 1l);
			assertEquals(rncProfileDetailData.getPulseVolumeUnit(), DataUnit.BYTE.name());
		}


	}

	public class TimeAndVolumeShouldBeConfiguredValueWhen{

		private RncProfileDetailData rncProfileDetailData;

		@Before
		public void setUp() {
			this.rncProfileDetailData = rncProfileData.getRncProfileDetailDatas().get(0);
		}

		@Test
		public void timePulseConfigured() {
			long pulseTime = nextLong(2l, 10000);
			rncProfileDetailData.setPulseTime(pulseTime);
			rncProfileDetailData.setPulseTimeUnit(TimeUnit.HOUR.name());
			rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			assertEquals(rncProfileDetailData.getPulseTime().longValue(), pulseTime);
			assertEquals(rncProfileDetailData.getPulseTimeUnit(), TimeUnit.HOUR.name());
		}

		@Test
		public void volumePulseConfigured() {
			long pulseVolume = nextLong(2l, 10000);
			rncProfileDetailData.setPulseVolume(pulseVolume);
			rncProfileDetailData.setPulseVolumeUnit(DataUnit.MB.name());
			rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			assertEquals(rncProfileDetailData.getPulseVolume().longValue(), pulseVolume);
			assertEquals(rncProfileDetailData.getPulseVolumeUnit(), DataUnit.MB.name());
		}


	}

	public class CreateQuotaProfile {

		@Test
		public void with_valid_hsq_balanceBasedQuotaProfileData() {

			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);

			ReflectionAssert.assertReflectionEquals(createExpectedQuotaProfile(rncProfileData), quotaProfile);
			assertTrue(failReasons.isEmpty());
		}

		@Test
		public void with_zero_renewalInterval_When_It_Comes_Null_From_Database() {
			rncProfileData.setRenewalInterval(null);
			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);

			Assert.assertEquals(0,quotaProfile.getRenewalInterval() );
			assertTrue(failReasons.isEmpty());
		}

		@Test
		public void with_valid_threeLevel_balanceBasedQuotaProfileData() {
			
			rncProfileData = PkgDataBuilder.createThreeFUPLevelBalanceBasedQuotaProfileData();
			
			ArrayList<String> failReasons = new ArrayList<String>();
			QuotaProfile quotaProfile = rncProfileFactory.create(rncProfileData, pccProfileName, failReasons);
			
			ReflectionAssert.assertReflectionEquals(createExpectedQuotaProfile(rncProfileData), quotaProfile);
			assertTrue(failReasons.isEmpty());
		}

		@Test
		public void withRevenueDetailConfiguredInQuotaProfile() {
			QuotaProfile quotaProfile =	rncProfileFactory.create(rncProfileData, pccProfileName, new ArrayList<>());
			RncProfileDetail rncQuotaProfileDetail= (RncProfileDetail)quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values().iterator().next();
			Assert.assertNotNull(rncQuotaProfileDetail.getRevenueDetail());
			Assert.assertEquals(rncQuotaProfileDetail.getRevenueDetail(),"RevenueDetail");
			ReflectionAssert.assertReflectionEquals(createExpectedQuotaProfile(rncProfileData), quotaProfile);
		}


		private QuotaProfile createExpectedQuotaProfile(RncProfileData balanceBasedQuotaProfileData) {

			PackageFactory packageFactory = new PackageFactory();
			
			return packageFactory.createBalanceBasedQuotaProfile("Default_Balance_Quota", "PackageTest", "1", BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED
					, createBalanceDetails(packageFactory, balanceBasedQuotaProfileData),CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
		}

		private List<Map<String, QuotaProfileDetail>> createBalanceDetails(PackageFactory packageFactory, RncProfileData balanceBasedQuotaProfileData) {
			
			List<Map<String, QuotaProfileDetail>> balanceBaseQuotaProfileDetails = new ArrayList<Map<String,QuotaProfileDetail>>();
			
			for (RncProfileDetailData quotaDetail : balanceBasedQuotaProfileData.getRncProfileDetailDatas()) {
				
				Map<String, QuotaProfileDetail> serviceToQuota = new HashMap<String, QuotaProfileDetail>();

				double rate = quotaDetail.getRate() == null ? 0  : quotaDetail.getRate().doubleValue();

				BalanceLevel balanceLevel = BalanceLevel.valueOf(quotaDetail.getRncProfileData().getBalanceLevel());

				boolean isHSQLevel = balanceLevel.fupLevel == quotaDetail.getFupLevel();

				serviceToQuota.put(quotaDetail.getDataServiceTypeData().getId()
						, packageFactory.createRnCQuotaProfileDetail(balanceBasedQuotaProfileData.getId(), balanceBasedQuotaProfileData.getName()
								, dataServiceTypeFactory.createServiceType(quotaDetail.getDataServiceTypeData()) , quotaDetail.getFupLevel()
								, packageFactory.createRatingGroup(quotaDetail.getRatingGroupData().getId(), quotaDetail.getRatingGroupData().getName(), null, quotaDetail.getRatingGroupData().getIdentifier())
								, createAggreationKeyToAllowedUsage(quotaDetail), quotaDetail.getPulseVolume(), quotaDetail.getPulseTime(), quotaDetail.getPulseVolume(), quotaDetail.getPulseTime(), quotaDetail.getPulseVolumeUnit(), quotaDetail.getPulseTimeUnit(), rate, UsageType.valueOf(quotaDetail.getRateUnit()),
								QuotaUsageType.valueOf(quotaDetail.getRncProfileData().getQuotaType()), VolumeUnitType.valueOf(quotaDetail.getRncProfileData().getUnitType()), isHSQLevel, pccProfileName,
								quotaDetail.getVolumeCarryForwardLimit(),quotaDetail.getTimeCarryForwardLimit(), Objects.nonNull(quotaDetail.getRevenueDetail())?quotaDetail.getRevenueDetail().getName():null));

				balanceBaseQuotaProfileDetails.add(serviceToQuota);
			}
			
			return balanceBaseQuotaProfileDetails;
		}

		private Map<AggregationKey, AllowedUsage> createAggreationKeyToAllowedUsage(RncProfileDetailData rncProfileDetailData) {

			Map<AggregationKey, AllowedUsage> allowedUsages = new HashMap<AggregationKey, AllowedUsage>();
			
			allowedUsages.put(AggregationKey.DAILY, AggregationKey.DAILY.createAllowedUsage(rncProfileDetailData));
			allowedUsages.put(AggregationKey.WEEKLY, AggregationKey.WEEKLY.createAllowedUsage(rncProfileDetailData));
			allowedUsages.put(AggregationKey.BILLING_CYCLE, AggregationKey.BILLING_CYCLE.createAllowedUsage(rncProfileDetailData));
			
			return allowedUsages;
		}
	}
}

