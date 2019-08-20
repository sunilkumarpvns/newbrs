package com.elitecore.coreeap.packet.types.tls.record.types;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.HandshakeMessageRecordType;

public class HandshakeMessageRecordTypeTest extends TestCase {

	/***
	 * 0					2					4					6					 8 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * | Handshake |		length				|	Handshake Messages...
	 * |  Type	   |	(HandMessage Length)	|
	 * -----------------------------------------------------------------------------------
	 * |								Handshake Messages... (Depends on Length)
	 * |
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
	 * Handshake Type   	= Section 7.4-Handshake protocol , Section 4.5-Enumerateds
	 * Length			    = Section 7.4-Handshake protocol
	 * Handshake Message    = Section 7.4-Handshake protocol	 
	 */	

	public HandshakeMessageRecordTypeTest(String name) {
		super(name);
	}
	/***
	 * This function will test the parseHandshakeRecord()
	 * Test Cases :
	 * 1. Valid Complete Record
	 * 2. Valid Minimun size Record
	 * 3. Incomplete Record
	 * 4. null
	 */
	public void testSetBytes(){
		final byte[] VALID_COMPLETE_RECORD = {
			1, //Handshake Type = Client Hello 
			0,0,75, //Handshake Message Length
			//client hello Message :
            3, 1, // Protocol Version
			97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
			32, // Sesssion ID Length<0..32>
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
			19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, // SessionID
			0, 4,// CipherSuite Length in bytes<2..65535>
			0, 1, 0, 10, // Cipher Suite { 1,10}
			1, // Compression Method Length in bytes <1..255>
			0 // Compression Method
		};
		final byte[] VALID_MIN_SIZE_RECORD ={
			14, //Handshake Type = ServerHello Done
			0,0,0, // length
		};
		final byte[] INCOMPLETE_RECORD ={
				1, //Handshake Type = Client Hello 
				0,0,75, //Handshake Message Length
				//client hello Message :
	            3, 1, // Protocol Version
				97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
				32, // Sesssion ID Length<0..32>				
		};
		
		try{
			HandshakeMessageRecordType tlsHandshakeRecord = null;
			final int MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION = 3;
			
			//Test Case : Valid Complete Record
			try{				
				tlsHandshakeRecord = new HandshakeMessageRecordType();
				/***
				 * 	PRotocol version does not have any affect in this test case
				 * 	So choosing Randomly any protocol between Tls1.0, Tls1.1 or Tls1.2
				 */
				tlsHandshakeRecord.setProtocolVersion(ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
				tlsHandshakeRecord.setBytes(VALID_COMPLETE_RECORD);
				byte[] handshakeMessage = new byte[75];
				System.arraycopy(VALID_COMPLETE_RECORD,4,handshakeMessage,0,75);
				assertEquals(1,tlsHandshakeRecord.getHandshakeMessageType());
				assertEquals(75,tlsHandshakeRecord.getHandshakeMessagelength());				
				assertEquals(true,Arrays.equals(VALID_COMPLETE_RECORD,tlsHandshakeRecord.getBytes()));
			}catch(Exception e){
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			//Test Case : Valid Minimun size Record
			try{
				tlsHandshakeRecord = new HandshakeMessageRecordType();
				/***
				 * 	PRotocol version does not have any affect in this test case
				 * 	So choosing Randomly any protocol between Tls1.0, Tls1.1 or Tls1.2
				 */
				tlsHandshakeRecord.setProtocolVersion(ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
				tlsHandshakeRecord.setBytes(VALID_MIN_SIZE_RECORD);
				assertEquals(14,tlsHandshakeRecord.getHandshakeMessageType());
				assertEquals(0,tlsHandshakeRecord.getHandshakeMessagelength());				
				assertEquals(true,Arrays.equals(VALID_MIN_SIZE_RECORD,tlsHandshakeRecord.getBytes()));
			}catch(Exception e){
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			//Test Case : Incomplete Record
			try{
				tlsHandshakeRecord = new HandshakeMessageRecordType();
				/***
				 * 	PRotocol version does not have any affect in this test case
				 * 	So choosing Randomly any protocol between Tls1.0, Tls1.1 or Tls1.2
				 */
				tlsHandshakeRecord.setProtocolVersion(ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
				tlsHandshakeRecord.setBytes(INCOMPLETE_RECORD);
				 fail("HandshakeMessageRecordType.setBytes(byte[]) is not throwing "
	                        + "IllegalArgumentException if the paramater is Incomplete Record");     
			}catch(IllegalArgumentException e){				
			}
			
			//Test Case : null
			try{
				tlsHandshakeRecord = new HandshakeMessageRecordType();
				/***
				 * 	PRotocol version does not have any affect int this test case
				 * 	So choosing Randomly any protocol between Tls1.0, Tls1.1 or Tls1.2
				 */
				tlsHandshakeRecord.setProtocolVersion(ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
				tlsHandshakeRecord.setBytes(null);
				 fail("HandshakeMessageRecordType.setBytes(byte[]) is not throwing "
	                        + "IllegalArgumentException if the paramater is null");    				
			}catch(IllegalArgumentException e){				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testsetBytes failed, reason: " + e.getMessage());
		}
	}
	
	/***
	 * This function will test the setHandshakeMessageType(int) and getHandshakeMessageType() function
	 * Test Cases : 
	 * 1. Valid Handshake Message Type
	 * 2. Invalid Handshake Message Type
	 */
	public void testSetGetHandshakeMessageType(){
		final int VALID_HANDSHAKE_MESSAGE_TYPE = 2;
		final int INVALID_HANDSHAKE_MESSAGE_TYPE  = -1;
		try{
			HandshakeMessageRecordType tlsHandshakeRecord = new HandshakeMessageRecordType();
			
			//Test Case : Valid Handshake Message Type
			try{
				tlsHandshakeRecord.setHandshakeMessageType(VALID_HANDSHAKE_MESSAGE_TYPE);
				assertEquals(VALID_HANDSHAKE_MESSAGE_TYPE,tlsHandshakeRecord.getHandshakeMessageType());
			}catch(Exception e){							
				fail("testSetGetHandshakeMessageType failed, reason: " + e.getMessage());
			}
			
			//Test Case : Invalid Handshake Message Type
			try{
				tlsHandshakeRecord.setHandshakeMessageType(INVALID_HANDSHAKE_MESSAGE_TYPE);
				fail("HandshakeMessageRecordType.setHandshakeMessageType(int) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Handshake Message Type");    	
			}catch(IllegalArgumentException e){							
			}						
		}catch(Exception e){
			e.printStackTrace();
			fail("testSetGetHandshakeMessageType failed, reason: " + e.getMessage());
		}
	}
	/***
	 * This function will test the setLength(int) and getHandshakeMessagelength() function
	 * Test Cases : 
	 * 1. Valid Length
	 * 2. Maximum Length
	 * 3. Minimum Length
	 * 4. Invalid Length	 
	 */
	public void testSetGetLength(){
		final int VALID_LENGTH = 75;
		final int VALID_MAX_LENGTH = 16777215;
		final int VALID_MIN_LENGTH = 0;
		final int INVALID_LENGTH = -1;
		try{
			HandshakeMessageRecordType tlsHandshakeRecord = new HandshakeMessageRecordType();
			//Test Case : Valid Length			
			try{
				tlsHandshakeRecord.setHandshakeMessagelength(VALID_LENGTH);
				assertEquals(VALID_LENGTH,tlsHandshakeRecord.getHandshakeMessagelength());
			}catch(Exception e){
				e.printStackTrace();
				fail("testSetGetLength failed, reason: " + e.getMessage());
			}
			
			//Test Case : Maximum Length
			try{
				tlsHandshakeRecord.setHandshakeMessagelength(VALID_MAX_LENGTH);
				assertEquals(VALID_MAX_LENGTH,tlsHandshakeRecord.getHandshakeMessagelength());
			}catch(Exception e){
				e.printStackTrace();
				fail("testSetGetLength failed, reason: " + e.getMessage());
			}
			
			//Test Case : Minimum Length
			try{
				tlsHandshakeRecord.setHandshakeMessagelength(VALID_MIN_LENGTH);
				assertEquals(VALID_MIN_LENGTH,tlsHandshakeRecord.getHandshakeMessagelength());
			}catch(Exception e){
				e.printStackTrace();
				fail("testSetGetLength failed, reason: " + e.getMessage());
			}

			//Test Case : Invalid Length
			try{
				tlsHandshakeRecord.setHandshakeMessagelength(INVALID_LENGTH);
				fail("HandshakeMessageRecordType.setHandshakeMessagelength(int) is not throwing "
                        + "IllegalArgumentException if the paramater has Invalid Message Length");    	
			}catch(IllegalArgumentException e){				
			}

		}catch(Exception e){			
			fail("testSetGetLength failed, reason: " + e.getMessage());
		}
	}
	
	/***
	 * This function will test the toBytes() function
	 * Test Cases : 
	 * 1. Valid Input
	 * 2. No Input
	 */
	public void testGetBytes(){
		final byte[] VALID_INPUT = {
				1, //Handshake Type = Client Hello 
				0,0,75, //Handshake Message Length
				//client hello Message :
	            3, 1, // Protocol Version
				97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
				32, // Sesssion ID Length<0..32>
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
				19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, // SessionID
				0, 4,// CipherSuite Length in bytes<2..65535>
				0, 1, 0, 10, // Cipher Suite { 1,10}
				1, // Compression Method Length in bytes <1..255>
				0 // Compression Method
		};
		try{
			HandshakeMessageRecordType tlsHandshakeRecord = null;
			final int MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION = 3;
			//Test Case : Valid Input
			try{
				/***
				 * 	PRotocol version does not have any affect in this test case
				 * 	So choosing Randomly any protocol between Tls1.0, Tls1.1 or Tls1.2
				 */
				tlsHandshakeRecord = new HandshakeMessageRecordType(VALID_INPUT, ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
//				tlsHandshakeRecord.setProtocolVersion(ProtocolVersion.getProtocolVersion(MAJOR_PROTOCOL_VERSION_FOR_ANY_TLS_VERSION, new Random().nextInt(3) + 1));
				assertEquals(true,Arrays.equals(VALID_INPUT,tlsHandshakeRecord.getBytes()));
			}catch(Exception e){
				e.printStackTrace();
				fail("testToBytes failed, reason: " + e.getMessage());
			}
			
			//Test Case : No Input
			try{
				tlsHandshakeRecord = new HandshakeMessageRecordType();
				final byte[] EXPECTED_BYTE_ARRAY = {
						0,// Handshake Type
						0,0,0 //Handshake Message Length
				};
				assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,tlsHandshakeRecord.getBytes()));
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
		suite.addTest(new HandshakeMessageRecordTypeTest("testSetBytes"));
		suite.addTest(new HandshakeMessageRecordTypeTest("testSetGetHandshakeMessageType"));
		suite.addTest(new HandshakeMessageRecordTypeTest("testSetGetLength"));		
		suite.addTest(new HandshakeMessageRecordTypeTest("testGetBytes"));
        return suite;
	}

}
