package com.elitecore.core.commons.tls.cipher;

import com.elitecore.core.commons.tls.TLSVersion;

import java.util.*;

/**
 * 
 * @author harsh patel
 * 
 * http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#footnote1-1 -- used for ordered cipher suites
 * 
 * http://www.iana.org/assignments/tls-parameters/tls-parameters.xhtml -- used to know cipher suite id
 *
 */
public enum CipherSuites {
	 
	
	

	/*  |   TLS1.2  | TLS1.1   | TLS 1    | value  |
	 *  |      0    |    0     |   0      |    0   | 
	 *  |      0    |    1     |   0      |    2   | 
	 *  |      0    |    0     |   1      |    1   | 
	 *  |      0    |    1     |   1      |    3   | 
	 *  |      1    |    0     |   0      |    4   | 
	 *  |      1    |    1     |   0      |    6   | 
	 *  |      1    |    0     |   1      |    5   | 
	 *  |      1    |    1     |   1      |    7   | 
	 */
	
	//DEFINE IN JAVA PREFERENCE ORDER 
	
	TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384(0xC024,1,"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",(byte)4),
	TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384(0xC028,1,"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",(byte)4),
	TLS_RSA_WITH_AES_256_CBC_SHA256(0x003D,1,"TLS_RSA_WITH_AES_256_CBC_SHA256",(byte)4),
	TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384(0xC026,1,"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384",(byte)4),
	TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384(0xC02A,1,"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",(byte)4),
	TLS_DHE_RSA_WITH_AES_256_CBC_SHA256(0x006B,1,"TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",(byte)4),
	TLS_DHE_DSS_WITH_AES_256_CBC_SHA256(0x006A,1,"TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",(byte)4),
	TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA(0xC00A,1,"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA(0xC014,1,"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_RSA_WITH_AES_256_CBC_SHA(0x0035,1, "TLS_RSA_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA(0xC005,1,"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", (byte)6),
	TLS_ECDH_RSA_WITH_AES_256_CBC_SHA(0xC00F,1,"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA",(byte)4),
	TLS_DHE_RSA_WITH_AES_256_CBC_SHA(0x0039,1, "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_DHE_DSS_WITH_AES_256_CBC_SHA(0x0038,1, "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256(0xC023,1,"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256(0xC027, 1 ,"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_RSA_WITH_AES_128_CBC_SHA256(0x003C,2,"TLS_RSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256(0xC025,1,"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256(0xC029,1,"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_DHE_RSA_WITH_AES_128_CBC_SHA256(0x0067,1,"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_DHE_DSS_WITH_AES_128_CBC_SHA256(0x0040,1,"TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA(0xC009,1,"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",(byte)4),
	TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA(0xC013, 1,"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",(byte)4),
	TLS_RSA_WITH_AES_128_CBC_SHA(0x002F,1,"TLS_RSA_WITH_AES_128_CBC_SHA",(byte)7),
	TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA(0xC004,1,"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",(byte)4),
	TLS_ECDH_RSA_WITH_AES_128_CBC_SHA(0xC00E,1,"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",(byte)4),
	TLS_DHE_RSA_WITH_AES_128_CBC_SHA(0x0033,1,"TLS_DHE_RSA_WITH_AES_128_CBC_SHA",(byte)7),
	TLS_DHE_DSS_WITH_AES_128_CBC_SHA(0x0032,1, "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",(byte)7),
	TLS_ECDHE_ECDSA_WITH_RC4_128_SHA(0xC007,2,"TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",(byte)4),
	TLS_ECDHE_RSA_WITH_RC4_128_SHA(0xC011,2,"TLS_ECDHE_RSA_WITH_RC4_128_SHA",(byte)4),
	TLS_RSA_WITH_RC4_128_SHA(0x0005,2,"SSL_RSA_WITH_RC4_128_SHA",(byte)7),
	TLS_ECDH_ECDSA_WITH_RC4_128_SHA(0xC002,2,"TLS_ECDH_ECDSA_WITH_RC4_128_SHA",(byte)4),
	TLS_ECDH_RSA_WITH_RC4_128_SHA(0xC00C,2,"TLS_ECDH_RSA_WITH_RC4_128_SHA",(byte)4),
	TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA(0xC008,1,"TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",(byte)4),
	TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA(0xC012,1,"TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",(byte)4),
	TLS_RSA_WITH_3DES_EDE_CBC_SHA(0x000A,1, "SSL_RSA_WITH_3DES_EDE_CBC_SHA",(byte)7),
	TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA(0xC003,1,"TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA",(byte)4),
	TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA(0xC00D,1,"TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",(byte)4),
	TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA(0x0016,1,"SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",(byte)7),
	//SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA
	TLS_RSA_WITH_RC4_128_MD5(0x0004,2, "SSL_RSA_WITH_RC4_128_MD5",(byte)7),
	
	
	/****************** supported but not enabled by default cipher suites  *********************/
	//TLS_DH_anon_WITH_AES_256_CBC_SHA256
	//TLS_ECDH_anon_WITH_AES_256_CBC_SHA
	TLS_DH_ANON_WITH_AES_256_CBC_SHA(0x003A,1, "TLS_DH_anon_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_DH_ANON_WITH_AES_128_CBC_SHA256(0x006C,1,"TLS_DH_anon_WITH_AES_128_CBC_SHA256",(byte)4),
	TLS_ECDH_ANON_WITH_AES_128_CBC_SHA(0xC018,1,"TLS_ECDH_anon_WITH_AES_128_CBC_SHA",(byte)4),
	TLS_DH_ANON_WITH_AES_128_CBC_SHA(0x0034,1,"TLS_DH_anon_WITH_AES_128_CBC_SHA",(byte)7),
	TLS_ECDH_ANON_WITH_RC4_128_SHA(0xC016,2,"TLS_ECDH_anon_WITH_RC4_128_SHA",(byte)4),
	TLS_DH_ANON_WITH_RC4_128_MD5(0x0018,1, "SSL_DH_anon_WITH_RC4_128_MD5",(byte)7),
	TLS_ECDH_ANON_WITH_3DES_EDE_CBC_SHA(0xC017,1,"TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA",(byte)4),
	TLS_DH_ANON_WITH_3DES_EDE_CBC_SHA(0x001B,1,"SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",(byte)7),
	TLS_RSA_WITH_NULL_SHA256(0x003B,2,"SSL_RSA_WITH_RC4_128_SHA",(byte)4),
	TLS_ECDHE_ECDSA_WITH_NULL_SHA(0xC006,1,"TLS_ECDHE_ECDSA_WITH_NULL_SHA",(byte)4),
	TLS_ECDHE_RSA_WITH_NULL_SHA(0xC010,1,"TLS_ECDHE_RSA_WITH_NULL_SHA",(byte)4),
	TLS_RSA_WITH_NULL_SHA(0x0002,2, "SSL_RSA_WITH_NULL_SHA",(byte)7),
	TLS_ECDH_ECDSA_WITH_NULL_SHA(0xC001,1,"TLS_ECDH_ECDSA_WITH_NULL_SHA",(byte)4),
	//TLS_ECDH_RSA_WITH_NULL_SHA
	TLS_ECDH_ANON_WITH_NULL_SHA(0xC015,1,"TLS_ECDH_anon_WITH_NULL_SHA",(byte)4),
	TLS_RSA_WITH_NULL_MD5(0x0001,2, "SSL_RSA_WITH_NULL_MD5",(byte)7),
	TLS_RSA_WITH_DES_CBC_SHA(0x0009,1,"SSL_RSA_WITH_DES_CBC_SHA",(byte)3),
	TLS_DHE_RSA_WITH_DES_CBC_SHA(0x0015,1,"SSL_DHE_RSA_WITH_DES_CBC_SHA",(byte)3),
	//SSL_DHE_DSS_WITH_DES_CBC_SHA
	TLS_DH_ANON_WITH_DES_CBC_SHA(0x001A,1,"SSL_DH_anon_WITH_DES_CBC_SHA",(byte)3),
	TLS_RSA_EXPORT_WITH_RC4_40_MD5(0x0003,2,"SSL_RSA_EXPORT_WITH_RC4_40_MD5",(byte)3),
	TLS_DH_ANON_EXPORT_WITH_RC4_40_MD5(0x0017,1, "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",(byte)1),
	TLS_RSA_EXPORT_WITH_DES40_CBC_SHA(0x0008,1,"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",(byte)1),
	TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA(0x0014,1,"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",(byte)1),
	TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA(0x0011,1, "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA",(byte)1),
	TLS_DH_ANON_EXPORT_WITH_DES40_CBC_SHA(0x0019,1,"SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",(byte)1),
	
	
	
	/************ below cipher suites not found in java ************************/
	
	/*TLS_RSA_WITH_IDEA_CBC_SHA(7,1,"TLS_RSA_WITH_IDEA_CBC_SHA",(byte)2),
	TLS_DH_DSS_WITH_DES_CBC_SHA(12,1, "TLS_DH_DSS_WITH_DES_CBC_SHA",(byte)2),
	TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA(13,1, "TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA",(byte)6),
	TLS_DH_RSA_WITH_DES_CBC_SHA(15,1, "TLS_DH_RSA_WITH_DES_CBC_SHA",(byte)2),
	TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA(16,1, "TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA",(byte)6),
	
	//TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA(19,1, "TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA",(byte)3),
	
	TLS_DH_DSS_WITH_AES_128_CBC_SHA(0x30,1,"TLS_DH_DSS_WITH_AES_128_CBC_SHA",(byte)6),
	TLS_DH_RSA_WITH_AES_128_CBC_SHA(0x31,1,"TLS_DH_RSA_WITH_AES_128_CBC_SHA",(byte)6),
	
	
	TLS_DH_DSS_WITH_AES_256_CBC_SHA(0x36,1, "TLS_DH_DSS_WITH_AES_256_CBC_SHA",(byte)6),
	TLS_DH_RSA_WITH_AES_256_CBC_SHA(0x37,1, "TLS_DH_RSA_WITH_AES_256_CBC_SHA",(byte)6),*/
	
	;
	
	public final int code;
	public final int cipherSuitesType;
	public final String name;
	
	
	
	private byte versionSupport;
	
	private static final Map<Integer,CipherSuites> map;
	private static final Map<String,CipherSuites> cipherSuiteNameMap;
	private static final Map<TLSVersion, List<CipherSuites>> versionTocipherSuitesMap;
	
	
	
	static {
		map = new LinkedHashMap<Integer,CipherSuites>();
		versionTocipherSuitesMap = new EnumMap<TLSVersion, List<CipherSuites>>(TLSVersion.class);
		cipherSuiteNameMap = new LinkedHashMap<String, CipherSuites>();
		for (CipherSuites cipherSuite : values()) {
			if(cipherSuite.isSupportTLS1_0()){
				List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_0);
				if(cipherSuites == null){
					cipherSuites = new ArrayList<CipherSuites>();
					versionTocipherSuitesMap.put(TLSVersion.TLS1_0, cipherSuites);
				}
				cipherSuites.add(cipherSuite);
			}
			
			if(cipherSuite.isSupportTLS1_1()){
				List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_1);
				if(cipherSuites == null){
					cipherSuites = new ArrayList<CipherSuites>();
					versionTocipherSuitesMap.put(TLSVersion.TLS1_1, cipherSuites);
				}
				cipherSuites.add(cipherSuite);
			}
			
			if(cipherSuite.isSupportTLS1_2()){
				List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_2);
				if(cipherSuites == null){
					cipherSuites = new ArrayList<CipherSuites>();
					versionTocipherSuitesMap.put(TLSVersion.TLS1_2, cipherSuites);
				}
				cipherSuites.add(cipherSuite);
			}
			
			map.put(cipherSuite.code, cipherSuite);
			cipherSuiteNameMap.put(cipherSuite.name, cipherSuite);
		}
	}

	private CipherSuites(int code, int cipherSuiteType, String name, byte versionSupport){
		this.code = code;
		this.cipherSuitesType = cipherSuiteType;
		this.name = name;
		this.versionSupport = versionSupport;
	}
	
	public int getCode(){
		return this.code;		
	}
	
	public static CipherSuites fromCipherCode(int code){
		return map.get(code);
	}
	
	public static CipherSuites fromCipherName(String name){
		return cipherSuiteNameMap.get(name);
	}
	
	public static boolean isSupported(int value) {
		return map.containsKey(value);
	}
	
	public boolean isBlockCipher(){
		return cipherSuitesType == 1;
	}
	
	public boolean isStreamCipher(){
		return cipherSuitesType == 2;
	}
	
	public boolean isNullCipher(){
		return cipherSuitesType == 3;
	}
	
	public boolean isSupportTLS1_0(){
		return  (versionSupport & 1) == 1;
	}
	
	public boolean isSupportTLS1_1(){
		return (versionSupport & 2) == 2;
	}
	
	public boolean isSupportTLS1_2(){
		return (versionSupport & 4) == 4;
	}
	
	public static List<CipherSuites> getSupportedCipherSuites(TLSVersion minTLSVersion, TLSVersion maxTlsVersion){
		List<CipherSuites> cipherSuites = new ArrayList<CipherSuites>();
		
		for(TLSVersion tlsVersion : TLSVersion.values()){
			if(tlsVersion.compareTo(minTLSVersion) >= 0 && tlsVersion.compareTo(maxTlsVersion) <= 0){
				cipherSuites.addAll(versionTocipherSuitesMap.get(tlsVersion));
			}
		}
		
		return cipherSuites;
	}

}
