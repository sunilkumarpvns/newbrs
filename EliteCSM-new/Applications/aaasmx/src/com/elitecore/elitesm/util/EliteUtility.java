
package com.elitecore.elitesm.util;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.CommunicationConstant;

public class EliteUtility {
    private static String smAbsolutePath;
    private static boolean sunJava = true;
    private static boolean initialized = false;
    private static String actionName;
    private static IStaffData istaffData;
    private static String remoteAddress;
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
     * author : Nayana Rathod
     */
    public static String formatDashboardName( String dashboardName ) {
        if (dashboardName != null)
            if (dashboardName.length() > BaseConstant.DASHBOARD_MAX_NAME_LENGTH)
                return dashboardName.substring(0, BaseConstant.DASHBOARD_MAX_NAME_LENGTH).concat("...");
            else return dashboardName;
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
    
    public static Date  parseDate(String str, String[] parsePatterns) throws ParseException {
    	if (str == null || parsePatterns == null) {
    		throw new IllegalArgumentException("Date and Patterns must not be null");
    	}
    	SimpleDateFormat parser = null;
    	ParsePosition pos = new ParsePosition(0);
    	for (int i = 0; i < parsePatterns.length; i++) {
    		if (i == 0) {
    			parser = new SimpleDateFormat(parsePatterns[0]);
    		} else {
    			parser.applyPattern(parsePatterns[i]);
    		}
    		pos.setIndex(0);
    		Date date = parser.parse(str, pos);
    		if (date != null && pos.getIndex() == str.length()) {
    			return date;
    		}
    	}
    	throw new ParseException("Unable to parse the date: " + str, -1);
    }

	public static String getRemainingTime(Timestamp validity) throws ParseException {
		String remainingTime;
		if(validity != null){
			  int SECOND = 1000,MINUTE = 60 * SECOND,HOUR = 60 * MINUTE,DAY = 24 * HOUR,WEEK=1000*60*60*24*7;
			 
			  SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
			  java.util.Date date = sdf.parse(validity.toString());
			  
			  long validityTimeValue=validity.getTime();
			  
			  Date currentDate = new Date(); 
				 
			  Timestamp currentTime=new java.sql.Timestamp(currentDate.getTime());
				
			  long currentTimeValue=currentTime.getTime();
			  long ms = validityTimeValue-currentTimeValue;
			 
				 if(ms > 0){
					 StringBuffer text = new StringBuffer("");
					 if(ms > WEEK){
						 text.append(ms / WEEK).append(" weeks ");
						 ms %= WEEK;
					 }
					 if (ms > DAY) {
					   text.append(ms / DAY).append(" days ");
					   ms %= DAY;
					 }
					 if (ms > HOUR) {
					   text.append(ms / HOUR).append(" hours ");
					   ms %= HOUR;
					 }
					 if (ms > MINUTE) {
					   text.append(ms / MINUTE).append(" minutes ");
					   ms %= MINUTE;
					 }
					 
					 remainingTime=text.toString();
				 }else{
					 remainingTime="-";
				 }
			}else{
				 remainingTime="-";
			}
			
		return remainingTime;
	}
	public static String getValidity(Timestamp validity) throws ParseException {
		if(validity !=null){
			String strValidity = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(validity);
			return strValidity;
		}else{
			return "-";
		}
	}

	public static String getRemoteAddress() {
		return remoteAddress;
 }

	public static void setRemoteAddress(String remoteAddress) {
		EliteUtility.remoteAddress = remoteAddress;
	}
	
	/**
	 * This method convert {}'",[] into HtmlEntity
	 * @param value String containing the following symbols {}'",[]
	 * @return String with HTML Entity
	 */
	public static String encodeHtmlEntity(String value){
		value = value.replaceAll("\\{" , "&#123;");
		value = value.replaceAll("\\}" , "&#125;");
		value = value.replaceAll("'" , "&#39;");
		value = value.replaceAll("," , "&#44;");
		value = value.replaceAll("\\[" , "&#91;");
		value = value.replaceAll("\\]" , "&#93;");
		value = value.replaceAll("\"" , "&#34;");
		return value;
	}
	
	/**
	 * This method converts convert json field value from JSONArray into Html Entity
	 * @param jsonArray JSONArray containing value that you want to convert into entity
	 * @param jsonFieldName Json field containing value
	 * @return JSONArray with updated json field value
	 */
	public static JSONArray convertJsonFieldValueIntoHtmlEntity(JSONArray jsonArray, String jsonFieldName){
		int jsonArraySize = jsonArray.size();

		for (int i = 0; i < jsonArraySize; i++) {
			JSONObject pluginDataJSONObj = jsonArray.getJSONObject(i);

			String ruleset = pluginDataJSONObj.getString(jsonFieldName);
			if(ruleset != null && ruleset.isEmpty() == false){
				String decodedRuleSet = StringEscapeUtils.unescapeHtml(ruleset);
				pluginDataJSONObj.remove(jsonFieldName);
				pluginDataJSONObj.put(jsonFieldName, decodedRuleSet);

				jsonArray.set(i, pluginDataJSONObj);
			}
		}

		return jsonArray;
	}
	
	public static String getServicePolicyOrder(List<String> serviceFlowOrderList) {
		String servicePolicyOrder = "-";
		StringBuilder sb = new StringBuilder();
		if(Collectionz.isNullOrEmpty(serviceFlowOrderList) == false){
			for (String handlerName : serviceFlowOrderList){
				sb.append(handlerName + "<br/>");
			}
			servicePolicyOrder = sb.toString();
		}
		return servicePolicyOrder;
	}
	
	/**<p><b>Checks whether given String is numeric or not .</b> </p>
	 * <pre>for Example
	 *{@code
	 * boolean isNumeric = isNumeric("134");
	 *}</pre>
	 *<p><b>output is :</b>
	 *<b>true</b>
	 *<pre>second Example
	 *{@code
	 * boolean isNumeric = isNumeric("temp123");
	 *}</pre>
	 *<p><b>output is :</b>
	 *<b>false</b>
	 *</p>
	 *
	 * @param strValue value to be checked
	 * @return if number return true else false
	 */
	public static Boolean isNumeric(String strValue){
		Boolean isNumeric = true;
		try{
			Double numberValue = Double.parseDouble(strValue);
		}catch(NumberFormatException nfe){
			isNumeric = false;
		}
		return isNumeric;
	}
	
	/**<p><b>Convert String to capitalize form .</b> </p>
	 * <pre>for Example
	 *{@code
	 * String outputStr = getCapitalizeString("this is demo");
	 *}</pre>
	 *<p><b>output is :</b>
	 *<b>This Is Demo</b>
	 *<pre>second Example
	 *{@code
	 * String outputStr = getCapitalizeString("123");
	 *}</pre>
	 *<p><b>output is :</b>
	 *<b>123</b>
	 *</p>
	 * @param strValue value to be capitalize
	 * @return capitalize string value
	 */
	public static String getCapitalizeString(String strValue){
		  StringBuilder str = new StringBuilder();

		    List<String> tokens = Arrays.asList(strValue.split("[\\s,-]"));// Can be space,comma or hyphen

		    for (String token : tokens) {
		    	if(Strings.isNullOrBlank(token) == false){
		    		String lowerCaseToken = token.toLowerCase();
		    		str.append(Character.toUpperCase(lowerCaseToken.charAt(0))).append(lowerCaseToken.substring(1)).append(" ");
		    	}
		    }
		    
		    return str.toString().trim(); 
	}

	public static String getLowerCaseString(String value){
		
		if (Strings.isNullOrBlank(value) == false) {
			return value.trim().toLowerCase();
		}
		
		return value;
	}
	
	public static boolean checkNullOrEmpty(Long object){
		return (object == null || object == 0L);
	}
	
	public static void doRemoteCommunication(String module, String method, String ip, String port, Object[] objArgValues, String[] strArgTypes) throws InitializationFailedException, ConnectException, CommunicationException, UnidentifiedServerInstanceException {
		
		IRemoteCommunicationManager remoteCommunicationManager = null;

		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		remoteCommunicationManager.init(ip,port, null, false);
		remoteCommunicationManager.execute(module,method,objArgValues,strArgTypes);

	}
 }