package com.elitecore.coreeap.packet.types.tls.record.types;

import java.util.Arrays;

import com.elitecore.coreeap.packet.types.tls.record.types.AlertRecordType;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AlertRecordTypeTest extends TestCase {
	/***
	 * 0					1					2			
	 * ------------------------------------------
	 * | 		Alert		|		Alert	
	 * |  		Level		|	 Description
	 * ------------------------------------------
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
	 * Alert Level 		  = Section 7.2-Alert Protocol , Section 4.5-Enumerateds
	 * Alert Description  = Section 7.2-Alert Description , Section 4.5-Enumerateds
	 */	

	public AlertRecordTypeTest(String name) {
		super(name);
	}

	/***
	 * This function will test the setType function.
	 * Test Cases : 
	 * 1. Valid Complete Record
	 * 2. Invalid Alert Level
	 * 3. Invalid Alert Description
	 * 4. Incomplete Record
	 * 5. null
	 */
	public void testSetBytes(){
		final byte[] VALID_COMPLETE_RECORD = {
			2, //Alert Level
			51 //Alert Description
		};
		final byte[] INVALID_ALERT_LEVEL = {
				3, //Alert Level
				51 //Alert Description				
		};
		final byte[] INVALID_ALERT_DESC = {
				2, //Alert Level
				2 //Alert Description				
		};
		final byte[] INCOMPLETE_RECORD = {
				2, //Alert Level								
		};
		
		try{

			AlertRecordType tlsAlertType = null;
			//Test Case : Valid Complete Record
			try{
				tlsAlertType =  new AlertRecordType();
				tlsAlertType.setBytes(VALID_COMPLETE_RECORD);
				assertEquals(true,Arrays.equals(VALID_COMPLETE_RECORD,tlsAlertType.getBytes()));
			}catch(Exception e){				
				e.printStackTrace();
				fail("testSetData failed, reason: " + e.getMessage());
			}			

			//Test Case : Invalid Alert Level
			try{
				tlsAlertType =  new AlertRecordType();
				tlsAlertType.setBytes(INVALID_ALERT_LEVEL);
				fail("AlertRecordType.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Alert Level");    	
			}catch(IllegalArgumentException e){							
			}
			
			//Test Case : Invalid Alert Description
			try{
				tlsAlertType =  new AlertRecordType();
				tlsAlertType.setBytes(INVALID_ALERT_DESC);
				fail("AlertRecordType.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Alert Descriptioin");    	
			}catch(IllegalArgumentException e){							
			}

			//Test Case : Incomplete Alert Record
			try{
				tlsAlertType =  new AlertRecordType();
				tlsAlertType.setBytes(INCOMPLETE_RECORD);
				fail("AlertRecordType.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater is incomplete");    	
			}catch(IllegalArgumentException e){							
			}

			//Test Case : null
			try{
				tlsAlertType =  new AlertRecordType();
				tlsAlertType.setBytes(null);
				fail("AlertRecordType.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater is null");    	
			}catch(IllegalArgumentException e){							
			}

		}catch(Exception e){
			e.printStackTrace();
			fail("testSetGetHandshakeMessageType failed, reason: " + e.getMessage());
		}
	}
	
	/***
	 * This function will test the setLevel(int) and getLevel() function
	 * Test Cases :
	 * 1. Valid Alert Level  
	 * 2. Invalid Alert Level
	 */
	public void testSetGetAlertLevel(){
		final int VALID_ALERT_LEVEL = 2;
		final int INVALID_ALERT_LEVEL = 3;
		try{

			AlertRecordType tlsAlertType = new AlertRecordType();
			//Test Case : Valid Complete Record
			try{
				tlsAlertType.setAlertLevel(VALID_ALERT_LEVEL);	
				assertEquals(VALID_ALERT_LEVEL,tlsAlertType.getAlertLevel());
			}catch(Exception e){				
				e.printStackTrace();
				fail("testSetGetAlertLevel failed, reason: " + e.getMessage());
			}			

			//Test Case : Invalid Alert Level
			try{				  				
				tlsAlertType.setAlertLevel(INVALID_ALERT_LEVEL);
				fail("AlertRecordType.setAlertLevel(int) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Alert Level");    	
			}catch(IllegalArgumentException e){							
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testSetGetAlertLevel failed, reason: " + e.getMessage());
		}

	}
	
	/***
	 * This function will test the setDescription(int) and getDescription() function
	 * Test Cases : 
	 * 1. Valid Alert Description
	 * 2. Invalid Alert Description
	 */
	public void testSetGetAlertDescription(){
		final int VALID_ALERT_DESCRIPTION = 51;
		final int INVALID_ALERT_DESCRIPTION = 2;
		try{

			AlertRecordType tlsAlertType = new AlertRecordType();
			//Test Case : Valid Complete Record
			try{
				tlsAlertType.setAlertDescription(VALID_ALERT_DESCRIPTION);	
				assertEquals(VALID_ALERT_DESCRIPTION,tlsAlertType.getAlertDescription());
			}catch(Exception e){				
				e.printStackTrace();
				fail("testSetGetAlertDescription failed, reason: " + e.getMessage());
			}			

			//Test Case : Invalid Alert Level
			try{				  				
				tlsAlertType.setAlertDescription(INVALID_ALERT_DESCRIPTION);
				fail("AlertRecordType.setAlertDescription(int) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Alert Description");    	
			}catch(IllegalArgumentException e){							
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testSetGetAlertDescription failed, reason: " + e.getMessage());
		}
	}
	
	/***
	 * This function will test the getBytes() function
	 * Test Cases : 
	 * 1. Valid Input
	 * 2. No Input 
	 */
	public void testGetBytes(){
		final byte[] VALID_COMPLETE_RECORD = {
				2, //Alert Level
				51 //Alert Description
			};
		try{

			AlertRecordType tlsAlertType = null;
			//Test Case : Valid Complete Record
			try{
				tlsAlertType = new AlertRecordType();					
				tlsAlertType.setBytes(VALID_COMPLETE_RECORD);
				assertEquals(true,Arrays.equals(VALID_COMPLETE_RECORD,tlsAlertType.getBytes()));
			}catch(Exception e){				
				e.printStackTrace();
				fail("testToBytes failed, reason: " + e.getMessage());
			}			

			//Test Case : Invalid Alert Level
			try{				
				tlsAlertType = new AlertRecordType();				
				final byte[] EXPECTED_BYTE_ARRAY = {
					0, //Alert Level
					0 //Alert Description
				};
				assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,tlsAlertType.getBytes()));
			}catch(Exception e){				
				e.printStackTrace();
				fail("testToBytes failed, reason: " + e.getMessage());
			}					
		}catch(Exception e){
			e.printStackTrace();
			fail("testToBytes failed, reason: " + e.getMessage());
		}		
	}
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new AlertRecordTypeTest("testSetBytes"));
        suite.addTest(new AlertRecordTypeTest("testSetGetAlertLevel"));
        suite.addTest(new AlertRecordTypeTest("testSetGetAlertDescription"));
        suite.addTest(new AlertRecordTypeTest("testGetBytes"));
        return suite;
    }	
}
