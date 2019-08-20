package com.elitecore.commons.base;

/**
 * A time source, that provides current time in milliseconds. This class
 * represents wall time.
 * 
 * <p>
 * This is a useful abstraction for testing, as it creates a level of indirection
 * when some class needs access to current time. This indirection can then be utilized
 * by supplying {@link FixedTimeSource} makes the class unit testable. 
 * Use {@link #systemTimeSource()} for production. 
 * 
 * <p>
 * This class should not be used to measure time elapsed. Use {@link Ticker}.
 * 
 * @author narendra.pathai
 *
 */
public abstract class TimeSource {
	
	private static final TimeSource SYSTEM_TIME_SOURCE = new TimeSource() {
		@Override
		public long currentTimeInMillis() {
			return System.currentTimeMillis();
		}
	};
	
	/**
	 * Returns the current wall time in milliseconds
	 */
	public abstract long currentTimeInMillis();
	
	/**
	 * Returns the system time source that uses {@link System#currentTimeMillis()}.
	 */
	public static TimeSource systemTimeSource() {
		return SYSTEM_TIME_SOURCE;
	}
}
