package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
public class GyServiceUnitsTest {

    private GyServiceUnits gyServiceUnits;

    @Before
    public void setUp() {
        gyServiceUnits = new GyServiceUnits();
    }

    public Object[][] dataProviderFor_floorReturnCalculatedPulseBaseOnFloorDiv() {

        return new Object[][]{
                {
                        3, 2, 0
                },

                {
                        3, 0, 0
                },

                {
                        3, 4, 1
                },

                {
                        3, 3, 1
                },

        };
    }


    @Test
    @Parameters(method = "dataProviderFor_floorReturnCalculatedPulseBaseOnFloorDiv")
    public void floorReturnCalculatedPulseBaseOnFloorDiv(long pulse, long input, long output) {
        gyServiceUnits.setPulseMinorUnit(pulse);
        assertEquals(output, gyServiceUnits.calculateFloorPulse(input));
    }

    public Object[][] dataProviderFor_ceilReturnCalculatedPulseBaseOnCeilDiv() {

        return new Object[][]{
                {
                        3, 2, 1
                },

                {
                        3, 0, 0
                },

                {
                        3, 4, 2
                },

                {
                        3, 3, 1
                },

                {
                        2, 1, 1
                },

        };
    }


    @Test
    @Parameters(method = "dataProviderFor_ceilReturnCalculatedPulseBaseOnCeilDiv")
    public void ceilReturnCalculatedPulseBaseOnCeilDiv(long pulse, long input, long output) {
        gyServiceUnits.setPulseMinorUnit(pulse);
        assertEquals(output, gyServiceUnits.calculateCeilPulse(input));
    }

    public Object[][] dataProviderFor_multiplyRetrunMultiplicationOfPulseAndPulseMinorUnit() {

        return new Object[][]{
                {
                        3, 2, 6
                },

                {
                        3, 0, 0
                },

                {
                        3, 4, 12
                },

                {
                        3, 3, 9
                },

                {
                        1, 1, 1
                },

        };
    }


    @Test
    @Parameters(method = "dataProviderFor_multiplyRetrunMultiplicationOfPulseAndPulseMinorUnit")
    public void multiplyRetrunMultiplicationOfUnitAndPulse(long pulse, long input, long output) {
        gyServiceUnits.setPulseMinorUnit(pulse);
        assertEquals(output, gyServiceUnits.calculateDeductableQuota(input));
    }

    public Object[][] dataProviderFor_deductableMoney() {

        return new Object[][]{
                {
                        3, 180, 2, 0.033333
                },

                {
                        3, 3, 0, 0.000000
                },

                {
                        1, 3600, 4, 0.001111
                },

                {
                        3, 3, 3, 3.000000
                },

        };
    }


    @Test
    @Parameters(method = "dataProviderFor_deductableMoney")
    public void deductableMoneyRetrunMultiplicationOfPulseAndRateThenDivideWithRateMinorUnit(long rate, long rateMinorUnit, long input, double output) {
        gyServiceUnits.setRate(rate);
        gyServiceUnits.setRateMinorUnit(rateMinorUnit);
        assertEquals(BigDecimal.valueOf(output).setScale(6, RoundingMode.HALF_UP), gyServiceUnits.calculateDeductableMoney(input));
    }

    @Test
    public void checkWhetherTheCopiedGyServiceUnitsIsEqualToTheOriginalGyServiceUnits(){
        gyServiceUnits = buidGyServiceUnits();
        GyServiceUnits copiedGyServiceUnits = gyServiceUnits.copy();
        assertReflectionEquals(gyServiceUnits, copiedGyServiceUnits);
    }

    private GyServiceUnits buidGyServiceUnits() {
        gyServiceUnits.setRateCardGroupName(UUID.randomUUID().toString());
        gyServiceUnits.setRateCardName(UUID.randomUUID().toString());
        gyServiceUnits.setPackageId(UUID.randomUUID().toString());
        gyServiceUnits.setQuotaProfileIdOrRateCardId(UUID.randomUUID().toString());
        gyServiceUnits.setSubscriptionId(UUID.randomUUID().toString());
        gyServiceUnits.setBalanceId(UUID.randomUUID().toString());
        gyServiceUnits.setMonetaryBalanceId(UUID.randomUUID().toString());
        gyServiceUnits.setVolume(RandomUtils.nextLong(0,10));
        gyServiceUnits.setTime(RandomUtils.nextLong(0,10));
        gyServiceUnits.setMoney(RandomUtils.nextLong(0,10));
        gyServiceUnits.setTimePulse(RandomUtils.nextLong(0,10));
        gyServiceUnits.setRateMinorUnit(RandomUtils.nextLong(0,10));
        gyServiceUnits.setReservationRequired(RandomUtils.nextBoolean());
        gyServiceUnits.setReservedMonetaryBalance(RandomUtils.nextDouble());
        gyServiceUnits.setRate(RandomUtils.nextDouble());
        return gyServiceUnits;
    }
}
