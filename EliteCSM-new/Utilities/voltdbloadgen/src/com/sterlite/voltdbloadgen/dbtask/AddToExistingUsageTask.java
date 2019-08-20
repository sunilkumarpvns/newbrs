package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;


import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;
import static com.sterlite.voltdbloadgen.util.Commons.printIfFail;

public class AddToExistingUsageTask implements java.util.concurrent.Callable<Integer> {

    private int id;
    private Client client;
    private final String procedure = "VoltAddToExistingUsageV2";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private final int executionCount;
    private final java.util.concurrent.CountDownLatch countDownLatch;
    private final java.util.concurrent.atomic.AtomicInteger requestCounter;

    public AddToExistingUsageTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter,
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
                String id = Integer.toString(counter.getAndIncrement());
                String subscriberId = id;
                ClientResponse clientResponse = client.callProcedure(/*NULL_CALL_BACK,*/ procedure, subscriberId,
                        createUsageArrayForReplaceUsage(subscriberId, "ID"+subscriberId));
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

    public static String[] createUsageArrayForReplaceUsage(String subscriberId, String id) {
        String[] usageArray = new String[20];

        usageArray[0] = 100 + "";
        usageArray[1] = 50 + "";
        usageArray[2] = 50 + "";
        usageArray[3] = 0 + "";

        usageArray[4] = 100 + "";
        usageArray[5] = 50 + "";
        usageArray[6] = 50 + "";
        usageArray[7] = 0 + "";

        usageArray[8] = 100 + "";
        usageArray[9] = 50 + "";
        usageArray[10] = 50 + "";
        usageArray[11] = 0 + "";

        usageArray[12] = 100 + "";
        usageArray[13] = 50 + "";
        usageArray[14] = 50 + "";
        usageArray[15] = 0 + "";

        usageArray[16] = subscriberId;
        usageArray[17] = id;

        return usageArray;
    }
}
