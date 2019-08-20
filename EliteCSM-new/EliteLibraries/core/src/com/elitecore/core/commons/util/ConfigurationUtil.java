package com.elitecore.core.commons.util;

import java.util.List;

import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;

/**
 * @deprecated This class is deprecated. Use elitecommons utilities.
 */
public class ConfigurationUtil {

	/**
	 * @deprecated This method is deprecated in favor of {@link Strings#toBoolean(String)} 
	 */
	public static boolean stringToBoolean(String originalString,boolean defaultValue) {
		boolean resultValue = defaultValue;
		try{
			resultValue = Boolean.parseBoolean(originalString.trim());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}
	
	/**
	 * @deprecated This method is deprecated in favor of {@link Numbers#parseInt(String, int)} 
	 */
	public static  int stringToInteger(String originalString,int defaultValue) {
		int resultValue = defaultValue;
		try{
			resultValue = Integer.parseInt(originalString.trim());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}

	/**
	 * @deprecated This method is deprecated in favor of {@link Numbers#parseLong(String, long)} 
	 */
	public static long stringToLong(String originalString,long defaultValue) {
		long resultValue = defaultValue;
		try{
			resultValue = Long.parseLong(originalString.trim());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultValue;
		
	}
	
	/**
	 * @deprecated Use <code>Strings.join(",", strArrayList, Strings.WITHIN_SINGLE_QUOTES) </code>instead
	 */
	@Deprecated
	public static String getStrFromStringArrayList(List<String> strArrayList) {
		StringBuffer resultString = new StringBuffer();
		if(strArrayList!=null){
			int len = strArrayList.size()-1;
			int i=0;
			for(;i<len;i++){
				resultString.append("'"+strArrayList.get(i)+"'");
				resultString.append(",");
}
			if(i<strArrayList.size()){
				resultString.append("'"+strArrayList.get(i)+"'");
			}			
		}
		return resultString.toString();
	}
}
