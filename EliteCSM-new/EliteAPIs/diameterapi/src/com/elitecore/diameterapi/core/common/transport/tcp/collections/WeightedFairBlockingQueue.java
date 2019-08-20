package com.elitecore.diameterapi.core.common.transport.tcp.collections;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;


/**
 * Weighted Fair Blocking Queue (WFBQ) Structure 
 * <br />
 * Element Distribution and Selection:
 * <pre>
 *                           Queue Capacity = 15
 *                      +----------------------------+
 *                      |                    Size = 3|
 *                      |                  +--------+|
 *                      |                  |  |->|->||
 *                      |                  +--------+|             |@>
 *    |                 |                    Size = 9|             |@>
 *  =>|  +------------+ |+--------------------------+| +--------+  |@>
 *  ->|  | Prioritize | ||  |  |  |  |  |=>|=>|=>|=>|| | Select |  |=>
 *  @>|  +------------+ |+--------------------------+| +--------+  |=>
 *    |                 |                    Size = 3|             |->
 *                      |                  +--------+|             |@>
 *                      |                  |  |@>|@>||              :	
 *                      |                  +--------+|              :
 *                      +----------------------------+              :
 *  where,           
 *  @> High Priority Element
 *  => Medium Priority Element
 *  -> Low Priority Element
 *  
 * Capacity Distribution:
 * 
 *   1   :   3    :  1
 *       +--------+
 *       |        |
 *       | Normal |
 * +-----+        +------+
 * | Low |        | High |   </pre>
 * Capacities in WFBQ are distributed in 
 * staircase spectrum.
 * 
 * @author monica.lulla
 *
 * @param <E> type of Element Queue Holds.
 */
