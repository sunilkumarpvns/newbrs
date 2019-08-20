package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.store.AddOnProductOfferStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class SubscriptionOperationUpdateFnFGroupTest {

    private DummyPolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    @Mock
    ProductOfferStore productOfferStore;
    @Mock
    AddOnProductOfferStore addOnProductOfferStore;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SubscriptionOperationTestSuite.SubscriptionDBHelper helper;

    private String dataSourceId = UUID.randomUUID().toString();
    private HibernateSessionFactory hibernateSessionFactory;

    @BeforeClass
    public static void globalSetup() throws ClassNotFoundException{
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        policyRepository = spy(new DummyPolicyRepository());
        String url = "jdbc:h2:mem:"+ dataSourceId +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
        transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();
        transactionFactory = spy(transactionFactory);

        helper = new SubscriptionOperationTestSuite.SubscriptionDBHelper(transactionFactory);
        helper.createTables();

        ProductOffer productOffer = helper.getProductOffer();
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.addOn()).thenReturn(addOnProductOfferStore);
        when(addOnProductOfferStore.byId(productOffer.getId())).thenReturn(productOffer);

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", url);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

    }

    private SubscriptionOperationTestSuite.SubscriptionOperationExt getNewSubscriptionOperation() {
        return new SubscriptionOperationTestSuite.SubscriptionOperationExt(mock(AlertListener.class), policyRepository);
    }

    private SPRInfo createSprInfo(String subscriberId){
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        return sprInfo;
    }

    private Subscription createSubscrption(String subscriptionId, String subscriberIdentity){
        return new Subscription(subscriptionId,
                subscriberIdentity, "pkgId", helper.getProductOffer().getId(), null, null, SubscriptionState.STARTED, 100,
                SubscriptionType.RO_ADDON, SubscriptionUtil.createMetaData("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[\"456852154\",\"5684216845\"]}}",null,null),
                null, null);
    }

    private void createAndSaveSubscriptionData(String subscriptionId, String subscriberIdentity){
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setSubscriptionId(subscriberIdentity);
        subscriptionData.setSubscriptionId(subscriptionId);
        subscriptionData.setProductOfferId(helper.getProductOffer().getId());
        subscriptionData.setStartTime(new Timestamp(System.currentTimeMillis()-3600000).toString());
        subscriptionData.setEndTime(new Timestamp(System.currentTimeMillis()+3600000).toString());
        subscriptionData.setStatus(Integer.toString(SubscriptionState.STARTED.getVal()));
        subscriptionData.setMetadata("{\"fnFGroup\":{\"name\":\"fnfGroup1\",\"members\":[\"5684216845\"]}}");
        subscriptionData.setPriority("100");
        subscriptionData.setSubscriptionTime(new Timestamp(System.currentTimeMillis()-3600000).toString());
        subscriptionData.setUsageresetDate(new Timestamp(System.currentTimeMillis()+3600000).toString());

        hibernateSessionFactory.save(subscriptionData);
    }

    public class OperationFailedExceptionWhen {
        @Test
        public void transactionFactoryIsDead() throws OperationFailedException{
            SubscriptionOperationImpl operation = getNewSubscriptionOperation();
            SPRInfo sprInfo = createSprInfo("identity");
            Subscription subscription = createSubscrption("subscription1", sprInfo.getSubscriberIdentity());

            doReturn(false).when(transactionFactory).isAlive();

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Datasource not available"); // part of message

            createAndSaveSubscriptionData("subscription1", sprInfo.getSubscriberIdentity());
            operation.updateFnFGroup(sprInfo, subscription, transactionFactory);
        }

        @Test
        public void activeSubscriptionNotFound() throws OperationFailedException{
            SubscriptionOperationImpl operation = getNewSubscriptionOperation();
            SPRInfo sprInfo = createSprInfo("identity");
            Subscription subscription = createSubscrption("subscription1", sprInfo.getSubscriberIdentity());

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Active subscription not found with ID: " + subscription.getId());

            operation.updateFnFGroup(sprInfo, subscription, transactionFactory);
        }

        @Test
        public void subscriberIdInRequestAndSubscriberIdInSubscriptionDoNotMatch() throws OperationFailedException{
            SubscriptionOperationImpl operation = getNewSubscriptionOperation();
            SPRInfo sprInfo = createSprInfo("id_id");
            Subscription subscription = createSubscrption("subscription1", "random");

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("SubscriberId(" + sprInfo.getSubscriberIdentity() + ") " +
                    "and susbcriptionId(" + subscription.getId() + ") are not related"); // part of message

            createAndSaveSubscriptionData("subscription1", sprInfo.getSubscriberIdentity());
            operation.updateFnFGroup(sprInfo, subscription, transactionFactory);
        }
    }

    @Test
    public void updateFnFgroupSuccessful() throws OperationFailedException{
        SubscriptionOperationImpl operation = getNewSubscriptionOperation();
        SPRInfo sprInfo = createSprInfo("identity");
        Subscription subscription = createSubscrption("subscription1", sprInfo.getSubscriberIdentity());

        createAndSaveSubscriptionData("subscription1", sprInfo.getSubscriberIdentity());
        operation.updateFnFGroup(sprInfo, subscription, transactionFactory);

        SubscriptionData subscriptionData = (SubscriptionData) hibernateSessionFactory.get(SubscriptionData.class).get(0);
        Assert.assertEquals(SubscriptionUtil.createMetaString(subscription.getMetadata(),null,null),subscriptionData.getMetadata());
    }

    @After
    public void tearDownConnection() throws Exception {
        helper.dropTables();
        transactionFactory.getConnection().close();
        DerbyUtil.closeDerby(dataSourceId);

        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }
}
