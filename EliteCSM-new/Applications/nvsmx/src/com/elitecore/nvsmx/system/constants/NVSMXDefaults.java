package com.elitecore.nvsmx.system.constants;

/**
 * Defines the defaults value for NVSMX
 * 
 * */
public enum NVSMXDefaults {
   LOGFILE_NAME("nvsmx"),
   LOGFILE_LOCATION("logs"),
   LOG_LEVEL("ALL");
   
   private final String val;
   
   private NVSMXDefaults(String val){
	   this.val=val;
   }
   
   public String getVal(){
	   return val;
   }
}
