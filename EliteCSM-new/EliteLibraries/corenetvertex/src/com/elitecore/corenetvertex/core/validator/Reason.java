package com.elitecore.corenetvertex.core.validator;

import com.elitecore.corenetvertex.constants.CommonConstants;

import java.util.ArrayList;
import java.util.List;

public class Reason {
	private String name;
	private String messages;
	private List<String> subReasons;
	private String remarks;

	public Reason(){}

	public Reason(String name){
		this.name = name;
		this.messages = CommonConstants.SUCCESS;
		this.subReasons = new ArrayList<String>();
		this.remarks = new String();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public List<String> getSubReasons() {
		return subReasons;
	}

	public void addFailReason(List<String> subReasons) {
		this.getSubReasons().addAll(subReasons);
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}

