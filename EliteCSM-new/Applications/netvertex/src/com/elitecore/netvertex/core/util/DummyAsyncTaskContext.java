package com.elitecore.netvertex.core.util;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

/***
 * Dummy Implementation of Aysnc Task Context
 * Which is required to create Task Schedular in Netvertex
 *
 */

public class DummyAsyncTaskContext implements AsyncTaskContext {


    public DummyAsyncTaskContext() {
        //NO SONAR This is dummy Implementation of Async Task Context in  Netvertex
    }

    @Override
    public synchronized void setAttribute(String key, Object attribute) {
        //NO SONAR This is dummy Implementation so need provide implementation
    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }

}
