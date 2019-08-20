package com.elitecore.core.serverx.policies.data;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:01 PM
 */
public class PolicyOverrideData{
	//Map<PolicyName,Map<Attribute,value>>
	private Map<String,Map<String,String>>checkItem;
	private Map<String,Map<String,String>>rejectItem;
	private Map<String,Map<String,Map<String,ArrayList<String>>>> replyItem;
	private Map<String,Map<String,Map<String,ArrayList<String>>>> addItem;
	
	private boolean bErrorInParsingCheckItem;
	private boolean bErrorInParsingRejectItem;
	private boolean bErrorInParsingReplyItem;
	private boolean bErrorInParsingAddItem;
	
	public boolean isErrorInParsingCheckItem() {
		return bErrorInParsingCheckItem;
	}


	public void setErrorInParsingCheckItem(boolean errorInParsingCheckItem) {
		bErrorInParsingCheckItem = errorInParsingCheckItem;
	}


	public boolean isErrorInParsingRejectItem() {
		return bErrorInParsingRejectItem;
	}


	public void setErrorInParsingRejectItem(boolean errorInParsingRejectItem) {
		bErrorInParsingRejectItem = errorInParsingRejectItem;
	}


	public boolean isErrorInParsingReplyItem() {
		return bErrorInParsingReplyItem;
	}


	public void setErrorInParsingReplyItem(boolean errorInParsingReplyItem) {
		bErrorInParsingReplyItem = errorInParsingReplyItem;
	}


	public PolicyOverrideData(){
	}


	public Map<String, Map<String, String>> getCheckItem() {
		return checkItem;
	}

	public Map<String, String> getCheckItem(String strPolicyName) {
		if(checkItem!=null)
			return checkItem.get(strPolicyName);
		return null;
	}

	public void setCheckItem(Map<String, Map<String, String>> checkItem) {
		this.checkItem = checkItem;
	}


	public Map<String, Map<String, String>> getRejectItem() {
		return rejectItem;
	}
	public Map<String, String> getRejectItem(String strPolicyName) {
		if(rejectItem!=null)
			return rejectItem.get(strPolicyName);
		return null;
	}


	public void setRejectItem(Map<String, Map<String, String>> rejectItem) {
		this.rejectItem = rejectItem;
	}


	public Map<String,Map<String,Map<String,ArrayList<String>>>> getReplyItem() {
		return replyItem;
	}
	public Map<String,Map<String,ArrayList<String>>> getReplyItem(String strPolicyName) {
		if(replyItem!=null)
			return replyItem.get(strPolicyName);
		return null;
	}

	public Map<String,Map<String,Map<String,ArrayList<String>>>> getAddItem() {
		return addItem;
	}
	public Map<String,Map<String,ArrayList<String>>> getAddItem(String strPolicyName) {
		if(addItem!=null)
			return addItem.get(strPolicyName);
		return null;
	}

	public void setReplyItem(Map<String,Map<String,Map<String,ArrayList<String>>>> replyItem) {
		this.replyItem = replyItem;
	}
	
	public void setAddItem(Map<String,Map<String,Map<String,ArrayList<String>>>> addItem) {
		this.addItem = addItem;
	}
	
	@Override
	public String toString() {
		StringBuilder valueString = new StringBuilder();
		if(checkItem != null)
			valueString.append("\nCheck Items Override Values: " + checkItem);
		if(rejectItem != null)
			valueString.append("\nReject Items Override Values: " + rejectItem);
		if(replyItem != null)
			valueString.append("\nReply Items Override Values: " + replyItem);
		if(addItem != null)
			valueString.append("\nAdd Items Override Values: " + addItem);
		
		return valueString.toString();
	}


	public void setErrorInParsingAddItem(boolean bErrorInParsingAddItem) {
		this.bErrorInParsingAddItem = bErrorInParsingAddItem;
	}


	public boolean isErrorInParsingAddItem() {
		return bErrorInParsingAddItem;
	}
}