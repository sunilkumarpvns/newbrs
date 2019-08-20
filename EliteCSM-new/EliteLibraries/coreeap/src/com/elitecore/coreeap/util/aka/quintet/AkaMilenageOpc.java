package com.elitecore.coreeap.util.aka.quintet;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.coreeap.util.tls.TLSUtility;

public class AkaMilenageOpc extends BaseAkaQuintet {
	
	private byte m_opc[] = null;
	
	@Override
	public void configure(String config) throws InvalidAlgorithmParameterException {
		if ((config == null) || (config.length() == 0)) {
			return;
		}
		if (config.length() != 32 && config.length() != 34) {
			throw new InvalidAlgorithmParameterException("Configuration data OP must be 32 hexadecimal characters");
		}
		m_opc = TLSUtility.HexToBytes(config);
	}
	
	@Override
	public void f1(byte[] k, byte[] rand, byte[] sqn, byte[] amf, byte[] mac_a)
			throws GeneralSecurityException {	
        byte temp[] = new byte[16];
        byte in1[] = new byte[16];
        byte out1[] = new byte[16];
        byte rijndaelInput[] = new byte[16];
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
        cipher.init(1, keySpec);
        for(int i = 0; i < 16; i++)
            rijndaelInput[i] = (byte)(rand[i] ^ m_opc[i]);

        cipher.update(rijndaelInput, 0, 16, temp);
        for(int i = 0; i < 6; i++)
        {
            in1[i] = sqn[i];
            in1[i + 8] = sqn[i];
        }

        for(int i = 0; i < 2; i++)
        {
            in1[i + 6] = amf[i];
            in1[i + 14] = amf[i];
        }

        for(int i = 0; i < 16; i++)
            rijndaelInput[(i + 8) % 16] = (byte)(in1[i] ^ m_opc[i]);

        for(int i = 0; i < 16; i++)
            rijndaelInput[i] ^= temp[i];

        cipher.update(rijndaelInput, 0, 16, out1);
        for(int i = 0; i < 16; i++)
            out1[i] ^= m_opc[i];

        System.arraycopy(out1, 0, mac_a, 0, 8);
	}

	@Override
	public void f1star(byte[] k, byte[] rand, byte[] sqn, byte[] amf,
			byte[] mac_s) throws GeneralSecurityException {
        byte temp[] = new byte[16];
        byte in1[] = new byte[16];
        byte out1[] = new byte[16];
        byte rijndaelInput[] = new byte[16];
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
        cipher.init(1, keySpec);
        for(int i = 0; i < 16; i++)
            rijndaelInput[i] = (byte)(rand[i] ^ m_opc[i]);

        cipher.update(rijndaelInput, 0, 16, temp);
        for(int i = 0; i < 6; i++)
        {
            in1[i] = sqn[i];
            in1[i + 8] = sqn[i];
        }

        for(int i = 0; i < 2; i++)
        {
            in1[i + 6] = amf[i];
            in1[i + 14] = amf[i];
        }

        for(int i = 0; i < 16; i++)
            rijndaelInput[(i + 8) % 16] = (byte)(in1[i] ^ m_opc[i]);

        for(int i = 0; i < 16; i++)
            rijndaelInput[i] ^= temp[i];

        cipher.update(rijndaelInput, 0, 16, out1);
        for(int i = 0; i < 16; i++)
            out1[i] ^= m_opc[i];

        System.arraycopy(out1, 8, mac_s, 0, 8);		
	}

	@Override
	public byte[] f2345(byte[] key, byte[] rand, byte[] ck, byte[] ik, byte[] ak)
			throws GeneralSecurityException {
        byte xres[] = new byte[8];
        byte temp[] = new byte[16];
        byte out[] = new byte[16];
        byte rijndaelInput[] = new byte[16];
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(1, keySpec);
        for(int i = 0; i < 16; i++)
            rijndaelInput[i] = (byte)(rand[i] ^ m_opc[i]);

        cipher.update(rijndaelInput, 0, 16, temp);
        for(int i = 0; i < 16; i++)
            rijndaelInput[i] = (byte)(temp[i] ^ m_opc[i]);

        rijndaelInput[15] ^= 1;
        cipher.update(rijndaelInput, 0, 16, out);
        for(int i = 0; i < 16; i++)
            out[i] ^= m_opc[i];

        System.arraycopy(out, 8, xres, 0, 8);
        System.arraycopy(out, 0, ak, 0, 6);
        for(int i = 0; i < 16; i++)
            rijndaelInput[(i + 12) % 16] = (byte)(temp[i] ^ m_opc[i]);

        rijndaelInput[15] ^= 2;
        cipher.update(rijndaelInput, 0, 16, out);
        for(int i = 0; i < 16; i++)
            out[i] ^= m_opc[i];

        System.arraycopy(out, 0, ck, 0, 16);
        for(int i = 0; i < 16; i++)
            rijndaelInput[(i + 8) % 16] = (byte)(temp[i] ^ m_opc[i]);

        rijndaelInput[15] ^= 4;
        cipher.update(rijndaelInput, 0, 16, out);
        for(int i = 0; i < 16; i++)
            out[i] ^= m_opc[i];

        System.arraycopy(out, 0, ik, 0, 16);
        return xres;
	}

	@Override
	public void f5star(byte[] k, byte[] rand, byte[] ak)
			throws GeneralSecurityException {
        byte temp[] = new byte[16];
        byte out[] = new byte[16];
        byte rijndaelInput[] = new byte[16];
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(k, "AES");
        cipher.init(1, keySpec);
        for(int i = 0; i < 16; i++)
            rijndaelInput[i] = (byte)(rand[i] ^ m_opc[i]);

        cipher.update(rijndaelInput, 0, 16, temp);
        for(int i = 0; i < 16; i++)
            rijndaelInput[(i + 4) % 16] = (byte)(temp[i] ^ m_opc[i]);

        rijndaelInput[15] ^= 8;
        cipher.update(rijndaelInput, 0, 16, out);
        for(int i = 0; i < 16; i++)
            out[i] ^= m_opc[i];

        System.arraycopy(out, 0, ak, 0, 6);		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AkaMilenageOpc akaMilenageOpc = null;
		akaMilenageOpc = (AkaMilenageOpc)super.clone();
		if(m_opc != null){
			akaMilenageOpc.m_opc = new byte[m_opc.length];
			System.arraycopy(m_opc, 0, akaMilenageOpc.m_opc, 0, m_opc.length);
		}
		return akaMilenageOpc;
	}
}
