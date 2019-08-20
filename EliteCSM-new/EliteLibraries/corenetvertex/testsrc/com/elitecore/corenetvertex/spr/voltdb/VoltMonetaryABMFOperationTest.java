package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.MonetaryBalanceBuilder;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.voltdb.InProcessVoltDBServer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@Ignore
public class VoltMonetaryABMFOperationTest {

    private static InProcessVoltDBServer voltServer;

    private VoltMonetaryABMFOperation monetaryABMFOperation;
    private static final double DELTA = 0.000000001;
    private long currentTime = System.currentTimeMillis();


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
    public void testAddMonetaryBalance() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID1";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(initialBalance, fetchedMonetaryBalance.getInitialBalance(), DELTA);
        assertEquals(availableBalance, fetchedMonetaryBalance.getAvailBalance(), DELTA);
        assertEquals(totalReservation, fetchedMonetaryBalance.getTotalReservation(), DELTA);
    }

    @Test
    public void testUpdateMoneatryBalance() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID2";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        double updateInitialBalance = 500000000000.001111;
        double updateAvaiBalance = 200000000000.001111;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance updateMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, updateInitialBalance, updateAvaiBalance, 0d).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.updateBalance(subscriberId, updateMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(initialBalance + updateInitialBalance, fetchedMonetaryBalance.getInitialBalance(), DELTA);
        assertEquals(availableBalance + updateAvaiBalance , fetchedMonetaryBalance.getAvailBalance(), DELTA);
    }

    @Test
    public void testReserveMoneatryBalance() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID3";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 1000000000000.001111;
        double totalReservation = 0d;
        double reserveBalance = 799999999.999999;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance reserveMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, reserveBalance).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.reserveBalance(subscriberId, Arrays.asList(reserveMonetaryBalance), new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(totalReservation + reserveBalance, fetchedMonetaryBalance.getTotalReservation(), DELTA);
    }

    @Test
    public void testDirectDebitMoneatryBalance() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID5";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 1000000000000.001111;
        double totalReservation = 0;
        double reportedBalance = 799999999.999999;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance directDebitMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, reportedBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.directDebitBalance(subscriberId, Arrays.asList(directDebitMonetaryBalance), new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(availableBalance - reportedBalance, fetchedMonetaryBalance.getAvailBalance(), DELTA);
    }

    @Test
    public void testReportAndReserveMoneatryBalance() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID6";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 1000000000000.001111;
        double totalReservation = 300;
        double reportedBalance = 799999999.999999;
        double reservedBalance = 10;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance reportAndReserveMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, reportedBalance, reservedBalance).withLastUpdateTime(System.currentTimeMillis()).build();
        monetaryABMFOperation.reportAndReserveBalance(subscriberId, Arrays.asList(reportAndReserveMonetaryBalance), new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(availableBalance - reportedBalance, fetchedMonetaryBalance.getAvailBalance(), DELTA);
        assertEquals(totalReservation + reservedBalance, fetchedMonetaryBalance.getTotalReservation(), DELTA);
    }

    @Test
    public void testUpdateCreditLimit() throws Exception {
        String subscriberId = "Subscriber1";
        String id = "ID7";
        double initialBalance = 1000000000000.001111;
        double availableBalance = 500000000000.001111;
        double totalReservation = 0d;
        double updateInitialBalance = 500000000000.001111;
        double updateAvaiBalance = 200000000000.001111;
        long creditLimit = 1000;
        long nextBillingCycleCreditLimit = 2000;
        MonetaryBalance addMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, initialBalance, availableBalance, totalReservation).withLastUpdateTime(System.currentTimeMillis()).withCreditLimit(0).withNextBillingCycleCreditLimit(0).build();
        monetaryABMFOperation.addBalance(subscriberId, addMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance updateMonetaryBalance = createMonetaryBalanceForTesting(subscriberId, id, updateInitialBalance, updateAvaiBalance, 0d).withLastUpdateTime(System.currentTimeMillis()).withCreditLimit(creditLimit).withNextBillingCycleCreditLimit(nextBillingCycleCreditLimit).build();
        monetaryABMFOperation.updateCreditLimit(subscriberId, updateMonetaryBalance, new DummyVoltDBClient(voltServer.getClient()));
        SubscriberMonetaryBalance subscriberMonetaryBalance = monetaryABMFOperation.getMonetaryBalance(subscriberId, monetaryBalance -> true, new DummyVoltDBClient(voltServer.getClient()));
        MonetaryBalance fetchedMonetaryBalance = subscriberMonetaryBalance.getBalanceById(id);
        assertEquals(creditLimit,fetchedMonetaryBalance.getCreditLimit());
        assertEquals(nextBillingCycleCreditLimit,fetchedMonetaryBalance.getNextBillingCycleCreditLimit());
        assertEquals(currentTime+60000,fetchedMonetaryBalance.getCreditLimitUpdateTime());
    }

    private MonetaryBalanceBuilder createMonetaryBalanceForTesting(String subscriberId, String id, double initialBalance,
                                                                   double availableBalance, double totalReservation) {
        MonetaryBalanceBuilder monetaryBalanceBuilder = new MonetaryBalanceBuilder(id, subscriberId).withServiceId("All-Service").
                withInitialBalance(initialBalance).
                withAvailableBalance(availableBalance).withTotalReservation(totalReservation).withCurrency("INR").withValidFromDate(1530642600000l)
                .withValidToDate(2161794600000l).withCreditLimitUpdateTime(currentTime+60000);
        return monetaryBalanceBuilder;
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

}
