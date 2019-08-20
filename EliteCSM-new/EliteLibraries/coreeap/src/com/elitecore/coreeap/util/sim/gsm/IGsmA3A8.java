package com.elitecore.coreeap.util.sim.gsm;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

public interface IGsmA3A8 extends Cloneable{
	public abstract void configure(String s) throws InvalidAlgorithmParameterException;
	public abstract byte[] compute(byte byte0[], byte byte1[]) throws GeneralSecurityException;	
	public Object clone() throws CloneNotSupportedException;
}
