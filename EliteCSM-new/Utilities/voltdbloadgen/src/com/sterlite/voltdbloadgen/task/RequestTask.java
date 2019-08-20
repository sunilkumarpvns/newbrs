package com.sterlite.voltdbloadgen.task;

import java.util.concurrent.atomic.AtomicLong;
import org.voltdb.client.Client;


import static com.sterlite.voltdbloadgen.util.AllOperations.session_delete;
import static com.sterlite.voltdbloadgen.util.AllOperations.session_insert;
import static com.sterlite.voltdbloadgen.util.AllOperations.session_select;
import static com.sterlite.voltdbloadgen.util.AllOperations.session_update;
import static com.sterlite.voltdbloadgen.util.AllOperations.subscriber_select;
import static com.sterlite.voltdbloadgen.util.AllOperations.usageOps;
import static com.sterlite.voltdbloadgen.util.AllOperations.usage_select;
import static com.sterlite.voltdbloadgen.util.Commons.getSessionId;

public class RequestTask implements java.util.concurrent.Callable<Integer> {

    private int stage;
    private Client client;
    private int configuredUpdates;
    private int decrementedUpdates;
    private java.util.concurrent.atomic.AtomicLong tpsRequestCounter;
    private String subscriberId;
    private String sessionId;
    private RequestBuildContext requestBuildContext;

    public RequestTask(int stage, Client client, String subscriberId,
                       int updates, AtomicLong tpsRequestCounter,
                       RequestBuildContext requestBuildContext) {
        this.stage = stage;
        this.client = client;
        this.configuredUpdates = updates;
        this.decrementedUpdates = updates;
        this.tpsRequestCounter = tpsRequestCounter;
        this.subscriberId = subscriberId;
        this.sessionId = getSessionId(subscriberId);
        this.requestBuildContext = requestBuildContext;
    }

    @Override
    public Integer call() {

        if (Thread.interrupted()) {
            System.out.println("Thread intruppted. Finishing dbtask.");
            return 0;
        }

        tpsRequestCounter.incrementAndGet();

        try {

            switch (stage) {
                case 1:
                    //INITIAL
                    handleInitial();
                    break;
                case 2:
                    handleUpdate();
                    break;
                case 3:
                    //TERMINATE
                    handleTerminate();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void handleTerminate() throws Exception {
        session_select(client, sessionId);
        usageOps(client, subscriberId);
        session_delete(client, sessionId);
        decrementedUpdates = configuredUpdates;
        stage = 1;
        requestBuildContext.addToInitialQueue(this);
    }

    private void handleUpdate() throws Exception {
        session_select(client, sessionId);
        usageOps(client, subscriberId);
        session_update(client, sessionId);
        decrementedUpdates--;

        if (decrementedUpdates > 0) {
            stage =  2;
        } else {
            stage =  3;
        }
        requestBuildContext.scheduleUpdate(this);
    }

    private void handleInitial() throws Exception {
        subscriber_select(client, subscriberId);
        usage_select(client, subscriberId);
        session_insert(client, sessionId);
        subscriber_select(client, subscriberId); // This is just added to make same count of VoltDB request in each App Request
        stage = 2;
        requestBuildContext.scheduleUpdate(this);
    }
}
