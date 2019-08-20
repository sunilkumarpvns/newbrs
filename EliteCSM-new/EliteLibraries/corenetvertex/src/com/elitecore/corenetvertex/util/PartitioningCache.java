package com.elitecore.corenetvertex.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.kpi.handler.BaseIntervalBasedTask;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;

public class PartitioningCache<K,V> implements PrimaryCache<K,V> {

	private static final String MODULE = "PARTITION-CACHE";

	private static final int CONCURRENCY_LEVEL = 50;
	private static final float LOAD_FACTOR = 0.75f;
	private static final int INITIAL_CAPACITY = 10000;
	@Nonnull
	private final PartitioningCacheStatistics statistics;
	private final long evictDuration;
	@Nonnull
	private ConcurrentHashMap<K, V> l1Cache = new ConcurrentHashMap<K, V>(INITIAL_CAPACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
	/*
	 * Why 8 as initial capacity ?
	 * 
	 * L2 and L3 cache will not be used until evict. During "evict" we assign L1 cache to L2 and L2 cache to L3. See evict().
	 * 
	 * so these two caches is created just to avoid null check nothing else.
	 * 
	 * Why Concurrency level are same?
	 * 
	 * 	to avoid contention in get(). If we provide default concurrency level(16), contention may occur.
	 * 
	 */
	@Nonnull
	private ConcurrentHashMap<K, V> l2Cache = new ConcurrentHashMap<K, V>(8, LOAD_FACTOR, CONCURRENCY_LEVEL);
	@Nonnull
	private ConcurrentHashMap<K, V> l3Cache = new ConcurrentHashMap<K, V>(8, LOAD_FACTOR, CONCURRENCY_LEVEL);

	@Nullable
	public CacheLoader<K, V> cacheLoader;

	@Nonnull
	private final Object synch;
	@Nonnull
	private final List<CacheEventListener<K, V>> eventListeners;

	private PartitioningCache(TaskScheduler taskScheduler, long evictDuration) {
		this.evictDuration = evictDuration;
		taskScheduler.scheduleIntervalBasedTask(new CacheCleanUpTask());
		this.synch = new Object();
		this.statistics = new PartitioningCacheStatistics();
		this.eventListeners = new CopyOnWriteArrayList<CacheEventListener<K, V>>();
	}

	@Override
	public V put(K key, V value) {
		V oldValue = l1Cache.put(key, value);
		if (oldValue == null) {
			oldValue = l2Cache.remove(key);

			if (oldValue == null) {
				oldValue = l3Cache.remove(key);
			}
		}


		if (oldValue != null) {
			notifyAboutCacheRemoval(key, oldValue);
		}

		notifyAboutCacheAdd(key, value);

		return oldValue;
	}

	@Override
	public V remove(K key) {
		V val = l1Cache.remove(key);

		if (val == null) {

			val = l2Cache.remove(key);

			if (val == null) {

				val = l3Cache.remove(key);
			}
		}

		if (val != null) {
			notifyAboutCacheRemoval(key, val);
		}
		return val;

	}

	@Override
	public V get(K key) throws Exception {

		V val = getWithoutLoad(key);

		if (val == null) {
			if (this.cacheLoader != null) {
				long startLoadingTime = System.currentTimeMillis();
				val = cacheLoader.load(key);
				long totalLoadingTime = System.currentTimeMillis() - startLoadingTime;
				statistics.incrementLoadCount();
				if (val != null) {
					put(key, val);

				}
				statistics.incrementAverageLoadPanelty(totalLoadingTime);

			}

		}

		return val;
	}

	@Override
	public V refresh(K key) throws Exception {
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
			promote(key, val);
		}
		statistics.incrementAverageLoadPanelty(totalLoadingTime);
		return val;
	}

	private V promote(K key, V val) {
		V oldValue = l1Cache.put(key, val);

		if (oldValue == null) {

			oldValue = l2Cache.remove(key);

			if (oldValue == null) {

				oldValue = l3Cache.remove(key);

			}
		}

		return oldValue;
	}

	public static class CacheBuilder<K, V> {

		private TaskScheduler taskSchedular;
		private CacheLoader<K, V> cacheLoader;
		private long evictDuration;

		public CacheBuilder(TaskScheduler taskScheduler) {
			this(taskScheduler, TimeUnit.HOURS.toSeconds(10));
		}

