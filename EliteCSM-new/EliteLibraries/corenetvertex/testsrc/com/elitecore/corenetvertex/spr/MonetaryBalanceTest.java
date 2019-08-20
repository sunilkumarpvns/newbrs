package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class MonetaryBalanceTest {


    @Test
    public void isExitTrueWhenAvailableBalanceIsPositiveNumeric() {
        MonetaryBalance monetaryBalance = createMonetaryBalance();
        assertTrue(monetaryBalance.isExist());
    }

    public class isExistFalseWhen {

        @Test
        public void availabaleBalanceIsZero() {
            long availBalance = 0;
            MonetaryBalance monetaryBalance = createMonetaryBalance();
            monetaryBalance.setAvailBalance(availBalance);

            assertFalse(monetaryBalance.isExist());
        }

        @Test
        public void availabaleBalanceIsLessThanZero() {
            long availBalance = -1;
            MonetaryBalance monetaryBalance = createMonetaryBalance();
            monetaryBalance.setAvailBalance(availBalance);

            assertFalse(monetaryBalance.isExist());
        }

    }

    @Test
    public void maxAllowedBalanceReturnAllowedBalanceFromActualBalance() {
        double totalBalance = 100;
        int totalReservation = 10;
        MonetaryBalance monetaryBalance = createMonetaryBalance();
        monetaryBalance.setAvailBalance(totalBalance);
        monetaryBalance.setTotalReservation(totalReservation);

        assertThat(monetaryBalance.maxAllowedBalance(2.0), is(equalTo(45l)));
    }


    @Test
    public void substractDeductFromAvailableBalance() {
        double totalBalance = 100;
        MonetaryBalance monetaryBalance = createMonetaryBalance();
        monetaryBalance.setAvailBalance(totalBalance);
        monetaryBalance.substract(10);
        assertThat(monetaryBalance.getAvailBalance(), is(equalTo(90.0)));
    }

    @Test
    public void substractReservationDeductFromTotalReservation() {
        double totalReservation = RandomUtils.nextDouble(1, 10000);
        double reservation = RandomUtils.nextDouble(1, totalReservation);
        MonetaryBalance monetaryBalance = createMonetaryBalance();
        monetaryBalance.setTotalReservation(totalReservation);
        monetaryBalance.substractReservation(reservation);
        assertThat(monetaryBalance.getTotalReservation(), is(equalTo(totalReservation - reservation)));
    }

    @Test
    public void addReservationAddIntoTotalReservation() {
        double totalBalance = RandomUtils.nextDouble(1, 10000);
        double reservation = RandomUtils.nextDouble(1, 10000);
        MonetaryBalance monetaryBalance = createMonetaryBalance();
        monetaryBalance.setTotalReservation(totalBalance);
        monetaryBalance.addReservation(reservation);
        assertThat(monetaryBalance.getTotalReservation(), is(equalTo(totalBalance + reservation)));
    }


    @Test
    public void copyCreateNewInstanceWithAllValueSame() {
        MonetaryBalance monetaryBalance = createMonetaryBalance();

        ReflectionAssert.assertLenientEquals(monetaryBalance.copy(), monetaryBalance);
    }

    private MonetaryBalance createMonetaryBalance() {

        double availableBalance = nextDouble(2, Double.MAX_VALUE);

        return new MonetaryBalance.MonetaryBalanceBuilder(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), MonetaryBalanceType.DEFAULT.name(),
                RandomUtils.nextLong())
                .withAvailableBalance(availableBalance)
                .withTotalBalance(nextDouble(1, Double.MAX_VALUE))
                .withTotalReservation(nextDouble(1, availableBalance-1))
                .withValidFromDate(System.currentTimeMillis())
                .withValidToDate(System.currentTimeMillis())
                .build();

    }
}
