package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;

import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateVerify;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class Tls1_2CertificateVerifyTest extends TestCase {
	
	public Tls1_2CertificateVerifyTest(String name) {
		super(name);
	}
	
	@Test
	public void testSetBytes(){
		final byte[] VALID_COMPLETE_MESSAGE = {
			6, // Hash Algorithm
			1, // Signature Algorithm
			1, 0, // Signatuer length
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // signature (256 bytes)
		};
		
		final byte[] INVALID_SIGNATURE_LEGTH = {
				6, // Hash Algorithm
				1, // Signature Algorithm
				1, 0, // Signatuer length
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // signature (248 bytes)
		};
		
		try{
			Tls1_2CertificateVerify  tls1_2CertificateVerify = null;
			
			try{
				
				tls1_2CertificateVerify = new Tls1_2CertificateVerify(VALID_COMPLETE_MESSAGE);
				
				// checking the hash algorithm
				assertEquals(tls1_2CertificateVerify.getHashAlgorithm(), 6);
				
				// checking the signature algorithm
				assertEquals(tls1_2CertificateVerify.getSignatureAlgorithm(), 1);
				
				// checking the length of the signature data
				assertEquals(tls1_2CertificateVerify.getSignatureLength(), 256);
				
				//checking signature bytes
				byte[] copy = Arrays.copyOfRange(VALID_COMPLETE_MESSAGE, 4, VALID_COMPLETE_MESSAGE.length);
				assertEquals(TLSUtility.bytesToHex(tls1_2CertificateVerify.getSignature()), TLSUtility.bytesToHex(copy));
				
				// checking the getBytes procedure
				assertEquals(TLSUtility.bytesToHex(tls1_2CertificateVerify.getBytes()), TLSUtility.bytesToHex(VALID_COMPLETE_MESSAGE));
				
			} catch (Exception e) {
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
			
			try{
				// here Tls1_2CertificateVerify constructor catches the exception but 
				// we want exception for test case so using readFrom procedure.
				try{
					tls1_2CertificateVerify = new Tls1_2CertificateVerify(null);
				} catch (Exception e) {
					// NOTHING TO DO HERE.
				}
				try{
					ByteArrayInputStream in = new ByteArrayInputStream(INVALID_SIGNATURE_LEGTH);
					tls1_2CertificateVerify.readFrom(in);
				} catch (Exception e) {
					Exception newe = new IOException("Improper Length");
					if(!e.getMessage().equals(newe.getMessage())){
						fail("testsetBytes failed, reason: " + e.getMessage());
					}
				}

				// checking the hash algorithm
				assertEquals(tls1_2CertificateVerify.getHashAlgorithm(), 6);
				
				// checking the signature algorithm
				assertEquals(tls1_2CertificateVerify.getSignatureAlgorithm(), 1);
				
				// checking the length of the signature data
				assertEquals(tls1_2CertificateVerify.getSignatureLength(), 256);
				
			} catch (Exception e) {
				e.printStackTrace();
				fail("testsetBytes failed, reason: " + e.getMessage());
			}
			
		}catch (Exception e){
			e.printStackTrace();
			fail("testsetBytes failed, reason: " + e.getMessage());
		}
	}
	
}
