package com.elitecore.core.commons.util.db.errorcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Malav Desai
 * 
 */
public enum PostgreSQLErrorCodes {
	PSQL_8000("08000", "connection_exception", true),
	PSQL_8003("08003", "connection_does_not_exist",true),
	PSQL_8006("08006", "connection_failure", true),
	PSQL_8001("08001", "sqlclient_unable_to_establish_sqlconnection", true),
	PSQL_8004("08004", "sqlserver_rejected_establishment_of_sqlconnection", true),
	PSQL_8007("08007", "transaction_resolution_unknown", true),
	PSQL_8P01("08P01", "protocol_violation", true),
	;
	
	public final String sqlState;
	public final String vendorErrorMessage;
	public final boolean isDBDownError;
	
	private static Map<String, PostgreSQLErrorCodes> sqlStateToErrorEnumMap;
	
	static{
		sqlStateToErrorEnumMap = new HashMap<String, PostgreSQLErrorCodes>();
		for (PostgreSQLErrorCodes code : values()) {
			sqlStateToErrorEnumMap.put(code.sqlState, code);
		}
	}
	
	private PostgreSQLErrorCodes(String errorCode, String vendorErrorMessage, boolean isDBDownError) {
		this.sqlState = errorCode;
		this.vendorErrorMessage = vendorErrorMessage;
		this.isDBDownError = isDBDownError;
	}
	
	public static boolean isDBDownSQLState(String sqlState){
		PostgreSQLErrorCodes code = sqlStateToErrorEnumMap.get(sqlState);
		if(code != null){
			return code.isDBDownError;
		}else {
			return false;
		}
	}
	
	public static String getVendorErrorMessageFromSQLState(String sqlState){
		PostgreSQLErrorCodes code = sqlStateToErrorEnumMap.get(sqlState);
		String message = null;
		if(code != null){
			message = code.vendorErrorMessage;
		}
		return message;
	}
	
	public static PostgreSQLErrorCodes fromSQLState(String sqlState){
		return sqlStateToErrorEnumMap.get(sqlState);
	}
}
