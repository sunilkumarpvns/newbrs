package com.elitecore.netvertexsm.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;

import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class EliteUtility {
	private static String smAbsolutePath;
	private final static String Module = "ELITEUTILITY";
	private static String contextPath;
	
	public static String getContextPath() {
		return contextPath;
	}

	public static void setContextPath(String contextPath) {
		EliteUtility.contextPath = contextPath;
	}


	private static DecimalFormat threeDecimalForm = new DecimalFormat("#.###");
	private static final double BYTE = 1024, KB = BYTE, MB = KB*BYTE, GB = MB*BYTE;
	
    public static String trimExtraChars( String value ,
                                         int toSize ) {
        return trimExtraChars(value, toSize, "...");
    }
    
    public static String trimExtraChars( String value ,
                                         int toSize ,
                                         String postFixValue ) {
        if (value != null && value.length() > toSize) {
            value = value.substring(0, toSize) + (postFixValue == null ? "" : postFixValue);
        }
        return value;
    }
    
    /**
     * Converts the date to the given format. If conversion fails default toString value will be returned.
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToString( Date date ,
                                       String pattern ) {
        String formattedDateString = "-";
        if (date != null) {
            try {
                formattedDateString = new SimpleDateFormat(pattern).format(date);
            }
            catch (Exception e) {
                formattedDateString = date.toString();
            }
        }
        return formattedDateString;
    }
    
    /*
     * author: kaushikvira
     */
    public static String formatDescription( String description ) {
        if (description != null)
            if (description.length() > BaseConstant.DESCRIPTION_LENGTH)
                return description.substring(0, BaseConstant.DESCRIPTION_LENGTH).concat("....");
            else return description;
        else return "";
    }
    
    /*
     * author: kaushikvira
     */
    public static final String trim(String val ) {
         if(val != null)
            return val.trim();
        return null;
    }

    public static final void setSMHome(String smHomePath) {
    	smAbsolutePath = smHomePath;
    }
    
    public static String getSMHome() {
    	return smAbsolutePath;
    }
    public static void setClientIP(HttpServletRequest request ){
    	String clientIP = request.getRemoteAddr();
    	System.out.println("set client ip:"+clientIP);
    	MDC.put("remoteaddress", clientIP);
    }

	public static String getClientIP(){
		String clientAddress = "Unknown";
		String remoteAddress = (String)MDC.get("remoteaddress");
		
		if(remoteAddress!=null){
			clientAddress  = remoteAddress;
		}
		return clientAddress;
	}
	
	public static Long getCurrentUserId(){
		Long longCurrentId=0L;
		
		try{
			String currentUserId = "0";
			String strCurrentUserId = (String)MDC.get("currentuserid");
			if(strCurrentUserId != null){
				currentUserId  = strCurrentUserId;
			}
			longCurrentId = Long.parseLong(currentUserId);
		}catch (NumberFormatException e) {
			Logger.logError(Module,"error in parsing Current User Id, Reason:"+e.getMessage());
		}
		return longCurrentId;
	}
	
	public static void setCurrentUserId(HttpServletRequest request){
		SystemLoginForm systemLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
		if(systemLoginForm!=null){
			String userId = systemLoginForm.getUserId();
			System.out.println("set current user Id:"+userId);	
			MDC.put("currentuserid", userId);
		}
	}
	
	
	
	public static void cleanMDC(){
		System.out.println("remove MDC parameters");
		MDC.remove("currentuserid");
		MDC.remove("remoteaddress");
	}
	
	public static Timestamp getCurrentTimeStamp(){
		return new Timestamp(new Date().getTime());
	}
	
	
	  public static String convertBytesToSuitableUnit(long bytes){
	    	String bytesToSuitableUnit= bytes + " B";
	    	
	        if(bytes >= GB) {
	        	double tempBytes = bytes/GB;
	        	bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " GB";
	        	return bytesToSuitableUnit;
	        }
	        if(bytes >= MB) {
	        	double tempBytes = bytes/MB;
	        	bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " MB";
	        	return bytesToSuitableUnit;
	        }
	        if(bytes >= KB) {
	        	double tempBytes = bytes/KB;
	        	bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " KB";
	        	return bytesToSuitableUnit;
	        }
	        return bytesToSuitableUnit;                    
	    } 
}