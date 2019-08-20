package com.sterlite.voltdbloadgen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import org.voltdb.client.Client;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.NullCallback;

public class MainProvisionFromFile {

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
    private static String fileName;
    private static java.util.concurrent.LinkedBlockingDeque<String> sharedQueue;
    private static Object objectlock = new Object();
    static java.io.BufferedReader reader;

    public static void main(String[] params) throws Exception {

        if (params.length < 4) {
            System.out.println("HELP:");
            System.out.println("Provide params in this sequence: <ip> <port> <clientShared> <threads> <subscribercount> <operation>");
            System.out.println("ip=voltdb IPs");
            System.out.println("port= voltdb port");
            System.out.println("threads= No of Threads");
            System.out.println("filename=<filename name>");
            return;
        }

        System.out.println("ip=" + params[0]);
        System.out.println("port=" + params[1]);
        System.out.println("threads=" + params[2]);
        System.out.println("filename=" + params[3]);

        noOfThreads = getInt(params[2]);
        totalSubscribers = 5000000;
        sharedQueue = new java.util.concurrent.LinkedBlockingDeque<>();
        countDownLatch = new java.util.concurrent.CountDownLatch(noOfThreads);
        oddPackage = "3GB_Test";
        evenPackage = "3GB_Test";
        fileName = params[3];
        reader = new java.io.BufferedReader(new java.io.FileReader(
                fileName));
        setUp(params[0], getInt(params[1]));

        executor = java.util.concurrent.Executors.newFixedThreadPool(noOfThreads);
        scheduledThreadPoolExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new com.sterlite.voltdbloadgen.MainProvisionFromFile.TPSCalculatorTask(), 0, 1, java.util.concurrent.TimeUnit.SECONDS);

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
        reader.close();
        client.drain();
        client.close();
    }

    private static int getInt(String param) {
        return Integer.parseInt(param);
    }

    public static void setUp(String ip, int port) throws Exception {

        client = ClientFactory.createClient();
        String[] ips = ip.split(",");
        for (int j = 0; j < ips.length; j++) {
            if (ips[j].length() > 0) {
                client.createConnection(ips[j], port);
            }
        }

        tasks = new java.util.concurrent.Callable[noOfThreads];
        for (int i = 0; i < tasks.length / 2; i++) {
            tasks[i] = new com.sterlite.voltdbloadgen.MainProvisionFromFile.InsertTask(i, client);
        }

        for (int i = tasks.length / 2; i < tasks.length; i++) {
            tasks[i] = new com.sterlite.voltdbloadgen.MainProvisionFromFile.FileReadTask();
        }
    }

    static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    static class FileReadTask implements java.util.concurrent.Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            for (int i = 1; i <= totalSubscribers / (noOfThreads / 2); i++) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        return 0;
                    }
                    sharedQueue.add(line.substring(1, 16));
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }


    static class InsertTask implements java.util.concurrent.Callable<Integer> {

        private int id;
        private Client client;
        private final String procedure = "VoltAddProfileSPR";

        InsertTask(int id, Client client) {
            this.id = id;
            this.client = client;
        }

        @Override
        public Integer call() {

            int total = 0;
            System.out.println("Id: " + id + " starts: " + procedure);
            for (int i = 1; i <= totalSubscribers / (noOfThreads / 2); i++) {

                try {
                    long queryExecutionTime = getCurrentTime();
                    requestCounter.incrementAndGet();
                    String subscriberIdentity = sharedQueue.take();
                    //System.out.println("id:" + id+  ">" +subscriberIdentity);
                    String datapackage = evenPackage;
                    client.callProcedure(procedure, subscriberIdentity, getArray(subscriberIdentity, datapackage), new java.util.Date(), new java.util.Date(2018, 10, 10));
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