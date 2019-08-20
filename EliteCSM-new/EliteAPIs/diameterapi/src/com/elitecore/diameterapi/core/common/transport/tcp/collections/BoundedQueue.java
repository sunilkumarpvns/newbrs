package com.elitecore.diameterapi.core.common.transport.tcp.collections;

import java.util.Queue;

public interface BoundedQueue<E> extends Queue<E> {

	public int remainingCapacity();
}
