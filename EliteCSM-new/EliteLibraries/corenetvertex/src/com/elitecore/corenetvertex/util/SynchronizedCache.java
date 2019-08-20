package com.elitecore.corenetvertex.util;

import com.elitecore.commons.kpi.handler.BaseIntervalBasedTask;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 1/11/17.
 */
@Path("/cache")
public class SynchronizedCache<K, V> implements PrimaryCache<K, V> {

    private static final String MODULE = "SYNC-CACHE";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String GET = "get";
    private static final String INVALIDATE = "invalidate";
    private static final String CONTEXT_PATH = "/netvertex/cache/";
    private static final String SERVER_SHUTDOWN = "server-stop";
    private static final String SERVER_START = "server-start";
    private static final String EXCHANGE_TPS = "exchange-tps";
    public static final String UTF_8 = "UTF-8";
    public static final String CACHE_SYNCH_CLEANUP_INTERVAL_MIN = "cache.sync.cleanup.interval.min";
    public static final String CACHE_SYNCH_MAX_DOWN_TIME_MILLIES = "cache.sync.max.thresholdtime.millies";
    public static final String CACHE_SYNCH_INVALIDATION_KEYS_BATCH_SIZE = "cache.sync.invalidate.key.batch.size";


    private volatile boolean running = true;
    private long lastDownTime;

    private long maxDownTimeInMillies = TimeUnit.MINUTES.toMillis(30);
    private long cleanUpIntervalInMin = 30;
    private long invalidationKeyBatchSize = 10;
    private final PrimaryCache<K, V> primaryCache;
    private final ThreadPoolExecutor synchronizerThreadPoolExecutor;
    private final Set<K> keysToBeSync;
    private final ExecutorService keySynchronizerExecutorService;
    private volatile State state;
    private final String restServerAddress;
    private final TaskScheduler taskScheduler;
    private final TPSProvider tpsProvider;
    private final Bifunction<String, K> keyBiFunction;
    private final Bifunction<String, V> valueBiFunction;
    private final HttpClient httpClient;

    private Future<?> unSynchorinizedKeyCleanUpTaskfuture;
    private Future<?> scannerFuture = null;

    public SynchronizedCache(PrimaryCache<K, V> primaryCache,
                             String restServerAddress,
                             TaskScheduler taskScheduler,
                             HttpClient httpClient,
                             TPSProvider tpsProvider,
                             Bifunction<String, K> keyBiFunction,
                             Bifunction<String, V> valueBiFunction) {
        this.primaryCache = primaryCache;
        this.restServerAddress = restServerAddress;
        this.taskScheduler = taskScheduler;
        this.httpClient = httpClient;
        this.tpsProvider = tpsProvider;
        this.keyBiFunction = keyBiFunction;
        this.valueBiFunction = valueBiFunction;
        this.keysToBeSync = Collections.newSetFromMap(new ConcurrentHashMap<K, Boolean>(CommonConstants.INITIAL_CAPACITY, CommonConstants.LOAD_FACTOR, CommonConstants.CONCURRENCY_LEVEL));
        this.synchronizerThreadPoolExecutor = new ThreadPoolExecutor(5, 15, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new EliteThreadFactory(MODULE, MODULE, Thread.NORM_PRIORITY));
        this.state = State.SYNC_STOP;
        this.keySynchronizerExecutorService = Executors.newSingleThreadExecutor(new EliteThreadFactory(MODULE, MODULE, Thread.NORM_PRIORITY));
    }

