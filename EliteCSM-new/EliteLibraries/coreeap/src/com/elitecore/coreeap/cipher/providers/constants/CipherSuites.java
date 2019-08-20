package com.elitecore.coreeap.cipher.providers.constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.KeyExchangeAlgorithm;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;


public enum CipherSuites {
//	//TODO - For the just R&D i have put the block size 8 to every ciphersuite, update it.
	
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
	
//	TLS_RSA_WITH_NULL_SHA(1,CommonConstants.RSA,"","","SHA",0,0,20,40,0,(byte)3,CommonConstants.STREAM_CIPHER,false, true),
//	TLS_RSA_WITH_NULL_MD5(2,CommonConstants.RSA,"","","MD5",0,0,16,32,0,(byte)3,CommonConstants.STREAM_CIPHER,false, true),
	TLS_RSA_WITH_RC4_128_MD5(4,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"RC4","RC4",HashAlgorithm.MD5,0,0,16,64,16,(byte)7,CommonConstants.STREAM_CIPHER,false,true),
	TLS_RSA_WITH_RC4_128_SHA(5,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"RC4","RC4",HashAlgorithm.SHA1,0,0,20,72,16,(byte)7,CommonConstants.STREAM_CIPHER,false,true),
//	TLS_RSA_EXPORT_WITH_RC2_CBC_40_MD5(6,CommonConstants.RSA_EXPORT,"RC2/CBC/PKCS5Padding","RC2","MD5",8,8,16,0,0,(byte)3,CommonConstants.STREAM_CIPHER, true, true),
	TLS_RSA_WITH_IDEA_CBC_SHA(7,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"IDEA/CBC/NoPadding","IDEA",HashAlgorithm.SHA1,8,8,20,104,8,(byte)3,CommonConstants.BLOCK_CIPHER,false, true),
//	TLS_RSA_EXPORT_WITH_DES40_CBC_SHA(8,CommonConstants.RSA_EXPORT,"","","SHA",8,8,20,66,5,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_RSA_WITH_DES_CBC_SHA(9,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"DES/CBC/NoPadding","DES",HashAlgorithm.SHA1,8,8,20,104,8,(byte)3,CommonConstants.BLOCK_CIPHER,false, true),
	TLS_RSA_WITH_3DES_EDE_CBC_SHA(10,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"DESede/CBC/NoPadding","DESede",HashAlgorithm.SHA1,8,8,20,104,24,(byte)7,CommonConstants.BLOCK_CIPHER,false, true),
//	TLS_DH_DSS_EXPORT_WITH_DES40_CBC_SHA(11,CommonConstants.DH_DSS_EXPORT,"","","SHA",8,8,20,66,5,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DH_DSS_WITH_DES_CBC_SHA(12,CommonConstants.DH_DSS,"DES/CBC/NoPadding","DES","SHA",8,8,20,72,8,(byte)3,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA(13,CommonConstants.DH_DSS,"DESede","DESede/CBC/NoPadding","SHA",8,8,20,104,24,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DH_RSA_EXPORT_WITH_DES40_CBC_SHA(14,CommonConstants.DH_RSA_EXPORT,"","","SHA",8,8,20,66,5,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DH_RSA_WITH_DES_CBC_SHA(15,CommonConstants.DH_RSA,"DES","DES/CBC/NoPadding","SHA",8,8,20,72,8,(byte)3,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA(16,CommonConstants.DH_RSA,"DESede","DESede/CBC/NoPadding","SHA",8,8,20,104,24,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA(17,CommonConstants.DHE_DSS_EXPORT,"","","SHA",8,8,20,66,5,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_DSS_WITH_DES_CBC_SHA(18,SignatureAlgorithm.DSA, KeyExchangeAlgorithm.DHE,"DES/CBC/NoPadding","DES",HashAlgorithm.SHA1,8,8,20,72,8,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA(19,SignatureAlgorithm.DSA, KeyExchangeAlgorithm.DHE,"DESede/CBC/NoPadding","DESede",HashAlgorithm.SHA1,8,8,20,104,24,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA(20,CommonConstants.DHE_RSA_EXPORT,"","","SHA",8,8,20,66,5,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_RSA_WITH_DES_CBC_SHA(21,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.DHE,"DES/CBC/NoPadding","DES",HashAlgorithm.SHA1,8,8,20,72,8,(byte)3,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA(22,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.DHE,"DESede/CBC/NoPadding","DESede",HashAlgorithm.SHA1,8,8,20,104,24,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),	

	
	
