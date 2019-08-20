package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.factory.BoDDataFactory;
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
        BoDSubscriptionOperationTest.class,
        SubscriptionOperationGetBoDSubscriptionTest.class,
        SubscriptionOperationSubscribeBoDByNameTest.class


})


public class BoDSubscriptionOperationTestSuite {
        public static final String DS_NAME = "test-DB";

        public static class BoDSubscriptionDBHelper {

            private TransactionFactory transactionFactory;
            private Map<String, LinkedHashMap<String, Subscription>> bodSubscriptions;

            public BoDSubscriptionDBHelper(TransactionFactory transactionFactory) {
                this.transactionFactory = transactionFactory;
                this.bodSubscriptions = new HashMap<>();
            }

            public void createTables() throws Exception {
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

            public void insertBoDSubscription(SubscriptionData bodSubscriptionData) throws Exception {

                executeQuery(bodSubscriptionData.insertQuery());

                LinkedHashMap<String, Subscription> subscribedList = bodSubscriptions.get(bodSubscriptionData.getSubscriberId());

                if (subscribedList == null) {
                    subscribedList = new LinkedHashMap<>();
                    bodSubscriptions.put(bodSubscriptionData.getSubscriberId(), subscribedList);
                }

                subscribedList.put(bodSubscriptionData.getSubscriptionId(), bodSubscriptionData.getBoDSubscription());
            }

            public void insertBoDRecords(List<SubscriptionData> bodSubscriptionDatas) throws Exception {

                for (SubscriptionData bodSubscription : bodSubscriptionDatas) {
                    insertBoDSubscription(bodSubscription);
                }
            }

            public void dropTables() throws Exception {
                executeQuery(SubscriptionData.dropTableQuery());

                bodSubscriptions.clear();
                getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
            }

            public LinkedHashMap<String, Subscription> getBoDForSubscriber(String subscriberIdentity) {
                return bodSubscriptions.containsKey(subscriberIdentity) ? bodSubscriptions.get(subscriberIdentity) : new LinkedHashMap<>();
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

            public BoDPackage getBoDPkg(){
                BoDData bodData = new BoDDataFactory()
                        .withId("100").withName("BoD").withStatus(PkgStatus.ACTIVE.name())
                        .withMode("LIVE").build();

                return BoDDataFactory.createBoDPkg(bodData, PolicyStatus.SUCCESS);
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
