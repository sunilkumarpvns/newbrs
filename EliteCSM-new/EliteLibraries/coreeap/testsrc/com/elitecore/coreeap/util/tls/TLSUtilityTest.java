package com.elitecore.coreeap.util.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.tls.TLSUtility;

@RunWith(JUnitParamsRunner.class)
public class TLSUtilityTest {

	private static final int SERVER_RANDOM_SIZE_IN_BYTE = 32;

	/***
	 * This Function will test the HMAC(String hashFunction, byte[] dataToBeEncrypted,byte[] sharedSecret) function
	 * Test Cases : [ According to RFC-2202 ] 
	 * [Test Cases for HMAC-MD5]
	 * 1. Test Case 1
	 * 2. Test Case 2
	 * 3. Test Case 3
	 * 4. Test Case 4
	 * 5. Test Case 5
	 * 6. Test Case 6
	 * 7. Test Case 7
	 * [Test Cases for HMAC-SHA-1]
	 * 8. Test Case 1
	 * 9. Test Case 2
	 * 10. Test Case 3
	 * 11. Test Case 4
	 * 12. Test Case 5
	 * 13. Test Case 6
	 * 14. Test Case 7
	 */
	@Test
	@Parameters(method = "dataFor_testHMAC")
	public void testHMAC(String caseName, String hash, byte[] input, byte[] sharedSecret, byte[] expectedOutput){
		assertTrue(caseName + " fails", Arrays.equals(expectedOutput, TLSUtility.HMAC(hash, input, sharedSecret)));
	}
	
