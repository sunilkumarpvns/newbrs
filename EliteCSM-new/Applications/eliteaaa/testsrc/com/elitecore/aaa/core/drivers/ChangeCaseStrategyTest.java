package com.elitecore.aaa.core.drivers;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.drivers.ChangeCaseStrategy.NoChange;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy.ToLowerCase;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy.ToUpperCase;

@RunWith(JUnitParamsRunner.class)
public class ChangeCaseStrategyTest {
	private static final String SOME_NON_NULL_STRING = "SOME_NON_NULL_STRING";
	
	@Test
	@Parameters(value = {"-1", "0", "4"})
	public void testGet_ShouldReturnNoChangeStrategy_IfIdPassedIsUnknown(int id) {
		assertEquals(NoChange.class, ChangeCaseStrategy.get(id).getClass());
	}
	
	@Test
	@Parameters(method = "dataFor_testGet_ShouldReturnStrategyBasedOnIdentifierProvided")
	public void testGet_ShouldReturnStrategyBasedOnIdentifierProvided(int id, 
			Class<?> clazz) {
		assertEquals(clazz, ChangeCaseStrategy.get(id).getClass());
	}
	
	public Object[] dataFor_testGet_ShouldReturnStrategyBasedOnIdentifierProvided() {
		return $(
				$(1, NoChange.class),
				$(2, ToLowerCase.class),
				$(3, ToUpperCase.class)
		);
	}
	
	@Test
	public void testNone_ShouldReturnNoChangeStrategy() {
		assertEquals(NoChange.class, ChangeCaseStrategy.none().getClass());
	}
	
	@Test
	@Parameters(method = "dataFor_testNoChangeStrategy_ShouldReturnTheStringUnchanged")
	public void testNoChangeStrategy_ShouldReturnTheStringUnchanged(
			String inputString) {
		assertEquals(inputString, new NoChange().apply(inputString));
	}
	
	public Object[] dataFor_testNoChangeStrategy_ShouldReturnTheStringUnchanged() {
		return $(
				$((String)null),
				$(""),
				$("a"),
				$("user abc"),
				$(SOME_NON_NULL_STRING)
			);
	}
	
	@Test
	@Parameters(method = "dataFor_testToLowerStrategy_ShouldReturnTheStringInLowerCase")
	public void testToLowerStrategy_ShouldReturnTheStringInLowerCase(
			String inputString) {
		assertEquals(inputString.toLowerCase(), new ToLowerCase().apply(inputString));
	}
	
	public Object[] dataFor_testToLowerStrategy_ShouldReturnTheStringInLowerCase() {
		return $(
				$(""),
				$("A"),
				$("uSeR ABC"),
				$(SOME_NON_NULL_STRING.toUpperCase())
			);
	}
	
	@Test
	@Parameters(method = "dataFor_testToLowerStrategy_ShouldReturnTheStringInUpperCase")
	public void testToUpperStrategy_ShouldReturnTheStringInUpperCase(
			String inputString) {
		assertEquals(inputString.toUpperCase(), new ToUpperCase().apply(inputString));
	}
	
	public Object[] dataFor_testToLowerStrategy_ShouldReturnTheStringInUpperCase() {
		return $(
				$(""),
				$("a"),
				$("A"),
				$("uSeR ABC"),
				$(SOME_NON_NULL_STRING.toLowerCase())
			);
	}
}
