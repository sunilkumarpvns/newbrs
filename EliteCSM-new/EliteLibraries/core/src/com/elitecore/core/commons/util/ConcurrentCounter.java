package com.elitecore.core.commons.util;

import java.io.Serializable;

public class ConcurrentCounter implements Cloneable, Serializable{

	private static final long serialVersionUID = 1L;
	transient private Object counterLock=new Object(); //To make the Lock private
	private long counter;
	private long maxVal;
	private long minVal;

	public ConcurrentCounter() {
		this(0,Long.MAX_VALUE);
	}
	
	public ConcurrentCounter(long minVal,long maxVal) {
		this.maxVal = maxVal;
		this.minVal = minVal;
		this.counter  = minVal;
	}
	
	
	public void incrementMaxVal(){
		synchronized (counterLock) {
			maxVal++;
		}
	}
	public long incrementCounter(){
		long currentVal;
		synchronized (counterLock) {
			if(counter > maxVal)
				counter = minVal;
			currentVal = counter++;
		}
		return currentVal;
		
	}
	
	public void decrementMaxVal(){
		synchronized (counterLock) {
			maxVal--;
		}
	}

	public long decrementCounter(){
		long currentVal;
		synchronized (counterLock) {
			if(counter < minVal)
				counter = maxVal;
			currentVal = counter--;
		}
		return currentVal;
		
	}

	public void setCounter(long counter){
		synchronized (counterLock) {
			this.counter=counter;
		}
	}

	public long getMinValue(){
		return this.minVal;
	}

	public long getMaxValue(){
		return this.maxVal;
	}

	public long getCounter(){
		return this.counter;
	}
}
