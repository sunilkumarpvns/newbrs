package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;


@RunWith(Suite.class)
@SuiteClasses({
	
	SubscriptionOperationTest.class,
	SubscriptionOperationSubscribeAddOnByNameTest.class,
	SubscriptionOperationSubscribeAddOnbyIdTest.class,
	SubscriptionOperationGetAddOnSubscriptionTest.class,
	SubscriptionOperationUpdateSubscriptionTest.class
	
})
public class SubscriptionOperationTestSuite {
	public static final String DS_NAME = "test-DB";

	public static class SubscriptionDBHelper {

		private TransactionFactory transactionFactory;
		private Map<String, LinkedHashMap<String, Subscription>> addonSubscriptions;

		public SubscriptionDBHelper(TransactionFactory transactionFactory) {
			this.transactionFactory = transactionFactory;
			this.addonSubscriptions = new HashMap<String, LinkedHashMap<String, Subscription>>();
		}

		public void createTables() throws Exception {
            executeQuery(SubscriberUsageData.createTableQuery());
			executeQuery(SubscriptionData.createTableQuery());
		}

		private void executeQuery(String query) throws Exception {
			Transaction transaction = transactionFactory.createTransaction();
			try {
				getLogger().debug("TEST", "Query: " + query);
				transaction.begin();
				transaction.prepareStatement(query).execute();
			} finally {
				transaction.end();
			}
		}

		public void insertAddonRecord(SubscriptionData AddOnSubscriptionData) throws Exception {

			executeQuery(AddOnSubscriptionData.insertQuery());

			LinkedHashMap<String, Subscription> subscribedList = addonSubscriptions.get(AddOnSubscriptionData.getSubscriberId());

			if (subscribedList == null) {
				subscribedList = new LinkedHashMap<String, Subscription>();
				addonSubscriptions.put(AddOnSubscriptionData.getSubscriberId(), subscribedList);
			}

			subscribedList.put(AddOnSubscriptionData.getSubscriptionId(), AddOnSubscriptionData.getAddonSubscription());
		}

		public void insertAddonRecords(List<SubscriptionData> AddOnSubscriptionDatas) throws Exception {

			for (SubscriptionData AddOnSubscriptionData : AddOnSubscriptionDatas) {
				insertAddonRecord(AddOnSubscriptionData);
			}
		}

		public void dropTables() throws Exception {
			executeQuery(SubscriptionData.dropTableQuery());
			executeQuery(SubscriberUsageData.dropTableQuery());

			addonSubscriptions.clear();
			getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
		}

		public LinkedHashMap<String, Subscription> getAddonsForSubscriber(String subscriberIdentity) {
			return addonSubscriptions.containsKey(subscriberIdentity) ? addonSubscriptions.get(subscriberIdentity) : new LinkedHashMap<String, Subscription>();
		}

		public SubscriptionState getCurrentSubscriptionStateForSubscriptionId(String subscriptionID) throws Exception {
			
			Class<SubscriptionData> clazz = SubscriptionData.class;
			
			Table table = clazz.getAnnotation(Table.class);
			
			String query = "select STATUS from " + table.name() 
					+ " where SUBSCRIPTION_ID='" + subscriptionID + "'";
			
			Transaction transaction = transactionFactory.createTransaction();
			ResultSet resultSet = null;
			try {
				getLogger().debug("TEST", "Query: " + query);
				transaction.begin();
				resultSet = transaction.statement().executeQuery(query);
				
				if (resultSet.next()) {
					int statusStr = resultSet.getInt("STATUS");
					getLogger().debug("TESt", "Subscription status from DB is" +  statusStr);
					return SubscriptionState.fromValue(statusStr);
				}
				return null;
			} finally {
				closeQuietly(resultSet);
				transaction.end();
			}
		}

		public ProductOffer getProductOffer(){
			ProductOfferData productOfferData = new ProductOfferDataFactory()
					.withId("100").withName("Addon").withStatus(PkgStatus.ACTIVE.name())
					.withMode("LIVE").withType("ADDON")
					.withDataServicePkgId(null).build();

			return ProductOfferDataFactory.createProductOffer(productOfferData, PolicyStatus.SUCCESS,null);
		}
	}
	
	
	
	/**
	 * This version is created to test expiry scenario that is based on current
	 * time
	 */
	public static class SubscriptionOperationExt extends SubscriptionOperationImpl {
		
		private long count = 0;
		
		public SubscriptionOperationExt(AlertListener alertListener, PolicyRepository policyRepository) {
			super(alertListener, policyRepository, null, null, null, null, null);
		}

		@Override
		protected long getCurrentTime() {

			Calendar customCalender = Calendar.getInstance();
			customCalender.set(2015, 00, 01, 01, 01, 01);
			return customCalender.getTimeInMillis();
		}
		
		@Override
		String getNextSubscriptionId() {
			return String.valueOf(++count);
		}
		
		public String getLastSubscriptionID() {
			return String.valueOf(count);
		}
	}
	
}
