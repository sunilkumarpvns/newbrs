package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.util.Arrays;

import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerHello;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServerHelloTest extends TestCase {

    /***
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * | Protocol  Version  |           Server Random ( 32 bytes).......
     * |  Major     Minor   |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * | Server Random      |SessionID  |   Session ID (Length defines in SessionIDLength)   
     * |                    |Length     |               (suppose SessionID length = 6)
     * -----------------------------------------------------------------------------------
     * |        |  First CipherSuite |Compression|          
     * |        |                    | Method    |
     * -------------------------------------------
     * 
     * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
     * Protocol Version   = Section 6.2.1-Fragmentation
     * Client Random      = Section 7.4.1.3-Server hello
     * SessionID Length   = Section 4.3-Vectors
     * Session ID         = Section 7.4.1.3-Server hello
     * CipherSuite        = Section 7.4.1.3-Server hello
     * Compression Method = Section 7.4.1.3-Server hello  
     */
	public ServerHelloTest(String name) {
		super(name);
	}
    /***
     * This function will test the setProtocolVersion(int,int),getProtocolVersionMajor() and getProtocolVersionMinor()
     * Test Cases:
     * 1. VALID PROTOCOL VERSION
     * 2. INVALID PROTOCOL VERSION 
     */
    public void testSetGetProtocolVersion(){
        try{
            ServerHello serverHello = null;
            
            //Test Case : VALID PROTOCOL VERSION : Major=3 and Minor=1
            try{
                serverHello = new ServerHello();
                serverHello.setProtocolVersion(ProtocolVersion.TLS1_0);
                assertEquals(3,serverHello.getProtocolVersionMajor());
                assertEquals(1,serverHello.getProtocolVersionMinor());                
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetProtocolVersion failed, reason: " + e.getMessage());
            }
            
//            //Test Case : INVALID PROTOCOL VERSION : Negative Version
//            try{
//                serverHello = new ServerHello();
////                serverHello.setProtocolVersion(-1,-1);
//                fail("ServerHello.setProtocolVersion(int,int) is not throwing "
//                        + "IllegalArgumentException if the paramater has invalid protocol version");     
//            }catch(IllegalArgumentException iae){                
//            }
            
        }catch(Exception e){
            e.printStackTrace();
            fail("testSetGetProtocolVersion failed, reason: " + e.getMessage());
        }
    }
    
    /***
     * This function will test the setServerRandom(byte[]) and getServerRandom() function
     * Test Cases:
     * 1. VALID SERVER RANDOM
     * 2. INVALID SERVER RANDOM
     * 3. null
     */
    public void testSetGetServerRandom(){
           final byte[] VALID_SERVER_RANDOM = {
             47,97,45,65, //unix time
             1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28 // random number
           };
           
           final byte[] INVALID_SERVER_RANDOM = {
               47,97,45,65 // unix time    
           };
           
           try{
               ServerHello serverHello = null;
               
               //Test case : VALID SERVER RANDOM
               try{
                   serverHello = new ServerHello();
                   serverHello.setServerRandom(VALID_SERVER_RANDOM);
                   assertEquals(true,Arrays.equals(VALID_SERVER_RANDOM,serverHello.getServerRandom()));
               }catch(Exception e){
                   e.printStackTrace();
                   fail("testSetGetServerRandom failed, reason: " + e.getMessage());                    
               }
               
               //Test Case : INVALID SERVER RANDOM
               try{
                   serverHello = new ServerHello();
                   serverHello.setServerRandom(INVALID_SERVER_RANDOM);                   
                   fail("ServerHello.setServerRandom(byte[]) is not throwing "
                           + "IllegalArgumentException if the paramater has invalid server random length");     
               }catch(IllegalArgumentException iae){                   
               }
               
               //Test Case : null
               try{
                   serverHello = new ServerHello();
                   serverHello.setServerRandom(null);                   
                   fail("ServerHello.parseMessage(byte[]) is not throwing "
                           + "IllegalArgumentException if the paramater has invalid server random");    
                   
               }catch(IllegalArgumentException iae){                   
               }
           }catch(Exception e){
               e.printStackTrace();
               fail("testSetGetServerRandom failed, reason: " + e.getMessage());   
           }
           
    }

    /***
     * This function will test the setSessionIdLength(int) and getSessionIdLength() 
     * Test Cases:
     * 1. Valid Session ID Length 
     * 2. Maximam Session ID Length
     * 3. Minimum Session ID Length
     * 4. Invalid Session ID Length 
     */
    public void testSetGetSessionIdLength(){
        try{
            ServerHello serverHello = new ServerHello();
            final int VALID_SESSIONID_LENGTH = 16;
            final int MAX_SESSIONID_LENGTH = 32;
            final int MIN_SESSIONID_LENGTH = 0;
            final int INVALID_SESSIONID_LENGTH = 64;
            
            //Test Case : Valid Session ID Length 
            try{                
                serverHello.setSessionIdLength(VALID_SESSIONID_LENGTH);
                assertEquals(VALID_SESSIONID_LENGTH,serverHello.getSessionIdLength());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetSessionId failed, reason: " + e.getMessage());
            }
            
            //Test Case : Maximam Session ID Length
            try{
                serverHello.setSessionIdLength(MAX_SESSIONID_LENGTH);
                assertEquals(MAX_SESSIONID_LENGTH,serverHello.getSessionIdLength());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetSessionId failed, reason: " + e.getMessage());
            }

            //Test Case : Minimum Session ID Length
            try{                
                serverHello.setSessionIdLength(MIN_SESSIONID_LENGTH);
                assertEquals(MIN_SESSIONID_LENGTH,serverHello.getSessionIdLength());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetSessionId failed, reason: " + e.getMessage());
            }

            //Test Case : Invalid Session ID Length
            try{                
                serverHello.setSessionIdLength(INVALID_SESSIONID_LENGTH);
                fail("ServerHello.setSessionIdLength(int) is not throwing "
                        + "IllegalArgumentException if the paramater has invalid Session ID Length");     
            }catch(IllegalArgumentException iae){                
            }
            
        }catch(Exception e){
            e.printStackTrace();
            fail("testSetGetSessionId failed, reason: " + e.getMessage());
        }
    }
    
    /***
     * This function will test the setSessionId(byte[]) and getSessionId() function
     * Test Cases:
     * 1. VALID SESSION ID
     * 2. INVALID SESSION ID
     * 3. null
     */
    public void testSetGetSessionId(){
           final byte[] VALID_SESSION_ID = {             
             1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32 // session Id
           };
           
           final byte[] INVALID_SESSION_ID = {
        	 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33 // session Id    
           };
           
           try{
               ServerHello serverHello = null;
               
               //Test case : VALID SESSION ID 
               try{
                   serverHello = new ServerHello();
                   serverHello.setSessionId(VALID_SESSION_ID);
                   assertEquals(true,Arrays.equals(VALID_SESSION_ID,serverHello.getSessionId()));
               }catch(Exception e){
                   e.printStackTrace();
                   fail("testSetGetSessionId failed, reason: " + e.getMessage());                    
               }
               
               //Test Case : INVALID SERVER RANDOM
               try{
                   serverHello = new ServerHello();
                   serverHello.setServerRandom(INVALID_SESSION_ID);                   
                   fail("ServerHello.setSessionId(byte[]) is not throwing "
                           + "IllegalArgumentException if the paramater has invalid session id length");     
               }catch(IllegalArgumentException iae){                   
               }
               
               //Test Case : null
               try{
                   serverHello = new ServerHello();
                   serverHello.setServerRandom(null);                   
                   fail("ServerHello.setSessionId(byte[]) is not throwing "
                           + "IllegalArgumentException if the paramater is null");                       
               }catch(IllegalArgumentException iae){                   
               }
               
           }catch(Exception e){
               e.printStackTrace();
               fail("testSetGetSessionId failed, reason: " + e.getMessage());   
           }
           
    }

    /***
     * This function will test the setCiphersuite(int) and getCiphersuite()
     * Test Cases:
     * 1. Valid Cihpersuite code
     * 2. Invalid Ciphersuite code
     */
    public void testSetGetCiphersuite(){
    	try{
    		final int VALID_CIPHERSUITE_CODE = 10;
    		final int INVALID_CIPHERSUITE_CODE = 65536;
    		ServerHello serverHello = new ServerHello();
    		//Test Case : Valid Cihpersuite code
    		try{
    			serverHello.setCiphersuite(VALID_CIPHERSUITE_CODE);
    			assertEquals(VALID_CIPHERSUITE_CODE,serverHello.getCiphersuite());
    		}catch(IllegalArgumentException iae){
    			iae.printStackTrace();
                fail("testSetGetCiphersuite failed, reason: " + iae.getMessage());
    		}
    		
    		//Test Case : Invalid Ciphersuite code
    		try{
    			serverHello.setCiphersuite(INVALID_CIPHERSUITE_CODE);
    			 fail("ServerHello.setCiphersuite(int) is not throwing "
                         + "IllegalArgumentException if the paramater has invalid Ciphersuite Code.");
    		}catch(IllegalArgumentException iae){    			
    		}

    	}catch(Exception e){
    		e.printStackTrace();
            fail("testSetGetCiphersuite failed, reason: " + e.getMessage());
    	}
    }
    
    /***
     * This function will test the setCiphersuite(int) and getCiphersuite()
     * Test Cases:
     * 1. Valid Compression Method code
     * 2. Compression Method Max code
     * 3. Compression Method Min code
     * 4. Invalid Compression Method code
     */
    public void testSetGetCompressionMethod(){
    	try{
    		final int VALID_COMPRESSION_METHOD_CODE = 10;
    		final int MAX_COMPRESSION_METHOD_CODE = 255;
    		final int MIN_COMPRESSION_METHOD_CODE = 0;
    		final int INVALID_COMPRESSION_METHOD_CODE = 256;
    		
    		ServerHello serverHello = new ServerHello();
            //Test Case : Valid Compression Method code 
    		
            try{                
                serverHello.setCompressionMethod(VALID_COMPRESSION_METHOD_CODE);
                assertEquals(VALID_COMPRESSION_METHOD_CODE,serverHello.getCompressionMethod());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetCompressionMethod failed, reason: " + e.getMessage());
            }
            
            //Test Case : Compression Method Max code
            try{
                serverHello.setCompressionMethod(MAX_COMPRESSION_METHOD_CODE);
                assertEquals(MAX_COMPRESSION_METHOD_CODE,serverHello.getCompressionMethod());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetCompressionMethod failed, reason: " + e.getMessage());
            }

            //Test Case : Compression Method Min code
            try{                
                serverHello.setCompressionMethod(MIN_COMPRESSION_METHOD_CODE);
                assertEquals(MIN_COMPRESSION_METHOD_CODE,serverHello.getCompressionMethod());
            }catch(Exception e){
                e.printStackTrace();
                fail("testSetGetCompressionMethod failed, reason: " + e.getMessage());
            }

            //Test Case : Invalid Compression Method code
            try{                
                serverHello.setCompressionMethod(INVALID_COMPRESSION_METHOD_CODE);
                fail("ServerHello.setCompressionMethod(int) is not throwing "
                        + "IllegalArgumentException if the paramater has invalid Compression Method Code");     
            }catch(IllegalArgumentException iae){                
            }

    	}catch(Exception e){
    		e.printStackTrace();
            fail("testSetGetCompressionMethod failed, reason: " + e.getMessage());
    	}
    }
    
    /***
     * This function will test the toBytes() function
     * Test Cases:
     * 1. VALID INPUT
     * 2. NOTHING WILL BE SET 
     */
    public void testToBytes(){
    	try{
            final byte[] VALID_SERVER_RANDOM = {
                    47,97,45,65, //unix time
                    1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28 // random number
                  };
            final byte[] VALID_SESSIONID = {
            		1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32 //sessiod id	
            };
            final int VALID_SESSIONID_LENGTH = 32,VALID_CIPHERSUITE_CODE = 10,VALID_COMPRESSION_METHOD_CODE = 0;                       

            ServerHello serverHello = null;
            //Test Case : VALID INPUT
            try{
            	serverHello = new ServerHello();
            	final byte[] EXPECTED_BYTE_ARRAY = {
            			3,1, //protocol version
            			47,97,45,65,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28, //sever random
            			32, // session id length
            			1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32, //sessiod id
            			0,10, //Ciphersuite Code
            			0 // Compression Method code
            	};
            	serverHello.setProtocolVersion(ProtocolVersion.TLS1_0);
            	serverHello.setServerRandom(VALID_SERVER_RANDOM);
            	serverHello.setSessionIdLength(VALID_SESSIONID_LENGTH);
            	serverHello.setSessionId(VALID_SESSIONID);
            	serverHello.setCiphersuite(VALID_CIPHERSUITE_CODE);
            	serverHello.setCompressionMethod(VALID_COMPRESSION_METHOD_CODE);
            	assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,serverHello.getBytes()));
            }catch(Exception e){
        		e.printStackTrace();
                fail("testToBytes failed, reason: " + e.getMessage());            	
            }
            
//            //Test Case : NULL
//            try{
//            	serverHello = new ServerHello();
//            	final byte[] EXPECTED_BYTE_ARRAY = {
//            			0,0, //protocol version    
//            			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, //sever random
//            			0, // session id length            			
//            			0,0, //Ciphersuite Code
//            			0 // Compression Method code
//            	};
//            	assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,serverHello.getBytes()));
//            }catch(Exception e){
//        		e.printStackTrace();
//                fail("testToBytes failed, reason: " + e.getMessage());           	            	
//            }
            
          //Test Case : NULL
            try{
            	serverHello = new ServerHello();
            	final byte[] EXPECTED_BYTE_ARRAY = {
            			3,1, //protocol version    
            			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, //sever random
            			0, // session id length            			
            			0,0, //Ciphersuite Code
            			0 // Compression Method code
            	};
            	assertEquals(true,Arrays.equals(EXPECTED_BYTE_ARRAY,serverHello.getBytes()));
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
        suite.addTest(new ServerHelloTest("testSetGetProtocolVersion"));
        suite.addTest(new ServerHelloTest("testSetGetServerRandom"));
        suite.addTest(new ServerHelloTest("testSetGetSessionIdLength"));
        suite.addTest(new ServerHelloTest("testSetGetSessionId"));
        suite.addTest(new ServerHelloTest("testSetGetCiphersuite"));
        suite.addTest(new ServerHelloTest("testSetGetCompressionMethod"));
        suite.addTest(new ServerHelloTest("testToBytes"));
        return suite;
    }
}
