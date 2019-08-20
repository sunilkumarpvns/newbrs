package com.elitecore.core.driverx.cdr.deprecated;

import java.util.function.Function;

public class NullTextResolver<T> implements Function<T, String> {
    @Override
    public String apply(T t) {
        return null;
    }
}
