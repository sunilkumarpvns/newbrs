/*
 *	Radius Client API
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Sep 13, 2006
 *	Created By Dhirendra Kumar Singh
 */

package com.elitecore.coreradius.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 * This class provides a collection of different RADIUS Server utilities like 
 * packet-identifier generator, password encryption, authenticator generator etc.
 * @author Elitecore Technologies Ltd.
 */
public class RadiusUtility {


	public static final String MODULE = "RADIUS UTILITY";

	private static final String HEX_INITIAL = "0x";
	private static final boolean IGNORE_CASE = true;
	private static final int START_OF_STRING = 0; 
	
	public static final int HMAC_MD5_LENGTH = 16;
	public static final int HMAC_SHA_LENGTH = 20;
	public static final int HMAC_SHARED_SECRET_MAX_LENGTH = 64;
	public static final int AUTH_PASS_LEN				 = 16;

	public static int salt = 32768;
	public static Pattern realmRegx;
	public static Pattern userNameRegx;
	static{
		//compiling the regular expressions for NAI
		realmRegx = Pattern.compile("[0-9]*[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*(\\.[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*)+$");
		userNameRegx = Pattern.compile("[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+[.]?[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+");;
	}
	
	public static final Splitter radAttributeIdSplitter = Splitter.on(':');

	/**
	 * It generates a randomly selected value upto 255 which can be used as 
	 * IDENTIFIER for a RadiusPacket. 
	 * @return randomly generated IDENTIFIER
	 * @see RadiusPacket#setIdentifier(int)
	 */
	/*changed to add 1 as identifier 0 is reserved for Status Server Message
	 *so the interval is now [1,255] which initially was [0,255]
	 */
	public static int generateIdentifier(){
		return (new Random()).nextInt(255) + 1;
	}

	/**
	 * It generates 16-byte long value that can be used as Request-Authenticator
	 * in an Authentication-Request packet. It follows RFC 2865 for calculation.
	 * @param sharedSecret the shared-secret to be used
	 * @return 16-byte value ready to be used as request-authenticator
	 * @throws RadiusGeneralException if MD5 algorithm could not be loaded
	 */
	public static byte[] generateRequestAuthenticatorForAuthentication(
			String sharedSecret) throws RadiusGeneralException {
		/*
		 * In Access-Request Packets, the Authenticator value is a 16
		 * octet random number, called the Request Authenticator. The
		 * value SHOULD be unpredictable and unique over the lifetime of a
		 * secret (the password shared between the client and the RADIUS
		 * server)
		 * Reference: RFC 2865
		 */
		byte[] requestAuthenticator = new byte[16];

		/*long randomNumber  = new Random(System.currentTimeMillis()).nextLong();


		for(int i=15; i<=0; i--){
		    requestAuthenticator[i] = (byte) randomNumber ;
            randomNumber = randomNumber >>> 8;
        }*/
		/*try {
			MessageDigest messageDigest = (MessageDigest) MessageDigest.getInstance("MD5").clone();
			messageDigest.reset();
	        messageDigest.update(sharedSecret.getBytes());
	        messageDigest.update(requestAuthenticator);
	        return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RadiusException("Encryption algorithm MD5 could not be found.");
		}catch (Exception e){
            throw new RadiusException("Error generating request authenticator, reason : " + e.getMessage());
        }*/
		return requestAuthenticator;
	}

	/**
	 * It generates 16-byte long value that can be used as Request-Authenticator
	 * in an Authentication-Request packet. It follows RFC 2866 for calculation.
	 * It is recommended that this method should be invoked after all the other fields
	 * of the <code>packet</code> has been set. Any change in the fields of 
	 * <code>packet</code> will require you to reinvoke this method to get new 
	 * Request-Authenticator field.
	 * @param packet the request-packet to be used
	 * @param sharedSecret shared-secret to be used
	 * @return 16-byte value ready to be used as request-authenticator for the <code>packet</code>
	 * @throws RadiusGeneralException if MD5 algorithm could not be loaded
	 */
	public static byte[] generateRequestAuthenticatorForAccounting(
			final RadiusPacket packet, final String sharedSecret) throws RadiusGeneralException{
		/*
		 * The Request Authenticator field in Accounting-Request packets contains a 
		 * one- way MD5 hash calculated over a stream of octets consisting of the 
		 * Code + Identifier + Length + 16 zero octets + request attributes + shared secret 
		 * (where + indicates concatenation).
		 * Reference: RFC 2866
		 */
		MessageDigest messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
		messageDigest.reset();
		//add the CODE
		messageDigest.update((byte)packet.getPacketType());
		//add the IDENTIFIER
		messageDigest.update((byte)packet.getIdentifier());
		//add the LENGTH(2 bytes)
		byte [] lengthArray = { (byte)((packet.getLength() >>> 8) & 0xFF),
				((byte)(packet.getLength() & 0xFF))};
		messageDigest.update(lengthArray);
		//add the 16 zero octets
		byte[] zeroOctets = new byte[16];
		Arrays.fill(zeroOctets, (byte)0);
		messageDigest.update(zeroOctets);
		//add the REQUEST-ATTRIBUTES
		messageDigest.update(packet.getAttributeBytes());
		//add the SHARED-SECRET
		try{
			messageDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			messageDigest.update(sharedSecret.getBytes());
		}
		return messageDigest.digest();
	}

