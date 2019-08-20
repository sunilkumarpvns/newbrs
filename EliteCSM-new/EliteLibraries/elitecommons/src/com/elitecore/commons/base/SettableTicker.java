package com.elitecore.commons.base;

/**
 * A time source that returns the fixed value of time. Useful for testing.
 *  
 * @author narendra.pathai
 *
 */
public class SettableTicker implements Ticker {
	
	private enum TickerState {
		INITIAL,
		NEXT
	}
	
	private long initialTime;
	private long nextTime;
	private TickerState state;
	
	/**
	 * Creates a ticker instance with the provided initial time
	 */
	public SettableTicker(long initialTime) {
		this.initialTime = initialTime;
		this.nextTime = initialTime;
		this.state = TickerState.INITIAL;
	}

	/**
	 * Sets the provided time as next time. The value of this time is returned once value of 
	 * initial time is consumed using {@code SettableTicker#nanoTime()}.
	 * 
	 */
	public void setNanoTime(long nanoTime) {
		this.nextTime = nanoTime;
	}

	@Override
	public long nanoTime() {
		switch (state) {
		case INITIAL:
			state = TickerState.NEXT;
			return initialTime;
		case NEXT:
			return nextTime;
		default: 
			throw new IllegalStateException();
		}
	}
}
