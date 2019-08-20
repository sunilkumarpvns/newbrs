package com.elitecore.elitesm.util;

import org.apache.commons.lang.StringEscapeUtils;

public class EliteStringEscapeUtils {
	public static String escapeJavaScript(String str){
		str = StringEscapeUtils.escapeJavaScript(str);
		str = str.replaceAll("'","&apos;");
		return str;
	}
	
	public static String escapeHtml(String str){
		str = StringEscapeUtils.escapeJavaScript(str);
		str = str.replaceAll("'","&apos;");
		return str;
	}
}
