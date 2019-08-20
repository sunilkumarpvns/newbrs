package com.elitecore.core.util.queue;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;

import com.elitecore.core.commons.util.ConcurrentCounter;


public class ConcurrentLinkedQueue<E> {
	private java.util.concurrent.ConcurrentLinkedQueue<E> queue;
	private long capacity;
	private ConcurrentCounter queueSize;

    /**
     * Creates a <tt>LinkedBlockingQueue</tt> with a capacity of
     * {@link Long#MAX_VALUE}.
     */
	public ConcurrentLinkedQueue() {
		this(Long.MAX_VALUE);
	}
	
    /**
     * Creates a <tt>ConcurrentLinkedQueue</tt> with the given (fixed) capacity.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if <tt>capacity</tt> is not greater
     *         than zero
     */
	public ConcurrentLinkedQueue(long capacity){
		if (capacity <= 0) throw new IllegalArgumentException();
		this.capacity = capacity;
		queue = new java.util.concurrent.ConcurrentLinkedQueue<E>();
		queueSize = new ConcurrentCounter(Long.MIN_VALUE,Long.MAX_VALUE);
		queueSize.setCounter(0);
	}
	
    /**
     * Inserts the specified element at the tail of this queue.
     *
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
	public boolean add(E e){
		if(queueSize.getCounter() < capacity){
			boolean bResult = queue.add(e);
			if(bResult)
				queueSize.incrementCounter();
			return bResult;
		}else
			return false;
	}
	
    /**
     * Returns <tt>true</tt> if this queue contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this queue contains
     * at least one element <tt>e</tt> such that <tt>o.equals(e)</tt>.
     *
     * @param o object to be checked for containment in this queue
     * @return <tt>true</tt> if this queue contains the specified element
     */
	public boolean contains(Object o){
		return queue.contains(o);
	}
	
	/**
     * Returns <tt>true</tt> if this queue contains no elements.
     *
     * @return <tt>true</tt> if this queue contains no elements
     */
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
    /**
     * Inserts the specified element at the tail of this queue.
     *
     * @return <tt>true</tt> (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
	public boolean offer(E o){
		if(queueSize.getCounter() < capacity){
			boolean bResult = queue.offer(o);
			if(bResult)
				queueSize.incrementCounter();
			return bResult;
		}else
			return false;
	}
	
    /**
     * Retrieves and removes the head of this queue, or null  if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty.
     */
	public E poll(){
		E e = queue.poll();
		if(e != null)
			queueSize.decrementCounter();
		return e;
	}
	
    /**
     * Retrieves, but does not remove, the head of this queue, returning null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty.
     */
	public E peek(){
		return queue.peek();
	}
	
	/**
     * Returns the number of elements in this queue.  If this queue
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * <p>Beware that, unlike in most collections, this method is
     * <em>NOT</em> a constant-time operation. Because of the
     * asynchronous nature of these queues, determining the current
     * number of elements requires an O(n) traversal.
     *
     * @return the number of elements in this queue
     */
	public int size(){
		return queue.size();
	}
	
	/**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element <tt>e</tt> such
     * that <tt>o.equals(e)</tt>, if this queue contains one or more such
     * elements.
     * Returns <tt>true</tt> if this queue contained the specified element
     * (or equivalently, if this queue changed as a result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return <tt>true</tt> if this queue changed as a result of the call
     */
	public boolean remove(Object o){
		boolean bResult = queue.remove(o);
		if(bResult)
			queueSize.decrementCounter();
		return bResult;
			
	}
	
	 /**
     * Removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
	public void clear() {
		queue.clear();
		queueSize.setCounter(0);
	}
	
	
	/**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
	public Object[] toArray(){
		return queue.toArray();
	}
	
	/**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence; the runtime type of the returned array is that of
     * the specified array.  If the queue fits in the specified array, it
     * is returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this queue.
     *
     * <p>If this queue fits in the specified array with room to spare
     * (i.e., the array has more elements than this queue), the element in
     * the array immediately following the end of the queue is set to
     * <tt>null</tt>.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose <tt>x</tt> is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of <tt>String</tt>:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this queue
     * @throws NullPointerException if the specified array is null
     */
	public <T> T[] toArray(T[] a){
		return queue.toArray(a);
	}
	
	/**
     * Returns an iterator over the elements in this queue in proper sequence.
     * The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence
     */
	public Iterator<E> iterator(){
		return queue.iterator();
	}
}
