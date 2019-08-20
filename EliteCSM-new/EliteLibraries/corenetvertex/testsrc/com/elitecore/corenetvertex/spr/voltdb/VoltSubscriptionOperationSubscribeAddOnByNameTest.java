package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.factory.AddOnPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(JUnitParamsRunner.class)
public class VoltSubscriptionOperationSubscribeAddOnByNameTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private static InProcessVoltDBServer voltServer;
    private DummyPolicyRepository policyRepository;
    private DummyVoltDBClient voltDBClient;

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

        voltSubscriptionOperation = new VoltSubscriptionOperation(mock(AlertListener.class), policyRepository, new FixedTimeSource(), null, null);
    }

    @Test
    public void addSubscriptionProvidedByIdSuccessfullyOnValidSubscription() throws Exception {
        String subscriberId = "501";
        long currentTime = System.currentTimeMillis();
        final long startTime = currentTime;
        final long endTime = currentTime + TimeUnit.DAYS.toMillis(1);
        final int status = 2;
        AddOn addon = ceateAddon();
        when(addon.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
        when(addon.getValidity()).thenReturn(1);

        List<Subscription> expectedAddonSubscription = voltSubscriptionOperation.subscribeProductOfferAddOnByName(createSPRInfo(subscriberId), subscriberId,null, addon.getName(),
                status, startTime, endTime,
                null, null, null, voltDBClient);

        assertTrue(Objects.nonNull(expectedAddonSubscription));
    }

    @Test
    @Parameters(value = {"1", "3", "4", "5", "6", "7", "8", "9"})
    public void throwOperationFailedExceptionWhenInvalidSubscriptionStateProvided(
            Integer subscriptionStatusValue) throws Exception {
        final Long startTime = null;
        final Long endTime = null;
        AddOn addOn = ceateAddon();

        expectedException.expect(OperationFailedException.class);
        if (subscriptionStatusValue == 9) {
            expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
        } else {
            expectedException.expectMessage("Invalid subscription status: " + SubscriptionState.fromValue(subscriptionStatusValue).name + " received");
        }
        try {
            voltSubscriptionOperation.subscribeProductOfferAddOnByName(createSPRInfo("101"), "102",null, addOn.getName(), subscriptionStatusValue, startTime, endTime, null, "param1", "param2", voltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        Assert.fail("should throw OperationFailedException");
    }

    @SuppressWarnings("unused")
    private Object dataProviderFor_startTime_endTime() {

        Long time = System.currentTimeMillis() +  TimeUnit.MINUTES.toMillis(10); /// future time

        return new Object[][] {
                {
                        time, time
                },
                {
                        time + TimeUnit.MINUTES.toMillis(1), time
                }
        };
    }

    @Test
    @Parameters(method = "dataProviderFor_startTime_endTime")
    public void throwOperationFailedExceptionWhenStarttimeIsMoreOrEqualEndtime(
            Long startTime, Long endTime) throws Exception {

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Start time(" + new Timestamp(startTime).toString() + ") is more or equal to end time("
                + new Timestamp(endTime).toString() + ")");

        final Integer state = null;
        AddOn addOn = ceateAddon();

        try {
            voltSubscriptionOperation.subscribeProductOfferAddOnByName(createSPRInfo("101"), "102",null, addOn.getName(), state, startTime, endTime, null, "param1", "param2", voltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            getLogger().debug("TEST", e.getMessage());
            throw e;
        }

        Assert.fail("should throw OperationFailedException");
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }

    private AddOn ceateAddon() {

        AddOn addOn = spy(AddOnPackageFactory.create(UUID.randomUUID().toString(), "name-" + UUID.randomUUID().toString()));
        policyRepository.addAddOn(addOn);
        return addOn;
    }

    private SPRInfo createSPRInfo(String subscriberId){
        SPRInfoImpl sprInfoImpl = new SPRInfoImpl();
        sprInfoImpl.setSubscriberIdentity(subscriberId);
        return sprInfoImpl;
    }
}
