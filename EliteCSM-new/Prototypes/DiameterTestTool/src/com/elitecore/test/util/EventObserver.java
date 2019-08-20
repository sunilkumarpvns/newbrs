package com.elitecore.test.util;

public interface EventObserver<T,V> {
	public void onEvent(T event, V data);
}
