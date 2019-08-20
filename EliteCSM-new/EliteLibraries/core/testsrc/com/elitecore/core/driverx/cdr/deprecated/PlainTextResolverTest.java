package com.elitecore.core.driverx.cdr.deprecated;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlainTextResolverTest {
    private PlainTextResolver<String> plainTextResolver = new PlainTextResolver<>("test");
    @Test
    public void applyReturnSameValueAsProvidedInInitialization() {
        assertThat(plainTextResolver.apply(null), is(equalTo("test")));
        assertThat(plainTextResolver.apply("test1"), is(equalTo("test")));
    }
}