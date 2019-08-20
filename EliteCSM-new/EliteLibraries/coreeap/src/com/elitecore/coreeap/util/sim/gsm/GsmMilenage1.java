package com.elitecore.coreeap.util.sim.gsm;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import com.elitecore.coreeap.data.sim.gsm.Milenage;

public class GsmMilenage1 extends BaseGSMA3A8 {
	Milenage milenage;

	public void configure(String config) throws InvalidAlgorithmParameterException {
		if (config == null || config.length() == 0) {
			milenage = new Milenage();
			return;
		}
		if (config.length() != 32 && config.length() !=34)
			throw new InvalidAlgorithmParameterException("Configuration data OP must be 32 hexadecimal characters");
		byte[] op = HexToBytes(config);
		milenage = new Milenage(op);
	}

	public byte[] compute(byte rand[], byte key[])
			throws NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchPaddingException, ShortBufferException {
		if (milenage == null)
			milenage = new Milenage();

		byte output[] = new byte[12];
		byte res[] = new byte[8];
		byte ck[] = new byte[16];
		byte ik[] = new byte[16];
		byte ak[] = new byte[6];
		milenage.f2345(key, rand, res, ck, ik, ak);
		for (int i = 0; i < 4; i++){
			output[i] = (byte) (res[i] ^ res[i + 4]);
		}
		for (int i = 0; i < 8; i++){
			output[i + 4] = (byte) (ck[i] ^ ck[i + 8] ^ ik[i] ^ ik[i + 8]);
		}
		return output;
	}

	protected static final byte[] HexToBytes(String data) {
		if (data.charAt(1) == 'x')
			data = data.substring(2);
		int len = data.length();
		if (len % 2 != 0)
			len++;
		byte[] returnBytes = new byte[len / 2];
		for (int i = 0; i < len - 1;) {
			returnBytes[i / 2] = (byte) (HexToByte(data.substring(i, i + 2)) & 0xFF);

			i += 2;
		}
		return returnBytes;
	}

	protected static final int HexToByte(String data) {
		int byteVal = toByte(data.charAt(0)) & 0xFF;
		byteVal = byteVal << 4;
		byteVal = byteVal | toByte(data.charAt(1));
		return byteVal;
	}

	protected static final int toByte(char ch) {
		if ((ch >= '0') && (ch <= '9')) {
			return ch - 48;
		} else if ((ch >= 'A') && (ch <= 'F')) {
			return ch - 65 + 10;
		} else if ((ch >= 'a') && (ch <= 'f')) {
			return ch - 97 + 10;
		} else {
			return 0;
		}
	}

	public Object clone() throws CloneNotSupportedException {
		GsmMilenage1 gsmMilenage1 = null;
		gsmMilenage1 = (GsmMilenage1) super.clone();
		if(milenage != null){
			gsmMilenage1.milenage = (Milenage) milenage.clone(); 
		}
		return gsmMilenage1;
	}
}
