package com.elitecore.ssp.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.BaseConstant;



public class EliteUtility {
    private static String sspAbsolutePath;
    private static boolean sunJava = true;
    private static boolean initialized = false;
    private static DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
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
    
    public static Long convertDateToMilliSeconds(String date, String dateFormat){
    	Long milliseconds = null;
    	try {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			Date d = format.parse(date);
			milliseconds = d.getTime();
			return milliseconds;
		} catch (ParseException e) {
			e.printStackTrace();
			return milliseconds;
		}
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
    
    public static final void setSSPHome(String sspHome) {
    	sspAbsolutePath = sspHome;
    }
    
    public static String getSSPHome() {
    	return sspAbsolutePath;
    }
    public static boolean isSunJava(){
		if(!initialized){
			String vendor = System.getProperty("java.vendor");
			if(vendor!=null && !vendor.toLowerCase().contains("sun")){
				sunJava  = false;
			}else{
				sunJava = true;
			}
		}
		return sunJava;
	}
    public static String convertBytesToSuitableUnit(long bytes){
    	String bytesToSuitableUnit= bytes + " B";
    	
        if(bytes >= GB) {
        	double tempBytes = bytes/GB;
        	bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + " GB";
        	return bytesToSuitableUnit;
        }
        if(bytes >= MB) {
        	double tempBytes = bytes/MB;
        	bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + " MB";
        	return bytesToSuitableUnit;
        }
        if(bytes >= KB) {
        	double tempBytes = bytes/KB;
        	bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + " kB";
        	return bytesToSuitableUnit;
        }
        return bytesToSuitableUnit;                    
    }    
    
    public static String convertToHourMinuteSecond(long totalSeconds){
    	long hours=0;
    	short minutes=0;
    	short seconds=0;
    	
    	
    	seconds = (short)(totalSeconds % 60);
    	minutes = (short)((totalSeconds % 3600) / 60);
    	hours = totalSeconds / 3600;
    	
    	String hrStr=((hours<10)?"0"+hours:""+hours);
    	String minStr=((minutes<10)?"0"+minutes:""+minutes);
    	String secStr=((seconds<10)?"0"+seconds:""+seconds);
    	
    	String fmtTime=hrStr+":"+minStr+":"+secStr;    	
    	
    	return fmtTime;
    }
    
    public static String convertStringToCamelCase(String word){
    	if(word!=null && word.trim().length()>0){
    		String firstChar=""+word.charAt(0);
    		firstChar=firstChar.toUpperCase();
    		word=(firstChar)+(word.substring(1).toLowerCase());
    	}    	    
    	return word;    	
    }
    
    public static SubscriberProfile getSubscriberProfile(com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile subscriberProfile){
    	SubscriberProfile subscriberProfileData=null;
    	Map<String,String> tempMap=new HashMap<String,String>();
    	for(com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.Entry entry : subscriberProfile.getEntry()){
             tempMap.put(entry.getKey(),entry.getValue());
    	}
    	subscriberProfileData=new SubscriberProfile(tempMap);
    	return subscriberProfileData;
    }
    
 }
