package com.elitecore.coreeap.util.tls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.spec.IvParameterSpec;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;


public class TLSUtility { 

	public static final String MODULE = "TLS UTILITY";
	static final String MASTER_SECRET_LABEL = "master secret";
	static final String FINISHED_LABEL = "master secret";	
	static int HMAC_MD5_LENGTH = 16;
	static int HMAC_SHA_LENGTH = 20;	
	static int HMAC_SHA256_LENGTH = 32;
	static int HMAC_SHARED_SECRET_MAX_LENGTH = 64;			
	static int MASTER_SECRET_MAX_LENGTH = 48;	
	static int VARIFY_DATA_LENGTH = 12;
	
	static int intSessionIdentifier = 0;
	
	/**
     * Fixed masks (0) of various block size, fed to decryption process for TLS 1.1
     */
	private static ConcurrentHashMap<Integer, IvParameterSpec> masks;

	/**
     * Generates an array of random bytes.
     * @param length number of random bytes to generate ( minimum length should be 4 )
     * @return array of random bytes
     */
    public static byte[] generateSecureRandom(int length)
    {
    	if(length < 4)
    		return null;
    	byte[] challenge = new byte[length];
		byte[] randomNumber = new byte[length-4];
		long gmtTime;
		gmtTime = System.currentTimeMillis() / 1000;				
		challenge[3] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		challenge[2] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		challenge[1] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		challenge[0] = (byte) gmtTime;
		Random rand = new Random();
		rand.nextBytes(randomNumber);
		System.arraycopy(randomNumber, 0, challenge, 4, length-4);
		return (challenge);
    }

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

