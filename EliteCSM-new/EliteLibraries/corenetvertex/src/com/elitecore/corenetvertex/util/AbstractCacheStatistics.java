package com.elitecore.corenetvertex.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by harsh on 5/21/16.
 */
public abstract  class AbstractCacheStatistics implements CacheStatistics {
	private AtomicLong hitCount;
    private AtomicLong missCount;
    private AtomicLong requestCount;
    private AtomicLong loadCount;
    private AtomicLong evictionCount;
    private long averageLoadPanelty;

	public AbstractCacheStatistics() {
		hitCount = new AtomicLong();
		missCount = new AtomicLong();
		loadCount = new AtomicLong();
		evictionCount = new AtomicLong();
		requestCount = new AtomicLong();

	}

	@Override
    public long getHitCount() {
        return hitCount.get();
    }

	@Override
    public long getEvictionCount() {
        return evictionCount.get();
    }

	@Override
    public long getMissCount() {
        return missCount.get();
    }

	@Override
    public long getRequestCount() {
        return requestCount.get();
    }

	@Override
    public long getAverageLoadPanelty() {
        return averageLoadPanelty;
    }

	@Override
    public long getLoadCount() {
        return loadCount.get();
    }

    public void incrementHitCount() {
        hitCount.incrementAndGet();
    }

    public void incrementEvictionCount() {
        evictionCount.incrementAndGet();
    }

    public void incrementMissCount() {
        missCount.incrementAndGet();
    }

	public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }


	public synchronized void incrementAverageLoadPanelty(long totalLoadingTime) {
        averageLoadPanelty = (averageLoadPanelty + totalLoadingTime) / loadCount.get();
    }

	@Override
    public void incrementLoadCount() {
        loadCount.incrementAndGet();
    }

	@Override
    public void clear() {
        hitCount.set(0);
        missCount.set(0);
        requestCount.set(0);
        loadCount.set(0);
        evictionCount.set(0);

        synchronized (this) {
            averageLoadPanelty = 0 ;
        }

    }
}
