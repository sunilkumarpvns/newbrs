package com.elitecore.coreeap.commons.util;


public interface TemporaryIdentityGenerator {
	public static final int ELITECRYPT = 3;
	public static final int BASE16 = 16;
	public static final int BASE32 = 32;
	public static final int BASE64 = 64; 
	public static final int BASIC_ALPHA_1 = 1;
	
	
	public String encodeTemporaryIdentity(String permIdentity, int method, String prefix, boolean hexEncoding);
	public String decodeTemporaryIdentity(String tempIdentity, int method, String prefix, boolean hexEncoding);
}
