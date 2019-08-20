package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberMode;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl.SPRInfoBuilder;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.data.TestSubscriberData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

@RunWith(Suite.class)
@SuiteClasses({
		SubscriberRepositoryTest.class,
		SubscriberRepositoryTestSubscriberEnableTest.class,
		SubscriberRepositoryTestSubscriberDisableTest.class,
		SubscriberRepositoryBasePackageUsageProvisionWhileAddProfileTest.class,
		SubscriberRepositoryBasePackageUsageProvisionWhileChangeDataPackageTest.class,
		SubscriberRepositorySubscribeBoDPackageTest.class

})
public class SubscriberRepositoryTestSuite {

	public static final UMConfigurationImpl UM_CONFIG_WITH_ZERO_BATCH_SIZE =  new UMConfigurationImpl();
	public static class SPROperationTestHelper {

		private TransactionFactory transactionFactory;
		private Map<String, SPRInfo> sprInfosById;

		public SPROperationTestHelper(TransactionFactory transactionFactory) {
			this.transactionFactory = transactionFactory;
			this.sprInfosById = new HashMap<String, SPRInfo>();
		}

		public void createProfileTable() throws Exception {
			executeQuery(SubscriberProfileData.createTableQuery());
			executeQuery(TestSubscriberData.createTableQuery());
			executeQuery(TblmDataBalanceEntity.createTableQuery());
			executeQuery(TblmMonetaryBalanceEntity.createTableQuery());
		}

		public void createUsageTable() throws Exception {
			executeQuery(SubscriberUsageData.createTableQuery());
		}

		public void executeQuery(String query) throws Exception {
			Transaction transaction = transactionFactory.createTransaction();

			try {
				transaction.begin();
				getLogger().debug(getClass().getSimpleName(), "Query: " + query);
				transaction.prepareStatement(query).execute();

			} finally {
				transaction.end();
			}
		}

		public void insertProfile(List<SubscriberProfileData> datas) throws Exception {
			for (SubscriberProfileData data : datas) {
				insertProfile(data);
			}
		}

		public void insertProfile(SubscriberProfileData data) throws Exception {
			executeQuery(data.insertQuery());
            SPRInfoImpl sprInfo = data.getSPRInfo();
            sprInfo.setSPRGroupIds(Collections.emptyList());
            sprInfosById.put(data.getSubscriberIdentity(), sprInfo);
		}

		public SPRInfo getExpectedSPRInfoForSubscriber(String subscriberIdentity) {

			SPRInfo sprInfo = sprInfosById.get(subscriberIdentity);

			if (sprInfo != null) {
				return sprInfo;
			}

			sprInfo = new SPRInfoBuilder().withSubscriberIdentity(subscriberIdentity)
					.withGroupIds(Collections.emptyList())
					.withCui(subscriberIdentity).build();

			sprInfo.setUnknownUser(true);
			sprInfo.setStatus("ANONYMOUS");

			return sprInfo;
		}

		public void dropProfileTables() throws Exception {
			executeQuery(SubscriberProfileData.dropTableQuery());
			executeQuery(TestSubscriberData.dropTableQuery());
			executeQuery(TblmMonetaryBalanceEntity.dropTableQuery());
		}

		public void dropUsageTable() throws Exception {
		    executeQuery(SubscriberUsageData.dropTableQuery());
        }
	}

	public static class UMConfigurationImpl implements UMconfiguration {

		@Override
		public int getBatchSize() {
			return 0;
		}

		@Override
		public int getBatchQueryTimeout() {
			return 0;
		}

		@Override
		public void toString(IndentingToStringBuilder builder) {

		}
	}

	public static class ABMFConfigurationImpl implements ABMFconfiguration {

		@Override
		public int getBatchSize() {
			return 0;
		}

		@Override
		public int getBatchQueryTimeout() {
			return 0;
		}

		@Override
		public int getQueryTimeout() {
			return 0;
		}

		@Override
		public void toString(IndentingToStringBuilder builder) {

		}
	}

	public static SPRInfo getSubscriberProfile(SubscriberMode subscriberMode, String packageName, String subscriberIdentity) throws OperationFailedException {
		SubscriberProfileData data = getSubscriberProfileData(subscriberIdentity);
		SPRInfo info = data.getSPRInfo();
		info.setProductOffer(packageName);
		info.setSubscriberMode(subscriberMode);
		return info;
	}

	public static SubscriberProfileData getSubscriberProfileData(String subscriberIdentity) {

		SubscriberProfileData data1 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity(subscriberIdentity)
				.withImsi("1234")
				.withMsisdn("9797979110")
				.withUserName("user110")
				.withPassword("user110")
				.withPhone("123110")
				.withBirthdate(new Timestamp(new Date().getTime()))
				.withExpiryDate(new Timestamp(System.currentTimeMillis()))
				.build();

		return data1;
	}

}
