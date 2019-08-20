package com.elitecore.netvertex.rnc;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class PulseCalculatorTest {

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
        assertEquals(output, new PulseCalculator(pulse).floor(input));
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
        assertEquals(output, new PulseCalculator(pulse).ceil(input));
    }

    public Object[][] dataProviderFor_multiplyRetrunMultiplicationOfUnitAndPulse() {

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
    @Parameters(method = "dataProviderFor_multiplyRetrunMultiplicationOfUnitAndPulse")
    public void multiplyRetrunMultiplicationOfUnitAndPulse(long pulse, long input, long output) {
        assertEquals(output, new PulseCalculator(pulse).multiply(input));
    }
}