    public void init() {

        final SystemPropertyReaders.SystemPropertyReader<Long> cleanUpIntervalReader = new SystemPropertyReaders.NumberReaderBuilder(CACHE_SYNCH_CLEANUP_INTERVAL_MIN)
                .between(1, TimeUnit.DAYS.toMinutes(1))
                .onFail(30, "Error while reading parameter " + CACHE_SYNCH_CLEANUP_INTERVAL_MIN).build();

        cleanUpIntervalInMin = cleanUpIntervalReader.read();

        final SystemPropertyReaders.SystemPropertyReader<Long> maxThresholdReader = new SystemPropertyReaders.NumberReaderBuilder(CACHE_SYNCH_MAX_DOWN_TIME_MILLIES)
                .between(TimeUnit.SECONDS.toMillis(1), TimeUnit.DAYS.toMillis(1))
                .onFail(TimeUnit.MINUTES.toMillis(30), "Error while reading parameter " + CACHE_SYNCH_MAX_DOWN_TIME_MILLIES).build();

        maxDownTimeInMillies = maxThresholdReader.read();

        final SystemPropertyReaders.SystemPropertyReader<Long> maxInvalidateKeyBatchSize = new SystemPropertyReaders.NumberReaderBuilder(CACHE_SYNCH_INVALIDATION_KEYS_BATCH_SIZE)
                .between(1, 1000)
                .onFail(10, "Error while reading parameter " + CACHE_SYNCH_INVALIDATION_KEYS_BATCH_SIZE).build();

        invalidationKeyBatchSize = maxInvalidateKeyBatchSize.read();

        this.scannerFuture = this.taskScheduler.scheduleIntervalBasedTask(new StatusScanner());
        synchronizerThreadPoolExecutor.prestartAllCoreThreads();
        indicateStartSync();
        keySynchronizerExecutorService.execute(new SynchronizedKeyTask());
    }

    @Override
    public void registerEventListener(CacheEventListener<K, V> eventListener) {
        primaryCache.registerEventListener(eventListener);
    }

    @Override
    public V put(K key, V value) {
        V oldValue = primaryCache.put(key, value);
        executeAsyncAdd(key, value);
        return oldValue;
    }

    @Override
    public V remove(K key) {
        V value = primaryCache.remove(key);
        executeAsyncRemove(key);
        return value;
    }

    @Override
    public V get(K key) throws Exception {
        V value = getWithoutLoad(key);
        if (value == null) {
            value = primaryCache.refresh(key);
        }

        return value;
    }

    @Override
    public int flush() {
        return primaryCache.flush();
    }

    @Override
    public int evict() {
        return primaryCache.evict();
    }

    @Override
    public V refresh(K key) throws Exception {
        V newVal = primaryCache.refresh(key);
        primaryCache.put(key, newVal);
        executeAsyncAdd(key, newVal);
        return newVal;
    }

    @Override
    public CacheStatistics statistics() {
        return primaryCache.statistics();
    }


    @Override
    public V getWithoutLoad(K key) {
        V value = primaryCache.getWithoutLoad(key);
        if (value == null) {
            value = executeGet(key);

            if (value != null) {
                primaryCache.put(key, value);
            }
        }

        return value;
    }

    @GET
    @Path("/" + GET + "/{key}")
    public String getFromPrimary(@PathParam("key") String key) throws UnsupportedEncodingException {
        return URLEncoder.encode(valueBiFunction.from(primaryCache.getWithoutLoad(keyBiFunction.to(URLDecoder.decode(key, UTF_8)))), UTF_8);
    }

    @DELETE
    @Path("/" + REMOVE + "/{key}")
    public void keyRemoved(@PathParam("key") String key) throws UnsupportedEncodingException {
        primaryCache.remove(keyBiFunction.to(URLDecoder.decode(key, UTF_8)));
    }

    @POST
    @Path("/" + INVALIDATE)
    public void invalidate(String key) throws UnsupportedEncodingException {

        final Collection<String> keys = GsonFactory.defaultInstance().fromJson(URLDecoder.decode(key, UTF_8), new TypeToken<Collection<String>>() {}.getType());

        for (String data : keys) {
            K k = keyBiFunction.to(data);
            primaryCache.remove(k);
            keysToBeSync.remove(k);
        }

    }


    @PUT
    @Path("/" + ADD + "/{key}/{value}")
    public void keyAdded(@PathParam("key") String key, @PathParam("value") String value) throws UnsupportedEncodingException {
        primaryCache.put(keyBiFunction.to(URLDecoder.decode(key, UTF_8)), valueBiFunction.to(URLDecoder.decode(value, UTF_8)));
    }

