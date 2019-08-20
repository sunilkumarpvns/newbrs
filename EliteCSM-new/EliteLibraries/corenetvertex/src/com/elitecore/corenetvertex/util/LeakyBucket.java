package com.elitecore.corenetvertex.util;


import java.util.Iterator;
import org.apache.commons.collections4.queue.CircularFifoQueue;


/**
 * @author Ishani Bhatt
 * {@http://en.wikipedia.org/wiki/Leaky_bucket} 
 * 
 */

public class LeakyBucket<T> implements Iterable<T> {
	private CircularFifoQueue<T> circularFifoQueue = null;


	public LeakyBucket(int capacity){
		circularFifoQueue = new CircularFifoQueue<>(capacity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return circularFifoQueue.iterator();
	}

	public int size() {
		return circularFifoQueue.size();
	}

	public boolean isEmpty() {
		return circularFifoQueue.isEmpty();
	}

	public boolean contains(T o) {
		return circularFifoQueue.contains(o);
	}

	public Object[] toArray() {
		return circularFifoQueue.toArray();
	}

	@SuppressWarnings("unchecked")
	public T[] toArray(T[] a) {
		return circularFifoQueue.toArray(a);
	}

	public boolean add(T e) {
		return circularFifoQueue.add(e);
		
	}
}
