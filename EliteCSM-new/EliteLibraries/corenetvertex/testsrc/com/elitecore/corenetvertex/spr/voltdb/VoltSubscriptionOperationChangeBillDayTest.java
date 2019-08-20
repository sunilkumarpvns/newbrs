package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Ignore
public class VoltSubscriptionOperationChangeBillDayTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private static InProcessVoltDBServer voltServer;
    private DummyPolicyRepository policyRepository;
    private DummyVoltDBClient voltDBClient;
    private VoltSubscriberDBHelper helper;

    private VoltSubscriptionOperation voltSubscriptionOperation;

    @BeforeClass
    public static void beforeClass() {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        // Add SQL File containing Create Table and Create Procedure query
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        policyRepository = new DummyPolicyRepository();
        voltDBClient = new DummyVoltDBClient(voltServer.getClient());
        MockitoAnnotations.initMocks(this);
        helper = new VoltSubscriberDBHelper();
        voltSubscriptionOperation = new VoltSubscriptionOperation(mock(AlertListener.class), policyRepository, new FixedTimeSource(), null, null);
    }

    private SubscriberProfileData getSubscriberProfile() {
        Timestamp nextBillDateFromDay = getNextBillDateFromDay(1);
        SubscriberProfileData subscriberProfile = new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("1010101010")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withStatus(SubscriberStatus.ACTIVE.name())
                .withPhone("123456").withBillChangeDate(nextBillDateFromDay).withNextBillDate(getNextBillingCycleDateFromBillChangeDate(28, nextBillDateFromDay)).build();


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

    private Timestamp getNextBillDateFromDay(Integer nextBillDate) {
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


    @Test
    public void changeBillDayForSubscriberWhenNextBillingCycleIsProvided() throws Exception {
        SubscriberProfileData subscriberProfileData = getSubscriberProfile();
        helper.insertSubscriber(subscriberProfileData);
        voltSubscriptionOperation.changeBillDay(subscriberProfileData.getSubscriberIdentity(), subscriberProfileData.getNextBillDate(), subscriberProfileData.getBillChangeDate(), voltDBClient);
        SPRInfo expectedSPRInfo = helper.getExpectedSPRInfoForSubscriber(subscriberProfileData.getSubscriberIdentity());
        assertEquals(getNextBillingCycleDateFromBillChangeDate(28,subscriberProfileData.getBillChangeDate()), expectedSPRInfo.getNextBillDate());

    }

    @Test
    public void test_changeBillDay_should_return_null_when_next_bill_date_not_configured() throws Exception {
        SubscriberProfileData subscriberProfile = getSubscriberProfileWithoutNextBillDate();
        helper.insertSubscriber(subscriberProfile);

        voltSubscriptionOperation.changeBillDay(subscriberProfile.getSubscriberIdentity(), null, null, voltDBClient);
        SPRInfo expectedSPRInfo = helper.getExpectedSPRInfoForSubscriber(subscriberProfile.getSubscriberIdentity());
        assertEquals(null, expectedSPRInfo.getNextBillDate());
    }


    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }


    public static class VoltSubscriberDBHelper {

        private Map<String, SPRInfo> sprInfosById;

        public VoltSubscriberDBHelper() {
            this.sprInfosById = new HashMap<>();
        }

        public void insertSubscriber(SubscriberProfileData subscriberProfileData) throws Exception {
            voltServer.runDDLFromString(subscriberProfileData.insertQuery());
            sprInfosById.put(subscriberProfileData.getSubscriberIdentity(), subscriberProfileData.getSPRInfo());
        }

        public SPRInfo getExpectedSPRInfoForSubscriber(String subscriberIdentity) {

            SPRInfo sprInfo = sprInfosById.get(subscriberIdentity);

            if (sprInfo != null) {
                return sprInfo;
            }

            sprInfo = new SPRInfoImpl.SPRInfoBuilder().withSubscriberIdentity(subscriberIdentity)
                    .withGroupIds(Collections.emptyList())
                    .withCui(subscriberIdentity).build();

            return sprInfo;
        }
    }
}
