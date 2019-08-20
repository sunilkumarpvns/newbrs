package com.elitecore.commons.base;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author narendra.pathai
 *
 */
public class SettableTickerTest {

	private SettableTicker ticker;
	private final long initialTime = System.nanoTime();
	
	@Before
	public void createTicker() {
		ticker = new SettableTicker(initialTime);
	}
	
	@Test
	public void returnsInitialTimeOnFirstInvocation() {
		assertThat(ticker.nanoTime(), is(equalTo(initialTime)));
	}
	
	@Test
	public void initialTimeCanOnlyBeSetOnceFromConstructor() {
		ticker.setNanoTime(initialTime + 1000);
		
		assertThat(ticker.nanoTime(), is(equalTo(initialTime)));
	}
	
	@Test
	public void returnsNextTimeOnlyAfterInitialTimeIsUtilizedByFirstInvocation() {
		ticker.setNanoTime(initialTime + 1000);
		
		ticker.nanoTime();
		
		assertThat(ticker.nanoTime(), is(equalTo(initialTime + 1000)));
	}
	
	@Test
	public void keepsReturningInitialTimeIfNextTimeIsNotSet() {
		assertThat(ticker.nanoTime(), is(equalTo(initialTime)));
		assertThat(ticker.nanoTime(), is(equalTo(initialTime)));
	}
}
