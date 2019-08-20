package com.elitecore.coreeap.commons.util.cipher;

import javax.crypto.SecretKey;

/**
 *	All the providers implementing this interface will have SESSION SCOPE.
 * 
 * @author elitecore
 *
 */

public interface ICipherProvider {
	public byte[] decrypt(byte[] encData,SecretKey encReadKey,byte[] clientWriteIV);
	public byte[] encrypt(byte[] plainData,SecretKey encWriteKey, byte[] serverWriteIV);
	public SecretKey generateKeyFromBytes(byte[] keyBytes);
	public int getHashSize();
	public String getHashAlgo();
	public String getMACAlgo();
	public int getBlockSize();
	public int getIVSize();
	public int getKeyBlockSize(); 
	public int getKeyMaterialSize();
	public boolean isSupported(int value);
}