public class WeightedFairBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

	private static final boolean FAIR = true;
	private static final String MODULE = "WEIGHTED-FAIR-BLOCKING-QUEUE";
	private Optional<BoundedQueue<E>> [] priortyQueues;

	/** Main lock guarding all access */
	private final ReentrantLock lock;

	/** Condition for waiting takes */
	private final Condition empty;

	/** Condition for waiting puts */
	private final Condition full;

	private int queuePointer;
	private int times;
	private int capacity;
	private FairnessPolicy<E> fairnessPolicy;

	public WeightedFairBlockingQueue(FairnessPolicy<E> fairnessPolicy) {
		this(Integer.MAX_VALUE, fairnessPolicy);
	}

	@SuppressWarnings("unchecked")
	public WeightedFairBlockingQueue(int capacity, FairnessPolicy<E> fairness) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity can not be zero");
		}
		this.fairnessPolicy = fairness;
		this.capacity = capacity;
		int[] individualCapacities = distributeCapacity(capacity, fairness.range());
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Total Capacity: " + capacity + 
					" Capacity Distribution: " + Arrays.toString(individualCapacities));
		}
		priortyQueues = new Optional[individualCapacities.length];
		for(int i = 0 ; i < individualCapacities.length ; i++) {
			BoundedQueue<E> boundedQueue = null;
			if(individualCapacities[i] > 0) {
				boundedQueue = new LinkedBoundedQueue<E>(individualCapacities[i]);
			}
			priortyQueues[i] = Optional.of(boundedQueue);
		}
		lock = new ReentrantLock(FAIR);
		empty = lock.newCondition();
		full =  lock.newCondition();
		changeQueuePointers();
	}
	
	//TODO design this for n levels
	private int[] distributeCapacity(int capacity, Range range) {
		
		int[] distributesCapacities = new int[3];
		switch (range) {
		
		case LOW_NORMAL:
			distributesCapacities[0] = capacity/4;
			distributesCapacities[1] = capacity - distributesCapacities[0];
			distributesCapacities[2] = 0;
			break;
			
		case LOW_TO_HIGH:
			distributesCapacities[0] = capacity/5;
			distributesCapacities[1] = capacity - distributesCapacities[0] - distributesCapacities[0];
			distributesCapacities[2] = distributesCapacities[0];
			break;
			
		case NORMAL_HIGH:
			distributesCapacities[0] = 0;
			distributesCapacities[2] = capacity/4;
			distributesCapacities[1] = capacity - distributesCapacities[2];
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return distributesCapacities;
	}

	@Override
	public E poll() {
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (size() == 0) {
				return null;
			}
			boundedQueue = selectQueueForFetch();
			if(boundedQueue == null) {
				return null;
			}
			E e =  boundedQueue.poll();
			if(e != null) {
				full.signal();
			}
			return e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Decide which queue to fetch
	 * @return selected Queue for Operation
	 */
	private BoundedQueue<E> selectQueueForFetch() {

		int count = priortyQueues.length;

		do {
			int queuePointer = this.queuePointer;
			if(priortyQueues[queuePointer].isPresent() 
					&& priortyQueues[queuePointer].get().size() > 0) {
				updateQueuePointers();
				return priortyQueues[queuePointer].get();
			} 
			changeQueuePointers();
		} while (--count > 0);

		return null;
	}

	private void changeQueuePointers() {
		times = 0;
		queuePointer--;
		if(queuePointer < 0) {
			queuePointer = priortyQueues.length -1;
		}

	}

	private void updateQueuePointers() {
		times++;
		if(times > queuePointer) {
			changeQueuePointers();
		}
	}

	@Override
	public E peek() {
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			boundedQueue = selectQueueForFetch();
			if(boundedQueue == null) {
				return null;
			}
			return boundedQueue.peek();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean offer(E e) {
		if (e == null) throw new NullPointerException();
		Queue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			boundedQueue = selectQueueForInsert(e);
			if(boundedQueue == null) {
				return false;
			}
			if(boundedQueue.offer(e)) {
				empty.signal();
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	private BoundedQueue<E> selectQueueForInsert(E e) {

		int index = fairnessPolicy.prioritize(e) - 1;

		do {
			if(priortyQueues[index].isPresent() == false) {
				throw new IllegalPriorityException("Queue is not allocated for Priority: " + (index + 1) +
						", Kindly verify Capacity or Range provided.");
			}
			if(priortyQueues[index].get().remainingCapacity() > 0) {
				return priortyQueues[index].get();
			}
			index--;
		} while (index >= 0);
		return null;
	}

	@Override
	public void put(E e) throws InterruptedException {
		if (e == null) throw new NullPointerException();
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while ((boundedQueue = selectQueueForInsert(e)) == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Waiting for Queue to get empty");
				}
				full.await();
			}
			if(boundedQueue.offer(e)) {
				empty.signal();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (e == null) throw new NullPointerException();
		long nanos = unit.toNanos(timeout);
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while ((boundedQueue = selectQueueForInsert(e)) == null) {
				if (nanos <= 0) {
					return false;
				}
				nanos = full.awaitNanos(nanos);
			}
			if( boundedQueue.offer(e)) {
				empty.signal();
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public E take() throws InterruptedException {
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (size() == 0) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Waiting for Elements");
				}
				empty.await();
			}
			boundedQueue = selectQueueForFetch();
			E e = boundedQueue.poll();
			full.signal();
			return e;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		BoundedQueue<E> boundedQueue = null;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while ((boundedQueue = selectQueueForFetch()) == null) {
				if (nanos <= 0) {
					return null;
				}
				nanos = empty.awaitNanos(nanos);
			}
			E e = boundedQueue.poll();
			full.signal();
			return e;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int remainingCapacity() {
		return capacity - size();
	}

	@Override
	public int drainTo(Collection<? super E> c) {

		if (c == null) throw new NullPointerException();
		if (c == this) throw new IllegalArgumentException();

		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int n = 0;
			int queuePointer = priortyQueues.length;
			while(--queuePointer >= 0) {
				int times = 0;
				while (times <= queuePointer) {
					if (priortyQueues[queuePointer].isPresent() == false) {
						break;
					}
					E e = priortyQueues[queuePointer].get().poll();
					if(e == null) {
						break;
					}
					c.add(e);
					times++;
					n++;
				}
			}
			if(n > 0) {
				full.signalAll();
			}
			return n;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		if (c == null) throw new NullPointerException();
		if (c == this) throw new IllegalArgumentException();

		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int n = 0;
			int queuePointer = priortyQueues.length;
			while(--queuePointer >= 0) {
				int times = 0;
				while (times <= queuePointer && n < maxElements) {
					
					if (priortyQueues[queuePointer].isPresent() == false) {
						break;
					}					
					E e = priortyQueues[queuePointer].get().poll();
					if(e == null) {
						break;
					}
					c.add(e);
					times++;
					n++;
				}
			}
			if(n > 0) {
				full.signalAll();
			}
			return n;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Iterator<E> iterator() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			
			@SuppressWarnings("unchecked")
			Optional<Iterator<E>> [] iterators = new Optional[priortyQueues.length];
			for(int i = 0 ; i < priortyQueues.length ; i++){
				Iterator<E> iterator = null;
				if(priortyQueues[i].isPresent()){
					iterator = priortyQueues[i].get().iterator();
				}
				iterators[i] = Optional.of(iterator);
			}
			return new Itr(iterators);
		} finally {
			lock.unlock();
		}
	}

	private class Itr implements Iterator<E> {

		private Optional<Iterator<E>> [] iterators;
		private int iteratorPonitor;
		private int times;

		public Itr(Optional<Iterator<E>> [] iterators) {
			this.iterators = iterators;
			iteratorPonitor = iterators.length -1;
			times = 0;
		}

		public boolean hasNext() {
			
			final ReentrantLock lock = WeightedFairBlockingQueue.this.lock;
			lock.lock();
			try {

				int count = priortyQueues.length;
				
				do {
					if (iterators[iteratorPonitor].isPresent() &&
							iterators[iteratorPonitor].get().hasNext()) {
						return true;
					}
					changeQueuePointers();
				} while (--count > 0);
				return false;
			} finally {
				lock.unlock();
			}
		}
		
		private void changeQueuePointers() {
			times = 0;
			iteratorPonitor--;
			if(iteratorPonitor < 0) {
				iteratorPonitor = iterators.length -1;
			}

		}

		private void updateQueuePointers() {
			times++;
			if(times > iteratorPonitor) {
				changeQueuePointers();
			}
		}

		public E next() {
			final ReentrantLock lock = WeightedFairBlockingQueue.this.lock;
			lock.lock();
			try {
				E e = iterators[iteratorPonitor].get().next();
				updateQueuePointers();
				return e;
			} finally {
				lock.unlock();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int size = 0;
			for(int i = 0 ; i < priortyQueues.length ; i++) {
				size += priortyQueues[i].isPresent() ? priortyQueues[i].get().size() : 0;
			}
			return size;
		} finally {
			lock.unlock();
		}
	}


	/**
	 * 
	 * Capacity of {@link WeightedFairBlockingQueue} will be
	 * distributed in Relation with Range.
	 * This is to optimize Capacity Distribution. 
	 * 
	 * @author monica.lulla
	 *
	 */
	public enum Range {
		/**
		 * Capacity will be allocated for Low to High Spectrum 
		 */
		LOW_TO_HIGH,
		
		/**
		 * Capacity will be allocated for Low to Normal Spectrum 
		 */
		LOW_NORMAL,
		
		/**
		 * Capacity will be allocated for Normal to High Spectrum
		 */
		NORMAL_HIGH;
	}

}


