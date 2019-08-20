package com.sterlite.voltdbloadgen.task;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.voltdb.client.Client;

public class RequestBuildTask implements Runnable {

    private java.util.concurrent.ScheduledThreadPoolExecutor executor;
    private final java.util.concurrent.atomic.AtomicLong tpsRequestCounter;
    private Client client;
    private java.util.concurrent.atomic.AtomicLong currentSubscriberCounter;
    private long totalSubscribers;
    private int updates;
    private java.util.concurrent.atomic.AtomicInteger callrate;
    private int updateInterval;
    private java.util.concurrent.ConcurrentLinkedQueue<com.sterlite.voltdbloadgen.task.RequestTask> initialQue;
    private java.util.concurrent.ConcurrentLinkedQueue<com.sterlite.voltdbloadgen.task.RequestTask> updateQueue;
    private java.util.Random random;
    private RequestBuildContext requestBuildContext;

    public RequestBuildTask(ScheduledThreadPoolExecutor executor, AtomicLong tpsRequestCounter, Client client,
                            long totalSubscriber,
                            AtomicLong currentSubscriberCounter,
                            int updates,
                            AtomicInteger callrate, int updateInterval) {
        this.executor = executor;
        this.tpsRequestCounter = tpsRequestCounter;
        this.client = client;
        this.currentSubscriberCounter = currentSubscriberCounter;
        this.totalSubscribers = totalSubscriber;
        this.updates = updates;
        this.callrate = callrate;
        this.updateInterval = updateInterval;
        this.initialQue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        this.updateQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        this.random = new java.util.Random();
        this.requestBuildContext = new RequestBuildContextImpl();
    }

    public void init() {
        for (int i = 0; i < totalSubscribers; i++) {
            initialQue.add(new RequestTask(1, client, Long.toString(currentSubscriberCounter.getAndIncrement()),
                    updates, tpsRequestCounter, requestBuildContext));
        }
    }

    @Override
    public void run() {

        for (int i = 0; i < callrate.get()/20; i++) {
            RequestTask requestTask = getRequestFromQue();
            if (requestTask == null) {
                return;
            }
            executor.submit(requestTask);
        }
    }

    private RequestTask getRequestFromQue() {
        RequestTask requestTask = updateQueue.poll();
        if (requestTask != null) {
            return requestTask;
        }
        return initialQue.poll();
    }

    private class AddUpdateQueueTask implements Runnable {
        private RequestTask requestTask;

        public AddUpdateQueueTask(RequestTask requestTask) {
            this.requestTask = requestTask;
        }

        @Override
        public void run() {
            updateQueue.add(requestTask);
        }
    }

    public int getUpdateQueSize() {
        return updateQueue.size();
    }

    public int getInitialQueSize() {
        return initialQue.size();
    }

    private class RequestBuildContextImpl implements RequestBuildContext {

        @Override
        public void addToInitialQueue(RequestTask requestTask) {
            initialQue.add(requestTask);
        }

        @Override
        public void scheduleUpdate(RequestTask requestTask) {
            executor.schedule(new AddUpdateQueueTask(requestTask),
                    updateInterval + random.nextInt(1000), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}
