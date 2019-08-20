package com.elitecore.commons.counters;

public class LongCounter implements CircularCounter<Long> {
	
	private long current;
	private long mod;
	
	public LongCounter() {
		this(Long.MAX_VALUE);
	}
	
	public LongCounter(long mod) {
		this(-1, mod);
	}

	public LongCounter(long initial, long mod) {
		this.current = initial;
		this.mod = mod;
	}

	@Override
	public Long next() {
		current = ((current + 1) % mod);
		return current;
	}

}
