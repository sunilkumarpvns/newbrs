package com.elitecore.coreeap.util.constants.tls.alert;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum TLSAlertDescConstants implements IEnum {
	 CLOSE_NOTIFY(0,"close_notify"),
     UNEXPECTED_MESSAGE(10,"unexpected_message"),
     BAD_RECORD_MAC(20,"bad_record_mac"),
     DECRYPTION_FAILED(21,"decryption_failed"),
     RECORD_OVERFLOW(22,"record_overflow"),
     DECOMPRESSION_FAILURE(30,"decompression_failure"),
     HANDSHAKE_FAILURE(40,"handshake_failure"),
     BAD_CERTIFICATE(42,"bad_certificate"),
     UNSUPPORTED_CERTIFICATE(43,"unsupported_certificate"),
     CERTIFICATE_REVOKED(44,"certificate_revoked"),
     CERTIFICATE_EXPIRED(45,"certificate_expired"),
     CERTIFICATE_UNKNOWN(46,"certificate_unknown"),
     ILLEGAL_PARAMETER(47,"illegal_parameter"),
     UNKNOWN_CA(48,"unknown_ca"),
     ACCESS_DENIED(49,"access_denied"),
     DECODE_ERROR(50,"decode_error"),
     DECRYPT_ERROR(51,"decrypt_error"),
     EXPORT_RESTRICTION(60,"export_restriction"),
     PROTOCOL_VERSION(70,"protocol_version"),
     INSUFFICIENT_SECURITY(71,"insufficient_security"),
     INTERNAL_ERROR(80,"internal_error"),
     USER_CANCELED(90,"user_canceled"),
     NO_RENEGOTIATION(100,"no_renegotiation"),
     RESERVED(255,"reserved");
	public final int typeId;
	public final String name;
	private static final Map<Integer,TLSAlertDescConstants> map;
	private static final Map<String,TLSAlertDescConstants> nameMap;
	public static final TLSAlertDescConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,TLSAlertDescConstants>();
		for (TLSAlertDescConstants type : VALUES) {
			map.put(type.typeId, type);
		}
		nameMap = new HashMap<String,TLSAlertDescConstants>();
		for (TLSAlertDescConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}	

	TLSAlertDescConstants(int id,String name){
		this.typeId= id;
		this.name = name;
	}
	public int getTypeId(){
		return this.typeId;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getTypeId(String name){
		return nameMap.get(name).typeId;
	}

}
