package com.sterlite.voltdbloadgen.dbtask;

import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;


import static com.sterlite.voltdbloadgen.util.Commons.getCurrentTime;
import static com.sterlite.voltdbloadgen.util.Commons.printIfFail;

public class InsertCoreSessionTask implements java.util.concurrent.Callable<Integer> {

    private int id;
    private Client client;
    private final String procedure = "VoltSaveCoreSessionV2";
    private java.util.concurrent.atomic.AtomicInteger counter;
    private int executionCount;
    private java.util.concurrent.CountDownLatch countDownLatch;
    private java.util.concurrent.atomic.AtomicInteger requestCounter;

    public InsertCoreSessionTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter,
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
                String coresessionId = Integer.toString(counter.getAndIncrement());
                requestCounter.incrementAndGet();
                String[] sessionData = getSessionDataArray(coresessionId);
                long queryExecutionTime = getCurrentTime();
                ClientResponse clientResponse = client.callProcedure(/*NULL_CALL_BACK,*/ procedure, coresessionId, sessionData);
                printIfFail(clientResponse, coresessionId);
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                totalTime +=queryExecutionTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        countDownLatch.countDown();
        return totalTime;
    }

    public static String[] getSessionDataArray(String coreSessionId) {

        String[] array = new String[39];

        array[0] = "subscriberId";
        array[1] = coreSessionId;
        array[2] = "SESSIONID";
        array[3] = "USERIDENTITY";
        array[4] = "SESSIONMANAGERID";
        array[5] = "IPV4" + coreSessionId;
        array[6] = "IPV6" + coreSessionId;
        array[7] = "ACCESSNETWORK";
        array[8] = "DIAMETER";//sessiontype
        array[9] = "MULTISESSIONID";
        array[10] = "QOSPROFILE";
        array[11] = "ADDONS";
        array[12] = "DATAPACKAGE";
        array[13] = "IMSPACKAGE";
        array[14] = "SOURCEGATEWAY";
        array[15] = "SYSESSIONID";
        array[16] = "SYGATEWAYNAME";
        array[17] = "GATEWAYNAME";
        array[18] = "1";//congestion
        array[19] = "IMSI";
        array[20] = "MSISDN";
        array[21] = "NAI";
        array[22] = "NAIREALM";
        array[23] = "NAIUSERNAME";
        array[24] = "SIPURL";
        array[25] = "PCCRULES";
        array[26] = "REQUESTEDQOS";
        array[27] = "SESSIONUSAGE";
        array[28] = "10";
        array[29] = "USAGERESERVATION";
        array[30] = "GA"+coreSessionId; // gateway Address
        array[31] = "GATEWAYREALM";
        array[32] = "PACKAGE_USAGE";
        array[33] = "CHARGINGRULEBASENAMES";
        array[34] = "PARAM1";
        array[35] = "PARAM2";
        array[36] = "PARAM3";
        array[37] = "PARAM4";
        array[38] = "PARAM5";
        return array;
    }
}
