package com.elitecore.coreeap.util.sim.gsm;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class GsmMilenageOpc2 extends BaseGSMA3A8 {
	byte m_opc[];

	public void configure(String config)
			throws InvalidAlgorithmParameterException {
		if (config == null || (config.length() != 32 && config.length() != 34))
			throw new InvalidAlgorithmParameterException("Configuration data (op_c) must be 32 hexadecimal characters");
		m_opc = HexToBytes(config);
	}

	public byte[] compute(byte rand[], byte key[])
			throws NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchPaddingException, ShortBufferException {
		byte output[] = new byte[12];
		byte res[] = new byte[8];
		byte ck[] = new byte[16];
		byte ik[] = new byte[16];
		byte ak[] = new byte[6];
		byte temp[] = new byte[16];
		byte out[] = new byte[16];
		byte rijndaelInput[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(1, keySpec);
		for (int i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (rand[i] ^ m_opc[i]);
		}
		cipher.update(rijndaelInput, 0, 16, temp);
		for (int i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (temp[i] ^ m_opc[i]);
		}
		rijndaelInput[15] ^= 1;
		cipher.update(rijndaelInput, 0, 16, out);
		for (int i = 0; i < 16; i++){
			out[i] ^= m_opc[i];
		}
		System.arraycopy(out, 8, res, 0, 8);
		System.arraycopy(out, 0, ak, 0, 6);
		for (int i = 0; i < 16; i++){
			rijndaelInput[(i + 12) % 16] = (byte) (temp[i] ^ m_opc[i]);
		}
		rijndaelInput[15] ^= 2;
		cipher.update(rijndaelInput, 0, 16, out);
		for (int i = 0; i < 16; i++){
			out[i] ^= m_opc[i];
		}
		System.arraycopy(out, 0, ck, 0, 16);
		for (int i = 0; i < 16; i++){
			rijndaelInput[(i + 8) % 16] = (byte) (temp[i] ^ m_opc[i]);
		}
		rijndaelInput[15] ^= 4;
		cipher.update(rijndaelInput, 0, 16, out);
		for (int i = 0; i < 16; i++){
			out[i] ^= m_opc[i];
		}
		System.arraycopy(out, 0, ik, 0, 16);
		System.arraycopy(res, 0, output, 0, 4);
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
		GsmMilenageOpc2 gsmMilenageOpc2 = null;
		gsmMilenageOpc2 = (GsmMilenageOpc2) super.clone();
		if(m_opc != null){
			gsmMilenageOpc2.m_opc = new byte[m_opc.length];
			System.arraycopy(m_opc, 0, gsmMilenageOpc2.m_opc, 0, m_opc.length);
		}
		return gsmMilenageOpc2;
	}

}
