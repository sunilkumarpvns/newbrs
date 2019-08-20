package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TopUpSubscriptionOperationGetQuotaTopUpSubscriptionTest.class,
    TopUpSubscriptionOperationSubscribeQuotaTopUpByNameTest.class,
    TopUpSubscriptionOpertaionSubscribeQuotaTopUpByIdTest.class,
    TopUpSubscriptionOperationSubscribeQuotaTopUpTest.class,
    TopUpSubscriptionOperationTest.class
})


public class TopUpSubscriptionOperationTestSuite {
    public static final String DS_NAME = "test-DB";

    public static class TopUpSubscriptionDBHelper {

        private TransactionFactory transactionFactory;
        private Map<String, LinkedHashMap<String, Subscription>> quotaTopUpSubscriptions;

        public TopUpSubscriptionDBHelper(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            this.quotaTopUpSubscriptions = new HashMap<String, LinkedHashMap<String, Subscription>>();
        }

        public void createTables() throws Exception {
            executeQuery(SubscriberUsageData.createTableQuery());
            executeQuery(TblmDataBalanceEntity.createTableQuery());
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

        public void insertAddonRecord(SubscriptionData addOnSubscriptionData) throws Exception {

            executeQuery(addOnSubscriptionData.insertQuery());

            LinkedHashMap<String, Subscription> subscribedList = quotaTopUpSubscriptions.get(addOnSubscriptionData.getSubscriberId());

            if (subscribedList == null) {
                subscribedList = new LinkedHashMap<String, Subscription>();
                quotaTopUpSubscriptions.put(addOnSubscriptionData.getSubscriberId(), subscribedList);
            }

            subscribedList.put(addOnSubscriptionData.getSubscriptionId(), addOnSubscriptionData.getQuotaTopUpSubscription());
        }

        public void insertAddonRecords(List<SubscriptionData> addOnSubscriptionDatas) throws Exception {

            for (SubscriptionData addOnSubscriptionData : addOnSubscriptionDatas) {
                insertAddonRecord(addOnSubscriptionData);
            }
        }

        public void dropTables() throws Exception {
            executeQuery(SubscriptionData.dropTableQuery());
            executeQuery(SubscriberUsageData.dropTableQuery());
            executeQuery(TblmDataBalanceEntity.dropTableQuery());

            quotaTopUpSubscriptions.clear();
            getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
        }

        public LinkedHashMap<String, Subscription> getQuotaTopUpsForSubscriber(String subscriberIdentity) {
            return quotaTopUpSubscriptions.containsKey(subscriberIdentity) ? quotaTopUpSubscriptions.get(subscriberIdentity) : new LinkedHashMap<String, Subscription>();
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
    }



    /**
     * This version is created to test expiry scenario that is based on current
     * time
     */
    public static class TopUpSubscriptionOperationExt extends TopUpSubscriptionOperation {

        private long count = 0;

        public TopUpSubscriptionOperationExt(AlertListener alertListener, PolicyRepository policyRepository) {
            super(alertListener, policyRepository, new ABMFOperation(alertListener,policyRepository,2000,200),null, null);
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
