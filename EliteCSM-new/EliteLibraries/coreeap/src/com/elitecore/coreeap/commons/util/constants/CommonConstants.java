package com.elitecore.coreeap.commons.util.constants;

public class CommonConstants {

	public static final String UTF8 = "UTF-8";
	public static final String SUN = "SUN";
	
	public static final int STREAM_CIPHER = 1;
	public static final int BLOCK_CIPHER = 2;
	
	// Key Exchange Algorithm Constants
	public static final String DHE_RSA = "DHE_RSA";
	public static final String RSA_EXPORT = "RSA_EXPORT";
	public static final String DH_ANON = "DH_ANON";
	public static final String DHE_DSS = "DHE_DSS";
	public static final String DH_DSS_EXPORT = "DH_DSS_EXPORT";
	public static final String DH_RSA_EXPORT = "DH_RSA_EXPORT";
	public static final String DH_DSS = "DH_DSS";
	public static final String DH_RSA = "DH_RSA";
	public static final String DHE_DSS_EXPORT = "DHE_DSS_EXPORT";
	public static final String RSA = "RSA";
	public static final String DH = "DH";
	
	/**
	 * 	Below are ASN1 Object Constants.
	 * 	
	 * 	Values are taken from 
	 * 		RFC 3447 ( Public-Key Cryptography Standards (PKCS) #1: RSA Cryptography
     *                 		Specifications Version 2.1 ) 
     *     	Section 9.2 ( EMSA-PKCS1-v1_5 ) Notes : 1
	 */
	public static final byte[] MD2_ASN1_OBJECT_BYTES = { 48, 32, 48, 12, 6, 8, 42, -122, 48, -122, -9, 13, 2, 2, 5, 0, 4, 16};
	public static final byte[] MD5_ASN1_OBJECT_BYTES = { 48, 32, 48, 12, 6, 8, 42, -122, 48, -122, -9, 13, 2, 5, 5, 0, 4, 16};
	public static final byte[] SHA_ASN1_OBJECT_BYTES = {48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20};
	public static final byte[] SHA256_ASN1_OBJECT_BYTES = {48, 49, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 1, 5, 0, 4, 32};
	public static final byte[] SHA384_ASN1_OBJECT_BYTES = {48, 65, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 2, 5, 0, 4, 48};
	public static final byte[] SHA512_ASN1_OBJECT_BYTES = {48, 81, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 3, 5, 0, 4, 64};
	
	//Signature Algorithm Constants
	public static final String NONE_WITH_RSA = "NONEwithRSA";
	public static final String NONE_WITH_DSA = "NONEwithDSA";
}