	public static Object[][] dataFor_testHMAC(){
		final String MD5 = "MD5";
		final String SHA1 = "SHA-1";
		final byte[] MD5_SHARED_SECRET_CASE_1 = {
				11,11,11,11,
				11,11,11,11,
				11,11,11,11,
				11,11,11,11
		};
		final byte[] SHA1_SHARED_SECRET_CASE_1 = {
				11,11,11,11,
				11,11,11,11,
				11,11,11,11,
				11,11,11,11,
				11,11,11,11
		};
		final byte[] SHARED_SECRET_CASE_2 = {
				74,101,102,101 // "Jefe"	
		};
		final byte[] MD5_SHARED_SECRET_CASE_3 = {
				(byte)170,(byte)170,(byte)170,(byte)170,	
				(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170
		};
		final byte[] SHA1_SHARED_SECRET_CASE_3 = {
				(byte)170,(byte)170,(byte)170,(byte)170,	
				(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170
		};
		final byte[] SHARED_SECRET_CASE_4 = {
				1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25
		};
		final byte[] MD5_SHARED_SECRET_CASE_5 = {
				12,12,12,12,
				12,12,12,12,
				12,12,12,12,
				12,12,12,12
		};
		final byte[] SHA1_SHARED_SECRET_CASE_5 = {
				12,12,12,12,
				12,12,12,12,
				12,12,12,12,
				12,12,12,12,
				12,12,12,12
		};

		final byte[] SHARED_SECRET_CASE_6_7 = {
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,
				(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170,(byte)170
		};
		
		return new Object[][]{
				{"Test Case - 1 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					"Hi There".getBytes(), MD5_SHARED_SECRET_CASE_1, 
					new byte[]{
							//0x92 94 72 7a 36 38 bb 1c 13 f4 8e f8 15 8b fc 9d
							(byte)146,(byte)148,(byte)114,(byte)122,(byte)54,(byte)56,(byte)187,(byte)28,
							(byte)19,(byte)244,(byte)142,(byte)248,(byte)21,(byte)139,(byte)252,(byte)157	
					}
				},
				{"Test Case - 2 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					"what do ya want for nothing?".getBytes(), SHARED_SECRET_CASE_2, 
					new byte[]{
							//0x75 0c 78 3e 6a b0 b5 03 ea a8 6e 31 0a 5d b7 38
							(byte)117,(byte)12,(byte)120,(byte)62,(byte)106,(byte)176,(byte)181,(byte)3,
							(byte)234,(byte)168,(byte)110,(byte)49,(byte)10,(byte)93,(byte)183,(byte)56						
					}
				},
				{"Test Case - 3 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					new byte[]{
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221						
						}, MD5_SHARED_SECRET_CASE_3,
						new byte[]{
								//0x56 be 34 52 1d 14 4c 88 db b8 c7 33 f0 e8 b3 f6
								(byte)86,(byte)190,(byte)52,(byte)82,(byte)29,(byte)20,(byte)76,(byte)136,
								(byte)219,(byte)184,(byte)199,(byte)51,(byte)240,(byte)232,(byte)179,(byte)246
						}
				},
				{"Test Case - 4 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					new byte[]{
						(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
						(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
						(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
						(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
						(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205
					}, SHARED_SECRET_CASE_4, 
					new byte[]{
							//0x69 7e af 0a ca 3a 3a ea 3a 75 16 47 46 ff aa 79
							(byte)105,(byte)126,(byte)175,(byte)10,(byte)202,(byte)58,(byte)58,(byte)234,
							(byte)58,(byte)117,(byte)22,(byte)71,(byte)70,(byte)255,(byte)170,(byte)121
					}
				},
				{"Test Case - 5 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					"Test With Truncation".getBytes(), MD5_SHARED_SECRET_CASE_5, 
					new byte[]{
						//0x56 46 1e f2 34 2e dc 00 f9 ba b9 95 69 0e fd 4c
						(byte)86,(byte)70,(byte)30,(byte)242,(byte)52,(byte)46,(byte)220,(byte)0,
						(byte)249,(byte)186,(byte)185,(byte)149,(byte)105,(byte)14,(byte)253,(byte)76
					}
				},
				{"Test Case - 6 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					"Test Using Larger Than Block-Size Key - Hash Key First".getBytes(), SHARED_SECRET_CASE_6_7,
					new byte[]{
							//0x6b 1a b7 fe 4b d7 bf 8f 0b 62 e6 ce 61 b9 d0 cd						
							(byte)107,(byte)26,(byte)183,(byte)254,(byte)75,(byte)215,(byte)191,(byte)143,
							(byte)11,(byte)98,(byte)230,(byte)206,(byte)97,(byte)185,(byte)208,(byte)205						
					}
				},
				{"Test Case - 7 [RFC-2202][Test Cases for HMAC-MD5]", MD5, 
					"Test Using Larger Than Block-Size Key and Larger Than One Block-Size Data".getBytes(), SHARED_SECRET_CASE_6_7,
					new byte[]{
							//0x6f 63 0f ad 67 cd a0 ee 1f b1 f5 62 db 3a a5 3e
							(byte)111,(byte)99,(byte)15,(byte)173,(byte)103,(byte)205,(byte)160,(byte)238,
							(byte)31,(byte)177,(byte)245,(byte)98,(byte)219,(byte)58,(byte)165,(byte)62												
					}
				},
				{"Test Case - 1 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1, 
					"Hi There".getBytes(), SHA1_SHARED_SECRET_CASE_1,
					new byte[]{
						//0xb6 17 31 86 55 05 72 64 e2 8b c0 b6 fb 37 8c 8e f1 46 be 00
						(byte)182,(byte)23,(byte)49,(byte)134,(byte)85,(byte)5,(byte)114,(byte)100,(byte)226,(byte)139,
						(byte)192,(byte)182,(byte)251,(byte)55,(byte)140,(byte)142,(byte)241,(byte)70,(byte)190,(byte)0
					}
				},
				{"Test Case - 2 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1, 
					"what do ya want for nothing?".getBytes(), SHARED_SECRET_CASE_2,
					new byte[]{
							//	0xef fc df 6a e5 eb 2f a2 d2 74 16 d5 f1 84 df 9c 25 9a 7c 79
							(byte)239,(byte)252,(byte)223,(byte)106,(byte)229,(byte)235,(byte)47,(byte)162,(byte)210,(byte)116,
							(byte)22,(byte)213,(byte)241,(byte)132,(byte)223,(byte)156,(byte)37,(byte)154,(byte)124,(byte)121						
					}
				},
				{"Test Case - 3 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1,
					new byte[]{
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,
							(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221,(byte)221						
						}, SHA1_SHARED_SECRET_CASE_3,
					new byte[]{
							//0x12 5d 73 42 b9 ac 11 cd 91 a3 9a f4 8a a1 7b 4f 63 f1 75 d3
							(byte)18,(byte)93,(byte)115,(byte)66,(byte)185,(byte)172,(byte)17,(byte)205,(byte)145,(byte)163,
							(byte)154,(byte)244,(byte)138,(byte)161,(byte)123,(byte)79,(byte)99,(byte)241,(byte)117,(byte)211
					}
				},
				{"Test Case - 4 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1,
					new byte[]{
							(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
							(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
							(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
							(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,
							(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205,(byte)205
						}, SHARED_SECRET_CASE_4,
					new byte[]{
							//0x4c 90 07 f4 02 62 50 c6 bc 84 14 f9 bf 50 c8 6c 2d 72 35 da
							(byte)76,(byte)144,(byte)7,(byte)244,(byte)2,(byte)98,(byte)80,(byte)198,(byte)188,(byte)132,
							(byte)20,(byte)249,(byte)191,(byte)80,(byte)200,(byte)108,(byte)45,(byte)114,(byte)53,(byte)218
					}
					
				},
				{"Test Case - 5 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1, 
					"Test With Truncation".getBytes(), SHA1_SHARED_SECRET_CASE_5,
					new byte[]{
							//0x4c 1a 03 42 4b 55 e0 7f e7 f2 7b e1 d5 8b b9 32 4a 9a 5a 04
							(byte)76,(byte)26,(byte)3,(byte)66,(byte)75,(byte)85,(byte)224,(byte)127,(byte)231,(byte)242,
							(byte)123,(byte)225,(byte)213,(byte)139,(byte)185,(byte)50,(byte)74,(byte)154,(byte)90,(byte)4
					}
				},
				{"Test Case - 6 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1, 
					"Test Using Larger Than Block-Size Key - Hash Key First".getBytes(), SHARED_SECRET_CASE_6_7,
					new byte[]{
							//0xaa 4a e5 e1 52 72 d0 0e 95 70 56 37 ce 8a 3b 55 ed 40 21 12		
							(byte)170,(byte)74,(byte)229,(byte)225,(byte)82,(byte)114,(byte)208,(byte)14,(byte)149,(byte)112,
							(byte)86,(byte)55,(byte)206,(byte)138,(byte)59,(byte)85,(byte)237,(byte)64,(byte)33,(byte)18
					}
				},
				{"Test Case - 7 [RFC-2202][Test Cases for HMAC-SHA-1]", SHA1, 
					"Test Using Larger Than Block-Size Key and Larger Than One Block-Size Data".getBytes(), SHARED_SECRET_CASE_6_7,
					new byte[]{
							//0xe8 e9 9d 0f 45 23 7d 78 6d 6b ba a7 96 5c 78 08 bb ff 1a 91
							(byte)232,(byte)233,(byte)157,(byte)15,(byte)69,(byte)35,(byte)125,(byte)120,(byte)109,(byte)107,
							(byte)186,(byte)167,(byte)150,(byte)92,(byte)120,(byte)8,(byte)187,(byte)255,(byte)26,(byte)145
					}
				}
		};
	}

	/***
	 * This function will test the generateServerRandom() function
	 * Test Cases : 
	 * 1. Check Null
	 * 2. Check Valid Length
	 */
	@Test
	public void testGenerateServerRandom(){
		try{
			byte[] serverRandom = TLSUtility.generateSecureRandom(SERVER_RANDOM_SIZE_IN_BYTE);

			//Test Case : Check Null
			assertNotNull(serverRandom);

			//Test Case : Check Valid Length
			assertEquals(32,serverRandom.length);
		}catch(Exception e){
			fail("testGenerateServerRandom failed, reason : " + e.getMessage());
		}
	}

	/***
	 * This function will test the generateSessionIdentifier() function
	 * Test Cases :
	 * 1. Check Null
	 * 2. Check Valid Length
	 */
	@Test
	public void testGenerateSessionIdentifier(){
		try{
			byte[] sessionIdentifier = TLSUtility.generateSessionIdentifier();

			//Test Case : check Null
			assertNotNull(sessionIdentifier);

			//Test Case : Check Valid Length
			assertEquals(32,sessionIdentifier.length);			
		}catch(Exception e){
			fail("testGenerateSessionIdentifier failed, reason : " + e.getMessage());
		}
	}

	/***
	 * This function will test the appendBytes() function
	 * Test Cases : 
	 * 1. Valid Input
	 * 2. Old Array byte is null
	 * 3. New Array byte is null
	 */
	@Test
	public void testAppendBytes(){
		try{
			//Test Case : Valid Input
			try{
				byte[] OLD_ARRAY_BYTE = {1,2,3,4,5};
				byte[] NEW_ARRAY_BYTE = {6,7,8,9,10};
				byte[] EXPECTED_BYTE_ARRAY= {1,2,3,4,5,6,7,8,9,10};
				byte[] output = TLSUtility.appendBytes(OLD_ARRAY_BYTE,NEW_ARRAY_BYTE);
				assertTrue(Arrays.equals(EXPECTED_BYTE_ARRAY,output));
			}catch(Exception e){
				fail("testAppendBytes failed, reason : " + e.getMessage());
			}
			//Test Case : Old Array byte is null
			try{
				byte[] OLD_ARRAY_BYTE = null;
				byte[] NEW_ARRAY_BYTE = {1,2,3,4,5};
				byte[] EXPECTED_BYTE_ARRAY= {1,2,3,4,5};
				byte[] output = TLSUtility.appendBytes(OLD_ARRAY_BYTE,NEW_ARRAY_BYTE);
				assertTrue(Arrays.equals(EXPECTED_BYTE_ARRAY,output));				
			}catch(Exception e){
				fail("testAppendBytes failed, reason : " + e.getMessage());
			}

			//Test Case : New Array byte is null
			try{
				byte[] OLD_ARRAY_BYTE = {1,2,3,4,5};
				byte[] NEW_ARRAY_BYTE = null;
				byte[] EXPECTED_BYTE_ARRAY= {1,2,3,4,5};
				byte[] output = TLSUtility.appendBytes(OLD_ARRAY_BYTE,NEW_ARRAY_BYTE);
				assertTrue(Arrays.equals(EXPECTED_BYTE_ARRAY,output));

			}catch(Exception e){
				fail("testAppendBytes failed, reason : " + e.getMessage());
			}
		}catch(Exception e){
			fail("testAppendBytes failed, reason : " + e.getMessage());
		}
	}

	/***
	 * This function will test TLSPRF() function
	 * Test Cases : 
	 * 1. Valid Input 
	 */
	@Test
	public void testTLSPRF(){
		try{
			//Test Case : Valid Input
			//For TLSv1.0 & TLSv1.1
			try{
				final byte[] SECRET = {
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,
						(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB
				};
				final String LABEL = "PRF Testvector";
				final byte[] SEED = {
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
						(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,
				};
				final int LENGTH = 104;
				final byte[] EXPECTED_OUTPUT = {
						(byte)0xD3,(byte)0xD4,(byte)0xD1,(byte)0xE3,(byte)0x49,(byte)0xB5,(byte)0xD5,(byte)0x15,
						(byte)0x04,(byte)0x46,(byte)0x66,(byte)0xD5,(byte)0x1D,(byte)0xE3,(byte)0x2B,(byte)0xAB,
						(byte)0x25,(byte)0x8C,(byte)0xB5,(byte)0x21,(byte)0xB6,(byte)0xB0,(byte)0x53,(byte)0x46,
						(byte)0x3E,(byte)0x35,(byte)0x48,(byte)0x32,(byte)0xFD,(byte)0x97,(byte)0x67,(byte)0x54,
						(byte)0x44,(byte)0x3B,(byte)0xCF,(byte)0x9A,(byte)0x29,(byte)0x65,(byte)0x19,(byte)0xBC,
						(byte)0x28,(byte)0x9A,(byte)0xBC,(byte)0xBC,(byte)0x11,(byte)0x87,(byte)0xE4,(byte)0xEB,
						(byte)0xD3,(byte)0x1E,(byte)0x60,(byte)0x23,(byte)0x53,(byte)0x77,(byte)0x6C,(byte)0x40,
						(byte)0x8A,(byte)0xAF,(byte)0xB7,(byte)0x4C,(byte)0xBC,(byte)0x85,(byte)0xEF,(byte)0xF6,
						(byte)0x92,(byte)0x55,(byte)0xF9,(byte)0x78,(byte)0x8F,(byte)0xAA,(byte)0x18,(byte)0x4C,
						(byte)0xBB,(byte)0x95,(byte)0x7A,(byte)0x98,(byte)0x19,(byte)0xD8,(byte)0x4A,(byte)0x5D,
						(byte)0x7E,(byte)0xB0,(byte)0x06,(byte)0xEB,(byte)0x45,(byte)0x9D,(byte)0x3A,(byte)0xE8,
						(byte)0xDE,(byte)0x98,(byte)0x10,(byte)0x45,(byte)0x4B,(byte)0x8B,(byte)0x2D,(byte)0x8F,
						(byte)0x1A,(byte)0xFB,(byte)0xC6,(byte)0x55,(byte)0xA8,(byte)0xC9,(byte)0xA0,(byte)0x13
				};

				TLSSecurityParameters tlsSecurityParameters = new TLSSecurityParameters();
				tlsSecurityParameters.setProtocolVersion(ProtocolVersion.TLS1_0);

				byte[] output = TLSUtility.TLSPRF(SECRET,LABEL.getBytes(),SEED,LENGTH,new TLSSecurityParameters());									
				assertTrue(Arrays.equals(EXPECTED_OUTPUT,output));
			}catch(Exception e){
				fail("testTLSPRF failed, reason : "+ e.getMessage());
			}

			//Test Case : Valid Input
			//For for TLSv1.2
			try{
				byte[] SECRET = new byte[16];
				SECRET = TLSUtility.HexToBytes("0x9bbe436ba940f017b17652849a71db35");
				final String LABEL = "test label";
				byte[] SEED = new byte[16];
				SEED = TLSUtility.HexToBytes("0xa0ba9f936cda311827a6f796ffd5198c");
				final int LENGTH = 100;
				TLSSecurityParameters tlsSecurityParameters = new TLSSecurityParameters();
				tlsSecurityParameters.setProtocolVersion(ProtocolVersion.TLS1_2);
				tlsSecurityParameters.setPRFAlgorithm("SHA-256");

				byte[] EXPECTED_OUTPUT = new byte[100];
				EXPECTED_OUTPUT = TLSUtility.HexToBytes("0xe3f229ba727be17b8d122620557cd453c2aab21d07c3d495329b52d4e61edb5a6b301791e90d35c9c9a46b4e14baf9af0fa022f7077def17abfd3797c0564bab4fbc91666e9def9b97fce34f796789baa48082d122ee42c5a72e5a5110fff70187347b66");

				//				byte[] output = TLSUtility.TLSPRF(SECRET,LABEL.getBytes(),SEED,LENGTH, tlsSecurityParameters);
				byte[] output = TLSUtility.P_HASH(SECRET, LABEL.getBytes(), SEED, LENGTH, tlsSecurityParameters);
				assertTrue(Arrays.equals(EXPECTED_OUTPUT,output));
			}catch(Exception e){
				fail("testTLSPRF failed, reason : "+ e.getMessage());
			}
		}catch(Exception e){
			fail("testTLSPRF failed, reason : "+ e.getMessage());
		}
	}

	/***
	 * This function will test the MD5() function
	 * Test Cases : 
	 * 1. Test Case 1- [RFC-1321][Appendix A.5]
	 * 2. Test Case 2- [RFC-1321][Appendix A.5]
	 * 3. Test Case 3- [RFC-1321][Appendix A.5]
	 * 4. Test Case 4- [RFC-1321][Appendix A.5]
	 * 5. Test Case 5- [RFC-1321][Appendix A.5]
	 * 6. Test Case 6- [RFC-1321][Appendix A.5]
	 * 7. Test Case 7- [RFC-1321][Appendix A.5]
	 */
	@Test
	public void testMD5(){
		final byte[] SERVER_RANDOM = null;
		final byte[] SERVER_PARAMS = null;
		try{
			//Test Case : Test Case 1- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0xd4,(byte)0x1d,(byte)0x8c,(byte)0xd9,(byte)0x8f,(byte)0x00,(byte)0xb2,(byte)0x04,
						(byte)0xe9,(byte)0x80,(byte)0x09,(byte)0x98,(byte)0xec,(byte)0xf8,(byte)0x42,(byte)0x7e	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));
			}catch(Exception e){
				fail("testMD5 failed, reason :" + e.getMessage());
			}
			//Test Case : Test Case 2- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "a";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0x0c,(byte)0xc1,(byte)0x75,(byte)0xb9,(byte)0xc0,(byte)0xf1,(byte)0xb6,(byte)0xa8,
						(byte)0x31,(byte)0xc3,(byte)0x99,(byte)0xe2,(byte)0x69,(byte)0x77,(byte)0x26,(byte)0x61	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){
				fail("testMD5 failed, reason :" + e.getMessage());
			}

			//Test Case : Test Case 3- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "abc";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0x90,(byte)0x01,(byte)0x50,(byte)0x98,(byte)0x3c,(byte)0xd2,(byte)0x4f,(byte)0xb0,
						(byte)0xd6,(byte)0x96,(byte)0x3f,(byte)0x7d,(byte)0x28,(byte)0xe1,(byte)0x7f,(byte)0x72	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){
				fail("testMD5 failed, reason :" + e.getMessage());
			}

			//Test Case : Test Case 4- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "message digest";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0xf9,(byte)0x6b,(byte)0x69,(byte)0x7d,(byte)0x7c,(byte)0xb7,(byte)0x93,(byte)0x8d,
						(byte)0x52,(byte)0x5a,(byte)0x2f,(byte)0x31,(byte)0xaa,(byte)0xf1,(byte)0x61,(byte)0xd0	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){
				fail("testMD5 failed, reason :" + e.getMessage());
			}

			//Test Case : Test Case 5- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "abcdefghijklmnopqrstuvwxyz";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0xc3,(byte)0xfc,(byte)0xd3,(byte)0xd7,(byte)0x61,(byte)0x92,(byte)0xe4,(byte)0x00,
						(byte)0x7d,(byte)0xfb,(byte)0x49,(byte)0x6c,(byte)0xca,(byte)0x67,(byte)0xe1,(byte)0x3b	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){				
				fail("testMD5 failed, reason :" + e.getMessage());
			}

			//Test Case : Test Case 6- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0xd1,(byte)0x74,(byte)0xab,(byte)0x98,(byte)0xd2,(byte)0x77,(byte)0xd9,(byte)0xf5,
						(byte)0xa5,(byte)0x61,(byte)0x1c,(byte)0x2c,(byte)0x9f,(byte)0x41,(byte)0x9d,(byte)0x9f	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){				
				fail("testMD5 failed, reason :" + e.getMessage());
			}

			//Test Case : Test Case 7- [RFC-1321][Appendix A.5]
			try{
				final String CLIENT_RANDOM = "12345678901234567890123456789012345678901234567890123456789012345678901234567890";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0x57,(byte)0xed,(byte)0xf4,(byte)0xa2,(byte)0x2b,(byte)0xe3,(byte)0xc9,(byte)0x55,
						(byte)0xac,(byte)0x49,(byte)0xda,(byte)0x2e,(byte)0x21,(byte)0x07,(byte)0xb6,(byte)0x7a
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.MD5);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){				
				fail("testMD5 failed, reason :" + e.getMessage());
			}

		}catch(Exception e){			
			fail("testMD5 failed, reason : " + e.getMessage());
		}
	}

	/***
	 * This function will test the SHA() function
	 * Test Cases : 
	 * 1. Test Case 1 - [RFC-3714][Section 7.3-Test Driver]
	 * 2. Test Case 2 - [RFC-3714][Section 7.3-Test Driver]
	 */
	@Test
	public void testSHA(){
		final byte[] SERVER_RANDOM = null;
		final byte[] SERVER_PARAMS = null;
		try{
			//Test Case : Test Case 1 - [RFC-3714][Section 7.3-Test Driver]
			try{
				final String CLIENT_RANDOM = "abc";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0xA9,(byte)0x99,(byte)0x3E,(byte)0x36,(byte)0x47,(byte)0x06,(byte)0x81,(byte)0x6A,
						(byte)0xBA,(byte)0x3E,(byte)0x25,(byte)0x71,(byte)0x78,(byte)0x50,(byte)0xC2,(byte)0x6C,
						(byte)0x9C,(byte)0xD0,(byte)0xD8,(byte)0x9D
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.SHA1);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));
			}catch(Exception e){

				fail("testSHA failed, reason : " + e.getMessage());
			}
			//Test Case : Test Case 2 - [RFC-3714][Section 7.3-Test Driver]
			try{
				final String CLIENT_RANDOM = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
				final byte[] EXPECTED_DIGEST_OUTPUT = {
						(byte)0x84,(byte)0x98,(byte)0x3E,(byte)0x44,(byte)0x1C,(byte)0x3B,(byte)0xD2,(byte)0x6E,
						(byte)0xBA,(byte)0xAE,(byte)0x4A,(byte)0xA1,(byte)0xF9,(byte)0x51,(byte)0x29,(byte)0xE5,
						(byte)0xE5,(byte)0x46,(byte)0x70,(byte)0xF1	
				};
				byte[] digest = TLSUtility.doHash(CLIENT_RANDOM.getBytes(), SERVER_RANDOM, SERVER_PARAMS, HashAlgorithm.SHA1);
				assertTrue(Arrays.equals(EXPECTED_DIGEST_OUTPUT,digest));

			}catch(Exception e){				
				fail("testSHA failed, reason : " + e.getMessage());
			}


		}catch(Exception e){			
			fail("testSHA failed, reason : " + e.getMessage());
		}
	}

	/***
	 * This function will test the byteToHex(byte[]) function
	 * Test Cases :
	 * 1. Valid Input
	 * 2. null
	 */
	@Test
	public void testByteToHex(){
		try{
			//Test Case : Valid Input
			try{
				final byte[] VALID_INPUT= {10,11,12,13,14};
				final String EXPECTED_OUTPUT_STRING = "0x0a0b0c0d0e";
				String output = TLSUtility.bytesToHex(VALID_INPUT);
				assertTrue(output.equals(EXPECTED_OUTPUT_STRING));
			}catch(Exception e){

				fail("testByteToHex failed, reason : " + e.getMessage());
			}
			//Test Case : null
			try{
				final byte[] VALID_INPUT= null;
				final String EXPECTED_OUTPUT_STRING = "null";
				String output = TLSUtility.bytesToHex(VALID_INPUT);
				assertTrue(output.equals(EXPECTED_OUTPUT_STRING));				
			}catch(Exception e){				
				fail("testByteToHex failed, reason : " + e.getMessage());
			}

		}catch(Exception e){			
			fail("testByteToHex failed, reason : " + e.getMessage());
		}
	}
	
	@Test
	@Parameters(method = "dataFor_testMatches")
	public void testMatches(boolean expectedOutput, String sourceString, String pattern){
		assertEquals("Unable to match  " + "\"" + sourceString + "\"" + " with pattern \"" + pattern + "\"", expectedOutput, TLSUtility.matches(sourceString, pattern));
	}
	
	public static Object[][] dataFor_testMatches(){
		
		return new Object[][]{
				{true, "", ""},
				{false, "", "*c*e"},
				{false, "a\\*", ""},
				
				{false, "a\\*", "*c*e"},
				{false, "\\*c", "*c*e"},
				{false, "c\\*", "*c*e"},
				{false, "\\*e", "*c*e"},
				{false, "a\\*c", "*c*e"},
				{false, "\\*c\\*", "*c*e"},
				{true, "c\\*e", "*c*e"},
				{false, "a\\*e", "*c*e"},
				{false, "\\*a\\*", "*c*e"},
				{false, "a\\*c*", "*c*e"},
				{true, "\\*c\\*e", "*c*e"},
				{true, "a\\*c\\*e", "*c*e"},
				
				{true, "a\\*", "a*"},
				{false, "\\*c", "a*"},
				{false, "c\\*", "a*"},
				{false, "\\*e", "a*"},
				{true, "a\\*c", "a*"},
				{false, "\\*c\\*", "a*"},
				{false, "c\\*e", "a*"},
				{true, "a\\*e", "a*"},
				{false, "\\*a\\*", "a*"},
				{true, "a\\*c*", "a*"},
				{false, "\\*c\\*e", "a*"},
				{true, "a\\*c\\*e", "a*"},
				
				{false, "a\\*", "a*c"},
				{false, "\\*c", "a*c"},
				{false, "c\\*", "a*c"},
				{false, "\\*e", "a*c"},
				{true, "a\\*c", "a*c"},
				{false, "\\*c\\*", "a*c"},
				{false, "c\\*e", "a*c"},
				{false, "a\\*e", "a*c"},
				{false, "\\*a\\*", "a*c"},
				{false, "a\\*c*", "a*c"},
				{false, "\\*c\\*e", "a*c"},
				{false, "a\\*c\\*e", "a*c"},
				
				{false, "a\\*", "*c"},
				{true, "\\*c", "*c"},
				{false, "c\\*", "*c"},
				{false, "\\*e", "*c"},
				{true, "a\\*c", "*c"},
				{false, "\\*c\\*", "*c"},
				{false, "c\\*e", "*c"},
				{false, "a\\*e", "*c"},
				{false, "\\*a\\*", "*c"},
				{false, "a\\*c*", "*c"},
				{false, "\\*c\\*e", "*c"},
				{false, "a\\*c\\*e", "*c"},
				
				{false, "a\\*", "*c*"},
				{true, "\\*c", "*c*"},
				{true, "c\\*", "*c*"},
				{false, "\\*e", "*c*"},
				{true, "a\\*c", "*c*"},
				{true, "\\*c\\*", "*c*"},
				{true, "c\\*e", "*c*"},
				{false, "a\\*e", "*c*"},
				{false, "\\*a\\*", "*c*"},
				{true, "a\\*c*", "*c*"},
				{true, "\\*c\\*e", "*c*"},
				{true, "a\\*c\\*e", "*c*"},
				
				{false, "a\\*", "c*"},
				{false, "\\*c", "c*"},
				{true, "c\\*", "c*"},
				{false, "\\*e", "c*"},
				{false, "a\\*c", "c*"},
				{false, "\\*c\\*", "c*"},
				{true, "c\\*e", "c*"},
				{false, "a\\*e", "c*"},
				{false, "\\*a\\*", "c*"},
				{false, "a\\*c*", "c*"},
				{false, "\\*c\\*e", "c*"},
				{false, "a\\*c\\*e", "c*"},
				
				{false, "a\\*", "c*e"},
				{false, "\\*c", "c*e"},
				{false, "c\\*", "c*e"},
				{false, "\\*e", "c*e"},
				{false, "a\\*c", "c*e"},
				{false, "\\*c\\*", "c*e"},
				{true, "c\\*e", "c*e"},
				{false, "a\\*e", "c*e"},
				{false, "\\*a\\*", "c*e"},
				{false, "a\\*c*", "c*e"},
				{false, "\\*c\\*e", "c*e"},
				{false, "a\\*c\\*e", "c*e"},
				
				{false, "a\\*", "*e"},
				{false, "\\*c", "*e"},
				{false, "c\\*", "*e"},
				{true, "\\*e", "*e"},
				{false, "a\\*c", "*e"},
				{false, "\\*c\\*", "*e"},
				{true, "c\\*e", "*e"},
				{true, "a\\*e", "*e"},
				{false, "\\*a\\*", "*e"},
				{false, "a\\*c*", "*e"},
				{true, "\\*c\\*e", "*e"},
				{true, "a\\*c\\*e", "*e"},
				
				{false, "a\\*", "a*e"},
				{false, "\\*c", "a*e"},
				{false, "c\\*", "a*e"},
				{false, "\\*e", "a*e"},
				{false, "a\\*c", "a*e"},
				{false, "\\*c\\*", "a*e"},
				{false, "c\\*e", "a*e"},
				{true, "a\\*e", "a*e"},
				{false, "\\*a\\*", "a*e"},
				{false, "a\\*c*", "a*e"},
				{false, "\\*c\\*e", "a*e"},
				{true, "a\\*c\\*e", "a*e"},
				
				{true, "a\\*", "*a*"},
				{false, "\\*c", "*a*"},
				{false, "c\\*", "*a*"},
				{false, "\\*e", "*a*"},
				{true, "a\\*c", "*a*"},
				{false, "\\*c\\*", "*a*"},
				{false, "c\\*e", "*a*"},
				{true, "a\\*e", "*a*"},
				{true, "\\*a\\*", "*a*"},
				{true, "a\\*c*", "*a*"},
				{false, "\\*c\\*e", "*a*"},
				{true, "a\\*c\\*e", "*a*"},
				
				{false, "a\\*", "a*c*"},
				{false, "\\*c", "a*c*"},
				{false, "c\\*", "a*c*"},
				{false, "\\*e", "a*c*"},
				{true, "a\\*c", "a*c*"},
				{false, "\\*c\\*", "a*c*"},
				{false, "c\\*e", "a*c*"},
				{false, "a\\*e", "a*c*"},
				{false, "\\*a\\*", "a*c*"},
				{true, "a\\*c*", "a*c*"},
				{false, "\\*c\\*e", "a*c*"},
				{true, "a\\*c\\*e", "a*c*"},
				
				{false, "a\\*", "*c*e"},
				{false, "\\*c", "*c*e"},
				{false, "c\\*", "*c*e"},
				{false, "\\*e", "*c*e"},
				{false, "a\\*c", "*c*e"},
				{false, "\\*c\\*", "*c*e"},
				{true, "c\\*e", "*c*e"},
				{false, "a\\*e", "*c*e"},
				{false, "\\*a\\*", "*c*e"},
				{false, "a\\*c*", "*c*e"},
				{true, "\\*c\\*e", "*c*e"},
				{true, "a\\*c\\*e", "*c*e"},
				
				{false, "a\\*", "a*c*e"},
				{false, "\\*c", "a*c*e"},
				{false, "c\\*", "a*c*e"},
				{false, "\\*e", "a*c*e"},
				{false, "a\\*c", "a*c*e"},
				{false, "\\*c\\*", "a*c*e"},
				{false, "c\\*e", "a*c*e"},
				{false, "a\\*e", "a*c*e"},
				{false, "\\*a\\*", "a*c*e"},
				{false, "a\\*c*", "a*c*e"},
				{false, "\\*c\\*e", "a*c*e"},
				{true, "a\\*c\\*e", "a*c*e"},
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_testConvertToPlainString")
	public void testConvertToPlainString(String input, String expectedOutput){
		assertEquals(expectedOutput, TLSUtility.convertToPlainString(input));
	}
	
	public Object[][] dataFor_testConvertToPlainString(){
		return new Object[][]{
				//checking blank string as input
				{null,""},
				{"" ,""},
				{" " ,""},
				
				//checking all hex digits are supported
				{"1234567890ABCDEF" ,"1234567890abcdef"},
				
				//checking all hex character conversion with input
				{"ABCDEF" ,"abcdef"}, // all char in upper case
				{"abcdef" ,"abcdef"}, // all char in lower case
				{"AbCdEf" ,"abcdef"}, // mixed string
				
				//checking for wild card character *
				{"*0*A*a*" ,"*0*a*a*"},
				{"*0*F*f*" ,"*0*f*f*"},
				{"*9*Z*z*" ,"*9***"},
				
				//checking for other char other than hex char
				{"abcdefghijklmnopqrstuvwxyz" ,"abcdef"},
				{"ABCDEFGHIJKLMNOPQRSTUVWXYZ" ,"abcdef"},
				
				//checking for all the symbols
				{"`~!@#$%^&*()_-=+{}|[]\\:;\'\"<>?,./\t\n","*"}
		};
	}

//	private byte[] PRF(byte secret[], String label, List seed, int length){
//		int loopc0 = ((length + 16) - 1) / 16;
//		int loopc1 = ((length + 20) - 1) / 20;
//		byte r0[] = new byte[loopc0 * 16];
//		byte r1[] = new byte[loopc1 * 20];
//		byte labelBytes[] = stringToLatin1Bytes(label);
//		seed.add(0, labelBytes);
//		Mac md5 = null;
//		Mac sha = null;
//		try {
//			md5 = Mac.getInstance("HmacMD5");
//			sha = Mac.getInstance("HmacSHA1");
//		} catch (Exception e) {
//		}
//		int half = (secret.length + 1) / 2;
//		P_hash(md5, new SecretKeySpec(secret, 0, half, ""), seed, r0);
//		P_hash(sha, new SecretKeySpec(secret, secret.length / 2, half, ""), seed, r1);
//		if(length == r0.length)
//		{
//			for(int n = 0; n < length; n++)
//				r0[n] ^= r1[n];
//
//			return r0;
//		}
//		if(length == r1.length)
//		{
//			for(int n = 0; n < length; n++)
//				r1[n] ^= r0[n];
//
//			return r1;
//		}
//		byte result[] = new byte[length];
//		for(int n = 0; n < length; n++)
//			result[n] = (byte)(r0[n] ^ r1[n]);
//
//		return result;
//	}
//
//	private void P_hash(Mac mac, Key secret, List seed, byte result[]){
//		try {
//			mac.init(secret);
//		} catch (InvalidKeyException e1) {
//		}
//		byte seedElement[];
//		for(Iterator i$ = seed.iterator(); i$.hasNext(); mac.update(seedElement))
//			seedElement = (byte[])i$.next();
//
//		byte An[] = mac.doFinal();
//		int width = mac.getMacLength();
//		for(int offset = 0; offset < result.length; offset += width)
//		{
//			mac.update(An);
//			//byte[] seedElement;
//			for(Iterator i$ = seed.iterator(); i$.hasNext(); mac.update(seedElement))
//				seedElement = (byte[])i$.next();
//
//			try {
//				mac.doFinal(result, offset);
//			} catch (ShortBufferException e) {				
//			} catch (IllegalStateException e) {				
//			}
//			An = mac.doFinal(An);
//		}
//	}

	public byte[] stringToLatin1Bytes(String s)
	{
		if(s == null)
			return null;
		else
			return stringToLatin1Bytes(s, 0, s.length());
	}

	public byte[] stringToLatin1Bytes(String s, int offset, int length)
	{
		byte bytes[] = new byte[length];
		stringToLatin1Bytes(s, offset, length + offset, bytes, 0);
		return bytes;
	}

	public int stringToLatin1Bytes(String s, byte buf[], int offset)
	{
		int length = s.length();
		stringToLatin1Bytes(s, 0, length, buf, offset);
		return length;
	}

	public int stringToLatin1Bytes(String string, int sOffset, int sLimit, byte bytes[], int bOffset)
	{
		for(int i = sOffset; i < sLimit; i++)
		{
			char ch = string.charAt(i);
			if(ch > '\377')
				throw new IllegalArgumentException("my exception");
			bytes[bOffset++] = (byte)ch;
		}

		return sLimit - sOffset;
	}
}
