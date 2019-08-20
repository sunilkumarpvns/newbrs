package com.elitecore.elitesm.web.core.system.startup;

import java.util.LinkedHashMap;

public class EliteSMStartupStatus {
	
	private static LinkedHashMap<SM_MODULE_CONSTANTS,SM_MODULE_STATUS> moduleStatusMap = new LinkedHashMap<SM_MODULE_CONSTANTS, SM_MODULE_STATUS>();

	public static boolean DB_SETUP_COMPLETED = false;
	
	public static void init(){
		moduleStatusMap = new LinkedHashMap<SM_MODULE_CONSTANTS, SM_MODULE_STATUS>();
		for(SM_MODULE_CONSTANTS constants : SM_MODULE_CONSTANTS.values()){
			moduleStatusMap.put(constants, SM_MODULE_STATUS.PENDING);
		}
	}
	public static LinkedHashMap<SM_MODULE_CONSTANTS, SM_MODULE_STATUS> getModuleStatusMap() {
		return moduleStatusMap;
	}

	public static void updateStatus ( SM_MODULE_CONSTANTS key, SM_MODULE_STATUS status){
		moduleStatusMap.put(key, status);
	}
	
	public enum SM_MODULE_CONSTANTS{
		SEV("Setting up environment variable"),
		SMSPC("Server Manager startup process creation"),
		DCC("Database configuration checking"),
		HCR("Hibernate configuration reading "),
		HES("Hibernate environment setup"),
		SSS("Server Manager service startup");		
		public String label;
		SM_MODULE_CONSTANTS(String label){
			this.label = label;
		}		
	}
	
	public enum SM_MODULE_STATUS {
		PENDING("Pending"),
		PROCESSING("Processing"),
		SUCCESS("Success"),
		FAILED("Failed");
		public String status;
		SM_MODULE_STATUS(String status){
			this.status = status;
		}
		
	}
}
