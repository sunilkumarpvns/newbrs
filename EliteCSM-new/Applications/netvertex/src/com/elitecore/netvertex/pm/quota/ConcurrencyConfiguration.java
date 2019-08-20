package com.elitecore.netvertex.pm.quota;

public class ConcurrencyConfiguration {

    public static final int DEFULT_CONCURRENCY_CONF = 3;

    public long getConcurrencyValue() {
        return DEFULT_CONCURRENCY_CONF;
    }
}