package com.elitecore.core.commons.util.constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * SMS Notifier Key Replacer replace SMS Notifier Key in Notifier request
 *  
 * @author punit.j.patel
 *
 */
public enum SMSNotifierKeyReplacer {
	USERNAME("{user-name}"), 
	PASSWORD("{password}"), 
	SENDER("{sender}"),
	MSGTXT("{MSG-TEXT}"),  
	MSGID("{MSG-ID}"),  
	ISFLASH("{isflash}"), 
	RECIPIENT("{MSG-TO}"),
	DLR_URL("{delivery-report-url}"), 
	DATA_HDR("{DATA-HDR}"), 
	PARAM1 ("{PARAM1}"), 
	PARAM2 ("{PARAM2}"), 
	PARAM3 ("{PARAM3}"),
	;
	
	private String key;
	private SMSNotifierKeyReplacer(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	
	/**
	 * Encode provided value and replace encoded value with Notification key in Service URL  
	 * 
	 * @param serviceURL a url contains notification keys
	 * @param replaceValue a replaceValue value for replace
	 * @param encodingType a encoding type  
	 * 
	 * @throws UnsupportedEncodingException 
	 *         If the named encoding is not supported
     * @see URLEncoder#encode(java.lang.String, java.lang.String)
	 */
	public void replace(StringBuilder serviceURL, String replaceValue, String encodingType) throws UnsupportedEncodingException{
		int start = serviceURL.indexOf(getKey());
		if(start >0 && replaceValue !=null && replaceValue.length()>0){
			int end = start + getKey().length();
			serviceURL.replace(start, end, URLEncoder.encode(replaceValue, encodingType));
		}
	}
	
}
