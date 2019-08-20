package com.sterlite.voltdbloadgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import com.sun.org.glassfish.gmbal.IncludeSubclass;
import org.voltdb.client.Client;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.NullCallback;

/**
 * This Class is kept for only reference purpose
 * Use MainProvisionFromFile or MainProvisionFromRange Class.
 */
@Deprecated
public class MainProvision {

    private static Client client;
    private static java.util.concurrent.Callable[] tasks;
    private static int totalSubscribers;
    private static int noOfThreads;
    private static java.util.concurrent.ExecutorService executor;
    private static java.util.concurrent.CountDownLatch countDownLatch;
    private static java.util.concurrent.atomic.AtomicLong requestCounter = new java.util.concurrent.atomic.AtomicLong(0);
    /// for TPS Calculation only
    private static java.util.concurrent.ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private static NullCallback nullCallback = new NullCallback();
    private static String oddPackage;
    private static String evenPackage;

    public static void main(String[] params) throws Exception {

        if (params.length < 6) {
            System.out.println("HELP:");
            System.out.println("Provide params in this sequence: <ip> <port> <clientShared> <threads> <subscribercount> <operation>");
            System.out.println("ip=voltdb IPs");
            System.out.println("port= voltdb port");
            System.out.println("threads= No of Threads");
            System.out.println("subscriberCount= No of subscribers");
            System.out.println("OddPackage=<pkg name>");
            System.out.println("EvenPackage=<pkg name>");
            return;
        }

        System.out.println("ip=" + params[0]);
        System.out.println("port=" + params[1]);
            System.out.println("threads=" + params[2]);
        System.out.println("subscriberCount=" + params[3]);
        System.out.println("OddPackage=" + params[4]);
        System.out.println("EvenPackage=" + params[5]);

        noOfThreads = getInt(params[2]);
        totalSubscribers = getInt(params[3]);
        countDownLatch = new java.util.concurrent.CountDownLatch(noOfThreads);
        oddPackage = params[4];
        evenPackage = params[5];

        setUp(params[0], getInt(params[1]));

        executor = java.util.concurrent.Executors.newFixedThreadPool(noOfThreads);
        scheduledThreadPoolExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new com.sterlite.voltdbloadgen.MainProvision.TPSCalculatorTask(), 0, 1, java.util.concurrent.TimeUnit.SECONDS);

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
        client.drain();
        client.close();
    }

    private static int getInt(String param) {
        return Integer.parseInt(param);
    }

    public static void setUp(String ip, int port) throws Exception {

		/*ClientConfig config = new ClientConfig("voltdb", "voltdb");
        config.setProcedureCallTimeout(1000);*/
        client = ClientFactory.createClient();
        String[] ips = ip.split(",");
        for (int j = 0; j < ips.length; j++) {
            if (ips[j].length() > 0) {
                client.createConnection(ips[j], port);
            }
        }

        tasks = new java.util.concurrent.Callable[noOfThreads];
        java.util.concurrent.atomic.AtomicLong counter = new java.util.concurrent.atomic.AtomicLong(303000000000000L);

        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new com.sterlite.voltdbloadgen.MainProvision.InsertTask(i, client, counter);
        }
    }

    static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    static class InsertTask implements java.util.concurrent.Callable<Integer> {

        private int id;
        private Client client;
        private final String procedure = "VoltAddProfileSPR";
        private java.util.concurrent.atomic.AtomicLong counter;

        InsertTask(int id, Client client, java.util.concurrent.atomic.AtomicLong counter) {
            this.id = id;
            this.client = client;
            this.counter = counter;
        }

        @Override
        public Integer call() {

            int total = 0;
            int intremTotal = 0;
            System.out.println("Id: " + id + " starts: " + procedure);
            for (int i = 1; i <= totalSubscribers / noOfThreads; i++) {

                try {
                    long queryExecutionTime = getCurrentTime();
                    requestCounter.incrementAndGet();
                    long subscriberIdentity = counter.incrementAndGet();
                    String datapackage = subscriberIdentity % 2 == 0 ? evenPackage : oddPackage;
                    //client.callProcedure("VoltAddProfileSPR", getArray(counter.getAndIncrement()+""), new java.util.Date(), new java.util.Date(2018,10,10));
                    client.callProcedure(procedure, Long.toString(subscriberIdentity), getArray(Long.toString(subscriberIdentity), datapackage), new java.util.Date(), new java.util.Date(2018, 10, 10));
                    queryExecutionTime = getCurrentTime() - queryExecutionTime;
                    total += queryExecutionTime;
                    intremTotal += queryExecutionTime;

                    if (i % 1000 == 0) {
                        //System.out.println("Id: " + id + ", Procedure: " + procedure + ", TimeTakenFor1000Req(ms): " + intremTotal);
                        intremTotal = 0;
                    }

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
            return total;
        }

        Object getArray(String subscriberId, String datapackage) {

            String[] array = new String[43];
            array[0] = subscriberId;
            array[1] = subscriberId;
            array[2] = "PASSWORD";
            array[3] = "Prepaid";
            array[4] = "ACTIVE";
            array[5] = datapackage;
            array[6] = "";
            array[7] = "10";
            array[8] = "AREA";
            array[9] = "CITY";
            array[10] = "PARAM1";
            array[11] = "PARAM2";
            array[12] = "PARAM3";
            array[13] = "PARAM4";
            array[14] = "PARAM5";
            array[15] = "";
            array[16] = "";
            array[17] = "";
            array[18] = "";
            array[19] = "";
            array[20] = "1";
            array[21] = "";
            array[22] = "a@b.com";
            array[23] = "97979797";
            array[24] = "";
            array[25] = "";
            array[26] = "";
            array[27] = subscriberId;
            array[28] = "";
            array[29] = "";
            array[30] = "";
            array[31] = "0";
            array[32] = "";
            array[33] = "";
            array[34] = "";
            array[35] = "";
            array[36] = "";
            array[37] = "";
            array[38] = "nasport";
            array[39] = "0.0.0.0";
            array[40] = "FALSE";
            array[41] = "FALSE";
            array[42] = "TRUE";

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
}
