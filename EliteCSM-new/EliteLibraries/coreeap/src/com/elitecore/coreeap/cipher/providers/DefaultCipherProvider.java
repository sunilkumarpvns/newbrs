package com.elitecore.coreeap.cipher.providers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.TLSAlertException;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertDescConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertLevelConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class DefaultCipherProvider implements ICipherProvider{
	private static final String MODULE = "DEFAULT_CIPHER_PROVIDER";
	private CipherType cipherType;
	private CipherSuites cipher;
	private ProtocolVersion protocolVersion;
	private boolean bIsTestMode;
	
	public DefaultCipherProvider(CipherSuites cipherSuite, ProtocolVersion protocolVersion) {
		this.cipher = cipherSuite;
		this.protocolVersion = protocolVersion;
		if(CommonConstants.BLOCK_CIPHER == cipherSuite.cipherType) {
			cipherType = new BlockCipher();
		} else if (CommonConstants.STREAM_CIPHER == cipherSuite.cipherType){
			cipherType = new StreamCipher();
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Cipher type not supported: " + cipherSuite.cipherType);
		}
	}
	
	public DefaultCipherProvider(CipherSuites cipherSuite, ProtocolVersion protocolVersion, boolean bIsTestMode) {
		this.bIsTestMode = bIsTestMode;
		this.cipher = cipherSuite;
		this.protocolVersion = protocolVersion;
		if(CommonConstants.BLOCK_CIPHER == cipherSuite.cipherType) {
			cipherType = new BlockCipher();
		} else if (CommonConstants.STREAM_CIPHER == cipherSuite.cipherType){
			cipherType = new StreamCipher();
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Cipher type not supported: " + cipherSuite.cipherType);
		}
	}

	@Override
	public byte[] decrypt(byte[] encData, SecretKey encReadKey,	byte[] clientWriteIV) {
		return cipherType.decrypt(encData, encReadKey, clientWriteIV);
	}

	@Override
	public byte[] encrypt(byte[] plainData, SecretKey encWriteKey, byte[] serverWriteIV) {
		return cipherType.encrypt(plainData, encWriteKey, serverWriteIV);
	}
	
	@Override
	public SecretKey generateKeyFromBytes(byte[] keyBytes) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Generating a Secretkey using TLS Cipher suite : " + cipher);
		if(keyBytes == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Keybytes is null ");
		}
		if(cipher.cipherAlgo == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Cipher Algorithm is null ");	
		}
		SecretKey secretKey = new SecretKeySpec(keyBytes, cipher.cipherAlgo);
		return secretKey;
	}
	
	@Override
	public int getHashSize() {
		return cipher.hashSize;
	}
	
	@Override
	public int getBlockSize() {
		return cipher.blockSize;
	}

	/**
	 *  Method returns Hash Algorithm value used for Keys Generation from Master Secret.
	 *  For Key generation in TLSv1.2 (RFC 5246) MD5/SHA-1 Algorithms are replaced by SHA-256
	 *  or greater Algorithms. So it returns Algorithm accordingly. 
	 *  <h3>Reference</h3> RFC 5246 Section 1.2 1st point 
	 *  
	 * 	@param protocolVersion take minor protocol version as <code>int</code>
	 */
	@Override
	public String getHashAlgo() {
		if(protocolVersion == ProtocolVersion.TLS1_2 && (cipher.getHashAlgorithm() == HashAlgorithm.MD5 || cipher.getHashAlgorithm() == HashAlgorithm.SHA1)){
			return HashAlgorithm.SHA256.getIdentifier();
		}else{
			return cipher.getHashAlgorithm().getIdentifier();
		}
	}
	
	/**
	 *  Method returns MAC Algorithm specified by respected Cipher Suite.
	 *  
	 *  <h3>Reference</h3> RFC 5246 Section 5
	 */
	@Override
	public String getMACAlgo(){
		return cipher.getHashAlgorithm().getIdentifier();
	}
	
	@Override
	public int getIVSize() {
		return cipher.IVSize;
	}

	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(MODULE);
		return strBuilder.toString();		
	}
	
	@Override
	public int getKeyBlockSize() {
		return cipher.keyBlockSize;
	}
	
	@Override
	public int getKeyMaterialSize() {
		return cipher.keyMaterialSize;
	}
	
	@Override
	public boolean isSupported(int value){
		return CipherSuites.isSupported(value, protocolVersion);
	}
	
	
	
	
	/*-------------------------------------------Cipher Types Begin Here-----------------------------------------------------------*/
	
	
	public interface CipherType {

		public byte[] decrypt(byte[] eDataBytes, SecretKey encryptionReadKey, byte[] clientWriteIV) throws TLSAlertException;
		public byte[] encrypt(byte[] plainData, SecretKey encryptionWriteKey, byte[] serverWriteIV) throws TLSAlertException;
	}

	/**
	 * 	This class is not Thread Safe.
	 *	@author elitecore
	 *
	 */
	public class StreamCipher implements CipherType {
		
		private static final String MODULE = "STREAM_CIPHER";
		
		private Cipher cipherInstanceDec;
		private Cipher cipherInstanceEnc;
		
		@Override
		public byte[] decrypt(byte[] encData, SecretKey encReadKey, byte[] clientWriteIV) throws TLSAlertException {
			
			/***
			 * Here is the core decryption logic.
			 */
			byte[] decryptedData = null;
		
			try {
				if(cipherInstanceDec == null){
					cipherInstanceDec = Cipher.getInstance(cipher.cipherString);
					cipherInstanceDec.init(Cipher.DECRYPT_MODE, encReadKey);
				}
				decryptedData = cipherInstanceDec.update(encData);
			} catch (NoSuchAlgorithmException nsae) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such algorithm exception : " + nsae.getMessage());
				LogManager.getLogger().trace(MODULE, nsae);
			} catch (NoSuchPaddingException nspe) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such padding exception: " + nspe.getMessage());
				LogManager.getLogger().trace(MODULE, nspe);
			} catch (InvalidKeyException ike) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid key exception: " + ike.getMessage());
				LogManager.getLogger().trace(MODULE, ike);
			} 	
			return decryptedData;
			
		}
		
		@Override
		public byte[] encrypt(byte[] plainData, SecretKey encWriteKey, byte[] serverWriteIV) throws TLSAlertException  {
			/***
			 * Here is the core encryption logic.
			 */
			byte[] encryptedData = null;
			
			try {
				if(cipherInstanceEnc == null){
					cipherInstanceEnc = Cipher.getInstance(cipher.cipherString);
					cipherInstanceEnc.init(Cipher.ENCRYPT_MODE, encWriteKey);
				}
				encryptedData = cipherInstanceEnc.update(plainData);
				
			} catch (NoSuchAlgorithmException nsae) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such algorithm exception : " + nsae.getMessage());
				LogManager.getLogger().trace(MODULE, nsae);
			} catch (NoSuchPaddingException nspe) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such padding exception: " + nspe.getMessage());
				LogManager.getLogger().trace(MODULE, nspe);
			} catch (InvalidKeyException ike) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid key exception: " + ike.getMessage());
				LogManager.getLogger().trace(MODULE, ike);
			} 	
			return encryptedData;
		}
		
	}

	public class BlockCipher implements CipherType {
		
		private static final String MODULE = "BLOCK_CIPHER";
		private static final int SIZE_OF_PADDING_LENGTH = 1;
		
		private SecureRandom secureRandom = new SecureRandom();
		
		public byte[] decrypt(byte[] eDataBytes, SecretKey encryptionReadKey, byte[] clientWriteIV) throws TLSAlertException {
			/***
			 * dDataBytes contain the decrypted data , including original data,MAC and padding
			 */
			byte[] dDataBytes = decrypt2(eDataBytes, encryptionReadKey, clientWriteIV);
			
			if(dDataBytes == null){
				//this case will occur if there is some exception in the decryption of the bytes
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
			}
			
			byte[] dDataBytesWithoutIV = dDataBytes;
			if(protocolVersion.isGreater(ProtocolVersion.TLS1_0)) {
				/**
				 * According to RFC 4346 (<a href="http://tools.ietf.org/html/rfc4346#section-6.2.3.2">Section 6.2.3.2</a>) 
				 * for TLSv1.1 or higher, The prepended IV is discarded
				 * 
				 */
				dDataBytesWithoutIV = removeIV(dDataBytes);
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Decrypted data bytes : " + Utility.bytesToHex(dDataBytes));

			return removePadding(dDataBytesWithoutIV);
		}

		public byte[] encrypt(byte[] plainData, SecretKey encryptionWriteKey, byte[] serverWriteIV) throws TLSAlertException  {
			
			byte[] encInput = addPadding(plainData);
			/**
			 * According to RFC for TLSv1.1, Prepending per record IV (RANDOM) to the TLSPlainText before encryption
			 */
			byte[] perRecordIV = serverWriteIV;
			if(protocolVersion.isGreater(ProtocolVersion.TLS1_0)) {
				encInput = addIV(encInput);
				perRecordIV = TLSUtility.getFixedMask(cipher.IVSize);
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Final input for encryption : " + Utility.bytesToHex(encInput));
			return encrypt2(encInput, encryptionWriteKey, perRecordIV);
		}
		
		private byte[] decrypt2(byte[] encData,SecretKey encReadKey,byte[] clientWriteIV){
			
			/***
			 * Here is the core decryption logic.
			 */
			byte[] decryptedData = null;
			
			try {
				IvParameterSpec ivParameterSpec = new IvParameterSpec(clientWriteIV.clone());			
				Cipher cipherInstance = Cipher.getInstance(cipher.cipherString);
				cipherInstance.init(Cipher.DECRYPT_MODE, encReadKey, ivParameterSpec);
				
				decryptedData = cipherInstance.doFinal(encData);
			} catch (NoSuchAlgorithmException nsae) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such algorithm exception : " + nsae.getMessage());
				LogManager.getLogger().trace(MODULE, nsae);
			} catch (NoSuchPaddingException nspe) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such padding exception: " + nspe.getMessage());
				LogManager.getLogger().trace(MODULE, nspe);
			} catch (InvalidKeyException ike) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid key exception: " + ike.getMessage());
				LogManager.getLogger().trace(MODULE, ike);
			} catch (IllegalBlockSizeException ibse) {		
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))	
					LogManager.getLogger().trace(MODULE, "Illegal block size exception: " + ibse.getMessage());
				LogManager.getLogger().trace(MODULE, ibse);
			} catch (BadPaddingException bpe) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Bad padding exception: " + bpe.getMessage());
				LogManager.getLogger().trace(MODULE, bpe);
			} catch (InvalidAlgorithmParameterException iape) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid Algorithm Paramter exception : " + iape.getMessage());
				LogManager.getLogger().trace(MODULE, iape);
			} 		
			return decryptedData;
			
		}
		
		private byte[] encrypt2(byte[] plainData,SecretKey encWriteKey,byte[] serverWriteIV){
			/***
			 * Here is the core encryption logic.
			 */
			byte[] encryptedData = null;
			
			try {
				IvParameterSpec ivParameterSpec = new IvParameterSpec(serverWriteIV.clone());			
				Cipher cipherInstance = Cipher.getInstance(cipher.cipherString);			
				cipherInstance.init(Cipher.ENCRYPT_MODE, encWriteKey, ivParameterSpec);
				
				encryptedData = cipherInstance.doFinal(plainData);
			} catch (NoSuchAlgorithmException nsae) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such algorithm exception : " + nsae.getMessage());
				LogManager.getLogger().trace(MODULE, nsae);
			} catch (NoSuchPaddingException nspe) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No such padding exception: " + nspe.getMessage());
				LogManager.getLogger().trace(MODULE, nspe);
			} catch (InvalidKeyException ike) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid key exception: " + ike.getMessage());
				LogManager.getLogger().trace(MODULE, ike);
			} catch (IllegalBlockSizeException ibse) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Illegal block size exception: " + ibse.getMessage());
				LogManager.getLogger().trace(MODULE, ibse);
			} catch (BadPaddingException bpe) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Bad padding exception: " + bpe.getMessage());
				LogManager.getLogger().trace(MODULE, bpe);
			} catch (InvalidAlgorithmParameterException iape) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Invalid Algorithm Paramter exception : " + iape.getMessage());
				LogManager.getLogger().trace(MODULE, iape);
			} 		
			return encryptedData;
		}

		/**
		 * According to RFC 4346 (<a href="http://tools.ietf.org/html/rfc4346#section-6.2.3.2">Section 6.2.3.2</a>) 
		 * for TLSv1.1 or higher, The prepended IV is discarded
		 * 
		 */
		private byte[] removeIV(byte[] dDataBytes){
			return Arrays.copyOfRange(dDataBytes, cipher.IVSize, dDataBytes.length);
		}
		
		/**
		 * According to RFC 4346 (<a href="http://tools.ietf.org/html/rfc4346#section-6.2.3.2">Section 6.2.3.2</a>) 
		 * for TLSv1.1 or higher, The prepended IV is discarded
		 * 
		 */
		private byte[] addIV(byte[] plainData){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Encrypting TLS record using TLS version 1.1");
			
			byte[] plainDataWithIV = new byte[cipher.IVSize + plainData.length];
			byte[] iv = new byte[cipher.IVSize];
			if(!isTestMode()){
				secureRandom.nextBytes(iv);
			}
			System.arraycopy(iv, 0, plainDataWithIV, 0, iv.length);
			System.arraycopy(plainData, 0, plainDataWithIV, iv.length, plainData.length);
			return plainDataWithIV;
		}
		
		private byte[] removePadding(byte[] dDataBytesWithoutIV){
			int paddingLength = dDataBytesWithoutIV[dDataBytesWithoutIV.length-1] & 0xFF;
			/**
			 * According to RFC for TLSv1.1, if Padding error occurs then, no need to generate alert while checking padding error,
			 * instead BAD_RECORD_MAC alert is generated after MAC is generated.
			 */
			if(!validatePadding(dDataBytesWithoutIV, paddingLength)) {
				if(protocolVersion == ProtocolVersion.TLS1_0){
					throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
				} else if(protocolVersion.isGreater(ProtocolVersion.TLS1_0)) {
					/*
					 * TLS 1.1 implementation note on handling padding errors
					 * For instance, if the pad appears to be incorrect, the implementation might assume a zero-length pad
					 * and then compute the MAC.
					 */
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
						LogManager.getLogger().trace(MODULE, "Padding Length received is invalid. ");
					}
					paddingLength = 0;	
				}
			}
			
			return Arrays.copyOfRange(dDataBytesWithoutIV, 0, dDataBytesWithoutIV.length - paddingLength - SIZE_OF_PADDING_LENGTH); 
		}
		
		private byte[] addPadding(byte[] plainData){
			int paddingLength = cipher.blockSize - (plainData.length % cipher.blockSize); 
			byte[] plainDataWithPadding = new byte[plainData.length + paddingLength];
			System.arraycopy(plainData, 0, plainDataWithPadding, 0, plainData.length);
			/**
			 * 	Adding padding
			 */
			Arrays.fill(plainDataWithPadding, plainData.length, plainDataWithPadding.length, (byte)(paddingLength -1));
			return plainDataWithPadding;
		}
		
		public boolean validatePadding(byte[] dDataBytesWithoutIV, int paddingLength) {
			
			/*
			 *  This is first line of defense.
			 *  This checks that padding length is proper or not.
			 *  If a decryption goes wrong and padding length is greater than 
			 *  	array size this will return false.
			 *  
			 */
			if(dDataBytesWithoutIV.length < (paddingLength + SIZE_OF_PADDING_LENGTH)) {
				return false;
			}
			/*
			 *  This second line of defense.
			 *  This checks that padding is proper or not by 
			 *  	comparing first & middle bytes of array 
			 *  	with padding length.
			 *  According to specification all bytes in padding is same as padding length. 
			 * 
			 */
			if((dDataBytesWithoutIV[dDataBytesWithoutIV.length - paddingLength -1] != paddingLength) ||
					(dDataBytesWithoutIV[dDataBytesWithoutIV.length - (paddingLength + 2)/2] != paddingLength)){
				return false;
			}
			
			return true;
		}
	}
	
	private boolean isTestMode(){
		return bIsTestMode;
	}
}

