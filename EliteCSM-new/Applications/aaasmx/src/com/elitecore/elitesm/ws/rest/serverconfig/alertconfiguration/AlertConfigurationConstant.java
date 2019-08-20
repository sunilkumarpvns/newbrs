package com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration;

import java.util.HashMap;
import java.util.Map;


public class AlertConfigurationConstant {

	public static final String GENERIC = "Generic";
	public static final String LEAF_NODE = "L";

	public static final String MINUTE_VALUE = "3";
	public static final String MINUTE = "Minute";
	public static final String HOUR_VALUE = "4";
	public static final String HOUR = "Hour";
	public static final String DAILY_VALUE = "5";
	public static final String DAILY = "Daily";

	public static final Long MINUTE_LONG = 3L;
	public static final Long HOUR_LONG = 4L;
	public static final Long DAILY_LONG = 5L;

	public static final int TIME_BASED_VALUE = 1;
	public static final int SIZE_BASED_VALUE = 2;
	public static final int INVALID_TYPE = -1;

	public enum Generics {

		RADIUSAUTHGENERIC("RadiusAuthGeneric", "AT000040"),
		RADIUSACCTGENERIC("RadiusAcctGeneric", "AT000041"),       
		DYNAAUTHGENERIC("DynaAuthGeneric", "AT000043"),    
		DATABASEGENERIC("DataBaseGeneric", "AT000045"),          
		LDAPGENERIC("LDAPGeneric", "AT000046"),              
		ESIGENERIC("ESIGeneric", "AT000047"),              
		IPPOOLGENERIC("IPPoolGeneric", "AT000052"),            
		CONCURRENCYGENERIC("ConcurrencyGeneric", "AT000053"),       
		PREPAIDGENERIC("PrePaidGeneric", "AT000054"),       
		NASGENERIC("NASGeneric", "AT000061"),           
		CREDITCONTROLGENERIC("CreditControlGeneric", "AT000062"),     
		EAPGENERIC("EAPGeneric", "AT000063"),     
		MIPGENERIC("MIPGeneric", "AT000064"),               
		DIAMETERWEBSERVICEGENERIC("DiameterWebServiceGeneric", "AT000065"),
		RADIUSWEBSERVICEGENERIC("RADIUSWebServiceGeneric", "AT000068"),
		OTHERGENERIC("OtherGeneric", "AT000067");             

		private final String name;                          
		private final String id;                            
		Generics(String name, String id) {        
			this.name = name;                               
			this.id = id;                                   
		}

		private static final Map<String, String> mapOne;
		private static final Map<String, String> mapTwo;
		public static final Generics[] VALUES = values();

		static {
			mapOne = new HashMap<String,String>();
			for (Generics type : VALUES) {
				mapOne.put(type.name, type.id);
			}

			mapTwo = new HashMap<String,String>();
			for (Generics type : VALUES) {
				mapTwo.put(type.id, type.name);
			}
		}

		public static String getGenericId(String name) {
			return mapOne.get(name);
		}

		public static String getGenericName(String id) {
			return mapTwo.get(id);
		}

	}
          
}