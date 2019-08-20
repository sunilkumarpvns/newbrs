package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class FixedTimeSourceTest {
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testGetCurrentTimeInMillis_ShouldReturnZero_WhenTimeSourceIsCreatedUsingDefaultConstructor() {
		assertEquals(0, new FixedTimeSource().currentTimeInMillis());
	}
	
	@Test
	@Parameters({
		"1",
		"10",
		"100"
	})
	public void testGetCurrentTimeInMillis_ShouldReturnTimeProvidedInConstructor_WhenTimeSourceIsCreatedUsingConstructorWithInitialTimeParameter(long currentTimeInMillis) {
		assertEquals(currentTimeInMillis, new FixedTimeSource(currentTimeInMillis).currentTimeInMillis());
	}
	
	@Test
	@Parameters({
		"1",
		"10",
		"100"
	})
	public void testGetCurrentTimeInMillis_ShouldReturnTimeProvided_WhenTimeSourceIsSetUsingSetter(long currentTimeInMillis) {
		FixedTimeSource timeSource = new FixedTimeSource();
		timeSource.setCurrentTimeInMillis(currentTimeInMillis);
		
		assertEquals(currentTimeInMillis, timeSource.currentTimeInMillis());
	}
	
	@Test
	@Parameters({
		"0",
		"1",
		"10",
		"100"
	})
	public void advancingTheTimesourceShouldChangeTheCurrentTimeValueByProvidedMilliseconds(long advanceDurationInMillis) {
		FixedTimeSource timeSource = new FixedTimeSource();
		long currentTime = System.currentTimeMillis();
		timeSource.setCurrentTimeInMillis(currentTime);
		
		timeSource.advance(advanceDurationInMillis);
		
		assertEquals(timeSource.currentTimeInMillis(), currentTime + advanceDurationInMillis);
	}
}
