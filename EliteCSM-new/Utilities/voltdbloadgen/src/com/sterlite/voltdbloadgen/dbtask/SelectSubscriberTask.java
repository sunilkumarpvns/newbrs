package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.client.Client;


import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;

public class SelectSubscriberTask implements java.util.concurrent.Callable<Integer> {

    private int id;
    private Client client;
    private final String procedure = "VoltSelectProfileById";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private final int executionCount;
    private final java.util.concurrent.CountDownLatch countDownLatch;
    private final java.util.concurrent.atomic.AtomicInteger requestCounter;

    public SelectSubscriberTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter,
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
                client.callProcedure(procedure, counter.getAndIncrement());
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                totalTime += queryExecutionTime;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
        return totalTime;
    }
}
