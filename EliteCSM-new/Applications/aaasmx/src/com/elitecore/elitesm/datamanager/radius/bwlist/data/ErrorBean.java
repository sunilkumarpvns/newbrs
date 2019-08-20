package com.elitecore.elitesm.datamanager.radius.bwlist.data;

import java.io.Serializable;

public class ErrorBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int lineNo;
	private String message;
	private String attribute;
	private String attributeValue;
	
	
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	
   
    public String toString() {
    	String str=null;
    	if(message==null){
    		message = "Error while inserting ";
    	}
    	
    	
    	str = message + " Entry ["+attribute+","+attributeValue+"] at Line = "+lineNo; 
    	
    	return str;
    }
	
	
	
	
	
	
	
	
	
	
}
