/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EliteExceptionUtils.java                             
 * ModualName ExceptionHandling                                      
 * Created on 08 October, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.ssp.util.exception;

import java.util.ArrayList;
import java.util.List;

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
   
   public static Object[] getFullStackTraceAsArray(Throwable arg0)
   {
	   Object objects[] =null;
	   try
	   {
		   String[] stackElements = null;
		   ArrayList errorList = new ArrayList();
		   Throwable temp = getCause(arg0);
		   String at = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; at  ";
		   String causedBy = "<b>Caused by: </b>";
		   if(arg0!=null)
		   {
			   errorList.add("<font color=#FF0000>"+arg0.getMessage()+"</font>");
			   StackTraceElement elements[] = arg0.getStackTrace();
			   for (int i = 0; i < elements.length; i++) {

				   errorList.add(at+elements[i].toString());
			   }
			   arg0.printStackTrace();
			   while(temp!=null)
			   {
				   errorList.add(causedBy+"<font color=#FF0000>"+temp.getMessage()+"</font>");
				   StackTraceElement tempelements[] = temp.getStackTrace();
				   for (int i = 0; i < tempelements.length; i++) {

					   errorList.add(at+tempelements[i].toString());

				   }
				   temp = temp.getCause();
			   }
		   }
		   objects= errorList.toArray();
	   }catch(Exception e){

	   }
	   return objects;
   }
   
   
  
    
}
