package com.elitecore.coreeap.util.aka.quintet;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;


public abstract class BaseAkaQuintet implements IAkaQuintet {

	public void configure(String s) throws InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		
	}

	public void f1(byte[] k, byte[] rand, byte[] sqn, byte[] amf, byte[] macA)
			throws GeneralSecurityException {
		// TODO Auto-generated method stub
		
	}
	
	public void f1star(byte[] k, byte[] rand, byte[] sqn, byte[] amf,
			byte[] macS) throws GeneralSecurityException {
		// TODO Auto-generated method stub
		
	}
	
	public byte[] f2345(byte[] key, byte[] rand, byte[] ck, byte[] ik, byte[] ak)
			throws GeneralSecurityException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void f5star(byte[] k, byte[] rand, byte[] ak)
			throws GeneralSecurityException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		BaseAkaQuintet baseAkaQuintet = null;
		baseAkaQuintet = (BaseAkaQuintet) super.clone();		
		return baseAkaQuintet;
	}

}
