package com.elitecore.coreeap.util.aka.quintet;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

public interface IAkaQuintet extends Cloneable {
	public abstract void configure(String s) throws InvalidAlgorithmParameterException;
	public void f1(byte k[], byte rand[], byte sqn[], byte amf[], byte mac_a[]) throws GeneralSecurityException;
	public byte[] f2345(byte key[], byte rand[], byte ck[], byte ik[],byte ak[]) throws GeneralSecurityException;
	public void f1star(byte k[], byte rand[], byte sqn[], byte amf[],byte mac_s[]) throws GeneralSecurityException;    
	public void f5star(byte k[], byte rand[], byte ak[]) throws GeneralSecurityException;	
	public Object clone() throws CloneNotSupportedException;
}
