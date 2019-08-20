package com.elitecore.coreeap.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum UlticomResultCodeConstants {
	
	ULCM_SUCCESS(0, "SUCCESS", false, false),

	ULCM_USER_UNAUTHORIZED(65536, "IMSI/MSISDN is valid and the user is authorized for the requested operation", false, false),
	
	ULCM_INVALID_USER(131072, "IMSI/MSISDN is not valid on any HLR served by this module or are missing in internal provisionning of the module", true, false),
	
	ULCM_G_COMM_ERROR(196608, "HLR and/or MAP Gateway is inaccessible", false, false),
	ULCM_G_MALFORMED_MESSAGE(196609, "HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_G_MAX_REQ_EXCEEDED(196610, "HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_G_GATEWAY_ERROR(196611,"HLR and/or MAP Gateway is inaccessible", false, false),		
	ULCM_L_TCP_INIT_FAILURE(196612,"HLR and/or MAP Gateway is inaccessible", false, true),		
	ULCM_L_INIT_REQ_FAILURE(196613,"HLR and/or MAP Gateway is inaccessible", false, false),		
	ULCM_L_MAX_REQ_EXCEDED(196614,"HLR and/or MAP Gateway is inaccessible", false, false),		
	ULCM_L_TCP_CONNECT_FAILURE(196615,"HLR and/or MAP Gateway is inaccessible", false, true), 	
	ULCM_L_TCP_SEND_FAILURE(196616,"HLR and/or MAP Gateway is inaccessible", false, true),		
	ULCM_L_MOD_HANDLE_SET_ALREADY(196617,"HLR and/or MAP Gateway is inaccessible", false, false),
	ULCM_L_INVALID_MOD_HANDLE(196618,"HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_L_REQ_NOT_FOUND(196619,"HLR and/or MAP Gateway is inaccessible", false, false),		
	ULCM_L_API_VERSION_INVALID(196620,"HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_L_MOD_HANDLE_NOT_INIT(196621,"HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_L_ASN_ENCODING_ERROR(196622,"HLR and/or MAP Gateway is inaccessible", false, false),	
	ULCM_L_ASN_DECODING_ERROR(196623,"HLR and/or MAP Gateway is inaccessible", false, false),
	EC_TIMEOUT(196659, "Timeout from HLR", false, false),
	
	ULCM_L_INVALID_ARG(262144,"Incorrect or stale parameter was specified to the call", false, false),
	
	ULCM_G_GENERAL_ERROR(327680, "HLR and/or MAP Gateway is not functional", false, false),		
	ULCM_G_GATEWAY_PROV_ERROR(327681,"HLR and/or MAP Gateway is not functional", false, false),	
	ULCM_L_CONF_INIT_FAILURE(327682,"HLR and/or MAP Gateway is not functional", false, false),	
	ULCM_L_CONF_PARAM_NOT_VALID(327683,"HLR and/or MAP Gateway is not functional", false, false), 
	ULCM_L_INTERNAL_ERROR(327684,"HLR and/or MAP Gateway is not functional", false, false),		
	ULCM_L_FUNCTION_NOT_SUPPORTED(327685,"HLR and/or MAP Gateway is not functional", false, false),	
	
	ULCM_L_VERSION_NOT_SUPPORTED(393216, "Version incompatibility between the application and the library or between the library and the Gateway ", false, false),
	
	ULCM_L_NO_TRIPLET_AVAILABLE(458753,"HLR returned Quintets where Triplets were expected", false, false),    
	ULCM_L_NO_QUINTET_AVAILABLE(458754,"HLR returned Triplets where Quintets were expected", false, false),
	
	UNKNOWN_RESPONSE_CODE(-1,"UNKNOWN", false, false);
	
	public final int resultCode;
	public final String message;
	private final boolean failure;
	private final boolean markDead;
	
	public static final UlticomResultCodeConstants[] types = values();
	private static final Map<Integer , UlticomResultCodeConstants> map;
	
	static {
		map = new HashMap<Integer,UlticomResultCodeConstants>();
		for ( UlticomResultCodeConstants type : types){
			map.put(type.resultCode, type);
		}
	}
	
	UlticomResultCodeConstants(int resultCode, String message, boolean failure, boolean markDead){
		this.resultCode = resultCode;
		this.message = message;
		this.failure = failure;
		this.markDead = markDead;
	}
	
	public static UlticomResultCodeConstants getName(int code){
		if (map.get(code)!=null){
			return map.get(code);
		}
		return map.get(-1);
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public boolean isFailure() {
		return failure;
	}
	
	public boolean needDriverDead() {
		return markDead;
	}
	
}
