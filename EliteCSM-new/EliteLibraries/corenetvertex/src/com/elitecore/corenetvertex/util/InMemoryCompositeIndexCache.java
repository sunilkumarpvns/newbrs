package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.kpi.handler.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by harsh on 5/21/16.
 */
public class InMemoryCompositeIndexCache<SI,PI,V> implements  Cache<SI,Iterator<V>>,CacheEventListener<PI,V> {

    @Nonnull private final Cache<PI,V> primaryCache;
    @Nonnull private final Function<V, PI> primaryKeyFunction;
    @Nonnull private final Function<V, SI> secondaryKeyFunction;
    @Nonnull private final SecondaryKeyCacheStatistics statistics;
    @Nonnull private ConcurrentHashMap<SI, Collection<PI>> l1Cache = new ConcurrentHashMap<SI, Collection<PI>>(CommonConstants.INITIAL_CAPACITY, CommonConstants.LOAD_FACTOR, CommonConstants.CONCURRENCY_LEVEL);
    @Nullable private CacheLoader<SI, Collection<V>> cacheLoader;

    private InMemoryCompositeIndexCache(TaskScheduler taskScheduler, Cache<PI, V> primaryCache, Function<V, PI> primaryKeyFunction, Function<V, SI> secondaryKeyFunction) {
        this.primaryCache = primaryCache;
        this.primaryKeyFunction = primaryKeyFunction;
        this.secondaryKeyFunction = secondaryKeyFunction;
        this.statistics = new SecondaryKeyCacheStatistics();
        taskScheduler.scheduleIntervalBasedTask(new CacheCleanUpTask());
    }

    public void add(SI key, V value) {

    	if (key == null) {
			return;
		}
    	
        PI primaryKey = primaryKeyFunction.apply(value);
        if(primaryKey == null) {
            return;
        }

        primaryCache.put(primaryKey, value);
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
    	statistics.incrementEvictionCount();
        int size = l1Cache.size();
        l1Cache.clear();
        return size;
    }

    @Override
    public CacheStatistics statistics() {
        return statistics;
    }

    @Override
    public Iterator<V> getWithoutLoad(SI key) {
    	
    	statistics.incrementRequestCount();
    	
        final Collection<PI> primaryKeys = l1Cache.get(key);
        
        if(Collectionz.isNullOrEmpty(primaryKeys)) {
        	statistics.incrementMissCount();
            return  null;
        } else {
        	statistics.incrementHitCount();
            return new CacheValueIterator(primaryKeys);
        }
    }

    @Override
    public void cacheRemoved(PI primaryKey, V value) {
		SI secondaryKey = secondaryKeyFunction.apply(value);
		if (secondaryKey == null) {
			return;
		}

		Collection<PI> primaryKeys = l1Cache.get(secondaryKey);
		if (primaryKeys != null) {
			primaryKeys.remove(primaryKey);
			
			synchronized (primaryKeys) {
				if (primaryKeys.isEmpty()) {
					l1Cache.remove(secondaryKey);
				}
			}
		}
	}

    @Override
    public void cacheAdded(PI key, V value) {
       
    	SI secondaryIndexKey = secondaryKeyFunction.apply(value);
        if (secondaryIndexKey == null) {
            return;
        }

        Collection<PI> previousValue = l1Cache.get(secondaryIndexKey);
        if(previousValue == null) {
        
        	createAndPutInCollection(key, secondaryIndexKey);
        } else {
        	
        	synchronized (previousValue) {
        		createAndPutInCollection(key, secondaryIndexKey);
			}
        }
    }

    @Override
    public void evict(Set<Map.Entry<PI, V>> entries) {
        entries.parallelStream().forEach(entry -> cacheRemoved(entry.getKey(), entry.getValue()));
    }

    private void createAndPutInCollection(PI key, SI secondaryIndexKey) {
		
		Collection<PI> list = new ConcurrentLinkedQueue<PI>();
		Collection<PI> previousValues = l1Cache.putIfAbsent(secondaryIndexKey, list);
		if(previousValues != null) {
			previousValues.add(key);
		}else {
		    list.add(key);
		}
	}

    public static class CacheBuilder<SI, PI,V> {

        private TaskScheduler taskScheduler;
        private PrimaryCache<PI,V> primaryCache;
        private CacheLoader<SI, Collection<V>> cacheLoader;
        private Function<V, PI> primaryKeyProvider;
        private Function<V, SI> secondaryKeyFunction;

