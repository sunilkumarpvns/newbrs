package com.elitecore.coreeap.util.tls.keyexchange;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;


public class RSAKeyExchange implements KeyExchange {
	
	private static final String MODULE = "RSA_KEY_EXCHANGE";
	
	private PrivateKey privateKey;
	public RSAKeyExchange(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	@Override
	public byte[] generateParameters() {
		throw new UnsupportedOperationException("RSA Key Exchange does not support generating key exchange parameters");
	}

	@Override
	public byte[] generatePMS(byte[] keyExchangeValue) {
		byte[] decryptedPms = null;
		try {		
			Cipher cipher = Cipher.getInstance(CommonConstants.RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(keyExchangeValue);
			decryptedPms = cipher.doFinal();
			
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
		}
		return decryptedPms;
	}
}
