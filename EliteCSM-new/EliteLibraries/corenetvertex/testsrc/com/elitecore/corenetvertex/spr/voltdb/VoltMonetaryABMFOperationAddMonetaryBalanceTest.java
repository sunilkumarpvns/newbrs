package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.MonetaryBalanceBuilder;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.Objects;

import static com.elitecore.corenetvertex.spr.voltdb.VoltMonetaryABMFOperation.ADD_MONETARY_BALANCE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE;
import static com.elitecore.corenetvertex.spr.voltdb.VoltMonetaryABMFOperation.MONETARY_BALANCE_ADD_STORED_PROCEDURE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
public class VoltMonetaryABMFOperationAddMonetaryBalanceTest {

    private static InProcessVoltDBServer voltServer;

    private VoltMonetaryABMFOperation monetaryABMFOperation;
    private static final double DELTA = 0.000000001;
    private long currentTime = System.currentTimeMillis();
    private static final String remark = "test-remark";
    private static final String requestIpAddress = "0:0:0:0";
    private static final String MODULE = "VOLT-MON-ABMF-OPR";
    private static final long expiryDate = 1551398400000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");
    }

    @Before
    public void setUp() throws Exception {
        monetaryABMFOperation = new VoltMonetaryABMFOperation(null, null, new FixedTimeSource(), null);
    }

    @Test
    public void testAddMonetaryBalanceWillOnlyProvisionMonetaryBalanceIfExpiryDataIsNull() throws Exception {
        String subscriberId = "Subscriber2";
        String id = "ID2";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        DummyVoltDBClient dummyVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addMonetaryBalance(subscriberId, addMonetaryBalance, remark, requestIpAddress, null, dummyVoltDBClient);
        verify(dummyVoltDBClient, times(1)).callProcedure(eq(MONETARY_BALANCE_ADD_STORED_PROCEDURE), eq(subscriberId), any(), any(), any(), any());
    }


    @Test
    public void testAddMonetaryBalanceWillProvisionMonetaryBalanceAndUpdateSubscriberProfile() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID1";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        DummyVoltDBClient dummyVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        EnumMap<SPRFields, String> updateProfile = createSPRFieldMap(expiryDate);
        monetaryABMFOperation.addMonetaryBalance(subscriberId, addMonetaryBalance, remark, requestIpAddress, updateProfile, dummyVoltDBClient);
        verify(dummyVoltDBClient, times(1)).callProcedure(eq(ADD_MONETARY_BALANCE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE), eq(subscriberId), any(), any(), any(), any(), any());
    }


    @Test(expected = OperationFailedException.class)
    public void voltDBClientGenerateOperationFaileddException() throws Exception {
        VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
        when(spiedVoltDBClient.isAlive()).thenReturn(false);

        String subscriberId = "Subscriber3";
        String id = "ID3";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        EnumMap<SPRFields, String> updateProfile = createSPRFieldMap(expiryDate);
        monetaryABMFOperation.addMonetaryBalance(subscriberId, addMonetaryBalance, remark, requestIpAddress, updateProfile, spiedVoltDBClient);

    }

    @Test
    public void voltDBClientGenerateIOException() throws Exception {


        String subscriberId = "Subscriber4";
        String id = "ID4";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;

        VoltDBClient spiedVoltDBClient = mock(VoltDBClient.class);
        when(spiedVoltDBClient.isAlive()).thenReturn(true);
        doThrow(IOException.class).when(spiedVoltDBClient).callProcedure(Mockito.anyString(), any(), any(), any(), any(), any());
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        expectedException.expect(OperationFailedException.class);
        monetaryABMFOperation.addMonetaryBalance(subscriberId, addMonetaryBalance, remark, requestIpAddress, null, spiedVoltDBClient);
    }

    @Test
    public void voltDBClientGenerateProcCallException() throws Exception {


        String subscriberId = "Subscriber5";
        String id = "ID5";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;

        VoltDBClient spiedVoltDBClient = mock(VoltDBClient.class);
        when(spiedVoltDBClient.isAlive()).thenReturn(true);
        doThrow(ProcCallException.class).when(spiedVoltDBClient).callProcedure(Mockito.anyString(), any(), any(), any(), any(), any());
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        expectedException.expect(OperationFailedException.class);
        monetaryABMFOperation.addMonetaryBalance(subscriberId, addMonetaryBalance, remark, requestIpAddress, null, spiedVoltDBClient);
    }

    private MonetaryBalanceBuilder createMonetaryBalanceForTesting(String subscriberId, String id, double initialBalance,
                                                                   double availableBalance, double totalReservation) {
        MonetaryBalanceBuilder monetaryBalanceBuilder = new MonetaryBalanceBuilder(id, subscriberId).withServiceId("All-Service").
                withInitialBalance(initialBalance).
                withAvailableBalance(availableBalance).withTotalReservation(totalReservation).withCurrency("INR").withValidFromDate(1530642600000l)
                .withValidToDate(2161794600000l).withCreditLimitUpdateTime(currentTime + 60000);
        return monetaryBalanceBuilder;
    }

    private EnumMap<SPRFields, String> createSPRFieldMap(long expiryDate) throws ParseException, OperationFailedException {

        EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);
        Timestamp dateToTimestamp = getTimestampValue(expiryDate);
        SPRFields.EXPIRY_DATE.validateTimeStampValue(dateToTimestamp);
        sprFieldMap.put(SPRFields.EXPIRY_DATE, String.valueOf(expiryDate));
        return sprFieldMap;
    }

    private Timestamp getTimestampValue(long expiryDate) throws OperationFailedException {
        Timestamp dateToTimestamp = null;
        if (Objects.isNull(expiryDate) == false) {
            try {
                dateToTimestamp = new Timestamp(expiryDate);
            } catch (NumberFormatException e) {
                LogManager.getLogger().error(MODULE, "Error while converting " + expiryDate + " to timestamp");
                throw new OperationFailedException("Error while converting " + expiryDate + " to timestamp", ResultCode.INVALID_INPUT_PARAMETER);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error while converting " + expiryDate + " to timestamp");
                throw new OperationFailedException("Error while converting " + expiryDate + " to timestamp", ResultCode.INVALID_INPUT_PARAMETER);
            }
        }
        return dateToTimestamp;
    }


    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

}
