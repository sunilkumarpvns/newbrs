package com.elitecore.netvertex.pm;

import junitparams.JUnitParamsRunner;

import org.junit.Ignore;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
@Ignore
public class QuotaProfileDetailTest {/*

	public Object[][] dataProvider_for_isUsageExceeded_should_return_false_when_daily_usage_exceeded() {

		Random random = new Random();
		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 99),
				$(UsageType.UPLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.DOWNLOAD.name(), 100, 99),
				$(UsageType.DOWNLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.TOTAL.name(), 100, 99),
				$(UsageType.TOTAL.name(), 100, random.nextInt(99)),

				$(UsageType.TIME.name(), 100, 99),
				$(UsageType.TIME.name(), 100, random.nextInt(99))

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_false_when_daily_usage_exceeded")
	public void test_isUsageExceeded_should_return_false_when_daily_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withDailyUploadUsage(usagelimit);
				subscriberUsageBuilder.withDailyUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withDailyDownlodUsage(usagelimit);
				subscriberUsageBuilder.withDailyDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withDailyTotalUsage(usagelimit);
				subscriberUsageBuilder.withDailyTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withDailyTime(usagelimit);
				subscriberUsageBuilder.withDailyTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertFalse(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_false_when_weekly_usage_exceeded() {

		Random random = new Random();

		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 99),
				$(UsageType.UPLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.DOWNLOAD.name(), 100, 99),
				$(UsageType.DOWNLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.TOTAL.name(), 100, 99),
				$(UsageType.TOTAL.name(), 100, random.nextInt(99)),

				$(UsageType.TIME.name(), 100, 99),
				$(UsageType.TIME.name(), 100, random.nextInt(99))

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_false_when_weekly_usage_exceeded")
	public void test_isUsageExceeded_should_return_false_when_weekly_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withWeeklyUploadUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withWeeklyDownlodUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withWeeklyTotalUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withWeeklyTime(usagelimit);
				subscriberUsageBuilder.withWeeklyTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertFalse(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_false_when_billing_cycle_usage_exceeded() {

		Random random = new Random();
		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 99),
				$(UsageType.UPLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.DOWNLOAD.name(), 100, 99),
				$(UsageType.DOWNLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.TOTAL.name(), 100, 99),
				$(UsageType.TOTAL.name(), 100, random.nextInt(99)),

				$(UsageType.TIME.name(), 100, 99),
				$(UsageType.TIME.name(), 100, random.nextInt(99))

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_false_when_billing_cycle_usage_exceeded")
	public void test_isUsageExceeded_should_return_false_when_billing_cycle_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withBillingCycleUploadUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withBillingCycleDownlodUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withBillingCycleTotalUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withBillingCycleTime(usagelimit);
				subscriberUsageBuilder.withBillingCycleTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertFalse(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_false_when_custom_usage_exceeded() {

		Random random = new Random();
		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 99),
				$(UsageType.UPLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.DOWNLOAD.name(), 100, 99),
				$(UsageType.DOWNLOAD.name(), 100, random.nextInt(99)),

				$(UsageType.TOTAL.name(), 100, 99),
				$(UsageType.TOTAL.name(), 100, random.nextInt(99)),

				$(UsageType.TIME.name(), 100, 99),
				$(UsageType.TIME.name(), 100, random.nextInt(99))

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_false_when_custom_usage_exceeded")
	public void test_isUsageExceeded_should_return_false_when_custom_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withCustomUploadUsage(usagelimit);
				subscriberUsageBuilder.withCustomUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withCustomDownlodUsage(usagelimit);
				subscriberUsageBuilder.withCustomDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withCustomTotalUsage(usagelimit);
				subscriberUsageBuilder.withCustomTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withCustomTime(usagelimit);
				subscriberUsageBuilder.withCustomTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertFalse(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_true_when_daily_usage_exceeded() {

		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 100),
				$(UsageType.UPLOAD.name(), 100, 101),

				$(UsageType.DOWNLOAD.name(), 100, 100),
				$(UsageType.DOWNLOAD.name(), 100, 101),

				$(UsageType.TOTAL.name(), 100, 100),
				$(UsageType.TOTAL.name(), 100, 101),

				$(UsageType.TIME.name(), 100, 100),
				$(UsageType.TIME.name(), 100, 101)

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_true_when_daily_usage_exceeded")
	public void test_isUsageExceeded_should_return_true_when_daily_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withDailyUploadUsage(usagelimit);
				subscriberUsageBuilder.withDailyUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withDailyDownlodUsage(usagelimit);
				subscriberUsageBuilder.withDailyDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withDailyTotalUsage(usagelimit);
				subscriberUsageBuilder.withDailyTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withDailyTime(usagelimit);
				subscriberUsageBuilder.withDailyTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertTrue(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_true_when_weekly_usage_exceeded() {

		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 100),
				$(UsageType.UPLOAD.name(), 100, 101),

				$(UsageType.DOWNLOAD.name(), 100, 100),
				$(UsageType.DOWNLOAD.name(), 100, 101),

				$(UsageType.TOTAL.name(), 100, 100),
				$(UsageType.TOTAL.name(), 100, 101),

				$(UsageType.TIME.name(), 100, 100),
				$(UsageType.TIME.name(), 100, 101)

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_true_when_weekly_usage_exceeded")
	public void test_isUsageExceeded_should_return_true_when_weekly_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withWeeklyUploadUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withWeeklyDownlodUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withWeeklyTotalUsage(usagelimit);
				subscriberUsageBuilder.withWeeklyTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withWeeklyTime(usagelimit);
				subscriberUsageBuilder.withWeeklyTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertTrue(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_true_when_billing_cycle_usage_exceeded() {

		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 100),
				$(UsageType.UPLOAD.name(), 100, 101),

				$(UsageType.DOWNLOAD.name(), 100, 100),
				$(UsageType.DOWNLOAD.name(), 100, 101),

				$(UsageType.TOTAL.name(), 100, 100),
				$(UsageType.TOTAL.name(), 100, 101),

				$(UsageType.TIME.name(), 100, 100),
				$(UsageType.TIME.name(), 100, 101)

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_true_when_billing_cycle_usage_exceeded")
	public void test_isUsageExceeded_should_return_true_when_billing_cycle_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withBillingCycleUploadUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withBillingCycleDownlodUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withBillingCycleTotalUsage(usagelimit);
				subscriberUsageBuilder.withBillingCycleTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withBillingCycleTime(usagelimit);
				subscriberUsageBuilder.withBillingCycleTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertTrue(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	public Object[][] dataProvider_for_isUsageExceeded_should_return_true_when_custom_usage_exceeded() {

		return new Object[][] {
				$(UsageType.UPLOAD.name(), 100, 100),
				$(UsageType.UPLOAD.name(), 100, 101),

				$(UsageType.DOWNLOAD.name(), 100, 100),
				$(UsageType.DOWNLOAD.name(), 100, 101),

				$(UsageType.TOTAL.name(), 100, 100),
				$(UsageType.TOTAL.name(), 100, 101),

				$(UsageType.TIME.name(), 100, 100),
				$(UsageType.TIME.name(), 100, 101)

		};

	}

	@Test
	@Parameters(method = "dataProvider_for_isUsageExceeded_should_return_true_when_custom_usage_exceeded")
	public void test_isUsageExceeded_should_return_true_when_custom_usage_exceeded(String usageType, long usagelimit, long actualUsage) {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder("su1", "12345", "s1", "q1", "pkg1");

		switch (UsageType.valueOf(usageType)) {
			case UPLOAD:
				quotaProfileDetailBuilder.withCustomUploadUsage(usagelimit);
				subscriberUsageBuilder.withCustomUploadUsage(actualUsage);
				break;

			case DOWNLOAD:
				quotaProfileDetailBuilder.withCustomDownlodUsage(usagelimit);
				subscriberUsageBuilder.withCustomDownlodUsage(actualUsage);
				break;
			case TOTAL:
				quotaProfileDetailBuilder.withCustomTotalUsage(usagelimit);
				subscriberUsageBuilder.withCustomTotalUsage(actualUsage);
				break;

			case TIME:
				quotaProfileDetailBuilder.withCustomTime(usagelimit);
				subscriberUsageBuilder.withCustomTime(actualUsage);
				break;
		}

		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		SubscriberUsage subscriberUsage = subscriberUsageBuilder.build();
		assertTrue(quotaProfileDetail.isUsageExceeded(subscriberUsage));
	}

	@Test
	public void test_isUsageExceeded_should_return_false_when_custom_usage_not_fould() {
		QuotaProfileDetail.QuotaProfileDetailBuilder quotaProfileDetailBuilder = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1);
		QuotaProfileDetail quotaProfileDetail = quotaProfileDetailBuilder.build();
		assertFalse(quotaProfileDetail.isUsageExceeded(null));
	}

	@Test
	public void test_getUsageKey_should_return_combination_of_quotaPofileId_and_serviceId() {

		QuotaProfileDetail quotaProfileDetail = new QuotaProfileDetail.QuotaProfileDetailBuilder("q1", "testQuotaProfile", "s1", 1).build();

		assertEquals("Usage key should be QuotaProfileId-ServiceId(q1-s1)", "q1-s1", quotaProfileDetail.getUsageKey());

	}

	private static enum UsageType {
		UPLOAD,
		DOWNLOAD,
		TOTAL,
		TIME
	}

*/}