	public static String decodepass( byte[] requestAuthenticator, byte[] encryptedPass, int len, String strSecret )
	{
		try
		{
			MessageDigest m_md5 = null;

			m_md5 = RadiusUtility.getMessageDigest(CommonConstants.MD5);

			m_md5.clone();
			m_md5.reset();
			// add the shared secret
			m_md5.update( strSecret.getBytes(CommonConstants.UTF8) );
			// add the  Request Authenticator.
			m_md5.update(requestAuthenticator);
			// get the md5 hash( b1 = MD5(S + RA) ).
			byte[] bn = m_md5.digest();

			boolean bNullFound = false;
			for (int i = 0; i < len; i++)
			{
				// perform the XOR as specified by RFC 2865.
				if(bNullFound)
					encryptedPass[i] = 0;
				else
					encryptedPass[i] ^= bn[i];
				if(encryptedPass[i] == 0)
					bNullFound = true;					
			}
			
			if( len <= AUTH_PASS_LEN )
			{
				String strPass = null;
				try{
					strPass = (new String( encryptedPass ,CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					strPass = (new String( encryptedPass ));
				}
				return strPass;
			}

			MessageDigest m_md5Temp = null;
			try
			{
				m_md5Temp = MessageDigest.getInstance("MD5");
			}
			catch( NoSuchAlgorithmException e ){
				LogManager.getLogger().trace(MODULE, e);
			}
			byte[] r = new byte[AUTH_PASS_LEN];
			byte[] s = null;
			int rlen = ((len - 1) / AUTH_PASS_LEN) * AUTH_PASS_LEN;

			for (int n = rlen; n > 0; n -= AUTH_PASS_LEN )
			{
				if(n == AUTH_PASS_LEN)
				{
					s = r;
				}
				else
				{
					s = new byte[n - AUTH_PASS_LEN];
					for( int j = 0 ; j < n - AUTH_PASS_LEN; j++ )
					{
						s[j] = encryptedPass[n - AUTH_PASS_LEN + j];
					}
				}
				m_md5Temp.reset();
				m_md5Temp.update( strSecret.getBytes(CommonConstants.UTF8) );
				m_md5Temp.update( s );

				byte digest[] = m_md5Temp.digest();
				for (int i = 0; i < AUTH_PASS_LEN && (i + n) < len; i++)
				{
					encryptedPass[i + n] ^= digest[i];
				}
			}

			String strPass = (new String( encryptedPass )).trim();
			return strPass;
		}
		catch( Exception e ){
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}

	/**
	 * It encrypts the <code>userPassword</code> assuming that it is a PAP-Password.
	 * It follows RFC 2865 for calculation.
	 * @param userPassword the password to encrypt
	 * @param sharedSecret shared-secret to be used for calculation
	 * @param requestAuthenticator 16-byte long request-authenticator
	 * @return
	 * @throws RadiusGeneralException
	 */
	public static byte[] encodePassword( String userPassword, 
			String sharedSecret, byte[] requestAuthenticator) throws RadiusGeneralException {

		String strPassword = userPassword;
		if (strPassword==null)
			strPassword = ""; //NOSONAR - Reason: Credentials should not be hard-coded
		// encrypt the password.
		byte[] finalUserPassBytes = null;
		//the password must be a multiple of 16 bytes and less than or equal
		//to 128 bytes. If it isn't a multiple of 16 bytes fill it out with
		// zeroes
		//to make it a multiple of 16 bytes. If it is greater than 128 bytes
		//truncate it at 128
		byte [] userPasswordBytes = null;
		try {
			userPasswordBytes = strPassword.getBytes(CommonConstants.UTF8);
		}catch(Exception e) {        	
			userPasswordBytes = strPassword.getBytes();
		}

		if (userPasswordBytes.length > 128) {
			finalUserPassBytes = new byte[128];
			System.arraycopy(userPasswordBytes, 0, finalUserPassBytes, 0, 128);
		} else {
			finalUserPassBytes = userPasswordBytes;
		}

		// declare the byte array to hold the final product
		byte[] encryptedPass = null;

		if (finalUserPassBytes.length < 128) {
			if (finalUserPassBytes.length % 16 == 0) {
				// It is already a multiple of 16 bytes
				encryptedPass = new byte[finalUserPassBytes.length];
			} else {
				// Make it a multiple of 16 bytes
				encryptedPass = new byte[((finalUserPassBytes.length / 16) * 16) + 16];
			}
		} else {
			// the encrypted password must be between 16 and 128 bytes
			encryptedPass = new byte[128];
		}

		// copy the userPass into the encrypted pass and then fill it out with
		// zeroes
		System.arraycopy(finalUserPassBytes, 0, encryptedPass, 0,
				finalUserPassBytes.length);
		//Arrays.fill(encryptedPass, userPassBytes.length, encryptedPass.length, (byte)0);

		MessageDigest messageDigest = null;
		messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);

		messageDigest.reset();
		// add the shared secret
		try {
			messageDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
		} catch (UnsupportedEncodingException e) {
			messageDigest.update(sharedSecret.getBytes());
		}
		// add the Request Authenticator.
		messageDigest.update(requestAuthenticator);
		// get the md5 hash( b1 = MD5(S + RA) ).
		byte bn[] = messageDigest.digest();

		for (int i = 0; i < 16; i++) {
			// perform the XOR as specified by RFC 2865.
			encryptedPass[i] = (byte) ( (bn[i] ) ^ (encryptedPass[i]));
		}

		if (encryptedPass.length > 16) {
			for (int i = 16; i < encryptedPass.length; i += 16) {
				messageDigest.reset();
				// add the shared secret
				try {
					messageDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
				} catch (UnsupportedEncodingException e) {
					messageDigest.update(sharedSecret.getBytes());
				}
				//add the previous(encrypted) 16 bytes of the user password
				messageDigest.update(encryptedPass, i - 16, 16);
				// get the md5 hash( bn = MD5(S + c(i-1)) ).
				bn = messageDigest.digest();
				for (int j = 0; j < 16; j++) {
					// perform the XOR as specified by RFC 2865.
					encryptedPass[i + j] = (byte) ( (bn[j]) ^ (encryptedPass[i + j]));
				}
			}
		}
		return encryptedPass;
	}

	/**
	 * It generates 16-byte long value that can be used as Response-Authenticator
	 * in an Authentication-Response packet.
	 * It follows RFC 2865 for calculation.
	 * @param packet
	 * @param requestAuthenticator
	 * @param sharedSecret
	 * @return
	 * @throws RadiusGeneralException
	 */
	public static byte[] generateResponseAuthenticatorForAutentication(
			final IRadiusPacket packet,byte[] requestAuthenticator, final String sharedSecret) throws RadiusGeneralException{
		try {
			MessageDigest messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			messageDigest.reset();
			//add the CODE
			messageDigest.update((byte)packet.getPacketType());
			//add the IDENTIFIER
			messageDigest.update((byte)packet.getIdentifier());
			//add the LENGTH(2 bytes)
			byte [] lengthArray = { (byte)((packet.getLength() >>> 8) & 0xFF),
					((byte)(packet.getLength() & 0xFF))};
			messageDigest.update(lengthArray);
			//add the Request Authenticator
			messageDigest.update(requestAuthenticator);
			//add the RESPONSE-ATTRIBUTES
			messageDigest.update(packet.getAttributeBytes());
			//add the SHARED-SECRET
			messageDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
			return messageDigest.digest();
		}catch (Exception e){
			throw new RadiusGeneralException("Error generating response authenticator, reason : " + e.getMessage(),e);
		}
	}


	/**
	 * It generates 16-byte long value that can be used as Response-Authenticator
	 * in an Accounting-Response packet.
	 * It follows RFC 2866 for calculation.
	 * @param packet
	 * @param requestAuthenticator
	 * @param sharedSecret
	 * @return
	 * @throws RadiusGeneralException
	 */
	public static byte[] generateResponseAuthenticatorForAccounting(
			final IRadiusPacket packet, byte[] requestAuthenticator, final String sharedSecret) throws RadiusGeneralException{
		try {
			MessageDigest messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			messageDigest.reset();
			//add the CODE
			messageDigest.update((byte)packet.getPacketType());
			//add the IDENTIFIER
			messageDigest.update((byte)packet.getIdentifier());
			//add the LENGTH(2 bytes)
			byte [] lengthArray = { (byte)((packet.getLength() >>> 8) & 0xFF),
					((byte)(packet.getLength() & 0xFF))};
			messageDigest.update(lengthArray);
			//add the Request Authenticator
			messageDigest.update(requestAuthenticator);
			//add the RESPONSE-ATTRIBUTES
			messageDigest.update(packet.getAttributeBytes());
			//add the SHARED-SECRET
			try{
				messageDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				messageDigest.update(sharedSecret.getBytes());
			}
			return messageDigest.digest();
		}catch (Exception e){
			throw new RadiusGeneralException("Error generating response authenticator, reason : " + e.getMessage(),e);
		}
	}

	/*
	 * The following methods are added by Hrishikesh K. Trivedi.
	 */
	//private static final byte [] RANDOM_STRING = {'1','2','3','1','2','3','1','2','3','1','2','3','1','2','3','1'};
	public static byte[] generateRFC2865RequestAuthenticator(){

		byte [] RANDOM_STRING = new byte[16]; 
		int random = 0;
		for(int i = 0 ; i< RANDOM_STRING.length; i++ ) {
			random = (int)(Math.random()*255);
			RANDOM_STRING[i] = (byte)random;
		}
		MessageDigest msgDigest = null;
		msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);

		msgDigest.update(RANDOM_STRING);

		return msgDigest.digest();

	}

	public static byte[] generateRFC2865ResponseAuthenticator(final IRadiusPacket authRequestPacket, 
			byte[] requestAuthenticator, 
			final String sharedSecret) {
		MessageDigest messageDigest = null;
		byte[] sharedSecretBytes = null;
		try{
			sharedSecretBytes = sharedSecret.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			sharedSecretBytes = sharedSecret.getBytes();
		}
		byte[] updateBytes = new byte[authRequestPacket.getBytes().length + sharedSecretBytes.length];
		System.arraycopy(authRequestPacket.getBytes(), 0, updateBytes, 0, authRequestPacket.getBytes().length);
		System.arraycopy(requestAuthenticator, 0, updateBytes, 4, requestAuthenticator.length);
		System.arraycopy(sharedSecretBytes, 0, updateBytes, authRequestPacket.getBytes().length, sharedSecretBytes.length);
		try {
			messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			messageDigest.reset();
			messageDigest.update(updateBytes);
			return messageDigest.digest();
			//We know these exceptions will never hit
		}catch (Exception e){
		}
		return new byte[16];
	}

	public static byte[] generateRFC2866RequestAuthenticator(final IRadiusPacket acctRequestPacket,
			final String strSharedSecret){

		MessageDigest messageDigest = null;
		RadiusPacket acctReqPacket = new RadiusPacket();
		acctReqPacket.setBytes(acctRequestPacket.getBytes());
		acctReqPacket.setAuthenticator(new byte[16]);
		try{
			messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			messageDigest.update(acctReqPacket.getBytes());
			try{
				messageDigest.update(strSharedSecret.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				messageDigest.update(strSharedSecret.getBytes());
			}

			return messageDigest.digest();
		}catch(Exception exc){

			return null;
		}
	}

	public static byte[] generateRFC2866ResponseAuthenticator(final IRadiusPacket acctResponsePacket,
			byte[] requestAuthenticator,
			final String strSharedSecret){
		return null;
	}

	public static byte[] HMAC(String hashFunction, byte[] dataToBeEncrypted, String strSharedSecret){

		byte[] sharedSecret = null;
		try{
			sharedSecret = strSharedSecret.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			sharedSecret = strSharedSecret.getBytes();
		}
		byte[] resultBytes = null;
		if("MD5".equals(hashFunction)){
			resultBytes = new byte[HMAC_MD5_LENGTH]; // The final output result will be stored in this byte array
		}else if("SHA-1".equals(hashFunction)){
			resultBytes = new byte[HMAC_SHA_LENGTH]; // The final output result will be stored in this byte array
		}

		byte sharedSecretBytes[] = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // the shared secret bytes will be stored here

		byte[] ipad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];
		byte[] opad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];

		byte[] ipadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing ipad bytes with sharedSecret bytes 
		byte[] opadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing opad bytes with sharedSecret bytes

		try{

			MessageDigest md5MessageDigest = (MessageDigest)MessageDigest.getInstance(hashFunction).clone();

			Arrays.fill(ipad,(byte)0x36); // fills the inner pad byte array with 0x36 ( 54 in Decimal )
			Arrays.fill(opad,(byte)0x5c); // fills the outer pad byte array with 0x5c ( 92 in Decimal )

			if(sharedSecret.length < HMAC_SHARED_SECRET_MAX_LENGTH){ // checks if the length of the sharedSecret is less than 64
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0, sharedSecret.length); // copies the bytes from shared secret in sharedSecretBytes
				Arrays.fill(sharedSecretBytes, sharedSecret.length, sharedSecretBytes.length - 1,(byte)0x00); // pads the rest of the bytes with 0x00 ( 0 in Decimal )

			}else if(sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH){ // checks if the length of the sharedSecret is more than 64
				md5MessageDigest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				sharedSecretBytes = md5MessageDigest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				Arrays.fill(sharedSecretBytes, sharedSecret.length, sharedSecretBytes.length - 1,(byte)0x00);
				//printBytes("SharedSecret Bytes",sharedSecretBytes);
			}else{
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0, sharedSecret.length);
			}

