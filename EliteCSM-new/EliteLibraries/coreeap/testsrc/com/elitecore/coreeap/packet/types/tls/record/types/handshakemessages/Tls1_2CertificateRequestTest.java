package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import org.junit.Test;

import junit.framework.TestCase;

import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateRequest;

public class Tls1_2CertificateRequestTest extends TestCase {
	
	public Tls1_2CertificateRequestTest(String name) {
		super(name);
	}
	
	@Test
	public void testSetBytes(){
		final byte[] VALID_COMPLETE_MESSAGE = {
				3, //Certificate Type Count
				1, 2, 64, // 3 types of certificate respectively RSA sign, DSS sign & ECDSA sign
				0, 24, //Signature Hash Algorithm Length
				6, 3, // Algo 1 - SHA512/ECDSA
				6, 1, // Algo 2 - SHA512/RSA
				5, 3, // Algo 3 - SHA384/ECDSA
				5, 1, // Algo 4 - SHA384/RSA
				4, 3, // Algo 5 - SHA256/ECDSA
				4, 1, // Algo 6 - SHA256/RSA
				3, 3, // Algo 7 - SHA224/ECDSA
				3, 1, // Algo 8 - SHA224/RSA
				2, 3, // Algo 9 - SHA1/ECDSA
				2, 1, // Algo 10 - SHA1/RSA
				2, 2, // Algo 11 - SHA1/DSA
				1, 1, // Algo 12 - MD5/RSA
				0, (byte) 170, // Distinguished Names Length
				0, (byte) 168, // Distinguished Name Length
				48, (byte) 129, (byte) 165, 
				49, 11, 48, 9, 6, 3, 85, 4, 6, 19, 2, 73, 78, 49, 12, 48,
				10, 6, 3, 85, 4, 8, 12, 3, 71, 85, 74, 49, 18, 48, 16, 6,
				3, 85, 4, 7, 12, 9, 65, 72, 77, 69, 68, 65, 66, 65, 68, 49,
				39, 48, 37, 6, 3, 85, 4, 10, 12, 30, 69, 76, 73, 84, 69, 67,
				79, 82, 69, 32, 84, 69, 67, 72, 78, 79, 76, 79, 71, 89, 32, 80,
				86, 84, 46, 32, 76, 84, 68, 46, 49, 12, 48, 10, 6, 3, 85, 4,
				11, 12, 3, 67, 83, 77, 49, 19, 48, 17, 6, 3, 85, 4, 3, 12,
				10, 104, 97, 114, 115, 104, 32, 114, 111, 111, 116, 49, 40, 48, 38, 6,
				9, 42, (byte) 134, 72, (byte) 134, (byte) 247, 13, 1, 9, 1, 22, 25, 104, 97, 114, 115,
				104, 46, 112, 97, 116, 101, 108, 64, 101, 108, 105, 116, 101, 99, 111, 114,
				101, 46, 99, 111, 109 // Distinguished name.
		};
		final byte[] VALID_MIN_LENGTH_MESSAGE = {
				1, //Certificate Type Count
				1, // Only RSA
				0, 2, //Signature Hash Algorithm Length
				6, 1, // Algo 2 - SHA512/RSA
				0, 18, // Distinguished Names Length
				0, (byte) 16, // Distinguished Name Length
				48, (byte) 129, (byte) 165, 
				49, 11, 48, 9, 6, 3, 85, 4, 6, 19, 2, 73, 78
		};
		final byte[] WRONG_DISTINGUISHED_NAME_LENGTH_COUNT = {
				3, //Certificate Type Count
				1, 2, 64, // 3 types of certificate respectively RSA sign, DSS sign & ECDSA sign
				0, 24, //Signature Hash Algorithm Length
				6, 3, // Algo 1 - SHA512/ECDSA
				6, 1, // Algo 2 - SHA512/RSA
				5, 3, // Algo 3 - SHA385/ECDSA
				5, 1, // Algo 4 - SHA385/RSA
				4, 3, // Algo 5 - SHA256/ECDSA
				4, 1, // Algo 6 - SHA256/RSA
				3, 3, // Algo 7 - SHA224/ECDSA
				3, 1, // Algo 8 - SHA224/RSA
				2, 3, // Algo 9 - SHA1/ECDSA
				2, 1, // Algo 10 - SHA1/RSA
				2, 2, // Algo 11 - SHA1/DSA
				1, 1, // Algo 12 - MD5/RSA
				0, (byte) 200, // Distinguished Names Length
				0, (byte) 168, // Distinguished Name Length
				48, (byte) 129, (byte) 165, 
				49, 11, 48, 9, 6, 3, 85, 4, 6, 19, 2, 73, 78, 49, 12, 48,
				10, 6, 3, 85, 4, 8, 12, 3, 71, 85, 74, 49, 18, 48, 16, 6,
				3, 85, 4, 7, 12, 9, 65, 72, 77, 69, 68, 65, 66, 65, 68, 49,
				39, 48, 37, 6, 3, 85, 4, 10, 12, 30, 69, 76, 73, 84, 69, 67,
				79, 82, 69, 32, 84, 69, 67, 72, 78, 79, 76, 79, 71, 89, 32, 80,
				86, 84, 46, 32, 76, 84, 68, 46, 49, 12, 48, 10, 6, 3, 85, 4,
				11, 12, 3, 67, 83, 77, 49, 19, 48, 17, 6, 3, 85, 4, 3, 12,
				10, 104, 97, 114, 115, 104, 32, 114, 111, 111, 116, 49, 40, 48, 38, 6,
				9, 42, (byte) 134, 72, (byte) 134, (byte) 247, 13, 1, 9, 1, 22, 25, 104, 97, 114, 115,
				104, 46, 112, 97, 116, 101, 108, 64, 101, 108, 105, 116, 101, 99, 111, 114,
				101, 46, 99, 111, 109 // Distinguished name.
		};
		
		try{
			
			Tls1_2CertificateRequest tls1_2CertificateRequest = null;
			try{
				
				tls1_2CertificateRequest = new Tls1_2CertificateRequest();
				tls1_2CertificateRequest.setBytes(VALID_COMPLETE_MESSAGE);
				// Check Certificate Type Counter
				assertEquals(3, tls1_2CertificateRequest.getNumberOfTypesOfCertificate());
				
				// Check Signature & HAsh Algo Length
				assertEquals(24, tls1_2CertificateRequest.getSignatureHashAlgoLength());
				
				// check legth of DNs
				assertEquals(170, tls1_2CertificateRequest.getLengthOfDistiguishedNames()/2);
				
				// to check that getBytes() works properly.
//				byte[] array = tls1_2CertificateRequest.getBytes();
//				for(byte b : array){
//					System.out.println(b + " ");
//				}
				
			} catch (Exception e){
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			try{
				
				tls1_2CertificateRequest = new Tls1_2CertificateRequest();
				tls1_2CertificateRequest.setBytes(VALID_MIN_LENGTH_MESSAGE);
				
				// Check Certificate Type Counter
				assertEquals(1, tls1_2CertificateRequest.getNumberOfTypesOfCertificate());
				
				// Check Signature & HAsh Algo Length
				assertEquals(2, tls1_2CertificateRequest.getSignatureHashAlgoLength());
				
				// check legth of DNs
				assertEquals(18, tls1_2CertificateRequest.getLengthOfDistiguishedNames()/2);
				
			} catch (Exception e){
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			try{
				
				tls1_2CertificateRequest = new Tls1_2CertificateRequest();
				tls1_2CertificateRequest.setBytes(WRONG_DISTINGUISHED_NAME_LENGTH_COUNT);
				
				// Check Certificate Type Counter
				assertEquals(3, tls1_2CertificateRequest.getNumberOfTypesOfCertificate());
				
				// Check Signature & HAsh Algo Length
				assertEquals(24, tls1_2CertificateRequest.getSignatureHashAlgoLength());
				
				// check legth of DNs
				assertNotSame(200, tls1_2CertificateRequest.getLengthOfDistiguishedNames()/2);
				
			} catch (Exception e){
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
		} catch (Exception e){
			e.printStackTrace();
			fail("testsetBytes failed, reason: " + e.getMessage());
		}
	}
	
}
