package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ClientHelloTest extends TestCase {

	/***
	 * 0					2					4					6					 8 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * | Protocol  Version	|			Client Random ( 32 bytes).......
	 * |  Major		Minor	|
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * | Client Random		|SessionID	|	Session ID (Length defines in SessionIDLength)	 
	 * | 					|Length		|				(suppose SessionID length = 5)
	 * -----------------------------------------------------------------------------------
	 * | CipherSuite List	| First CipherSuite	|Second CipherSuite	|Compression |Compression			
	 * | 	Length(if 4)	|			|		|			|		|Method Len	 | Method
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
	 * Protocol Version   = Section 6.2.1-Fragmentation
	 * Client Random	  = Section 7.4.1.2-Client hello
	 * Session ID 		  = Section 7.4.1.2-Client hello
	 * CipherSuite		  = Section 7.4.1.2-Client hello
	 * Compression Method = Section 7.4.1.2-Client hello
	 * SessionID Length,CipherSuiteList Length,Compression Method Length = Section 4.3-Vectors 
	 */	

	public ClientHelloTest(String name) {
		super(name);
	}
	/***
	 * This function will test the parseMessage function
	 * Test Cases :
	 *  1. VALID_COMPLETE_MESSAGE
	 *  2. VALID_MIN_LENGTH_MESSAGE
	 *  3. INVALID_CIPHERSUITE_LENGTH_MESSAGE
	 *  4. INVALID_SESSIONID_LENGTH_MESSAGE 
	 *  5. null value
	 *  6. incomplete byte[]  
	 */
	public void testSetBytes() {

		final byte[] VALID_COMPLETE_MESSAGE = {
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
		final byte[] VALID_MIN_LENGTH_MESSAGE = { 
                3, 1, // Protocol Version
                97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
                0, // Sesssion ID Length <0..32>
                0, 2,// CipherSuite Length in bytes<2..65535>
                0, 1,// Cipher Suite { 1,10}
                1, // Compression Method Length in bytes<1.255>
                0 // Compression Method
        };

		final byte[] INVALID_CIPHERSUITE_LENGTH_MESSAGE = {
                3, 1, // Protocol Version
                97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
                32, // Sesssion ID Length <0..32>
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, // SessionID
                (byte) 255, (byte) 255,// CipherSuite Length in bytes<2..65535>
                0, 1, 0, 10, // Cipher Suite { 1,10}
                1, // Compression Method Length in bytes <1..255>
                0 // Compression Method
		};

		final byte[] INVALID_SESSIONID_LENGTH_MESSAGE = { 
                3, 1, // Protocol Version
				97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
				(byte) 255, // Sesssion ID Length <0..32>
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, // SessionID
				0, 4,// CipherSuite Length in bytes <2..65535>
				0, 1, 0, 10, // Cipher Suite { 1,10}
				1, // Compression Method Length in bytes
				0 // Compression Method
		};
        final byte[] INCOMPLETE_MESSAGE = { 
                3, 1, // Protocol Version
                97, 26, 25, 87, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, // Random
                0, // Sesssion ID Length <0..32>                
        };

		try {
			ClientHello clientHello = null;

            // Test Case : Valid Complete Message
			try {
                clientHello = new ClientHello();
				clientHello.setBytes(VALID_COMPLETE_MESSAGE);
				// check protocol version
				assertEquals(3, clientHello.getProtocolVersion().getMajor());
				assertEquals(1, clientHello.getProtocolVersion().getMinor());
				// check client random
				// check session ID
				byte[] sessionID = new byte[32];
                System.arraycopy(VALID_COMPLETE_MESSAGE, 35, sessionID, 0, 32);
                assertEquals(true, Arrays.equals(sessionID, clientHello.getSessionId()));
				// check ciphersuite
				List<Integer> arrayList = clientHello.getCiphersuiteList();
                assertEquals(2, arrayList.size());
                assertEquals(1, ((Integer) arrayList.get(0)).intValue());
                assertEquals(10, ((Integer) arrayList.get(1)).intValue());
				// check complete client hello message
				assertEquals(true, Arrays.equals(VALID_COMPLETE_MESSAGE,clientHello.getBytes()));
			} catch (Exception e) {
				e.printStackTrace();
                fail("testsetBytes failed, reason: " + e.getMessage());
			}
            
            // Test Case : VALID MINIMUM LENGTH MESSAGE
            try {
                clientHello = new ClientHello();
                clientHello.setBytes(VALID_MIN_LENGTH_MESSAGE);
                // check protocol version
                assertEquals(3, clientHello.getProtocolVersion().getMajor());
                assertEquals(1, clientHello.getProtocolVersion().getMinor());
                // check client random
                assertEquals(null, clientHello.getSessionId());
                // check ciphersuite
                List<Integer> arrayList = clientHello.getCiphersuiteList();
                assertEquals(1, arrayList.size());
                assertEquals(1, ((Integer) arrayList.get(0)).intValue());                
                // check complete client hello message
                assertEquals(true, Arrays.equals(VALID_MIN_LENGTH_MESSAGE, clientHello.getBytes()));
            } catch (Exception e) {
                fail("testsetBytes failed, reason: " + e.getMessage());
            }

            // Test Case : INVALID CIPHERSUITE LENGTH MESSAGE
            try {
                clientHello = new ClientHello();
                clientHello.setBytes(INVALID_CIPHERSUITE_LENGTH_MESSAGE);
                fail("ClientHello.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater has invalid ciphersuite length");                
            } catch (IllegalArgumentException e) {                
            }
            
            // Test Case : INVALID SESSIONID LENGTH MESSAGE
            try {
                clientHello = new ClientHello();
                clientHello.setBytes(INVALID_SESSIONID_LENGTH_MESSAGE);
                fail("ClientHello.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater has invalid session ID length");                
            } catch (IllegalArgumentException e) {
            }

            
            // Test Case : incomplete message
            try {
                clientHello = new ClientHello();         
                clientHello.setBytes(INCOMPLETE_MESSAGE);
                fail("ClientHello.setBytes(byte[]) is not throwing "
                        + "IllegalArgumentException if the paramater is incomplete byte[]");
            } catch (IllegalArgumentException iae) {
            }

			// Test Case : null value
			try {
                clientHello = new ClientHello();
				clientHello.setBytes(null);
				fail("ClientHello.setBytes(byte[]) is not throwing "
						+ "IllegalArgumentException if the paramater is null");
			} catch (IllegalArgumentException iae) {
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("testsetBytes failed, reason: " + e.getMessage());
		}
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ClientHelloTest("testSetBytes"));
        return suite;
	}

}