    @PUT
    @Path("/" + SERVER_SHUTDOWN)
    public synchronized void remoteServerShutdown() {
        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Stopping sync. Reason: Notification received for Remote-NetVertex " + restServerAddress + " shutdown");

        this.state = State.SYNC_STOP;

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Clear all unsynchronized keys(" + keysToBeSync.size() + ")");
        keysToBeSync.clear();

        if (unSynchorinizedKeyCleanUpTaskfuture != null) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Stopping unsynchronized key clean up tasks");
            }
            this.unSynchorinizedKeyCleanUpTaskfuture.cancel(true);
        }
    }

    @PUT
    @Path("/" + SERVER_START)
    public synchronized void remoteServerStarted() {
        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Starting sync. Reason: Notification received for Remote-NetVertex " + restServerAddress + " start-up");

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Clear all unsynchronized keys(" + keysToBeSync.size() + ")");

        keysToBeSync.clear();

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Marking remote server up");

        this.state = State.UP;
        if (unSynchorinizedKeyCleanUpTaskfuture != null) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Stopping unsynchronized key clean up tasks");
            }
            this.unSynchorinizedKeyCleanUpTaskfuture.cancel(true);
        }
    }

    @GET
    @Path("/" + EXCHANGE_TPS + "/{tps}")
    public long exchangeTPS(@PathParam("tps")long tps) {
        markUp(tps);
        return tpsProvider.currentTPS();
    }


    private V executeGet(K key) {

        StringBuilder uri = null;
        try {
            uri = new StringBuilder("http://").append(restServerAddress).append(CONTEXT_PATH).append(GET).append("/").append(URLEncoder.encode(keyBiFunction.from(key), UTF_8));
        } catch (UnsupportedEncodingException ex) {
            getLogger().error(MODULE, "Error while encoding/decoding. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
            return null;
        }

        HttpGet request = new HttpGet(uri.toString());
        try {
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                LogManager.getLogger().info(MODULE, "HTTP Get Request URI : " + request.getURI());

            HttpResponse response = httpClient.execute(request);
            return valueBiFunction.to(URLDecoder.decode(handleResponse(response), UTF_8));
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while executing request(" + request.getURI() + "). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            markDown();
        }


        return null;
    }

    private void executeAsyncRemove(K key) {
        if (this.state == State.DOWN) {
            this.keysToBeSync.add(key);
        } else if (state == State.UP) {
            synchronizerThreadPoolExecutor.submit(new RemoveTask(key));
        }
    }

    private void executeAsyncAdd(K key, V value) {
        if (state == State.DOWN) {
            keysToBeSync.add(key);
        } else if (state == State.UP) {
            synchronizerThreadPoolExecutor.submit(new AddTask(key, value));
        }
    }

    private String handleResponse(HttpResponse response) throws IOException {

        StatusLine statusLine = response.getStatusLine();
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Http Response Code : " + statusLine.getStatusCode() + " and Reason Phrase: " + statusLine.getReasonPhrase());
        }
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300 || statusLine.getStatusCode() < 200) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return entity == null ? null : EntityUtils.toString(entity);
    }


    public synchronized void shutDown() {
        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Shutting down connection manager for " + restServerAddress);
        }

        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Shutting down scanner");
        }

        if(scannerFuture != null) {
            scannerFuture.cancel(true);
        }
        this.running = false;

        if (unSynchorinizedKeyCleanUpTaskfuture != null) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Stopping unsynchronized key clean up tasks");
            }
            this.unSynchorinizedKeyCleanUpTaskfuture.cancel(true);
        }

        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Stopping unsynchronized keys invalidation tasks");
        }
        this.keySynchronizerExecutorService.shutdownNow();

        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Stopping synchronizer tasks");
        }
        synchronizerThreadPoolExecutor.shutdownNow();

        indicateStopSync();
        httpClient.getConnectionManager().shutdown();
    }


    private synchronized void markUp(long remoteServerTPS) {
        if (this.state == State.UP) {
            return;
        }

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Marking remote cache as up");

        this.state = State.UP;

        final long totalDowntime = System.currentTimeMillis() - lastDownTime;
        final long currentTPS = tpsProvider.currentTPS();
        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Current TPS is " + currentTPS + " and remote server TPS is " + remoteServerTPS);
        }


        if (totalDowntime >= maxDownTimeInMillies && remoteServerTPS > currentTPS ) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Clear unsynchronized keys(" + keysToBeSync.size() + "). Reason: Down time(" + totalDowntime
                        + ") is  more than max down time" + maxDownTimeInMillies + "  and server TPS("+ currentTPS+") is more than remote server TPS("+ remoteServerTPS+")");
            }
            keysToBeSync.clear();
        } else {
            synchronized (keysToBeSync) {
                if (LogManager.getLogger().isInfoLogLevel()) {
                    LogManager.getLogger().info(MODULE, "Notify about remote cache up.Reason: Down time(" + totalDowntime
                            + ") is under than max down time" + maxDownTimeInMillies + "  Or server TPS("
                            + currentTPS + ") is more than remote server TPS(" + remoteServerTPS + ")");
                }
                keysToBeSync.notifyAll();
            }
        }

        if (unSynchorinizedKeyCleanUpTaskfuture != null) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Stopping unsynchronized key clean up tasks");
            }
            this.unSynchorinizedKeyCleanUpTaskfuture.cancel(true);
        }
    }

    public synchronized void markDown() {
        if (this.state == State.UP) {
            if(getLogger().isInfoLogLevel())
                getLogger().info(MODULE, "Marking remote cache as down");

            this.state = State.DOWN;
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Scheduling unsynchronized key clean up tasks");
            }
            unSynchorinizedKeyCleanUpTaskfuture = taskScheduler.scheduleIntervalBasedTask(new UnsynchronizedKeyCleanUpTask());
            this.lastDownTime = System.currentTimeMillis();
        }
    }

    private void indicateStopSync() {
        if (state == State.DOWN) {
            if(getLogger().isInfoLogLevel())
                getLogger().info(MODULE, "Skippig indication of shutdown to remote server(" + this.restServerAddress +"). Reason: Remote server is dead");
        } else if (state == State.UP) {
            StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                    .append(CONTEXT_PATH).append(SERVER_SHUTDOWN);

            if(getLogger().isInfoLogLevel())
                getLogger().info(MODULE, "Indicating shutdown task to remote server(" + this.restServerAddress +") using Request URI: " + uri.toString());
            HttpPut request = new HttpPut(uri.toString());

            try {
                httpClient.execute(request);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while executing request(" + request.getURI() + "). Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }

    private void indicateStartSync() {
        StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                .append(CONTEXT_PATH).append(SERVER_START);

        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
            LogManager.getLogger().info(MODULE, "Indicating start-up of user using Request URI: " + uri.toString());
        HttpPut request = new HttpPut(uri.toString());
        try {

            httpClient.execute(request);
            this.state = State.UP;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while executing request(" + request.getURI() + "). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }


    private class UnsynchronizedKeyCleanUpTask extends BaseIntervalBasedTask {
        @Override
        public void execute() {
            if (Thread.currentThread().isInterrupted() == false) {
                synchronized (SynchronizedCache.this) {
                    if(getLogger().isInfoLogLevel())
                        getLogger().info(MODULE, "Clear unsynchronized keys(" + keysToBeSync.size() + ")");
                    keysToBeSync.clear();
                }
            }
        }

        @Override
        public long getInterval() {
            return cleanUpIntervalInMin;
        }

        @Override
        public long getInitialDelay() {
            return cleanUpIntervalInMin;
        }

        @Override
        public boolean isFixedDelay() {
            return true;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MINUTES;
        }
    }

    private class RemoveTask implements Runnable {
        private final K key;

        private RemoveTask(K key) {
            this.key = key;
        }

        @Override
        public void run() {
            if (state == State.DOWN) {
                keysToBeSync.add(key);
            } else if (state == State.UP) {
                try {
                    StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                            .append(CONTEXT_PATH).append(REMOVE)
                            .append("/").append(URLEncoder.encode(keyBiFunction.from(key), UTF_8));
                    HttpDelete request = new HttpDelete(uri.toString());
                    executeREST(request, key);
                } catch (UnsupportedEncodingException ex) {
                    getLogger().error(MODULE, "Error while encoding key: " + key +". Reason: " + ex.getMessage());
                    getLogger().trace(MODULE, ex);
                }

            }
        }
    }

    private class AddTask implements Runnable {
        private final K key;
        private V value;

        private AddTask(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void run() {
            if (state == State.DOWN) {
                keysToBeSync.add(key);
            } else if (state == State.UP) {
                try {
                    StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                            .append(CONTEXT_PATH).append(ADD).append("/")
                            .append(URLEncoder.encode(keyBiFunction.from(key), "UTF-8")).append('/').append(URLEncoder.encode(valueBiFunction.from(value), "UTF-8"));

                    HttpPut request = new HttpPut(uri.toString());
                    executeREST(request, key);
                } catch (UnsupportedEncodingException ex)  {
                    getLogger().error(MODULE, "Error while encoding key: " + key +". Reason: " + ex.getMessage());
                    getLogger().trace(MODULE, ex);
                }

            }
        }


    }

    private void executeREST(HttpRequestBase request, K key) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
            LogManager.getLogger().info(MODULE, "HTTP Request URI : " + request.getURI());

        try {
            httpClient.execute(request);
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while executing request(" + request.getURI() + "). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            keysToBeSync.add(key);
            markDown();
        }
    }

    private class SynchronizedKeyTask implements Runnable {

        private volatile boolean stopIndicated;

        @Override
        public void run() {

            while (running) {
                waitUntilSynchronizerUp();
                if (stopIndicated) {
                    break;
                }
                synchronizedKeys();
            }
        }

        private void waitUntilSynchronizerUp() {
            while (Thread.currentThread().isInterrupted() == false && (state != State.UP || keysToBeSync.isEmpty())) {
                synchronized (keysToBeSync) {
                    try {
                        keysToBeSync.wait(TimeUnit.MINUTES.toMillis(10));
                    } catch (InterruptedException e) { //NOSONAR "InterruptedException" should not be ignored
                        stopIndicated = true;
                        break;
                    }
                }
            }
        }

        private void synchronizedKeys() {
            final Iterator<K> keyIterator = keysToBeSync.iterator();

            if(getLogger().isInfoLogLevel())
                getLogger().info(MODULE, "Start invalidating unsynchronized keys");

            int count = 0;

            List<String> keys = new ArrayList<String>();

            while (keyIterator.hasNext()) {
                final K key = keyIterator.next();

                keyIterator.remove();
                keys.add(keyBiFunction.from(key));

                if(keys.size() >= invalidationKeyBatchSize) {

                    final boolean callSucceed = executeInvalidateREST(keys);
                    int size = keys.size();
                    keys.clear();

                    if (callSucceed == false) {
                        addKeysBackLog(keys);
                        break;
                    } else {
                        count += size;
                    }
                }

            }

            if(keys.isEmpty() == false) {
                if(executeInvalidateREST(keys) == false) {
                    addKeysBackLog(keys);
                } else {
                    count += keys.size();
                }
            }

            if(getLogger().isInfoLogLevel())
                getLogger().info(MODULE, "Total "+ count +" unsynchronized keys invalidated");
        }

        private void addKeysBackLog(List<String> keys) {
            for (String key : keys) {
                keysToBeSync.add(keyBiFunction.to(key));
            }
        }

        private boolean executeInvalidateREST(List<String> keys) {
            StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                    .append(CONTEXT_PATH).append(INVALIDATE);

            HttpPost request = new HttpPost(uri.toString());
            try {
                request.setEntity(new StringEntity(URLEncoder.encode(GsonFactory.defaultInstance().toJson(keys), UTF_8)));

                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "HTTP Request URI : " + request.getURI());

                httpClient.execute(request);

                return true;
            } catch (UnsupportedEncodingException ex) {
                getLogger().error(MODULE, "Error encoding key: " + keys.toString() + ". Reason: " + ex.getMessage());
                getLogger().trace(MODULE, ex);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while executing request(" + uri + "). Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
                markDown();

            }

            return false;
        }


    }

    private class StatusScanner extends BaseIntervalBasedTask {

        @Override
        public void execute() {
            if (state == State.UP) {
               return;
            }

            StringBuilder uri = new StringBuilder("http://").append(restServerAddress)
                    .append(CONTEXT_PATH).append(EXCHANGE_TPS).append('/').append(tpsProvider.currentTPS());

            HttpGet request = new HttpGet(uri.toString());
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            synchronized (SynchronizedCache.this) {
                try {

                    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                        LogManager.getLogger().info(MODULE, "HTTP Request URI : " + request.getURI());

                    final HttpResponse response = httpClient.execute(request);
                    long remoteServerTPS = handleResponse(response);
                    if (Thread.currentThread().isInterrupted() == false) {
                        markUp(remoteServerTPS);
                    }
                } catch (Exception e) {
                    getLogger().error(MODULE, "Error while executing request(" + request.getURI() + "). Reason: " + e.getMessage());
                    getLogger().trace(MODULE, e);
                }
            }
        }

        private long handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Http Response Code : " + statusLine.getStatusCode() + " and Reason Phrase: " + statusLine.getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300 || statusLine.getStatusCode() < 200) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }

            return Long.parseLong(EntityUtils.toString(entity));
        }

        @Override
        public long getInterval() {
            return 1;
        }

        @Override
        public long getInitialDelay() {
            return 1;
        }

        @Override
        public boolean isFixedDelay() {
            return true;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MINUTES;
        }
    }

    private enum State {
        UP,
        DOWN,
        SYNC_STOP
    }


}
