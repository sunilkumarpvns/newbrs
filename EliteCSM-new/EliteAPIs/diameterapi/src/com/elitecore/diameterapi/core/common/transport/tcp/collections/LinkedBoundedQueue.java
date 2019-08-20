package com.elitecore.diameterapi.core.common.transport.tcp.collections;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class LinkedBoundedQueue<E> extends AbstractQueue<E> 
implements BoundedQueue<E> {

	/**
	 * Linked list node class
	 */
	static class Node<E> {
		E item;
		/**
		 * One of:
		 * - the real successor Node
		 * - this Node, meaning the successor is head.next
		 * - null, meaning there is no successor (this is the last node)
		 */

		Node<E> next;
		Node(E x) { item = x; }
	}

	/** The capacity bound, or Integer.MAX_VALUE if none */
	private final int capacity;

	/** Current number of elements */
	private int count = 0;

	/** Head of linked list */
	private transient Node<E> head;

	/** Tail of linked list */
	private transient Node<E> last;

	/**
	 * Creates a node and links it at end of queue.
	 * @param x the item
	 */
	private void enqueue(E x) {
		// assert putLock.isHeldByCurrentThread();
		last = last.next = new Node<E>(x);
	}

	/**
	 * Removes a node from head of queue.
	 * @return the node
	 */
	private E dequeue() {
		// assert takeLock.isHeldByCurrentThread();
		Node<E> h = head;
		Node<E> first = h.next;
		h.next = h; // help GC
		head = first;
		E x = first.item;
		first.item = null;
		return x;
	}


	/**
	 * Creates a <tt>LinkedQueue</tt> with a capacity of
	 * {@link Integer#MAX_VALUE}.
	 */
	public LinkedBoundedQueue() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * Creates a <tt>LinkedQueue</tt> with the given (fixed) capacity.
	 *
	 * @param capacity the capacity of this queue
	 * @throws IllegalArgumentException if <tt>capacity</tt> is not greater
	 *         than zero
	 */
	public LinkedBoundedQueue(int capacity) {
		if (capacity <= 0) throw new IllegalArgumentException();
		this.capacity = capacity;
		last = head = new Node<E>(null);
	}

	/**
	 * Creates a <tt>LinkedQueue</tt> with a capacity of
	 * {@link Integer#MAX_VALUE}, initially containing the elements of the
	 * given collection,
	 * added in traversal order of the collection's iterator.
	 *
	 * @param c the collection of elements to initially contain
	 * @throws NullPointerException if the specified collection or any
	 *         of its elements are null
	 */
	public LinkedBoundedQueue(Collection<? extends E> c) {
		this(Integer.MAX_VALUE);
		int n = 0;
		for (E e : c) {
			if (e == null)
				throw new NullPointerException();
			if (n == capacity)
				throw new IllegalStateException("Queue full");
			enqueue(e);
			++n;
		}
		count = n;
	}


	// this doc comment is overridden to remove the reference to collections
	// greater in size than Integer.MAX_VALUE
	/**
	 * Returns the number of elements in this queue.
	 *
	 * @return the number of elements in this queue
	 */
	public int size() {
		return count;
	}

	// this doc comment is a modified copy of the inherited doc comment,
	// without the reference to unlimited queues.
	/**
	 * Returns the number of additional elements that this queue can ideally
	 * (in the absence of memory or resource constraints) accept. 
	 * This is always equal to the initial capacity of this queue
	 * less the current <tt>size</tt> of this queue.
	 *
	 * <p>Note that you <em>cannot</em> always tell if an attempt to insert
	 * an element will succeed by inspecting <tt>remainingCapacity</tt>
	 * because it may be the case that another thread is about to
	 * insert or remove an element.
	 */
	public int remainingCapacity() {
		return capacity - count;
	}

	/**
	 * Inserts the specified element at the tail of this queue if it is
	 * possible to do so immediately without exceeding the queue's capacity,
	 * returning <tt>true</tt> upon success and <tt>false</tt> if this queue
	 * is full.
	 *
	 * @throws NullPointerException if the specified element is null
	 */
	public boolean offer(E e) {
		if (e == null) throw new NullPointerException();
		if (count == capacity)
			return false;
		int c = -1;
		if (count < capacity) {
			enqueue(e);
			c = count;
			count++;
		}
		return c >= 0;
	}

	public E poll() {
		if (count == 0)
			return null;
		E x = null;
		if (count > 0) {
			x = dequeue();
			count--;
		}
		return x;
	}


	public E peek() {
		if (count == 0)
			return null;
		Node<E> first = head.next;
		if (first == null)
			return null;
		else
			return first.item;
	}

	/*
	 * Unlinks interior Node p with predecessor trail.
	 */
	private void unlink(Node<E> p, Node<E> trail) {
		// assert isFullyLocked();
		// p.next is not changed, to allow iterators that are
		// traversing p to maintain their weak-consistency guarantee.
		p.item = null;
		trail.next = p.next;
		if (last == p)
			last = trail;
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
	public Object[] toArray() {
		int size = count;
		Object[] a = new Object[size];
		int k = 0;
		for (Node<E> p = head.next; p != null; p = p.next)
			a[k++] = p.item;
		return a;
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
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = count;
		if (a.length < size)
			a = (T[])java.lang.reflect.Array.newInstance
			(a.getClass().getComponentType(), size);

		int k = 0;
		for (Node<E> p = head.next; p != null; p = p.next)
			a[k++] = (T)p.item;
		if (a.length > k)
			a[k] = null;
		return a;
	}

	/**
	 * Returns an iterator over the elements in this queue in proper sequence.
	 * The returned <tt>Iterator</tt> is a "weakly consistent" iterator that
	 * will never throw {@link ConcurrentModificationException},
	 * and guarantees to traverse elements as they existed upon
	 * construction of the iterator, and may (but is not guaranteed to)
	 * reflect any modifications subsequent to construction.
	 *
	 * @return an iterator over the elements in this queue in proper sequence
	 */
	public Iterator<E> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<E> {
		/*
		 * Basic weakly-consistent iterator.  At all times hold the next
		 * item to hand out so that if hasNext() reports true, we will
		 * still have it to return even if lost race with a take etc.
		 */
		private Node<E> current;
		private Node<E> lastRet;
		private E currentElement;

		Itr() {
			current = head.next;
			if (current != null)
				currentElement = current.item;
		}

		public boolean hasNext() {
			return current != null;
		}

		/**
		 * Returns the next live successor of p, or null if no such.
		 *  
		 * Unlike other traversal methods, iterators need to handle both:
		 * - dequeued nodes (p.next == p)
		 * -  (possibly multiple) interior removed nodes (p.item == null)
		 */
		private Node<E> nextNode(Node<E> p) {
			for (; ;) {   
				Node<E> s = p.next;
				if (s == p)
					return head.next;
				if (s == null || s.item != null)
					return s;
				p = s;
			}
		}

		public E next() {
			if (current == null)
				throw new NoSuchElementException();
			E x = currentElement;
			lastRet = current;
			current = nextNode(current);
			currentElement = (current == null) ? null : current.item;
			return x;
		}

		public void remove() {
			if (lastRet == null)
				throw new IllegalStateException();
			Node<E> node = lastRet;
			lastRet = null;
			for (Node<E> trail = head, p = trail.next;
					p != null;
					trail = p, p = p.next) {
				if (p == node) {
					unlink(p, trail);
					break;
				}
			}
		}
	}
}
