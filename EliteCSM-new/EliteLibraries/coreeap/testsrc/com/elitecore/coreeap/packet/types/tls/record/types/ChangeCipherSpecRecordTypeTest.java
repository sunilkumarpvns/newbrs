package com.elitecore.coreeap.packet.types.tls.record.types;

import java.util.Arrays;

import com.elitecore.coreeap.packet.types.tls.record.types.ChangeCipherSpecRecordType;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChangeCipherSpecRecordTypeTest extends TestCase {

	public ChangeCipherSpecRecordTypeTest(String name) {
		super(name);
	}
	/***
	 * This function will test the toBytes() function
	 * Test Cases :
	 * 1. Valid Input
	 */
	public void testGetBytes(){
		try{
			ChangeCipherSpecRecordType tlsChangeCipherSpec = new ChangeCipherSpecRecordType();
			tlsChangeCipherSpec.setChangeCipherSpecType(1);
			//Test Case : Valid Input
			final byte[] EXPECTED_BYTE_ARRAY ={
				1 //CCS Type	
			};
			assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,tlsChangeCipherSpec.getBytes()));
		}catch(Exception e){			
            fail("testGetBytes failed, reason: " + e.getMessage());
		}
	}
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ChangeCipherSpecRecordTypeTest("testGetBytes"));
        return suite;
    }
}
