package com.elitecore.config.core;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BooleanAdapterTest {
    private BooleanAdapter booleanAdapter = new BooleanAdapter();

    @Test
    public void marshalReturnStringTrueWhenTrueBooleanValueProvided() throws Exception {
        assertEquals("true", booleanAdapter.marshal(Boolean.TRUE));
    }
}