	TLS_DH_anon_WITH_RC4_128_MD5(0x18,SignatureAlgorithm.ANONYMOUS, KeyExchangeAlgorithm.DHE,"RC4","RC4",HashAlgorithm.MD5,0,0,16,64,16,(byte)7,CommonConstants.STREAM_CIPHER,true,false),
	
	//According to RFC-3268
	TLS_RSA_WITH_AES_128_CBC_SHA(0x2F,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER,false, true),
//	TLS_DH_DSS_WITH_AES_128_CBC_SHA(0x30,CommonConstants.DH_DSS,"AES/CBC/NoPadding","AES","SHA",16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DH_RSA_WITH_AES_128_CBC_SHA(0x31,CommonConstants.DH_RSA,"AES/CBC/NoPadding","AES","SHA",16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
	TLS_DHE_DSS_WITH_AES_128_CBC_SHA(0x32,SignatureAlgorithm.DSA, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_RSA_WITH_AES_128_CBC_SHA(0x33,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DH_anon_WITH_AES_128_CBC_SHA(0x34,SignatureAlgorithm.ANONYMOUS, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,104,16,(byte)7,CommonConstants.BLOCK_CIPHER, true, false),
//	TLS_RSA_WITH_AES_256_CBC_SHA(0x35,SignatureAlgorithm.RSA,KeyExchangeAlgorithm.RSA,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER,false, true),
//	TLS_DH_DSS_WITH_AES_256_CBC_SHA(0x36,CommonConstants.DH_DSS,"AES/CBC/NoPadding","AES","SHA",16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DH_RSA_WITH_AES_256_CBC_SHA(0x37,CommonConstants.DH_RSA,"AES/CBC/NoPadding","AES","SHA",16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_DHE_DSS_WITH_AES_256_CBC_SHA(0x38,SignatureAlgorithm.DSA,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DHE_RSA_WITH_AES_256_CBC_SHA(0x39,SignatureAlgorithm.RSA,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DH_anon_WITH_AES_256_CBC_SHA(0x3A,SignatureAlgorithm.ANONYMOUS,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA1,16,16,20,136,32,(byte)7,CommonConstants.BLOCK_CIPHER, true, false),
	
	//According to RFC-5246
	// TODO Uncomment this Cipher Suite in 6.6
	TLS_RSA_WITH_AES_128_CBC_SHA256(0x3C,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.RSA,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,128,16,(byte)4,CommonConstants.BLOCK_CIPHER, false, true),
//	TLS_RSA_WITH_AES_256_CBC_SHA256(0x3D,SignatureAlgorithm.RSA,KeyExchangeAlgorithm.RSA,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,160,32,(byte)4,CommonConstants.BLOCK_CIPHER, false, true),
	TLS_DHE_DSS_WITH_AES_128_CBC_SHA256(0x40,SignatureAlgorithm.DSA, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,128,16,(byte)4,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DHE_RSA_WITH_AES_128_CBC_SHA256(0x67,SignatureAlgorithm.RSA, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,128,16,(byte)4,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DHE_DSS_WITH_AES_256_CBC_SHA256(0x6A,SignatureAlgorithm.DSA,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,160,32,(byte)4,CommonConstants.BLOCK_CIPHER, true, true),
//	TLS_DHE_RSA_WITH_AES_256_CBC_SHA256(0x6B,SignatureAlgorithm.RSA,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,160,32,(byte)4,CommonConstants.BLOCK_CIPHER, true, true),
	TLS_DH_anon_WITH_AES_128_CBC_SHA256(0x6C,SignatureAlgorithm.ANONYMOUS, KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,128,16,(byte)4,CommonConstants.BLOCK_CIPHER, true, false),
//	TLS_DH_anon_WITH_AES_256_CBC_SHA256(0x6D,SignatureAlgorithm.ANONYMOUS,KeyExchangeAlgorithm.DHE,"AES/CBC/NoPadding","AES",HashAlgorithm.SHA256,16,16,32,160,32,(byte)4,CommonConstants.BLOCK_CIPHER, true, false),
	;
	
	public final int code;
	private final SignatureAlgorithm signatureAlgorithm;
	private final KeyExchangeAlgorithm keyExchangeAlgorithm;
	public final String cipherString;
	public final String cipherAlgo;
	private final HashAlgorithm hashAlgo;
	public final int IVSize;
	public final int blockSize;
	public final int hashSize;
	/*
	 * 	keyBlockSize value in above cipher suites are calculated as per below...
	 * 			keyBlockSize = 	(2 X keyMaterialSize) 	+
	 * 							(2 X hashSize)			+
	 * 							(2 X IVSize)
	 */
	public final int keyBlockSize;
	public final int keyMaterialSize;
	public final int protocolVersion;
	public final int cipherType;
	public final boolean serverKeyExchangeRequired;
	/***
	 * 	Certificate message to be sent and certificate request are decided using same flags 
	 * 	Now but if there are some other cipher suites that allows client authentication without server authentication
	 */
	public final boolean certificateMessageRequired; 
	
	private static final Map<Integer,CipherSuites> map;
	private static final Map<ProtocolVersion,Map<Integer,CipherSuites>> versionMap;
	
	static {
		map = new HashMap<Integer,CipherSuites>();
		versionMap = new HashMap<ProtocolVersion, Map<Integer,CipherSuites>>();
		Map<Integer,CipherSuites> mapTls1_0 = new HashMap<Integer, CipherSuites>();
		Map<Integer,CipherSuites> mapTls1_1 = new HashMap<Integer, CipherSuites>();
		Map<Integer,CipherSuites> mapTls1_2 = new HashMap<Integer, CipherSuites>();

		for(CipherSuites type : values()){
			
			map.put(type.code, type);
			
			if((type.protocolVersion & 4) == 4){
				mapTls1_2.put(type.code, type);
			}
			if((type.protocolVersion & 2) == 2){
				mapTls1_1.put(type.code, type);
			}
			if((type.protocolVersion & 1) == 1){
				mapTls1_0.put(type.code, type);
			}
		}
		versionMap.put(ProtocolVersion.TLS1_2, mapTls1_2);
		versionMap.put(ProtocolVersion.TLS1_1, mapTls1_1);
		versionMap.put(ProtocolVersion.TLS1_0, mapTls1_0);
	}

	CipherSuites(int code,SignatureAlgorithm signatureAlgo, KeyExchangeAlgorithm keyExchangeAlgorithm, String cipherString,String cipherAlgo,HashAlgorithm hashAlgo,int IVSize,int blockSize,int hashSize,int keyBlockSize,int keyMaterialSize,byte protocolVersion, int cipherType, boolean serverKeyExchangeRequired, boolean certificateMessageRequired){
		this.code = code;
		this.signatureAlgorithm = signatureAlgo;
		this.keyExchangeAlgorithm = keyExchangeAlgorithm;
		this.cipherAlgo = cipherAlgo;
		this.cipherString = cipherString;
		this.hashSize = hashSize;
		this.blockSize = blockSize;
		this.hashAlgo = hashAlgo;
		this.IVSize = IVSize;
		this.keyBlockSize = keyBlockSize;
		this.keyMaterialSize = keyMaterialSize;
		this.protocolVersion = protocolVersion;
		this.cipherType = cipherType;
		this.certificateMessageRequired = certificateMessageRequired;
		this.serverKeyExchangeRequired = serverKeyExchangeRequired;
	}

	public int getCode(){
		return this.code;		
	}
	
	public HashAlgorithm getHashAlgorithm(){
		return this.hashAlgo;
	}
	
	public KeyExchangeAlgorithm getKeyExchangeAlgorithm(){
		return this.keyExchangeAlgorithm;
	}
	
	public SignatureAlgorithm getSignatureAlgorithm(){
		return this.signatureAlgorithm;
	}
	
	public static CipherSuites fromCipherCode(int code){
		return map.get(code);
	}

	public static boolean isSupported(int value, ProtocolVersion protocolVersion) {
		return versionMap.get(protocolVersion).containsKey(value);
	}
	
	public static boolean isBlockCipher(int code){
		return (map.get(code).cipherType == CommonConstants.BLOCK_CIPHER)?true:false;
	}
	
	public static Collection<CipherSuites> getSupportedCipherSuites(ProtocolVersion minTLSVersion, ProtocolVersion maxTlsVersion){
		Set<CipherSuites> cipherSuites = new HashSet<CipherSuites>();
		
		for (int index = minTLSVersion.getMinor(); index <= maxTlsVersion.getMinor(); index++) {
			cipherSuites.addAll(versionMap.get(ProtocolVersion.getProtocolVersion(minTLSVersion.getMajor(), index)).values());
		}
		return cipherSuites;
	}
	
	public static List<Integer> getSupportedCiphersuiteIDs(ProtocolVersion minTLSVersion, ProtocolVersion maxTlsVersion) {
		List<Integer> cipherSuiteIds = new ArrayList<Integer>();
		
		for (CipherSuites cipherSuite : getSupportedCipherSuites(minTLSVersion, maxTlsVersion)) {
			cipherSuiteIds.add(cipherSuite.getCode());
		}
		return cipherSuiteIds;
	}
}
