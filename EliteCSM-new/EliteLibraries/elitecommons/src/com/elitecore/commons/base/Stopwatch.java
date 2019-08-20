package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkNotNull;
import static com.elitecore.commons.base.Preconditions.checkState;

import java.util.concurrent.TimeUnit;

/**
 * A class that measures the elapsed time in nanoseconds.
 * 
 * <p>
 * This class is useful for scenarios where run time of some calculation or code is 
 * to be calculated.
 * 
 * <p>Usage:
 * <pre>
 *   Stopwatch stopwatch = new Stopwatch().{@link #start start}();
 *   doSomething();
 *   stopwatch.{@link #stop stop}(); // optional
 *
 *   long elapsedInMillis = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
 *
 *   LogManager.getLogger().info(String.valueOf(stopwatch)); // formatted string like "12.3 ms"</pre>
 *   
 * <p>
 * This class is not thread safe
 * 
 * @author narendra.pathai
 *
 */
public final class Stopwatch {
	private final Ticker ticker;
	private long startTime;
	private boolean running;
	private long elapsedTime;
	
	/**
	 * Creates a stopwatch that uses {@link System#nanoTime()} to measure
	 * elapsed time
	 */
	public Stopwatch() {
		this(new Ticker() {
			
			@Override
			public long nanoTime() {
				return System.nanoTime();
			}
		});
	}

	/**
	 * Creates a stopwatch that uses provided timeSource to measure elapsed time
	 * 
	 * @throws NullPointerException if ticker is null
	 */
	public Stopwatch(Ticker ticker) {
		this.ticker = checkNotNull(ticker, "ticker is null");
	}

	/**
	 * Starts the stopwatch.
	 * 
	 * @return this {@code Stopwatch} instance
	 * @throws IllegalStateException if stopwatch is already running
	 */
	public Stopwatch start() {
		checkState(running == false, "This stopwatch is already running.");
		running = true;
		startTime = ticker.nanoTime();
		return this;
	}

	/**
	 * Stops the stopwatch.
	 * 
	 * @return this {@code Stopwatch} instance
	 * @throws IllegalStateException if stopwatch is already stopped
	 */
	public Stopwatch stop() {
		long endTime = ticker.nanoTime();
		checkState(running, "This stopwatch is already stopped.");
		running = false;
		elapsedTime += endTime - startTime;
		return this;
	}

	/**
	 * Returns the elapsed time in nanoseconds. If the stopwatch is running then
	 * returns the difference between current time and start time.
	 * 
	 */
	public long elapsedInNanos() {
		return running  ? ticker.nanoTime() - startTime + elapsedTime : elapsedTime;
	}

	/**
	 * Returns the elapsed time in the desired unit.
	 * 
	 */
	public long elapsedTime(TimeUnit timeUnit) {
		return timeUnit.convert(elapsedTime, TimeUnit.NANOSECONDS);
	}
	
	@Override
	public String toString() {
		return format("%.4g %s");
	}

	/**
	 * Returns the elapsed time in the provided format.
	 * <p>
	 * <pre>
	 *   LogManager.getLogger().info(stopwatch.format("Elapsed time is %.3g %s"));
	 * </pre>
	 */
	public String format(String format) {
		long elapsed = elapsedInNanos();
		TimeUnit timeUnit = selectUnit(elapsed);
		double convertedTime = (double) elapsed / TimeUnit.NANOSECONDS.convert(1, timeUnit);
		return String.format(format, convertedTime, stringify(timeUnit));
	}
	
	private Object stringify(TimeUnit timeUnit) {
		switch (timeUnit) {
		case NANOSECONDS:
			return "ns";
		case MICROSECONDS:
			return "\u03bcs";
		case MILLISECONDS:
			return "ms";
		case SECONDS:
			return "s";
		default:
			throw new AssertionError();
		}
	}

	/*
	 * Selects the best suitable unit for the elapsed time going from higher unit
	 * to lower unit
	 */
	private TimeUnit selectUnit(long elapsed) {
		if (TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS) > 0) {
			return TimeUnit.SECONDS;
		} 
		if (TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS) > 0) {
			return TimeUnit.MILLISECONDS;
		}
		if (TimeUnit.MICROSECONDS.convert(elapsed, TimeUnit.NANOSECONDS) > 0) {
			return TimeUnit.MICROSECONDS;
		}
		return TimeUnit.NANOSECONDS;
	}
}
