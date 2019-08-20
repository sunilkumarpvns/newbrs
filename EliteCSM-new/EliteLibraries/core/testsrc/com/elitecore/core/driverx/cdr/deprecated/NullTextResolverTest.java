package com.elitecore.core.driverx.cdr.deprecated;

import org.junit.Test;

import static org.junit.Assert.*;

public class NullTextResolverTest {

    @Test
    public void applyAlwaysReturnNullValue() {
        assertNull(new NullTextResolver().apply(null));
    }
}