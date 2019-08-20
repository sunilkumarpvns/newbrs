package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.util.Arrays;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ClientKeyExchangeTest extends TestCase {

    /***  
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * |	length			| 				EncryptedPreMasterSecret...								
     * | 					|
     * -----------------------------------------------------------------------------------
     * |
     * |						EncryptedPreMasterSecret.... ( depends on length)
     * -----------------------------------------------------------------------------------
     * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
     * Length 					= Section 4.3-Vectors
     * EncryptedPreMasterSecret	= Section 7.4.7.1-RSA encrypted premaster secret message 
     */

	public ClientKeyExchangeTest(String name) {
		super(name);		
	}
	/***
	 * This function will test the parseMessage(byte[]) function
	 * Test Cases : 
	 * 1. Valid Complete Message
	 * 2. Invalid Complete Message
	 * 2. Incomplete Message 
	 * 3. null
	 */
	public void testSetBytes(){
		final byte[] VALID_COMPLETE_MESSAGE = {
			0,48, // length
			47,58,4,5,54,65,12,45,65,78,54,95,65,25,14,12,24,32,55,12,14,15,16,32,54,15,14,21,23,2,11
			,5,54,65,15,21,32,45,65,78,12,45,65,45,78,12,45,56 // Key Exchange Value (Encrypted PreMasterSecret)
		};
		final byte[] INCOMPLETE_MESSAGE = {
			0,48, // length
			12,45,87,98,45 // Key Exchange Value (Encrypted PreMasterSecret)
		};		
		try{
			ClientKeyExchange clientKeyExchange = null;
			//Test Case : Valid Complete Message
			try{
				clientKeyExchange = new ClientKeyExchange();
				clientKeyExchange.setBytes(VALID_COMPLETE_MESSAGE);
				byte[] keyExchageValue = new byte[48];
				System.arraycopy(VALID_COMPLETE_MESSAGE,2,keyExchageValue,0,48);
				assertEquals(48,clientKeyExchange.getKeyExchangeLength());
				assertEquals(true,Arrays.equals(keyExchageValue,clientKeyExchange.getKeyExchangeValue()));
			}catch(Exception e){
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			//Test Case : Incomplete Message 
			try{
				clientKeyExchange = new ClientKeyExchange();
				clientKeyExchange.setBytes(INCOMPLETE_MESSAGE);
                fail("ClientKeyExchange.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater has Illegal EncryptedPreMasterSecret Length");              				
			}catch(IllegalArgumentException e){				
			}
			
			//Test Case : null
			try{
				clientKeyExchange = new ClientKeyExchange();
				clientKeyExchange.setBytes(null);
				fail("ClienthKeyExchange.parseMessge(byte[]) is not throwing "
						+ "IllegalArgumentException if the parameter is null");
			}catch(IllegalArgumentException e){				
			}
			
		}catch(Exception e){
			fail("testsetBytes failed, reason: " + e.getMessage());
		}
	}
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ClientKeyExchangeTest("testSetBytes"));
        return suite;
    }

}
