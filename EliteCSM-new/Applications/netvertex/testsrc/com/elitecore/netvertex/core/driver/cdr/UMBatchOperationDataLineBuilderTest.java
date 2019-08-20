package com.elitecore.netvertex.core.driver.cdr;

import static org.mockito.Mockito.mock;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.UMBatchOperation;
import com.elitecore.corenetvertex.spr.UMBatchOperation.BatchOperationData;

@SuppressWarnings("deprecation")
public class UMBatchOperationDataLineBuilderTest {
	
	private static final String SUBSCRIPTION_ID = "subscription1";
	private static final String ID = "1";
	private static final long CONSTANT_TIME = 1504105520090l;
	private static final String PACKAGE_ID = "PACKAGE_ID";
	private static final String PRODUCT_OFFER_ID = "PRODUCT_OFFER_ID";
	private static final String QUOTA_ID = "QUOTA_ID";
	private static final String SERVICE_ID = "SERVICE_ID";
	private static final String SUBSCRIBER_IDENTITY = "007";
	private static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yy hh.mm.ss.S a";
	private static final String REPLACE = "REPLACE";
	private static final String ADD_TO_EXISTING = "ADD_TO_EXISTING";
	private static final String INSERT = "INSERT";
	
	private final SimpleDateFormat timestampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);

	@Test
	public void test_csvLineBuilderForUM_returns_expectedRecord() {
		
		TimeSource timeSource = new TimeSource() {
			
			@Override
			public long currentTimeInMillis() {
				return CONSTANT_TIME;
			}
		};
		
		BatchOperationData batchOperationData = createBatchOperationData();
		List<String> actualCSVRecords = new UMCSVDataBuilder().getLineBuilder(timeSource).getCSVRecords(batchOperationData);
		
		List<String> expectedCSVRecord = createExpectedCSVRecord(timeSource,batchOperationData);
		
		System.out.println("Expected: " + expectedCSVRecord);
		System.out.println("Actual:   " + actualCSVRecords);
		
		ReflectionAssert.assertLenientEquals(expectedCSVRecord, actualCSVRecords);
	}

	private List<String> createExpectedCSVRecord(TimeSource timeSource, BatchOperationData batchOperationData) {

		SubscriberUsage usage = batchOperationData.getUsage();
		 
		return Arrays.asList(usage.getId() + CommonConstants.COMMA + usage.getSubscriberIdentity() + CommonConstants.COMMA 
				+ usage.getPackageId() + CommonConstants.COMMA + usage.getSubscriptionId() + CommonConstants.COMMA
				+ usage.getQuotaProfileId() + CommonConstants.COMMA + usage.getServiceId() + CommonConstants.COMMA 
				
				+ usage.getBillingCycleTotal() + CommonConstants.COMMA + usage.getBillingCycleUpload() + CommonConstants.COMMA
				+ usage.getBillingCycleDownload() + CommonConstants.COMMA + usage.getBillingCycleTime() + CommonConstants.COMMA
				
				+ usage.getDailyTotal() + CommonConstants.COMMA + usage.getDailyUpload() + CommonConstants.COMMA
				+ usage.getDailyDownload() + CommonConstants.COMMA + usage.getDailyTime() + CommonConstants.COMMA
				
				+ usage.getWeeklyTotal() + CommonConstants.COMMA + usage.getWeeklyUpload() + CommonConstants.COMMA
				+ usage.getWeeklyDownload() + CommonConstants.COMMA + usage.getWeeklyTime() + CommonConstants.COMMA
				
				+ usage.getCustomTotal() + CommonConstants.COMMA + usage.getCustomUpload() + CommonConstants.COMMA
				+ usage.getCustomDownload() + CommonConstants.COMMA + usage.getCustomTime() + CommonConstants.COMMA
				
				+ usage.getBillingCycleResetTime() + CommonConstants.COMMA + usage.getDailyResetTime() + CommonConstants.COMMA 
				+ usage.getWeeklyResetTime() + CommonConstants.COMMA + usage.getCustomResetTime() + CommonConstants.COMMA
				
				+ addOperationType(batchOperationData) + CommonConstants.COMMA 
				+ timestampFormat.format(new Timestamp(timeSource.currentTimeInMillis())));
	}

	private BatchOperationData createBatchOperationData() {

		Random random = new Random();
		return new UMBatchOperation(mock(TransactionFactory.class), null, null, null, 12, 1000, null).new BatchOperationData(
				new SubscriberUsage.SubscriberUsageBuilder(ID, SUBSCRIBER_IDENTITY, SERVICE_ID, QUOTA_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
				.withBillingCycleUsage(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong())
				.withDailyUsage(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong())
				.withWeeklyUsage(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong())
				.withCustomUsage(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong())
				.withBillingCycleResetTime(random.nextLong())
				.withDailyResetTime(random.nextLong())
				.withWeeklyResetTime(random.nextLong())
				.withCustomResetTime(random.nextLong())
				.withSubscriptionId(SUBSCRIPTION_ID).build()
				, SUBSCRIBER_IDENTITY, BatchOperationData.INSERT);
		
	}
	
	private String addOperationType(BatchOperationData batchOperationData) {
		
		int operation = batchOperationData.getOperation();
		
		if (operation == 0) {
			return INSERT;
		} else if (operation == 1) {
			return ADD_TO_EXISTING;
		} else if (operation == 2) {
			return REPLACE;
		}
		
		return null;
	}
}