        public CacheBuilder(TaskScheduler taskScheduler, PrimaryCache<PI,V> primaryCache, Function<V, PI> primaryKeyProvider, Function<V, SI> secondaryKeyFunction){
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

        public InMemoryCompositeIndexCache<SI, PI,V> build(){
            InMemoryCompositeIndexCache<SI, PI,V> cache = new InMemoryCompositeIndexCache<SI, PI,V>(taskScheduler,primaryCache, primaryKeyProvider, secondaryKeyFunction);
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

	@Override
	public Iterator<V> put(SI key, Iterator<V> iterator) {

		if (key == null) {
			return null;
		}
		
		Iterator<V> previousValuesIterator = remove(key);
		
		while (iterator.hasNext()) {
        	
        	V value = iterator.next();
        	PI primaryKey = primaryKeyFunction.apply(value);
        	if(primaryKey == null) {
                continue;
            }

        	 primaryCache.put(primaryKey, value);
        }
		
        return previousValuesIterator;
    }
	
	public Iterator<V> put(SI key, Collection<V> values) {

		if (key == null) {
			return null;
		}
		
		Iterator<V> previousValuesIterator = remove(key);

		for (V value : values) {
	        PI primaryKey = primaryKeyFunction.apply(value);
	        if(primaryKey == null) {
                continue;
            }
	        
	       primaryCache.put(primaryKey, value);
		}
		
        return previousValuesIterator;
    }
	
	@Override
	public Iterator<V> remove(SI key) {
		
		if (key == null) {
			return null;
		}
		
        final Collection<PI> primaryKeys = l1Cache.remove(key);
        if(Collectionz.isNullOrEmpty(primaryKeys)) {
            return  null;
        }

        Collection<V> valuesToReturn = new ConcurrentLinkedQueue<V>();
        for (PI primaryKey : primaryKeys) {
        	
        	V removedPrimaryKey = primaryCache.remove(primaryKey);
        	if (removedPrimaryKey != null) {
        		valuesToReturn.add(removedPrimaryKey);
        	}
        }
        
        return valuesToReturn.iterator(); 
    }

	@Override
	public Iterator<V> get(SI key) throws Exception {
        Iterator<V> valueIterator = getWithoutLoad(key);

        if(valueIterator == null) {
            if(cacheLoader != null) {
            	
            	long startLoadingTime = System.currentTimeMillis();
                Collection<V> values = cacheLoader.load(key);
                long totalLoadingTime = System.currentTimeMillis() - startLoadingTime;
                
                statistics.incrementLoadCount();
                statistics.incrementAverageLoadPanelty(totalLoadingTime);
                
                if(values != null) {
                    put(key,values);
                    return values.iterator();
                }
            }
        }

        return valueIterator;
    }
	
	@Override
	public Iterator<V> refresh(SI key) throws Exception {
      
		statistics.incrementRequestCount();
        Preconditions.checkNotNull(key, "key is null");

        if (this.cacheLoader == null) {
            return null;
        }

        Collection<V> values = null;
        long startLoadingTime = System.currentTimeMillis();
        values = cacheLoader.reload(key);
        long totalLoadingTime = System.currentTimeMillis() - startLoadingTime;
        statistics.incrementLoadCount();
       
        if (values == null) {
        	return null;
        }
        put(key, values);
        statistics.incrementAverageLoadPanelty(totalLoadingTime);
        
        return values.iterator();
    }
	
	private class CacheValueIterator implements Iterator<V> {
		
    	private Iterator<PI> primaryIndexIterator;
    	private V value;
    	private boolean hasNextVal;
    	private boolean hasNextCalled;
    	private boolean nextCalled;
    	
		private Collection<PI> primaryKeys;

    	public CacheValueIterator(Collection<PI> primaryKeys) {
    		this.primaryKeys = primaryKeys;
    	}
    	
		@Override
		public boolean hasNext() {
			
			if(primaryIndexIterator == null) {
				primaryIndexIterator = primaryKeys.iterator();
				primaryKeys = null;
			}
			
			if(hasNextCalled && nextCalled == false) {
				return hasNextVal;
			}
			
			hasNextCalled = true;
			
			while(true) {
				boolean hasNext = primaryIndexIterator.hasNext();
				if(hasNext) {
					PI primaryIndexKey = primaryIndexIterator.next();
					V data = primaryCache.getWithoutLoad(primaryIndexKey);
					
					if(data != null) {
						value = data;
						hasNextVal = true;
						break;
					} else {
						primaryIndexIterator.remove();
					}
				} else {
					hasNextVal = false;
					break;
				}
			}
			
			nextCalled = false;
			return hasNextVal;
			
			
		}

		@Override
		public V next() {
			nextCalled = true;
			if(hasNextCalled) {
				return storeAndReset();
			} else {
				if(hasNext()) {
					return storeAndReset();
				} else {
					throw new NoSuchElementException();
				}
			}
			
		}

		private V storeAndReset() {
			V temp = value;
			reset();
			return temp;
		}

		private void reset() {
			value = null;
			hasNextCalled = false;
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException("Remove operation from cache iterator is not supported");
		}
	
		}
}
