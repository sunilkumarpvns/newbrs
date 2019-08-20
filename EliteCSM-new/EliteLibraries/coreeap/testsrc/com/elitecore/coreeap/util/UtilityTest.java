package com.elitecore.coreeap.util;

import com.elitecore.coreeap.util.Utility;


import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UtilityTest extends TestCase {

	public UtilityTest(String name) {
		super(name);		
	}
	
	/***
	 * This function will test the byteToHex(byte[]) function
	 * Test Cases :
	 * 1. Valid Input
	 * 2. null
	 */
	public void testbyteToHex(){
		try{
			//Test Case : Valid Input
			try{
				final byte[] VALID_INPUT= {10,11,12,13,14};
				final String EXPECTED_OUTPUT_STRING = "0x0a0b0c0d0e";
				String output = Utility.bytesToHex(VALID_INPUT);
				assertTrue(output.equals(EXPECTED_OUTPUT_STRING));
			}catch(Exception e){
				e.printStackTrace();
				fail("testbyteToHex failed, reason : " + e.getMessage());
			}
			//Test Case : null
			try{
				final byte[] VALID_INPUT= null;
				final String EXPECTED_OUTPUT_STRING = "null";
				String output = Utility.bytesToHex(VALID_INPUT);
				assertTrue(output.equals(EXPECTED_OUTPUT_STRING));				
			}catch(Exception e){
				e.printStackTrace();
				fail("testbyteToHex failed, reason : " + e.getMessage());
			}

		}catch(Exception e){
			e.printStackTrace();
			fail("testbyteToHex failed, reason : " + e.getMessage());
		}
	}	
	public static TestSuite suite(){		
		TestSuite suite = new TestSuite();		
		suite.addTest(new UtilityTest("testbyteToHex"));		
		return suite;
	}
}
