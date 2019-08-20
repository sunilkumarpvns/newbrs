package com.elitecore.coreeap.data.sim.gsm;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class Milenage implements Cloneable{
	private byte m_op[] = { 99, -65, -91, 14, -26, 82, 51, 101, -1, 20, -63,
			-12, 95, -120, 115, 125 };

	public Milenage() {

	}

	public Milenage(byte op[]) {
		System.arraycopy(op, 0, m_op, 0, 16);
	}

	public void f1(byte k[], byte rand[], byte sqn[], byte amf[], byte mac_a[])
			throws NoSuchProviderException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, ShortBufferException {
		byte op_c[] = new byte[16];
		byte temp[] = new byte[16];
		byte in1[] = new byte[16];
		byte out1[] = new byte[16];
		byte rijndaelInput[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
		cipher.init(1, keySpec);
		cipher.update(m_op, 0, 16, op_c);
		for (byte i = 0; i < 16; i++){
			op_c[i] ^= m_op[i];
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (rand[i] ^ op_c[i]);
		}
		cipher.update(rijndaelInput, 0, 16, temp);
		for (byte i = 0; i < 6; i++) {
			in1[i] = sqn[i];
			in1[i + 8] = sqn[i];
		}

		for (byte i = 0; i < 2; i++) {
			in1[i + 6] = amf[i];
			in1[i + 14] = amf[i];
		}

		for (byte i = 0; i < 16; i++){
			rijndaelInput[(i + 8) % 16] = (byte) (in1[i] ^ op_c[i]);
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] ^= temp[i];
		}
		cipher.update(rijndaelInput, 0, 16, out1);
		for (byte i = 0; i < 16; i++){
			out1[i] ^= op_c[i];
		}
		System.arraycopy(out1, 0, mac_a, 0, 8);
	}

	public void f2345(byte k[], byte rand[], byte res[], byte ck[], byte ik[],byte ak[]) 
	        throws NoSuchProviderException,NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, ShortBufferException {
		byte op_c[] = new byte[16];
		byte temp[] = new byte[16];
		byte out[] = new byte[16];
		byte rijndaelInput[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
		cipher.init(1, keySpec);
		cipher.update(m_op, 0, 16, op_c);
		for (byte i = 0; i < 16; i++){
			op_c[i] ^= m_op[i];
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (rand[i] ^ op_c[i]);
		}
		cipher.update(rijndaelInput, 0, 16, temp);
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (temp[i] ^ op_c[i]);
		}
		rijndaelInput[15] ^= 1;
		cipher.update(rijndaelInput, 0, 16, out);
		for (byte i = 0; i < 16; i++){
			out[i] ^= op_c[i];
		}
		System.arraycopy(out, 8, res, 0, 8);
		
		System.arraycopy(out, 0, ak, 0, 6);
		
		for (byte i = 0; i < 16; i++){
			rijndaelInput[(i + 12) % 16] = (byte) (temp[i] ^ op_c[i]);
		}
		rijndaelInput[15] ^= 2;
		cipher.update(rijndaelInput, 0, 16, out);

		for (byte i = 0; i < 16; i++){
			out[i] ^= op_c[i];
		}
		System.arraycopy(out, 0, ck, 0, 16);

		for (byte i = 0; i < 16; i++){
			rijndaelInput[(i + 8) % 16] = (byte) (temp[i] ^ op_c[i]);
		}
		rijndaelInput[15] ^= 4;
		cipher.update(rijndaelInput, 0, 16, out);
		for (byte i = 0; i < 16; i++){
			out[i] ^= op_c[i];
		}
		System.arraycopy(out, 0, ik, 0, 16);
	}

	public void f1star(byte k[], byte rand[], byte sqn[], byte amf[],byte mac_s[]) 
	        throws NoSuchProviderException,NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, ShortBufferException {
		byte op_c[] = new byte[16];
		byte temp[] = new byte[16];
		byte in1[] = new byte[16];
		byte out1[] = new byte[16];
		byte rijndaelInput[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
		cipher.init(1, keySpec);
		cipher.update(m_op, 0, 16, op_c);
		for (byte i = 0; i < 16; i++){
			op_c[i] ^= m_op[i];
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (rand[i] ^ op_c[i]);
		}
		cipher.update(rijndaelInput, 0, 16, temp);
		for (byte i = 0; i < 6; i++) {
			in1[i] = sqn[i];
			in1[i + 8] = sqn[i];
		}

		for (byte i = 0; i < 2; i++) {
			in1[i + 6] = amf[i];
			in1[i + 14] = amf[i];
		}

		for (byte i = 0; i < 16; i++){
			rijndaelInput[(i + 8) % 16] = (byte) (in1[i] ^ op_c[i]);
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] ^= temp[i];
		}

		cipher.update(rijndaelInput, 0, 16, out1);
		for (byte i = 0; i < 16; i++){
			out1[i] ^= op_c[i];
		}
		System.arraycopy(out1, 8, mac_s, 0, 8);
	}

	public void f5star(byte k[], byte rand[], byte ak[])
			throws NoSuchProviderException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, ShortBufferException {
		byte op_c[] = new byte[16];
		byte temp[] = new byte[16];
		byte out[] = new byte[16];
		byte rijndaelInput[] = new byte[16];
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
		cipher.init(1, keySpec);
		cipher.update(m_op, 0, 16, op_c);
		for (byte i = 0; i < 16; i++){
			op_c[i] ^= m_op[i];
		}
		for (byte i = 0; i < 16; i++){
			rijndaelInput[i] = (byte) (rand[i] ^ op_c[i]);
		}
		cipher.update(rijndaelInput, 0, 16, temp);
		for (byte i = 0; i < 16; i++){
			rijndaelInput[(i + 4) % 16] = (byte) (temp[i] ^ op_c[i]);
		}
		rijndaelInput[15] ^= 8;
		cipher.update(rijndaelInput, 0, 16, temp);
		for (byte i = 0; i < 16; i++){
			out[i] ^= op_c[i];
		}
		System.arraycopy(out, 0,ak, 0, 6);
	}
	
	public Object clone() throws CloneNotSupportedException {
		Milenage milenage = new Milenage(m_op);
		return milenage;
	}
}