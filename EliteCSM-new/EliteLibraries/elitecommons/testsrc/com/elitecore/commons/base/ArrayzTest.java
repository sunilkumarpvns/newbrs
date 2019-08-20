package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import static junitparams.JUnitParamsRunner.$;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class ArrayzTest {

	@Test
	public void test() {
		assertTrue(Arrayz.isNullOrEmpty(new Object[]{}));
	}
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnTrue_IfArrayIsNull() {
		assertTrue(Arrayz.isNullOrEmpty((Object[])null));
	}
	
	@Test
	@Parameters(method = "dataFor_testIsNullOrEmpty")
	public void testIsNullOrEmpty(Object[] inputArray, boolean expectedResult) {
		assertEquals(expectedResult, Arrayz.isNullOrEmpty(inputArray));
	}
	
	public static Object[] dataFor_testIsNullOrEmpty() {
		return $(
				// input array							expectedResult
				$($((Object)null), 							false),
				$($((Object)null, (Object)null), 			false),
				$($("", null), 								false),
				$($("", ""), 								false),
				$($(null, ""), 								false),
				$($("test", "test1"), 						false)
		); 
	}
}