		public CacheBuilder(TaskScheduler taskScheduler, long evictDuration) {
			Preconditions.checkNotNull(taskScheduler, "Task scheduler can not be null");
			this.taskSchedular = taskScheduler;
			this.evictDuration = evictDuration;
		}

		public CacheBuilder<K, V> withCacheLoader(CacheLoader<K, V> cacheLoader) {
			this.cacheLoader = cacheLoader;
			return this;
		}

		public PartitioningCache<K, V> build() {
			PartitioningCache<K, V> cache = new PartitioningCache<K, V>(taskSchedular, evictDuration);
			if (cacheLoader != null)
				cache.cacheLoader = cacheLoader;
			return cache;
		}
	}


	@Override
	public int flush() {
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Flushing cache");
		}

		Map<K, V> oldL1Cache = l1Cache;
		Map<K, V> oldL2Cache = l2Cache;
		Map<K, V> oldL3Cache = l3Cache;
		int count;
		synchronized (synch) {
			count = l1Cache.size() + l2Cache.size() + l3Cache.size();

			l1Cache = new ConcurrentHashMap<K, V>(INITIAL_CAPACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
			l2Cache = new ConcurrentHashMap<K, V>(16, LOAD_FACTOR, CONCURRENCY_LEVEL);
			l3Cache = new ConcurrentHashMap<K, V>(16, LOAD_FACTOR, CONCURRENCY_LEVEL);
			statistics.clear();
		}

		for (Map.Entry<K, V> entry : oldL1Cache.entrySet()) {
			notifyAboutCacheRemoval(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<K, V> entry : oldL2Cache.entrySet()) {
			notifyAboutCacheRemoval(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<K, V> entry : oldL3Cache.entrySet()) {
			notifyAboutCacheRemoval(entry.getKey(), entry.getValue());
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Total " + count + " Cache flushed");
		}

		return count;
	}


	@Override
	public int evict() {

		statistics.incrementEvictionCount();
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Evicting cache");
		}

		Map<K, V> oldestCache;
		int count;
		synchronized (synch) {
			oldestCache = l3Cache;
			count = l3Cache.size();
			l3Cache = l2Cache;
			l2Cache = l1Cache;
			l1Cache = new ConcurrentHashMap<K, V>(INITIAL_CAPACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
		}

		for (Map.Entry<K, V> entry : oldestCache.entrySet()) {
			notifyAboutCacheRemoval(entry.getKey(), entry.getValue());
		}

		notifyAboutAutoSessionClosure(oldestCache.entrySet());

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Total " + count + " cache evict");
		}

		return count;

	}

	private void notifyAboutCacheRemoval(K key, V value) {
		for (int index = 0; index < eventListeners.size(); index++) {
			eventListeners.get(index).cacheRemoved(key, value);
		}

	}

	private void notifyAboutAutoSessionClosure(Set entrySet) {
		for (int index = 0; index < eventListeners.size(); index++) {
			eventListeners.get(index).evict(entrySet);
		}
	}

	private void notifyAboutCacheAdd(K key, V value) {
		for (int index = 0; index < eventListeners.size(); index++) {
			eventListeners.get(index).cacheAdded(key, value);
		}
	}

	private class CacheCleanUpTask extends BaseIntervalBasedTask {

		@Override
		public void execute() {
			evict();
		}

		@Override
		public long getInitialDelay() {
			return evictDuration;
		}

		@Override
		public long getInterval() {
			return evictDuration;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}
	}

	public class PartitioningCacheStatistics extends AbstractCacheStatistics {

		public PartitioningCacheStatistics() {
			super();

		}

		@Override
		public long getCacheCount() {
			return l1Cache.size() + l2Cache.size() + l3Cache.size();
		}
	}

	@Override
	public CacheStatistics statistics() {
		return statistics;
	}

	@Override
	public void registerEventListener(CacheEventListener<K, V> eventListener) {
		eventListeners.add(eventListener);
	}

	@Override
	public V getWithoutLoad(K key) {

		statistics.incrementRequestCount();
		V val = l1Cache.get(key);

		if (val != null) {
			statistics.incrementHitCount();
			return val;
		}


		val = l2Cache.remove(key);
		if (val == null) {

			val = l3Cache.remove(key);

			if (val == null) {
				statistics.incrementMissCount();
				return null;
			}
		}


		l1Cache.put(key, val);
		statistics.incrementHitCount();
		return val;
	}
}
