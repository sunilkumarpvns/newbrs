package com.elitecore.commons.base;

/**
 * A time source; return time value representing the number of nanoseconds elapsed since
 * some fixed but arbitrary point in time.
 * 
 * <p>
 * <b>NOTE:</b> This interface can only be used to measure elapsed time and not wall time.
 * For wall time use {@link TimeSource}.
 * 
 * @author narendra.pathai
 *
 */
public interface Ticker {
	/**
	 * Returns the number of nanoseconds elapsed since fixed point of reference.
	 * 
	 */
	public long nanoTime();
}
