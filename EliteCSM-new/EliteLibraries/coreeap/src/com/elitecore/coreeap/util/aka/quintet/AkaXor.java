package com.elitecore.coreeap.util.aka.quintet;

import java.security.GeneralSecurityException;

public class AkaXor extends BaseAkaQuintet {

	@Override
	public void f1(byte[] k, byte[] rand, byte[] sqn, byte[] amf, byte[] mac_a)
			throws GeneralSecurityException {
        byte xdout[] = new byte[16];
        for(int i = 0; i < 16; i++)
            xdout[i] = (byte)(k[i] ^ rand[i]);

        byte cdout[] = new byte[8];
        System.arraycopy(sqn, 0, cdout, 0, 6);
        cdout[6] = amf[0];
        cdout[7] = amf[1];
        for(int i = 0; i < 8; i++)
            mac_a[i] = (byte)(xdout[i] ^ cdout[i]);
		
	}

	@Override
	public void f1star(byte[] k, byte[] rand, byte[] sqn, byte[] amf,
			byte[] mac_s) throws GeneralSecurityException {
        byte xdout[] = new byte[16];
        for(int i = 0; i < 16; i++)
            xdout[i] = (byte)(k[i] ^ rand[i]);

        byte cdout[] = new byte[8];
        System.arraycopy(sqn, 0, cdout, 0, 6);
        cdout[6] = amf[0];
        cdout[7] = amf[1];
        for(int i = 0; i < 8; i++)
            mac_s[i] = (byte)(xdout[i] ^ cdout[i]);		
	}

	@Override
	public byte[] f2345(byte[] key, byte[] rand, byte[] ck, byte[] ik, byte[] ak)
			throws GeneralSecurityException {
        byte xdout[] = new byte[16];
        byte xres[] = new byte[16];
        for(int i = 0; i < 16; i++)
        {
            byte temp = (byte)(key[i] ^ rand[i]);
            xdout[i] = temp;
            xres[i] = temp;
            ck[(i + 15) % 16] = temp;
            ik[(i + 14) % 16] = temp;
            if(i > 2 && i < 9)
                ak[i - 3] = temp;
        }

        return xres;
	}

	@Override
	public void f5star(byte[] k, byte[] rand, byte[] ak)
			throws GeneralSecurityException {
        for(int i = 3; i < 9; i++)
            ak[i - 3] = (byte)(k[i] ^ rand[i]);		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AkaXor akaXor = null;
		akaXor = (AkaXor)super.clone();		
		return akaXor;
	}
}
