package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author narendra.pathai
 */
public class StopwatchTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Stopwatch stopwatch;
	private SettableTicker ticker;
	private long currentNanos = 0;

	@Before
	public void setUp() {
		ticker = new SettableTicker(currentNanos);
		stopwatch = new Stopwatch(ticker);
	}

	@Test
	public void testConstructor_ShouldThrowNullPointerException_IfTickerIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("ticker is null");

		new Stopwatch(null);
	}

	@Test
	public void testElapsedInNanos_ShouldReturnTheElapsedTime_WhenStopwatchHasBeenStopped()
			throws InterruptedException {
		
		runLap(100);

		assertEquals(100, stopwatch.elapsedInNanos());
	}

	@Test
	public void testElapsedInNanos_ShouldReturnSameElapsedTimeEverytime_WhenStopwatchHasBeenStopped()
			throws InterruptedException {
		runLap(100);

		assertEquals(100, stopwatch.elapsedInNanos());
		assertEquals(100, stopwatch.elapsedInNanos());
	}

	@Test
	public void testElapsedInNanos_ShouldReturnTheDifferenceOfCurrentTimeAndStartTime_IfStopwatchHasNotBeenStopped()
			throws InterruptedException {
		runLap(100);

		assertEquals(100, stopwatch.elapsedInNanos());
	}

	@Test
	public void testStart_ShouldThrowIllegalStateException_IfStartIsCalledTwiceWithoutStoppingTheWatch() {
		stopwatch.start();

		exception.expect(IllegalStateException.class);
		exception.expectMessage("This stopwatch is already running.");

		stopwatch.start();
	}

	@Test
	public void testStop_ShouldThrowIllegalStateException_IfStopIsCalledTwiceWithoutRestartingTheWatch() {
		stopwatch.start();
		stopwatch.stop();

		exception.expect(IllegalStateException.class);
		exception.expectMessage("This stopwatch is already stopped.");

		stopwatch.stop();
	}

	@Test
	public void testStop_ShouldThrowIllegalStateException_IfStopIsCalledWithoutStartingTheWatch() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("This stopwatch is already stopped.");

		stopwatch.stop();
	}

	@Test
	public void testElapsedInNanos_ShouldReturnTotalElapsedTime_WhenStopwatchIsStartedAndStoppedTwice()
			throws InterruptedException {
		
		runLap(100);
		
		runLap(200);

		assertEquals(300, stopwatch.elapsedInNanos());
	}
	
	@Test
	public void testElapsedInNanos_ShouldReturnTheTotalElapsedTime_WhenTheSameStopwatchIsStartedAgain() throws InterruptedException {
		runLap(100);

		runLap(200);
		
		long startTime = System.nanoTime();
		ticker.setNanoTime(startTime);
		stopwatch.start();
		ticker.setNanoTime(startTime + 300);
		
		assertEquals(600, stopwatch.elapsedInNanos());
	}

	@Test
	public void testElapsedTime_WithTimeUnit_ShouldReturnElapsedTimeInDesiredTimeUnit()
			throws InterruptedException {
		runLap(100);

		assertEquals(TimeUnit.NANOSECONDS.toMillis(100),
				stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
	}

	@Test
	public void testToString_ShouldRepresentTheElapsedTimeInSeconds_IfTimeElapsedIsMeasurableInSeconds()
			throws InterruptedException {
		runLap(TimeUnit.SECONDS.toNanos(1));
		assertEquals("1.000 s", stopwatch.toString());
	}
	
	@Test
	public void testToString_ShouldRepresentTheElapsedTimeInMilliSeconds_IfTimeElapsedIsMeasurableInMilliSeconds()
			throws InterruptedException {
		runLap(TimeUnit.MILLISECONDS.toNanos(1));
		assertEquals("1.000 ms", stopwatch.toString());
	}
	
	@Test
	public void testToString_ShouldRepresentTheElapsedTimeInMicroSeconds_IfTimeElapsedIsMeasurableInMicroSeconds()
			throws InterruptedException {
		runLap(TimeUnit.MICROSECONDS.toNanos(1));
		assertEquals("1.000 μs", stopwatch.toString());
	}
	
	@Test
	public void testToString_ShouldRepresentTheElapsedTimeInNanoSeconds_IfTimeElapsedIsMeasurableInNanoSeconds()
			throws InterruptedException {
		runLap(TimeUnit.NANOSECONDS.toNanos(10));
		assertEquals("10.00 ns", stopwatch.toString());
	}
	
	@Test
	public void testFormat_ShouldFormatTheTimeWithProvidedFormat() throws InterruptedException {
		runLap(TimeUnit.SECONDS.toNanos(1));
		assertEquals("Elapsed time is 1.00 s", stopwatch.format("Elapsed time is %.3g %s"));
	}
	
	@Test
	public void testStart_ShouldReturnThis() {
		assertSame(stopwatch, stopwatch.start());
	}
	
	@Test
	public void testStop_ShouldReturnThis() {
		assertSame(stopwatch, stopwatch.start().stop());
	}
	
	@Test
	public void testDefaultConstructor_ShouldCreateStopwatchInstance_WithSystemTicker() throws InterruptedException {
		Stopwatch stopwatchUnderTest = new Stopwatch().start();
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		
		stopwatchUnderTest.stop();
		
		assertEquals(1, stopwatchUnderTest.elapsedTime(TimeUnit.SECONDS));
	}

	private void runLap(long durationInNanos) throws InterruptedException {
		stopwatch.start();

		currentNanos = currentNanos + durationInNanos;
		ticker.setNanoTime(currentNanos);

		stopwatch.stop();
	}
}
