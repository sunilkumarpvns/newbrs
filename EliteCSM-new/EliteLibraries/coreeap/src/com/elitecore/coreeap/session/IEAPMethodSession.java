package com.elitecore.coreeap.session;

public interface IEAPMethodSession extends Cloneable{
	//constants used as Parameter Key for TLSSessionState.
	/**
	 *  SESSION_ID   
	 *  Type : byte[]
	 *  Length : 32(Default)
	 */
	public final static String SESSION_ID = "SESSION_ID"; //byte[]
	/**
	 * SESSION_ID_LENGTH
	 * Type : int
	 * Defualt Value : SESSION_ID_DEFUALT_LENGTH 
	 */
	public final static String SESSION_ID_LENGTH = "SESSION_ID_LENGTH"; //int
	/**
	 * SESSION_ID_DEFUALT_LENGTH
	 * Type : int
	 * Value : 32
	 */
	public final static int SESSION_ID_DEFUALT_LENGTH = 32;
	/**
	 * KEY_EXCHANGE_ALGO
	 * Type : String
	 */
	public final static String KEY_EXCHANGE_ALGO = "KEY_EXCHANGE_ALGO"; //String
	/**
	 * MAC_ALGO
	 * Type : String
	 */
	public final static String MAC_ALGO = "MACAlgorithm"; //String
	/**
	 * ENCRYPTION_ALGO
	 * Type : String
	 */
	public final static String ENCRYPTION_ALGO = "ENCRYPTION_ALGO "; // String
	/**
	 * COMPRESSION_METHOD
	 * Type : String
	 */
	public final static String COMPRESSION_METHOD = "COMPRESSION_METHOD";//String
	/**
	 * PEER_PUBLIC_KEY
	 * Type : byte[]
	 * Value: PublicKey
	 */
	public final static String PEER_PUBLIC_KEY = "peerPublicKey";//byte[]
	/**
	 * MASTER_SECRET
	 * Type  : byte[]
	 * Value : Master Secret generated through TLSUtility.generateMS() 
	 */
	public final static String MASTER_SECRET = "masterSecret";//byte[]
	/**
	 * CIPHERSUITE
	 * Type  : CipherSuite
	 * Value : Selected CipherSuite from the Client Hello.
	 */
	public final static String CIPHERSUITE= "CIPHERSUITE";// CipherSuite
	/**
	 * TLS_CONNECTION_STATE
	 * Type : TLSConnectionState
	 */
	public final static String TLS_CONNECTION_STATE = "TLS_CONNECTION_STATE";

	//constants used as Parameter Key for the TLSConnectionState
	/**
	 * CLIENT_RANDOM
	 * Type   : byte[]
	 * Length : 32 bytes
	 * Value  : random from the Client Hello  
	 */
	public final static String CLIENT_RANDOM = "CLIENT_RANDOM";
	/**
	 * SERVER_RANDOM
	 * Type   : byte[] 
	 * Length : 32 bytes
	 * Value  : random from the Server Hello  
	 */	
	public final static String SERVER_RANDOM = "SERVER_RANDOM";
	/**
	 * MAC_READ
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getClientWriteMACSecret()
	 */	
	public final static String MAC_READ = "MAC_READ";
	/**
	 * MAC_WRITE
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getServerWriteMACSecret()
	 */	
	public final static String MAC_WRITE = "MAC_WRITE";
	/**
	 * ENCRYPTION_READ
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getClientWriteKey()
	 */	
	public final static String ENCRYPTION_READ = "ENCRYPTION_READ";
	/**
	 * ENCRYPTION_WRITE
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getServerWriteKey()
	 */		
	public final static String ENCRYPTION_WRITE = "ENCRYPTION_WRITE";
	/**
	 * CLIENT_IV
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getClientWriteIV()
	 */		
	public final static String CLIENT_IV = "CLIENT_IV";
	/**
	 * SERVER_IV
	 * Type   : byte[]
	 * Length : Depends on Selected CipherSuite
	 * Value  : generated from the TLSUtility.getServerWriteIV()
	 */	
	public final static String SERVER_IV = "SERVER_IV";
	/**
	 * READ_SEQ
	 * Type   : int 
	 * Value  : 
	 */	
	public final static String READ_SEQ = "READ_SEQ";
	/**
	 * WRITE_SEQ
	 * Type   : int 
	 * Value  : 
	 */	
	public final static String WRITE_SEQ = "WRITE_SEQ";	
	/**
	 * WAIT_FOR_NEXT_FRAGMENT
	 * Type  : boolean
	 * Value : false (Default)
	 */
	public final static String WAIT_FOR_NEXT_FRAGMENT = "WAIT_FOR_NEXT_FRAGMENT";
	
	/**
	 * NEXT_TLS_RECORD_TYPE
	 * Type	 : int
	 * value :
	 */
	public final static String NEXT_TLS_RECORD_TYPE = "NEXT_TLS_RECORD_TYPE";
	
	/**
	 * NEXT_HANDSHAKE_MESSAGE_TYPE
	 * Type  : int
	 * Value :
	 */
	public static final String NEXT_HANDSHAKE_MESSAGE_TYPE = "NEXT_HANDSHAKE_MESSAGE_TYPE";
		
	/**
	 * ALERT_GENERATED
	 * Type  : boolean
	 * Value : false(default)
	 */
	public static final String ALERT_GENERATED = "ALERT_GENERATED";
	
	/**
	 * FRAGMENTED_DATA_BYTES
	 * Type  : byte[]
	 * Value : 
	 */
	public final static String FRAGMENTED_DATA_BYTES = "FRAGMENTED_DATA_BYTES";
	/**
	 * ALL_HANDSHAKE_MESSAGES
	 * Type  : byte[]
	 * Value : 
	 */
	public final static String ALL_HANDSHAKE_MESSAGES = "ALL_HANDSHAKE_MESSAGES";

	/**
	 * ALL_HANDSHAKE_MESSAGES
	 * Type  : boolean
	 * Value : false(Default)
	 */
	public final static String CHANGE_CIPHER_SPEC_RECEIVED = "CHANGE_CIPHER_SPEC_RECEIVED";
	
	/**
	 * 
	 * @param parameterKey 
	 */
	public final static String TTLS_SUCCESS = "TTLS_SUCCESS";
	
	public final static String TTLS_MAC_READ = "TTLS_MAC_READ"; 
	public final static String TTLS_MAC_WRITE = "TTLS_MAC_WRITE";
	public final static String TTLS_ENCRYPTION_READ = "TTLS_ENCRYPTION_READ";
	public final static String TTLS_ENCRYPTION_WRITE = "TTLS_ENCRYPTION_WRITE";
	public final static String TTLS_CLIENT_IV = "TTLS_CLIENT_IV";
	public final static String TTLS_SERVER_IV = "TTLS_SERVER_IV";
	
	public final static String TLS_SECURITY_KEY = "TLS_SECURITY_KEY";
	public final static String TLS_SECURITY_PARAMETER = "TLS_SECURITY_PARAMETER";
	
	public final static String TTLS_USER_IDENTITY = "TTLS_USER_IDENTITY";
	public Object getParameterValue(String parameterKey);
	public void setParameterValue(String parameterKey,Object parameterValue);	
}
