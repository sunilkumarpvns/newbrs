package com.sterlite.voltdbloadgen;

import org.voltdb.client.Client;
import org.voltdb.client.ClientFactory;


import static com.sterlite.voltdbloadgen.util.Commons.NULL_CALL_BACK;


public class MainSingleOperationLoad {

    private static Client[] clients;
    private static java.util.concurrent.Callable[] tasks;
    private static int subscriberIdStartNum = 0;
    private static int totalSubscribers;
    private static int noOfThreads;
    private static java.util.concurrent.ExecutorService executor;
    private static java.util.concurrent.CountDownLatch countDownLatch;
    private static java.util.concurrent.atomic.AtomicInteger requestCounter = new java.util.concurrent.atomic.AtomicInteger(0);
    /// for TPS Calculation only
    private static java.util.concurrent.ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private static final String SELECT_OPERATION = "SELECT";
    private static final String INSERT_OPERATION = "INSERT";
    private static final String UPDATE_OPERATION = "UPDATE";
    private static final String DELETE_OPERATION = "DELETE";
    private static final String INSERT_USAGE_OPERATION = "INSERT_USAGE";
    private static final String SELECT_USAGE_OPERATION = "SELECT_USAGE";
    private static final String ADDTOEXISTING_USAGE_OPERATION = "ADDTOEXISTING_USAGE";
    private static final String REPLACE_USAGE_OPERATION = "REPLACE_USAGE";
    private static final String INSERT_CORESESSION_OPERATION = "INSERT_CORESESSION";
    private static final String SELECT_CORESESSION_OPERATION = "SELECT_CORESESSION";
    private static final String DELETE_CORESESSION_OPERATION = "DELETE_CORESESSION";
    private static final String UPDATE_CORESESSION_OPERATION = "UPDATE_CORESESSION";
    private static final String SELECT_CORESESSION_BY_IPV4_OPERATION = "SELECT_CORESESSION_BY_IPV4";

    public static void main(String[] params) throws Exception {

        if (params.length < 6) {
            System.out.println("HELP:");
            System.out.println("Provide params in this sequence: <ip> <port> <clientShared> <threads> <subscribercount> <operation>");
            System.out.println("ip=voltdb IPs");
            System.out.println("port= voltdb port");
            System.out.println("clients=0(shared), 1(individual)");
            System.out.println("threads=No of Threads");
            System.out.println("subscriberCount=start number");
            System.out.println("subscriberCount=No of subscribers");
            System.out.println("operation=" + SELECT_OPERATION +
                    ", " + INSERT_OPERATION +
                    ", " + UPDATE_OPERATION);
            return;
        }

        System.out.println("ip=" + params[0]);
        System.out.println("port=" + params[1]);
        System.out.println("clients=0(shared), 1(individual)=" + params[2]);
        System.out.println("threads=" + params[3]);
        System.out.println("subscriberCount=" + params[4]);
        System.out.println("operation=" + params[5]);

        noOfThreads = getInt(params[3]);
        subscriberIdStartNum = getInt(params[4]);
        totalSubscribers = getInt(params[5]);
        countDownLatch = new java.util.concurrent.CountDownLatch(noOfThreads);

        setUp(params[0], getInt(params[1]), getInt(params[2]), params[6]);
        executor = java.util.concurrent.Executors.newFixedThreadPool(noOfThreads);
        scheduledThreadPoolExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new com.sterlite.voltdbloadgen.MainSingleOperationLoad.TPSCalculatorTask(), 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        java.util.List<java.util.concurrent.Future<Integer>> fts = new java.util.ArrayList<>();

        for (int i = 0; i < tasks.length; i++) {
            fts.add(executor.submit(tasks[i]));
        }

        waitTillAllTaskComplete();

        System.out.println("Actual Total Time: " + getTime(fts));

        shutdown();
    }

    private static int getTime(java.util.List<java.util.concurrent.Future<Integer>> fts) throws Exception {
        int count = 0;
        for (java.util.concurrent.Future<Integer> future : fts) {
            count += future.get();
        }
        return count;
    }

    private static void waitTillAllTaskComplete() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void shutdown() throws Exception {
        executor.shutdown();
        scheduledThreadPoolExecutor.shutdown();

        for (Client client : clients) {
            client.drain();
            client.close();
        }
    }

    private static int getInt(String param) {
        return Integer.parseInt(param);
    }

