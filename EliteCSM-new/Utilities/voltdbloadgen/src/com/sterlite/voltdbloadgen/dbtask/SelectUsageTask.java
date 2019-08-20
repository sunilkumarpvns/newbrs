package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;


import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;
import static com.sterlite.voltdbloadgen.util.Commons.printIfFail;

public class SelectUsageTask implements java.util.concurrent.Callable<Integer> {

    private int id;
    private Client client;
    private final String reverseLookUp_procedure = "VoltReverseLookUpSubscriberIdToId";
    private final String procedure = "VoltGetUsageV2";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private final int executionCount;
    private final java.util.concurrent.CountDownLatch countDownLatch;
    private final java.util.concurrent.atomic.AtomicInteger requestCounter;

    public SelectUsageTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter,
                           int executionCount, java.util.concurrent.CountDownLatch countDownLatch,
                           java.util.concurrent.atomic.AtomicInteger requestCounter) {
        this.id = id;
        this.client = client;
        this.counter = counter;
        this.executionCount = executionCount;
        this.countDownLatch = countDownLatch;
        this.requestCounter = requestCounter;
    }

    @Override
    public Integer call() {
        int totalTime = 0;
        System.out.println("Id: " + id + " starts: " + procedure);
        for (int i = 1; i <= executionCount; i++) {

            try {
                long queryExecutionTime = getCurrentTime();
                requestCounter.incrementAndGet();
                String subscriberId = Integer.toString(counter.getAndIncrement());
                /*String[] ids = getIdFromSubscriberId(subscriberId);

                for (String id : ids) {
                    client.callProcedure(procedure, id);
                }*/

                ClientResponse clientResponse = client.callProcedure(procedure, subscriberId);
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                printIfFail(clientResponse, subscriberId);
                totalTime += queryExecutionTime;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
        return totalTime;
    }

    private String[] getIdFromSubscriberId(String subscriberId) throws java.io.IOException, ProcCallException {
        ClientResponse clientResponse = client.callProcedure(reverseLookUp_procedure, subscriberId);
        if (clientResponse.getStatus() != ClientResponse.SUCCESS) {
            new java.io.IOException("Id not found for subscriber id: " + subscriberId + ". Reason: " + clientResponse.getStatusString());
        }

        VoltTable vt = clientResponse.getResults()[0];

        if (vt.getRowCount() == 0) {
            new java.io.IOException("Id not found for subscriber id: " + subscriberId);
        }
        String[] ids = new String[vt.getRowCount()];

        int i = 0;
        while (vt.advanceRow()) {
            ids[i] = vt.getString("ID");
            i++;
        }
        return ids;
    }
}
