package com.elitecore.aaa.core.constant;

import java.util.HashMap;
import java.util.Map;


public enum DriverTypes {
	RAD_OPENDB_AUTH_DRIVER(1),
	RAD_OPENDB_ACCT_DRIVER(2), 
	RAD_LDAP_AUTH_DRIVER(3),
	RAD_USERFILE_AUTH_DRIVER(4),
	RAD_DETAIL_LOCAL_ACCT_DRIVER(5),
	RAD_CLASSIC_CSV_ACCT_DRIVER(6),	
	RAD_WEBSERVICE_AUTH_DRIVER(17),
	RAD_HTTP_AUTH_DRIVER(26),
	RAD_HSS_AUTH_DRIVER(31),
	
	DIAMETER_DB_DRIVER(7), 
	DIAMETER_LDAP_DRIVER(8),
	DIAMETER_USERFILE_DRIVER(9),
	DIAMETER_MAPGW_AUTH_DRIVER(27),
	DIAMETER_HTTP_AUTH_DRIVER(28),
	DIA_HSS_AUTH_DRIVER(32),
	
	NAS_OPENDB_ACCT_DRIVER(10),	
	NAS_CLASSIC_CSV_ACCT_DRIVER(11),
	NAS_DETAIL_LOCAL_ACCT_DRIVER(12),
	CRESTEL_RATING_DRIVER(13),
	DIAMETER_CRESTEL_OCSv2_DRIVER(30),
	RAD_MAPGW_AUTH_DRIVER(14),
	
	
	RM_PARLAY_DRIVER(15),
	RM_DIAMETER_DRIVER(16),
	RM_CRESTEL_CHARGING_DRIVER(18),
	RM_CLASSIC_CSV_ACCT_DRIVER(19),
	RM_CRESTEL_OCSV2_DRIVER(29),
	
	
	/*Its for internal use only.*/
	RM_OPENDB_IPPOOL_DRIVER(25);
	 
	public final int value;
	private static final Map<Integer, String> nameMap;
	private static final Map<String, DriverTypes> typeByNameMap;
	private static final DriverTypes[] VALUES = values();
	static{
		nameMap = new HashMap<Integer, String>();
		typeByNameMap = new HashMap<String, DriverTypes>();
		for(DriverTypes type: VALUES){
			nameMap.put(type.value, type.name());
			typeByNameMap.put(type.name(), type);
		}
	}
	DriverTypes(int value) {
		this.value = value;
	}
	
	public static String getDriverTypeStr(int driverTypeID){
		return nameMap.get(driverTypeID);
	}
	
	public static DriverTypes getDriverTypeByName(String driverTypeName){
		return typeByNameMap.get(driverTypeName);
	}
	
}
