package com.elitecore.core.driverx.cdr.deprecated;

import java.util.function.Function;

public class PlainTextResolver<T> implements Function<T, String> {

    private String value;

    public PlainTextResolver(String value) {
        this.value = value;
    }

    @Override
    public String apply(T t) {
        return value;
    }
}
