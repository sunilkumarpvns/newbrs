package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.TestSubscriberCache;
import com.elitecore.corenetvertex.spr.TestSubscriberEnabledSPInterface;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class VoltSPRPurgeProfileTest {
    private static final String DS_NAME = "test-DB";
    private static InProcessVoltDBServer voltServer;

    private SubscriberProfileData profileData1;
    private SubscriberProfileData profileData2;
    private SubscriberProfileData profileData3;
    private SubscriberProfileData profileData4;
    private VoltSubscriberRepositoryImpl voltSubscriberRepository;
    @Mock
    PolicyRepository policyRepository = mock(PolicyRepository.class);
    @Mock
    TransactionFactory taTransactionFactory;
    private TestSubscriberEnabledSPInterface testSubscriberEnabledSPInterface;

    @Mock private AlertListener alertListener;
    private DummyVoltDBClient dummyVoltDBClient;

    @Mock TestSubscriberCache testSubscriberCache;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dummyVoltDBClient = new DummyVoltDBClient(voltServer.getClient());
        profileData1 = createProfile1();
        profileData2 = createProfile2();
        profileData3 = createProfile3();
        profileData4 = createProfile4();
        when(testSubscriberCache.exists(profileData1.getSubscriberIdentity())).thenReturn(true);
        testSubscriberEnabledSPInterface = new TestSubscriberEnabledSPInterface(new VoltDBSPInterface(alertListener, dummyVoltDBClient, new FixedTimeSource()),
                policyRepository, testSubscriberCache);
        voltServer.runDDLFromString(profileData1.insertQuery());
        voltServer.runDDLFromString(profileData2.insertQuery());
        voltServer.runDDLFromString(profileData3.insertQuery());
        voltServer.runDDLFromString(profileData4.insertQuery());
        when(taTransactionFactory.isAlive()).thenReturn(true);
        voltSubscriberRepository = new VoltSubscriberRepositoryImpl(testSubscriberEnabledSPInterface,
                "id", "name", new DummyVoltDBClient(voltServer.getClient()), null,
                policyRepository, new ArrayList<>(), SPRFields.SUBSCRIBER_IDENTITY, null,
                new VoltUMOperation(null, policyRepository,
                        new FixedTimeSource(), null, null), new FixedTimeSource(),"INR",null, null, null);



    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SubscriberProfileData createProfile1() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("myUser")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }

    private SubscriberProfileData createProfile2() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("Id1")
                .withImsi("2345")
                .withMsisdn("9898989898")
                .withUserName("user2")
                .withPassword("user2")
                .withPhone("123456")
                .build();
    }

    private SubscriberProfileData createProfile3() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("Id2")
                .withImsi("7894")
                .withMsisdn("9999999999")
                .withUserName("user3")
                .withPassword("user3")
                .withPhone("123456")
                .build();
    }

    private SubscriberProfileData createProfile4() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("Id3")
                .withImsi("7894")
                .withMsisdn("9999999999")
                .withUserName("user3")
                .withPassword("user3")
                .withPhone("123456")
                .build();
    }

    @Test
    public void throwsOperationFailedExceptionWhenSubscriberNotFound() throws OperationFailedException {
        profileData1 = createProfile1();
        voltSubscriberRepository.addTestSubscriber(profileData1.getSubscriberIdentity());
        expectedException.expect(OperationFailedException.class);
        voltSubscriberRepository.purgeProfile(profileData1.getSubscriberIdentity(), null);
    }

    @Test
    public void testsubscriberIsPurged() throws OperationFailedException, InvocationTargetException, IllegalAccessException {
        profileData1 = createProfile1();
        voltSubscriberRepository.addTestSubscriber(profileData1.getSubscriberIdentity());
        voltSubscriberRepository.markForDeleteProfile(profileData1.getSubscriberIdentity(), null);
        int result = voltSubscriberRepository.purgeProfile(profileData1.getSubscriberIdentity(), null);
        Assert.assertEquals(1, result);
    }

    @Test
    public void testMultipleSubscribersArePurged() throws OperationFailedException {
        voltSubscriberRepository.addTestSubscriber("Id1");
        voltSubscriberRepository.addTestSubscriber("Id2");
        voltSubscriberRepository.markForDeleteProfile("Id1", null);
        voltSubscriberRepository.markForDeleteProfile("Id2", null);
        Map<String, Integer> result = voltSubscriberRepository.purgeProfile(Arrays.asList("Id1", "Id2"), null);
        Map<String, Integer> expectedResultMap = new HashMap<>();
        expectedResultMap.put("Id1", new Integer(1));
        expectedResultMap.put("Id2", new Integer(1));
        Assert.assertEquals(expectedResultMap, result);
    }

    @Test
    public void testSubscribersWhichAreNotFoundAreNotPurged() throws OperationFailedException {
        voltSubscriberRepository.addTestSubscriber("Id2");
        voltSubscriberRepository.markForDeleteProfile("Id2", null);
        Map<String, Integer> result = voltSubscriberRepository.purgeProfile(Arrays.asList("Id1", "Id2"), null);
        Map<String, Integer> expectedResultMap = new HashMap<>();
        expectedResultMap.put("Id1", new Integer(-1));
        expectedResultMap.put("Id2", new Integer(1));
        Assert.assertEquals(expectedResultMap, result);
    }

    @Test
    public void testAllSubscribersArePurged() throws OperationFailedException {
        voltSubscriberRepository.addTestSubscriber("Id1");
        voltSubscriberRepository.addTestSubscriber("Id2");
        voltSubscriberRepository.addTestSubscriber("Id3");
        voltSubscriberRepository.markForDeleteProfile("Id1", null);
        voltSubscriberRepository.markForDeleteProfile("Id2", null);
        voltSubscriberRepository.markForDeleteProfile("Id3", null);
        Map<String, Integer> result = voltSubscriberRepository.purgeAllProfile(null);
        Map<String, Integer> expectedResultMap = new HashMap<>();
        expectedResultMap.put("Id1", new Integer(1));
        expectedResultMap.put("Id2", new Integer(1));
        expectedResultMap.put("Id3", new Integer(1));
        Assert.assertEquals(expectedResultMap, result);
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }
}
