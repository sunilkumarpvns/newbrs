package com.elitecore.corenetvertex.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by harsh on 2/8/17.
 */
@Ignore
public class SynbchronizedCacheTest {

    @Mock private CacheLoader<String, String> cacheLoader;
    private SynchronizedCache<String, String> synchronizedCache;
    private TestableHttpClient httpClient;
    private PartitioningCache<String, String> partitioningCache;
    private TestableTaskScheduler taskSchedular;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.taskSchedular = new TestableTaskScheduler();
        this.partitioningCache = new PartitioningCache.CacheBuilder<String, String>(Mockito.mock(TaskScheduler.class)).withCacheLoader(cacheLoader).build();
        this.httpClient = new TestableHttpClient("/netvertex/cache/");
        this.synchronizedCache = new SynchronizedCache<String, String>(partitioningCache, "127.0.0.1", taskSchedular, httpClient, new TPSProvider() {
            @Override
            public long currentTPS() {
                return 0;
            }
        }, new Bifunction<String, String>() {
            @Override
            public String to(String s) {
                return s;
            }

            @Override
            public String from(String s) {
                return s;
            }
        }, new Bifunction<String, String>() {
            @Override
            public String to(String s) {
                return s;
            }

            @Override
            public String from(String s) {
                return s;
            }
        });
        synchronizedCache.init();
        synchronizedCache.exchangeTPS(1000);
    }

    @Test
    public void getShouldNotCallRemoteCacheWhenSessionFoundFromLocalCache() throws Exception {
        partitioningCache.put("test", "test");
        synchronizedCache.get("test");
        httpClient.checkNoInteraction("add/test/test");
    }

    @Test
    public void getShouldFetchSessionFromRemoteCacheWhenNotFoundFromLocalCache() throws Exception {
        httpClient.setResponseForPath("get/test", 201, "SUCCESS", "test");
        synchronizedCache.get("test");
        httpClient.checkPathCalled("get/test");
        verifyZeroInteractions(cacheLoader);
    }

    @Test
    public void getShouldFetchSessionFromCacheLoaderWhenNotFoundFromRemoteAndLocalCache() throws Exception {
        synchronizedCache.get("test");
        httpClient.checkPathCalled("get/test");
        verify(cacheLoader, times(1)).reload(any(String.class));
    }

    @Test
    public void putShouldAddDataInRemotelyCache() throws Exception {
        synchronizedCache.put("test", "test_result");
        Thread.sleep(1000);
        httpClient.checkPathCalled("add/test/test_result");
    }

    @Test
    public void remoteShouldRemoteDataFromRemoteCache() throws Exception {
        synchronizedCache.put("test", "test_result");
        synchronizedCache.remove("test");
        Thread.sleep(1000);
        httpClient.checkPathCalled("remove/test");
    }



    @Test
    public void remoteServerShouldStopSynchronizationOnRemoteServerShutdown() throws Exception {
        synchronizedCache.remoteServerShutdown();
        synchronizedCache.put("test", "test_result");
        httpClient.setResponseForPath("get/test", 201, "SUCCESS", "test");
        synchronizedCache.remove("test");
        Thread.sleep(100);
        httpClient.checkNoInteraction("add/test/test_result");
        httpClient.checkNoInteraction("remove/test");
    }

    @Test
    public void remoteServerShouldSendAllDataToRemoteCacheToInvalidateWhichReceivedDuringLinkDownWhenLinkUp() throws Exception {
        httpClient.throwExceptionOnAnyCall("add/test/test", new Exception("Link down exception"));
        httpClient.throwExceptionOnAnyCall("add/test1/test1", new Exception("Link down exception"));
        httpClient.throwExceptionOnAnyCall("add/test2/test2", new Exception("Link down exception"));
        httpClient.setResponseForPath("exchange-tps/0", 201, "SUCCESS", "100");

        synchronizedCache.put("test", "test");
        httpClient.waitTillCallReceived("add/test/test", TimeUnit.MINUTES.toMillis(1));
        synchronizedCache.put("test1", "test1");
        httpClient.waitTillCallReceived("add/test1/test1", TimeUnit.MINUTES.toMillis(1));
        synchronizedCache.put("test2", "test2");
        httpClient.waitTillCallReceived("add/test2/test2", TimeUnit.MINUTES.toMillis(1));

        Thread.sleep(1000);
        taskSchedular.executeIntervalBaseTask();

        httpClient.waitTillCallReceived("invalidate/test", TimeUnit.MINUTES.toMillis(2));
        httpClient.waitTillCallReceived("invalidate/test1", TimeUnit.MINUTES.toMillis(1));
        httpClient.waitTillCallReceived("invalidate/test2", TimeUnit.MINUTES.toMillis(1));
    }

    @Test
    public void synchronizedCacheShouldNotifyStartOfServer() throws Exception {
        httpClient.checkPathCalled("server-start");
    }

    @Test
    public void synchronizedCacheShouldNotifyShutdownOfServer() throws Exception {
        synchronizedCache.shutDown();
        httpClient.checkPathCalled("server-stop");
    }

    @Test
    public void synchronizedCacheShouldShutdownHttpConnectorOnShutdown() throws Exception {
        synchronizedCache.shutDown();
        httpClient.checkClosed();
    }


}
