package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;


import static com.sterlite.voltdbloadgen.util.Commons.FUTURE_DATE;
import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;
import static com.sterlite.voltdbloadgen.util.Commons.printIfFail;

public class InsertUsageTask implements java.util.concurrent.Callable<Integer> {

    private int id;
    private Client client;
    private final String inser_reverseLookUp_procedure = "VoltInsertReverseLookUpSubscriberIdToId";
    private final String procedure = "VoltInsertUsage";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private int executionCount;
    private java.util.concurrent.CountDownLatch countDownLatch;
    private java.util.concurrent.atomic.AtomicInteger requestCounter;

    public InsertUsageTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter,
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
        int totalTime=0;
        System.out.println("Id: " + id + " starts: " + procedure);
        for (int i=1; i <=executionCount; i++) {

            try {
                String subscriber_Id = Integer.toString(counter.getAndIncrement());
                String id = subscriber_Id;
                requestCounter.incrementAndGet();
                long queryExecutionTime = getCurrentTime();
               /* client.callProcedure(NULL_CALL_BACK,inser_reverseLookUp_procedure, subscriber_Id, id);
                client.callProcedure(NULL_CALL_BACK,inser_reverseLookUp_procedure, Integer.toString(counter.get()-1), id);
                client.callProcedure(NULL_CALL_BACK,inser_reverseLookUp_procedure, Integer.toString(counter.get()), id);*/
                ClientResponse clientResponse = client.callProcedure(/*NULL_CALL_BACK,*/ procedure,
                        subscriber_Id, createInsertUsageArray(subscriber_Id));
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                printIfFail(clientResponse, id);
                totalTime +=queryExecutionTime;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        countDownLatch.countDown();
        return totalTime;
    }

    private static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String[] createInsertUsageArray(String subscriberId) {

        String[] usageArray = new String[26];

        usageArray[0] = "ID" + subscriberId;
        usageArray[1] =  subscriberId;
        usageArray[2] =  "oddPackage";
        usageArray[3] = getUUID();
        usageArray[4] = getUUID();
        usageArray[5] = getUUID();

        usageArray[6] =  100+"";
        usageArray[7] =  50+"";
        usageArray[8] =  50+"";
        usageArray[9] =  0+"";

        usageArray[10] =  100+"";
        usageArray[11] =  50+"";
        usageArray[12] =  50+"";
        usageArray[13] =  0+"";

        usageArray[14] =  100+"";
        usageArray[15] =  50+"";
        usageArray[16] =  50+"";
        usageArray[17] =  0+"";

        usageArray[18] =  100+"";
        usageArray[19] =  50+"";
        usageArray[20] =  50+"";
        usageArray[21] =  0+"";

        usageArray[22] =  getCurrentTime()+"";
        usageArray[23] =  getCurrentTime()+"";
        usageArray[24] =  FUTURE_DATE+"";
        usageArray[25] =  FUTURE_DATE+"";

        return usageArray;
    }
}
