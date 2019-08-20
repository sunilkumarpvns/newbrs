/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EliteExceptionUtils.java                             
 * ModualName ExceptionHandling                                      
 * Created on 08 October, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.exception;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * @author kaushikvira
 */
public class EliteExceptionUtils extends ExceptionUtils {
	
	private static final String REFERENCED  = "referenced";
	private static final String UNIQUE_CONSTRAINT = "unique constraint";
	private static final String FOREIGNKEY_CONSTRAINT  =  "foreign key constraint";
	private static final String INTEGRITY_CONSTRAINT  =  "integrity constraint";
	
	private static final String PARENT_KEY_NOT_FOUND = "Primary key does not exist";
	private static final String DUPLICATE_NAME_FOUND = "Duplicate name found";
	private static final String REFERENCE_KEY_FOUND  = "Key value for constraint is still being referenced";
    
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
   
   /**
	 * Extract the error code of the violated constraint from the given SQLException.
	 *
	 * @param sqle The exception that was the result of the constraint violation.
	 * @return customErrorMsg Custom error message.
	 */
	public static String extractConstraintName(SQLException sqle) {
		
		String errorMessage = sqle.getMessage();
		
		if (errorMessage.contains(FOREIGNKEY_CONSTRAINT) || errorMessage.contains(INTEGRITY_CONSTRAINT)) {
			return PARENT_KEY_NOT_FOUND;
		} else if (errorMessage.contains(UNIQUE_CONSTRAINT)) {
			return DUPLICATE_NAME_FOUND;
		} else if (errorMessage.contains(REFERENCED)) {
			return REFERENCE_KEY_FOUND;
		} else {
			return sqle.getMessage();
		}
	}
}
