package com.elitecore.core.driverx.cdr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CSVStripMapping{

	private String key;
	private String pattern;
	private String seperator;
	
	public CSVStripMapping(String key, String pattern, String seperator) {
		this.key = key;
		this.pattern = pattern;
		this.seperator = seperator;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getSeperator() {
		return seperator;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	Key = " + key);
		out.println("	Pattern = " + pattern);
		out.println("	Serperator = " + seperator);
		out.close();
		return stringBuffer.toString();
	}
}
