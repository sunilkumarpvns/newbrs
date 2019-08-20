package com.elitecore.commons.counters;

public class ThreadSafeCounter<T> implements CircularCounter<T> {
	
	private CircularCounter<T> conter;

	public ThreadSafeCounter(CircularCounter<T> integerCounter) {
		this.conter = integerCounter;
	}

	@Override
	public synchronized T next() {
		return conter.next();
	}
	
}
