package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.DerbyUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriberChangeBillDayTest {

    private DummyTransactionFactory transactionFactory;
    private SubscriberRepositoryTestSuite.SPROperationTestHelper helper;

    private PolicyRepository policyRepository;
    private String dataSourceId = UUID.randomUUID().toString();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String url = "jdbc:derby:memory:"+ dataSourceId +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
        policyRepository = mock(PolicyRepository.class);

        transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

        helper = new SubscriberRepositoryTestSuite.SPROperationTestHelper(transactionFactory);
        helper.createProfileTable();
    }

    private SubscriberProfileData getSubscriberProfile() {
        SubscriberProfileData subscriberProfile = new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("1010101010")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withStatus(SubscriberStatus.ACTIVE.name())
                .withPhone("123456").withBillChangeDate(getBillChangeDate(1)).withNextBillDate(getNextBillingCycleDateFromBillChangeDate(28, getBillChangeDate(1))).build();


        return subscriberProfile;
    }

    private SubscriberProfileData getSubscriberProfileWithoutNextBillDate() {
        SubscriberProfileData subscriberProfile = new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("1010101010")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withStatus(SubscriberStatus.ACTIVE.name())
                .withPhone("123456").build();


        return subscriberProfile;
    }

    @Test
    public void test_changeBillDay_should_update_next_bill_date_when_provided() throws Exception {
        SubscriberProfileData subscriberProfile = getSubscriberProfile();
        helper.insertProfile(subscriberProfile);
        SubscriptionOperationImpl operation = getNewSubscriptionOperation();

        Timestamp billChangeDate = getBillChangeDate(1);
        operation.changeBillDay(subscriberProfile.getSubscriberIdentity(), getNextBillingCycleDateFromBillChangeDate(28,billChangeDate), billChangeDate, transactionFactory);
        SPRInfo expectedSPRInfo = helper.getExpectedSPRInfoForSubscriber(subscriberProfile.getSubscriberIdentity());
        assertEquals(getNextBillingCycleDateFromBillChangeDate(28,billChangeDate), expectedSPRInfo.getNextBillDate());
    }


    @Test
    public void test_changeBillDay_should_return_null_when_next_bill_date_not_configured() throws Exception {
        SubscriberProfileData subscriberProfile = getSubscriberProfileWithoutNextBillDate();
        helper.insertProfile(subscriberProfile);
        SubscriptionOperationImpl operation = getNewSubscriptionOperation();

        operation.changeBillDay(subscriberProfile.getSubscriberIdentity(), null, null, transactionFactory);
        SPRInfo expectedSPRInfo = helper.getExpectedSPRInfoForSubscriber(subscriberProfile.getSubscriberIdentity());
        assertEquals(null, expectedSPRInfo.getNextBillDate());
    }

    private SubscriptionOperationTestSuite.SubscriptionOperationExt getNewSubscriptionOperation() {
        return new SubscriptionOperationTestSuite.SubscriptionOperationExt(mock(AlertListener.class), policyRepository);
    }


    @Test
    public void test_changeBillDay_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        SubscriptionOperationImpl operation = getNewSubscriptionOperation();
        SubscriberProfileData subscriberProfile = getSubscriberProfileWithoutNextBillDate();

        setUpMockTransactionFactoryDead();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Datasource not available"); // part of message

        try {
            Timestamp billChangeDate = getBillChangeDate(10);
            operation.changeBillDay(subscriberProfile.getSubscriberIdentity(), getNextBillingCycleDateFromBillChangeDate(28,billChangeDate) , billChangeDate,  transactionFactory);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }


    @Test
    public void test_changeBillDay_should_generate_QUERYTIMEOUT_alert() throws Exception {
        SubscriptionOperationImpl operation = getNewSubscriptionOperation();
        SubscriberProfileData subscriberProfile = getSubscriberProfile();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("query timeout");

        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();

        try {
            operation.changeBillDay(subscriberProfile.getSubscriberIdentity(), subscriberProfile.getNextBillDate(), subscriberProfile.getBillChangeDate(), transactionFactory);
        } catch (OperationFailedException e) {
            throw e;
        }

        fail("should throw OperationFailedException");
    }


    @org.junit.After
    public void afterDropTables() throws Exception {
        helper.dropProfileTables();
        transactionFactory.getConnection().close();
        DerbyUtil.closeDerby(dataSourceId);
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalert() throws Exception {
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        return factory;
    }

    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }


    private Timestamp getBillChangeDate(Integer nextBillDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (nextBillDate <= calendar.get(Calendar.DAY_OF_MONTH)) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, nextBillDate);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);

        return new Timestamp(calendar.getTimeInMillis());
    }

    private Timestamp getNextBillingCycleDateFromBillChangeDate(Integer billDate, Timestamp billChangeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(billChangeDate.getTime());

        if (calendar.get(Calendar.DAY_OF_MONTH) >= billDate) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, billDate);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);

        return new Timestamp(calendar.getTimeInMillis());
    }



}
