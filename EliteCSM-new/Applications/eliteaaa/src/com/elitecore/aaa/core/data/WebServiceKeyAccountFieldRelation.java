package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class WebServiceKeyAccountFieldRelation {
	
	private String webServiceMethodKey;
	private String defaultValue;
	private Map<String,String> valueMapping;
	

	public String getWebServiceMethodKey() {
		return webServiceMethodKey;
	}
	public void setWebServiceMethodKey(String webServiceMethodKey) {
		this.webServiceMethodKey = webServiceMethodKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Map<String, String> getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		if(valueMapping!=null){
			String[] keyPairArray = valueMapping.split(",");
			if(keyPairArray.length>0){
				this.valueMapping = new HashMap<String,String>();				
				for (int i = 0; i < keyPairArray.length; i++) {
					String[] keyPair = keyPairArray[i].split(":");
					if(keyPair.length==2){
						this.valueMapping.put(keyPair[0].trim(),keyPair[1].trim());
					}
				}
			}
		}
	}
	
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();
		out.println("        Key			        : " + ((webServiceMethodKey != null)?webServiceMethodKey:""));
		out.println("        Default Value     	    : " + ((defaultValue != null)?defaultValue:""));
		out.println("        Value Mapping          : " + ((valueMapping!=null)?valueMapping:""));
		out.println();
		
		return out.toString();
	}
	
}
