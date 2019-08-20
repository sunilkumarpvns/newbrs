package com.elitecore.coreeap.util.sim.gsm;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class GsmAes extends BaseGSMA3A8 {

	public void configure(String s) {
	}

	public byte[] compute(byte rand[], byte key[])
			throws NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchPaddingException, ShortBufferException {
		byte output[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(1, keySpec);
		cipher.update(rand, 0, 16, output);		
		System.arraycopy(output,8, output,4, 8);
		return output;
	}
	
	public Object clone() throws CloneNotSupportedException {
		GsmAes gsmAes = null;
		gsmAes = (GsmAes) super.clone();
		return gsmAes;
	}
}
