package com.elitecore.coreradius.util.eap.tls;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;



public class TLSUtility { 

	public static final String MODULE = "TLS UTILITY";
	static final String MASTER_SECRET_LABEL = "master secret";
	static final String FINISHED_LABEL = "master secret";	
	static int HMAC_MD5_LENGTH = 16;
	static int HMAC_SHA_LENGTH = 20;	
	static int HMAC_SHARED_SECRET_MAX_LENGTH = 64;			
	static int MASTER_SECRET_MAX_LENGTH = 48;	
	static int VARIFY_DATA_LENGTH = 12;
	
	static int intSessionIdentifier = 0;
	

	public static  byte[] HMAC(String hashFunction, byte[] dataToBeEncrypted,byte[] sharedSecret) {
		
		byte[] resultBytes = null;
		
//		if (hashFunction.equals("MD5")) {
//			resultBytes = new byte[HMAC_MD5_LENGTH]; // The final output result will be stored in this byte array			
//		} else if (hashFunction.equals("SHA-1")) {
//			resultBytes = new byte[HMAC_SHA_LENGTH]; // The final output result will be stored in this byte array			
//		}

		byte sharedSecretBytes[] = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // the shared secret bytes will be stored here

		byte[] ipad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];
		byte[] opad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];

		byte[] ipadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing ipad bytes with sharedSecret bytes 
		byte[] opadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing opad bytes with sharedSecret bytes

		try {

			MessageDigest md5MessageDigest = (MessageDigest) MessageDigest.getInstance(hashFunction).clone();

			Arrays.fill(ipad, (byte) 0x36); // fills the inner pad byte array with 0x36 ( 54 in Decimal )
			Arrays.fill(opad, (byte) 0x5c); // fills the outer pad byte array with 0x5c ( 92 in Decimal )

			if (sharedSecret.length < HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is less than 64
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
						sharedSecret.length); // copies the bytes from shared secret in sharedSecretBytes
				Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00); // pads the rest of the bytes with 0x00 ( 0 in Decimal )

			/*} else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64
				md5MessageDigest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				sharedSecretBytes = md5MessageDigest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
				
				//printBytes("SharedSecret Bytes",sharedSecretBytes);*/
				
			} else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64
				md5MessageDigest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				byte[] tmpSharedSecret = md5MessageDigest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
				System.arraycopy(tmpSharedSecret, 0, sharedSecretBytes, 0, tmpSharedSecret.length);
				//printBytes("SharedSecret Bytes",sharedSecretBytes);
				Arrays.fill(sharedSecretBytes, tmpSharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);
			} else {
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
						sharedSecret.length);
			}

			for (int i = 0; i < sharedSecretBytes.length; i++) {
				ipadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (ipad[i] & 0xFF)); // XORing the sharedSecretBytes with the ipad bytes
				opadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (opad[i] & 0xFF)); // XORing the sharedSecretBytes with the opad bytes
			}

			md5MessageDigest.update(ipadXORSharedSecret);
			md5MessageDigest.update(dataToBeEncrypted);
			byte[] tempResult = md5MessageDigest.digest(); // generating the encrypted value of the bytes set
			md5MessageDigest.update(opadXORSharedSecret);
			md5MessageDigest.update(tempResult);
			resultBytes = md5MessageDigest.digest();

		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

		} catch (CloneNotSupportedException cns) {

		}

		return resultBytes;
	}

	public static  byte[] TLSPRF(byte[] secret, byte[] masterSecretLabel, byte[] client_server_random,int length) {
		byte[] result = new byte[length];
		int midpoint;
/*		if (secret.length % 2 == 0)
			midpoint = secret.length / 2;
		else
			midpoint = (secret.length / 2) + 1;*/
		midpoint = (secret.length+1) / 2;

		
		byte[] s1 = new byte[midpoint];
		byte[] s2 = new byte[midpoint];

		System.arraycopy(secret, 0, s1, 0,midpoint);
		//System.arraycopy(secret, midpoint, s2,0, midpoint);
		System.arraycopy(secret,secret.length- midpoint, s2,0, midpoint);
		
		//System.out.println("S1 : "+TLSUtility.getBytesAsString("", s1));
		//System.out.println("S2 : "+TLSUtility.getBytesAsString("", s2));
		
		byte[] seed = new byte[client_server_random.length + masterSecretLabel.length];

		System.arraycopy(masterSecretLabel, 0, seed, 0, masterSecretLabel.length);
		System.arraycopy(client_server_random, 0, seed, masterSecretLabel.length, client_server_random.length);
		//System.arraycopy(data, 0, seed, label.length, 96);
		byte[] p_md5 = P_MD5(s1, seed,length);
		byte[] p_sha = P_SHA(s2, seed,length);

		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (p_md5[i] ^ p_sha[i]);
		}
		return result;
	}

		
	public static  byte[] P_MD5(byte[] s1, byte[] seed, int length) {
		byte[] md5Result = new byte[length];
		byte[][] hmac = new byte[50][];
		byte[][] A = new byte[50][];
		
		A[0]= new byte[seed.length];
		System.arraycopy(seed, 0, A[0], 0, seed.length);
		//System.out.println("A[0] : "+TLSUtility.getBytesAsString("", A[0]));
		int iIteration=0;
		
		while((iIteration*HMAC_MD5_LENGTH)< length)
		{
			iIteration++;
			A[iIteration]= HMAC("md5", A[iIteration-1], s1);
			//System.out.println("A["+iIteration+"] : "+TLSUtility.getBytesAsString("", A[iIteration]));
			byte[] tempSeed = new byte[A[iIteration].length + seed.length];
			System.arraycopy(A[iIteration], 0, tempSeed, 0, A[iIteration].length);
			System.arraycopy(seed, 0, tempSeed, A[iIteration].length, seed.length);
			//System.out.println("Temp Seed : "+TLSUtility.getBytesAsString("", tempSeed));
			hmac[iIteration] = HMAC("md5", tempSeed, s1);			
			if((length-((iIteration-1)*HMAC_MD5_LENGTH)) < HMAC_MD5_LENGTH)
			{				
				System.arraycopy(hmac[iIteration], 0, md5Result,HMAC_MD5_LENGTH * (iIteration-1),length-((iIteration-1)*HMAC_MD5_LENGTH));
			}
			else
			{					
				System.arraycopy(hmac[iIteration], 0, md5Result,HMAC_MD5_LENGTH * (iIteration-1),HMAC_MD5_LENGTH);
			}
			//System.out.println("Final Result Bytes : "+TLSUtility.getBytesAsString("", hmac[iIteration]));
			//System.out.println("Final Out Put : "+TLSUtility.getBytesAsString("", md5Result));
		}
		return md5Result;
	}

	
	public static  byte[] P_SHA(byte[] s2, byte[] seed, int length) {
		byte[] shaResult = new byte[length];

		byte[][] hmac = new byte[50][];
		byte[][] A = new byte[50][];
		
		A[0]= new byte[seed.length];
		System.arraycopy(seed, 0, A[0], 0, seed.length);
		//System.out.println("A[0] : "+TLSUtility.getBytesAsString("", A[0]));
		int iIteration=0;		
		while((iIteration*HMAC_SHA_LENGTH)< length)
		{
			iIteration++;			
			A[iIteration]= HMAC("sha", A[iIteration-1], s2);
			//System.out.println("A["+iIteration+"] : "+TLSUtility.getBytesAsString("", A[iIteration]));
			byte[] tempSeed = new byte[A[iIteration].length + seed.length];
			System.arraycopy(A[iIteration], 0, tempSeed, 0, A[iIteration].length);
			System.arraycopy(seed, 0, tempSeed, A[iIteration].length, seed.length);
			//System.out.println("Temp Seed : "+TLSUtility.getBytesAsString("", tempSeed));
			hmac[iIteration] = HMAC("sha", tempSeed, s2);
			if((length-((iIteration-1)*HMAC_SHA_LENGTH)) < HMAC_SHA_LENGTH)
			{				
				System.arraycopy(hmac[iIteration], 0, shaResult,HMAC_SHA_LENGTH * (iIteration-1),length-((iIteration-1)*HMAC_SHA_LENGTH));
			}
			else
			{					
				System.arraycopy(hmac[iIteration], 0, shaResult,HMAC_SHA_LENGTH * (iIteration-1),HMAC_SHA_LENGTH);
			}
			//System.out.println("Final Result Bytes : "+TLSUtility.getBytesAsString("", hmac[iIteration]));
			//System.out.println("Final Out Put : "+TLSUtility.getBytesAsString("", shaResult));
		}					
		return shaResult;
	}
}
