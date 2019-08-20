package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class DoubleToStringAdapterTest {

    private DoubleToStringAdapter doubleToStringAdapter = new DoubleToStringAdapter();


    @Test
    public void marshalShouldReturnEmptyStringForNullValue () throws Exception {
        assertEquals(doubleToStringAdapter.marshal(null),"");
    }

    @Test
    public void unmarshalShouldReturnNullForEmptyString() throws Exception {
        assertEquals(doubleToStringAdapter.unmarshal(""),null);
    }

    @Test
    public void unmarshalShouldReturnMinimumnValueOfDoubleForAnyInvalidValueOccurInParsing() throws Exception {
        assertEquals(Double.MIN_VALUE,doubleToStringAdapter.unmarshal(UUID.randomUUID().toString()),0);
    }


    @Test
    @Parameters(method = "dataProviderForDoubleStringAdapter")
    public void testMarshalAndUnMarshalShouldReturnSameValue(String strDoubleValue,Double doubleValue) throws Exception {
        assertEquals(doubleValue, doubleToStringAdapter.unmarshal(strDoubleValue));
        assertEquals(strDoubleValue, doubleToStringAdapter.marshal(doubleValue));
    }

    private Object[] dataProviderForDoubleStringAdapter() {
        Object[][] testArray = new Object[50][2];
        for (int i = 0; i < 50; i++) {
            int integral = RandomUtils.nextInt();
            int fractional = RandomUtils.nextInt(999999);
            String strDoubleValue = integral + "." + fractional;
            Double doubleValue = Double.parseDouble(strDoubleValue);
            testArray[i][0] = new BigDecimal(strDoubleValue).stripTrailingZeros().toPlainString();
            testArray[i][1] = doubleValue;
        }
        return testArray;
    }
}
