package com.elitecore.nvsmx.system.constants;

/**
 * 
 * ModuleConstant, This enum is used for contains fixed set of Module constants.
 * 
 * @author akash.khare
 *
 */
public enum ModuleConstants {
   POLICYDESIGNER("policydesigner"),
   PARTNERRNC("partnerrnc");
   
   private final String val;
   
   private ModuleConstants(String val){
	   this.val=val;
   }
   
   public String getVal(){
	   return val;
   }
}
