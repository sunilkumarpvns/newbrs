package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;


@XmlType(propOrder={})
public class DynAuthResponseCodeDetail {

	 private boolean isRetryEnabled;
	 private List<Integer> responceCodeList;
	 private int retryLimit = 3;
	 
	 public DynAuthResponseCodeDetail() {
		 this.responceCodeList = new ArrayList<Integer>();
	 }

	@Reloadable(type=Boolean.class)
	@XmlAttribute(name="enabled")
	public boolean getIsRetryEnabled() {
		return isRetryEnabled;
	}

	public void setIsRetryEnabled(boolean isRetryEnabled) {
		this.isRetryEnabled = isRetryEnabled;
	}

	@Reloadable(type=List.class)
	@XmlElementWrapper(name="response-code-list")
	@XmlElement(name="response-code",type=Integer.class)
	public List<Integer> getResponceCodeList() {
		return responceCodeList;
	}

	public void setResponceCodeList(List<Integer> responceCodeList) {
		this.responceCodeList = responceCodeList;
	}

	@XmlElement(name="retry-limit", type=int.class, defaultValue="3")
	public int getRetryLimit() {
		return retryLimit;
	}
	public void setRetryLimit(int retryLimit) {
		this.retryLimit = retryLimit;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Response Code Data");
		out.println("    Enabled        = " + isRetryEnabled);
		out.println("    Retry Limit    = " + retryLimit);
		if(responceCodeList!=null && responceCodeList.size()>0)
			out.println("    Response Codes = " + responceCodeList);
		else {
			out.println("No Responce Code Configured");
		}
		out.close();
		return stringBuffer.toString();
	}


}
