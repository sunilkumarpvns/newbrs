package com.elitecore.corenetvertex.util;

import java.io.Serializable;

/**
 * CircularCounter increments counter value upto defined max-value, <br />
 * then it continues its counter from min-value.
 * 
 * this is thread-safe counter.
 * Default constructor takes min-value=0 and max-vale=LONG.MAX_VALUE.
 * 
 */
public class ConcurrentCounter implements Serializable {

	private static final long serialVersionUID = 1L;
    private transient Object counterLock = new Object(); //To make the Lock private
	private long counter;
	private long maxVal;
	private long minVal;

	/**
	 * Creates counter with minVal=0 and maxVal=Long.MAX_VALUE
	 */
	public ConcurrentCounter() {
		this(0,Long.MAX_VALUE);
	}
	
	/**
	 * creates counter with defined min-value and max-value, counter will be assigned to min-value.
	 * 
	 * @param minVal minimum value for counter, used to start counter initially
	 * @param maxVal maximum limit value for counter
	 */
	public ConcurrentCounter(long minVal,long maxVal) {
		this.maxVal = maxVal;
		this.minVal = minVal;
		this.counter  = minVal;
	}
	
	/**
	 * increments Max value by 1
	 */
	public void incrementMaxVal() {
		synchronized (counterLock) {
			maxVal++;
		}
	}

	/**
	 * increments counter value by 1.
	 * 
	 * //need to add comment after changing behaviour
	 * 
	 */
	public long incrementCounter() {
		long currentVal;
		synchronized (counterLock) {
            if (counter >= maxVal) {
				counter = minVal;
                currentVal = counter;
            } else {
                currentVal = counter++;
            }
		}
		return currentVal;
		
	}
	
	/**
	 * decrements max-value by 1
	 */
	public void decrementMaxVal() {
		synchronized (counterLock) {
			maxVal--;
		}
	}

	/**
	 * decrements counter value by 1.
	 * 
	 * //need to add comment after changing behaviour
	 * 
	 */
	public long decrementCounter() {
		long currentVal;
		synchronized (counterLock) {
            if (counter <= minVal) {
				counter = maxVal;
                currentVal = counter;
            } else {
                currentVal = counter--;
            }
		}
		return currentVal;
		
	}

	/**
	 * set value for counter
	 * 
	 * @param counter, value will override current counter value
	 */
	public void setCounter(long counter) {
		synchronized (counterLock) {
			this.counter=counter;
		}
	}

	public long getMinValue() {
		return this.minVal;
	}

	public long getMaxValue() {
		return this.maxVal;
	}

	public long getCounter() {
		return this.counter;
	}
}
