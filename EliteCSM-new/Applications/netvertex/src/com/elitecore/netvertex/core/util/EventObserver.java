package com.elitecore.netvertex.core.util;

public interface EventObserver<T,V> {
	public void onEvent(T event,V data);
}
