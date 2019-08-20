package com.elitecore.commons.base;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class provides basic implementation for {@link Iterator} interface.
 * <p>
 * As java's iterator requires query for availability of data which does
 * not change the state of the iterator, it becomes tricky to support query
 * and get operations for certain situations. This iterator is further useful
 * for data sources which do not support query, they just support read such as
 * a Reader.
 * <p>
 * This is made easier by this class as user just needs to implement {@link #computeNext()}
 * method and invoke {@link #endOfData()} when appropriate to signify the end of elements.
 * 
 * @author narendra.pathai
 *
 */
public abstract class AbstractIterator<T> implements Iterator<T> {
	
	private State state = State.NOT_READY;
	private T next;
	
	enum State {
		/** 
		 * Haven't computed or have already returned element.
		 */ 
		NOT_READY,
		/**
		 * Have computed next element and haven't returned.
		 */
		READY,
		/**
		 * Have reached end of data and no more elements are left.
		 */
		DONE;
	}
	
	@Override
	public boolean hasNext() {
		switch (state) {
		case READY:
			return true;
		case DONE:
			return false;
		default:
		}
		return tryComputeNext();
	}

	private boolean tryComputeNext() {
		next = computeNext();
		if (state != State.DONE) {
			state = State.READY;
			return true;
		}
		return false;
	}

	/**
	 * Returns the next element.
	 * <p>
	 * Note: The implementation must call {@link #endOfData()} when there are no elements
	 * left for iteration. Failure to call it may cause infinite loop.
	 * 
	 * <p>
	 * This method is called on initial invocation of {@link #hasNext()} or {@link #next()}
	 * and on first invocation of {@link #hasNext()} or {@link #next()} after each successful
	 * invocation of {@link #next()}. Once the implementation calls {@link #endOfData()}, it
	 * is guaranteed not to be called after that.
	 * 
	 * @return the next element if there is. If {@code endOfData()} is called then return 
	 * value is ignored.
	 */
	protected abstract T computeNext();
	
	@Override
	public T next() {
		if (hasNext() == false) {
			throw new NoSuchElementException();
		}
		
		state = State.NOT_READY;
		T result = next;
		next = null;
		return result;
	}
	
	/**
	 * Signifies the end of iteration. The implementation <b>must<b> invoke this method
	 * where no more elements are left.
	 * 
	 * @return null only for convenience so that implementation can use it as
	 * {@code return endOfData();}
	 */
	protected final T endOfData() {
		state = State.DONE;
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