    public static void setUp(String ip, int port, int clientShared, String operationName) throws Exception {

		/*ClientConfig config = new ClientConfig("voltdb", "voltdb");
		config.setProcedureCallTimeout(1000);*/

        if (clientShared == 0) {
            clients = new Client[1];
        } else {
            clients = new Client[noOfThreads];
        }

        for (int i = 0; i < clients.length; i++) {
            clients[i] = ClientFactory.createClient();
            String[] ips = ip.split(",");
            for (int j = 0; j < ips.length; j++) {
                if (ips[j].length() > 0) {
                    clients[i].createConnection(ips[j], port);
                }
            }
        }

        tasks = new java.util.concurrent.Callable[noOfThreads];
        java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(subscriberIdStartNum);

        for (int i = 0; i < tasks.length; i++) {
            if (clientShared == 0) {
                tasks[i] = getTask(operationName, counter, i, clients[0]);
            } else {
                tasks[i] = getTask(operationName, counter, i, clients[i]);
            }
        }
    }

    private static java.util.concurrent.Callable getTask(String operationName, java.util.concurrent.atomic.AtomicInteger counter, int i, Client client) {

        if (SELECT_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.SelectSubscriberTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (INSERT_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.InsertSubscriberTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (UPDATE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.MainSingleOperationLoad.UpdateTask(i, client, counter);
        } else if (DELETE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.MainSingleOperationLoad.DeleteTask(i, client, counter);
        } else if (INSERT_USAGE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.InsertUsageTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (ADDTOEXISTING_USAGE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.AddToExistingUsageTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (REPLACE_USAGE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.ReplaceUsageTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (SELECT_USAGE_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.SelectUsageTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (INSERT_CORESESSION_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.InsertCoreSessionTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (SELECT_CORESESSION_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.SelectCoresessionTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (DELETE_CORESESSION_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.DeleteCoresessionTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (UPDATE_CORESESSION_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.UpdateCoresessionTask(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else if (SELECT_CORESESSION_BY_IPV4_OPERATION.equalsIgnoreCase(operationName)) {
            return new com.sterlite.voltdbloadgen.dbtask.SelectCoresessionByIPv4Task(i, client, counter, getExecutionCount(), countDownLatch, requestCounter);
        } else {
            throw new UnsupportedOperationException("Operation(" + operationName + ") Not Supported");
        }
    }

    public static int getExecutionCount() {
        return totalSubscribers / noOfThreads;
    }

    static class DeleteTask implements java.util.concurrent.Callable<Integer> {

        private int id;
        private Client client;
        private final String procedure = "VoltPurgeProfileById";
        private java.util.concurrent.atomic.AtomicInteger counter;

        DeleteTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter) {
            this.id = id;
            this.client = client;
            this.counter = counter;
        }

        @Override
        public Integer call() {
            int totalTime = 0;
            System.out.println("Id: " + id + " starts: " + procedure);
            for (int i = 1; i <= totalSubscribers / noOfThreads; i++) {

                try {
                    long queryExecutionTime = getCurrentTime();
                    requestCounter.incrementAndGet();
                    String subscriberId = Integer.toString(counter.incrementAndGet());
                    client.callProcedure(NULL_CALL_BACK, procedure, subscriberId);
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

    static long getCurrentTime() {
        return System.currentTimeMillis();
    }



    static class UpdateTask implements java.util.concurrent.Callable<Integer> {

        private int id;
        private Client client;
        private final String procedure = "VoltUpdateProfileById";
        private java.util.concurrent.atomic.AtomicInteger counter;

        UpdateTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter) {
            this.id = id;
            this.client = client;
            this.counter = counter;
        }

        @Override
        public Integer call() {

            int total = 0;
            System.out.println("Id: " + id + " starts: " + procedure);
            for (int i = 1; i <= totalSubscribers / noOfThreads; i++) {

                try {
                    long queryExecutionTime = getCurrentTime();
                    requestCounter.incrementAndGet();
                    String subscriberId = Integer.toString(counter.incrementAndGet());
                    client.callProcedure(NULL_CALL_BACK, procedure, subscriberId, getArray(subscriberId), new java.util.Date(), new java.util.Date(2018, 10, 10), new java.util.Date(2018, 10, 10));
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
            array[5] = "volt_base_New";
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

    /**
     * A Runnable dbtask to count TPM for pcrf submit request
     *
     * @author Milan Paliwal
     */

    private static class TPSCalculatorTask implements Runnable {

        private long lastResetTimeMillis;

        public TPSCalculatorTask() {
            lastResetTimeMillis = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long tempReqCount = requestCounter.get();
            requestCounter.set(0);
            System.out.println("TPS: " + tempReqCount);
        }
    }


    private static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

}
