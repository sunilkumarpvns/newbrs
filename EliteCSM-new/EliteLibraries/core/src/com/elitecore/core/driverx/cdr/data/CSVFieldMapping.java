package com.elitecore.core.driverx.cdr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.commons.base.Strings;

public class CSVFieldMapping {
	private String headerField;
	private String key;
	private String defaultValue="";
	private boolean bUseDictionaryValue;
	
	public CSVFieldMapping(String headerField, String key,String defaultValue,boolean bUseDictionaryValue) {
		this.headerField = headerField;
		this.key = key;
		if(Strings.isNullOrEmpty(defaultValue) == false) {
			this.defaultValue = defaultValue;
		}
		this.bUseDictionaryValue = bUseDictionaryValue;
	}
	
	public String getHeaderField() {
		return headerField;
	}
	
	public String getKey() {
		return key;
	}
	
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isbUseDictionaryValue() {
		return bUseDictionaryValue;
	}

	public void setbUseDictionaryValue(boolean bUseDictionaryValue) {
		this.bUseDictionaryValue = bUseDictionaryValue;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	Header Field = " + headerField);
		out.println("	Key = " + key);
		out.println("	Default Value = " + defaultValue);
		out.println("	Use Dictionary  Attribute = " + bUseDictionaryValue);
		out.close();
		return stringBuffer.toString();
	}
}
