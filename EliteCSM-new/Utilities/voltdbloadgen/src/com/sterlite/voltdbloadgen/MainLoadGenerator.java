package com.sterlite.voltdbloadgen;

import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;


import static com.sterlite.voltdbloadgen.util.Commons.getInt;
import static com.sterlite.voltdbloadgen.util.Commons.getLong;

public class MainLoadGenerator {

    private static Client client;
    /// for TPS Calculation only
    private static java.util.concurrent.atomic.AtomicLong tpsRequestCounter = new java.util.concurrent.atomic.AtomicLong(0);
    private static java.util.concurrent.atomic.AtomicInteger callRate;
    private static java.util.concurrent.ScheduledThreadPoolExecutor requestBuildExecutor;
    private static java.util.concurrent.ScheduledThreadPoolExecutor requestProcessExecutor;

    public static void main(String[] params) throws Exception {

        if (params.length < 7) {
            System.out.println("HELP:");
            System.out.println("Provide params in this sequence: <ip> <port> <isSync> <threads> <startNum> <totalSubscribers> <updates> <update-interval>");
            System.out.println("ip=voltdb IPs");
            System.out.println("port= voltdb port");
            System.out.println("isSync= 0 : async, 1: sync");
            System.out.println("threads= No of Threads");
            System.out.println("Start Num= Starting Subscriber ID");
            System.out.println("Subscribers= No of Subscribers");
            System.out.println("Updates=updates count. Total Request will be updates+2");
            System.out.println("Update Interval= interval between two update requests");
            return;
        }

        System.out.println("ip=" + params[0]);
        System.out.println("port=" + params[1]);
        System.out.println("isSync=" + params[2]);
        System.out.println("threads=" + params[3]);
        System.out.println("Start Num=" + params[4]);
        System.out.println("subscriberCount=" + params[5]);
        System.out.println("Updates=" + params[6]);
        System.out.println("Update Interval=" + params[7]);

        com.sterlite.voltdbloadgen.util.AllOperations.isSync = params[2].equalsIgnoreCase("1") ? true : false;
        int noOfThreads = getInt(params[3]);
        long startNum = getLong(params[4]);
        int totalSubscribers = getInt(params[5]);
        int updates = getInt(params[6]);
        int updateInterval = getInt(params[7]);
        callRate = new java.util.concurrent.atomic.AtomicInteger(100);

        java.util.concurrent.atomic.AtomicLong currentSubscriberCounter1 = new java.util.concurrent.atomic.AtomicLong(startNum);

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setTopologyChangeAware(true);
        clientConfig.setProcedureCallTimeout(1000);

        client = ClientFactory.createClient(clientConfig);
        String[] ips = params[0].split(",");
        for (int j = 0; j < ips.length; j++) {
            if (ips[j].length() > 0) {
                client.createConnection(ips[j], getInt(params[1]));
            }
        }

        requestProcessExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(noOfThreads);
        requestBuildExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(10);
        com.sterlite.voltdbloadgen.task.RequestBuildTask requestBuildTask1 = new com.sterlite.voltdbloadgen.task.RequestBuildTask(requestProcessExecutor, tpsRequestCounter, client,
                totalSubscribers, currentSubscriberCounter1, updates, callRate, updateInterval);
        requestBuildTask1.init();

        requestBuildExecutor.scheduleAtFixedRate(requestBuildTask1, 50, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
        requestBuildExecutor.scheduleAtFixedRate(requestBuildTask1, 100, 100, java.util.concurrent.TimeUnit.MILLISECONDS);

        requestProcessExecutor.scheduleWithFixedDelay(new com.sterlite.voltdbloadgen.MainLoadGenerator.TPSCalculatorTask(), 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        consoleInput(requestBuildTask1);
    }

    private static void consoleInput(com.sterlite.voltdbloadgen.task.RequestBuildTask requestBuildTask) throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (true) {
            System.out.println("\nChooose Option: ");
            System.out.println("1. Change TPS: c <TPS>");
            System.out.println("2. Current TPS: t");
            System.out.println("3. Exit: q");

            String input = scanner.nextLine();

            if (input.startsWith("c ")) {

                try {
                    if (input.split(" ").length != 2) {
                        System.out.println("Invalid Input. Try c<space><tps>");
                    } else {

                        int callRateTmp = getInt(input.split(" ")[1]);
                        if (callRateTmp > 0) {
                            callRate.set(callRateTmp);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "\n Invalid Input. Try c<space><tps>");
                }
            } else if (input.equals("t")) {
                System.out.println("Current TPS: " + com.sterlite.voltdbloadgen.MainLoadGenerator.TPSCalculatorTask.getLastTPS());
                /*System.out.println("Current Initial Que Size: " + requestBuildTask.getInitialQueSize());
                System.out.println("Current Update Que Size: " +requestBuildTask.getUpdateQueSize());*/
            } else if (input.equals("q")) {
                shutdown();
                System.exit(0);
            } else {
                System.out.println("Invalid Input. Try Again");
            }
        }
    }

    private static void shutdown() throws Exception {
        requestProcessExecutor.shutdown();
        requestProcessExecutor.shutdownNow();
        client.drain();
        client.close();
    }

    /**
     * A Runnable dbtask to count TPM for pcrf submit request
     *
     * @author Milan Paliwal
     */

    private static class TPSCalculatorTask implements Runnable {

        private long lastResetTimeMillis;
        public static long lastTPS;

        public TPSCalculatorTask() {
            lastResetTimeMillis = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long tempReqCount = tpsRequestCounter.get();
            tpsRequestCounter.set(0);
            com.sterlite.voltdbloadgen.MainLoadGenerator.TPSCalculatorTask.lastTPS = tempReqCount;
            // System.out.println("TPS: " + tempReqCount);
        }

        public static long getLastTPS() {
            return lastTPS;
        }
    }
}
