package com.elitecore.core.driverx.cdr.deprecated;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CSVFieldMapping  {
	private String headerField;
	private String key;
	
	public CSVFieldMapping(String headerField, String key) {
		this.headerField = headerField;
		this.key = key;
	}
	
	public String getHeaderField() {
		return headerField;
	}
	
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	Header Field = " + headerField);
		out.println("	Key = " + key);
		out.close();
		return stringBuffer.toString();
	}

}
