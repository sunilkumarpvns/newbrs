package com.elitecore.coreeap.util.aka.quintet;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

import com.elitecore.coreeap.data.sim.gsm.Milenage;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AkaMilenage extends BaseAkaQuintet {

	Milenage milenage;

	public void configure(String config) throws InvalidAlgorithmParameterException {
		if ((config == null) || (config.length() == 0)) {
			this.milenage = new Milenage();
			return;
		}
		if (config.length() != 32 && config.length() != 34) {
			throw new InvalidAlgorithmParameterException("Configuration data OP must be 32 hexadecimal characters");
		}
		byte[] op = TLSUtility.HexToBytes(config);
		this.milenage = new Milenage(op);
	}

	public void f1(byte[] key, byte[] rand, byte[] sqn, byte[] amf, byte[] mac) throws GeneralSecurityException {
		this.milenage.f1(key, rand, sqn, amf, mac);
	}

	public void f1star(byte[] key, byte[] rand, byte[] sqn, byte[] amf, byte[] mac_s) throws GeneralSecurityException {
		this.milenage.f1star(key, rand, sqn, amf, mac_s);
	}

	public void f5star(byte[] key, byte[] rand, byte[] ak) throws GeneralSecurityException {
		this.milenage.f5star(key, rand, ak);
	}

	public byte[] f2345(byte[] key, byte[] rand, byte[] ck, byte[] ik, byte[] ak) throws GeneralSecurityException {
		byte[] xres = new byte[8];
		this.milenage.f2345(key, rand, xres, ck, ik, ak);
		return xres;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AkaMilenage akaMilenage = null;
		akaMilenage = (AkaMilenage) super.clone();
		if(milenage != null){
			akaMilenage.milenage = (Milenage) milenage.clone();
		}
			
		return akaMilenage;
	}
}
