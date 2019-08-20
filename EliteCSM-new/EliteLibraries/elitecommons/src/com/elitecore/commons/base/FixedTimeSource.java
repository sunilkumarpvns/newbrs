package com.elitecore.commons.base;

/**
 * Provides fixed set time. Useful for testing as it removes direct dependency
 * of system environment by providing a nifty abstraction to achieve the same effect.
 * 
 * <p>
 * Usage:
 * <pre>
 * <code>
 * class Example {
 *  // Inject time source abstraction via constructor for testing
 *  Example(TimeSource timeSource) {
 *   this.timeSource = timeSource;	
 *  }
 *  
 *  private void someMethod() {
 *   long currentTime = timeSource.currentTimeInMillis(); // use time source abstraction
 *  }
 * }
 * </code>
 * Usage in unit test: setup an instance of Example which uses {@link FixedTimeSource}
 * <code>
 * class ExampleTest {
 * 	{@literal @}Before
 *  public void setUp() {
 *   this.timeSource = new FixedTimeSource(); // time in our control
 *   this.example = new Example(this.timeSource);	
 *  }
 * }
 * </code>
 * </pre>
 * 
 * @author narendra.pathai
 *
 */
public final class FixedTimeSource extends TimeSource {

	private long currentTimeInMillis;

	/**
	 * Creates fixed time source with 0 as initial time
	 */
	public FixedTimeSource() {
		this(0);
	}

	/**
	 * Creates fixed time source with provided time
	 */
	public FixedTimeSource(long initialTimeInMillis) {
		setCurrentTimeInMillis(initialTimeInMillis);
	}

	@Override
	public long currentTimeInMillis() {
		return currentTimeInMillis;
	}

	/**
	 * Sets the current time to be fixed value {@code currentTimeInMillis}
	 */
	public void setCurrentTimeInMillis(long currentTimeInMillis) {
		this.currentTimeInMillis = currentTimeInMillis;
	}

	/**
	 * Advances the current time by the provided number of milliseconds.
	 * 
	 * @param advanceDurationInMillis advance duration in milliseconds
	 */
	public void advance(long advanceDurationInMillis) {
		setCurrentTimeInMillis(currentTimeInMillis() + advanceDurationInMillis);
	}
}
