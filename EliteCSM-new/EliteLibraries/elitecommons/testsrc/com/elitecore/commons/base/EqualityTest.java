package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class EqualityTest {

	@Test
	@Parameters({
				"0 ,0 ,true",
				"0 ,1 ,false",
				"-1,0 ,false",
				"-1,-1,true",
				"1 ,1 ,true"
				})
	public void testEquals_ForInt(int iThis, int iThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(iThis, iThat));
	}
	
	@Test
	@Parameters({
				"0 ,0 ,true",
				"0 ,1 ,false",
				"-1,0 ,false",
				"-1,-1,true",
				"1 ,1 ,true"
				})
	public void testEquals_ForLong(long lThis, long lThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(lThis, lThat));
	}
	
	@Test
	@Parameters({
				"0 ,0 ,true",
				"0 ,1 ,false",
				"-1,0 ,false",
				"-1,-1,true",
				"1 ,1 ,true"
				})
	public void testEquals_ForByte(byte bThis, byte bThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(bThis, bThat));
	}
	
	@Test
	@Parameters({
				"0 ,0 ,true",
				"0 ,1 ,false",
				"-1,0 ,false",
				"-1,-1,true",
				"1 ,1 ,true",
				"1.1,1.1,true",
				"-1,-1.0,true"
				})
	public void testEquals_ForDouble(double dThis, double dThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(dThis, dThat));
	}
	
	@Test
	@Parameters({
				"a ,a ,true",
				"a ,b ,false",
				"b,a ,false"
				})
	public void testEquals_ForByte(char cThis, char cThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(cThis, cThat));
	}

	@Test
	@Parameters({
				"0 ,0 ,true",
				"0 ,1 ,false",
				"-1,0 ,false",
				"-1,-1,true",
				"1 ,1 ,true",
				"1.1,1.1,true",
				"-1,-1.0,true"
				})
	public void testEquals_ForFloat(float fThis, float fThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(fThis, fThat));
	}
	
	@Test
	@Parameters({
				"this,this ,true",
				"This ,this ,false",
				"this,This ,false",
				"THIS,this,false"
				})
	public void testEquals_ForString(String sThis, String sThat, boolean expectedResult){
		assertEquals(expectedResult, Equality.areEqual(sThis, sThat));
	}
	
	@Test
	public void testEquals_ForString_WhenThisIsNull(){
		assertFalse(Equality.areEqual(null, "that"));
	}
	
	@Test
	public void testEquals_ForString_WhenThisAndThatAreNull(){
		assertTrue(Equality.areEqual(null, null));
	}
	
	@Test
	public void testEquals_ForString_WhenThatIsNull(){
		assertFalse(Equality.areEqual("this", null));
	}
}