			for(int i = 0; i < sharedSecretBytes.length ; i++){
				ipadXORSharedSecret[i] = (byte)((sharedSecretBytes[i] & 0xFF) ^ (ipad[i] & 0xFF)); // XORing the sharedSecretBytes with the ipad bytes
				opadXORSharedSecret[i] = (byte)((sharedSecretBytes[i] & 0xFF)^ (opad[i] & 0xFF));  // XORing the sharedSecretBytes with the opad bytes
			}

			md5MessageDigest.update(ipadXORSharedSecret);
			md5MessageDigest.update(dataToBeEncrypted);
			byte[] tempResult = md5MessageDigest.digest(); // generating the encrypted value of the bytes set
			md5MessageDigest.update(opadXORSharedSecret);
			md5MessageDigest.update(tempResult);
			resultBytes = md5MessageDigest.digest();

		}catch(NoSuchAlgorithmException noSuchAlgorithmException){

		}catch(CloneNotSupportedException cns){

		}

		return resultBytes;
	}

	public static byte[] encryptPasswordRFC2865(final String actualPassword, 
			byte[] requestAuthenticator, 
			final String strSharedSecret){
		
		
		byte[] encryptedPasswordBytes =null;
		
		if(actualPassword!=null){
			
			byte[] actualPasswordBytes = null;
			try{
				actualPasswordBytes = actualPassword.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				actualPasswordBytes = actualPassword.getBytes();
			}
			int pswdLength = actualPasswordBytes.length;
			byte[] paddedActualPassword = null;
			if(pswdLength == 0)
				paddedActualPassword = new byte[16];
			else if(pswdLength % 16 != 0)
				paddedActualPassword = new byte[((pswdLength/16)+1)*16];
			else
				paddedActualPassword = new byte[(pswdLength/16)*16];
			System.arraycopy(actualPasswordBytes, 0, paddedActualPassword, 0, pswdLength);
			encryptedPasswordBytes = new byte[paddedActualPassword.length];
			byte[] bytesToBeXORed = new byte[16];
			MessageDigest msgDigest = null;
			msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			try{
				msgDigest.update(strSharedSecret.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				msgDigest.update(strSharedSecret.getBytes());
			}
			msgDigest.update(requestAuthenticator);
			bytesToBeXORed = msgDigest.digest();
			for(int i=0; i<(paddedActualPassword.length/16); i++){
				byte[] tempBytes = new byte[16];
				for(int j=0; j<16; j++){
					encryptedPasswordBytes[(i*16)+j] = (byte)(paddedActualPassword[(i*16)+j] ^ bytesToBeXORed[j]);
					tempBytes[j] = (byte)(paddedActualPassword[(i*16)+j] ^ bytesToBeXORed[j]);
				}
				msgDigest.reset();
				try{
					msgDigest.update(strSharedSecret.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					msgDigest.update(strSharedSecret.getBytes());
				}
				msgDigest.update(tempBytes);
				bytesToBeXORed = msgDigest.digest();
			}
			
		}


		return encryptedPasswordBytes;
	} 

	public static String decryptPasswordRFC2865(byte[] encryptedPasswordBytes, 
			byte[] requestAuthenticator, 
			final String sharedSecret){
		MessageDigest msgDigest = null;

		msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);

		byte[] actualPasswordBytes = null;
		List<byte[]> byteChunks = getByteChunks(encryptedPasswordBytes, 16);
		int numberOfChunks = byteChunks.size();
		actualPasswordBytes = new byte[numberOfChunks*16];
		for(int i=(numberOfChunks-1); i>=0; i--){
			byte[] tempChunk = (byte[])byteChunks.get(i);
			byte[] bytesToBeXORed = null;
			if(i == 0){
				msgDigest.reset();
				try{
					msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					msgDigest.update(sharedSecret.getBytes());
				}
				msgDigest.update(requestAuthenticator);
				bytesToBeXORed = msgDigest.digest();
				for(int j=(tempChunk.length-1); j>=0; j--){
					actualPasswordBytes[j] = (byte)(bytesToBeXORed[j] ^ tempChunk[j]);
				}
			}else{
				msgDigest.reset();
				try{
					msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					msgDigest.update(sharedSecret.getBytes());
				}
				msgDigest.update((byte[])byteChunks.get(i-1));
				bytesToBeXORed = msgDigest.digest();
				for(int j=(tempChunk.length-1); j>=0; j--){
					actualPasswordBytes[(i*16)+j] = (byte)(bytesToBeXORed[j] ^ tempChunk[j]);
				}
			}
		}
		String actualPassword;

		try{
			actualPassword = new String(actualPasswordBytes,CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			actualPassword =  new String(actualPasswordBytes);
		}
		int i = actualPassword.indexOf('\0');
		if(i!=-1)
			actualPassword = actualPassword.substring(0,i);

		return actualPassword;
	}

	public static List<byte[]> getByteChunks(byte[] bytes, int chunkSize){
		List<byte[]> byteChunks = new ArrayList<byte[]>();
		int noOfChunks = (bytes.length / chunkSize);

		if(noOfChunks >= 1){
			if( bytes.length % chunkSize == 0 ){
				for(int i=1, j=0; i<=noOfChunks && j<bytes.length ; i++, j+=chunkSize){
					byte[] tempBytes = new byte[chunkSize];
					System.arraycopy(bytes, j, tempBytes, 0, chunkSize);
					byteChunks.add(tempBytes);
				}
			}else{
				for(int i=1, j=0; i<=(noOfChunks+1) && j<bytes.length; i++, j+=chunkSize){
					if(i==noOfChunks+1){
						byte[] lastBytes = new byte[bytes.length % chunkSize];
						System.arraycopy(bytes, j, lastBytes, 0, (bytes.length % chunkSize));
						byteChunks.add(lastBytes);
					}else{
						byte[] tempBytes = new byte[chunkSize];
						System.arraycopy(bytes, j, tempBytes, 0, chunkSize);
						byteChunks.add(tempBytes);
					}
				}
			}

		}else{
			if(bytes.length != 0){
				byteChunks.add(bytes);
			}
		}
		return byteChunks;
	}

	public static boolean isByteArraySame(byte[] bytes1, byte[] bytes2){
		if(bytes1.length == bytes2.length){
			for(int i=0; i<bytes2.length; i++){
				if(bytes1[i] != bytes2[i]){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	public static String getBytesAsString(String title, byte[] bytesToPrint){
		StringWriter strWriter = new StringWriter();
		PrintWriter out = new PrintWriter(strWriter);
		out.print(title);
		out.print("-->");
		for (int i = 0; i < bytesToPrint.length; i++) {
			byte b = bytesToPrint[i];
			out.print(Integer.toHexString((b & 0xFF)));
			out.print(" ");
		}
		out.flush();
		out.close();
		return strWriter.toString();
	}


	//----------------------------------------------------------------------------

	public static String bytesToHex(byte buf[]){
		if(buf == null)
			return "";
		else
			return bytesToHex(buf, 0, buf.length);
	}

	public static String bytesToHex(byte buf[], int offset, int limit){
		char[] hexbuf = new char[(((limit - offset)*2) + 2)];
		hexbuf[0]='0';
		hexbuf[1]='x';
		for(int i = offset,k=2; i < limit; i++){
			hexbuf[k++] = (HEX[buf[i] >> 4 & 0xf]);
			hexbuf[k++] = (HEX[buf[i] & 0xf]);
		}
		return String.valueOf(hexbuf);
	}

	private static final char HEX[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f'
	};

	//----------------------------------------------------------------------------
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

	/*	public static void main(String args[]) {
		byte [] requestAuth1 = {0x72,(byte)0x94, 0x2a, (byte)0xd4, 0x59, (byte)0xef, 0x51, (byte)0xcb, 0x43, 0x23, 0x22, 0x1b, (byte)0xb4, 0x33, 0x02, 0x22};
		byte [] requestAuth2 = {(byte)0xc3,(byte)0x83,0x01,0x1f,(byte)0x8f,(byte)0xfb,0x4d,0x59,(byte)0xcb,0x6d,(byte)0xf0,(byte)0xa1,(byte)0xe3,0x7b,0x37,(byte)0xaf};
		byte [] requestAuth3 = {0x1f,(byte)0xa2,0x3a,(byte)0x84,0x2d,(byte)0xa2,(byte)0xbd,(byte)0xb3,0x4d,0x2a,0x2d,0x41,(byte)0xd8,(byte)0xf8,0x39,(byte)0xa9};
		byte [] encryptedPass1 = {(byte)0xc0,0x15,0x27,0x44,(byte)0xa8,(byte)0xdc,(byte)0x8b,0x73,0x63,0x2d,(byte)0x84,(byte)0x9a,(byte)0x83,0x5c,0x4b,(byte)0xc8};
		byte [] encryptedPass2 = {(byte)0x90,0x3b,0x08,0x58,(byte)0xf8,(byte)0xfb,(byte)0xc5,(byte)0x89,(byte)0xe8,0x36,(byte)0xf8,(byte)0xc4,0x43,(byte)0xbd,0x5d,0x3c};
		byte [] encryptedPass3 = {(byte)0xfb,0x07,(byte)0x95,(byte)0xd2,0x19,0x56,(byte)0x8f,0x43,0x69,(byte)0xb8,0x6f,0x7b,(byte)0x9e,0x47,0x51,(byte)0xd5};
		System.out.println("Check Password : " + decryptPasswordRFC2865(encryptedPass3,requestAuth3,"localkey"));
	}
	 */
	public static byte[] encryptKeyRFC2868(byte[] key, String sharedSecret, byte[] requestAuthenticator, byte[] saltBytes){
		byte[] encrptedKey = null;		
		byte[] plainText = null;

		if((key.length+1) % 16 != 0){
			plainText = new byte[(((1 + key.length)/16)+1) * 16];		
			encrptedKey = new byte[plainText.length];
		}else{
			plainText = new byte[((1 + key.length)/16)*16];	
			encrptedKey = new byte[plainText.length];
		}		

		plainText[0] = (byte)key.length;
		System.arraycopy(key, 0, plainText, 1, key.length);
		if((key.length+1) % 16 != 0){
			Arrays.fill(plainText, key.length+1, plainText.length-1, (byte)0);
		}			

		byte[][] plainKeyBlocks = new byte[plainText.length/16][16];
		byte[][] intermediateValues = new byte[plainText.length/16][16];
		byte[][] cipherBlocks = new byte[plainText.length/16][16];

		int itr = plainText.length/16;
		MessageDigest msgDigest = null;

		for(int i=0; i<itr; i++){
			System.arraycopy(plainText, i*16, plainKeyBlocks[i], 0, 16);		
			//We know these exceptions will never hit
			msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			try{
				msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				msgDigest.update(sharedSecret.getBytes());
			}
			if(i == 0){
				msgDigest.update(requestAuthenticator);
				msgDigest.update(saltBytes);
			}else{				
				msgDigest.update(cipherBlocks[i-1]);	
			}
			intermediateValues[i] = msgDigest.digest();
			for(int j=0; j<16; j++){
				cipherBlocks[i][j] = (byte)(plainKeyBlocks[i][j] ^ intermediateValues[i][j]);
			}

		}

		for(int i=0; i<itr; i++){
			System.arraycopy(cipherBlocks[i], 0, encrptedKey, i*16, cipherBlocks[i].length);
		}

		return encrptedKey;
	}		

	public static byte[] decryptKeyRFC2868(byte[] encKey, String sharedSecret, byte[] requestAuthenticator, byte[] saltBytes){
		byte[] plainText = null;

		byte[][] plainKeyBlocks = new byte[encKey.length/16][16];
		byte[][] intermediateValues = new byte[encKey.length/16][16];
		byte[][] cipherBlocks = new byte[encKey.length/16][16];

		int itr = encKey.length/16;
		MessageDigest msgDigest = null;

		for(int i=0; i<itr; i++){
			System.arraycopy(encKey, i*16, cipherBlocks[i], 0, 16);
			msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			try{
				msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				msgDigest.update(sharedSecret.getBytes());
			}
			if(i == 0){				
				msgDigest.update(requestAuthenticator);
				msgDigest.update(saltBytes);																		
			}else{
				msgDigest.update(cipherBlocks[i-1]);																	
			}
			intermediateValues[i] = msgDigest.digest();
			for(int j=0; j<16; j++){
				plainKeyBlocks[i][j] = (byte)(cipherBlocks[i][j] ^ intermediateValues[i][j]);
			}
		}
		plainText = new byte[(encKey.length/16)*16];
		for(int i=0; i<itr; i++){
			System.arraycopy(plainKeyBlocks[i], 0, plainText, i*16, plainKeyBlocks[i].length);
		}

		byte[] decryptedKey = new byte[plainText[0]];

		//		System.out.println("decrypted plain text: " + TLSUtility.bytesToHex(plainText));
		System.arraycopy(plainText, 1, decryptedKey, 0, plainText[0]);		
		return decryptedKey;
	}

	public static byte[] getBytesFromHexValue(String strValue){
		byte[] valueBytes = null;
		String hexValue = strValue.substring(2);
		if(hexValue.length() % 2 == 0) {
			int len  = hexValue.length()/2;
			valueBytes = new byte[len];
			for (int i = 0; i < len; i++) {
				valueBytes[i] = (byte) Integer.parseInt(hexValue.substring(2*i, 2*i+2), 16);
			}
		}
		return valueBytes;
	}

	public static RadiusPacket applyRFC4849NasFilterRule(RadiusPacket radiusResponsePacket){

		Collection<IRadiusAttribute> radiusAttributes = radiusResponsePacket.getRadiusAttributes();
		if(radiusAttributes == null)
			return radiusResponsePacket;
		RadiusPacket responsePacket = new RadiusPacket();
		responsePacket.setIdentifier(radiusResponsePacket.getIdentifier());
		responsePacket.setPacketType(radiusResponsePacket.getPacketType());
		responsePacket.setAuthenticator(radiusResponsePacket.getAuthenticator());
		Iterator<IRadiusAttribute> itrAttr = radiusAttributes.iterator();
		StringBuffer nasFilterRuleValue = new StringBuffer();
		while(itrAttr.hasNext()){
			IRadiusAttribute radiusAttr =  itrAttr.next();			
			if(radiusAttr.getType() == RadiusAttributeConstants.NAS_FILTER_RULE){
				if(nasFilterRuleValue.length() == 0){
					nasFilterRuleValue.append(radiusAttr.getStringValue());
				}else{
					nasFilterRuleValue.append('\0');
					nasFilterRuleValue.append(radiusAttr.getStringValue());
				}
				itrAttr.remove();
			}else if(radiusAttr.getType() == RadiusAttributeConstants.FILTER_ID){				
				itrAttr.remove();
			}
		}		
		responsePacket.addAttributes(radiusAttributes);
		Collection<byte[]> radiusAttrInResponse = RadiusUtility.getByteChunks(nasFilterRuleValue.toString().getBytes(), 253); 
		Iterator<byte[]> radiusAttrItr = radiusAttrInResponse.iterator();
		while(radiusAttrItr.hasNext()){
			IRadiusAttribute radiusAttrAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_FILTER_RULE);
			radiusAttrAttribute.setValueBytes((byte[])radiusAttrItr.next());			
			responsePacket.addAttribute(radiusAttrAttribute);
		}
		responsePacket.refreshPacketHeader();
		return responsePacket;
	}

	public static void main(String args[]){
		System.out.println(getBytesAsString("Check : " , "02".getBytes()));
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(RadiusUtility.class);
		
	}

	/**   
	 *    @return     integer Array 
	 *    @arguments  strAttributeId
	 * 	  For getting the AttributesId from the String of AttributeIds
	 */

	public static int[] getAttributeIds(String strAttributeId) throws NumberFormatException {
		int ids[] = null;
		if(strAttributeId.contains(":")){
			String strIds[] = strAttributeId.split(":");
			ids = new int[strIds.length];
			for(int i=0; i<strIds.length; i++){
				ids[i] = Integer.parseInt(strIds[i ]);
			}
		}else{
			ids = new int[2];
			ids[0] = 0;
			ids[1] = Integer.parseInt(strAttributeId);
		}
		return ids;
	}

	public static boolean checkInRadiusPacket(IRadiusPacket radiusPacket,String attributeId,String value){
		boolean result = false;
		ArrayList <IRadiusAttribute>attributeList = null;
		attributeList =(ArrayList<IRadiusAttribute>) radiusPacket.getRadiusAttributes(attributeId,true);
		if(attributeList == null){
			return false;
		}else{
			final int listSize = attributeList.size();
			for(int i=0;i<listSize;i++) {
				IRadiusAttribute radiusAttribute = attributeList.get(i);
				if(value.contains("*")|| value.contains("?"))
					result = radiusAttribute.patternCompare(value);
				else
					result = radiusAttribute.stringCompare(value);
				if(result)
					break;
			}
		}
		return result;
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

	/**
	 * @deprecated This method can convert hex string to bytes when hex string starts with 0x (small x) but do not work with capital x. Use getBytesFromHex() method instead.
	 * 
	 * @param data
	 * @return
	 */
	@Deprecated 
	public static final byte[] HexToBytes(String data){
		if(data == null)
			return null;
		String rawData = data;
		if(data.charAt(1) == 'x')
			rawData = data.substring(2);		
		int len = rawData.length();
		if(len % 2 != 0)
			len ++ ;		
		byte[] returnBytes = new byte[len/2];
		for(int i=0 ; i<len-1; ){
			returnBytes[i/2] = (byte) (HexToByte(rawData.substring(i, i+2)) & 0xFF);			
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


	public static MessageDigest getMessageDigest(String algorithm){
		MessageDigest msgDigest = null;
		try {
			try {
				msgDigest = (MessageDigest)MessageDigest.getInstance(algorithm,CommonConstants.SUN).clone();
			} catch (NoSuchProviderException e) {
				msgDigest = (MessageDigest)MessageDigest.getInstance(algorithm).clone();
			}
		} catch (NoSuchAlgorithmException e) {			
			LogManager.getLogger().trace(MODULE, e);
		} catch (CloneNotSupportedException e) {		
			LogManager.getLogger().trace(MODULE, e);
		}
		return msgDigest;
	}

	public static int getSalt(){
		if(salt < 65535)
			salt++;
		else
			salt=32768;
		return salt;
	}

	public static boolean isOurRealm(String nai,List<String> realmList){

		/*
		1----split the our realm part
		2----check if valid according to the ABNF
		3----if it is our realm then return true*/

		String[] lst = nai.split("@");
		if(lst.length > 2){
			return false;
		}
		String realm = lst[lst.length-1];
		if(!isValidRealmAccordingToABNF(realm)){
			return false;
		}
		return realmList.contains(realm);

	}

	public static String getProxyRealm(String nai){
		if(nai.contains("!")){
			String otherRealm = nai.substring(0, nai.indexOf('!'));
			return otherRealm;
		}else{
			return "";
		}

	}

	public static boolean isValidRealmAccordingToABNF(String realm){
		return(realmRegx.matcher(realm).matches());
	}

	public static String transformNAI(String originalNAI, List<String> realmNames) throws MalformedNAIException{
		StringBuffer buffer = new StringBuffer(); 
		String otherRealm =  originalNAI.substring(originalNAI.lastIndexOf("@")+1, originalNAI.length());
		int startIndex = 0;
		String[] tokens = originalNAI.split("!");
		for(int i=0;i<tokens.length-1;i++){
			if(realmNames.contains(tokens[i])){
				startIndex += tokens[i].length() +1;
			}else{
				if(!(tokens[i].trim().length() == 0)){
					startIndex += tokens[i].length()+1;
					otherRealm = tokens[i];
					if(!isValidRealmAccordingToABNF(otherRealm)){
						throw new  MalformedNAIException("Realm Name : " + otherRealm + " is Invalid according to RFC 4282");
					}
					break;
				}else{
					for(int j=i;j<tokens.length-1;j++){
						if(!(tokens[j].trim().length() == 0)){
							throw new MalformedNAIException("NAI is malformed.");
						}
					}
				}
			}
		}
		buffer.append(originalNAI.substring(startIndex , originalNAI.lastIndexOf('@')+1) + otherRealm);
		return new String(buffer);
	}

	public static boolean isValidUserAccordingToABNF(String nai){
		String user = nai;
		if(nai.contains("@")){
			user = nai.substring(0,nai.indexOf('@'));
		}
		//TODO the check for the backslash is remaining
		return (userNameRegx.matcher(user).matches());
	}

	public static boolean isNAIDecorated(String nai){

		return(nai.contains("@"));
	}

	public static boolean isValidForProxy(String nai){
		return(nai.contains("!") && !(nai.indexOf('!') == 0));		
	}

	
	public static boolean checkRealms(List<String> realmNames, String nai){
		String realm = nai.substring(nai.indexOf('@')+1,nai.length());
		return(realmNames.contains(realm));
	}
	
	public static String getActualUserName(String data){				
		
		int indexOfBrace = data.lastIndexOf('}');
		int index = data.lastIndexOf("@");
		
		if(data.startsWith("{")){
			if(indexOfBrace >= 0){
				if(index >= 0){
					return data.substring(indexOfBrace + 1,index);
				}else{
					return data.substring(indexOfBrace + 1,data.length());
				}				
			}else{
				return data.substring(0,index);
			}			
		}else{
			return data.substring(0,index);
		}						
	}
	
	public static String getRealm(String data){	
		int index = data.lastIndexOf("@");
		if(index >= 0){
			return data.substring(index+1,data.length());
		}
		return null;
	}
	
	public static String getDecoration(String data){		
		int indexOfOpeningBrace = data.indexOf('{');
		int indexOfClosingBrace = data.lastIndexOf('}');
		
		
		if(indexOfOpeningBrace == 0){
			if(indexOfClosingBrace >= 0){
				return data.substring(indexOfOpeningBrace + 1,indexOfClosingBrace);
			}else{
				return null;
			}			
		}else{
			return null;
		}						
	}
	
	public static Long bytesToLong(byte[] bytes){
		if(bytes == null)
			return null;
		
		Long val = 0L;
		for(byte b : bytes){
			val <<= 8;
			val |= b & 0xFF;
		}
		
		return val;
	}
	
	/**
	 * Used to generate Message Authenticator for <b>Radius Packet</b>. The packet MUST be the final packet with all
	 * the required attributes. If any of the attributes are added after calling this method then the value of
	 * Message Authenticator will be invalidated and will be needed to calculate again.
	 * For Further information on Message Authenticator Attribute
	 * <br/>
	 * <b>RFC 3579</b>
	 * <br/>
	 * {@link http://tools.ietf.org/html/rfc3579#section-3.2}
	 * 
	 * @param packetBytes the packet bytes for which the Authenticator is to be calculated. <b>NOTE: These bytes must include Message-Authenticator(0:80) attribute.</b>
	 * @param authenticator bytes to be used to calculate Message Authenticator
	 * @param sharedSecret the shared secret
	 * @return message authenticator bytes that need to be set in Message-Authenticator (0:80) attribute. If packet bytes do not contain Message-Authenticator(0:80) attribute bytes then it returns <b>null</b> otherwise.
	 * <br>If any of the arguments to this method are <code>null</code>, then it returns <code>null</code>.
	 */
	public static byte[] generateMessageAuthenticator(byte[] packetBytes, byte[] authenticator, String sharedSecret){
		byte[] calculatedMsgAuthenticatorBytes = null;
		
		if(packetBytes == null || authenticator == null || sharedSecret == null){
			return calculatedMsgAuthenticatorBytes;
		}
		
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setBytes(packetBytes);
		radiusPacket.refreshPacketHeader();
		
		IRadiusAttribute msgAuthenticatorAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticatorAttr != null){
			msgAuthenticatorAttr.setValueBytes(new byte[16]);
			radiusPacket.setAuthenticator(authenticator);
			radiusPacket.refreshPacketHeader();
			calculatedMsgAuthenticatorBytes = HMAC(CommonConstants.MD5, radiusPacket.getBytes(), sharedSecret);
		}
		
		return calculatedMsgAuthenticatorBytes;
	}
	
	/**
	 * Used to validate the message authenticator received in <b>Radius Packet</b>.
	 * <br/>
	 * <b>RFC 3579</b>
	 * <br/>
	 * {@link http://tools.ietf.org/html/rfc3579#section-3.2}
	 * @param packetBytes the untampered or unchanged packet bytes received
	 * @param msgAuthenticator the message authenticator bytes with which the value is to be validated. Usually the value bytes of Message-Authenticator(0:80) attribute.
	 * @param sharedSecret the shared secret between Radius and client
	 * @return <code>true</code> if the message authenticator is validated as per RFC standard, returns <code>false</code> otherwise.
	 * <br>If any of the arguments to this method are <code>null</code>, then it returns <code>false</code>.  
	 */
	public static boolean validateMessageAuthenticator(byte[] packetBytes, byte[] authenticator, byte[] msgAuthenticator, String sharedSecret){
		boolean isValid = false;
		
		if(packetBytes == null || authenticator == null || msgAuthenticator == null || sharedSecret == null){
			return isValid;
		}
		
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setBytes(packetBytes);
		
		IRadiusAttribute msgAuthenticatorAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticatorAttr != null){
			msgAuthenticatorAttr.setValueBytes(new byte[16]);
			radiusPacket.setAuthenticator(authenticator);
			radiusPacket.refreshPacketHeader();
			
			byte[] calculatedMsgAuthenticatorBytes = HMAC(CommonConstants.MD5, radiusPacket.getBytes(), sharedSecret);
			isValid = Arrays.equals(calculatedMsgAuthenticatorBytes, msgAuthenticator);
		}
		
		return isValid;
	}

	/**
	 * Extracting key from the given key value pair. It will return the left side of equal to sign (value).
	 * It is expected that key value pair is separated by equal to sign.
	 * For Example,
	 * <pre>
	 * String keyValuePair = "abc=123";
	 * String key = extractKey(keyValuePair);
	 * </pre>
	 * In this example, <i>key</i> will be <i>abc</i>
	 * 
	 * @param value - key value pair
	 * @return key from key value pair
	 * @exception IllegalArgumentException - if value passed is null, empty, value not separated by equal to sign
	 * 										 or value begins or ends with equal to sign 
	 */
	public static String extractKey(String value){
		checkArgument(!isNullOrEmpty(value), "value passed is null or empty");
	
		int indexOfEquals = value.indexOf("=");
		if(indexOfEquals == -1)
			throw new IllegalArgumentException("Invalid value : " + value + " received, key value pair must be separated by =");
		
		if(indexOfEquals == 0 || indexOfEquals == value.length()-1) {
			throw new IllegalArgumentException("Invalid value : " + value + " received, Reason: value must not begin or end with =");
		}
		
		return value.substring(0,indexOfEquals).trim();
	}
	
	/**
	 * Extracting value from the given key value pair. It will return the string that is at the right side of the equal to sign (value).
	 * It is expected that key value pair is separated with equal to sign.
	 * If the string contains more than one equal to sign then first "=" will be considered as separator 
	 * and after that everything will be considered as value including "="
	 * For Example,
	 * <pre>
	 * String keyValuePair = "abc=123";
	 * String value = extractKey(keyValuePair);
	 * </pre>
	 * In this example, <i>value</i> will be <i>123</i>
	 * 
	 * @param key value pair
	 * @return value of the key from key value pair
	 * @exception IllegalArgumentException - if value passed is null, empty, value not separated by equal to sign
	 * 										 or value begins or ends with equal to sign
	 */
	public static String extractValue(String value){
		checkArgument(!isNullOrEmpty(value), "value passed is null or empty");
		
		int indexOfEquals = value.indexOf("=");
		if(indexOfEquals == -1)
			throw new IllegalArgumentException("Invalid value : " + value + " received, Reason: value must contains =");
		
		if(indexOfEquals == 0 || indexOfEquals == value.length()-1) {
			throw new IllegalArgumentException("Invalid value : " + value + " received, Reason: value must not begin or end with =");
		}
		
		return value.substring(indexOfEquals + 1).trim();
	}
	
	/**
	 * Checks that whether the value starts with <b><i>0x</i></b> or <b><i>0X</i></b> 
	 * and then decide that the value is hexadecimal or not.
	 * For example,
	 * <pre>
	 * boolean result1 = isHex("0x01F8");
	 * boolean result2 = isHex("01F8");
	 * </pre>
	 * Here, <i>result1</i> will be true and <i>result2</i> will be false.
	 * 
	 * @param value in hexadecimal
	 * @return true if value is hexadecimal otherwise false
	 * @throws NullPointerException - if value is null
	 * 
	 */
	public static boolean isHex(String value) {
		checkNotNull(value, "value passed is null");
		return value.regionMatches(IGNORE_CASE, START_OF_STRING, HEX_INITIAL, START_OF_STRING, HEX_INITIAL.length());
	}
	
	/**
	 * Checks whether the value passed in the parameter is null or empty.<br>
	 * Note: This method will trim the value and then check for its emptiness
	 * 
	 * @param value - string to be checked 
	 * @return true - if value is null or empty<br>
	 * 		   false - if value is neither null nor empty
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value == null || value.trim().isEmpty());
	}
	
	/**
	 * Checks whether the bytes passed in the parameter is null or empty.
	 * Empty means zero length byte array.
	 * 
	 * @param bytes - byte[] to be checked
	 * @return true - if bytes are null or empty<br>
	 * 		   false - if bytes are neither null nor empty
	 */
	public static boolean isNullOrEmpty(byte[] bytes) {
		return (bytes == null || bytes.length == 0);
	}
	
	/**
	 * Reads the exactly specified number of bytes from the given input stream and then convert them to long value
	 * 
	 * @param inputStream - source stream
	 * @param noOfBytes - number of bytes to be read from the input stream
	 * @return long value generated from bytes from the input stream
	 * @throws IOException - if input stream does not contain sufficient bytes.<br>
	 * @throws NullPointerException - if inputStream passed in the parameter is null.<br>
	 * @throws IllegalArgumentException - if number of bytes do not fall in closed range of 1 to 8.
	 * 
	 */
	public static long readLong(InputStream inputStream, int noOfBytes) throws IOException{
		checkNotNull(inputStream, "Input Stream passed is null");
		checkArgument(noOfBytes >= 1 && noOfBytes <= 8, "No of bytes should fall in [1-8]");
		
		byte[] buffer = new byte[noOfBytes];
		int noOfBytesRead = inputStream.read(buffer);
		if(noOfBytesRead < noOfBytes)
			throw new IOException("Sufficient bytes unavailable in stream");
		
		long value = 0;
		for(int i = 0; i < noOfBytes; i++){
			value = value << 8 | (buffer[i] & 0xFF);
		}
		
		return value;
	}
	
	/**
	 * Reads the given number of bytes from the specified input stream
	 * 
	 * @param inputStream - to read bytes from
	 * @param noOfBytes - Number of bytes need to read from input stream
	 * @return byte array of size no of bytes specified 
	 * @throws IOException - if no of bytes passed in the argument is greater than bytes available to read in the input stream.<br>
	 * @throws IllegalArgumentException - if input stream passed in the parameter is null 
	 * 									  or if no of bytes is less than zero (exclusive)
	 */
	public static byte[] readBytes(InputStream inputStream, int noOfBytes) throws IOException{
		checkNotNull(inputStream, "Input Stream passed is null");
		checkArgument(noOfBytes >= 0 , "No of bytes should be more than or equal to zero");
		
		byte[] buffer = new byte[noOfBytes];
		int noOfBytesRead = inputStream.read(buffer);
		if(noOfBytesRead < noOfBytes)
			throw new IOException("Sufficient bytes unavailable in stream");
		
		return buffer;
	}
	
	/**
	 * Convert the long value into byte array of exactly specified number of bytes.<br>
	 * Note: value will be considered as signed and not unsigned.
	 * 
	 * @param value - it is of type long from which bytes are generated
	 * @param noOfBytes - Number of bytes to specify the size of the bytes to be returned
	 * @return byte array of size no of bytes specified from the value
	 */
	public static byte[] toByteArray(long value, int noOfBytes){
		checkArgument(noOfBytes >= 1 && noOfBytes <= 8, "No of bytes should fall in [1-8]");

		byte[] longBytes = new byte[noOfBytes];
		for (int i = noOfBytes-1; i >= 0; i--, value >>>= 8)
			longBytes[i] = (byte)value;
		return longBytes;
	}
	
	/**
	 * Convert the integer value into byte array of exactly specified number of bytes.<br>
	 * Note: value will be considered as signed and not unsigned.
	 * 
	 * @param value - integer value 
	 * @param noOfBytes - Number of bytes to specify the size of the bytes to be returned
	 * @return byte array of size no of bytes specified from the value
	 */
	public static byte[] toByteArray(int value, int noOfBytes){
		checkArgument(noOfBytes >= 1 && noOfBytes <= 4, "No of bytes should fall in [1-4]");
		
		byte[] intBytes = new byte[noOfBytes];
		for (int i = noOfBytes-1; i >= 0; i--, value >>>= 8)
			intBytes[i] = (byte)value;
		return intBytes;
	}
	
	public static <T> T checkNotNull(T reference, String message){
		if(reference == null)
			throw new NullPointerException(message);
		
		return reference;
	}
	
	public static void checkArgument(boolean argument, String message){
		if(!argument)
			throw new IllegalArgumentException(message);
	}
	
	public static int readInt(InputStream inputStream, int noOfBytes) throws IOException{
		checkNotNull(inputStream, "Input Stream passed is null");
		checkArgument(noOfBytes >= 1 && noOfBytes <= 4, "No of bytes should fall in [1-4]");
		
		byte[] buffer = new byte[noOfBytes];
		int noOfBytesRead = inputStream.read(buffer);
		if(noOfBytesRead < noOfBytes)
			throw new IOException("Sufficient bytes unavailable in stream");
		
		int value = 0;
		for(int i = 0; i < noOfBytes; i++){
			value = value << 8 | (buffer[i] & 0xFF);
		}
		
		return value;
	}
	
	public static void writeBytesSilently(ByteArrayOutputStream out, byte... bytesToWrite){
		try {
			out.write(bytesToWrite);
		} catch (IOException e) {
			throw new IllegalStateException(e);
			//This state MUST never occur
		}
	}
	
	 /**
	  * Taken from StringUtility of core lib
	  * @param <T> 
	  * @param input List of objects of type T (T must override <code>toString()</code>)
	  * @param delim delimiter with which the elements are to be separated
	  * @return Single string separated with delimiter if input is not null, returns blank string otherwise
	  */
	public static final <T> String getDelimiterSeparatedString(List<T> input,String delim){
		StringBuilder builder = new StringBuilder();
		if(input != null && input.size() > 0){
			final int size = input.size();
			for(int i=0;i<size - 1;i++){
				builder.append(String.valueOf(input.get(i))).append(delim);
			}
			builder.append(String.valueOf(input.get(size - 1)));
		}		
		return builder.toString();
	}
	
	
	public static class TabbedPrintWriter extends PrintWriter{
		private int presentTabCount = 0;
		
		public TabbedPrintWriter(Writer out) {
			super(out);
		}
		
		public void incrementIndentation(){
			presentTabCount++;
		}
		
		public void decrementIndentation(){
			presentTabCount--;
		}
		
		@Override
		public void println(String str) {
			super.println(getTabs(presentTabCount) + str);
		}
		
		private String getTabs(int tabCount){
			StringBuffer stringBuffer = new StringBuffer();
			for(int i=0;i<tabCount;i++){
				stringBuffer.append("\t");
			}
			return stringBuffer.toString();
		}
	}
	
	
}