			MessageDigest digest = (MessageDigest) MessageDigest.getInstance(hashFunction).clone();

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
				digest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				sharedSecret = digest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0, sharedSecret.length);
				//printBytes("SharedSecret Bytes",sharedSecretBytes);
				Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);
			} else {
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
						sharedSecret.length);
			}

			for (int i = 0; i < sharedSecretBytes.length; i++) {
				ipadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (ipad[i] & 0xFF)); // XORing the sharedSecretBytes with the ipad bytes
				opadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (opad[i] & 0xFF)); // XORing the sharedSecretBytes with the opad bytes
			}

			digest.update(ipadXORSharedSecret);
			digest.update(dataToBeEncrypted);
			byte[] tempResult = digest.digest(); // generating the encrypted value of the bytes set
			digest.update(opadXORSharedSecret);
			digest.update(tempResult);
			resultBytes = digest.digest();

		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

		} catch (CloneNotSupportedException cns) {

		}
		return resultBytes;
	}
	
	/**
	 * 	RFC 5246 TLS 1.2 
	 * 	TLS's PRF is created by applying P_hash to the secret as:
	 * 			PRF(secret, label, seed) = P_<hash>(secret, label + seed)
	 * 
	 * @param secret
	 * @param masterSecretLabel
	 * @param client_server_random
	 * @param length
	 * @param tlsSecurityParameters
	 * @return
	 */
	public static byte[] P_HASH(byte[] secret, byte[] masterSecretLabel, byte[] client_server_random, int length, TLSSecurityParameters tlsSecurityParameters){
		byte[] result = new byte[length];
		byte[] seed = new byte[client_server_random.length + masterSecretLabel.length];
		System.arraycopy(masterSecretLabel, 0, seed, 0, masterSecretLabel.length);
		System.arraycopy(client_server_random, 0, seed, masterSecretLabel.length, client_server_random.length);
		byte[] A = seed;
		byte[] tmpSeed;
		int iResultPointer = 0;
		while((length - HMAC_SHA256_LENGTH) > 0){
			A = HMAC(tlsSecurityParameters.getPRFAlgorithm(), A, secret);
			tmpSeed = HMAC(tlsSecurityParameters.getPRFAlgorithm(), appendBytes(A, seed), secret);
			System.arraycopy(tmpSeed, 0, result, iResultPointer, tmpSeed.length);
			iResultPointer += HMAC_SHA256_LENGTH;
			length -= HMAC_SHA256_LENGTH;
		}
		A = HMAC(tlsSecurityParameters.getPRFAlgorithm(), A, secret);
		tmpSeed = HMAC(tlsSecurityParameters.getPRFAlgorithm(), appendBytes(A, seed), secret);
		System.arraycopy(tmpSeed, 0, result, iResultPointer, length);
		return result;
	}
	
	/**
	 * 
	 * 	RFC 4346 and RFC 2246
	 * 	The PRF is then defined as the result of mixing the two pseudorandom
	 * 	streams by exclusive-ORing them together.
	 * 	PRF(secret, label, seed) = P_MD5(S1, label + seed) XOR
	 * 							  P_SHA-1(S2, label + seed);
	 * 
	 * @param secret
	 * @param masterSecretLabel
	 * @param client_server_random
	 * @param length
	 * @return
	 */
	private static byte[] P_SHA1_MD5(byte[] secret, byte[] masterSecretLabel, byte[] client_server_random, int length){
		byte[] result = new byte[length];
		byte[] seed = new byte[client_server_random.length + masterSecretLabel.length];
		System.arraycopy(masterSecretLabel, 0, seed, 0, masterSecretLabel.length);
		System.arraycopy(client_server_random, 0, seed, masterSecretLabel.length, client_server_random.length);

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
		byte[] p_md5 = P_MD5(s1, seed,length);
		byte[] p_sha = P_SHA(s2, seed,length);

		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (p_md5[i] ^ p_sha[i]);
		}
		return result;
	}
	
	public static  byte[] TLSPRF(byte[] secret, byte[] masterSecretLabel, byte[] client_server_random, int length, TLSSecurityParameters tlsSecurityParameters) {
		byte[] result = new byte[length];
		//here P_* functions are the expansion functions as defined in TLS RFCs
		if(tlsSecurityParameters.getProtocolVersion() == ProtocolVersion.TLS1_2){
			/*
			 * RFC 5246 TLS 1.2 
			 * TLS's PRF is created by applying P_hash to the secret as:
		     * 		PRF(secret, label, seed) = P_<hash>(secret, label + seed)
			 */
			result = P_HASH(secret, masterSecretLabel, client_server_random, length, tlsSecurityParameters);
		} else {
			/*
			 * RFC 4346 and RFC 2246
			 * The PRF is then defined as the result of mixing the two pseudorandom
			 * streams by exclusive-ORing them together.
			 * PRF(secret, label, seed) = P_MD5(S1, label + seed) XOR
			 * 							  P_SHA-1(S2, label + seed);
			 */
			result = P_SHA1_MD5(secret, masterSecretLabel, client_server_random, length);
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

	public static byte[] doHash(byte[] clientRandom,byte[] serverRandom,byte[] serverParams, HashAlgorithm algorithm){
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getIdentifier());
			if(clientRandom != null)
				messageDigest.update(clientRandom);
			if(serverRandom != null)
				messageDigest.update(serverRandom);
			if(serverParams != null)
				messageDigest.update(serverParams);
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException nsae) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, algorithm.getIdentifier() + " not supported, Reason: " + nsae.getMessage());
		}
		return null;
	}
	
	/**
	 * generates a master secret.
	 * RFC 2246 - The TLS Protocol Version 1.0 [8.1. Computing the master secret]
	 * @param preMasterSecret = it is generated by tlsUtility.decryptPMS_RSA() or tlsUtility.generatePMS_DH() 
	 * @param serverRandom = byte[32]
	 * @param clientRandom = byte[32]
	 * @return byte[MASTER_SECRET_MAX_LENGTH]
	 */	
	public static  byte[] generateMS(byte[] preMasterSecret,byte[] serverRandom,byte[] clientRandom, TLSSecurityParameters tlsSecurityParameters){
		byte[] masterSecret = new byte[MASTER_SECRET_MAX_LENGTH];
		
		byte[] seed = new byte[64];
		
		System.arraycopy(clientRandom, 0, seed, 0, clientRandom.length);
		System.arraycopy(serverRandom, 0, seed, clientRandom.length, serverRandom.length);
		try{
			masterSecret = TLSPRF(preMasterSecret,MASTER_SECRET_LABEL.getBytes(CommonConstants.UTF8),seed,MASTER_SECRET_MAX_LENGTH, tlsSecurityParameters);
		}catch(UnsupportedEncodingException e){
			masterSecret = TLSPRF(preMasterSecret,MASTER_SECRET_LABEL.getBytes(),seed,MASTER_SECRET_MAX_LENGTH, tlsSecurityParameters);
		}
		return (masterSecret);
	}

	public static  byte[] generateKeyBlock(byte[] ms,String label,byte[] serverRandom,byte[] clientRandom,int keyBlockSize, TLSSecurityParameters tlsSecurityParameters)
	{
		byte[] key_block= new byte[keyBlockSize];
		byte[] seed = new byte[serverRandom.length + clientRandom.length];
		System.arraycopy(serverRandom, 0, seed, 0, serverRandom.length);
		System.arraycopy(clientRandom, 0, seed, serverRandom.length, clientRandom.length);
		try{
			key_block = TLSPRF(ms,label.getBytes(CommonConstants.UTF8),seed,keyBlockSize, tlsSecurityParameters);
		}catch(UnsupportedEncodingException e){
			key_block = TLSPRF(ms,label.getBytes(),seed,keyBlockSize, tlsSecurityParameters);
		}
		return (key_block);		
	}

	public static  byte[] getClientWriteMACSecret(byte[] keyBlock,int hashSize)
	{		
		byte[] returnByte = new byte[hashSize];
		System.arraycopy(keyBlock,0,returnByte,0,hashSize);
		return (returnByte);
	}
	
	public static  byte[] getServerWriteMACSecret(byte[] keyBlock,int hashSize)
	{
		
		byte[] returnByte = new byte[hashSize];
		System.arraycopy(keyBlock,hashSize,returnByte,0,hashSize);
		return (returnByte);
	}
	
	public static  byte[] getClientWriteKey(byte[] keyBlock,int hashSize, int keyMaterialSize)
	{	
		byte[] returnByte = new byte[keyMaterialSize];
		System.arraycopy(keyBlock,(2*hashSize),returnByte,0,keyMaterialSize);
		return (returnByte);
	}
	
	public static  byte[] getServerWriteKey(byte[] keyBlock,int hashSize, int keyMaterialSize)
	{		
		byte[] returnByte = new byte[keyMaterialSize];
		System.arraycopy(keyBlock,(2*hashSize) + keyMaterialSize,returnByte,0,keyMaterialSize);
		return (returnByte);
	}
	
	public static  byte[] getClientWriteIV(byte[] keyBlock,int hashSize,int keyMaterialSize,int ivSize)
	{		
		byte[] returnByte = new byte[ivSize];
		System.arraycopy(keyBlock,(2*hashSize) + (2 *keyMaterialSize),returnByte,0,ivSize);
		return (returnByte);
	}
	
	public static  byte[] getServerWriteIV(byte[] keyBlock,int hashSize,int keyMaterialSize,int ivSize)
	{		
		byte[] returnByte = new byte[ivSize];
		System.arraycopy(keyBlock,(2*hashSize) + (2 *keyMaterialSize) + ivSize,returnByte,0,ivSize);
		return (returnByte);
	}
	
	public static byte[] generateSessionIdentifier()
	{				
		byte[] byteSessionIdentifier = new byte[32];
		String randomString = new String();
		
		UUID uuid = UUID.randomUUID();
		randomString = uuid.toString();
		
		MessageDigest msgDigest = null;
			msgDigest = Utility.getMessageDigest(HashAlgorithm.MD5.getIdentifier());
		try{
			msgDigest.update(randomString.getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			msgDigest.update(randomString.getBytes());
		}
		byte[] reqAuthBytes1 = msgDigest.digest();	
			
		randomString = uuid.toString();
		
		try{
			msgDigest.update(randomString.getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			msgDigest.update(randomString.getBytes());
		}
		byte[] reqAuthBytes2 = msgDigest.digest();
		
		System.arraycopy(reqAuthBytes1, 0, byteSessionIdentifier, 0, 16);
		System.arraycopy(reqAuthBytes2, 0, byteSessionIdentifier, 16, 16);
		
		return byteSessionIdentifier;
	}
	
	public static byte[]  generateVerifyData(byte[] MS,String finished_label,byte[] handshakeMessages, TLSSecurityParameters tlsSecurityParameters)
	{		
		byte[] handshake_hash = null;
		/*
		 * 	TLSv1.2 (RFC 5246) specify that in Finished message PRFAlgorithm is 
		 * 	used to verify the data.
		 * 
		 *  TLS1.0 (RFC 2246) & TLSv1.1 (RFC 4346) specify that in Finished message 
		 *  MD-5/SHA algorithm combination is user to verify the data.
		 *  
		 */
		if(tlsSecurityParameters.getProtocolVersion() == ProtocolVersion.TLS1_2){
			handshake_hash = new byte[32];
			MessageDigest PRFMessageDigest = Utility.getMessageDigest(tlsSecurityParameters.getPRFAlgorithm());
			PRFMessageDigest.update(handshakeMessages);
			handshake_hash = PRFMessageDigest.digest();
		}else {
			byte[] md_hash = null;
			byte[] sha_hash = null;

			MessageDigest md5MessageDigest = Utility.getMessageDigest(HashAlgorithm.MD5.getIdentifier());
			md5MessageDigest.update(handshakeMessages);
			md_hash = md5MessageDigest.digest();

			MessageDigest shaMessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());
			shaMessageDigest.update(handshakeMessages);
			sha_hash = shaMessageDigest.digest();

			handshake_hash = new byte[md_hash.length + sha_hash.length];
			System.arraycopy(md_hash, 0, handshake_hash, 0, md_hash.length);
			System.arraycopy(sha_hash, 0, handshake_hash, md_hash.length, sha_hash.length);
		}
		byte[] verifyData = null;
		try{
			verifyData = TLSPRF(MS,finished_label.getBytes(CommonConstants.UTF8),handshake_hash,12, tlsSecurityParameters);
		}catch(UnsupportedEncodingException e){
			verifyData = TLSPRF(MS,finished_label.getBytes(),handshake_hash,12, tlsSecurityParameters);
		}	
		
		return verifyData;
	}	
	
	public static int readint(byte[] byteData,int startPosition,int Byte)
	{
		int number=0,iCounter=0;
		for(iCounter=0;iCounter<Byte ; iCounter++)
		{
			number = number << 8;
			number =  (int)number | (byteData[startPosition + iCounter] & 0xFF);			
		}		
		return (number);
	}
	/***
	 * appends bytes of newArrayBytes to the oldArrayBytes. 
	 * if the oldArrayBytes is null, then it will assign newArrayBytes to the OldArrayBytes
	 * if the newArrayBytes is null, then it will do nothing, simply return OldArrayBytes 
	 * @param oldArrayBytes
	 * @param newArrayBytes
	 * @return
	 */
	public static byte[] appendBytes(byte[] oldArrayBytes,byte[] newArrayBytes)
	{
		if(oldArrayBytes == null)
			return(newArrayBytes);
		
		byte[] tempArrayBytes = oldArrayBytes;
		if(newArrayBytes != null)
		{
			tempArrayBytes = new byte[oldArrayBytes.length + newArrayBytes.length];
			System.arraycopy(oldArrayBytes,0,tempArrayBytes,0,oldArrayBytes.length);
			System.arraycopy(newArrayBytes,0,tempArrayBytes,oldArrayBytes.length,newArrayBytes.length);
		}
		return(tempArrayBytes);
	}
	/***
	 * it will return byte[2] array.
	 * byte[0] = tag
	 * byte[1] = number of byte for length
	 * byte[2] = length
	 * minimam start position is 0
	 */
	public static int[] getTBSformatAttribute(byte[] tbsFormatByte,int startPosition)
	{
		int[] returnByte = new int[3];
		returnByte[0] = tbsFormatByte[startPosition];
//		System.out.println("byte : " + tbsFormatByte[startPosition+1]);
//		System.out.println("actual : " + (byte)128);
		if(tbsFormatByte[startPosition+1] > (byte)128 && tbsFormatByte[startPosition+1]<0)
			returnByte[1] = (tbsFormatByte[startPosition+1] - (byte)128);
		else
		{
			returnByte[1] = 0;
			returnByte[2] = (tbsFormatByte[startPosition+1]);
			return(returnByte);
		}		
		returnByte[2] = TLSUtility.readint(tbsFormatByte,startPosition+2,returnByte[1]);
		return(returnByte);
	}
	public static byte[] getDitinguishedNameOfCertificate(byte[] byteCertificate) {
		
		int TAG_ATTRIBUTE_LENGTH=2;
		byte[] distinguishedName = null;		
		
		int[] tagAttribute = null;
		byte[] sequenceByte = null;
		byte[] headerByte = null;		
		boolean flagDNFound = false;		
		int currentPosition=0;

		while (true)
		{
			tagAttribute = TLSUtility.getTBSformatAttribute(byteCertificate,currentPosition);			
			if (tagAttribute[0] == 48) 
			{
				currentPosition += TAG_ATTRIBUTE_LENGTH + tagAttribute[1];
				sequenceByte = TLSUtility.getBytefromArray(byteCertificate,	currentPosition, tagAttribute[2]);
				if(flagDNFound)
				{
					headerByte = TLSUtility.getBytefromArray(byteCertificate,currentPosition-(TAG_ATTRIBUTE_LENGTH + tagAttribute[1]),(TAG_ATTRIBUTE_LENGTH + tagAttribute[1]));
					break;
				}
			}			
			else if (tagAttribute[0] == 5)
			{
				flagDNFound = true;				
				currentPosition += TAG_ATTRIBUTE_LENGTH + tagAttribute[2];			
				continue;
			}								
			else
			{
				currentPosition += TAG_ATTRIBUTE_LENGTH + tagAttribute[2];							
			}			
		}				
		distinguishedName = TLSUtility.appendBytes(headerByte,sequenceByte);				
		return distinguishedName;
	}	

	public static byte[] getBytefromArray(byte[] byteCertificate,int currentPosition,int length)
	{
		byte[] returnByte = new byte[length];
		System.arraycopy(byteCertificate,currentPosition,returnByte,0,length);
		return(returnByte);
	}
	
	public static byte[] generateTTLSChapChallange(byte[] masterSecret,byte[] clientRandom,byte[] serverRandom, TLSSecurityParameters tlsSecurityParameters)
	{
		byte[] chapChallange = null;
		final String CHAP_LABEL = "ttls challenge";
		byte[] seed = new byte[clientRandom.length + serverRandom.length];
		System.arraycopy(clientRandom,0,seed,0,clientRandom.length);
		System.arraycopy(serverRandom,0,seed,clientRandom.length,serverRandom.length);
		try{
			chapChallange = TLSPRF(masterSecret,CHAP_LABEL.getBytes(CommonConstants.UTF8),seed,17, tlsSecurityParameters);
		}catch(UnsupportedEncodingException e){
			chapChallange = TLSPRF(masterSecret,CHAP_LABEL.getBytes(),seed,17, tlsSecurityParameters);
		}
//		if(chapChallange != null){
//			Logger.logTrace(MODULE, "TTLS master secret : " + bytesToHex(masterSecret));
//			Logger.logTrace(MODULE, "TTLS seed : " + bytesToHex(seed));
//			Logger.logTrace(MODULE, "TTLS Chap Challenge Generated SuccessFully");
//		}
		return(chapChallange);
	}
	
	public static final boolean validateCHAPPassword(byte[] receivedCHAPPassword,byte bChapID,String strUserPassword,byte[] ttlsChapChallenge){
		
		byte[] bCHAPPassword = new byte[16];
		System.arraycopy(receivedCHAPPassword,1,bCHAPPassword,0,16);
//		byte chapID = receivedChapPassword[0];
		//final int PASSWORD_LENGTH = 15;
		 boolean bAccept = false;		 
		 MessageDigest messageDigest = null;
				messageDigest = Utility.getMessageDigest(HashAlgorithm.MD5.getIdentifier());			
			
		 if(messageDigest!=null && bCHAPPassword!=null && strUserPassword!=null){
		 	messageDigest.reset();
		 	messageDigest.update(bChapID);
		 	try{
		 		messageDigest.update(strUserPassword.getBytes(CommonConstants.UTF8));
		 	}catch(UnsupportedEncodingException e){
		 		messageDigest.update(strUserPassword.getBytes());
		 	}
		 	messageDigest.update(ttlsChapChallenge);
		 	byte bDigestedMessage1[] = messageDigest.digest();
			if(bDigestedMessage1.length != bCHAPPassword.length){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid length of received CHAP-Password: " + bCHAPPassword.length + ", expected length: " + bDigestedMessage1.length);
				bAccept = false;
			}else{
				bAccept = true;
				for(int iCount=0;iCount<bDigestedMessage1.length;iCount++){
					if(bDigestedMessage1[iCount]!=bCHAPPassword[iCount]){
						bAccept = false;
					   break;	
					}
				}
				
				if(!bAccept){
					bChapID = receivedCHAPPassword[0];
					messageDigest.reset();
				 	messageDigest.update(bChapID);
				 	try{
				 		messageDigest.update(strUserPassword.getBytes(CommonConstants.UTF8));
				 	}catch(UnsupportedEncodingException e){
				 		messageDigest.update(strUserPassword.getBytes());
				 	}
				 	messageDigest.update(ttlsChapChallenge);
				 	byte bDigestedMessage2[] = messageDigest.digest();
					bAccept = true;
					for(int iCount=0;iCount<bDigestedMessage2.length;iCount++){
						if(bDigestedMessage2[iCount]!=bCHAPPassword[iCount]){
							bAccept = false;
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Invalid CHAP-Password. Expected: " + bytesToHex(bDigestedMessage2) + ", Received: " + bytesToHex(bCHAPPassword));
						   break;	
						}
					}
				}
			}
		 }else{
		 	if(messageDigest==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "MessageDigest is not set , so ignoring the request");
		 	}else if(bCHAPPassword==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "CHAP-Password is Not Found , so ignoring the request");
		 	}else if(strUserPassword==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "User-Password is Not Found , so ignoring the request");
		 	}
		 }		 
		 return bAccept;
	}

	public static byte[] generateMSK(byte[] masterSecret,byte[] clientRandom,byte[] serverRandom,String label, TLSSecurityParameters tlsSecurityParameters)
	{
		byte[] tlsPRFOutput = null;
		byte[] msk = new byte[128];
		byte[] random = new byte[clientRandom.length + serverRandom.length];
		System.arraycopy(clientRandom,0,random,0,clientRandom.length);
		System.arraycopy(serverRandom,0,random,clientRandom.length,serverRandom.length);
		try{
			tlsPRFOutput = TLSPRF(masterSecret,label.getBytes(CommonConstants.UTF8),random,128, tlsSecurityParameters);
		}catch(UnsupportedEncodingException e){
			tlsPRFOutput = TLSPRF(masterSecret,label.getBytes(),random,128, tlsSecurityParameters);
		}
		System.arraycopy(tlsPRFOutput,0,msk,0,128);		
		return(msk);		
	}

	/***
	 * This function will display byte[] in Hexadecimal form. 
	 * if the data is null then it will simply print "null"
	 * @param data[]
	 * @return Hexadecimal form of the data[] in String format.
	 */
	public static String bytesToHex(byte[] data) {		
        StringBuilder buf = new StringBuilder();
        if(data == null)
        {
        	buf.append("null");
        }
        else
        {
        	buf.append("0x");
        	for (int i = 0; i < data.length; i++) {
        		buf.append(byteToHex(data[i]));
        	}
        }
        return (buf.toString());
    }

	/***
	 * this function will never use from other class. it is only call from the
	 * TLSUtility.bytesToHex(byte[] data) function.
	 * @param data
	 * @return Hexdecimal of a given byte in a String format.
	 */
	private static String byteToHex(byte data) {
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	/***
	 * this funtion will never use from other class.it is only call from the 
	 * TLSUtility.byteToHex(byte data) function.
	 * @param iNumber
	 * @return Hexadecimal of a given number in Char format.
	 */
	private static char toHexChar(int iNumber) {
        if ((0 <= iNumber) && (iNumber <= 9)) {
            return (char) ('0' + iNumber);
        } else {
            return (char) ('a' + (iNumber - 10));
        }
    }

	/***
	 * This function will display byte[] in Hexadecimal form. with the given title
	 * if the given byte[] is null, it will display "null" instead of exception.
	 * @param title
	 * @param bytesToPrint[]
	 * @return title with Hexadecimal form of the data[] in String format.
	 */
	public static String getBytesAsString(String title, byte[] bytesToPrint){
		StringWriter strWriter = new StringWriter();
		PrintWriter out = new PrintWriter(strWriter);
		out.print(title);
		out.print("-->");
		if(bytesToPrint == null)
		{
			out.print("null");
		}
		else
		{					
			for (int i = 0; i < bytesToPrint.length; i++) {
				byte b = bytesToPrint[i];
				out.print(Integer.toHexString((b & 0xFF)));
				out.print(" ");
			}
		}
		out.flush();
		out.close();
		return strWriter.toString();
	}
	
	public static final byte[] HexToBytes(String data){
		if(data == null || data.length() < 2)
			return null;
		if(data.charAt(1) == 'x')
			data = data.substring(2);		
		int len = data.length();
		if(len % 2 != 0)
			len ++ ;		
		byte[] returnBytes = new byte[len/2];
		for(int i=0 ; i<len-1; ){
			returnBytes[i/2] = (byte) (HexToByte(data.substring(i, i+2)) & 0xFF);			
			i +=2;			
		}
		return returnBytes;
	}
	
	protected static final int HexToByte(String data){
		int byteVal = toByte(data.charAt(0)) & 0xFF;
		byteVal = byteVal << 4;
		byteVal = byteVal | toByte(data.charAt(1));
		return byteVal;
	}
	
	protected static final int toByte(char ch){
		if((ch >= '0') && (ch <= '9')){
			return ch - 48;
		}else if((ch >= 'A') && (ch <= 'F')){
			return ch - 65 + 10;
		}else if((ch >= 'a') && (ch <= 'f')){
			return ch - 97 + 10;
		}else {
			return 0;
		}
	}

	/**
	 * This method converts the input string to a string containing only hex characters considering <b>*</b> as Hex character and <i>skipping</i> all non-hex characters.<br/>
	 * Examples:<br/>
	 * <table border='1'>
	 * 		<tr align='center'>
	 * 			<td><b> Input</b> </td>
	 * 			<td><b> Output</b> </td>
	 * 		</tr>
	 * 		<tr align='center'>
	 * 			<td> <code>null or {whitespace}</code> </td>
	 * 			<td> <code>{blank-string}</code> </td>
	 * 		</tr>
	 * 		<tr align='center'>
	 * 			<td> 1234567890ABCDEF </td>
	 * 			<td> 1234567890abcdef </td>
	 * 		</tr>
	 * 		<tr align='center'>
	 * 			<td> *0*A*a* </td>
	 * 			<td> *0*a*a* </td>
	 * 		</tr>
	 * 		<tr align='center'>
	 * 			<td> abcghi(){:!?def </td>
	 * 			<td> abcdef </td>
	 * 		</tr>
	 * </table>
	 * 
	 */
	public static String convertToPlainString(String strValue){		
    	String value = "";
    	if(strValue == null)
    		return value;    	    	
    	
    	for(int i=0;i< strValue.length() ; i++){
    		if(isHexDigit(strValue.charAt(i))){
    			value= value + strValue.charAt(i);    			
    		}
    	}
    	return value.toLowerCase();
	}
	
	private static boolean isHexDigit(int character){
		if(character >= '0' && character <= '9' ){
			return true;
		}else if(character >= 'A' && character <='F'){
			return true;
		}else if(character >= 'a' && character <='f'){
			return true;
		}else if(character == '*'){
			return true;
		}
		return false;
	}
	
	public static byte[] getFixedMask(int ivSize) {
		if (masks == null) {
            masks = new ConcurrentHashMap<Integer, IvParameterSpec>(5);
        }

        IvParameterSpec iv = masks.get(ivSize);
        if (iv == null) {
            iv = new IvParameterSpec(new byte[ivSize]);
            masks.put(ivSize, iv);
        }
        return iv.getIV();
	}
	
	public static boolean matches(String sourceString, String pattern){
		return matches(sourceString,pattern.toCharArray());
	}
	public static boolean matches(String sourceString, char[] pattern){
		int stringOffset = 0;
		char[] stringCharArray = sourceString.toCharArray();
		final int stringLen = stringCharArray.length;
		final int patternLen = pattern.length;
		int currentPos=0;
		try{
			for(currentPos=0;currentPos<patternLen;currentPos++,stringOffset++){
				if(stringOffset == stringLen){
					while(currentPos < patternLen){
						if(pattern[currentPos] != '*')
							return false;
						currentPos++;
					}
					return true;
				}


				if(pattern[currentPos]!=stringCharArray[stringOffset]){
					if(pattern[currentPos]=='\\'){
						currentPos++;
						if(pattern[currentPos]!=stringCharArray[stringOffset])
							return false;
						else
							continue;
					}
					if(pattern[currentPos]=='*'){
						boolean bStar = true;
						currentPos++;
						if(currentPos == patternLen)
							return true;
						while(bStar){
							int tmpCurrentPos = currentPos;
							//go to first matching occurrence
							while(stringCharArray[stringOffset]!=pattern[tmpCurrentPos]){
								stringOffset++;
								if(stringOffset == stringLen){
									while(tmpCurrentPos < patternLen){
										if(pattern[tmpCurrentPos] != '*')
											return false;
										tmpCurrentPos++;
									}
									return true;
								}
							}
							//match whole string until next * come
							while(tmpCurrentPos < patternLen){
								if(pattern[tmpCurrentPos] != stringCharArray[stringOffset]){
									if(pattern[tmpCurrentPos] == '*'){
										bStar = false;
										currentPos = tmpCurrentPos - 1;
										stringOffset--;
										break;
									}else if(pattern[tmpCurrentPos] != '?'){
										break;
									}
								}
								tmpCurrentPos++;
								stringOffset++;
								if(stringOffset == stringLen){
									while(tmpCurrentPos < patternLen){
										if(pattern[tmpCurrentPos] != '*'){
											return false;
										}
										tmpCurrentPos++;
									}
									return true;
								}
							}
							if(stringOffset == stringLen && tmpCurrentPos == patternLen)
								return true;
						}
						continue;
					}
					if(pattern[currentPos]=='?'){
						continue;
					}
					return false;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		if(currentPos < patternLen){
			while(pattern[currentPos] == '*')
				currentPos++;
		}
		return(currentPos == patternLen && stringOffset == stringLen);
	}
}
