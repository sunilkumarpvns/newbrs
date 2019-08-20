package com.elitecore.diameterapi.diameter.common.packet.avps.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.elitecore.diameterapi.diameter.common.packet.avps.NegativeValueException;

public class AvpUnsigned64Test {

	@Test
	public void testSmallValue(){
		
		String value = "1844674407370955";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(value);
		assertEquals("Improper Avp value arrived", avp.getStringValue(), value);
	}
	
	@Test(expected=NegativeValueException.class)
	public void testNegetiveSmallValue(){
		
		String value = "-1";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(value);
		fail("Negative Value must not be supported");
	}
	
	@Test
	public void testBigValue(){
		
		String value = "18446744073709551614";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(value);
		assertEquals("Improper Avp value arrived", avp.getStringValue(), value);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testNegetiveBigValue(){
		
		String value = "-18446744073709551614";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(value);
		fail("Negative Value must not be supported");
	}
	
	@Test
	public void testDoPlusWithSmallValues(){

		String avpValue = "1844";
		String addValue = "1879";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		try{
			avp.doPlus(addValue);
			assertEquals("Small values not added properly", avp.getInteger(), 1844+1879);
		}catch (IllegalArgumentException e){
			fail("Do plus improper for values: " + avpValue + "  ,  " + addValue);
		}
	}
	
	@Test
	public void testDoPlusWithSmallLargeValues(){

		String avpValue = "1";
		String addValue = "18446744073709551614";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		try{
			avp.doPlus(addValue);
			assertEquals("Small Large values not added properly", avp.getStringValue(), "18446744073709551615");
		}catch (IllegalArgumentException e){
			fail("Do plus improper for values: " + avpValue + "  ,  " + addValue);
		}
	}
	
	@Test
	public void testDoPlusWithLargeSmallValues(){

		String avpValue = "18446744073709551614";
		String addValue = "1";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		try{
			avp.doPlus(addValue);
			assertEquals("Large Small values not added properly", avp.getStringValue(), "18446744073709551615");
		}catch (IllegalArgumentException e){
			fail("Do plus improper for values: " + avpValue + "  ,  " + addValue);
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDoPlusOffRangeLargeSmallValues(){

		String avpValue = "18446744073709551614";
		String addValue = "10";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		avp.doPlus(addValue);
		fail("Must give off range exception");
	}
	
	@Test
	public void testDoPlusWithResultLarge(){

		String avpValue = String.valueOf(Long.MAX_VALUE);
		String addValue = "179879779";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		try{
			avp.doPlus(addValue);
		}catch (IllegalArgumentException e){
			fail("Do plus improper for values: " + avpValue + "  ,  " + addValue);
		}
	}
	
	@Test(expected=NumberFormatException.class)
	public void testDoPlusOffRangeLargeValue(){

		String avpValue = "18446744073709551614";
		String addValue = "18446744073709551614";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		avp.doPlus(addValue);
		fail("Must give off range exception");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDoPlusOffRangeSmallValue(){

		String avpValue = "10";
		String addValue = "118446744073709551614";
		AvpUnsigned64 avp = new AvpUnsigned64(0, 0, (byte)32, "", "");
		avp.setStringValue(avpValue);
		avp.doPlus(addValue);
		fail("Must give off range exception");
	}
	
}
