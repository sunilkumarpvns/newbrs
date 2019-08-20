package com.sterlite.voltdbloadgen;

import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This Class is kept for only reference purpose
 * Use MainSingleOperationLoad Class.
 */
@Deprecated
public class Main {

	private static Client[] clients;
	private static java.util.concurrent.Callable[] tasks;
	private static int totalSubscribers;
	private static int noOfThreads;
	private static java.util.concurrent.ExecutorService executor;
	private static java.util.concurrent.CountDownLatch countDownLatch;

	private static final String SELECT_OPERATION = "SELECT";
	private static final String INSERT_OPERATION = "INSERT";

	public static void main(String[] params) throws Exception {

		if (params.length < 6) {
			System.out.println("HELP:");
			System.out.println("Provide params in this sequence: <ip> <port> <clientShared> <threads> <subscribercount> <operation>");
			System.out.println("ip=voltdb IPs");
			System.out.println("port= voltdb port");
			System.out.println("clients=0(shared), 1(individual)");
			System.out.println("threads=No of Threads");
			System.out.println("subscriberCount=No of subscribers");
			System.out.println("operation=" + SELECT_OPERATION + ", " + INSERT_OPERATION);
			return;
		}

        System.out.println("ip=" + params[0]);
        System.out.println("port="+ params[1]);
        System.out.println("clients=0(shared), 1(individual)=" + params[2]);
        System.out.println("threads="+ params[3]);
        System.out.println("subscriberCount="+ params[4]);
        System.out.println("operation="+ params[5]);

		noOfThreads = getInt(params[3]);
		totalSubscribers = getInt(params[4]);
		countDownLatch = new java.util.concurrent.CountDownLatch(noOfThreads);

		setUp(params[0], getInt(params[1]), getInt(params[2]), params[5]);
		executor = java.util.concurrent.Executors.newFixedThreadPool(5);

		java.util.List<java.util.concurrent.Future<Integer>> fts = new java.util.ArrayList<>();

		for (int i=0; i < tasks.length; i++) {
			fts.add(executor.submit(tasks[i]));
		}

		waitTillAllTaskComplete();

		System.out.println("Actual Total Time: " + getTime(fts));

		shutdown();
	}

	private static int getTime(java.util.List<java.util.concurrent.Future<Integer>> fts) throws Exception {
		int count = 0;
		for(java.util.concurrent.Future<Integer> future : fts) {
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

		for (Client client : clients) {
			client.drain();
			client.close();
		}
	}

	private static int getInt(String param) {
		return Integer.parseInt(param);
	}

	public static void setUp(String ip, int port, int clientShared, String operationName) throws Exception {

		ClientConfig config = new ClientConfig("voltdb", "voltdb");
		config.setProcedureCallTimeout(1000);

		if (clientShared == 0) {
			clients = new Client[1];
		} else {
			clients = new Client[noOfThreads];
		}

		for (int i=0; i < clients.length; i++) {
			clients[i] = ClientFactory.createClient(config);
            String[] ips = ip.split(",");

            clients[i].createConnection(ips[0], port);

            if (ips.length == 2) {
                clients[i].createConnection(ips[1], port);
            }
		}

		tasks = new java.util.concurrent.Callable[noOfThreads];

		java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger();

		for (int i=0; i < tasks.length; i++) {
			if (clientShared == 0) {
				tasks[i] = getTask(operationName, counter, i, clients[0]);
			} else {
				tasks[i] = getTask(operationName, counter, i, clients[i]);
			}
		}
	}

	private static java.util.concurrent.Callable getTask(String operationName, java.util.concurrent.atomic.AtomicInteger counter, int i, Client client) {

		if (SELECT_OPERATION.equalsIgnoreCase(operationName)) {
			return new com.sterlite.voltdbloadgen.Main.SelectTask(i, client, counter);
		} else if (INSERT_OPERATION.equalsIgnoreCase(operationName)) {
			return new com.sterlite.voltdbloadgen.Main.InsertTask(i, client, counter);
		} else {
			throw new UnsupportedOperationException("Operation(" + operationName + ") Not Supported");
		}
	}

	static class SelectTask implements java.util.concurrent.Callable<Integer> {

		private int id;
		private Client client;
		private final String procedure = "VoltSelectProfileById";
		private java.util.concurrent.atomic.AtomicInteger counter;

		SelectTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter) {
			this.id = id;
			this.client = client;
			this.counter = counter;
		}

		@Override
		public Integer call() {
			int totalTime=0;
			int intremTotal = 0;
			System.out.println("Id: " + id + " starts: " + procedure);
			for (int i=1; i <=totalSubscribers/noOfThreads; i++) {

				try {
					long queryExecutionTime = getCurrentTime();
					client.callProcedure(procedure, counter.getAndIncrement());
					queryExecutionTime = getCurrentTime() - queryExecutionTime;
					totalTime +=queryExecutionTime;
					intremTotal += queryExecutionTime;

					if (i%1000 == 0) {
						System.out.println("Id: " + id + ", Procedure: " + procedure + ", TimeTakenFor1000Req(ms): " + intremTotal);
						intremTotal = 0;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			countDownLatch.countDown();

			System.out.println("Id: " + id + ", complete: " + procedure + ", Total Time=" + totalTime);
			return totalTime;
		}


	}

	static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	static class InsertTask implements java.util.concurrent.Callable<Integer> {

		private int id;
		private Client client;
		private final String procedure = "VoltAddProfileSPR";
		private java.util.concurrent.atomic.AtomicInteger counter;

		InsertTask(int id, Client client, java.util.concurrent.atomic.AtomicInteger counter) {
			this.id = id;
			this.client = client;
			this.counter = counter;
		}

		@Override
		public Integer call() {

			int total=0;
			int intremTotal = 0;
			System.out.println("Id: " + id + " starts: " + procedure);
			for (int i=1; i <=totalSubscribers/noOfThreads; i++) {

				try {
					long queryExecutionTime = getCurrentTime();
					client.callProcedure("VoltAddProfileSPR", getArray(counter.getAndIncrement() + "a"), new java.util.Date(), new java.util.Date(2018,10,10));
					queryExecutionTime = getCurrentTime() - queryExecutionTime;
					total +=queryExecutionTime;
					intremTotal += queryExecutionTime;

					if (i%1000 == 0) {
						System.out.println("Id: " + id + ", Procedure: " + procedure + ", TimeTakenFor1000Req(ms): " + intremTotal);
						intremTotal = 0;
					}

				} catch (java.io.IOException e) {
					e.printStackTrace();
				} catch (ProcCallException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			countDownLatch.countDown();
			System.out.println("Id: " + id + ", complete: " + procedure + ", Total Time=" + total);
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
}
