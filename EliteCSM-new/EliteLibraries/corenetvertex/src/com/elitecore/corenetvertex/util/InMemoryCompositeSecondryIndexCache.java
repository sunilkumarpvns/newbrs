package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.kpi.handler.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by harsh on 5/21/16.
 */
public class InMemoryCompositeSecondryIndexCache<SI,PI,V> implements  Cache<SI,Collection<V>>,CacheEventListener<PI,V> {

    private static final int CONCURRENCY_LEVEL = 50;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int INITIAL_CAPACITY = 10000;


    @Nonnull private final Cache<PI,V> primaryCache;

    @Nonnull private final Function<V, PI> primaryKeyFunction;
    @Nonnull private final Function<V, SI> secondaryKeyFunction;
    @Nonnull private final SecondaryKeyCacheStatistics statistics;


    @Nonnull private ConcurrentHashMap<SI, Collection<PI>> l1Cache = new ConcurrentHashMap<SI, Collection<PI>>(INITIAL_CAPACITY,LOAD_FACTOR,CONCURRENCY_LEVEL);


    @Nullable private CacheLoader<SI, Collection<V>> cacheLoader;


    private InMemoryCompositeSecondryIndexCache(TaskScheduler taskScheduler, Cache<PI, V> primaryCache, Function<V, PI> primaryKeyFunction, Function<V, SI> secondaryKeyFunction) {
        this.primaryCache = primaryCache;
        this.primaryKeyFunction = primaryKeyFunction;
        this.secondaryKeyFunction = secondaryKeyFunction;
        this.statistics = new SecondaryKeyCacheStatistics();
        taskScheduler.scheduleIntervalBasedTask(new CacheCleanUpTask());
    }



    public void add(SI key, V value) {

        PI primaryKey = primaryKeyFunction.apply(value);
        if(primaryKey == null) {
            return;
        }

        Collection<PI> values = l1Cache.get(key);
        if(values == null) {
            List<PI> list = Collections.synchronizedList(new LinkedList<PI>());
            values = l1Cache.putIfAbsent(key, list);
            if(values != null) {
                values.add(primaryKey);
            }else {
                list.add(primaryKey);
            }
        } else {
            values.add(primaryKey);
        }
    }

    @Override
    public Collection<V> put(SI key, Collection<V> values) {

        List<PI> primaryKeys = Collections.synchronizedList(new LinkedList<PI>());
        for(V value : values) {
            PI primaryKey = primaryKeyFunction.apply(value);
            if(primaryKey == null) {
                continue;
            }

            primaryKeys.add(primaryKey);
        }

        Collection<V> previousValue = remove(key);
        if(primaryKeys.isEmpty() == false) {
            l1Cache.put(key,primaryKeys);
        }

        return previousValue;

    }

    @Override
    public Collection<V> remove(SI key) {
        Collection<PI> primaryKeys = l1Cache.remove(key);
        if(Collectionz.isNullOrEmpty(primaryKeys)) {
            return  null;
        }

        List<V> values = new ArrayList<V>(primaryKeys.size());
        synchronized (primaryKeys) {

            for(PI primaryKey : primaryKeys) {
                V value = primaryCache.getWithoutLoad(primaryKey);
                if(value != null) {
                    values.add(value);
                }
            }
        }

        return values;

    }

    @Override
    public Collection<V> get(SI key) throws Exception {
        Collection<V> value = getWithoutLoad(key);

        if(value == null) {
            if(cacheLoader != null) {
                value = cacheLoader.load(key);
                if(value != null) {
                    put(key,value);
                }
            }
        }

        return value;
    }

    @Override
    public int flush() {
        int size = l1Cache.size();
        l1Cache.clear();
        statistics.clear();
        return size;
    }

    @Override
    public int evict() {
        int size = l1Cache.size();
        l1Cache.clear();
        return size;
    }

    @Override
    public Collection<V> refresh(SI key) throws Exception {
        statistics.incrementRequestCount();
        Preconditions.checkNotNull(key, "key is null");

        if (this.cacheLoader == null) {
            return null;
        }

        Collection<V> val = null;
        long startLoadingTime = System.currentTimeMillis();
        val = cacheLoader.reload(key);
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
    public Collection<V> getWithoutLoad(SI key) {
        Collection<PI> primaryKeys = l1Cache.get(key);
        if(Collectionz.isNullOrEmpty(primaryKeys)) {
            return  null;
        } else {
            ArrayList<V> values = new ArrayList<V>(primaryKeys.size());
            synchronized (primaryKeys) {
                for(PI primaryKey : primaryKeys) {
                    V value = primaryCache.getWithoutLoad(primaryKey);
                    if(value == null) {
                        remove(key);
                    } else {
                        values.add(value);
                    }
                }
            }


            return values;
        }
    }

    @Override
    public void cacheRemoved(PI key, V value) {
        SI indexKey = secondaryKeyFunction.apply(value);
        if(indexKey == null) {
            return;
        }

        Collection<PI> previousValue = l1Cache.get(indexKey);
        if(previousValue != null) {
            synchronized (previousValue) {
                previousValue.remove(key);
                if(previousValue.isEmpty()) {
                    l1Cache.remove(indexKey);
                }
            }
        }
    }

    @Override
    public void cacheAdded(PI key, V value) {
        SI indexKey = secondaryKeyFunction.apply(value);
        if (indexKey == null) {
            return;
        }

        Collection<PI> previousValue = l1Cache.get(indexKey);
        if(previousValue == null) {
            Collection<PI> list = Collections.synchronizedSet(new HashSet<PI>());
            previousValue = l1Cache.putIfAbsent(indexKey, list);
            if(previousValue != null) {
                previousValue.add(key);
            }else {
                list.add(key);
            }
        } else {
            previousValue.add(key);
        }
    }

    @Override
    public void evict(Set<Map.Entry<PI, V>> entries) {
        entries.parallelStream().forEach(entry -> cacheRemoved(entry.getKey(), entry.getValue()));
    }

    public static class CacheBuilder<SI, PI,V> {

        private TaskScheduler taskScheduler;
        private Cache<PI,V> primaryCache;
        private CacheLoader<SI, Collection<V>> cacheLoader;
        private Function<V, PI> primaryKeyProvider;
        private Function<V, SI> secondaryKeyFunction;

        public CacheBuilder(TaskScheduler taskScheduler, Cache<PI,V> primaryCache, Function<V, PI> primaryKeyProvider, Function<V, SI> secondaryKeyFunction){
            Preconditions.checkNotNull(primaryKeyProvider, "Primary key provider could not be null");
            Preconditions.checkNotNull(secondaryKeyFunction, "Secondary key provider could not be null");
            Preconditions.checkNotNull(primaryCache, "Primary cache could not be null");
            Preconditions.checkNotNull(taskScheduler, "Task scheduler could not be null");

            this.secondaryKeyFunction = secondaryKeyFunction;
            this.primaryKeyProvider = primaryKeyProvider;
            this.primaryCache = primaryCache;
            this.taskScheduler = taskScheduler;
        }

        public CacheBuilder<SI, PI,V> withCacheLoader(CacheLoader<SI,Collection<V>> cacheLoader){
            this.cacheLoader = cacheLoader;
            return this;
        }

        public InMemoryCompositeSecondryIndexCache<SI, PI,V> build(){
            InMemoryCompositeSecondryIndexCache<SI, PI,V> cache = new InMemoryCompositeSecondryIndexCache<SI, PI,V>(taskScheduler,primaryCache, primaryKeyProvider, secondaryKeyFunction);
            if(cacheLoader != null) {
                cache.cacheLoader = cacheLoader;
            }

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
