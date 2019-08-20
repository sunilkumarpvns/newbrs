package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Function;
import com.elitecore.core.serverx.sessionx.SessionData;

public class KeyFunction implements Function<SessionData, String> {

    private String key;

    public KeyFunction(String key) {
        this.key = key;
    }

    @Override
    public String apply(SessionData sessionData) {
        return sessionData.getValue(key);
    }
}
