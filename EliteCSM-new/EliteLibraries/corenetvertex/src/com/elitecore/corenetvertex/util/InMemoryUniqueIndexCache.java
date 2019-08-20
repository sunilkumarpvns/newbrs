package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.kpi.handler.BaseIntervalBasedTask;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class InMemoryUniqueIndexCache<SI,PI,V> implements  Cache<SI,V>,CacheEventListener<PI,V> {
    private static final String MODULE = "IN-MEM-SEC-INDEX-CACHE";

    private static final int CONCURRENCY_LEVEL = 50;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int INITIAL_CAPACITY = 10000;


    @Nonnull private final Cache<PI,V> primaryCache;

    @Nonnull private final Function<V, PI> primaryKeyFunction;
    @Nonnull private final Function<V, SI> secondaryKeyFunction;
    @Nonnull private final SecondaryKeyCacheStatistics statistics;


    @Nonnull private ConcurrentHashMap<SI, PI> l1Cache = new ConcurrentHashMap<SI, PI>(INITIAL_CAPACITY,LOAD_FACTOR,CONCURRENCY_LEVEL);


    @Nullable private CacheLoader<SI, V> cacheLoader;


    private InMemoryUniqueIndexCache(TaskScheduler taskScheduler, Cache<PI, V> primaryCache, Function<V, PI> primaryKeyFunction, Function<V, SI> secondaryKeyFunction) {
        this.primaryCache = primaryCache;
        this.primaryKeyFunction = primaryKeyFunction;
        this.secondaryKeyFunction = secondaryKeyFunction;
        this.statistics = new SecondaryKeyCacheStatistics();
        taskScheduler.scheduleIntervalBasedTask(new CacheCleanUpTask());
    }


    @Override
    public V put(SI key, V value) {

        PI primaryKey = primaryKeyFunction.apply(value);
        if(primaryKey != null) {
            return primaryCache.put(primaryKey, value);
        }
        return null;
    }

    @Override
    public V remove(SI key) {
        PI primaryKey = l1Cache.remove(key);
        if(primaryKey == null) {
            return  null;
        }

        return primaryCache.remove(primaryKey);
    }

    @Override
    public V get(SI key) throws Exception {

        V value = getWithoutLoad(key);

        if(value == null) {
            if(cacheLoader != null) {
                long startLoadingTime = System.currentTimeMillis();
                 value = cacheLoader.load(key);
                long totalLoadingTime = System.currentTimeMillis() - startLoadingTime;
                statistics.incrementLoadCount();
                if(value != null) {
                   put(key,value);
                }
                statistics.incrementAverageLoadPanelty(totalLoadingTime);
            }
        }

        return value;
    }

    @Override
    public int flush() {
        if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
            LogManager.getLogger().debug(MODULE, "Flushing cache");
        }

        int size = l1Cache.size();
        l1Cache.clear();
        statistics.clear();

        if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
            LogManager.getLogger().debug(MODULE, "Total " + size + " Cache flushed");
        }
        return size;
    }

    @Override
    public int evict() {
        statistics.incrementEvictionCount();
        if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
            LogManager.getLogger().debug(MODULE, "Evicting cache");
        }
        int size = l1Cache.size();
        l1Cache.clear();
        if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
            LogManager.getLogger().debug(MODULE, "Total "+ size +" cache evict");
        }
        return size;
    }


    @Override
    public V refresh(SI key) throws Exception {
        statistics.incrementRequestCount();
        Preconditions.checkNotNull(key, "key is null");

        if (this.cacheLoader == null) {
            return null;
        }


        long startLoadingTime = System.currentTimeMillis();
        V val = cacheLoader.reload(key);
        long totalLoadingTime = System.currentTimeMillis() - startLoadingTime;
        statistics.incrementLoadCount();
        if (val != null) {
            put(key, val);
        }
        statistics.incrementAverageLoadPanelty(totalLoadingTime);
        return val;
    }

    @Override
    public CacheStatistics statistics() {
        return statistics;
    }

    @Override
    public V getWithoutLoad(SI key) {

        statistics.incrementRequestCount();

        PI primaryKey = l1Cache.get(key);

        if(primaryKey == null) {
            statistics.incrementMissCount();
            return  null;

        } else {
            V value = primaryCache.getWithoutLoad(primaryKey);
            if(value == null) {
                statistics.incrementMissCount();
                remove(key);
            }

            statistics.incrementHitCount();
            return value;
        }
    }

    @Override
    public void cacheRemoved(PI key, V value) {
        SI indexKey = secondaryKeyFunction.apply(value);
        if(indexKey != null) {
            l1Cache.remove(indexKey,key);
        }
    }

    @Override
    public void cacheAdded(PI key, V value) {
        SI indexKey = secondaryKeyFunction.apply(value);
        if(indexKey != null) {
            l1Cache.put(indexKey, key);
        }
    }

    @Override
    public void evict(Set<Map.Entry<PI, V>> entries) {
        entries.parallelStream().forEach(entry -> cacheRemoved(entry.getKey(), entry.getValue()));
    }

    public static class CacheBuilder<SI, PI,V> {

        private TaskScheduler taskScheduler;
        private PrimaryCache<PI,V> primaryCache;
        private CacheLoader<SI, V> cacheLoader;
        private Function<V, PI> primaryKeyProvider;
        private Function<V, SI> secondaryKeyFunction;

        public CacheBuilder(TaskScheduler taskScheduler, PrimaryCache<PI,V> primaryCache, Function<V, PI> primaryKeyProvider, Function<V, SI> secondaryKeyFunction){
            Preconditions.checkNotNull(primaryKeyProvider, "Primary key provider can not be null");
            Preconditions.checkNotNull(secondaryKeyFunction, "Secondary key provider can not be null");
            Preconditions.checkNotNull(primaryCache, "Primary cache can not be null");
            Preconditions.checkNotNull(taskScheduler, "Task scheduler can not be null");

            this.secondaryKeyFunction = secondaryKeyFunction;
            this.primaryKeyProvider = primaryKeyProvider;
            this.primaryCache = primaryCache;
            this.taskScheduler = taskScheduler;

        }

        public CacheBuilder<SI, PI,V> withCacheLoader(CacheLoader<SI,V> cacheLoader){
            this.cacheLoader = cacheLoader;
            return this;
        }

        public InMemoryUniqueIndexCache<SI, PI,V> build(){
            InMemoryUniqueIndexCache<SI, PI,V> cache = new InMemoryUniqueIndexCache<SI, PI,V>(taskScheduler,primaryCache, primaryKeyProvider, secondaryKeyFunction);
            if(cacheLoader != null) {
                cache.cacheLoader = cacheLoader;
            }
            primaryCache.registerEventListener(cache);

            return cache;
        }
    }

    private class CacheCleanUpTask extends BaseIntervalBasedTask {

        @Override
        public void execute() {
            evict();
        }

        @Override
        public long getInitialDelay() {
            return TimeUnit.HOURS.convert(24, TimeUnit.HOURS);
        }

        @Override
        public long getInterval() {
            return TimeUnit.HOURS.convert(24, TimeUnit.HOURS);
        }

        @Override
        public boolean isFixedDelay() {
            return true;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.HOURS;
        }
    }

    private class SecondaryKeyCacheStatistics extends AbstractCacheStatistics {

        @Override
        public long getCacheCount() {
            return l1Cache.size();
        }
    }
}
