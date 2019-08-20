/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EliteExceptionUtils.java                             
 * ModualName ExceptionHandling                                      
 * Created on 08 October, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitelicgen.exception;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * @author kaushikvira
 */
public class EliteExceptionUtils extends ExceptionUtils {
    
    public static String convertToString( String[] args0 ) {
        StringBuffer sb = new StringBuffer();
        
        if (args0 != null) {
            for ( int i = 0; i < args0.length; i++ ) {
               sb.append("\n");
               sb.append(args0[i]);
            }
        }
        return sb.toString();
    }
    
   public static String  getRootCauseStackTraceAsString(Throwable arg0)
   {
      String [] trace = ExceptionUtils.getRootCauseStackTrace(arg0);
      return EliteExceptionUtils.convertToString(trace);
   }
   
   
   public static String getFullStackTrace(Throwable args0)
   {
      return EliteExceptionUtils.getFullStackTrace(args0); 
   }
   
  
   
   
  
    
}
