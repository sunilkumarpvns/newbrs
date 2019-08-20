package com.elitecore.aaa.core.eap.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.TemporaryIdentityGenerator;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class TemporaryIdentityGeneratorImpl implements TemporaryIdentityGenerator {

	private static final String MODULE = "TEMP-ID-GNRTR";
	public TemporaryIdentityGeneratorImpl() {
	}

	@Override
	public String encodeTemporaryIdentity(String permIdentity, int method, String prefix, boolean hexEncoding) {
		if (method == BASIC_ALPHA_1){
			try {
				String encodedIdentity = generateBasicAlphaOne(permIdentity, prefix);
				if (hexEncoding){
					encodedIdentity = RadiusUtility.bytesToHex(encodedIdentity.getBytes());
					if (encodedIdentity.length() > 0){
						encodedIdentity = encodedIdentity.substring(2);
					}
				}
				return prefix +encodedIdentity;
			} catch (IOException e){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to generate temporary Identity Using BASIC_ALPHA_1 method. Reason: " + e.getMessage());
			}
		} else {
			try {
				String encodedIdentity = PasswordEncryption.getInstance().crypt(permIdentity, method); 
				if (hexEncoding){
					encodedIdentity = RadiusUtility.bytesToHex(encodedIdentity.getBytes());
					if (encodedIdentity.length() > 0){
						encodedIdentity = encodedIdentity.substring(2);
					}
				}
				return prefix + encodedIdentity;
			} catch (NoSuchEncryptionException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Method(" + method+ ") chosen for generating temporary identity");
			} catch (EncryptionFailedException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Invalid Method(" + method+ ") chosen for generating temporary identity");
				}
			}
		}
		return null;
	}

	private String generateBasicAlphaOne(String permId, String prefix) throws IOException {
		Random random = new Random();
		byte[] byt = new byte[1];
		random.nextBytes(byt);
		ByteArrayOutputStream pseudo = new ByteArrayOutputStream();
		pseudo.write(RadiusUtility.encryptPasswordRFC2865(permId, byt, prefix));
		while(pseudo.size() % 4 != 0){
			pseudo.write(255);
		}
		String strPseudo = Utility.bytesToHex(pseudo.toByteArray());
		
		return  Utility.bytesToHex(byt).substring(2) + strPseudo.substring(2);
	}

	private String decodeBasicAlphaOne(String tempID, String prefix){
		byte[] random = RadiusUtility.HexToBytes(tempID.substring(0,2)); 
		byte[] tempIDBytes = RadiusUtility.HexToBytes(tempID.substring(2));
		return RadiusUtility.decryptPasswordRFC2865(tempIDBytes, random, prefix);	
	}
	@Override
	public String decodeTemporaryIdentity(String tempIdentity, int method, String prefix, boolean hexEncoding) {
		if (method == BASIC_ALPHA_1){
			if (hexEncoding){
				tempIdentity = String.valueOf(RadiusUtility.HexToBytes(tempIdentity.substring(prefix.length()-1)));
			}
			return decodeBasicAlphaOne(tempIdentity, prefix);
		} else {
			try {
				if (hexEncoding){
					tempIdentity = String.valueOf(RadiusUtility.HexToBytes(tempIdentity.substring(prefix.length()-1)));
				}
				String decodedIdentity = PasswordEncryption.getInstance().decrypt(tempIdentity, method); 
				return decodedIdentity;
			} catch (NoSuchEncryptionException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Method(" + method+ ") chosen for generating temporary identity");
			} catch (DecryptionNotSupportedException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Method: " + method + " does not support decryption");
				}
			} catch (DecryptionFailedException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Method: " + method + " does not support decryption");
			}
		}
		return null;
	}

}
