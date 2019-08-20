package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.client.Client;


import static com.sterlite.voltdbloadgen.util.Commons.NULL_CALL_BACK;
import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;

public class InsertSubscriberTask implements java.util.concurrent.Callable<Integer> {
    private int id;
    private Client client;
    private final String procedure = "VoltAddProfileSPR";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private final int executionCount;
    private final java.util.concurrent.CountDownLatch countDownLatch;
    private final java.util.concurrent.atomic.AtomicInteger requestCounter;

    public InsertSubscriberTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter, int executionCount, java.util.concurrent.CountDownLatch countDownLatch,
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

        int total = 0;
        System.out.println("Id: " + id + " starts: " + procedure);
        for (int i = 1; i <= executionCount; i++) {

            try {
                String subscriberIdentity = Integer.toString(counter.incrementAndGet());
                requestCounter.incrementAndGet();
                long queryExecutionTime = getCurrentTime();
                client.callProcedure(NULL_CALL_BACK, procedure, subscriberIdentity, getArray(subscriberIdentity),
                        new java.util.Date(), new java.util.Date(2018, 10, 10));
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                total += queryExecutionTime;

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        countDownLatch.countDown();
        return total;
    }

    Object getArray(String subscriberId) {

        String[] array = new String[43];
        array[0] = subscriberId;
        array[1] = "USERNAME";
        array[2] = "PASSWORD";
        array[3] = "custType";
        array[4] = "ACTIVE";
        array[5] = "volt_base";
        array[6] = "imspackage";
        array[7] = "10";
        array[8] = "AREA";
        array[9] = "CITY";
        array[10] = "PARAM1";
        array[11] = "PARAM2";
        array[12] = "PARAM3";
        array[13] = "PARAM4";
        array[14] = "PARAM5";
        array[15] = "ZONE";
        array[16] = "country";
        array[17] = "ROLE";
        array[18] = "COMPANY";
        array[19] = "DEPAR";
        array[20] = "1";
        array[21] = "CADRE";
        array[22] = "a@b.com";
        array[23] = "97979797";
        array[24] = "SIPURL";
        array[25] = "CUI";
        array[26] = "IMSI";
        array[27] = "97979797";
        array[28] = "MAC";
        array[29] = "EUI64";
        array[30] = "MODIEUI";
        array[31] = "encryptiontype";
        array[32] = "ESN";
        array[33] = "MEID";
        array[34] = "PARENTID";
        array[35] = "GROUPNAME";
        array[36] = "IMEI";
        array[37] = "callingstationid";
        array[38] = "nasport";
        array[39] = "0.0.0.0";
        array[40] = "true";
        array[41] = "true";
        array[42] = "true";

        return array;
    }
}
